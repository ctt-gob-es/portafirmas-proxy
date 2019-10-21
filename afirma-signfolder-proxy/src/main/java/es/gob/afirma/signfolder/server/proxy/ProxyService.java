package es.gob.afirma.signfolder.server.proxy;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.activation.DataHandler;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.bind.JAXBElement;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import es.gob.afirma.core.misc.AOUtil;
import es.gob.afirma.core.misc.Base64;
import es.gob.afirma.core.signers.TriphaseData;
import es.gob.afirma.signfolder.client.MobileAccesoClave;
import es.gob.afirma.signfolder.client.MobileApplication;
import es.gob.afirma.signfolder.client.MobileApplicationList;
import es.gob.afirma.signfolder.client.MobileDocSignInfo;
import es.gob.afirma.signfolder.client.MobileDocSignInfoList;
import es.gob.afirma.signfolder.client.MobileDocument;
import es.gob.afirma.signfolder.client.MobileDocumentList;
import es.gob.afirma.signfolder.client.MobileException;
import es.gob.afirma.signfolder.client.MobileFireDocument;
import es.gob.afirma.signfolder.client.MobileFireRequest;
import es.gob.afirma.signfolder.client.MobileFireRequestList;
import es.gob.afirma.signfolder.client.MobileFireTrasactionResponse;
import es.gob.afirma.signfolder.client.MobileRequest;
import es.gob.afirma.signfolder.client.MobileRequestFilter;
import es.gob.afirma.signfolder.client.MobileRequestFilterList;
import es.gob.afirma.signfolder.client.MobileRequestList;
import es.gob.afirma.signfolder.client.MobileSIMUser;
import es.gob.afirma.signfolder.client.MobileSIMUserStatus;
import es.gob.afirma.signfolder.client.MobileService;
import es.gob.afirma.signfolder.client.MobileService_Service;
import es.gob.afirma.signfolder.client.MobileSignLine;
import es.gob.afirma.signfolder.client.MobileStringList;
import es.gob.afirma.signfolder.server.proxy.SignLine.SignLineType;
import es.gob.afirma.signfolder.server.proxy.sessions.SessionCollector;

/** Servicio Web para firma trif&aacute;sica.
 * @author Tom&aacute;s Garc&iacute;a-;er&aacute;s */
