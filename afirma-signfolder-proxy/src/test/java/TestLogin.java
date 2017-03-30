import java.security.KeyStore;
import java.security.KeyStore.PasswordProtection;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

import org.junit.Test;

import es.gob.afirma.core.misc.Base64;
import es.gob.afirma.core.misc.http.UrlHttpManager;
import es.gob.afirma.core.misc.http.UrlHttpManagerFactory;
import es.gob.afirma.core.misc.http.UrlHttpMethod;
import es.gob.afirma.core.signers.AOPkcs1Signer;
import junit.framework.Assert;

public class TestLogin {

	private static final String CERT_PATH = "ANCERTCCP_FIRMA.p12"; //$NON-NLS-1$
	private static final char[] CERT_PASS = "1111".toCharArray(); //$NON-NLS-1$
	private static final String CERT_ALIAS = "juan ejemplo español"; //$NON-NLS-1$

	private static final String CERT_PATH2 = "ANCERTCCP_AUTH.p12"; //$NON-NLS-1$
	private static final char[] CERT_PASS2 = "1111".toCharArray(); //$NON-NLS-1$
	private static final String CERT_ALIAS2 = "juan ejemplo español"; //$NON-NLS-1$

	private static final String URL_BASE = "http://localhost:8080/afirma-signfolder-proxy/pf?"; //$NON-NLS-1$

