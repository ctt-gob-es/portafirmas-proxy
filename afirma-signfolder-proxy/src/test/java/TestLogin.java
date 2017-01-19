import java.security.KeyStore;
import java.security.KeyStore.PasswordProtection;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.MessageDigest;
import java.security.cert.X509Certificate;
import java.util.Properties;

import org.junit.Test;

import es.gob.afirma.core.misc.Base64;
import es.gob.afirma.core.misc.http.UrlHttpManager;
import es.gob.afirma.core.misc.http.UrlHttpManagerFactory;
import es.gob.afirma.core.misc.http.UrlHttpMethod;
import es.gob.afirma.signers.cades.AOCAdESSigner;
import junit.framework.Assert;

public class TestLogin {

	private static final String CERT_PATH = "ANCERTCCP_FIRMA.p12"; //$NON-NLS-1$
	private static final char[] CERT_PASS = "1111".toCharArray(); //$NON-NLS-1$
	private static final String CERT_ALIAS = "juan ejemplo español"; //$NON-NLS-1$

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
		final X509Certificate cert = (X509Certificate) ks.getCertificate(CERT_ALIAS);

		final byte[] certDigest = MessageDigest.getInstance("SHA-256").digest(cert.getEncoded()); //$NON-NLS-1$
		final String certDigestB64 = Base64.encode(certDigest);

		String xml = "<lgnrq cd=\"" + certDigestB64 + "\" />"; //$NON-NLS-1$ //$NON-NLS-2$

		String url = URL_BASE + createUrlParams("10", xml); //$NON-NLS-1$

		byte[] data = urlManager.readUrl(url, UrlHttpMethod.POST);

		String xmlResponse = new String(data);

		System.out.println("Respuesta a la peticion de login:\n" + new String(xmlResponse)); //$NON-NLS-1$

		final String tokenB64 = xmlResponse.substring(xmlResponse.indexOf("'>") + "'>".length(), xmlResponse.indexOf("</ss>")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		// --------------------------
		// Llamada al metodo de validar login
		// --------------------------

		final Properties extraParams = new Properties();
		extraParams.setProperty("mode", "implicit"); //$NON-NLS-1$ //$NON-NLS-2$

		final AOCAdESSigner signer = new AOCAdESSigner();
		final byte[] signature = signer.sign(Base64.decode(tokenB64), "SHA1withRSA", pke.getPrivateKey(), pke.getCertificateChain(), extraParams); //$NON-NLS-1$

		xml = "<rqtvl><sg>" + Base64.encode(signature) + "</sg></rqtvl>"; //$NON-NLS-1$ //$NON-NLS-2$

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
}