public final class ProxyService extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final String DEFAULT_CHARSET = "utf-8";  //$NON-NLS-1$

	private static final String SIGNATURE_SERVICE_URL = "TRIPHASE_SERVER_URL"; //$NON-NLS-1$

	private static final String PARAMETER_NAME_OPERATION = "op"; //$NON-NLS-1$
	private static final String PARAMETER_NAME_DATA = "dat"; //$NON-NLS-1$
	private static final String PARAMETER_NAME_SHARED_SESSION_ID = "ssid"; //$NON-NLS-1$


	private static final String OPERATION_PRESIGN = "0"; //$NON-NLS-1$
	private static final String OPERATION_POSTSIGN = "1"; //$NON-NLS-1$
	private static final String OPERATION_REQUEST = "2"; //$NON-NLS-1$
	private static final String OPERATION_REJECT = "3"; //$NON-NLS-1$
	private static final String OPERATION_DETAIL = "4"; //$NON-NLS-1$
	private static final String OPERATION_DOCUMENT_PREVIEW = "5"; //$NON-NLS-1$
	private static final String OPERATION_CONFIGURING = "6"; //$NON-NLS-1$
	private static final String OPERATION_APPROVE = "7"; //$NON-NLS-1$
	private static final String OPERATION_SIGN_PREVIEW = "8"; //$NON-NLS-1$
	private static final String OPERATION_REPORT_PREVIEW = "9"; //$NON-NLS-1$
	private static final String OPERATION_REQUEST_LOGIN = "10"; //$NON-NLS-1$
	private static final String OPERATION_VALIDATE_LOGIN = "11"; //$NON-NLS-1$
	private static final String OPERATION_LOGOUT = "12"; //$NON-NLS-1$
	private static final String OPERATION_REGISTER_NOTIFICATION_SYSTEM = "13"; //$NON-NLS-1$
	private static final String OPERATION_CLAVE_LOGIN = "14"; //$NON-NLS-1$
	private static final String OPERATION_FIRE_LOAD_DATA = "16"; //$NON-NLS-1$
	private static final String OPERATION_FIRE_SIGN = "17"; //$NON-NLS-1$

	private static final String[]  OPERATIONS_WITHOUT_LOGIN = new String[] {
			OPERATION_REQUEST_LOGIN,
			OPERATION_VALIDATE_LOGIN,
			OPERATION_CLAVE_LOGIN
	};

	private static final String CRYPTO_PARAM_NEED_DATA = "NEED_DATA"; //$NON-NLS-1$

	private static final String DATE_TIME_FORMAT = "dd/MM/yyyy  HH:mm"; //$NON-NLS-1$

	private static final String LOGIN_SIGNATURE_ALGORITHM = "SHA256withRSA"; //$NON-NLS-1$

	private static final String PAGE_CLAVE_LOADING = "clave-loading.jsp"; //$NON-NLS-1$

	static final Logger LOGGER = Logger.getLogger("es.gob.afirma"); //$NON-NLS-1$:

	private static final boolean DEBUG = false;

	private final DocumentBuilder documentBuilder;

	static {
		if (DEBUG) {
			// Configuracion de un truststore con el certificado SSL del servicio del Portafirmas
			System.setProperty("javax.net.ssl.trustStore", "C:\\Users\\carlos.gamuci\\Documents\\Afirma\\Repositorios_GitHub\\portafirmas-proxy\\afirma-signfolder-proxy\\src\\test\\resources\\redsara_ts.jks"); //$NON-NLS-1$ //$NON-NLS-2$
			System.setProperty("javax.net.ssl.trustStorePassword", "111111"); //$NON-NLS-1$ //$NON-NLS-2$
			System.setProperty("javax.net.ssl.trustStoreType", "JKS"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}


	/** Construye un Servlet que sirve operaciones de firma trif&aacute;sica.
	 * @throws ParserConfigurationException Cuando no puede crearse un <code>DocumentBuilder</code> XML */
	public ProxyService() throws ParserConfigurationException {
		this.documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

		LOGGER.info("Cargamos el fichero de configuracion del Proxy"); //$NON-NLS-1$
		ConfigManager.checkInitialized();

		// Si esta configurada la variable SIGNATURE_SERVICE_URL en el sistema, se utiliza en lugar de propiedad
		// interna de la aplicacion
		try {
			final String systemSignatureServiceUrl = System.getProperty(SIGNATURE_SERVICE_URL);
			if (systemSignatureServiceUrl != null) {
				ConfigManager.setTriphaseServiceUrl(systemSignatureServiceUrl);
				LOGGER.info("Se sustituye la URL del servicio de firma por la configurada en la propiedad del sistema " + SIGNATURE_SERVICE_URL + " con el valor: " + systemSignatureServiceUrl);	 //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		catch (final Exception e) {
			LOGGER.warning("No se ha podido recuperar la URL del servicio de firma configurado en la variable " + SIGNATURE_SERVICE_URL + " del sistema: " + e);	 //$NON-NLS-1$ //$NON-NLS-2$
		}

		if (DEBUG) {
			disabledSslSecurity();
		}
	}

	private static TrustManager[] DUMMY_TRUST_MANAGER = null;
	private static HostnameVerifier HOSTNAME_VERIFIER = null;

	private static void disabledSslSecurity() {

		// Si ya esta establecido, no repetimos
		if (HOSTNAME_VERIFIER != null && HOSTNAME_VERIFIER == HttpsURLConnection.getDefaultHostnameVerifier()) {
			return;
		}

		DUMMY_TRUST_MANAGER = new TrustManager[] {
				new X509TrustManager() {
					@Override
					public java.security.cert.X509Certificate[] getAcceptedIssuers() {
						return null;
					}
					@Override
					public void checkClientTrusted(final X509Certificate[] certs, final String authType) { /* No hacemos nada */ }
					@Override
					public void checkServerTrusted(final X509Certificate[] certs, final String authType) {  /* No hacemos nada */  }

				}
		};

		HOSTNAME_VERIFIER = new HostnameVerifier() {
			@Override
			public boolean verify(final String hostname, final SSLSession session) {
				return true;
			}
		};

		try {
			final SSLContext sc = SSLContext.getInstance("SSL"); //$NON-NLS-1$
			sc.init(
				null,
				DUMMY_TRUST_MANAGER,
				new java.security.SecureRandom()
			);

			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			HttpsURLConnection.setDefaultHostnameVerifier(HOSTNAME_VERIFIER);
		} catch (final Exception e) {
			LOGGER.log(Level.WARNING, "No se pudo deshabilitar la verificacion del SSL", e); //$NON-NLS-1$
		}
	}

	/** Realiza una operaci&oacute;n de firma en tres fases.
	 * Acepta los siguientes c&oacute;digos de operaci&oacute;n en el par&aacute;metro <code>op</code>:
	 * <dl>
	 *  <dt>1</dt>
	 *   <dd>Firma</dd>
	 *  <dt>2</dt>
	 *   <dd>Petici&oacute;n de solicitudes</dd>
	 *  <dt>3</dt>
	 *   <dd>Rechazo de solicitudes</dd>
	 *  <dt>4</dt>
	 *   <dd>Detalle</dd>
	 *  <dt>5</dt>
	 *   <dd>Previsualizaci&oacute;n</dd>
	 *  </dl>
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse response) */
	@Override
	protected void service(final HttpServletRequest request, final HttpServletResponse response) {

		LOGGER.info("Peticion al proxy Portafirmas"); //$NON-NLS-1$

		final Responser responser;
		try {
			responser = new Responser(response);
		} catch (final Exception e) {
			LOGGER.severe("No se puede responder a la peticion: " + e);	 //$NON-NLS-1$
			return;
		}

		final String operation = request.getParameter(PARAMETER_NAME_OPERATION);
		if (operation == null) {
			responser.print(ErrorManager.genError(ErrorManager.ERROR_MISSING_OPERATION_NAME, null));
			return;
		}

		final String data = request.getParameter(PARAMETER_NAME_DATA);
		if (data == null) {
			LOGGER.severe("No se han proporcionado los datos"); //$NON-NLS-1$
			responser.print(ErrorManager.genError(ErrorManager.ERROR_MISSING_DATA, null));
			return;
		}

		byte[] xml;
		try {
			xml = GzipCompressorImpl.gunzip(Base64.decode(data, true));
		}
		catch(final IOException e) {
			if (DEBUG) {
				LOGGER.fine("Los datos de entrada no estan comprimidos: " + e); //$NON-NLS-1$
			}
			try {
				xml = Base64.decode(data, true);
			} catch (final Exception ex) {
				LOGGER.warning("Los datos de entrada no estan correctamente codificados: " + ex); //$NON-NLS-1$
				return;
			}
		}

		if (DEBUG) {
			LOGGER.info("XML de la peticion:\n" + new String(xml)); //$NON-NLS-1$
		}

		Object ret;

		try {
			HttpSession session = null;
			if (operationNeedLogin(operation)) {
				final String ssid = request.getParameter(PARAMETER_NAME_SHARED_SESSION_ID);
				session = SessionCollector.getSession(request, ssid);
				if (session == null || !Boolean.parseBoolean((String) session.getAttribute(SessionParams.VALID_SESSION))) {
					LOGGER.warning("Se ha solicitado la siguiente operacion del proxy sin estar autenticado: " + operation); //$NON-NLS-1$
					ret = ErrorManager.genError(ErrorManager.ERROR_AUTHENTICATING_REQUEST);
					if (session != null) {
						SessionCollector.removeSession(session);
					}
				}
				else {
					ret = processRequest(operation, xml, request, session);
				}
			}
			else {
				ret = processRequest(operation, xml, request, session);
			}

		} catch(final SAXException e) {
			LOGGER.log(Level.SEVERE, ErrorManager.genError(ErrorManager.ERROR_BAD_XML) + ": " + e, e); //$NON-NLS-1$
			responser.print(ErrorManager.genError(ErrorManager.ERROR_BAD_XML));
			return;
		} catch (final CertificateException e) {
			LOGGER.log(Level.SEVERE, ErrorManager.genError(ErrorManager.ERROR_BAD_CERTIFICATE) + ": " + e, e); //$NON-NLS-1$
			responser.print(ErrorManager.genError(ErrorManager.ERROR_BAD_CERTIFICATE));
			return;
		} catch (final MobileException e) {
			LOGGER.log(Level.SEVERE, ErrorManager.genError(ErrorManager.ERROR_COMMUNICATING_PORTAFIRMAS) + ": " + e, e); //$NON-NLS-1$
			responser.print(ErrorManager.genError(ErrorManager.ERROR_COMMUNICATING_PORTAFIRMAS));
			return;
		} catch (final IOException e) {
			LOGGER.log(Level.SEVERE, ErrorManager.genError(ErrorManager.ERROR_COMMUNICATING_PORTAFIRMAS) + ": " + e, e); //$NON-NLS-1$
			responser.print(ErrorManager.genError(ErrorManager.ERROR_COMMUNICATING_PORTAFIRMAS));
			return;
		} catch (final SOAPFaultException e) {
			LOGGER.log(Level.SEVERE, ErrorManager.genError(ErrorManager.ERROR_AUTHENTICATING_REQUEST) + ": " + e, e); //$NON-NLS-1$
			responser.print(ErrorManager.genError(ErrorManager.ERROR_AUTHENTICATING_REQUEST));
			return;
		} catch (final WebServiceException e) {
			LOGGER.log(Level.SEVERE, ErrorManager.genError(ErrorManager.ERROR_COMMUNICATING_SERVICE) + ": " + e, e); //$NON-NLS-1$
			responser.print(ErrorManager.genError(ErrorManager.ERROR_COMMUNICATING_SERVICE));
			return;
		} catch (final Exception e) {
			LOGGER.log(Level.SEVERE, ErrorManager.genError(ErrorManager.ERROR_UNKNOWN_ERROR) + ": " + e, e); //$NON-NLS-1$
			responser.print(ErrorManager.genError(ErrorManager.ERROR_UNKNOWN_ERROR));
			return;
		}

		if (ret instanceof InputStream) {
			LOGGER.info("La respuesta es un flujo de datos de salida"); //$NON-NLS-1$
			responser.write((InputStream) ret);
			try {
				((InputStream) ret).close();
			} catch (final IOException e) {
				LOGGER.warning("No se pudo cerrar el flujo de datos: " + e); //$NON-NLS-1$
			}
		}
		else {
			if (DEBUG) {
				LOGGER.info("XML de respuesta:\n" + ret); //$NON-NLS-1$
			}
			responser.print((String) ret);
		}
		if (DEBUG) {
			LOGGER.info("Fin peticion ProxyService"); //$NON-NLS-1$
		}
	}

	private Object processRequest(final String operation, final byte[] xml,
			final HttpServletRequest request, final HttpSession session)
			throws CertificateException, SAXException, IOException, MobileException {

		Object ret;
		if (OPERATION_REQUEST_LOGIN.equals(operation)) {
			LOGGER.info("Solicitud de login"); //$NON-NLS-1$
			ret = processRequestLogin(request, xml);
		}
		else if (OPERATION_VALIDATE_LOGIN.equals(operation)) {
			LOGGER.info("Validacion de login"); //$NON-NLS-1$
			final String ssid = request.getParameter(PARAMETER_NAME_SHARED_SESSION_ID);
			ret = processValidateLogin(request, xml, ssid);
		}
		else if (OPERATION_CLAVE_LOGIN.equals(operation)) {
			LOGGER.info("Solicitud de login con Cl@ve"); //$NON-NLS-1$
			ret = processRequestClaveLogin(request, xml);
		}
		else if (OPERATION_LOGOUT.equals(operation)) {
			LOGGER.info("Logout"); //$NON-NLS-1$
			ret = processLogout(session, xml);
		}
		else if (OPERATION_REGISTER_NOTIFICATION_SYSTEM.equals(operation)) {
			LOGGER.info("Registro en el sistema de notificaciones"); //$NON-NLS-1$
			ret = processNotificationRegistry(session, xml);
		}
		else if (OPERATION_PRESIGN.equals(operation)) {
			LOGGER.info("Solicitud de prefirma"); //$NON-NLS-1$
			ret = processPreSigns(session, xml);
		}
		else if (OPERATION_POSTSIGN.equals(operation)) {
			LOGGER.info("Solicitud de postfirma"); //$NON-NLS-1$
			ret = processPostSigns(session, xml);
		}
		else if (OPERATION_REQUEST.equals(operation)) {
			LOGGER.info("Solicitud del listado de peticiones"); //$NON-NLS-1$
			ret = processRequestsList(session, xml);
		}
		else if (OPERATION_REJECT.equals(operation)) {
			LOGGER.info("Solicitud de rechazo peticiones"); //$NON-NLS-1$
			ret = processRejects(session, xml);
		}
		else if (OPERATION_DETAIL.equals(operation)) {
			LOGGER.info("Solicitud de detalle de una peticion"); //$NON-NLS-1$
			ret = processRequestDetail(session, xml);
		}
		else if (OPERATION_DOCUMENT_PREVIEW.equals(operation)) {
			LOGGER.info("Solicitud de previsualizacion de un documento"); //$NON-NLS-1$
			ret = processDocumentPreview(session, xml);
		}
		else if (OPERATION_CONFIGURING.equals(operation)) {
			LOGGER.info("Solicitud de la configuracion"); //$NON-NLS-1$
			ret = processConfigueApp(session, xml);
		}
		else if (OPERATION_APPROVE.equals(operation)) {
			LOGGER.info("Solicitud de aprobacion de una peticion"); //$NON-NLS-1$
			ret = processApproveRequest(session, xml);
		}
		else if (OPERATION_SIGN_PREVIEW.equals(operation)) {
			LOGGER.info("Solicitud de previsualizacion de una firma"); //$NON-NLS-1$
			ret = processSignPreview(session, xml);
		}
		else if (OPERATION_REPORT_PREVIEW.equals(operation)) {
			LOGGER.info("Solicitud de previsualizacion de un informe de firma"); //$NON-NLS-1$
			ret = processSignReportPreview(session, xml);
		}
		else if (OPERATION_FIRE_LOAD_DATA.equals(operation)) {
			LOGGER.info("Solicitud de carga de datos con FIRe"); //$NON-NLS-1$
			ret = processFireLoadData(session, xml);
		}
		else if (OPERATION_FIRE_SIGN.equals(operation)) {
			LOGGER.info("Solicitud de firma con FIRe"); //$NON-NLS-1$
			ret = processFireSign(session, xml);
		}
		else {
			LOGGER.warning("Se ha indicado un codigo de operacion no valido"); //$NON-NLS-1$
			ret = ErrorManager.genError(ErrorManager.ERROR_UNSUPPORTED_OPERATION_NAME);
		}

		return ret;
	}

	/**
	 * Indica si una operaci&oacute;n requiere que antes se haya hecho login para que
	 * sea atendida por el proxy.
	 * @param operation C&oacute;digo de la operaci&oacute;n que se quiere comprobar.
	 * @return {@code true} si el c&oacute;digo proporcionado no est&aacute; en el listado
	 * de operaciones que se pueden realizar sin login, {@code false} en caso contrario.
	 */
	private static boolean operationNeedLogin(final String operation) {

		for (final String opwl : OPERATIONS_WITHOUT_LOGIN) {
			if (opwl.equals(operation)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Procesa una petici&oacute;n de acceso a la aplicaci&oacute;n. Como respuesta a esta
	 * petici&oacute;n, se emitir&aacute; un token para la firma por parte del cliente y
	 * posterior validaci&oacute;n de la sesi&oacute;n.
	 * @param request Petici&oacute;n realizada al servicio.
	 * @param xml XML con los datos para el proceso de autenticaci&oacute;n.
	 * @return XML con el resultado a la petici&oacute;n.
	 * @throws SAXException Cuando ocurre alg&uacute;n error al procesar los XML.
	 * @throws IOException Cuando ocurre alg&uacute;n problema de comunicaci&oacute;n con el servidor.
	 */
	private String processRequestLogin(final HttpServletRequest request,  final byte[] xml)
			throws SAXException, IOException {

		LOGGER.info("Solicitud de nueva sesion del Portafirmas movil"); //$NON-NLS-1$

		final Document doc = this.documentBuilder.parse(new ByteArrayInputStream(xml));
		try {
			LoginRequestParser.parse(doc);
		}
		catch (final Exception e) {
			LOGGER.warning("No se ha proporcionado una peticion de login valida: " + e); //$NON-NLS-1$
			throw new SAXException("No se ha proporcionado una peticion de login valida", e); //$NON-NLS-1$
		}

		HttpSession session = SessionCollector.getSession(request, null);
		if (session != null) {
			SessionCollector.removeSession(session);
		}

		session = SessionCollector.createSession(request);

		final LoginRequestData loginRequestData = createLoginRequestData(session);

		session.setAttribute(SessionParams.INIT_TOKEN, loginRequestData.getData());

		// Si hay que compartir la sesion, se obtiene el ID de sesion compartida
		String sessionId = null;
		if (ConfigManager.isShareSessionEnabled() && ConfigManager.isShareSessionWithCertEnabled()) {
			sessionId = SessionCollector.createSharedSession(session);
		}

		LOGGER.info("Devolvemos el identificador de sesion y los datos a firmar para su validacion: " + session.getId()); //$NON-NLS-1$

		return XmlResponsesFactory.createRequestLoginResponse(loginRequestData, sessionId);
	}

	/**
	 * Obtiene los datos necesarios para el login de la aplicaci&oacute;n cliente.
	 * @param session Sesi&oacute;n sobre la que se desea autenticar el usuario.
	 * @return Datos de inicio de sesi&oacute;n.
	 */
	private static LoginRequestData createLoginRequestData(final HttpSession session) {

		final LoginRequestData loginRequestData = new LoginRequestData(session.getId());
		// Establecemos los datos a firmar (Token)
		loginRequestData.setData(new StringBuilder()
				.append(session.getCreationTime()).append("|") //$NON-NLS-1$
				.append(UUID.randomUUID().toString()).toString().getBytes());

		return loginRequestData;
	}

	/**
	 * Valida el acceso de la aplicaci&oacute;n al Portafirmas. Como resultado, se enviar&aacute;
	 * el DNI del usuario autenticado.
	 * @param request Petici&oacute;n realizada al servicio.
	 * @param xml XML con los datos para el proceso de autenticaci&oacute;n.
	 * @return XML con el resultado a la petici&oacute;n.
	 * @throws SAXException Cuando ocurre alg&uacute;n error al procesar los XML.
	 * @throws IOException Cuando ocurre alg&uacute;n problema de comunicaci&oacute;n con el servidor.
	 */
	private String processValidateLogin(final HttpServletRequest request,  final byte[] xml, final String ssid) throws SAXException, IOException {

		LOGGER.info("Validacion del login del usuario"); //$NON-NLS-1$

		final Document doc = this.documentBuilder.parse(new ByteArrayInputStream(xml));
		final ValidateLoginRequest loginRequest = ValidateLoginRequestParser.parse(doc);

		final HttpSession session = SessionCollector.getSession(request, ssid);

		final ValidateLoginResult validateLoginResult = validateLoginData(session, loginRequest);

		if (validateLoginResult.isLogged()) {
			session.setAttribute(SessionParams.VALID_SESSION, Boolean.TRUE.toString());
			// Se guarda el certificado en sesion para realizar peticiones. En el futuro no se enviara
			if (loginRequest.getCertificate() != null) {
				session.setAttribute(SessionParams.CERT, Base64.encode(loginRequest.getCertificate()));
			}

			session.setAttribute(SessionParams.DNI, validateLoginResult.getDni());

			session.removeAttribute(SessionParams.INIT_TOKEN);

			SessionCollector.updateSession(session);
		}

		LOGGER.info("Devolvemos el resultado del proceso de login para la sesion " + //$NON-NLS-1$
				(session == null ? "null" : session.getId()) + ": " +  //$NON-NLS-1$ //$NON-NLS-2$
				validateLoginResult.isLogged());

		return XmlResponsesFactory.createValidateLoginResponse(validateLoginResult);
	}

	private static ValidateLoginResult validateLoginData(final HttpSession session, final ValidateLoginRequest loginRequest) {

		final ValidateLoginResult result = new ValidateLoginResult();
		if (session == null) {
			result.setError("No se ha realizado previamente el inicio de sesion"); //$NON-NLS-1$
			return result;
		}

		// Comprobamos la validez de la firma PKCS1 remitida contra el certificado recibido
		// y el token que se envio originalmente, y se manda a validar el certificado para
		// el inicio de sesion
		try {
			checkPkcs1(loginRequest.getPkcs1(),
					loginRequest.getCertificate(),
					(byte[]) session.getAttribute(SessionParams.INIT_TOKEN));
		}
		catch (final Exception e) {
			LOGGER.log(Level.WARNING, "Ocurrio un error durante la comprobacion del token de sesion. No se permitira el acceso: " + e); //$NON-NLS-1$
			result.setError(e.getMessage());
			return result;
		}

		if (DEBUG) {
			disabledSslSecurity();
		}

		try {
			// Validacion contra el portafirmas (que valide que
			// la firma es valida y el certificado usado se corresponde con
			// un usuario)
			final MobileService_Service mobileService = new MobileService_Service(ConfigManager.getSignfolderUrl());
			final MobileService service = mobileService.getMobileServicePort();

			final String dni = service.validateUser(loginRequest.getCertificate());

			result.setDni(dni);
		}
		catch (final Exception e) {
			LOGGER.log(Level.WARNING, "Ocurrio un error durante la validacion de la firma de login. No se permitira el acceso", e); //$NON-NLS-1$
			result.setError(e.getMessage());
		}
		return result;
	}

	/**
	 * Procesa una petici&oacute;n de acceso al Portafirmas autentic&aacute;ndose con Cl@ve.
	 * Como respuesta a esta petici&oacute;n, se generar&aacute; la informaci&oacute;n de inicio de
	 * sesi&oacute;n y se remitir&aacute; una URL para la redirecci&oacute;n del usuario a la p&aacute;gina
	 * de Cl@ve para que autorice el acceso.
	 * @param request Petici&oacute;n realizada al servicio.
	 * @param xml XML con los datos para el proceso de autenticaci&oacute;n.
	 * @return XML con el resultado a la petici&oacute;n.
	 * @throws SAXException Cuando ocurre alg&uacute;n error al procesar los XML.
	 * @throws IOException Cuando ocurre alg&uacute;n problema de comunicaci&oacute;n con el servidor.
	 * @throws MobileException Cuando ocurre un error al contactar con el servidor.
	 */
	private String processRequestClaveLogin(final HttpServletRequest request,  final byte[] xml) throws SAXException, IOException, MobileException {

		LOGGER.info("Solicitud de nueva sesion del Portafirmas movil con Cl@ve"); //$NON-NLS-1$

		final Document doc = this.documentBuilder.parse(new ByteArrayInputStream(xml));
		try {
			LoginClaveRequestParser.parse(doc);
		}
		catch (final Exception e) {
			LOGGER.warning("No se ha proporcionado una peticion de login valida: " + e); //$NON-NLS-1$
			throw new SAXException("No se ha proporcionado una peticion de login valida", e); //$NON-NLS-1$
		}

		// Si ya existe una sesion, la invalidamos
		HttpSession session = SessionCollector.getSession(request, null);
		if (session != null) {
			SessionCollector.removeSession(session);
		}

		// Creamos una nueva sesion
		session = SessionCollector.createSession(request);

		// Si hay que compartir la sesion, se obtiene el ID de sesion compartida
		String sessionId = null;
		if (ConfigManager.isShareSessionEnabled()) {
			sessionId = SessionCollector.createSharedSession(session);
		}

		if (DEBUG) {
			disabledSslSecurity();
		}

		final String baseUrl = getProxyBaseUrl(request);
		String resultUrl = baseUrl + "claveResultService"; //$NON-NLS-1$
		if (sessionId != null) {
			resultUrl += "?" + PARAMETER_NAME_SHARED_SESSION_ID + "=" + sessionId; //$NON-NLS-1$ //$NON-NLS-2$
		}

		// Conectamos con el Portafirmas web
		final MobileService service = new MobileService_Service(ConfigManager.getSignfolderUrl()).getMobileServicePort();
		final MobileAccesoClave claveResponse = service.solicitudAccesoClave(resultUrl, resultUrl);

		if (claveResponse.getClaveServiceUrl() == null) {
			LOGGER.log(Level.WARNING, "Error en la solicitud de inicio de sesion con Cl@ve: No se recupero la URL"); //$NON-NLS-1$
			SessionCollector.removeSession(session);
			throw new IOException("No se recupero la URL de redireccion de Cl@ve"); //$NON-NLS-1$
		}
		session.setAttribute(SessionParams.CLAVE_URL, claveResponse.getClaveServiceUrl());

		if (claveResponse.getSamlRequest() == null) {
			LOGGER.log(Level.WARNING, "Error en la solicitud de inicio de sesion con Cl@ve: No se recupero el token SAML"); //$NON-NLS-1$
			SessionCollector.removeSession(session);
			throw new IOException("No se recupero el token de sesion de Cl@ve"); //$NON-NLS-1$
		}
		session.setAttribute(SessionParams.CLAVE_REQUEST_TOKEN, claveResponse.getSamlRequest());

		if (claveResponse.getExcludedIdPList() != null) {
			session.setAttribute(SessionParams.CLAVE_EXCLUDED_IDPS, claveResponse.getExcludedIdPList());
		}
		if (claveResponse.getForcedIdP() != null) {
			session.setAttribute(SessionParams.CLAVE_FORCED_IDP, claveResponse.getForcedIdP());
		}

		// Damos por iniciado el proceso de login con Clave
		session.setAttribute(SessionParams.INIT_WITH_CLAVE, Boolean.TRUE);

		// Generamos un identificador de inicio para comprobar mas adelante
		final String authenticationId = generateAuthenticationId();
		session.setAttribute(SessionParams.CLAVE_AUTHENTICATION_ID, authenticationId);

		// Se actualiza la sesion compatida con los nuevos datos asignados
		SessionCollector.updateSession(session);

		// Obtenemos la URL de redireccion
		String redirectionUrl = baseUrl + PAGE_CLAVE_LOADING;
		if (sessionId != null) {
			redirectionUrl += "?" + PARAMETER_NAME_SHARED_SESSION_ID + "=" + sessionId; //$NON-NLS-1$ //$NON-NLS-2$
		}

		return XmlResponsesFactory.createRequestClaveLoginResponse(redirectionUrl, sessionId);
	}

	/**
	 * Proporciona la URL base de las p&aacute;ginas y servicios del proxy.
	 * @param request Petici&oacute;n realizada.
	 * @return URL base del proxy terminada en '/'.
	 */
	private static String getProxyBaseUrl(final HttpServletRequest request) {
		final String baseUrl = request.getRequestURL().substring(0, request.getRequestURL().lastIndexOf("/") + 1); //$NON-NLS-1$
		return ConfigManager.getProxyBaseUrl() != null ?
				ConfigManager.getProxyBaseUrl() : baseUrl;
	}

	/**
	 * Genera un c&oacute;digo de autenticaci&oacute;n aleatorio.
	 * @return Identificador de acceso aleatorio.
	 */
	private static String generateAuthenticationId() {
		return UUID.randomUUID().toString();
	}

	/**
	 * Procesa una petici&oacute;n de cierre de sesi&oacute;n.
	 * @param request Petici&oacute;n realizada al servicio.
	 * @param xml XML con los datos para el proceso de autenticaci&oacute;n.
	 * @return XML con el resultado a la petici&oacute;n.
	 * @throws SAXException Cuando ocurre alg&uacute;n error al procesar los XML.
	 * @throws IOException Cuando ocurre alg&uacute;n problema de comunicaci&oacute;n con el servidor.
	 */
	private String processLogout(final HttpSession session,  final byte[] xml) throws SAXException, IOException {

		final Document doc = this.documentBuilder.parse(new ByteArrayInputStream(xml));
		try {
			LogoutRequestParser.parse(doc);
		}
		catch (final Exception e) {
			LOGGER.warning("No se ha proporcionado una peticion de logout valida: " + e); //$NON-NLS-1$
			throw new SAXException("No se ha proporcionado una peticion de logout valida", e); //$NON-NLS-1$
		}

		LOGGER.info("Solicitud de cierre de sesion del Portafirmas movil"); //$NON-NLS-1$

		if (session != null) {
			SessionCollector.removeSession(session);
		}

		LOGGER.info("Devolvemos el resultado del cierre de sesion"); //$NON-NLS-1$

		return XmlResponsesFactory.createRequestLogoutResponse();
	}

	/**
	 * Comprueba que un PKCS#1 se genero en base a un certificado y sobre unos datos concretos.
	 * @param pkcs1 Firma PKCS#1 calculada sobre el algoritmo SHA-256.
	 * @param certEncoded Certificado electr&oacute;nico con el que se gener&oacute; el PKCS#1.
	 * @param data Datos que se firmaron.
	 * @throws NoSuchAlgorithmException Cuando el algoritmo de firma no este soportado.
	 * @throws CertificateException Cuando el certificado de la firma no sea valido o no coincida con el indicado.
	 * @throws InvalidKeyException Cuando el certificado no contenga una clave publica v&aacute;lida.
	 * @throws Exception Cuando no se puede completar la validaci&oacute;n de la estructura.
	 */
	private static void checkPkcs1(final byte[] pkcs1, final byte[] certEncoded, final byte[] data) throws SignatureException, NoSuchAlgorithmException, CertificateException, InvalidKeyException {

		final Certificate cert = CertificateFactory.getInstance("X.509").generateCertificate( //$NON-NLS-1$
				new ByteArrayInputStream(certEncoded)
		);

		final Signature signer = Signature.getInstance(LOGIN_SIGNATURE_ALGORITHM);
		signer.initVerify(cert.getPublicKey());
		signer.update(data);

		if (!signer.verify(pkcs1)) {
			throw new SignatureException("La firma no se corresponde con la del token proporcionado o el certificado indicado"); //$NON-NLS-1$
		}
	}

	private String processNotificationRegistry(final HttpSession session, final byte[] xml) throws SAXException, IOException, MobileException {

		final Document xmlDoc = this.documentBuilder.parse(new ByteArrayInputStream(xml));
		final NotificationRegistry registry = NotificationRegistryParser.parse(xmlDoc);

		final String dni = (String) session.getAttribute(SessionParams.DNI);
		final NotificationRegistryResult result = doNotificationRegistry(dni, registry);

		return XmlResponsesFactory.createNotificationRegistryResponse(result);
	}

	private static NotificationRegistryResult doNotificationRegistry(final String dni, final NotificationRegistry registry)
			throws MobileException {

		if (DEBUG) {
			disabledSslSecurity();
		}

		final MobileService_Service mobileService = new MobileService_Service(ConfigManager.getSignfolderUrl());
		final MobileService service = mobileService.getMobileServicePort();

		LOGGER.info("Registramos al usuario en el sistema de notificaciones"); //$NON-NLS-1$

		final MobileSIMUser user = new MobileSIMUser();
		user.setIdDispositivo(registry.getDeviceId());	// Identificador unico del dispositivo
		user.setPlataforma(registry.getPlatform());		// Plataforma de notificacion ("GCM" para Android y "APNS" para iOS)
		user.setIdRegistro(registry.getIdRegistry());

		LOGGER.info("Registro de dispositivo: \nDispositivo: " + user.getIdDispositivo() + "\nPlataforma: " + user.getPlataforma() + "\nToken de registro: " + user.getIdRegistro()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$


		final MobileSIMUserStatus status = service.registerSIMUser(dni, user);

		LOGGER.info("Resultado del registro:\nResultado:" + status.getStatusCode() + "\nTexto: " + status.getStatusText() + "\nDetalle: " + status.getDetails()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		final NotificationRegistryResult result = new NotificationRegistryResult(
				status.getStatusCode(),
				status.getStatusText());
		if (!result.isRegistered()) {
			result.setErrorDetails(status.getDetails());
		}
		return result;
	}

	/**
	 * Procesa las peticiones de prefirma. Se realiza la prefirma de cada uno de los documentos de las peticiones indicadas.
	 * Si se produce alg&uacute;n error al procesar un documento de alguna de las peticiones, se establece como incorrecta
	 * la petici&oacute;n al completo.
	 * @param xml XML con los datos para el proceso de las prefirmas.
	 * @return XML con el resultado a la petici&oacute;n de prefirma.
	 * @throws SAXException Cuando ocurre alg&uacute;n error al procesar los XML.
	 * @throws IOException Cuando ocurre alg&uacute;n problema de comunicaci&oacute;n con el servidor.
	 * @throws CertificateException Cuando ocurre alg&uacute;n problema con el certificado de firma.
	 */
	private String processPreSigns(final HttpSession session, final byte[] xml) throws SAXException, IOException, CertificateException {

		// Cargamos los datos trifasicos
		final Document xmlDoc = this.documentBuilder.parse(new ByteArrayInputStream(xml));
		final TriphaseRequestBean triRequests = SignRequestsParser.parse(xmlDoc, Base64.decode((String) session.getAttribute(SessionParams.CERT)));

		// Prefirmamos
		preSign(triRequests);

		// Generamos la respuesta
		return XmlResponsesFactory.createPresignResponse(triRequests);
	}



	/**
	 * Procesa las peticiones de postfirma. Se realiza la postfirma de cada uno de los documentos de las peticiones indicadas.
	 * Si se produce alg&uacute;n error al procesar un documento de alguna de las peticiones, se establece como incorrecta
	 * la petici&oacute;n al completo.
	 * @param xml XML con los datos para el proceso de las prefirmas.
	 * @return XML con el resultado a la petici&oacute;n de prefirma.
	 * @throws SAXException Cuando ocurre alg&uacute;n error al procesar los XML.
	 * @throws IOException Cuando ocurre alg&uacute;n problema de comunicaci&oacute;n con el servidor.
	 * @throws CertificateException Cuando ocurre alg&uacute;n problema con el certificado de firma.
	 */
	private String processPostSigns(final HttpSession session, final byte[] xml) throws SAXException, IOException, CertificateException {

		final Document xmlDoc = this.documentBuilder.parse(new ByteArrayInputStream(xml));

		final byte[] cer = Base64.decode((String) session.getAttribute(SessionParams.CERT));
		final TriphaseRequestBean triRequests = SignRequestsParser.parse(xmlDoc, cer);

		// Ejecutamos las postfirmas y se registran las firmas en el servidor
		postSign(triRequests);

		// Generamos la respuesta
		return XmlResponsesFactory.createPostsignResponse(triRequests);
	}

	/**
	 * Transforma una peticion de tipo TriphaseRequest en un MobileDocSignInfoList.
	 * @param req Petici&oacute;n de firma con el resultado asociado a cada documento.
	 * @return Listado de firmas de documentos.
	 */
	private static MobileDocSignInfoList transformToWsParams(final TriphaseRequest req) {

		final MobileDocSignInfoList signInfoList = new MobileDocSignInfoList();
		final List<MobileDocSignInfo> list = signInfoList.getMobileDocSignInfo();

		MobileDocSignInfo signInfo;
		for (final TriphaseSignDocumentRequest docReq : req) {
			signInfo = new MobileDocSignInfo();
			signInfo.setDocumentId(docReq.getId());
			signInfo.setSignFormat(docReq.getSignatureFormat());
			signInfo.setSignature(new DataHandler(
					new ByteArrayDataSource(docReq.getResult(), null)));
			list.add(signInfo);
		}

		return signInfoList;
	}

	/**
	 * Procesa la petici&oacute;n de un listado de peticiones de firma.
	 * @param xml XML con la solicitud.
	 * @return XML con la respuesta a la petici&oacute;n.
	 * @throws SAXException Cuando ocurre alg&uacute;n error al procesar los XML.
	 * @throws IOException Cuando ocurre alg&uacute;n errlr al leer el XML.
	 * @throws MobileException Cuando ocurre un error al contactar con el servidor.
	 */
	private String processRequestsList(final HttpSession session, final byte[] xml) throws SAXException, IOException, MobileException {

		final Document doc = this.documentBuilder.parse(new ByteArrayInputStream(xml));
		final ListRequest listRequest = ListRequestParser.parse(doc);
		LOGGER.info("Solicitamos las peticiones de firma al Portafirmas"); //$NON-NLS-1$

		final String dni = (String) session.getAttribute(SessionParams.DNI);
		final PartialSignRequestsList signRequests = getRequestsList(dni, listRequest);

		LOGGER.info("Hemos obtenido las peticiones de firma del Portafirmas"); //$NON-NLS-1$

		return XmlResponsesFactory.createRequestsListResponse(signRequests);
	}

	/**
	 * Recupera un listado de peticiones del Portafirmas a partir de la solicitud proporcionada.
	 * @param dni DNI del usuario.
	 * @param listRequest Solicitud de peticiones de firma.
	 * @return Listado de peticiones.
	 * @throws MobileException Cuando ocurre un error al contactar con el Portafirmas.
	 */
	private static PartialSignRequestsList getRequestsList(final String dni, final ListRequest listRequest) throws MobileException {

		if (DEBUG) {
			disabledSslSecurity();
		}

		final MobileService_Service mobileService = new MobileService_Service(ConfigManager.getSignfolderUrl());
		final MobileService service = mobileService.getMobileServicePort();

		// Listado de formatos de firma soportados
		final MobileStringList formatsList = new MobileStringList();
		for (final String supportedFormat : listRequest.getFormats()) {
			formatsList.getStr().add(supportedFormat);
		}

		// Listado de filtros para la consulta
		final MobileRequestFilterList filterList = new MobileRequestFilterList();
		if (listRequest.getFilters() != null) {
			for (final String filterKey : listRequest.getFilters().keySet().toArray(new String[listRequest.getFilters().size()])) {
				final MobileRequestFilter filter = new MobileRequestFilter();
				filter.setKey(filterKey);
				filter.setValue(listRequest.getFilters().get(filterKey));
				filterList.getRequestFilter().add(filter);
			}
		}

		// Solicitud de lista de peticiones
		final MobileRequestList mobileRequestsList = service.queryRequestList(
				dni.getBytes(),
				listRequest.getState(),
				Integer.toString(listRequest.getNumPage()),
				Integer.toString(listRequest.getPageSize()),
				formatsList,
				filterList);

		final SimpleDateFormat dateFormater = new SimpleDateFormat("dd/MM/yyyy"); //$NON-NLS-1$
		final List<SignRequest> signRequests = new ArrayList<>(mobileRequestsList.getSize().intValue());
		for (final MobileRequest request : mobileRequestsList.getRequest()) {

			final List<MobileDocument> docList = request.getDocumentList() != null ?
					request.getDocumentList().getDocument() : new ArrayList<MobileDocument>();

			final SignRequestDocument[] docs = new SignRequestDocument[docList.size()];

			try {
				for (int j = 0; j < docs.length; j++) {
					final MobileDocument doc = docList.get(j);

					docs[j] = new SignRequestDocument(
							doc.getIdentifier(),
							doc.getName(),
							doc.getSize().getValue(),
							doc.getMime(),
							doc.getOperationType(),
							doc.getSignatureType().getValue().value(),
							doc.getSignAlgorithm().getValue(),
							prepareSignatureParamenters(doc.getSignatureParameters()));
				}
			} catch (final Exception e) {
				final String id = request.getIdentifier() != null ?
						request.getIdentifier().getValue() : "null";  //$NON-NLS-1$
				LOGGER.warning("Se ha encontrado un error al analizar los datos de los documentos de la peticion con ID '" + id + "' y no se mostrara: " + e.toString()); //$NON-NLS-1$ //$NON-NLS-2$
				continue;
			}

			signRequests.add(new SignRequest(
					request.getRequestTagId(),
					request.getSubject().getValue(),
					request.getSenders().getStr().get(0),
					request.getView(),
					dateFormater.format(request.getFentry().getValue().toGregorianCalendar().getTime()),
					request.getFexpiration() != null ?
							dateFormater.format(request.getFexpiration().getValue().toGregorianCalendar().getTime()) :
							null,
					request.getImportanceLevel().getValue(),
					request.getWorkflow().getValue().booleanValue(),
					request.getForward().getValue().booleanValue(),
					request.getRequestType(),
					docs
					));
		}

		return new PartialSignRequestsList(
				signRequests.toArray(new SignRequest[signRequests.size()]),
				mobileRequestsList.getSize().intValue());
	}

	private static String prepareSignatureParamenters(final JAXBElement<String> parameters) {
		if (parameters == null) {
			return null;
		}
		return parameters.getValue();
	}

	private String processRejects(final HttpSession session, final byte[] xml) throws SAXException, IOException {
		final Document doc = this.documentBuilder.parse(new ByteArrayInputStream(xml));
		final RejectRequest request = RejectsRequestParser.parse(doc);

		final String dni = (String) session.getAttribute(SessionParams.DNI);
		final RequestResult[] requestResults = doReject(dni, request);

		return XmlResponsesFactory.createRejectsResponse(requestResults);
	}

	/**
	 * Rechaza el listado de solicitudes indicado en la petici&oacute;n de rechazo.
	 * @param dni DNI del usuario.
	 * @param rejectRequest Petici&oacute;n de rechazo.
	 * @return Resultado del rechazo de cada solicitud.
	 */
	private static RequestResult[] doReject(final String dni, final RejectRequest rejectRequest) {

		LOGGER.info("Se solicita el rechazo de peticiones: " + Integer.toString(rejectRequest.size())); //$NON-NLS-1$

		final MobileService service = new MobileService_Service(ConfigManager.getSignfolderUrl()).getMobileServicePort();

		final List<Boolean> rejectionsResults = new ArrayList<>();
		for (final String id : rejectRequest) {
			// Si devuelve cualquier texto es que la operacion ha terminado correctamente. Por defecto,
			// devuelve el mismo identificador de la peticion, aunque no es obligatorio
			// Si falla devuelve una excepcion.
			try {
				service.rejectRequest(dni.getBytes(), id, rejectRequest.getRejectReason());
				rejectionsResults.add(Boolean.TRUE);
			} catch (final Exception e) {
				LOGGER.log(Level.WARNING, "Error en el rechazo de la peticion " + id + ": " + e, e); //$NON-NLS-1$ //$NON-NLS-2$
				rejectionsResults.add(Boolean.FALSE);
			}
		}

		final RequestResult[] result = new RequestResult[rejectRequest.size()];
		for (int i = 0; i < rejectRequest.size(); i++) {
			result[i] = new RequestResult(rejectRequest.get(i), rejectionsResults.get(i).booleanValue());
		}

		LOGGER.info("Se devuelve el resultado del rechazo de peticiones: " + result.length); //$NON-NLS-1$

		return result;
	}

	private String processRequestDetail(final HttpSession session, final byte[] xml) throws SAXException, IOException, MobileException {
		final Document doc = this.documentBuilder.parse(new ByteArrayInputStream(xml));
		final DetailRequest detRequest = DetailRequestParser.parse(doc);

		LOGGER.info("Solicitamos el detalle de una peticion al Portafirmas"); //$NON-NLS-1$

		final String dni = (String) session.getAttribute(SessionParams.DNI);
		final Detail requestDetails = getRequestDetail(dni, detRequest);

		LOGGER.info("Hemos obtenido el detalle de la peticion del Portafirmas"); //$NON-NLS-1$

		return XmlResponsesFactory.createRequestDetailResponse(requestDetails);
	}

	/**
	 * Obtiene el detalle de un solicitud de firma a partir de una petici&oacute;n de detalle.
	 * @param dni DNI del usuario.
	 * @param request Petici&oacute;n que debe realizarse.
	 * @return Detalle de la solicitud.
	 * @throws MobileException Cuando ocurre un error al contactar con el Portafirmas.
	 */
	private static Detail getRequestDetail(final String dni, final DetailRequest request) throws MobileException {

		if (DEBUG) {
			disabledSslSecurity();
		}

		final MobileService_Service mobileService = new MobileService_Service(ConfigManager.getSignfolderUrl());
		final MobileService service = mobileService.getMobileServicePort();

		// Solicitud de lista de peticiones
		final MobileRequest mobileRequest = service.queryRequest(dni.getBytes(), request.getRequestId());

		// Listado de documentos de la peticion
		final List<MobileDocument> mobileDocs = mobileRequest.getDocumentList().getDocument();
		final SignRequestDocument[] docs = new SignRequestDocument[mobileDocs.size()];
		for (int i = 0; i < mobileDocs.size(); i++) {
			final MobileDocument doc = mobileDocs.get(i);
			docs[i] = new SignRequestDocument(
					doc.getIdentifier(),
					doc.getName(),
					doc.getSize().getValue(),
					doc.getMime(),
					doc.getOperationType(),
					doc.getSignatureType().getValue().value(),
					doc.getSignAlgorithm().getValue(),
					prepareSignatureParamenters(doc.getSignatureParameters()));
		}

		// Listado de adjuntos de la peticion
		final List<MobileDocument> mobileAttached = mobileRequest.getAttachList().getValue().getDocument();
		final SignRequestDocument[] attached = new SignRequestDocument[mobileAttached.size()];
		for (int i = 0; i < mobileAttached.size(); i++) {
			final MobileDocument att = mobileAttached.get(i);
			attached[i] = new SignRequestDocument(
					att.getIdentifier(),
					att.getName(),
					att.getSize().getValue(),
					att.getMime());
		}

		// Listado de remitentes de la peticion
		final List<MobileSignLine> mobileSignLines = mobileRequest.getSignLineList().getValue().getMobileSignLine();
		final SignLine[] signLines = new SignLine[mobileSignLines.size()];
		for (int i = 0; i < signLines.length; i++) {
			final List<String> lines = new ArrayList<>();
			for (final String line : mobileSignLines.get(i).getMobileSignerList().getValue().getStr()) {
				lines.add(line);
			}
			signLines[i] = new SignLine(lines.toArray(new String[lines.size()]));
			if (mobileSignLines.get(i).getType() != null) {
				signLines[i].setType(SignLineType.valueOf(mobileSignLines.get(i).getType().getValue()));
			}
		}

		final SimpleDateFormat df = new SimpleDateFormat(DATE_TIME_FORMAT);

		// Creamos el objeto de detalle
		final Detail detail = new Detail(mobileRequest.getRequestTagId());
		detail.setApp(mobileRequest.getApplication() != null ? mobileRequest.getApplication().getValue() : ""); //$NON-NLS-1$
		detail.setDate(mobileRequest.getFentry() != null ? df.format(mobileRequest.getFentry().getValue().toGregorianCalendar().getTime()) : ""); //$NON-NLS-1$
		detail.setExpDate(mobileRequest.getFexpiration() != null ? df.format(mobileRequest.getFexpiration().getValue().toGregorianCalendar().getTime()) : ""); //$NON-NLS-1$
		detail.setSubject(mobileRequest.getSubject().getValue());
		detail.setText(mobileRequest.getText() != null ? mobileRequest.getText().getValue() : ""); //$NON-NLS-1$
		detail.setWorkflow(mobileRequest.getWorkflow().getValue().booleanValue());
		detail.setForward(mobileRequest.getForward().getValue().booleanValue());
		detail.setPriority(mobileRequest.getImportanceLevel().getValue());
		detail.setType(mobileRequest.getRequestType());
		detail.setRef(mobileRequest.getRef() != null ? mobileRequest.getRef().getValue() : ""); //$NON-NLS-1$
		detail.setRejectReason(mobileRequest.getRejectedText() != null ? mobileRequest.getRejectedText().getValue(): null);
		detail.setSignLinesFlow(mobileRequest.isCascadeSign() ? Detail.SIGN_LINES_FLOW_CASCADE : Detail.SIGN_LINES_FLOW_PARALLEL);
		detail.setSenders(mobileRequest.getSenders().getStr().toArray(new String[mobileRequest.getSenders().getStr().size()]));
		detail.setDocs(docs);
		detail.setAttached(attached);
		detail.setSignLines(signLines);

		return detail;
	}

	private InputStream processDocumentPreview(final HttpSession session, final byte[] xml) throws SAXException, IOException, MobileException {
		final Document doc = this.documentBuilder.parse(new ByteArrayInputStream(xml));
		final PreviewRequest request = PreviewRequestParser.parse(doc);

		LOGGER.info("Solicitamos la previsualizacion de un documento al Portafirmas"); //$NON-NLS-1$

		final String dni = (String) session.getAttribute(SessionParams.DNI);
		final DocumentData documentData = previewDocument(dni, request);

		LOGGER.info("Hemos obtenido la previsualizacion de un documento del Portafirmas"); //$NON-NLS-1$

		return documentData.getDataIs();
	}

	private InputStream processSignPreview(final HttpSession session, final byte[] xml) throws SAXException, IOException, MobileException {
		final Document doc = this.documentBuilder.parse(new ByteArrayInputStream(xml));
		final PreviewRequest request = PreviewRequestParser.parse(doc);

		LOGGER.info("Solicitamos la previsualizacion de una firma al Portafirmas"); //$NON-NLS-1$

		final String dni = (String) session.getAttribute(SessionParams.DNI);
		final DocumentData documentData = previewSign(dni, request);

		LOGGER.info("Hemos obtenido la previsualizacion de una firma del Portafirmas"); //$NON-NLS-1$

		return documentData.getDataIs();
	}

	private InputStream processSignReportPreview(final HttpSession session, final byte[] xml) throws SAXException, IOException, MobileException {
		final Document doc = this.documentBuilder.parse(new ByteArrayInputStream(xml));
		final PreviewRequest request = PreviewRequestParser.parse(doc);

		LOGGER.info("Solicitamos la previsualizacion de un informe de firma al Portafirmas"); //$NON-NLS-1$

		final String dni = (String) session.getAttribute(SessionParams.DNI);
		final DocumentData documentData = previewSignReport(dni, request);

		LOGGER.info("Hemos obtenido la previsualizacion de un informe de firma del Portafirmas"); //$NON-NLS-1$

		return documentData.getDataIs();
	}

	/**
	 * Recupera los datos para la previsualizaci&oacute;n de un documento a partir del identificador
	 * del documento.
	 * @param dni DNI del usuario.
	 * @param request Petici&oacute;n de visualizaci&oacute;n de un documento.
	 * @return Datos necesarios para la previsualizaci&oacute;n.
	 * @throws MobileException Cuando ocurre un error al contactar con el Portafirmas.
	 * @throws IOException Cuando no ha sido posible leer el documento.
	 */
	private static DocumentData previewDocument(final String dni, final PreviewRequest request) throws MobileException, IOException {
		return buildDocumentData(new MobileService_Service(ConfigManager.getSignfolderUrl()).getMobileServicePort()
				.documentPreview(dni.getBytes(), request.getDocId()));
	}

	/**
	 * Recupera los datos para la descarga de una firma a partir del hash del documento firmado.
	 * @param dni DNI del usuario.
	 * @param request Petici&oacute;n de visualizaci&oacute;n de un documento.
	 * @return Datos necesarios para la previsualizaci&oacute;n.
	 * @throws MobileException Cuando ocurre un error al contactar con el Portafirmas.
	 * @throws IOException Cuando no ha sido posible leer el documento.
	 */
	private static DocumentData previewSign(final String dni, final PreviewRequest request) throws MobileException, IOException {
		return buildDocumentData(new MobileService_Service(ConfigManager.getSignfolderUrl()).getMobileServicePort()
				.signPreview(dni.getBytes(), request.getDocId()));
	}

	/**
	 * Recupera los datos para la visualizaci&oacute;n de un informe de firma a partir del hash del
	 * documento firmado.
	 * @param dni DNI del usuario.
	 * @param request Petici&oacute;n de visualizaci&oacute;n de un documento.
	 * @return Datos necesarios para la previsualizaci&oacute;n.
	 * @throws MobileException Cuando ocurre un error al contactar con el Portafirmas.
	 * @throws IOException Cuando no ha sido posible leer el documento.
	 */
	private static DocumentData previewSignReport(final String dni, final PreviewRequest request) throws MobileException, IOException {
	return buildDocumentData(new MobileService_Service(ConfigManager.getSignfolderUrl()).getMobileServicePort()
				.reportPreview(dni.getBytes(), request.getDocId()));
	}

	/**
	 * Construye un objeto documento para previsualizaci&oacute;n.
	 * @param document Datos del documento.
	 * @return Contenido y metadatos del documento.
	 * @throws IOException Cuando ocurre un error en la lectura de los datos.
	 */
	private static DocumentData buildDocumentData(final MobileDocument document) throws IOException {

		final InputStream contentIs;
		final Object content = document.getData().getValue().getContent();
		if (content instanceof InputStream) {
			contentIs = (InputStream) content;
		}
		else if (content instanceof String) {
			contentIs = new ByteArrayInputStream(Base64.decode((String) content));
		}
		else {
			throw new IOException("No se puede manejar el tipo de objeto devuelto por el servicio de previsualizacion de documentos: " + content); //$NON-NLS-1$
		}

		return new DocumentData(
				document.getIdentifier(),
				document.getName(),
				document.getMime(),
				contentIs);
	}

	private String processConfigueApp(final HttpSession session, final byte[] xml) throws SAXException, IOException, MobileException {

		final Document doc = this.documentBuilder.parse(new ByteArrayInputStream(xml));
		final ConfigurationRequest request = ConfigurationRequestParser.parse(doc);

		LOGGER.info("Solicitamos la configuracion al Portafirmas"); //$NON-NLS-1$

		final String dni = (String) session.getAttribute(SessionParams.DNI);
		final AppConfiguration appConfig = loadConfiguration(dni, request);

		LOGGER.info("Hemos obtenido la configuracion del Portafirmas"); //$NON-NLS-1$

		return XmlResponsesFactory.createConfigurationResponse(appConfig);
	}

	/**
	 * Recupera los datos de confguracion de la aplicaci&oacute;n. Hasta el momento:
	 * <ul>
	 * <li>Listado de aplicaciones.</li>
	 * </ul>
	 * @param dni DNI del usuario.
	 * @param request Datos gen&eacute;ricos necesarios para la petici&oacute;n.
	 * @return Configuraci&oacute;n de la aplicaci&oacute;n.
	 * @throws MobileException Cuando ocurre un error al contactar con el Portafirmas.
	 * @throws IOException Cuando no ha sido posible leer el documento.
	 */
	private static AppConfiguration loadConfiguration(final String dni, final ConfigurationRequest request) throws MobileException, IOException {

		if (DEBUG) {
			disabledSslSecurity();
		}

		final MobileService service = new MobileService_Service(ConfigManager.getSignfolderUrl()).getMobileServicePort();
		final MobileApplicationList appList = service.queryApplicationsMobile(dni.getBytes());

		final List<String> appIds = new ArrayList<>();
		final List<String> appNames = new ArrayList<>();
		for (final MobileApplication app : appList.getApplicationList()) {
			appIds.add(app.getId());
			appNames.add(app.getName() != null ? app.getName() : app.getId());
		}

		return new AppConfiguration(appIds, appNames);
	}


	private String processApproveRequest(final HttpSession session, final byte[] xml) throws SAXException, IOException {

		final Document xmlDoc = this.documentBuilder.parse(new ByteArrayInputStream(xml));
		final ApproveRequestList appRequests = ApproveRequestParser.parse(xmlDoc);

		LOGGER.info("Solicitamos la aprobacion de peticiones al Portafirmas"); //$NON-NLS-1$

		final String dni = (String) session.getAttribute(SessionParams.DNI);
		final ApproveRequestList approvedList = approveRequests(dni, appRequests);

		LOGGER.info("Hemos obtenido la listad de peticiones aprobadas del Portafirmas"); //$NON-NLS-1$

		return XmlResponsesFactory.createApproveRequestsResponse(approvedList);
	}

	/**
	 * Aprueba el listado de solicitudes indicado en la petici&oacute;n de aprobaci$oacute;n.
	 * @param dni DNI del usuario.
	 * @param appRequest Petici&oacute;n de aprobaci&oacute;n.
	 * @return Resultado de la aprobaci&oacute;n de cada solicitud.
	 */
	private static ApproveRequestList approveRequests(final String dni, final ApproveRequestList appRequests) {
		final MobileService_Service mobileService = new MobileService_Service(ConfigManager.getSignfolderUrl());
		final MobileService service = mobileService.getMobileServicePort();

		for (final ApproveRequest appReq : appRequests) {
			try {
				service.approveRequest(dni.getBytes(), appReq.getRequestTagId());
			} catch (final MobileException e) {
				appReq.setOk(false);
			}
		}
		return appRequests;
	}

	/**
	 * Procesa las peticiones de firma con FIRe. Se realiza la prefirma de cada uno
	 * de los documentos de las peticiones indicadas, se envian a FIRe para que realice una
	 * firma PKCS#1 y se vuelve la URL a la que este redirigio.
	 * Si se produce alg&uacute;n error al procesar un documento de alguna de las peticiones, se establece como incorrecta
	 * la petici&oacute;n al completo.
	 * @param session Sesi&oacute;n establecida con el portafirmas m&oacute;vil.
	 * @param xml XML con los datos para el proceso de firma.
	 * @return XML con la URL de redirecci&oacute;n a FIRe.
	 * @throws SAXException Cuando ocurre alg&uacute;n error al procesar los XML.
	 * @throws IOException Cuando ocurre alg&uacute;n problema de comunicaci&oacute;n con el servidor.
	 * @throws CertificateException Cuando ocurre alg&uacute;n problema con el certificado de firma.
	 */
	private String processFireLoadData(final HttpSession session, final byte[] xml) throws SAXException, IOException, CertificateException {

		final String dni = (String) session.getAttribute(SessionParams.DNI);

		final Document xmlDoc = this.documentBuilder.parse(new ByteArrayInputStream(xml));
		final TriphaseRequestBean triRequests = SignRequestsParser.parse(xmlDoc, null);

		final MobileStringList requestList = new MobileStringList();
		final List<String> idRequestList = requestList.getStr();
		for (final TriphaseRequest request : triRequests) {
			idRequestList.add(request.getRef());
		}

		final FireLoadDataResult loadDataResult = fireLoadData(dni, requestList);

		if (loadDataResult.isStatusOk()) {
			session.setAttribute(SessionParams.FIRE_TRID, loadDataResult.getTransactionId());
			session.setAttribute(SessionParams.FIRE_REQUESTS, idRequestList.toArray(new String[0]));
			SessionCollector.updateSession(session);
		}

		return XmlResponsesFactory.createFireLoadDataResponse(loadDataResult);
	}


	/**
	 * Realiza la carga de peticiones a firmar en FIRe.
	 * @param dni DNI del usuario.
	 * @param requestsRefList Listado de referencias de las peticiones a firmar.
	 * @return
	 */
	private static FireLoadDataResult fireLoadData(final String dni, final MobileStringList requestsRefList) {


		final MobileService_Service mobileService = new MobileService_Service(ConfigManager.getSignfolderUrl());
		final MobileService service = mobileService.getMobileServicePort();

		MobileFireTrasactionResponse response;
		try {
			response = service.fireTransaction(dni.getBytes(), requestsRefList);
		} catch (final MobileException e) {
			LOGGER.log(Level.SEVERE, "Error durante el envio de documentos a Clave Firma", e); //$NON-NLS-1$
			return new FireLoadDataResult();
		}

		final FireLoadDataResult loadDataResult = new FireLoadDataResult();
		loadDataResult.setStatusOk(true);
		loadDataResult.setTransactionId(response.getTransactionId());
		loadDataResult.setUrlRedirect(response.getUrlRedirect());

		return loadDataResult;
	}

	/**
	 * Procesa las peticiones de firma con ClaveFirma. Se realiza la prefirma de cada uno
	 * de los documentos de las peticiones indicadas, se envian a FIRe para que realice una
	 * firma PKCS#1 y se vuelve la URL a la que este redirigio.
	 * Si se produce alg&uacute;n error al procesar un documento de alguna de las peticiones, se establece como incorrecta
	 * la petici&oacute;n al completo.
	 * @param request Petici&oacute;n HTTP recibida.
	 * @param xml XML con los datos para el proceso de firma.
	 * @return Resultado del proceso de firma.
	 * @throws SAXException Cuando ocurre alg&uacute;n error al procesar los XML.
	 * @throws IOException Cuando ocurre alg&uacute;n problema de comunicaci&oacute;n con el servidor.
	 * @throws CertificateException Cuando ocurre alg&uacute;n problema con el certificado de firma.
	 */
	private String processFireSign(final HttpSession session, final byte[] xml) throws SAXException, IOException, CertificateException {

		final Document xmlDoc = this.documentBuilder.parse(new ByteArrayInputStream(xml));

		// Comprobamos que el XML de peticion esta bien formado
		FireSignRequestParser.parse(xmlDoc);

		final String dni = (String) session.getAttribute(SessionParams.DNI);
		final String transactionId = (String) session.getAttribute(SessionParams.FIRE_TRID);
		final String[] requestsToSign = (String[]) session.getAttribute(SessionParams.FIRE_REQUESTS);

		session.removeAttribute(SessionParams.FIRE_TRID);
		session.removeAttribute(SessionParams.FIRE_REQUESTS);

		final FireSignResult response = fireSign(dni, transactionId, requestsToSign);

		return XmlResponsesFactory.createFireSignResponse(!response.isError(), response.getErrorType());
	}

	/**
	 * Envia a firmar las peticiones cargadas en FIRe.
	 * @param dni DNI del usuario.
	 * @param transactionId Identificador de la transacci&oacute;n de FIRe.
	 * @param requestRefs Listado de referencias de las peticiones enviadas a firmar.
	 * @return Resultado de la operaci&oacute;n de firma.
	 */
	private static FireSignResult fireSign(final String dni, final String transactionId, final String[] requestRefs) {

		final MobileStringList requestList = new MobileStringList();
		final List<String> idRequestList = requestList.getStr();
		for (final String ref : requestRefs) {
			idRequestList.add(ref);
		}

		final MobileService_Service mobileService = new MobileService_Service(ConfigManager.getSignfolderUrl());
		final MobileService service = mobileService.getMobileServicePort();

		MobileFireRequestList response;
		try {
			response = service.signFireCloud(dni.getBytes(), requestList, transactionId);
		} catch (final MobileException e) {
			LOGGER.log(Level.SEVERE, "Error durante el envio de documentos a FIRe", e); //$NON-NLS-1$
			return new FireSignResult(FireSignResult.ERROR_TYPE_COMMUNICATION);
		}

		final FireSignResult result = new FireSignResult(transactionId);

		final List<MobileFireRequest> fireRequestResults = response.getMobileFireRequest();
		for (int i = 0; !result.isError() && i < fireRequestResults.size(); i++) {
			final MobileFireRequest fireRequest = fireRequestResults.get(i);
			if (fireRequest.getErrorPeticion() != null) {
				result.setErrorType(FireSignResult.ERROR_TYPE_REQUEST);
			}
			else {
				final List<MobileFireDocument> documentResults = fireRequest.getDocumentos().getMobileFireDocumentList();
				for (int j = 0; !result.isError() && j < documentResults.size(); j++) {
					final MobileFireDocument docResult = documentResults.get(j);
					if (docResult.getError() != null) {
						result.setErrorType(FireSignResult.ERROR_TYPE_DOCUMENT);
					}
				}
			}
		}
		return result;
	}

	/**
	 * Genera las prefirmas.
	 * @param triRequests Listado de datos trif&aacute;sicos que prefirmar.
	 */
	private static void preSign(final TriphaseRequestBean triRequests) {

		if (DEBUG) {
			disabledSslSecurity();
		}

		final MobileService_Service mobileService = new MobileService_Service(ConfigManager.getSignfolderUrl());
		final MobileService service = mobileService.getMobileServicePort();

		LOGGER.info("Procesamos las peticiones que se van a prefirmar"); //$NON-NLS-1$

		// Prefirmamos cada uno de los documentos de cada una de las peticiones. Si falla la prefirma de
		// un documento, se da por erronea la prefirma de toda la peticion
		for (final TriphaseRequest singleRequest : triRequests) {
			try {
				final MobileDocumentList downloadedDocs = service.getDocumentsToSign(triRequests.getCertificate().getEncoded(), singleRequest.getRef());
				LOGGER.info("Recuperamos los documentos de la peticion"); //$NON-NLS-1$
				if (singleRequest.size() != downloadedDocs.getDocument().size()) {
					LOGGER.info("No se han recuperado tantos documentos para la peticion " + singleRequest.getRef() + "' como los indicados en la propia peticion"); //$NON-NLS-1$ //$NON-NLS-2$
					throw new Exception("No se han recuperado tantos documentos para la peticion '" + //$NON-NLS-1$
							singleRequest.getRef() + "' como los indicados en la propia peticion"); //$NON-NLS-1$
				}

				// Prefirmamos cada documento de la peticion
				for (final TriphaseSignDocumentRequest docRequest : singleRequest) {

					LOGGER.info(" == PREFIRMA == "); //$NON-NLS-1$

					// Buscamos para la prefirma el documento descargado que corresponde para la peticion
					// de firma del documento actual
					for (final MobileDocument downloadedDoc : downloadedDocs.getDocument()) {
						if (downloadedDoc.getIdentifier().equals(docRequest.getId())) {

							LOGGER.info(" Procesamos documento con el id: " + downloadedDoc.getIdentifier()); //$NON-NLS-1$

							docRequest.setCryptoOperation(downloadedDoc.getOperationType());

							// Del servicio remoto obtener los parametros de configuracion, tal como deben pasarse al cliente
							// Lo pasamos a base 64 URL_SAFE para que no afecten al envio de datos
							final String extraParams = downloadedDoc.getSignatureParameters() != null ? downloadedDoc.getSignatureParameters().getValue() : null;

							if (extraParams != null) {
								docRequest.setParams(Base64.encode(extraParams.getBytes(), true));
							}

							final DataHandler dataHandler = downloadedDoc.getData() != null ? downloadedDoc.getData().getValue() : null;
							if (dataHandler == null) {
								throw new IllegalArgumentException("No se han recuperado los datos del documento"); //$NON-NLS-1$
							}
							final Object content = dataHandler.getContent();
							if (content instanceof InputStream) {
								docRequest.setContent(Base64.encode(AOUtil.getDataFromInputStream((InputStream) content), true));
							}
							else if (content instanceof String) {
								docRequest.setContent(((String) content).replace('+', '-').replace('/', '_'));
							}
							else {
								throw new IOException("No se puede manejar el tipo de objeto devuelto por el servicio de prefirma de documentos: " + content); //$NON-NLS-1$
							}
							break;
						}
					}
					if (docRequest.getContent() == null) {
						throw new Exception("No se encontro correlacion entre los documentos declarados en la peticion y los documentos descargados"); //$NON-NLS-1$
					}

					LOGGER.info("Procedemos a realizar la prefirma del documento " + docRequest.getId()); //$NON-NLS-1$
					TriSigner.doPreSign(
							docRequest,
							triRequests.getCertificate(),
							ConfigManager.getTriphaseServiceUrl(),
							ConfigManager.getForcedExtraParams());
				}
			} catch (final Exception mex) {
				LOGGER.log(Level.SEVERE, "Error en la prefirma de la peticion " + //$NON-NLS-1$
						singleRequest.getRef() + ": " + mex, mex); //$NON-NLS-1$
				singleRequest.setStatusOk(false);
				singleRequest.setThrowable(mex);
			}
		}
	}

	/**
	 * Genera las postfirmas.
	 * @param triRequests Listado de datos trif&aacute;sicos con las prefirmas y firmas PKCS#1.
	 * @param service Servicio de conexion con el proxy.
	 */
	private static void postSign(final TriphaseRequestBean triRequests) {

		LOGGER.info("Procesamos la peticiones que se van a postfirmar"); //$NON-NLS-1$

		if (DEBUG) {
			disabledSslSecurity();
		}

		final MobileService_Service mobileService = new MobileService_Service(ConfigManager.getSignfolderUrl());
		final MobileService service = mobileService.getMobileServicePort();

		// Postfirmamos cada uno de los documentos de cada una de las peticiones. Si falla la
		// postfirma de un solo documento, se da por erronea la postfirma de toda la peticion
		for (final TriphaseRequest triRequest : triRequests) {

			LOGGER.info(" == POSTFIRMA == "); //$NON-NLS-1$

			// Sustituir. Algunos formatos de firma no requeriran que se vuelva a descargar el
			// documento. Solo los descargaremos si es necesario para al menos una de las firmas.

			// Tomamos nota de que firmas requieren el documento original
			final Set<String> requestNeedContent = new HashSet<>();
			for (final TriphaseSignDocumentRequest docRequest: triRequest) {

				final TriphaseData triData = docRequest.getPartialResult();
				if (triData.getSignsCount() > 0 &&
						(!triData.getSign(0).getDict().containsKey(CRYPTO_PARAM_NEED_DATA) ||
						Boolean.parseBoolean(triData.getSign(0).getDict().get(CRYPTO_PARAM_NEED_DATA)))) {
					LOGGER.info("Descargamos el documento '" + docRequest.getId() + "' para su uso en la postfirma"); //$NON-NLS-1$ //$NON-NLS-2$
					requestNeedContent.add(docRequest.getId());
				}
			}

			// Descargamos los documentos originales si los necesitamos
			MobileDocumentList downloadedDocs = null;
			if (!requestNeedContent.isEmpty()) {
				try {
					downloadedDocs = service.getDocumentsToSign(triRequests.getCertificate().getEncoded(), triRequest.getRef());
				} catch (final Exception ex) {
					LOGGER.warning("Ocurrio un error al descargar los documentos de la peticion " + triRequest.getRef() + ": " + ex);  //$NON-NLS-1$//$NON-NLS-2$
					triRequest.setStatusOk(false);
					continue;
				}
			}

			// Para cada documento, le asignamos su documento (si es necesario) y lo postfirmamos
			try {
				for (final TriphaseSignDocumentRequest docRequest : triRequest) {

					// Asignamos el documento a la peticion si es necesario
					if (downloadedDocs != null && requestNeedContent.contains(docRequest.getId())) {
						// Buscamos para la postfirma el documento descargado que corresponde para la peticion
						// de firma del documento actual
						for (final MobileDocument downloadedDoc : downloadedDocs.getDocument()) {
							if (downloadedDoc.getIdentifier().equals(docRequest.getId())) {
								final Object content = downloadedDoc.getData().getValue().getContent();
								if (content instanceof InputStream) {
									docRequest.setContent(Base64.encode(AOUtil.getDataFromInputStream((InputStream) content), true));
								}
								else {
									docRequest.setContent((String) content);
								}
								// Del servicio remoto obtener los parametros de configuracion, tal como deben pasarse al cliente
								// Lo pasamos a base 64 URL_SAFE para que no afecten al envio de datos
								final String extraParams = downloadedDoc.getSignatureParameters() != null ? downloadedDoc.getSignatureParameters().getValue() : null;
								if (extraParams != null) {
									docRequest.setParams(Base64.encode(extraParams.getBytes(), true));
								}
							}
						}
					}

					LOGGER.info("Procedemos a realizar la postfirma"); //$NON-NLS-1$
					TriSigner.doPostSign(
							docRequest,
							triRequests.getCertificate(),
							ConfigManager.getTriphaseServiceUrl(),
							ConfigManager.getForcedExtraParams());
				}
			} catch (final Exception ex) {
				LOGGER.log(Level.WARNING, "Ocurrio un error al postfirmar un documento: " + ex, ex);  //$NON-NLS-1$
				triRequest.setStatusOk(false);
				continue;
			}

			LOGGER.info("Registramos el resultado en el portafirmas"); //$NON-NLS-1$

			// Guardamos las firmas de todos los documentos de cada peticion
			try {
				service.saveSign(triRequests.getCertificate().getEncoded(),
						triRequest.getRef(), transformToWsParams(triRequest));
			} catch (final Exception ex) {
				LOGGER.warning(
						"Ocurrio un error al guardar la peticion de firma " + triRequest.getRef() + //$NON-NLS-1$
						": " + ex); //$NON-NLS-1$
				ex.printStackTrace();
				triRequest.setStatusOk(false);
			}
		}
	}

	/**
	 * Permite enviar respuestas al cliente de un servicio.
	 */
	private final class Responser {

		final HttpServletResponse response;

		/**
		 * Crea el Responser enlaz&aacute;ndolo con una petici&oacute;n concreta al servicio.
		 * @param response Manejador para el env&iacute;o de la respuesta.
		 * @throws IOException Cuando no se puede escribir una respuesta.
		 */
		public Responser(final HttpServletResponse response) throws IOException {
			this.response = response;
		}

		/**
		 * Imprime una respuesta en la salida del servicio y cierra el flujo.
		 * @param message Mensaje que imprimir como respuesta.
		 */
		public void print(final String message) {
			try (final OutputStream os = this.response.getOutputStream();) {
				os.write(message.getBytes(Charset.forName(DEFAULT_CHARSET)));
			}
			catch (final Exception e) {
				LOGGER.info("Error al devolver el resultado al cliente a traves del metodo print: " + e); //$NON-NLS-1$
			}
		}

		public void write(final InputStream message) {
			int n = -1;
			final byte[] buffer = new byte[1024];
			try (final OutputStream os = this.response.getOutputStream();) {
				while ((n = message.read(buffer)) > 0) {
					os.write(buffer, 0, n);
				}
			}
			catch (final Exception e) {
				LOGGER.info("Error al devolver el resultado al cliente a traves del metodo write: " + e); //$NON-NLS-1$
			}
		}
	}

}