	@Test
	public void testLoginOk() throws Exception {

		final UrlHttpManager urlManager = UrlHttpManagerFactory.getInstalledManager();

		// --------------------------
		// Llamada al metodo de login
		// --------------------------

		// Cargamos el certificado
		final KeyStore ks = KeyStore.getInstance("PKCS12"); //$NON-NLS-1$
		ks.load(ClassLoader.getSystemResourceAsStream(CERT_PATH), CERT_PASS);
		final PrivateKeyEntry pke = (PrivateKeyEntry) ks.getEntry(CERT_ALIAS, new PasswordProtection(CERT_PASS));

		String xml = "<lgnrq />"; //$NON-NLS-1$

		String url = URL_BASE + createUrlParams("10", xml); //$NON-NLS-1$

		byte[] data = urlManager.readUrl(url, UrlHttpMethod.POST);

		String xmlResponse = new String(data);

		System.out.println("Respuesta a la peticion de login:\n" + new String(xmlResponse)); //$NON-NLS-1$

		final String tokenB64 = xmlResponse.substring(xmlResponse.indexOf("'>") + "'>".length(), xmlResponse.indexOf("</lgnrq>")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		// --------------------------
		// Llamada al metodo de validar login
		// --------------------------

		final X509Certificate cert = (X509Certificate) ks.getCertificate(CERT_ALIAS);
		final String certB64 = Base64.encode(cert.getEncoded());

		final AOPkcs1Signer signer = new AOPkcs1Signer();
		final byte[] signature = signer.sign(Base64.decode(tokenB64), "SHA256withRSA", pke.getPrivateKey(), pke.getCertificateChain(), null); //$NON-NLS-1$

		xml = "<rqtvl><cert>" + certB64 + "</cert><pkcs1>" + Base64.encode(signature) + "</pkcs1></rqtvl>"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		System.out.println("==========" + xml);

		url = URL_BASE + createUrlParams("11", xml); //$NON-NLS-1$

		data = urlManager.readUrl(url, UrlHttpMethod.POST);

		xmlResponse = new String(data);

		System.out.println("Respuesta a la peticion de validacion de login:\n" + xmlResponse); //$NON-NLS-1$

		Assert.assertTrue("La validacion del login devolvio un error", xmlResponse.indexOf("ok='true'") != -1); //$NON-NLS-1$ //$NON-NLS-2$

		// --------------------------
		// Llamada a un metodo de operacion
		// --------------------------

		xml = "<rqtconf><cert>" + Base64.encode(cert.getEncoded()) + "</cert></rqtconf>"; //$NON-NLS-1$ //$NON-NLS-2$

		url = URL_BASE + createUrlParams("6", xml); //$NON-NLS-1$

		data = urlManager.readUrl(url, UrlHttpMethod.POST);

		xmlResponse = new String(data);

		System.out.println("Respuesta a la peticion de datos de configuracion:\n" + xmlResponse); //$NON-NLS-1$

		Assert.assertNotSame(
				"Se obtuvo el mensaje de error en la autenticacion", //$NON-NLS-1$
				"<err cd=\"ERR-11\">Error en la autenticacion de la peticion</err>", //$NON-NLS-1$
				xmlResponse);
	}

	@Test
	public void testWrongToken() throws Exception {

		final UrlHttpManager urlManager = UrlHttpManagerFactory.getInstalledManager();

		// --------------------------
		// Llamada al metodo de login
		// --------------------------

		// Cargamos el certificado
		final KeyStore ks = KeyStore.getInstance("PKCS12"); //$NON-NLS-1$
		ks.load(ClassLoader.getSystemResourceAsStream(CERT_PATH), CERT_PASS);
		final PrivateKeyEntry pke = (PrivateKeyEntry) ks.getEntry(CERT_ALIAS, new PasswordProtection(CERT_PASS));

		String xml = "<lgnrq />"; //$NON-NLS-1$

		String url = URL_BASE + createUrlParams("10", xml); //$NON-NLS-1$

		byte[] data = urlManager.readUrl(url, UrlHttpMethod.POST);

		String xmlResponse = new String(data);

		System.out.println("Respuesta a la peticion de login:\n" + new String(xmlResponse)); //$NON-NLS-1$

		// --------------------------
		// Llamada al metodo de validar login
		// --------------------------

		// Firmamos un token distinto

		final X509Certificate cert = (X509Certificate) ks.getCertificate(CERT_ALIAS);
		final String certB64 = Base64.encode(cert.getEncoded());

		final AOPkcs1Signer signer = new AOPkcs1Signer();
		final byte[] pkcs1 = signer.sign("Hola Mundo!!".getBytes(), "SHA256withRSA", pke.getPrivateKey(), pke.getCertificateChain(), null); //$NON-NLS-1$

		xml = "<rqtvl><cert>" + certB64 + "</cert><pkcs1>" + Base64.encode(pkcs1) + "</pkcs1></rqtvl>"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		url = URL_BASE + createUrlParams("11", xml); //$NON-NLS-1$

		data = urlManager.readUrl(url, UrlHttpMethod.POST);

		xmlResponse = new String(data);

		System.out.println("Respuesta a la peticion de validacion de login:\n" + xmlResponse); //$NON-NLS-1$

		Assert.assertTrue("La validacion del login devolvio un error", xmlResponse.indexOf("ok='false'") != -1); //$NON-NLS-1$ //$NON-NLS-2$

	}

	@Test
	public void testWrongCertificate() throws Exception {

		final UrlHttpManager urlManager = UrlHttpManagerFactory.getInstalledManager();

		// --------------------------
		// Llamada al metodo de login
		// --------------------------

		// Cargamos el certificado
		final KeyStore ks = KeyStore.getInstance("PKCS12"); //$NON-NLS-1$
		ks.load(ClassLoader.getSystemResourceAsStream(CERT_PATH), CERT_PASS);
		final PrivateKeyEntry pke = (PrivateKeyEntry) ks.getEntry(CERT_ALIAS, new PasswordProtection(CERT_PASS));

		String xml = "<lgnrq />"; //$NON-NLS-1$

		String url = URL_BASE + createUrlParams("10", xml); //$NON-NLS-1$

		byte[] data = urlManager.readUrl(url, UrlHttpMethod.POST);

		String xmlResponse = new String(data);

		System.out.println("Respuesta a la peticion de login:\n" + new String(xmlResponse)); //$NON-NLS-1$

		final String tokenB64 = xmlResponse.substring(xmlResponse.indexOf("'>") + "'>".length(), xmlResponse.indexOf("</lgnrq>")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		// --------------------------
		// Llamada al metodo de validar login
		// --------------------------

		final AOPkcs1Signer signer = new AOPkcs1Signer();
		final byte[] pkcs1 = signer.sign(Base64.decode(tokenB64), "SHA256withRSA", pke.getPrivateKey(), pke.getCertificateChain(), null); //$NON-NLS-1$

		// Cargamos otro almacen y declaramos un certificado distinto al usado
		final KeyStore ks2 = KeyStore.getInstance("PKCS12"); //$NON-NLS-1$
		ks2.load(ClassLoader.getSystemResourceAsStream(CERT_PATH2), CERT_PASS2);
		final Certificate cert2 = ks2.getCertificate(CERT_ALIAS2);

		final String certB64 = Base64.encode(cert2.getEncoded());

		xml = "<rqtvl><cert>" + certB64 + "</cert><pkcs1>" + Base64.encode(pkcs1) + "</pkcs1></rqtvl>"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		url = URL_BASE + createUrlParams("11", xml); //$NON-NLS-1$

		data = urlManager.readUrl(url, UrlHttpMethod.POST);

		xmlResponse = new String(data);

		System.out.println("Respuesta a la peticion de validacion de login:\n" + xmlResponse); //$NON-NLS-1$

		Assert.assertTrue("La validacion del login devolvio un error", xmlResponse.indexOf("ok='false'") != -1); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Test
	public void testNoLogin() throws Exception {

		final UrlHttpManager urlManager = UrlHttpManagerFactory.getInstalledManager();

		// Cargamos el certificado
		final KeyStore ks = KeyStore.getInstance("PKCS12"); //$NON-NLS-1$
		ks.load(ClassLoader.getSystemResourceAsStream(CERT_PATH), CERT_PASS);
		final X509Certificate cert = (X509Certificate) ks.getCertificate(CERT_ALIAS);

		// --------------------------
		// Llamada a un metodo de operacion
		// --------------------------

		final String xml = "<rqtconf><cert>" + Base64.encode(cert.getEncoded()) + "</cert></rqtconf>"; //$NON-NLS-1$ //$NON-NLS-2$

		final String url = URL_BASE + createUrlParams("6", xml); //$NON-NLS-1$

		final byte[] data = urlManager.readUrl(url, UrlHttpMethod.POST);

		final String xmlResponse = new String(data);

		System.out.println("Respuesta a la peticion de datos de configuracion:\n" + xmlResponse); //$NON-NLS-1$

		Assert.assertEquals(
				"No se obtuvo el mensaje de error en la autenticacion", //$NON-NLS-1$
				"<err cd=\"ERR-11\">Error en la autenticacion de la peticion</err>", //$NON-NLS-1$
				xmlResponse);
	}


	private static String createUrlParams(final String op, final String data) {
		return "op=" + op + "&dat=" + Base64.encode(data.getBytes(), true); //$NON-NLS-1$ //$NON-NLS-2$
	}


	public static void main(String[] args) throws Exception {

		final String SIGNATURE_ALGORITHM = "SHA256withRSA";
		final byte[] DATA = "Hola Mundo!!".getBytes();


		// Cargamos el certificado
		final KeyStore ks = KeyStore.getInstance("PKCS12"); //$NON-NLS-1$
		ks.load(ClassLoader.getSystemResourceAsStream(CERT_PATH), CERT_PASS);
		final PrivateKeyEntry pke = (PrivateKeyEntry) ks.getEntry(CERT_ALIAS, new PasswordProtection(CERT_PASS));


		final AOPkcs1Signer signer = new AOPkcs1Signer();
		final byte[] signature = signer.sign(DATA, SIGNATURE_ALGORITHM, pke.getPrivateKey(), pke.getCertificateChain(), null);

		final Signature verifier = Signature.getInstance(SIGNATURE_ALGORITHM);
		verifier.initVerify(pke.getCertificate().getPublicKey());
		verifier.update(DATA);
		verifier.verify(signature);
	}

	@Test
	public void testCloseSession() throws Exception {

		final UrlHttpManager urlManager = UrlHttpManagerFactory.getInstalledManager();

		// Cargamos el certificado
		final KeyStore ks = KeyStore.getInstance("PKCS12"); //$NON-NLS-1$
		ks.load(ClassLoader.getSystemResourceAsStream(CERT_PATH), CERT_PASS);
		final PrivateKeyEntry pke = (PrivateKeyEntry) ks.getEntry(CERT_ALIAS, new PasswordProtection(CERT_PASS));


		// --------------------------
		// Llamada al metodo de login
		// --------------------------

		String xml = "<lgnrq />"; //$NON-NLS-1$

		String url = URL_BASE + createUrlParams("10", xml); //$NON-NLS-1$

		byte[] data = urlManager.readUrl(url, UrlHttpMethod.POST);

		String xmlResponse = new String(data);

		System.out.println("Respuesta a la peticion de login:\n" + new String(xmlResponse)); //$NON-NLS-1$

		final String tokenB64 = xmlResponse.substring(xmlResponse.indexOf("'>") + "'>".length(), xmlResponse.indexOf("</lgnrq>")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		// --------------------------
		// Llamada al metodo de validar login
		// --------------------------

		final X509Certificate cert = (X509Certificate) ks.getCertificate(CERT_ALIAS);
		final String certB64 = Base64.encode(cert.getEncoded());

		final AOPkcs1Signer signer = new AOPkcs1Signer();
		final byte[] signature = signer.sign(Base64.decode(tokenB64), "SHA256withRSA", pke.getPrivateKey(), pke.getCertificateChain(), null); //$NON-NLS-1$

		xml = "<rqtvl><cert>" + certB64 + "</cert><pkcs1>" + Base64.encode(signature) + "</pkcs1></rqtvl>"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		System.out.println("==========" + xml);

		url = URL_BASE + createUrlParams("11", xml); //$NON-NLS-1$

		data = urlManager.readUrl(url, UrlHttpMethod.POST);

		xmlResponse = new String(data);

		System.out.println("Respuesta a la peticion de validacion de login:\n" + xmlResponse); //$NON-NLS-1$

		Assert.assertTrue("La validacion del login devolvio un error", xmlResponse.indexOf("ok='true'") != -1); //$NON-NLS-1$ //$NON-NLS-2$

		// --------------------------
		// Llamada a un metodo de operacion
		// --------------------------

		xml = "<rqtconf><cert>" + Base64.encode(cert.getEncoded()) + "</cert></rqtconf>"; //$NON-NLS-1$ //$NON-NLS-2$

		url = URL_BASE + createUrlParams("6", xml); //$NON-NLS-1$

		data = urlManager.readUrl(url, UrlHttpMethod.POST);

		xmlResponse = new String(data);

		Assert.assertNotSame(
				"Se obtuvo el mensaje de error en la autenticacion", //$NON-NLS-1$
				"<err cd=\"ERR-11\">Error en la autenticacion de la peticion</err>", //$NON-NLS-1$
				xmlResponse);

		// --------------------------
		// Llamada al metodo de cierre de sesion
		// --------------------------

		 xml = "<lgorq/>"; //$NON-NLS-1$

		url = URL_BASE + createUrlParams("12", xml); //$NON-NLS-1$

		data = urlManager.readUrl(url, UrlHttpMethod.POST);

		xmlResponse = new String(data);

		Assert.assertEquals(
				"No se obtuvo la respuesta de cierre de sesion", //$NON-NLS-1$
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?><lgorq/>", //$NON-NLS-1$
				xmlResponse);

		// --------------------------
		// Llamada a un metodo de operacion
		// --------------------------

		xml = "<rqtconf><cert>" + Base64.encode(cert.getEncoded()) + "</cert></rqtconf>"; //$NON-NLS-1$ //$NON-NLS-2$

		url = URL_BASE + createUrlParams("6", xml); //$NON-NLS-1$

		data = urlManager.readUrl(url, UrlHttpMethod.POST);

		xmlResponse = new String(data);

		Assert.assertEquals(
				"El servicio no notifico un error de autenticacion", //$NON-NLS-1$
				"<err cd=\"ERR-11\">Error en la autenticacion de la peticion</err>", //$NON-NLS-1$
				xmlResponse);
	}
}
