package es.gob.afirma.signfolder.server.proxy;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

import javax.activation.DataHandler;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.bind.JAXBElement;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.ws.Binding;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.soap.SOAPFaultException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import es.gob.afirma.core.RuntimeConfigNeededException;
import es.gob.afirma.core.misc.AOUtil;
import es.gob.afirma.core.misc.Base64;
import es.gob.afirma.core.signers.TriphaseData;
import es.gob.afirma.signfolder.client.EstadoNotifyPushResponse;
import es.gob.afirma.signfolder.client.MobileAccesoClave;
import es.gob.afirma.signfolder.client.MobileApplication;
import es.gob.afirma.signfolder.client.MobileApplicationList;
import es.gob.afirma.signfolder.client.MobileAutorizacion;
import es.gob.afirma.signfolder.client.MobileAutorizacionesList;
import es.gob.afirma.signfolder.client.MobileConfiguracionUsuario;
import es.gob.afirma.signfolder.client.MobileDocSignInfo;
import es.gob.afirma.signfolder.client.MobileDocSignInfoList;
import es.gob.afirma.signfolder.client.MobileDocument;
import es.gob.afirma.signfolder.client.MobileDocumentList;
import es.gob.afirma.signfolder.client.MobileException;
import es.gob.afirma.signfolder.client.MobileFiltroAnioList;
import es.gob.afirma.signfolder.client.MobileFiltroGenerico;
import es.gob.afirma.signfolder.client.MobileFiltroMesList;
import es.gob.afirma.signfolder.client.MobileFiltroTipoList;
import es.gob.afirma.signfolder.client.MobileFireDocument;
import es.gob.afirma.signfolder.client.MobileFireRequest;
import es.gob.afirma.signfolder.client.MobileFireRequestList;
import es.gob.afirma.signfolder.client.MobileFireTrasactionResponse;
import es.gob.afirma.signfolder.client.MobileRequest;
import es.gob.afirma.signfolder.client.MobileRequestFilter;
import es.gob.afirma.signfolder.client.MobileRequestFilterList;
import es.gob.afirma.signfolder.client.MobileRequestList;
import es.gob.afirma.signfolder.client.MobileRole;
import es.gob.afirma.signfolder.client.MobileRoleList;
import es.gob.afirma.signfolder.client.MobileSIMUser;
import es.gob.afirma.signfolder.client.MobileSIMUserStatus;
import es.gob.afirma.signfolder.client.MobileService;
import es.gob.afirma.signfolder.client.MobileService_Service;
import es.gob.afirma.signfolder.client.MobileSignLine;
import es.gob.afirma.signfolder.client.MobileStringList;
import es.gob.afirma.signfolder.client.MobileTipoGenerico;
import es.gob.afirma.signfolder.client.MobileUsuarioGenerico;
import es.gob.afirma.signfolder.client.MobileUsuariosList;
import es.gob.afirma.signfolder.client.MobileValidador;
import es.gob.afirma.signfolder.client.MobileValidadorList;
import es.gob.afirma.signfolder.client.ObjectFactory;
import es.gob.afirma.signfolder.client.UpdateNotifyPushResponse;
import es.gob.afirma.signfolder.server.proxy.SignLine.SignLineType;
import es.gob.afirma.signfolder.server.proxy.sessions.SessionCollector;
import es.gob.afirma.signfolder.soap.security.SecurityHandler;

/**
 * Servicio Web para firma trif&aacute;sica.
 *
 * @author Tom&aacute;s Garc&iacute;a-;er&aacute;s
 */
public final class ProxyService extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final String DEFAULT_CHARSET_NAME = "utf-8"; //$NON-NLS-1$
	private static Charset DEFAULT_CHARSET;

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
	private static final String OPERATION_GET_USER_CONFIG = "18"; //$NON-NLS-1$
	private static final String OPERATION_FIND_USER = "19"; //$NON-NLS-1$
	private static final String OPERATION_VERIFY = "20"; //$NON-NLS-1$
	private static final String OPERATION_GET_PUSH_STATUS = "22"; //$NON-NLS-1$
	private static final String OPERATION_UPDATE_PUSH = "23"; //$NON-NLS-1$
	private static final String OPERATION_LIST_AUTHORIZATIONS = "24"; //$NON-NLS-1$
	private static final String OPERATION_SAVE_AUTHORIZATION = "25"; //$NON-NLS-1$
	private static final String OPERATION_REVOCATE_AUTHORIZATION = "26"; //$NON-NLS-1$
	private static final String OPERATION_ACCEPT_AUTHORIZATION = "27"; //$NON-NLS-1$
	private static final String OPERATION_LIST_VALIDATORS = "28"; //$NON-NLS-1$
	private static final String OPERATION_SAVE_VALIDATOR = "29"; //$NON-NLS-1$
	private static final String OPERATION_REVOCATE_VALIDATOR = "30"; //$NON-NLS-1$


	private static final String[] OPERATIONS_CREATE_SESSION = new String[] { OPERATION_REQUEST_LOGIN,
			OPERATION_CLAVE_LOGIN };

	private static final String[] OPERATIONS_WITHOUT_LOGIN = new String[] { OPERATION_REQUEST_LOGIN,
			OPERATION_VALIDATE_LOGIN, OPERATION_CLAVE_LOGIN };

	private static final String CRYPTO_PARAM_NEED_DATA = "NEED_DATA"; //$NON-NLS-1$

	private static final String LOGIN_SIGNATURE_ALGORITHM = "SHA256withRSA"; //$NON-NLS-1$

	private static final String PAGE_CLAVE_LOADING = "clave-loading.jsp"; //$NON-NLS-1$

	private static final String SYSTEM_PROPERTY_DEBUG = "proxy.debug"; //$NON-NLS-1$

	private static final String KEY_FILTER_PERIOD = "mesFilter"; //$NON-NLS-1$
	private static final String VALUE_DEFAULT_PERIOD = "all"; //$NON-NLS-1$

	private static final String SIGNFOLDER_VALUE_TRUE = "S"; //$NON-NLS-1$
	private static final String SIGNFOLDER_VALUE_FALSE = "N"; //$NON-NLS-1$

	private static final String AUTH_ACTION_REVOCATION = "revocar"; //$NON-NLS-1$
	private static final String AUTH_ACTION_ACCEPT = "aceptar"; //$NON-NLS-1$

	private static final String VALID_ACTION_INSERT = "insertar"; //$NON-NLS-1$

	/** N&uacute;mero de peticiones a la cach&eacute; que se deben realizar entre una limpieza y otra. */
	private final int DEFAULT_CACHE_REQUEST_TO_CLEAN = 1000;

	static final Logger LOGGER = LoggerFactory.getLogger(ProxyService.class);

	private static boolean DEBUG;

	private static DocumentBuilderFactory SECURE_BUILDER_FACTORY;

	private static TrustManager[] DUMMY_TRUST_MANAGER = null;
	private static HostnameVerifier HOSTNAME_VERIFIER = null;

	private DocumentCache documentCache = null;

	private final MobileService_Service mobileService;


	/** N&uacute;mero de peticiones a la cach&eacute; que quedan hasta la siguiente limpieza. */
	private int numCacheRequestToClean = this.DEFAULT_CACHE_REQUEST_TO_CLEAN;


	static {

		// Configuramos el manejador de log, redirigiendo los logs de Java (JUL) a SLF4J
		try {
			java.util.logging.Logger.getLogger("es.gob.afirma").setLevel(Level.INFO); //$NON-NLS-1$
		}
		catch (final Exception e) {
			// No hacemos nada
			LOGGER.warn("No se ha podido redirigir los logs de Java a SLF4J", e); //$NON-NLS-1$
		}

		// Habilitamos el modo debug si corresponde
		try {
			DEBUG = Boolean.parseBoolean(System.getProperty(SYSTEM_PROPERTY_DEBUG));
			if (DEBUG) {
				java.util.logging.Logger.getLogger("es.gob.afirma").setLevel(Level.FINE); //$NON-NLS-1$

				// Desactivamos las comprobaciones de los certificados SSL servidor durante las conexiones de red
				disabledSslSecurity();

				// Establecemos las propiedades para imprimir en el log las peticiones y respuestas SOAP
				System.setProperty("com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump", "true"); //$NON-NLS-1$ //$NON-NLS-2$
				System.setProperty("com.sun.xml.internal.ws.transport.http.client.HttpTransportPipe.dump", "true"); //$NON-NLS-1$ //$NON-NLS-2$
				System.setProperty("com.sun.xml.ws.transport.http.HttpAdapter.dump", "true"); //$NON-NLS-1$ //$NON-NLS-2$
				System.setProperty("com.sun.xml.internal.ws.transport.http.HttpAdapter.dump", "true"); //$NON-NLS-1$ //$NON-NLS-2$
				System.setProperty("com.sun.xml.internal.ws.transport.http.HttpAdapter.dumpTreshold", "999999"); //$NON-NLS-1$ //$NON-NLS-2$
			}
		} catch (final Exception e) {
			LOGGER.warn("Error al habilitar todas las opciones de depuracion", e); //$NON-NLS-1$
		}

		// Juego de caracteres que se usara por defecto
		try {
			DEFAULT_CHARSET = Charset.forName(DEFAULT_CHARSET_NAME);
		}
		catch (final Exception e) {
			LOGGER.error("No se reconocio el juego de caracteres por defecto: ", e); //$NON-NLS-1$
		}

		// Creamos un DocumentBuilderFactory seguro con el que analizar los documentos XML
		SECURE_BUILDER_FACTORY = XmlUtils.getSecureDocumentBuilderFactory();
	}

	/** Construye un Servlet que sirve operaciones de firma trif&aacute;sica. */
	public ProxyService() {

		ConfigManager.checkInitialized();

		try {
			this.mobileService = new MobileService_Service(ConfigManager.getSignfolderUrl());
		} catch (final Exception e) {
			LOGGER.error("Error en la configuracion de la conexion con el Portafirmas web", e); //$NON-NLS-1$
			throw new IllegalStateException("Error en la configuracion de la conexion con el Portafirmas web"); //$NON-NLS-1$
		}

		// Activamos la cache si se configuro
		if (ConfigManager.isCacheEnabled()) {
			final String cacheSystemClassname = ConfigManager.getCacheSystemClass();
			try {
				final Class<?> docCacheClass = Class.forName(cacheSystemClassname);
				if (DocumentCache.class.isAssignableFrom(docCacheClass)) {
					this.documentCache = (DocumentCache) docCacheClass.newInstance();
				}
				else {
					LOGGER.error("La clase de cache configurada no implementa la clase es.gob.afirma.signfolder.server.proxy.DocumentCache"); //$NON-NLS-1$
				}
			}
			catch (final Exception e) {
				LOGGER.error("La clase de cache configurada no es valida", e); //$NON-NLS-1$
				this.documentCache = null;
			}
		}

		// Si se activo la cache, realizamos una limpieza preliminar
		if (this.documentCache != null) {
			new CacheCleanerThread(this.documentCache, ConfigManager.getCacheExpirationTime()).start();
		}
	}

	private static void disabledSslSecurity() {

		// Si ya esta establecido, no repetimos
		if (HOSTNAME_VERIFIER == null) {

			DUMMY_TRUST_MANAGER = new TrustManager[] { new X509TrustManager() {
				@Override
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				@Override
				public void checkClientTrusted(final X509Certificate[] certs, final String authType) {
					/* No hacemos nada */ }

				@Override
				public void checkServerTrusted(final X509Certificate[] certs, final String authType) {
					/* No hacemos nada */ }

			} };

			HOSTNAME_VERIFIER = new HostnameVerifier() {
				@Override
				public boolean verify(final String hostname, final SSLSession session) {
					return true;
				}
			};
		}

		if (HOSTNAME_VERIFIER != HttpsURLConnection.getDefaultHostnameVerifier()) {
			try {
				final SSLContext sc = SSLContext.getInstance("SSL"); //$NON-NLS-1$
				sc.init(null, DUMMY_TRUST_MANAGER, new java.security.SecureRandom());

				HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
				HttpsURLConnection.setDefaultHostnameVerifier(HOSTNAME_VERIFIER);
			} catch (final Exception e) {
				LOGGER.warn("No se pudo deshabilitar la verificacion del SSL", e); //$NON-NLS-1$
			}
		}
	}

	/**
	 * Realiza una operaci&oacute;n de firma en tres fases. Acepta los
	 * siguientes c&oacute;digos de operaci&oacute;n en el par&aacute;metro
	 * <code>op</code>:
	 * <dl>
	 * <dt>1</dt>
	 * <dd>Firma</dd>
	 * <dt>2</dt>
	 * <dd>Petici&oacute;n de solicitudes</dd>
	 * <dt>3</dt>
	 * <dd>Rechazo de solicitudes</dd>
	 * <dt>4</dt>
	 * <dd>Detalle</dd>
	 * <dt>5</dt>
	 * <dd>Previsualizaci&oacute;n</dd>
	 * </dl>
	 *
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void service(final HttpServletRequest request, final HttpServletResponse response) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Peticion al proxy Portafirmas"); //$NON-NLS-1$
			final Cookie[] cookies = request.getCookies();
			LOGGER.debug("Cookies de la peticion: " + (cookies != null ? cookies.length : 0)); //$NON-NLS-1$
			if (cookies != null) {
				for (final Cookie tempCookie : cookies) {
					LOGGER.debug(" - " + tempCookie.getName() + "=" + tempCookie.getValue()); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
		}

		final Responser responser = new Responser(response);

		// Obtenemos los parametros de la peticion
		Map<String, String> parameters;
		try {
			parameters = getParameters(request);
		}
		catch (final Exception | Error e) {
			LOGGER.error("No se pudieron leer los parametros de la peticion: " + e); //$NON-NLS-1$
			try {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			} catch (final IOException e1) {
				LOGGER.error("No se pudo enviar un error al cliente", e); //$NON-NLS-1$
			}
			return;
		}

		final String operation = parameters.get(PARAMETER_NAME_OPERATION);
		if (operation == null) {
			LOGGER.warn("No se han proporcionado identificador de operacion"); //$NON-NLS-1$
			responser.print(ErrorManager.genError(ErrorManager.ERROR_MISSING_OPERATION_NAME, null));
			return;
		}

		final String data = parameters.get(PARAMETER_NAME_DATA);
		if (data == null) {
			LOGGER.warn("No se han proporcionado los datos"); //$NON-NLS-1$
			responser.print(ErrorManager.genError(ErrorManager.ERROR_MISSING_DATA, null));
			return;
		}

		byte[] xml;
		try {
			xml = Base64.decode(data, true);
		} catch (final Exception ex) {
			LOGGER.warn("Los datos de entrada no estan correctamente codificados: " + ex); //$NON-NLS-1$
			return;
		}

//		try {
//			xml = GzipCompressorImpl.gunzip(xml);
//		} catch (final IOException e) {
//			LOGGER.debug("Los datos de entrada no estaban comprimidos: " + e); //$NON-NLS-1$
//		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("XML de la peticion:\n{}", new String(xml, DEFAULT_CHARSET)); //$NON-NLS-1$
		}

		Object ret;

		try {
			String ssid = null;

			// Salvo que sea una operacion de creacion de sesion, permitimos que
			// se nos pase el ID de sesion compartida
			if (!isCreatingLoginOperation(operation)) {
				ssid = parameters.get(PARAMETER_NAME_SHARED_SESSION_ID);
			}

			// Obtenemos la sesion que corresponda y, si requiere estar validada
			// para realizar
			// la operacion solicitada y no lo esta, se establecera un error de
			// autenticacion
			final HttpSession session = SessionCollector.getSession(request, ssid);
			if (session == null) {
				LOGGER.debug("No se encontro sesion asociada a la peticion y ni la sesion compartida: " + ssid); //$NON-NLS-1$
			}

			if (isLoginRequieredOperation(operation) && !isValidatedSession(session)) {
				LOGGER.warn("Se ha solicitado la siguiente operacion del proxy sin estar autenticado: " + operation); //$NON-NLS-1$
				ret = ErrorManager.genError(ErrorManager.ERROR_AUTHENTICATING_REQUEST);
				if (session != null) {
					SessionCollector.removeSession(session);
				}
			} else {
				ret = processRequest(operation, xml, request, session);
			}

		} catch (final Exception e) {
			LOGGER.warn("El Portafirmas devolvio un error", e); //$NON-NLS-1$
			if (e instanceof SAXException) {
				ret = ErrorManager.genError(ErrorManager.ERROR_BAD_XML);
			} else if (e instanceof CertificateException) {
				ret = ErrorManager.genError(ErrorManager.ERROR_BAD_CERTIFICATE);
			} else if (e instanceof MobileException) {
				ret = ErrorManager.genError(ErrorManager.ERROR_COMMUNICATING_PORTAFIRMAS);
			} else if (e instanceof IOException) {
				ret = ErrorManager.genError(ErrorManager.ERROR_COMMUNICATING_PORTAFIRMAS);
			} else if (e instanceof SOAPFaultException) {
				ret = ErrorManager.genError(ErrorManager.ERROR_AUTHENTICATING_REQUEST);
			} else if (e instanceof WebServiceException) {
				ret = ErrorManager.genError(ErrorManager.ERROR_COMMUNICATING_SERVICE);
			} else {
				ret = ErrorManager.genError(ErrorManager.ERROR_UNKNOWN_ERROR);
			}
		}

		if (ret instanceof InputStream) {
			LOGGER.debug("La respuesta es un flujo de datos de salida"); //$NON-NLS-1$
			responser.write((InputStream) ret);
			try {
				((InputStream) ret).close();
			} catch (final IOException e) {
				LOGGER.warn("No se pudo cerrar el flujo de datos: " + e); //$NON-NLS-1$
			}
		} else {
			LOGGER.debug("XML de respuesta:\n{}", ret); //$NON-NLS-1$
			responser.print((String) ret);
		}
		LOGGER.debug("Fin peticion ProxyService"); //$NON-NLS-1$
	}


	public static Document parse(final byte[] data) throws SAXException, IOException {
		DocumentBuilder documentBuilder;
		try {
			synchronized (SECURE_BUILDER_FACTORY) {
				documentBuilder = SECURE_BUILDER_FACTORY.newDocumentBuilder();
			}
		} catch (final Exception e) {
			throw new IllegalStateException("Error interno en la configuracion del parser XML", e); //$NON-NLS-1$
		}
		return documentBuilder.parse(new ByteArrayInputStream(data));
	}

	/**
	 * Carga los par&aacute;metros de entrada del servicio.
	 * @param request Petici&oacute;n al servicio.
	 * @return Mapa con los par&aacute;metros de entrada.
	 * @throws UnsupportedEncodingException Cuando la codificacion por defecto no este soportada.
	 * @throws IOException Cuando no se puedan leer los parametros de entrada.
	 */
	private static Map<String, String> getParameters(final HttpServletRequest request)
			throws UnsupportedEncodingException, IOException {

		final Map<String, String> parameters = new HashMap<>();

		// Cargamos los parametros de la URL
		final Map<String, String[]> headerParameters = request.getParameterMap();
		for (final String param : headerParameters.keySet().toArray(new String[0])) {
			parameters.put(param, headerParameters.get(param)[0]);
		}

		// Cargamos los parametros del cuerpo del mensaje
		final String[] params = new String(AOUtil.getDataFromInputStream(request.getInputStream()), DEFAULT_CHARSET)
				.split("&"); //$NON-NLS-1$

		for (final String param : params) {
			if (param.indexOf('=') != -1) {
				try {
					parameters.put(
							param.substring(0, param.indexOf('=')),
							URLDecoder.decode(param.substring(param.indexOf('=') + 1), DEFAULT_CHARSET_NAME));
				}
				catch (final Exception e) {
					LOGGER.warn("Error al decodificar un parametro de la peticion: " + e); //$NON-NLS-1$
				}
			}
		}
		return parameters;
	}

	/**
	 * Indica si una sesi&oacute;n con certificado local se ha validado.
	 *
	 * @param session
	 *            Sesi&oacute;n que se desea comprobar.
	 * @return {@code true} si la sesi&oacute;n est&aacute; validada,
	 *         {@code false} si la sesi&oacute;n es nula, si se inicio con
	 *         certificado en la nube o si a&uacute;n no se ha validado.
	 */
	private static boolean isValidatedSession(final HttpSession session) {
		return session != null && Boolean.parseBoolean((String) session.getAttribute(SessionParams.VALID_SESSION));
	}

	private Object processRequest(final String operation, final byte[] xml, final HttpServletRequest request,
			final HttpSession session) throws CertificateException, SAXException, IOException, MobileException {

		Object ret;
		if (OPERATION_REQUEST_LOGIN.equals(operation)) {
			LOGGER.info("Solicitud de login"); //$NON-NLS-1$
			ret = processRequestLogin(request, session, xml);
		} else if (OPERATION_VALIDATE_LOGIN.equals(operation)) {
			LOGGER.info("Validacion de login"); //$NON-NLS-1$
			ret = processValidateLogin(session, xml);
		} else if (OPERATION_CLAVE_LOGIN.equals(operation)) {
			LOGGER.info("Solicitud de login con Cl@ve"); //$NON-NLS-1$
			ret = processRequestClaveLogin(request, session, xml);
		} else if (OPERATION_LOGOUT.equals(operation)) {
			LOGGER.info("Solicitud de logout"); //$NON-NLS-1$
			ret = processLogout(session, xml);
		} else if (OPERATION_REGISTER_NOTIFICATION_SYSTEM.equals(operation)) {
			LOGGER.info("Solicitud de registro en el sistema de notificaciones"); //$NON-NLS-1$
			ret = processNotificationRegistry(session, xml);
		} else if (OPERATION_PRESIGN.equals(operation)) {
			LOGGER.info("Solicitud de prefirma"); //$NON-NLS-1$
			ret = processPreSigns(session, xml);
		} else if (OPERATION_POSTSIGN.equals(operation)) {
			LOGGER.info("Solicitud de postfirma"); //$NON-NLS-1$
			ret = processPostSigns(session, xml);
		} else if (OPERATION_REQUEST.equals(operation)) {
			LOGGER.info("Solicitud del listado de peticiones"); //$NON-NLS-1$
			ret = processRequestsList(session, xml);
		} else if (OPERATION_REJECT.equals(operation)) {
			LOGGER.info("Solicitud de rechazo peticiones"); //$NON-NLS-1$
			ret = processRejects(session, xml);
		} else if (OPERATION_DETAIL.equals(operation)) {
			LOGGER.info("Solicitud de detalle de una peticion"); //$NON-NLS-1$
			ret = processRequestDetail(session, xml);
		} else if (OPERATION_DOCUMENT_PREVIEW.equals(operation)) {
			LOGGER.info("Solicitud de previsualizacion de un documento"); //$NON-NLS-1$
			ret = processDocumentPreview(session, xml);
		} else if (OPERATION_CONFIGURING.equals(operation)) {
			LOGGER.info("Solicitud de la configuracion"); //$NON-NLS-1$
			ret = processConfigueApp(session, xml);
		} else if (OPERATION_APPROVE.equals(operation)) {
			LOGGER.info("Solicitud de aprobacion de una peticion"); //$NON-NLS-1$
			ret = processApproveRequest(session, xml);
		} else if (OPERATION_SIGN_PREVIEW.equals(operation)) {
			LOGGER.info("Solicitud de previsualizacion de una firma"); //$NON-NLS-1$
			ret = processSignPreview(session, xml);
		} else if (OPERATION_REPORT_PREVIEW.equals(operation)) {
			LOGGER.info("Solicitud de previsualizacion de un informe de firma"); //$NON-NLS-1$
			ret = processSignReportPreview(session, xml);
		} else if (OPERATION_FIRE_LOAD_DATA.equals(operation)) {
			LOGGER.info("Solicitud de carga de datos con FIRe"); //$NON-NLS-1$
			ret = processFireLoadData(session, xml);
		} else if (OPERATION_FIRE_SIGN.equals(operation)) {
			LOGGER.info("Solicitud de firma con FIRe"); //$NON-NLS-1$
			ret = processFireSign(session, xml);
		} else if (OPERATION_GET_USER_CONFIG.equals(operation)) {
			LOGGER.info("Solicitud de recuperacion de la configuracion de usuarios."); //$NON-NLS-1$
			ret = processGetUserConfig(session, xml);
		} else if (OPERATION_FIND_USER.equals(operation)) {
			LOGGER.info("Solicitud de recuperacion de usuarios."); //$NON-NLS-1$
			ret = processFindUser(session, xml);
		} else if (OPERATION_VERIFY.equals(operation)) {
			LOGGER.info("Solicitud de validacion de peticion."); //$NON-NLS-1$
			ret = processVerifyPetitions(session, xml);
		} else if (OPERATION_LIST_AUTHORIZATIONS.equals(operation)) {
			LOGGER.info("Solicitud de listado de autorizaciones de usuario."); //$NON-NLS-1$
			ret = processListAuthorizations(session, xml);
		} else if (OPERATION_SAVE_AUTHORIZATION.equals(operation)) {
			LOGGER.info("Solicitud de guardado de autorizacion de usuario."); //$NON-NLS-1$
			ret = processSaveAuthorization(session, xml);
		} else if (OPERATION_REVOCATE_AUTHORIZATION.equals(operation)) {
			LOGGER.info("Solicitud de revocacion de una autorizacion de usuario."); //$NON-NLS-1$
			ret = processRevocateAuthorization(session, xml);
		} else if (OPERATION_ACCEPT_AUTHORIZATION.equals(operation)) {
			LOGGER.info("Solicitud de aceptacion de una autorizacion de usuario."); //$NON-NLS-1$
			ret = processAcceptAuthorization(session, xml);
		} else if (OPERATION_LIST_VALIDATORS.equals(operation)) {
			LOGGER.info("Solicitud de listado de autorizaciones de usuario."); //$NON-NLS-1$
			ret = processListValidators(session, xml);
		} else if (OPERATION_SAVE_VALIDATOR.equals(operation)) {
			LOGGER.info("Solicitud de guardado de autorizacion de usuario."); //$NON-NLS-1$
			ret = processSaveValidator(session, xml);
		} else if (OPERATION_REVOCATE_VALIDATOR.equals(operation)) {
			LOGGER.info("Solicitud de revocacion de una autorizacion de usuario."); //$NON-NLS-1$
			ret = processRevocateValidator(session, xml);
		} else if (OPERATION_GET_PUSH_STATUS.equals(operation)) {
			LOGGER.info("Solicitud de obtencion del estado de las notificaciones push"); //$NON-NLS-1$
			ret = processGetPushStatus(session, xml);
		} else if (OPERATION_UPDATE_PUSH.equals(operation)) {
			LOGGER.info("Solicitud de actualizacion del estado de las notificaciones push"); //$NON-NLS-1$
			ret = processUpdatePushStatus(session, xml);
		} else {
			LOGGER.warn("Se ha indicado un codigo de operacion no valido"); //$NON-NLS-1$
			ret = ErrorManager.genError(ErrorManager.ERROR_UNSUPPORTED_OPERATION_NAME);
		}

		return ret;
	}

	/**
	 * Indica si una operaci&oacute;n requiere que antes se haya hecho login
	 * para que sea atendida por el proxy.
	 *
	 * @param operation
	 *            C&oacute;digo de la operaci&oacute;n que se quiere comprobar.
	 * @return {@code true} si el c&oacute;digo proporcionado no est&aacute; en
	 *         el listado de operaciones que se pueden realizar sin login,
	 *         {@code false} en caso contrario.
	 */
	private static boolean isLoginRequieredOperation(final String operation) {
		for (final String opwl : OPERATIONS_WITHOUT_LOGIN) {
			if (opwl.equals(operation)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Indica si una operaci&oacute;n es de las que crean una nueva
	 * sesi&oacute;n de usuario.
	 *
	 * @param operation
	 *            C&oacute;digo de la operaci&oacute;n que se quiere comprobar.
	 * @return {@code true} si el c&oacute;digo proporcionado est&aacute; en el
	 *         listado de operaciones que crean una nueva sesi&oacute;n,
	 *         {@code false} en caso contrario.
	 */
	private static boolean isCreatingLoginOperation(final String operation) {
		for (final String opwl : OPERATIONS_CREATE_SESSION) {
			if (opwl.equals(operation)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Procesa una petici&oacute;n de acceso a la aplicaci&oacute;n. Como
	 * respuesta a esta petici&oacute;n, se emitir&aacute; un token para la
	 * firma por parte del cliente y posterior validaci&oacute;n de la
	 * sesi&oacute;n.
	 *
	 * @param request
	 *            Petici&oacute;n realizada al servicio.
	 * @param xml
	 *            XML con los datos para el proceso de autenticaci&oacute;n.
	 * @return XML con el resultado a la petici&oacute;n.
	 * @throws SAXException
	 *             Cuando ocurre alg&uacute;n error al procesar los XML.
	 * @throws IOException
	 *             Cuando ocurre alg&uacute;n problema de comunicaci&oacute;n
	 *             con el servidor.
	 */
	private static String processRequestLogin(final HttpServletRequest request, final HttpSession session, final byte[] xml)
			throws SAXException, IOException {

		final Document doc = parse(xml);
		try {
			LoginRequestParser.parse(doc);
		} catch (final Exception e) {
			throw new SAXException("No se ha proporcionado una peticion de login valida", e); //$NON-NLS-1$
		}

		if (session != null) {
			SessionCollector.removeSession(session);
		}

		final HttpSession newSession = SessionCollector.createSession(request);

		final LoginRequestData loginRequestData = createLoginRequestData(newSession);

		newSession.setAttribute(SessionParams.INIT_TOKEN, loginRequestData.getData());

		// Si hay que compartir la sesion, se obtiene el ID de sesion compartida
		String sessionId = null;
		if (ConfigManager.isShareSessionEnabled() && ConfigManager.isShareSessionWithCertEnabled()) {
			sessionId = SessionCollector.createSharedSession(newSession);
		}

		return XmlResponsesFactory.createLoginResponse(loginRequestData, sessionId);
	}

	/**
	 * Obtiene los datos necesarios para el login de la aplicaci&oacute;n
	 * cliente.
	 *
	 * @param session
	 *            Sesi&oacute;n sobre la que se desea autenticar el usuario.
	 * @return Datos de inicio de sesi&oacute;n.
	 */
	private static LoginRequestData createLoginRequestData(final HttpSession session) {

		final LoginRequestData loginRequestData = new LoginRequestData(session.getId());
		// Establecemos los datos a firmar (Token)
		loginRequestData.setData(("" + session.getCreationTime() + "|" //$NON-NLS-1$ //$NON-NLS-2$
				+ UUID.randomUUID().toString()).getBytes(DEFAULT_CHARSET));

		return loginRequestData;
	}

	/**
	 * Valida el acceso de la aplicaci&oacute;n al Portafirmas. Como resultado,
	 * se enviar&aacute; el DNI del usuario autenticado.
	 *
	 * @param session
	 *            Sesi&oacute;n pendiente de validar.
	 * @param xml
	 *            XML con los datos para el proceso de autenticaci&oacute;n.
	 * @return XML con el resultado a la petici&oacute;n.
	 * @throws SAXException
	 *             Cuando ocurre alg&uacute;n error al procesar los XML.
	 * @throws IOException
	 *             Cuando ocurre alg&uacute;n problema de comunicaci&oacute;n
	 *             con el servidor.
	 */
	private String processValidateLogin(final HttpSession session, final byte[] xml) throws SAXException, IOException {

		final Document doc = parse(xml);
		final ValidateLoginRequest loginRequest = ValidateLoginRequestParser.parse(doc);

		final ValidateLoginResult validateLoginResult = validateLoginData(session, loginRequest);

		if (validateLoginResult.isLogged()) {
			session.setAttribute(SessionParams.VALID_SESSION, Boolean.TRUE.toString());
			// Se guarda el certificado en sesion para realizar peticiones. En
			// el futuro no se enviara
			if (loginRequest.getCertificate() != null) {
				session.setAttribute(SessionParams.CERT, Base64.encode(loginRequest.getCertificate()));
			}

			session.setAttribute(SessionParams.DNI, validateLoginResult.getDni());

			session.removeAttribute(SessionParams.INIT_TOKEN);

			SessionCollector.updateSession(session);
		} else {
			SessionCollector.removeSession(session);
		}

		LOGGER.info("Devolvemos el resultado del proceso de login para la sesion " + //$NON-NLS-1$
				(session == null ? "null" : session.getId()) + ": " + //$NON-NLS-1$ //$NON-NLS-2$
				validateLoginResult.isLogged());

		return XmlResponsesFactory.createValidateLoginResponse(validateLoginResult);
	}

	private ValidateLoginResult validateLoginData(final HttpSession session, final ValidateLoginRequest loginRequest) {

		final ValidateLoginResult result = new ValidateLoginResult();
		if (session == null) {
			LOGGER.warn("No se ha realizado previamente el inicio de sesion"); //$NON-NLS-1$
			result.setError("No se ha realizado previamente el inicio de sesion"); //$NON-NLS-1$
			return result;
		}

		// Comprobamos la validez de la firma PKCS1 remitida contra el
		// certificado recibido
		// y el token que se envio originalmente, y se manda a validar el
		// certificado para
		// el inicio de sesion
		try {
			checkPkcs1(loginRequest.getPkcs1(), loginRequest.getCertificate(),
					(byte[]) session.getAttribute(SessionParams.INIT_TOKEN));
		} catch (final Exception e) {
			LOGGER.warn("La firma del token de sesion no es valida", e); //$NON-NLS-1$
			result.setError("La firma del token de sesi\u00F3n no es v\u00E1lida"); //$NON-NLS-1$
			return result;
		}

		String dni = null;
		try {
			// Validacion contra el portafirmas (que valide que
			// la firma es valida y el certificado usado se corresponde con
			// un usuario)
			dni = getService().validateUser(loginRequest.getCertificate());
		} catch (final MobileException e) {
			LOGGER.warn("Error devuelto por el servicio de validacion", e); //$NON-NLS-1$
			final String errMsg = processLoginErrorMessage(e.getMessage());
			result.setError(errMsg != null ? errMsg
					: "El usuario no dispone de cuenta en este portafirmas o utiliz\u00F3 un certificado no v\u00E1lido."); //$NON-NLS-1$
		} catch (final Exception e) {
			LOGGER.warn("Error al validar la firma del token de login", e); //$NON-NLS-1$
			result.setError("El certificado utilizado no es v\u00E1lido."); //$NON-NLS-1$
		}

		// El Portafirmas nunca devolveria un DNI nulo, pero lo comprobamos
		if (dni != null) {
			result.setDni(dni);
		} else if (result.getError() == null) {
			LOGGER.warn("No se pudo obtener el DNI del usuario del certificado indicado"); //$NON-NLS-1$
			result.setError("No se ha podido identificar al usuario"); //$NON-NLS-1$
		}

		return result;
	}

	/**
	 * Procesa el mensaje de error remitido por el Portafirmas para hacerlo
	 * legible para el usuario.
	 *
	 * @param rawErrorMessage
	 *            Mensaje devuelto por el Portafirmas.
	 * @return Mensaje procesado o {@code null} si no hay ninguno.
	 */
	private static String processLoginErrorMessage(final String rawErrorMessage) {
		String msg = null;
		if (rawErrorMessage != null) {
			msg = rawErrorMessage;
			if (msg.indexOf(':') > -1) {
				msg = msg.substring(0, msg.indexOf(':'));
			}
			if (!msg.endsWith(".")) { //$NON-NLS-1$
				msg += "."; //$NON-NLS-1$
			}
		}
		return msg;
	}

	/**
	 * Procesa una petici&oacute;n de acceso al Portafirmas
	 * autentic&aacute;ndose con Cl@ve. Como respuesta a esta petici&oacute;n,
	 * se generar&aacute; la informaci&oacute;n de inicio de sesi&oacute;n y se
	 * remitir&aacute; una URL para la redirecci&oacute;n del usuario a la
	 * p&aacute;gina de Cl@ve para que autorice el acceso.
	 *
	 * @param request
	 *            Petici&oacute;n realizada al servicio.
	 * @param session
	 *            Ses&iacute;n que pueda haber actualmente iniciada.
	 * @param xml
	 *            XML con los datos para el proceso de autenticaci&oacute;n.
	 * @return XML con el resultado a la petici&oacute;n.
	 * @throws SAXException
	 *             Cuando ocurre alg&uacute;n error al procesar los XML.
	 * @throws IOException
	 *             Cuando ocurre alg&uacute;n problema de comunicaci&oacute;n
	 *             con el servidor.
	 * @throws MobileException
	 *             Cuando ocurre un error al contactar con el servidor.
	 */
	private String processRequestClaveLogin(final HttpServletRequest request, final HttpSession session,
			final byte[] xml) throws SAXException, IOException, MobileException {

		final Document doc = parse(xml);
		try {
			LoginClaveRequestParser.parse(doc);
		} catch (final Exception e) {
			throw new SAXException("No se ha proporcionado una peticion de login valida", e); //$NON-NLS-1$
		}

		// Si ya existe una sesion, la invalidamos
		if (session != null) {
			SessionCollector.removeSession(session);
		}

		// Creamos una nueva sesion
		final HttpSession newSession = SessionCollector.createSession(request);

		// Si hay que compartir la sesion, se obtiene el ID de sesion compartida
		String sessionId = null;
		if (ConfigManager.isShareSessionEnabled()) {
			sessionId = SessionCollector.createSharedSession(newSession);
		}

		final String baseUrl = getProxyBaseUrl(request);
		String resultUrl = baseUrl + "claveResultService"; //$NON-NLS-1$
		if (sessionId != null) {
			resultUrl += "?" + PARAMETER_NAME_SHARED_SESSION_ID + "=" + sessionId; //$NON-NLS-1$ //$NON-NLS-2$
		}

		// Conectamos con el Portafirmas web
		final MobileAccesoClave claveResponse = getService().solicitudAccesoClave(resultUrl, resultUrl);

		if (claveResponse.getClaveServiceUrl() == null) {
			SessionCollector.removeSession(newSession);
			throw new IOException("No se recupero la URL de redireccion para el inicio de sesion en Cl@ve"); //$NON-NLS-1$
		}
		newSession.setAttribute(SessionParams.CLAVE_URL, claveResponse.getClaveServiceUrl());

		if (claveResponse.getSamlRequest() == null) {
			SessionCollector.removeSession(newSession);
			throw new IOException("No se recupero el token SAML para el inicio de sesion en Cl@ve"); //$NON-NLS-1$
		}
		newSession.setAttribute(SessionParams.CLAVE_REQUEST_TOKEN, claveResponse.getSamlRequest());

		if (claveResponse.getExcludedIdPList() != null) {
			newSession.setAttribute(SessionParams.CLAVE_EXCLUDED_IDPS, claveResponse.getExcludedIdPList());
		}
		if (claveResponse.getForcedIdP() != null) {
			newSession.setAttribute(SessionParams.CLAVE_FORCED_IDP, claveResponse.getForcedIdP());
		}

		// Damos por iniciado el proceso de login con Clave
		newSession.setAttribute(SessionParams.INIT_WITH_CLAVE, Boolean.TRUE);

		// Generamos un identificador de inicio para comprobar mas adelante
		final String authenticationId = generateAuthenticationId();
		newSession.setAttribute(SessionParams.CLAVE_AUTHENTICATION_ID, authenticationId);

		// Se actualiza la sesion compatida con los nuevos datos asignados
		SessionCollector.updateSession(newSession);

		// Obtenemos la URL de redireccion
		String redirectionUrl = baseUrl + PAGE_CLAVE_LOADING;
		if (sessionId != null) {
			redirectionUrl += "?" + PARAMETER_NAME_SHARED_SESSION_ID + "=" + sessionId; //$NON-NLS-1$ //$NON-NLS-2$
		}

		return XmlResponsesFactory.createClaveLoginResponse(redirectionUrl, sessionId);
	}

	/**
	 * Proporciona la URL base de las p&aacute;ginas y servicios del proxy.
	 *
	 * @param request
	 *            Petici&oacute;n realizada.
	 * @return URL base del proxy terminada en '/'.
	 */
	private static String getProxyBaseUrl(final HttpServletRequest request) {
		final String baseUrl = request.getRequestURL().substring(0, request.getRequestURL().lastIndexOf("/") + 1); //$NON-NLS-1$
		return ConfigManager.getProxyBaseUrl() != null ? ConfigManager.getProxyBaseUrl() : baseUrl;
	}

	/**
	 * Genera un c&oacute;digo de autenticaci&oacute;n aleatorio.
	 *
	 * @return Identificador de acceso aleatorio.
	 */
	private static String generateAuthenticationId() {
		return UUID.randomUUID().toString();
	}

	/**
	 * Procesa una petici&oacute;n de cierre de sesi&oacute;n.
	 *
	 * @param request
	 *            Petici&oacute;n realizada al servicio.
	 * @param xml
	 *            XML con los datos para el proceso de autenticaci&oacute;n.
	 * @return XML con el resultado a la petici&oacute;n.
	 * @throws SAXException
	 *             Cuando ocurre alg&uacute;n error al procesar los XML.
	 * @throws IOException
	 *             Cuando ocurre alg&uacute;n problema de comunicaci&oacute;n
	 *             con el servidor.
	 */
	private String processLogout(final HttpSession session, final byte[] xml) throws SAXException, IOException {

		final Document doc = parse(xml);
		try {
			LogoutRequestParser.parse(doc);
		} catch (final Exception e) {
			throw new SAXException("No se ha proporcionado una peticion de logout valida", e); //$NON-NLS-1$
		}

		if (session != null) {
			SessionCollector.removeSession(session);
		}

		return XmlResponsesFactory.createLogoutResponse();
	}

	/**
	 * Comprueba que un PKCS#1 se genero en base a un certificado y sobre unos
	 * datos concretos.
	 *
	 * @param pkcs1
	 *            Firma PKCS#1 calculada sobre el algoritmo SHA-256.
	 * @param certEncoded
	 *            Certificado electr&oacute;nico con el que se gener&oacute; el
	 *            PKCS#1.
	 * @param data
	 *            Datos que se firmaron.
	 * @throws NoSuchAlgorithmException
	 *             Cuando el algoritmo de firma no este soportado.
	 * @throws CertificateException
	 *             Cuando el certificado de la firma no sea valido o no coincida
	 *             con el indicado.
	 * @throws InvalidKeyException
	 *             Cuando el certificado no contenga una clave publica
	 *             v&aacute;lida.
	 * @throws Exception
	 *             Cuando no se puede completar la validaci&oacute;n de la
	 *             estructura.
	 */
	private static void checkPkcs1(final byte[] pkcs1, final byte[] certEncoded, final byte[] data)
			throws SignatureException, NoSuchAlgorithmException, CertificateException, InvalidKeyException {

		final Certificate cert = CertificateFactory.getInstance("X.509").generateCertificate( //$NON-NLS-1$
				new ByteArrayInputStream(certEncoded));

		final Signature signer = Signature.getInstance(LOGIN_SIGNATURE_ALGORITHM);
		signer.initVerify(cert.getPublicKey());
		signer.update(data);

		if (!signer.verify(pkcs1)) {
			throw new SignatureException(
					"La firma no se realizo sobre el token proporcionado o con el certificado indicado"); //$NON-NLS-1$
		}
	}

	private String processNotificationRegistry(final HttpSession session, final byte[] xml)
			throws SAXException, IOException, MobileException {

		final Document xmlDoc = parse(xml);
		final NotificationRegistry registry = NotificationRegistryParser.parse(xmlDoc);

		final String dni = (String) session.getAttribute(SessionParams.DNI);
		final NotificationRegistryResult result = doNotificationRegistry(dni, registry);

		return XmlResponsesFactory.createNotificationRegistryResponse(result);
	}

	private NotificationRegistryResult doNotificationRegistry(final String dni, final NotificationRegistry registry)
			throws MobileException {

		final MobileSIMUser user = new MobileSIMUser();
		user.setIdDispositivo(registry.getDeviceId()); // Identificador unico
														// del dispositivo
		user.setPlataforma(registry.getPlatform()); // Plataforma de
													// notificacion ("GCM" para
													// Android y "APNS" para
													// iOS)
		user.setIdRegistro(registry.getIdRegistry()); // Token de registro

		LOGGER.info("Registro de dispositivo: \nDispositivo: {}\nPlataforma: {}\nToken de registro: {}", //$NON-NLS-1$
				user.getIdDispositivo(), user.getPlataforma(), user.getIdRegistro());

		final MobileSIMUserStatus status = getService().registerSIMUser(dni.getBytes(DEFAULT_CHARSET), user);

		LOGGER.info("Resultado del registro:\nResultado: {}\nTexto: {}\nDetalle: {}", //$NON-NLS-1$
				status.getDetails(), status.getStatusCode(), status.getStatusText(), status.getDetails());

		final NotificationRegistryResult result = new NotificationRegistryResult(status.getStatusCode(),
				status.getStatusText());
		if (!result.isRegistered()) {
			result.setErrorDetails(status.getDetails());
		}
		return result;
	}

	/**
	 * Procesa las peticiones de prefirma. Se realiza la prefirma de cada uno de
	 * los documentos de las peticiones indicadas. Si se produce alg&uacute;n
	 * error al procesar un documento de alguna de las peticiones, se establece
	 * como incorrecta la petici&oacute;n al completo.
	 *
	 * @param xml
	 *            XML con los datos para el proceso de las prefirmas.
	 * @return XML con el resultado a la petici&oacute;n de prefirma.
	 * @throws SAXException
	 *             Cuando ocurre alg&uacute;n error al procesar los XML.
	 * @throws IOException
	 *             Cuando ocurre alg&uacute;n problema de comunicaci&oacute;n
	 *             con el servidor.
	 * @throws CertificateException
	 *             Cuando ocurre alg&uacute;n problema con el certificado de
	 *             firma.
	 */
	private String processPreSigns(final HttpSession session, final byte[] xml)
			throws SAXException, IOException, CertificateException {

		// Cargamos los datos trifasicos
		final Document xmlDoc = parse(xml);
		final TriphaseRequestBean triRequests = SignRequestsParser.parse(xmlDoc,
				Base64.decode((String) session.getAttribute(SessionParams.CERT)));

		// Prefirmamos
		preSign(triRequests);

		// Generamos la respuesta
		return XmlResponsesFactory.createPresignResponse(triRequests);
	}

	/**
	 * Procesa las peticiones de postfirma. Se realiza la postfirma de cada uno
	 * de los documentos de las peticiones indicadas. Si se produce alg&uacute;n
	 * error al procesar un documento de alguna de las peticiones, se establece
	 * como incorrecta la petici&oacute;n al completo.
	 *
	 * @param xml
	 *            XML con los datos para el proceso de las prefirmas.
	 * @return XML con el resultado a la petici&oacute;n de prefirma.
	 * @throws SAXException
	 *             Cuando ocurre alg&uacute;n error al procesar los XML.
	 * @throws IOException
	 *             Cuando ocurre alg&uacute;n problema de comunicaci&oacute;n
	 *             con el servidor.
	 * @throws CertificateException
	 *             Cuando ocurre alg&uacute;n problema con el certificado de
	 *             firma.
	 */
	private String processPostSigns(final HttpSession session, final byte[] xml)
			throws SAXException, IOException, CertificateException {

		final Document xmlDoc = parse(xml);

		final byte[] cer = Base64.decode((String) session.getAttribute(SessionParams.CERT));
		final TriphaseRequestBean triRequests = SignRequestsParser.parse(xmlDoc, cer);

		// Ejecutamos las postfirmas y se registran las firmas en el servidor
		postSign(triRequests);

		// Generamos la respuesta
		return XmlResponsesFactory.createPostsignResponse(triRequests);
	}

	/**
	 * Transforma una peticion de tipo TriphaseRequest en un
	 * MobileDocSignInfoList.
	 *
	 * @param req
	 *            Petici&oacute;n de firma con el resultado asociado a cada
	 *            documento.
	 * @return Listado de firmas de documentos.
	 */
	private static MobileDocSignInfoList transformToWsParams(final TriphaseRequest req) {

		final MobileDocSignInfoList signInfoList = new MobileDocSignInfoList();
		final List<MobileDocSignInfo> list = signInfoList.getMobileDocSignInfo();

		final ObjectFactory factory = new ObjectFactory();

		MobileDocSignInfo signInfo;
		for (final TriphaseSignDocumentRequest docReq : req) {
			signInfo = new MobileDocSignInfo();
			signInfo.setDocumentId(docReq.getId());
			signInfo.setSignFormat(docReq.getSignatureFormat());
			signInfo.setSignature(new DataHandler(new ByteArrayDataSource(docReq.getResult(), null)));
			if (docReq.isNeedConfirmation()) {
				signInfo.setValidar(factory.createMobileDocSignInfoValidar("false")); //$NON-NLS-1$
			}
			list.add(signInfo);
		}

		return signInfoList;
	}

	/**
	 * Procesa la petici&oacute;n de un listado de peticiones de firma.
	 *
	 * @param xml
	 *            XML con la solicitud.
	 * @return XML con la respuesta a la petici&oacute;n.
	 * @throws SAXException
	 *             Cuando ocurre alg&uacute;n error al procesar los XML.
	 * @throws IOException
	 *             Cuando ocurre alg&uacute;n errlr al leer el XML.
	 * @throws MobileException
	 *             Cuando ocurre un error al contactar con el servidor.
	 */
	private String processRequestsList(final HttpSession session, final byte[] xml)
			throws SAXException, IOException, MobileException {

		final Document doc = parse(xml);
		final ListRequest listRequest = ListRequestParser.parse(doc);

		// El DNI a recuperar debe ser el DNI del propietario de la peticion.
		final String dni = listRequest.getOwnerId() != null
				? listRequest.getOwnerId()
				: (String) session.getAttribute(SessionParams.DNI);
		final PartialSignRequestsList signRequests = getRequestsList(dni, listRequest);

		return XmlResponsesFactory.createRequestsListResponse(signRequests);
	}

	/**
	 * Recupera un listado de peticiones del Portafirmas a partir de la
	 * solicitud proporcionada.
	 *
	 * @param dni
	 *            DNI del usuario.
	 * @param listRequest
	 *            Solicitud de peticiones de firma.
	 * @return Listado de peticiones.
	 * @throws MobileException
	 *             Cuando ocurre un error al contactar con el Portafirmas.
	 */
	private PartialSignRequestsList getRequestsList(final String dni, final ListRequest listRequest)
			throws MobileException {

		// Listado de formatos de firma soportados
		final MobileStringList formatsList = new MobileStringList();

		// Enviamos el listado de formatos vacio para que no se omitan peticiones
//		for (final String supportedFormat : listRequest.getFormats()) {
//			formatsList.getStr().add(supportedFormat);
//		}

		// Listado de filtros para la consulta
		final MobileRequestFilterList filterList = new MobileRequestFilterList();
		final Map<String, String> definedFilters = listRequest.getFilters();
		if (definedFilters != null) {
			for (final String filterKey : definedFilters.keySet().toArray(new String[0])) {
				final String value = definedFilters.get(filterKey);
				if (value != null && !value.isEmpty()) {
					final MobileRequestFilter filter = new MobileRequestFilter();
					filter.setKey(filterKey);
					filter.setValue(value);
					filterList.getRequestFilter().add(filter);
				}
			}
		}

		// El filtro de periodo de tiempo es obligatorio. Si no estaba entre los filtros
		// definidos, se agregar con un valor por defecto para mostrar todas las peticiones
		if (definedFilters == null || !definedFilters.containsKey(KEY_FILTER_PERIOD)) {
			final MobileRequestFilter filter = new MobileRequestFilter();
			filter.setKey(KEY_FILTER_PERIOD);
			filter.setValue(VALUE_DEFAULT_PERIOD);
			filterList.getRequestFilter().add(filter);
		}

		// Solicitud de lista de peticiones
		final MobileRequestList mobileRequestsList = getService().queryRequestList(dni.getBytes(DEFAULT_CHARSET),
				listRequest.getState(), Integer.toString(listRequest.getNumPage()),
				Integer.toString(listRequest.getPageSize()), formatsList, filterList);

		final List<SignRequest> signRequests = new ArrayList<>(mobileRequestsList.getSize().intValue());
		for (final MobileRequest request : mobileRequestsList.getRequest()) {

			final List<MobileDocument> docList = request.getDocumentList() != null
					? request.getDocumentList().getDocument() : new ArrayList<MobileDocument>();

			final SignRequestDocument[] docs = new SignRequestDocument[docList.size()];

			try {
				for (int j = 0; j < docs.length; j++) {
					final MobileDocument doc = docList.get(j);

					docs[j] = new SignRequestDocument(doc.getIdentifier(), doc.getName(), doc.getSize().getValue(),
							doc.getMime(), doc.getOperationType(), doc.getSignatureType().getValue().value(),
							doc.getSignAlgorithm().getValue(),
							prepareSignatureParamenters(doc.getSignatureParameters()));
				}
			} catch (final Exception e) {
				final String id = request.getIdentifier() != null ? request.getIdentifier().getValue() : "null"; //$NON-NLS-1$
				LOGGER.warn("Se ha encontrado un error al analizar los datos de los documentos de la peticion con ID '" //$NON-NLS-1$
						+ id + "' y no se mostrara: " + e.toString()); //$NON-NLS-1$
				continue;
			}

			signRequests
					.add(new SignRequest(request.getRequestTagId(), request.getSubject().getValue(),
							request.getSenders().getStr().get(0), request.getView(),
							request.getFentry().getValue(),
							request.getFexpiration() != null ? request.getFexpiration().getValue() : null,
							request.getImportanceLevel().getValue(), request.getWorkflow().getValue().booleanValue(),
							request.getForward().getValue().booleanValue(), request.getRequestType(), docs));
		}

		return new PartialSignRequestsList(signRequests.toArray(new SignRequest[signRequests.size()]),
				mobileRequestsList.getSize().intValue());
	}

	private static String prepareSignatureParamenters(final JAXBElement<String> parameters) {
		if (parameters == null) {
			return null;
		}
		return parameters.getValue();
	}

	private String processRejects(final HttpSession session, final byte[] xml) throws SAXException, IOException {
		final Document doc = parse(xml);
		final RejectRequest request = RejectsRequestParser.parse(doc);

		final String dni = (String) session.getAttribute(SessionParams.DNI);
		final RequestResult[] requestResults = doReject(dni, request);

		return XmlResponsesFactory.createRejectsResponse(requestResults);
	}

	/**
	 * Rechaza el listado de solicitudes indicado en la petici&oacute;n de
	 * rechazo.
	 *
	 * @param dni
	 *            DNI del usuario.
	 * @param rejectRequest
	 *            Petici&oacute;n de rechazo.
	 * @return Resultado del rechazo de cada solicitud.
	 */
	private RequestResult[] doReject(final String dni, final RejectRequest rejectRequest) {

		final MobileService service = getService();
		final List<Boolean> rejectionsResults = new ArrayList<>();
		for (final String id : rejectRequest) {
			// Si devuelve cualquier texto es que la operacion ha terminado
			// correctamente. Por defecto,
			// devuelve el mismo identificador de la peticion, aunque no es
			// obligatorio
			// Si falla devuelve una excepcion.
			try {
				service.rejectRequest(dni.getBytes(DEFAULT_CHARSET), id, rejectRequest.getRejectReason());
				rejectionsResults.add(Boolean.TRUE);
			} catch (final Exception e) {
				LOGGER.warn("Error en el rechazo de la peticion " + id, e); //$NON-NLS-1$
				rejectionsResults.add(Boolean.FALSE);
			}
		}

		final RequestResult[] result = new RequestResult[rejectRequest.size()];
		for (int i = 0; i < rejectRequest.size(); i++) {
			result[i] = new RequestResult(rejectRequest.get(i), rejectionsResults.get(i).booleanValue());
		}

		return result;
	}

	private String processRequestDetail(final HttpSession session, final byte[] xml)
			throws SAXException, IOException, MobileException {

		final Document doc = parse(xml);
		final DetailRequest detRequest = DetailRequestParser.parse(doc);

		// El DNI debe ser el DNI del propietario de la peticion.
		final String dni = detRequest.getOwnerId() != null ? detRequest.getOwnerId()
				: (String) session.getAttribute(SessionParams.DNI);
		final Detail requestDetails = getRequestDetail(dni, detRequest);

		return XmlResponsesFactory.createRequestDetailResponse(requestDetails);
	}

	/**
	 * Obtiene el detalle de un solicitud de firma a partir de una
	 * petici&oacute;n de detalle.
	 *
	 * @param dni
	 *            DNI del usuario.
	 * @param request
	 *            Petici&oacute;n que debe realizarse.
	 * @return Detalle de la solicitud.
	 * @throws MobileException
	 *             Cuando ocurre un error al contactar con el Portafirmas.
	 */
	private Detail getRequestDetail(final String dni, final DetailRequest request) throws MobileException {

		// Solicitud de lista de peticiones
		final MobileRequest mobileRequest = getService().queryRequest(dni.getBytes(DEFAULT_CHARSET), request.getRequestId());

		// Listado de documentos de la peticion
		final List<MobileDocument> mobileDocs = mobileRequest.getDocumentList().getDocument();
		final SignRequestDocument[] docs = new SignRequestDocument[mobileDocs.size()];
		for (int i = 0; i < mobileDocs.size(); i++) {
			final MobileDocument doc = mobileDocs.get(i);
			docs[i] = new SignRequestDocument(doc.getIdentifier(), doc.getName(), doc.getSize().getValue(),
					doc.getMime(), doc.getOperationType(), doc.getSignatureType().getValue().value(),
					doc.getSignAlgorithm().getValue(), prepareSignatureParamenters(doc.getSignatureParameters()));
		}

		// Listado de adjuntos de la peticion
		final List<MobileDocument> mobileAttached = mobileRequest.getAttachList().getValue().getDocument();
		final SignRequestDocument[] attached = new SignRequestDocument[mobileAttached.size()];
		for (int i = 0; i < mobileAttached.size(); i++) {
			final MobileDocument att = mobileAttached.get(i);
			attached[i] = new SignRequestDocument(att.getIdentifier(), att.getName(), att.getSize().getValue(),
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

		// Creamos el objeto de detalle
		final Detail detail = new Detail(mobileRequest.getRequestTagId());
		detail.setApp(mobileRequest.getApplication() != null ? mobileRequest.getApplication().getValue() : ""); //$NON-NLS-1$
		detail.setDate(mobileRequest.getFentry() != null
				? mobileRequest.getFentry().getValue() : null);
		detail.setExpDate(mobileRequest.getFexpiration() != null
				? mobileRequest.getFexpiration().getValue() : null);
		detail.setSubject(mobileRequest.getSubject().getValue());
		detail.setText(mobileRequest.getText() != null ? cleanHtmlTags(mobileRequest.getText().getValue()) : ""); //$NON-NLS-1$
		detail.setWorkflow(mobileRequest.getWorkflow().getValue().booleanValue());
		detail.setForward(mobileRequest.getForward().getValue().booleanValue());
		detail.setPriority(mobileRequest.getImportanceLevel().getValue());
		detail.setType(mobileRequest.getRequestType());
		detail.setRef(mobileRequest.getRef() != null ? mobileRequest.getRef().getValue() : ""); //$NON-NLS-1$
		detail.setRejectReason(
				mobileRequest.getRejectedText() != null ? cleanHtmlTags(mobileRequest.getRejectedText().getValue()) : null);
		detail.setSignLinesFlow(
				mobileRequest.isCascadeSign() ? Detail.SIGN_LINES_FLOW_CASCADE : Detail.SIGN_LINES_FLOW_PARALLEL);
		detail.setSenders(
				mobileRequest.getSenders().getStr().toArray(new String[mobileRequest.getSenders().getStr().size()]));
		detail.setDocs(docs);
		detail.setAttached(attached);
		detail.setSignLines(signLines);

		return detail;
	}

	private static String cleanHtmlTags(final String text) {

		if (text == null) {
			return ""; //$NON-NLS-1$
		}

		return text
				.replace("<br/>", "\r\n") //$NON-NLS-1$ //$NON-NLS-2$
				.replace("<br>", "\r\n") //$NON-NLS-1$ //$NON-NLS-2$
				.trim();

	}

	private InputStream processDocumentPreview(final HttpSession session, final byte[] xml)
			throws SAXException, IOException, MobileException {
		final Document doc = parse(xml);
		final PreviewRequest request = PreviewRequestParser.parse(doc);

		final String dni = (String) session.getAttribute(SessionParams.DNI);
		final DocumentData documentData = previewDocument(dni, request);

		return documentData.getDataIs();
	}

	private InputStream processSignPreview(final HttpSession session, final byte[] xml)
			throws SAXException, IOException, MobileException {
		final Document doc = parse(xml);
		final PreviewRequest request = PreviewRequestParser.parse(doc);

		final String dni = (String) session.getAttribute(SessionParams.DNI);
		final DocumentData documentData = previewSign(dni, request);

		return documentData.getDataIs();
	}

	private InputStream processSignReportPreview(final HttpSession session, final byte[] xml)
			throws SAXException, IOException, MobileException {
		final Document doc = parse(xml);
		final PreviewRequest request = PreviewRequestParser.parse(doc);

		final String dni = (String) session.getAttribute(SessionParams.DNI);
		final DocumentData documentData = previewSignReport(dni, request);

		return documentData.getDataIs();
	}

	/**
	 * Recupera los datos para la previsualizaci&oacute;n de un documento a
	 * partir del identificador del documento.
	 *
	 * @param dni
	 *            DNI del usuario.
	 * @param request
	 *            Petici&oacute;n de visualizaci&oacute;n de un documento.
	 * @return Datos necesarios para la previsualizaci&oacute;n.
	 * @throws MobileException
	 *             Cuando ocurre un error al contactar con el Portafirmas.
	 * @throws IOException
	 *             Cuando no ha sido posible leer el documento.
	 */
	private DocumentData previewDocument(final String dni, final PreviewRequest request)
			throws MobileException, IOException {
		return buildDocumentData(getService().documentPreview(dni.getBytes(DEFAULT_CHARSET), request.getDocId()));
	}

	/**
	 * Recupera los datos para la descarga de una firma a partir del hash del
	 * documento firmado.
	 *
	 * @param dni
	 *            DNI del usuario.
	 * @param request
	 *            Petici&oacute;n de visualizaci&oacute;n de un documento.
	 * @return Datos necesarios para la previsualizaci&oacute;n.
	 * @throws MobileException
	 *             Cuando ocurre un error al contactar con el Portafirmas.
	 * @throws IOException
	 *             Cuando no ha sido posible leer el documento.
	 */
	private DocumentData previewSign(final String dni, final PreviewRequest request)
			throws MobileException, IOException {
		return buildDocumentData(getService().signPreview(dni.getBytes(DEFAULT_CHARSET), request.getDocId()));
	}

	/**
	 * Recupera los datos para la visualizaci&oacute;n de un informe de firma a
	 * partir del hash del documento firmado.
	 *
	 * @param dni
	 *            DNI del usuario.
	 * @param request
	 *            Petici&oacute;n de visualizaci&oacute;n de un documento.
	 * @return Datos necesarios para la previsualizaci&oacute;n.
	 * @throws MobileException
	 *             Cuando ocurre un error al contactar con el Portafirmas.
	 * @throws IOException
	 *             Cuando no ha sido posible leer el documento.
	 */
	private DocumentData previewSignReport(final String dni, final PreviewRequest request)
			throws MobileException, IOException {
		return buildDocumentData(getService().reportPreview(dni.getBytes(DEFAULT_CHARSET), request.getDocId()));
	}

	/**
	 * Construye un objeto documento para previsualizaci&oacute;n.
	 *
	 * @param document
	 *            Datos del documento.
	 * @return Contenido y metadatos del documento.
	 * @throws IOException
	 *             Cuando ocurre un error en la lectura de los datos.
	 */
	private static DocumentData buildDocumentData(final MobileDocument document) throws IOException {

		final InputStream contentIs;
		final Object content = document.getData().getValue().getContent();
		if (content instanceof InputStream) {
			contentIs = (InputStream) content;
		} else if (content instanceof String) {
			contentIs = new ByteArrayInputStream(Base64.decode((String) content));
		} else {
			final String msg = "No se puede manejar el tipo de objeto devuelto por el servicio de previsualizacion de documentos: " //$NON-NLS-1$
					+ (content == null ? null : content.getClass());
			throw new IOException(msg);
		}

		return new DocumentData(document.getIdentifier(), document.getName(), document.getMime(), contentIs);
	}

	/**
	 * Procesa la petici&oacute;n de recuperaci&oacute;n de la configuraci&oacute;n de
	 * aplicaci&oacute;n.
	 *
	 * @param session
	 *            Sesi&oacute;n de usuario.
	 * @param xml
	 *            Petici&oacute;n recibida.
	 * @return Un XML con la configuraci&oacute;n de la aplicaci&oacute;n solicitada.
	 * @throws SAXException
	 *             Cuando ocurre un error de parseo de petici&oacute;n.
	 * @throws IOException
	 *             Cuando ocurre un error de lectura/escritura.
	 * @throws MobileException
	 *             Cuando ocurre un error durante la comunicaci&oacute;n con
	 *             portafirmas-web.
	 */
	private String processConfigueApp(final HttpSession session, final byte[] xml)
			throws SAXException, IOException, MobileException {

		final Document doc = parse(xml);
		final ConfigurationRequest request = ConfigurationRequestParser.parse(doc);

		final String dni = (String) session.getAttribute(SessionParams.DNI);
		final AppConfiguration appConfig = loadConfiguration(dni, request);

		return XmlResponsesFactory.createConfigurationResponse(appConfig);
	}

	/**
	 * Recupera los datos de confguracion de la aplicaci&oacute;n. Hasta el
	 * momento:
	 * <ul>
	 * <li>Listado de aplicaciones.</li>
	 * </ul>
	 *
	 * @param dni
	 *            DNI del usuario.
	 * @param request
	 *            Datos gen&eacute;ricos necesarios para la petici&oacute;n.
	 * @return Configuraci&oacute;n de la aplicaci&oacute;n.
	 * @throws MobileException
	 *             Cuando ocurre un error al contactar con el Portafirmas.
	 * @throws IOException
	 *             Cuando no ha sido posible leer el documento.
	 */
	private AppConfiguration loadConfiguration(final String dni, final ConfigurationRequest request)
			throws MobileException, IOException {

		final MobileApplicationList appList = getService().queryApplicationsMobile(dni.getBytes(DEFAULT_CHARSET));

		final List<String> appIds = new ArrayList<>();
		final List<String> appNames = new ArrayList<>();

		for (final MobileApplication app : appList.getApplication()) {
			appIds.add(app.getId());
			appNames.add(app.getName() != null ? app.getName() : app.getId());
		}

		return new AppConfiguration(appIds, appNames);
	}

	private String processApproveRequest(final HttpSession session, final byte[] xml) throws SAXException, IOException {

		final Document xmlDoc = parse(xml);
		final ApproveRequestList appRequests = ApproveRequestParser.parse(xmlDoc);

		final String dni = (String) session.getAttribute(SessionParams.DNI);
		final ApproveRequestList approvedList = approveRequests(dni, appRequests);

		return XmlResponsesFactory.createApproveRequestsResponse(approvedList);
	}

	/**
	 * Aprueba el listado de solicitudes indicado en la petici&oacute;n de
	 * aprobaci$oacute;n.
	 *
	 * @param dni
	 *            DNI del usuario.
	 * @param appRequest
	 *            Petici&oacute;n de aprobaci&oacute;n.
	 * @return Resultado de la aprobaci&oacute;n de cada solicitud.
	 */
	private ApproveRequestList approveRequests(final String dni, final ApproveRequestList appRequests) {
		final MobileService service = getService();

		for (final ApproveRequest appReq : appRequests) {
			try {
				service.approveRequest(dni.getBytes(DEFAULT_CHARSET), appReq.getRequestTagId());
			} catch (final MobileException e) {
				appReq.setOk(false);
			}
		}
		return appRequests;
	}

	/**
	 * Procesa las peticiones de firma con FIRe. Se realiza la prefirma de cada
	 * uno de los documentos de las peticiones indicadas, se envian a FIRe para
	 * que realice una firma PKCS#1 y se vuelve la URL a la que este redirigio.
	 * Si se produce alg&uacute;n error al procesar un documento de alguna de
	 * las peticiones, se establece como incorrecta la petici&oacute;n al
	 * completo.
	 *
	 * @param session
	 *            Sesi&oacute;n establecida con el portafirmas m&oacute;vil.
	 * @param xml
	 *            XML con los datos para el proceso de firma.
	 * @return XML con la URL de redirecci&oacute;n a FIRe.
	 * @throws SAXException
	 *             Cuando ocurre alg&uacute;n error al procesar los XML.
	 * @throws IOException
	 *             Cuando ocurre alg&uacute;n problema de comunicaci&oacute;n
	 *             con el servidor.
	 * @throws CertificateException
	 *             Cuando ocurre alg&uacute;n problema con el certificado de
	 *             firma.
	 */
	private String processFireLoadData(final HttpSession session, final byte[] xml)
			throws SAXException, IOException, CertificateException {

		final String dni = (String) session.getAttribute(SessionParams.DNI);

		final Document xmlDoc = parse(xml);
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
	 *
	 * @param dni
	 *            DNI del usuario.
	 * @param requestsRefList
	 *            Listado de referencias de las peticiones a firmar.
	 * @return
	 */
	private FireLoadDataResult fireLoadData(final String dni, final MobileStringList requestsRefList) {

		MobileFireTrasactionResponse response;
		try {
			response = getService().fireTransaction(dni.getBytes(DEFAULT_CHARSET), requestsRefList);
		} catch (final MobileException e) {
			LOGGER.warn("Error durante la carga de documentos en FIRe", e); //$NON-NLS-1$
			return new FireLoadDataResult();
		}

		final FireLoadDataResult loadDataResult = new FireLoadDataResult();
		loadDataResult.setStatusOk(true);
		loadDataResult.setTransactionId(response.getTransactionId());
		loadDataResult.setUrlRedirect(response.getUrlRedirect());

		return loadDataResult;
	}

	/**
	 * Procesa las peticiones de firma con ClaveFirma. Se realiza la prefirma de
	 * cada uno de los documentos de las peticiones indicadas, se envian a FIRe
	 * para que realice una firma PKCS#1 y se vuelve la URL a la que este
	 * redirigio. Si se produce alg&uacute;n error al procesar un documento de
	 * alguna de las peticiones, se establece como incorrecta la petici&oacute;n
	 * al completo.
	 *
	 * @param request
	 *            Petici&oacute;n HTTP recibida.
	 * @param xml
	 *            XML con los datos para el proceso de firma.
	 * @return Resultado del proceso de firma.
	 * @throws SAXException
	 *             Cuando ocurre alg&uacute;n error al procesar los XML.
	 * @throws IOException
	 *             Cuando ocurre alg&uacute;n problema de comunicaci&oacute;n
	 *             con el servidor.
	 * @throws CertificateException
	 *             Cuando ocurre alg&uacute;n problema con el certificado de
	 *             firma.
	 */
	private String processFireSign(final HttpSession session, final byte[] xml)
			throws SAXException, IOException, CertificateException {

		final Document xmlDoc = parse(xml);

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
	 * M&eacute;todo que procesa la petici&oacute;n del servicio "getUserConfiguration".
	 *
	 * @param session
	 *            Sesi&oacute;n HTTP.
	 * @param xml
	 *            Petici&oacute;n XML.
	 * @return la respuesta del servicio.
	 * @throws SAXException
	 *             Si algo falla.
	 * @throws IOException
	 *             Si algo falla.
	 */
	private String processGetUserConfig(final HttpSession session, final byte[] xml) throws SAXException, IOException {

		final Document xmlDoc = parse(xml);

		// Comprobamos que el XML de peticion esta bien formado.
		GetUserConfiguration.parse(xmlDoc);

		final String dni = (String) session.getAttribute(SessionParams.DNI);
		final GetUserConfigResult response = getUserConfiguration(dni);
		return XmlResponsesFactory.createGetUserConfigurationResponse(response);
	}

	/**
	 * M&eacute;todo que realiza la operaci&oacute;n de b&uacute;squeda de usuarios.
	 * @param session Sesi&oacute;n HTTP.
	 * @param xml Petici&oacute;n XML.
	 * @return la respuesta del servicio.
	 * @throws SAXException Si el proceso falla.
	 * @throws IOException Si el proceso falla.
	 */
	private String processFindUser(final HttpSession session, final byte[] xml)
			throws SAXException, IOException {

		final Document xmlDoc = parse(xml);

		// Extraemos la informacion del XML de peticion
		final FindUserRequest request = GetUserRequestParser.parse(xmlDoc);

		final String dni = (String) session.getAttribute(SessionParams.DNI);

		// Realizamos la consulta al Portafirmas
		final FindUserResult response = getUsers(dni, request.getMode(), request.getText());

		// Devolvemos la respuesta
		return XmlResponsesFactory.createFindUserResponse(response);
	}

	/**
	 * M&eacute;todo que recupera la lista de usuarios acordes a los criterios de b&uacute;squeda.
	 * @param dni DNI del usuario que realiza la b&uacute;squeda.
	 * @param mode Finalidad que se le asignara al usuario a buscar (validador, autorizado, etc.).
	 * @return la respuesta con el usuario encontrado.
	 */
	private FindUserResult getUsers(final String dni, final String mode, final String text) {
		MobileUsuariosList response;
		try {
			response = getService().busquedaUsuariosMobile(dni.getBytes(DEFAULT_CHARSET), text, mode, null);
		} catch (final MobileException e) {
			LOGGER.warn("Error durante la recuperacion de usuarios", e); //$NON-NLS-1$
			return new FindUserResult(FindUserResult.ERROR_TYPE_COMMUNICATION);
		}

		final List<GenericUser> usersList = new ArrayList<>();
		for (final MobileUsuarioGenerico user : response.getUsuario()) {
			final String id = user.getId();
			final String idDni = user.getCidentifierDNI();
			final String name = user.getNombreCompleto();
			usersList.add(new GenericUser(id, idDni, name));
		}

		return new FindUserResult(usersList);
	}

	/**
	 * M&eacute;todo que procesa la petici&oacute;n de validar una petici&oacute;n de firma.
	 * @param session Sesi&oacute;n HTTP.
	 * @param xml Petici&oacute;n XML.
	 * @return el resultado del servicio de validar una petici&oacute;n.
	 * @throws IOException Si algo falla.
	 * @throws SAXException Si algo falla.
	 */
	private String processVerifyPetitions(final HttpSession session, final byte[] xml) throws SAXException, IOException {

		final Document xmlDoc = parse(xml);

		// Comprobamos que el XML de peticion esta bien formado.
		final List<String> petitionsIds = VerifyPetitionParser.parse(xmlDoc);

		// Recuperamos el DNI del validador y la lista de peticiones a validar.
		final String dni = (String) session.getAttribute(SessionParams.DNI);

		// Lanzamos todas las peticiones de validacion necesarias.
		final List<VerifyPetitionResult> responseList = new LinkedList<>();
		for (final String petitionId : petitionsIds) {
			final VerifyPetitionResult response = verifyPetitions(dni, petitionId);
			responseList.add(response);
		}

		// Construimos la respuesta para la aplicacion movil.
		return XmlResponsesFactory.createVerifyPetitionsResponse(responseList);
	}

	/**
	 * M&eacute;todo que realiza la llamada al servicio de validar peticiones del
	 * portafirmas-web.
	 *
	 * @param dni
	 *            DNI del validador.
	 * @param petitionId
	 *            identificador de la petici&oacute;n a validar.
	 * @return un objeto de tipo GetVerifyPetitionsResult que representa la
	 *         respuesta del servicio.
	 */
	private VerifyPetitionResult verifyPetitions(final String dni, final String petitionId) {
		VerifyPetitionResult response;
		try {
			response = new VerifyPetitionResult(getService().validarPeticion(dni.getBytes(DEFAULT_CHARSET), petitionId), petitionId);
		} catch (final MobileException e) {
			LOGGER.warn("Error durante la validacion de peticiones", e); //$NON-NLS-1$
			return new VerifyPetitionResult(FireSignResult.ERROR_TYPE_COMMUNICATION, petitionId);
		}
		return response;
	}


	/**
	 * Procesa la petici&oacute;n de listado de autorizaciones.
	 * @param session Sesi&oacute;n HTTP.
	 * @param xml Petici&oacute;n XML.
	 * @return El resultado del servicio de listar las autorizaciones.
	 * @throws IOException Si no es posible remitir la petici&oacute;n.
	 * @throws SAXException Si falla el procesado de la petici&oacute;n.
	 */
	private String processListAuthorizations(final HttpSession session, final byte[] xml) throws SAXException, IOException {

		final Document doc = parse(xml);

		// Parseamos la peticion
		GenericRequestParser.parse(doc);

		// Recuperamos el DNI del usuario
		final String dni = (String) session.getAttribute(SessionParams.DNI);

		// Listamos las autorizaciones registradas
		final ListAuthorizations authorizationsList = listAuthorizations(dni);

		// Construimos la respuesta para la aplicacion movil.
		return XmlResponsesFactory.createListAuthorizationsResponse(authorizationsList);
	}

	/**
	 * Lista las autorizaciones de un usuario.
	 * @param dni DNI del usuario del que se desean obtener las autorizaciones.
	 * @return Resultado con el listado de autorizaciones o el error recibido.
	 */
	private ListAuthorizations listAuthorizations(final String dni) {

		final ListAuthorizations response;
		try {
			final List<Authorization> authorizationsList = new ArrayList<>();
			final MobileAutorizacionesList auths = getService().recuperarAutorizaciones(dni.getBytes(DEFAULT_CHARSET));
			for (final MobileAutorizacion mobileAuth : auths.getAutorizacion()) {
				final Authorization auth = new Authorization();
				auth.setId(mobileAuth.getId());
				auth.setType(Authorization.getFormatedType(mobileAuth.getPfAuthorizationType().getCodigo()));
				auth.setUser(buildGenericUser(mobileAuth.getPfUser()));
				auth.setAuthorizedUser(buildGenericUser(mobileAuth.getPfAuthorizedUser()));
				auth.setState(Authorization.getFormatedState(mobileAuth.getEstado()));
				auth.setStartDate(DateTimeFormatter.getSignFolderFormatterInstance().parse(mobileAuth.getFrequest()));
				if (mobileAuth.getFrevocation() != null) {
					auth.setRevocationDate(DateTimeFormatter.getSignFolderFormatterInstance().parse(mobileAuth.getFrevocation()));
				}
				auth.setObservations(mobileAuth.getObservations());
				// Indicamos expresamente si el usuario es quien emitio esa autorizacion
				auth.setSended(dni.equalsIgnoreCase(mobileAuth.getPfUser().getCidentifierDNI()));
				authorizationsList.add(auth);
			}
			response = new ListAuthorizations(authorizationsList);
		} catch (final ParseException e) {
			LOGGER.warn("El formato de las fechas recibidas no es el esperado", e); //$NON-NLS-1$
			return new ListAuthorizations(FireSignResult.ERROR_TYPE_DOCUMENT);
		} catch (final MobileException e) {
			LOGGER.warn("Error durante la validacion de peticiones", e); //$NON-NLS-1$
			return new ListAuthorizations(FireSignResult.ERROR_TYPE_COMMUNICATION);
		}
		return response;
	}

	private static GenericUser buildGenericUser(final MobileUsuarioGenerico user) {
		return new GenericUser(
				user.getId(),
				user.getCidentifierDNI(),
				user.getNombreCompleto());
	}

	/**
	 * Procesa una petici&oacute;n para dar de alta una nueva autorizaci&oacute;n.
	 * @param session Sesi&oacute;n HTTP.
	 * @param xml Petici&oacute;n XML.
	 * @return El resultado del servicio de alta de autorizaci&oacute;n.
	 * @throws IOException Si no es posible remitir la petici&oacute;n.
	 * @throws SAXException Si falla el procesado de la petici&oacute;n.
	 */
	private String processSaveAuthorization(final HttpSession session, final byte[] xml) throws SAXException, IOException {

		final Document doc = parse(xml);

		// Parseamos la peticion
		final Authorization authorization = SaveAuthorizationRequestParser.parse(doc);

		// Establecemos al usuario de la sesion como el emisor de la autorizacion
		final String dni = (String) session.getAttribute(SessionParams.DNI);
		authorization.setUser(new GenericUser(null, dni, null));

		// Guardamos la autorizacion
		final GenericResult result = saveAuthorization(dni, authorization);

		// Construimos la respuesta para la aplicacion movil
		return XmlResponsesFactory.createGenericResponse(result);
	}

	/**
	 * Realiza la llamada al Portafirmas para el alta de una autorizaci&oacute;n de usuario.
	 * @param authorization Autorizaci&oacute;n que dar de alta.
	 * @return Resultado del proceso de alta.
	 */
	private GenericResult saveAuthorization(final String dni, final Authorization authorization) {

		String typeId;
		try {
			typeId = AuthorizationUtils.translateAuthorizationCodeToId(getService(), dni.getBytes(DEFAULT_CHARSET), authorization.getType());
		}
		catch (final IllegalArgumentException e) {
			LOGGER.error("Tipo de autorizacion no soportado", e); //$NON-NLS-1$
			return new GenericResult(GenericResult.ERROR_TYPE_REQUEST);
		}
		catch (final MobileException e) {
			LOGGER.error("Error al acceder al servicio de listado de tipos de autorizacion", e); //$NON-NLS-1$
			return new GenericResult(GenericResult.ERROR_TYPE_COMMUNICATION, e.getMessage());
		}
		catch (final Exception e) {
			LOGGER.error("Error al acceder al servicio de listado de tipos de autorizacion", e); //$NON-NLS-1$
			return new GenericResult(GenericResult.ERROR_TYPE_COMMUNICATION);
		}

		final MobileAutorizacion mobileAuth = new MobileAutorizacion();
		mobileAuth.setId(""); //$NON-NLS-1$

		final MobileTipoGenerico type = new MobileTipoGenerico();
		type.setId(typeId);
		type.setCodigo(authorization.getType());
		mobileAuth.setPfAuthorizationType(type);

		final GenericUser user = authorization.getUser();
		final MobileUsuarioGenerico mobileUser = new MobileUsuarioGenerico();
		mobileUser.setId(user.getId());
		mobileUser.setCidentifierDNI(user.getDni());
		mobileUser.setNombreCompleto(user.getName());
		mobileAuth.setPfUser(mobileUser);

		final GenericUser authUser = authorization.getAuthorizedUser();
		final MobileUsuarioGenerico mobileAuthUser = new MobileUsuarioGenerico();
		mobileAuthUser.setId(authUser.getId());
		mobileAuthUser.setCidentifierDNI(authUser.getDni());
		mobileAuthUser.setNombreCompleto(authUser.getName());
		mobileAuth.setPfAuthorizedUser(mobileAuthUser);

		mobileAuth.setFrequest(DateTimeFormatter.getSignFolderFormatterInstance().format(new Date()));
		if (authorization.getStartDate() != null) {
			mobileAuth.setFauthorization(DateTimeFormatter.getSignFolderFormatterInstance().format(authorization.getStartDate()));
		}
		if (authorization.getRevocationDate() != null) {
			mobileAuth.setFrevocation(DateTimeFormatter.getSignFolderFormatterInstance().format(authorization.getRevocationDate()));
		}

		mobileAuth.setObservations(authorization.getObservations());

		GenericResult result;
		try {
			final String text = getService().salvarAutorizacionMobile(mobileAuth);
			LOGGER.debug("Resultado alta autorizacion: " + text); //$NON-NLS-1$
			result = new GenericResult(true);
		}
		catch (final MobileException e) {
			LOGGER.error("Error al dar de alta la autorizacion", e); //$NON-NLS-1$
			return new GenericResult(GenericResult.ERROR_TYPE_COMMUNICATION, e.getMessage());
		}
		catch (final Exception e) {
			LOGGER.error("Error desconocido al dar de alta la autorizacion", e); //$NON-NLS-1$
			result = new GenericResult(GenericResult.ERROR_TYPE_COMMUNICATION);
		}

		return result;
	}

	/**
	 * Procesa una petici&oacute;n para revocar una autorizaci&oacute;n de usuario.
	 * @param session Sesi&oacute;n HTTP.
	 * @param xml Petici&oacute;n XML.
	 * @return El resultado de revocar la autorizaci&oacute;n.
	 * @throws IOException Si no es posible remitir la petici&oacute;n.
	 * @throws SAXException Si falla el procesado de la petici&oacute;n.
	 */
	private String processRevocateAuthorization(final HttpSession session, final byte[] xml) throws SAXException, IOException {

		final Document doc = parse(xml);

		// Parseamos la peticion
		final String authId = ChangeAuthorizationRequestParser.parse(doc);

		// Recuperamos el DNI del validador
		final String dni = (String) session.getAttribute(SessionParams.DNI);

		// Realizamos el cambio de estado de la autorizacion
		final GenericResult result = changeAuthorizationState(dni, authId, AUTH_ACTION_REVOCATION);

		// Construimos la respuesta para la aplicacion movil
		return XmlResponsesFactory.createGenericResponse(result);
	}

	/**
	 * Cambia el estado de una autorizaci&oacute;n de usuario.
	 * @param dni DNI del usuario que realiza el cambio de estado.
	 * @param authId Identificador de la autorizaci&oacute;n de la que cambiar el estado.
	 * @param action Acci&oacute;n que se desea realizar.
	 * @return
	 */
	private GenericResult changeAuthorizationState(final String dni, final String authId, final String action) {

		GenericResult result;
		try {
			final String text = getService().cambiarEstadoAutorizacionMobile(
					dni.getBytes(DEFAULT_CHARSET), action, authId);
			LOGGER.debug("Resultado del cambio de estado de la autorizacion: " + text); //$NON-NLS-1$
			result = new GenericResult(true);
		}
		catch (final MobileException e) {
			LOGGER.error("Error al cambiar de estos la autorizacion", e); //$NON-NLS-1$
			return new GenericResult(GenericResult.ERROR_TYPE_COMMUNICATION, e.getMessage());
		}
		catch (final Exception e) {
			LOGGER.error("Error desconocido al cambiar de estos la autorizacion", e); //$NON-NLS-1$
			result = new GenericResult(GenericResult.ERROR_TYPE_COMMUNICATION);
		}

		return result;
	}

	/**
	 * Procesa una petici&oacute;n para aceptar una autorizaci&oacute;n de usuario.
	 * @param session Sesi&oacute;n HTTP.
	 * @param xml Petici&oacute;n XML.
	 * @return El resultado de aceptar la autorizaci&oacute;n.
	 * @throws IOException Si no es posible remitir la petici&oacute;n.
	 * @throws SAXException Si falla el procesado de la petici&oacute;n.
	 */
	private String processAcceptAuthorization(final HttpSession session, final byte[] xml) throws SAXException, IOException {

		final Document doc = parse(xml);

		// Parseamos la peticion
		final String authId = ChangeAuthorizationRequestParser.parse(doc);

		// Recuperamos el DNI del validador
		final String dni = (String) session.getAttribute(SessionParams.DNI);

		// Realizamos el cambio de estado de la autorizacion
		final GenericResult result = changeAuthorizationState(dni, authId, AUTH_ACTION_ACCEPT);

		// Construimos la respuesta para la aplicacion movil
		return XmlResponsesFactory.createGenericResponse(result);
	}

	/**
	 * Procesa la petici&oacute;n de listado de validadores.
	 * @param session Sesi&oacute;n HTTP.
	 * @param xml Petici&oacute;n XML.
	 * @return El resultado del servicio de listar los validadores del usuario.
	 * @throws IOException Si no es posible remitir la petici&oacute;n.
	 * @throws SAXException Si falla el procesado de la petici&oacute;n.
	 */
	private String processListValidators(final HttpSession session, final byte[] xml) throws SAXException, IOException {

		final Document doc = parse(xml);

		GenericRequestParser.parse(doc);

		// Recuperamos el DNI del usuario
		final String dni = (String) session.getAttribute(SessionParams.DNI);

		// Listamos los validadores registrados
		final ListValidators validatorsList = listValidators(dni);

		// Construimos la respuesta para la aplicacion movil.
		return XmlResponsesFactory.createListValidatorsResponse(validatorsList);
	}

	/**
	 * Lista las autorizaciones de un usuario.
	 * @param dni DNI del usuario del que se desean obtener las autorizaciones.
	 * @return Resultado con el listado de autorizaciones o el error recibido.
	 */
	private ListValidators listValidators(final String dni) {
		final ListValidators response;
		try {
			final List<Validator> validatorsList = new ArrayList<>();
			final MobileValidadorList validators = getService().recuperarValidadoresMobile(dni.getBytes(DEFAULT_CHARSET));
			for (final MobileValidador mobileValidator : validators.getValidador()) {

				final MobileUsuarioGenerico mobileUser = mobileValidator.getPfValidadorUser();
				final GenericUser user = new GenericUser(
						mobileUser.getId(),
						mobileUser.getCidentifierDNI(),
						mobileUser.getNombreCompleto());

				final String validatorForApps = mobileValidator.getValidadorPorAplicacion();

				final Validator validator = new Validator();
				validator.setUser(user);
				validator.setValidatorForApps(SIGNFOLDER_VALUE_TRUE.equalsIgnoreCase(validatorForApps));

				validatorsList.add(validator);
			}
			response = new ListValidators(validatorsList);
		} catch (final MobileException e) {
			LOGGER.warn("Error durante la validacion de peticiones", e); //$NON-NLS-1$
			return new ListValidators(FireSignResult.ERROR_TYPE_COMMUNICATION);
		}
		return response;
	}

	/**
	 * Procesa una petici&oacute;n para dar de alta un nuevo validador.
	 * @param session Sesi&oacute;n HTTP.
	 * @param xml Petici&oacute;n XML.
	 * @return El resultado del servicio de alta del validador.
	 * @throws IOException Si no es posible remitir la petici&oacute;n.
	 * @throws SAXException Si falla el procesado de la petici&oacute;n.
	 */
	private String processSaveValidator(final HttpSession session, final byte[] xml) throws SAXException, IOException {

		final Document doc = parse(xml);

		// Parseamos la peticion
		final Validator validator = SaveValidatorRequestParser.parse(doc);

		// Recuperamos el DNI del validador y la lista de peticiones a validar.
		final String dni = (String) session.getAttribute(SessionParams.DNI);

		// Lanzamos todas las peticiones de validacion necesarias
		final GenericResult result = saveValidator(dni, validator);

		// Construimos la respuesta para la aplicacion movil
		return XmlResponsesFactory.createGenericResponse(result);
	}

	/**
	 * Realiza la llamada al Portafirmas para asignar un nuevo validador al usuario.
	 * @param dni DNI del usuario que solicita el alta.
	 * @param validator Informaci&oacute;n sobre el validador que dar de alta.
	 * @return Resultado del proceso de alta.
	 */
	private GenericResult saveValidator(final String dni, final Validator validator) {

		final GenericUser user = validator.getUser();

		final MobileUsuarioGenerico mobileUser = new MobileUsuarioGenerico();
		mobileUser.setId(user.getId());
		mobileUser.setCidentifierDNI(user.getDni());
		mobileUser.setNombreCompleto(user.getName());

		final String forApps = validator.isValidatorForApps()
				? SIGNFOLDER_VALUE_TRUE : SIGNFOLDER_VALUE_FALSE;

		final MobileValidador mobileValidador = new MobileValidador();
		mobileValidador.setPfValidadorUser(mobileUser);
		mobileValidador.setValidadorPorAplicacion(forApps);

		GenericResult result;
		try {
			final String text = getService().salvarValidadorMobile(dni.getBytes(DEFAULT_CHARSET), mobileValidador, VALID_ACTION_INSERT);
			LOGGER.debug("Resultado alta validador: " + text); //$NON-NLS-1$
			result = new GenericResult(true);
		}
		catch (final MobileException e) {
			LOGGER.error("Error al dar de alta el validador", e); //$NON-NLS-1$
			return new GenericResult(GenericResult.ERROR_TYPE_COMMUNICATION, e.getMessage());
		}
		catch (final Exception e) {
			LOGGER.error("Error desconocido al dar de alta el validador", e); //$NON-NLS-1$
			result = new GenericResult(GenericResult.ERROR_TYPE_COMMUNICATION);
		}

		return result;
	}

	/**
	 * Procesa una petici&oacute;n para dar de baja un validador.
	 * @param session Sesi&oacute;n HTTP.
	 * @param xml Petici&oacute;n XML.
	 * @return El resultado de dar de baja al validador.
	 * @throws IOException Si no es posible remitir la petici&oacute;n.
	 * @throws SAXException Si falla el procesado de la petici&oacute;n.
	 */
	private String processRevocateValidator(final HttpSession session, final byte[] xml) throws SAXException, IOException {

		final Document doc = parse(xml);

		// Parseamos la peticion
		final String validatorId = RemoveValidatorRequestParser.parse(doc);

		// Recuperamos el DNI del validador
		final String dni = (String) session.getAttribute(SessionParams.DNI);

		// Realizamos la baja del validador
		final GenericResult result = revocateValidator(dni, validatorId);

		// Construimos la respuesta para la aplicacion movil
		return XmlResponsesFactory.createGenericResponse(result);
	}

	/**
	 * Solicita al Portafirmas que de de baja un validador.
	 * @param dni DNI del usuario que realiza el cambio de estado.
	 * @param authId Identificador de la autorizaci&oacute;n de la que cambiar el estado.
	 * @param action Acci&oacute;n que se desea realizar.
	 * @return Resultado de la operaci&oacute;n.
	 */
	private GenericResult revocateValidator(final String dni, final String validatorId) {

		GenericResult result;
		try {
			final String text = getService().eliminarValidadorMobile(dni.getBytes(DEFAULT_CHARSET), validatorId);
			LOGGER.debug("Resultado de la baja del validador: " + text); //$NON-NLS-1$
			result = new GenericResult(true);
		}
		catch (final MobileException e) {
			LOGGER.error("Error al dar de baja el validador", e); //$NON-NLS-1$
			return new GenericResult(GenericResult.ERROR_TYPE_COMMUNICATION, e.getMessage());
		}
		catch (final Exception e) {
			LOGGER.error("Error desconocido al dar de baja el validador", e); //$NON-NLS-1$
			result = new GenericResult(GenericResult.ERROR_TYPE_COMMUNICATION);
		}

		return result;
	}

	// /**
	// * M&eacute;todo encargado de extraer de la petici&oacute;n la lista de aplicaciones
	// * asociadas a la creaci&oacute;n del validador.
	// *
	// * @param xmlDoc
	// * Documento que representa la petici&oacute;n XML.
	// * @return una lista con los identificadores de las aplicaciones a las que
	// * permitir el acceso al validador.
	// */
	// private List<String> getListApps(Document xmlDoc) {
	// List<String> res = null;
	//
	// if (xmlDoc.getElementsByTagName("apps").item(0) != null) {
	//
	// res = new ArrayList<>();
	// NodeList apps = xmlDoc.getElementsByTagName("appId");
	//
	// for (int i = 0; i < apps.getLength(); i++) {
	// res.add(apps.item(i).getTextContent());
	// }
	// }
	//
	// return res;
	// }

	/**
	 * Env&iacute;a a firmar las peticiones cargadas en FIRe.
	 *
	 * @param dni
	 *            DNI del usuario.
	 * @param transactionId
	 *            Identificador de la transacci&oacute;n de FIRe.
	 * @param requestRefs
	 *            Listado de referencias de las peticiones enviadas a firmar.
	 * @return Resultado de la operaci&oacute;n de firma.
	 */
	private FireSignResult fireSign(final String dni, final String transactionId, final String[] requestRefs) {

		final MobileStringList requestList = new MobileStringList();
		final List<String> idRequestList = requestList.getStr();
		for (final String ref : requestRefs) {
			idRequestList.add(ref);
		}

		MobileFireRequestList response;
		try {
			response = getService().signFireCloud(dni.getBytes(DEFAULT_CHARSET), requestList, transactionId);
		} catch (final MobileException e) {
			LOGGER.warn("Error durante la firma de documentos con FIRe", e); //$NON-NLS-1$
			return new FireSignResult(FireSignResult.ERROR_TYPE_COMMUNICATION);
		}

		final FireSignResult result = new FireSignResult(transactionId);

		final List<MobileFireRequest> fireRequestResults = response.getMobileFireRequest();
		for (int i = 0; !result.isError() && i < fireRequestResults.size(); i++) {
			final MobileFireRequest fireRequest = fireRequestResults.get(i);
			if (fireRequest.getErrorPeticion() != null) {
				result.setErrorType(FireSignResult.ERROR_TYPE_REQUEST);
			} else {
				final List<MobileFireDocument> documentResults = fireRequest.getDocumentos()
						.getMobileFireDocumentList();
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
	 * Solicita la configuraci&oacute;n de un determinado usuario.
	 *
	 * @param dni
	 *            Identificador del usuario.
	 * @return Configuraci&oacute;n de usuario.
	 */
	private GetUserConfigResult getUserConfiguration(final String dni) {
		MobileConfiguracionUsuario userConfig;
		try {
			userConfig = getService().configuracionUsuarioMobile(dni.getBytes(DEFAULT_CHARSET));
		} catch (final MobileException e) {
			LOGGER.warn("Error durante la recuperacion de la configuracion de usuario.", e); //$NON-NLS-1$
			return new GetUserConfigResult(GetUserConfigResult.ERROR_TYPE_COMMUNICATION);
		}

		GetUserConfigResult result;
		try {
			final List<GenericFilter> yearsFilters = loadYearsFilters(userConfig);
			final List<GenericFilter> periodsFilters = loadPeriodsFilters(userConfig);
			final List<GenericFilter> applicationsFilters = loadApplicationsFilters(userConfig);
			final List<GenericFilter> typesFilters = loadTypesFilters(userConfig);
			final List<Role> userRoles = loadRoles(userConfig);
			final Boolean hasValidator = loadHasValidator(userConfig);
			final Boolean simConfigured = loadSimConfigurationState(userConfig);
			final Boolean notificationActivated = loadNotificationsState(userConfig);

			result = new GetUserConfigResult();
			result.setYearsFilters(yearsFilters);
			result.setPeriodsFilters(periodsFilters);
			result.setApplicationsFilters(applicationsFilters);
			result.setTypesFilters(typesFilters);
			result.setUserRoles(userRoles);
			result.setValidatorAssigned(hasValidator);
			result.setSimConfigured(simConfigured);
			result.setNotificationActivated(notificationActivated);
		}
		catch (final Exception e) {
			LOGGER.warn("La configuracion de usuario recibida del servidor no es correcta", e); //$NON-NLS-1$
			result = new GetUserConfigResult(GetUserConfigResult.ERROR_TYPE_RESPONSE);
		}

		return result;
	}

	private static List<GenericFilter> loadYearsFilters(final MobileConfiguracionUsuario userConfig) {

		List<GenericFilter> resultFilters = null;

		// Cargamos los filtros de anyos
		final List<MobileFiltroAnioList> filterLists = userConfig.getMobileFiltroAnioList();
		if (filterLists != null && filterLists.size() > 0 && filterLists.get(0) != null) {
			// El campo se llama "Application" por un defecto en el XSD. Su contenido
			// es el anyo.
			final List<MobileFiltroGenerico> filters = filterLists.get(0).getApplication();
			if (filters != null && filters.size() > 0 && filters.get(0) != null) {
				resultFilters = new ArrayList<>();
				for (final MobileFiltroGenerico filter : filters) {
					if (filter.getId() != null) {
						resultFilters.add(new GenericFilter(filter.getId(), filter.getDescripcion()));
					}
				}
			}
		}
		return resultFilters;
	}

	private static List<GenericFilter> loadPeriodsFilters(final MobileConfiguracionUsuario userConfig) {

		List<GenericFilter> resultFilters = null;

		// Cargamos los filtros de anyos
		final List<MobileFiltroMesList> filterLists = userConfig.getMobileFiltroMesList();
		if (filterLists != null && filterLists.size() > 0 && filterLists.get(0) != null) {
			// El campo se llama "Application" por un defecto en el XSD. Su contenido
			// es el periodo de tiempo.
			final List<MobileFiltroGenerico> filters = filterLists.get(0).getApplication();
			if (filters != null && filters.size() > 0 && filters.get(0) != null) {
				resultFilters = new ArrayList<>();
				for (final MobileFiltroGenerico filter : filters) {
					if (filter.getId() != null) {
						resultFilters.add(new GenericFilter(filter.getId(), filter.getDescripcion()));
					}
				}
			}
		}
		return resultFilters;
	}

	private static List<GenericFilter> loadApplicationsFilters(final MobileConfiguracionUsuario userConfig) {

		List<GenericFilter> resultFilters = null;

		// Cargamos los filtros de aplicacion
		final List<MobileApplicationList> filterLists = userConfig.getMobileFiltroApplicationList();
		if (filterLists != null && filterLists.size() > 0 && filterLists.get(0) != null) {
			final List<MobileApplication> filters = filterLists.get(0).getApplication();
			if (filters != null && filters.size() > 0 && filters.get(0) != null) {
				resultFilters = new ArrayList<>();
				for (final MobileApplication filter : filters) {
					if (filter.getId() != null) {
						resultFilters.add(new GenericFilter(filter.getId(), filter.getName()));
					}
				}
			}
		}
		return resultFilters;
	}

	private static List<GenericFilter> loadTypesFilters(final MobileConfiguracionUsuario userConfig) {

		List<GenericFilter> resultFilters = null;

		// Cargamos los filtros de tipo
		final List<MobileFiltroTipoList> filterLists = userConfig.getMobileFiltroTipoList();
		if (filterLists != null && filterLists.size() > 0 && filterLists.get(0) != null) {
			// El campo se llama "Application" por un defecto en el XSD. Su contenido
			// es el periodo de tiempo.
			final List<MobileFiltroGenerico> filters = filterLists.get(0).getApplication();
			if (filters != null && filters.size() > 0 && filters.get(0) != null) {
				resultFilters = new ArrayList<>();
				for (final MobileFiltroGenerico filter : filters) {
					if (filter.getId() != null) {
						resultFilters.add(new GenericFilter(filter.getId(), filter.getDescripcion()));
					}
				}
			}
		}
		return resultFilters;
	}

	private static List<Role> loadRoles(final MobileConfiguracionUsuario userConfig) {

		List<Role> resultRoles = null;

		final List<MobileRoleList> rolesList = userConfig.getRolesList();
		if (rolesList != null && rolesList.size() > 0 && rolesList.get(0) != null) {
			final List<MobileRole> roles = rolesList.get(0).getRole();
			if (roles != null && roles.size() > 0 && roles.get(0) != null) {
				resultRoles = new ArrayList<>();
				for (final MobileRole role : roles) {
					final Role sfRole = new Role(role.getIdRole(), role.getNameRole());
					sfRole.setUserId(role.getDniUsuario());
					sfRole.setUserName(role.getNameUsuario());
					resultRoles.add(sfRole);
				}
			}
		}

		return resultRoles;
	}


	private static Boolean loadHasValidator(final MobileConfiguracionUsuario userConfig) {

		Boolean hasValidator = null;

		final List<String> hasValidatorConfig = userConfig.getUsuarioConValidadores();
		if (hasValidatorConfig != null && hasValidatorConfig.size() > 0) {
			final String config = hasValidatorConfig.get(0);
			if (config != null) {
				hasValidator =  Boolean.valueOf(SIGNFOLDER_VALUE_TRUE.equals(config));
			}
		}
		return hasValidator;
	}

	private static Boolean loadSimConfigurationState(final MobileConfiguracionUsuario userConfig) {

		Boolean simConfigurated = null;

		final List<String> simConfiguratedState = userConfig.getParametrosSIMConfigurados();
		if (simConfiguratedState != null && simConfiguratedState.size() > 0) {
			final String state = simConfiguratedState.get(0);
			if (state != null) {
				simConfigurated =  Boolean.valueOf(SIGNFOLDER_VALUE_TRUE.equals(state));
			}
		}
		return simConfigurated;
	}

	private static Boolean loadNotificationsState(final MobileConfiguracionUsuario userConfig) {

		Boolean notificationsConfigurated = null;

		final List<String> notificationsConfiguratedState = userConfig.getValorNotifyPush();
		if (notificationsConfiguratedState != null && notificationsConfiguratedState.size() > 0) {
			final String state = notificationsConfiguratedState.get(0);
			if (state != null) {
				notificationsConfigurated = Boolean.valueOf(SIGNFOLDER_VALUE_TRUE.equals(state));
			}
		}
		return notificationsConfigurated;
	}

	/**
	 * Genera las prefirmas.
	 *
	 * @param triRequests
	 *            Listado de datos trif&aacute;sicos que prefirmar.
	 */
	private void preSign(final TriphaseRequestBean triRequests) {

		// Prefirmamos cada uno de los documentos de cada una de las peticiones.
		// Si falla la prefirma de
		// un documento, se da por erronea la prefirma de toda la peticion
		final MobileService service = getService();
		for (final TriphaseRequest triRequest : triRequests) {

			// Si esta habilitada la cache, tratamos de recuperar los documentos de ella
			boolean loadedFromCache = false;
			if (this.documentCache != null) {
				try {
					loadDocumentsFromCache(triRequest, false);
					loadedFromCache = true;
					LOGGER.info(" ==== Se han cargado de cache los documentos de la peticion {}", triRequest.getRef()); //$NON-NLS-1$
				}
				catch (final Exception e) {
					LOGGER.debug("No se encontraron en cache todos los documentos de la peticion {}", triRequest.getRef()); //$NON-NLS-1$
				}
			}
			// Si aun no se han cargado los documentos, los descargamos del Portafirmas
			if (!loadedFromCache) {
				try {
					loadDocumentsFromService(triRequest, null, triRequests.getCertificate().getEncoded(), service);
					LOGGER.info(" ==== Se han descargado del Portafirmas los documentos de la peticion {}", triRequest.getRef()); //$NON-NLS-1$
				}
				catch (final Exception e2) {
					LOGGER.warn("Error al cargar los documentos de la peticion " + triRequest.getRef(), e2); //$NON-NLS-1$
					triRequest.setStatusOk(false);
					triRequest.setThrowable(e2);
					continue;
				}
			}

			LOGGER.debug("Prefirma de la peticion: {}", triRequest.getRef()); //$NON-NLS-1$

			try {
				// Prefirmamos cada documento de la peticion
				for (final TriphaseSignDocumentRequest docRequest : triRequest) {

					// Los documentos no deben estar marcados como que necesitan
					// confirmacion del usuario antes de prefirmarse
					docRequest.setNeedConfirmation(false);

					// Prefirmamos
					LOGGER.debug("Procedemos a realizar la prefirma del documento {}", docRequest.getId()); //$NON-NLS-1$
					try {
						TriSigner.doPreSign(docRequest, triRequests.getCertificate(), ConfigManager.getTriphaseServiceUrl(), ConfigManager.getForcedExtraParams());
					}
					catch (final RuntimeConfigNeededException e) {
						LOGGER.warn("Se interrumpe la operacion porque requiere intervencion del usuario: {}", e.getRequestorText()); //$NON-NLS-1$
						docRequest.setNeedConfirmation(true);
						triRequest.addConfirmationRequirement(e.getRequestorText());
					}
				}
			} catch (final Exception e) {
				LOGGER.warn("Error en la prefirma de la peticion " + triRequest.getRef(), e); //$NON-NLS-1$
				if (loadedFromCache) {
					removeFromCache(triRequest);
				}
				triRequest.setStatusOk(false);
				triRequest.setThrowable(e);
				continue;
			}

			// Cacheamos los documentos si a cache esta habilitada y no estan ya cacheados
			if (this.documentCache != null && !loadedFromCache) {
				try {
					saveInCache(triRequest);
					LOGGER.info(" ==== Se guardaron en cache los documentos de la peticion {}", triRequest.getRef()); //$NON-NLS-1$
				}
				catch (final Exception e) {
					LOGGER.warn("No se pudieron cachear los documentos de la peticion {}: {}", triRequest.getRef(), e); //$NON-NLS-1$
				}
			}
		}
	}

	/**
	 * Genera las postfirmas.
	 *
	 * @param triRequests
	 *            Listado de datos trif&aacute;sicos con las prefirmas y firmas
	 *            PKCS#1.
	 * @param service
	 *            Servicio de conexion con el proxy.
	 */
	private void postSign(final TriphaseRequestBean triRequests) {

		final MobileService service = getService();

		// Postfirmamos cada uno de los documentos de cada una de las
		// peticiones. Si falla la
		// postfirma de un solo documento, se da por erronea la postfirma de
		// toda la peticion
		for (final TriphaseRequest triRequest : triRequests) {

			LOGGER.debug("Postfirma de la peticion: {}", triRequest.getRef()); //$NON-NLS-1$

			// Sustituir. Algunos formatos de firma no requeriran que se vuelva
			// a descargar el
			// documento. Solo los descargaremos si es necesario para al menos
			// una de las firmas.

			// Tomamos nota de que firmas requieren el documento original
			final Set<String> requestNeedContent = new HashSet<>();
			for (final TriphaseSignDocumentRequest docRequest : triRequest) {

				final TriphaseData triData = docRequest.getPartialResult();
				if (triData.getSignsCount() > 0 && (!triData.getSign(0).getDict().containsKey(CRYPTO_PARAM_NEED_DATA)
						|| Boolean.parseBoolean(triData.getSign(0).getDict().get(CRYPTO_PARAM_NEED_DATA)))) {
					LOGGER.debug("Se necesitara el documento '" + docRequest.getId() + "' para su uso en la postfirma"); //$NON-NLS-1$ //$NON-NLS-2$
					requestNeedContent.add(docRequest.getId());
				}
			}

			// Obtenemos los documentos originales si los necesitamos
			if (!requestNeedContent.isEmpty()) {
				// Si esta habilitada la cache, tratamos de recuperar los documentos de ella
				boolean loaded = false;
				if (this.documentCache != null) {
					try {
						loadDocumentsFromCache(triRequest, true);
						loaded = true;
						LOGGER.info(" ==== Se han cargado de cache los documentos de la peticion: {}", triRequest.getRef()); //$NON-NLS-1$
					} catch (final Exception e) {
						// No hacemos nada
					}
				}
				// Si aun no se han cargado los documentos, los descargamos del Portafirmas
				if (!loaded) {
					try {
						loadDocumentsFromService(triRequest, requestNeedContent, triRequests.getCertificate().getEncoded(), service);
						LOGGER.info(" ==== Se han descargado del Portafirmas los documentos de la peticion: {}", triRequest.getRef()); //$NON-NLS-1$
					} catch (final Exception e2) {
						LOGGER.warn("Ocurrio un error al cargar los documentos de la peticion {}: {}", triRequest.getRef(), e2); //$NON-NLS-1$
						triRequest.setStatusOk(false);
						triRequest.setThrowable(e2);
						continue;
					}
				}
			}


			// Para cada documento, le asignamos su documento (si es necesario)
			// y lo postfirmamos
			try {
				for (final TriphaseSignDocumentRequest docRequest : triRequest) {
					LOGGER.debug("Procedemos a realizar la postfirma del documento: {}", docRequest.getId()); //$NON-NLS-1$
					TriSigner.doPostSign(docRequest, triRequests.getCertificate(), ConfigManager.getTriphaseServiceUrl(), ConfigManager.getForcedExtraParams());
				}
			} catch (final Exception ex) {
				LOGGER.warn("Ocurrio un error al postfirmar un documento", ex); //$NON-NLS-1$
				triRequest.setStatusOk(false);
				continue;
			}

			LOGGER.debug("Registramos el resultado en el portafirmas"); //$NON-NLS-1$

			// Guardamos las firmas de todos los documentos de cada peticion
			try {
				service.saveSign(triRequests.getCertificate().getEncoded(), triRequest.getRef(),
						transformToWsParams(triRequest));
			} catch (final Exception ex) {
				LOGGER.warn("Ocurrio un error al guardar los documentos de la peticion de firma " + triRequest.getRef(), ex); //$NON-NLS-1$
				triRequest.setStatusOk(false);
			}
		}
	}

	/**
	 * Guarda en cach&eacute; los documentos de una petici&oacute;n concreta y la operaci&oacute;n
	 * criptogr&aacute;fica que realizar sobre cada uno de ellos, ya que &eacute;stas no se
	 * proporcionan junto a la petici&oacute;n de firma, sino al recuperar los documentos.
	 * @param triRequest Petici&oacute;n de firma.
	 * @throws IOException Cuando no se puede guardar alguno de los documentos.
	 */
	private void saveInCache(final TriphaseRequest triRequest) throws IOException {

		final String requestRef = triRequest.getRef();
		for (final TriphaseSignDocumentRequest docRequest : triRequest) {
			final String docId = docRequest.getId();
			final String cop = docRequest.getCryptoOperation();
			final byte[] content = docRequest.getContent();
			this.documentCache.saveDocument(requestRef, docId, cop, content);
			countCacheAccess();
		}
	}

	/**
	 * Elimina de cach&eacute; los documentos de una petici&oacute;n de firma.
	 * @param triRequest Petici&oacute;n de firma.
	 */
	private void removeFromCache(final TriphaseRequest triRequest) {

		final String requestRef = triRequest.getRef();
		for (final TriphaseSignDocumentRequest docRequest : triRequest) {
			final String docId = docRequest.getId();
			try {
				this.documentCache.removeDocument(requestRef, docId);
			}
			catch (final Exception e) {
				LOGGER.warn("No se pudo eliminar un documento de la cache", e); //$NON-NLS-1$
			}
		}
	}

	/**
	 * Carga los documentos de la peticion de cach&eacute;.
	 * @param triRequest Peticion de la que cargar los documentos.
	 * @param delete Indica si se debe eliminar el documento de cach&eacute; tras cargarlo.
	 * @throws IOException Cuando no puede cargar alguno de los documentos.
	 */
	private void loadDocumentsFromCache(final TriphaseRequest triRequest, final boolean delete) throws IOException {

		final String requestRef = triRequest.getRef();
		for (final TriphaseSignDocumentRequest docRequest : triRequest) {
			final String docId = docRequest.getId();
			final CachedDocument document = this.documentCache.loadDocument(requestRef, docId, delete);
			docRequest.setCryptoOperation(document.getCryptoOperation());
			docRequest.setContent(document.getContent());
			countCacheAccess();
		}
	}

	/**
	 * Contabiliza un acceso a la cache y, cuando se alcanza el limite preestablecido,
	 * ejecuta el hilo para su limpieza y reinicia el contador.
	 */
	private void countCacheAccess() {
		synchronized (this.documentCache) {
			this.numCacheRequestToClean--;
			if (this.numCacheRequestToClean <= 0) {
				this.numCacheRequestToClean = this.DEFAULT_CACHE_REQUEST_TO_CLEAN;
				new CacheCleanerThread(this.documentCache, ConfigManager.getCacheExpirationTime()).start();
			}
		}
	}

	/**
	 * Carga el contenido de los documentos de una petici&oacute;n descarg&aacute;ndolos del servicio Portafirmas.
	 * @param triRequest Petici&oacute;n de la que descargar los documentos.
	 * @param requestNeedContent Conjunto de identificadores de los documentos que se necesitan.
	 * @param certEncoded Certificado con el que hacer la petici&oacute;n al sevicio.
	 * @param service Servicio del Portafirmas.
	 * @throws IOException Cuando falla la obtenci&oacute;n de datos del servicio.
	 * @throws MobileException Cuando falla la llamada al servicio.
	 */
	private static void loadDocumentsFromService(final TriphaseRequest triRequest, final Set<String> requestNeedContent,
			final byte[] certEncoded, final MobileService service) throws IOException, MobileException {

		// Descargamos los documentos de la peticion
		final MobileDocumentList downloadedDocs = service.getDocumentsToSign(certEncoded, triRequest.getRef());

		if (downloadedDocs == null) {
			throw new IOException("No se descargaron los documentos de la peticion: " + triRequest.getRef()); //$NON-NLS-1$
		}
		if (triRequest.size() != downloadedDocs.getDocument().size()) {
			throw new IOException("No se han recuperado tantos documentos como los indicados en la peticion " //$NON-NLS-1$
					+ triRequest.getRef());
		}

		// Asociamos el contenido a cada uno de los documentos
		for (final TriphaseSignDocumentRequest docRequest : triRequest) {

			LOGGER.debug("Procesamos documento con el id: " + docRequest.getId()); //$NON-NLS-1$

			// Si no se restringe de que documentos obtener el contenido o si se trata de uno de los
			// documentos que se necesitaban, se asocia el contenido descargado al documento de la peticion
			if (requestNeedContent == null || requestNeedContent.contains(docRequest.getId())) {

				final MobileDocument downloadedDoc = getDownloadedDocument(docRequest.getId(), downloadedDocs);

				if (downloadedDoc == null) {
					throw new IOException("No se encontro entre los documentos descargados aquel con identificador: " + docRequest.getId()); //$NON-NLS-1$
				}

				// Establecemos la operacion
				docRequest.setCryptoOperation(downloadedDoc.getOperationType());

				// Si el documento descargado define la configuracion que se debe aplicar,
				// la sumamos a la configuracion ya establecida para la firma
				// Lo pasamos a base 64 URL_SAFE para que no afecten al envio de datos
				final String downloadedExtraParams = downloadedDoc.getSignatureParameters() != null
						? downloadedDoc.getSignatureParameters().getValue()
						: null;
				if (downloadedExtraParams != null) {
					String params;
					final String currentParamsB64 = docRequest.getParams();
					if (currentParamsB64 != null && !currentParamsB64.trim().isEmpty()) {
						params = new String(Base64.decode(currentParamsB64, true), DEFAULT_CHARSET);
						params += "\n" + downloadedExtraParams; //$NON-NLS-1$
					}
					else {
						params = downloadedExtraParams;
					}
					docRequest.setParams(Base64.encode(params.getBytes(DEFAULT_CHARSET), true));
				}

				// Asociamos el contenido
				final DataHandler dataHandler = downloadedDoc.getData() != null
						? downloadedDoc.getData().getValue() : null;
				if (dataHandler == null) {
					throw new IllegalArgumentException("No se han recuperado los datos del documento"); //$NON-NLS-1$
				}
				final Object content = dataHandler.getContent();
				if (content instanceof InputStream) {
					docRequest.setContent(AOUtil.getDataFromInputStream((InputStream) content));
				} else if (content instanceof String) {
					docRequest.setContent(Base64.decode((String) content));
				} else {
					throw new IOException(
							"El tipo con el que se devuelve el contenido del documento no esta soportado: " //$NON-NLS-1$
							+ (content == null ? null : content.getClass()));
				}
			}
		}
	}

	/**
	 * Obtiene el documento con el identificador indicado de entre un listado de documentos descargados.
	 * @param id Identificador del documento deseado.
	 * @param downloadedDocs Listado de documentos descargados.
	 * @return Documento descargado o {@code null} si no se encontr&oacute;.
	 */
	private static MobileDocument getDownloadedDocument(final String id, final MobileDocumentList downloadedDocs) {
		for (final MobileDocument downloadedDoc : downloadedDocs.getDocument()) {
			if (downloadedDoc.getIdentifier().equals(id)) {
				return downloadedDoc;
			}
		}
		return null;
	}

	/**
	 * Obtiene el servicio para realizar las peticiones al Portafirmas web. Si
	 * se tienen habilitadas las opciones de depuraci&oacute;n, se desactivan
	 * las comprobaciones SSL.
	 *
	 * @return Servicio para la conexi&oacute;n con el Portafirmas web.
	 */
	private MobileService getService() {
		if (DEBUG) {
			disabledSslSecurity();
		}
		final MobileService servicePort = this.mobileService.getMobileServicePort();


		final BindingProvider bindingProvider = (BindingProvider) servicePort;

		if (ConfigManager.getSignfolderUsername() != null &&  ConfigManager.getSignfolderPassword() != null) {
			final Handler handler = new SecurityHandler(ConfigManager.getSignfolderUsername(), ConfigManager.getSignfolderPassword());
			final Binding binding = bindingProvider.getBinding();
			binding.setHandlerChain(Arrays.asList(handler));
		}

		// Definimos las cabeceras de la peticion
		final Map<String, List<String>> requestHeaders;


		final Object headers = bindingProvider.getRequestContext().get(MessageContext.HTTP_REQUEST_HEADERS);
		if (headers != null && headers instanceof Map<?, ?>) {
			requestHeaders = (Map<String, List<String>>) headers;
		}
		else {
			requestHeaders = new HashMap<>();
		}

		// Agrega cabeceras HTTP
		requestHeaders.put("Expect", Arrays.asList("100-Continue")); //$NON-NLS-1$ //$NON-NLS-2$

	    bindingProvider.getRequestContext().put(MessageContext.HTTP_REQUEST_HEADERS, requestHeaders);

		return servicePort;
	}

	/**
	 * Procesa la petici&oacute;n de obtener el estado de las notificaciones
	 * push por parte del cliente m&oacute;vil y devuelve la respuesta recibida
	 * del portafirmas-web.
	 *
	 * @param session
	 *            Sesi&oacute;n establecida con el portafirmas m&oacute;vil.
	 * @param xml
	 *            XML con los datos necesarios para la consulta.
	 * @return XML con la respuesta del servicio.
	 * @throws SAXException
	 *             Cuando ocurre alg&uacute;n error al procesar los XML.
	 * @throws IOException
	 *             Cuando ocurre alg&uacute;n problema de comunicaci&oacute;n
	 *             con el servidor.
	 * @throws MobileException
	 *             Cuando ocurre alg&uacute;n problema con el servicio.
	 */
	private String processGetPushStatus(final HttpSession session, final byte[] xml)
			throws SAXException, IOException, MobileException {
		final Document xmlDoc = parse(xml);

		// Comprobamos que el XML de peticion esta bien formado.
		GetPushStatusParser.parse(xmlDoc);

		// Recuperamos el DNI del usuario.
		final String dni = (String) session.getAttribute(SessionParams.DNI);

		final EstadoNotifyPushResponse response = new EstadoNotifyPushResponse();
		response.setValorNotifyPush(getService().estadoNotifyPush(dni.getBytes(DEFAULT_CHARSET)));

		// Construimos la respuesta para la aplicacion movil.
		return XmlResponsesFactory.createGetPushStatusResponse(response);
	}

	/**
	 * Procesa la petici&oacute;n de actualizar el estado de las notificaciones
	 * push por parte del cliente m&oacute;vil y devuelve la respuesta recibida
	 * del portafirmas-web.
	 *
	 * @param session
	 *            Sesi&oacute;n establecida con el portafirmas m&oacute;vil.
	 * @param xml
	 *            XML con los datos necesarios para la consulta.
	 * @return XML con la respuesta del servicio.
	 * @throws SAXException
	 *             Cuando ocurre alg&uacute;n error al procesar los XML.
	 * @throws IOException
	 *             Cuando ocurre alg&uacute;n problema de comunicaci&oacute;n
	 *             con el servidor.
	 * @throws MobileException
	 *             Cuando ocurre alg&uacute;n problema con el servicio.
	 */
	private String processUpdatePushStatus(final HttpSession session, final byte[] xml)
			throws SAXException, IOException, MobileException {
		final Document xmlDoc = parse(xml);

		// Comprobamos que el XML de peticion esta bien formado y obtenemos la cadena
		// que determina si las notificaciones deben habilitarse o deshabilitarse
		final boolean enable = UpdatePushStatusParser.parse(xmlDoc);

		// Recuperamos el DNI del usuario y el nuevo estado de las
		// notificaciones push.
		final String dni = (String) session.getAttribute(SessionParams.DNI);
		final String enableRequest = enable ? "S" : "N"; //$NON-NLS-1$ //$NON-NLS-2$

		// Realizamos la llamada.
		final UpdateNotifyPushResponse response = new UpdateNotifyPushResponse();
		response.setResultado(getService().updateNotifyPush(dni.getBytes(DEFAULT_CHARSET), enableRequest));

		return XmlResponsesFactory.createUpdatePushStatusResponse(response);
	}

	/**
	 * Permite enviar respuestas al cliente de un servicio.
	 */
	private final class Responser {

		final HttpServletResponse response;

		/**
		 * Crea el Responser enlaz&aacute;ndolo con una petici&oacute;n concreta
		 * al servicio.
		 *
		 * @param response
		 *            Manejador para el env&iacute;o de la respuesta.
		 */
		public Responser(final HttpServletResponse response) {
			this.response = response;
		}

		/**
		 * Imprime una respuesta en la salida del servicio y cierra el flujo.
		 *
		 * @param message
		 *            Mensaje que imprimir como respuesta.
		 */
		public void print(final String message) {
			try (final OutputStream os = this.response.getOutputStream();) {
				os.write(message.getBytes(DEFAULT_CHARSET));
			} catch (final Exception e) {
				LOGGER.error("Error al devolver el resultado al cliente a traves del metodo print", e); //$NON-NLS-1$
			}
		}

		public void write(final InputStream message) {
			int n = -1;
			final byte[] buffer = new byte[1024];
			try (final OutputStream os = this.response.getOutputStream();) {
				while ((n = message.read(buffer)) > 0) {
					os.write(buffer, 0, n);
				}
			} catch (final Exception e) {
				LOGGER.error("Error al devolver el resultado al cliente a traves del metodo write", e); //$NON-NLS-1$
			}
		}
	}

	/**
	 * Hilo para la limpieza de la cach&eacute;.
	 */
	class CacheCleanerThread extends Thread {

		private final Logger LOGGER_THREAD = LoggerFactory.getLogger(CacheCleanerThread.class);

		private final DocumentCache cache;
		private final long expirationTime;

		/**
		 * Crea un hilo para le borrado de los ficheros de sesi&oacute;n caducados.
		 * @param dir Directorio con los ficheros de sesion.
		 * @param soft Indica si se desea una limpieza cuidadosa {@code true}}
		 */
		public CacheCleanerThread(final DocumentCache cache, final long expirationTime) {
			this.cache = cache;
			this.expirationTime = expirationTime;
		}

		@Override
		public void run() {

			this.LOGGER_THREAD.debug("Se inicia el hilo de limpieza de la cache"); //$NON-NLS-1$

			this.cache.cleanExpiredFiles(this.expirationTime);
		}
	}
}
