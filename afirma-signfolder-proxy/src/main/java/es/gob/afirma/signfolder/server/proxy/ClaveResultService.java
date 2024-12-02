package es.gob.afirma.signfolder.server.proxy;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.ws.Binding;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.gob.afirma.signfolder.client.MobileError;
import es.gob.afirma.signfolder.client.MobileException;
import es.gob.afirma.signfolder.client.MobileService;
import es.gob.afirma.signfolder.client.MobileService_Service;
import es.gob.afirma.signfolder.server.proxy.sessions.SessionCollector;
import es.gob.afirma.signfolder.soap.security.SecurityHandler;

/**
 * Servicio para la obtenci&oacute;n del resultado del inicio de sesi&oacute;n con Cl@ve.
 */
public class ClaveResultService extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.getLogger(ClaveResultService.class);

	private static final String REQUEST_PARAM_RESPONSE = "SAMLResponse"; //$NON-NLS-1$
	private static final String REQUEST_PARAM_SHARED_SESSION_ID = "ssid"; //$NON-NLS-1$

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ClaveResultService() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {

		LOGGER.debug("Se recibe la respuesta de Cl@ve"); //$NON-NLS-1$

		final String sessionId = request.getParameter(REQUEST_PARAM_SHARED_SESSION_ID);
		final HttpSession session = SessionCollector.getSession(request, sessionId);
		if (session == null) {
			LOGGER.warn("Error {}: Se intenta acceder a traves del servicio de resultado de Clave sin iniciar sesion", OperationError.LOGIN_CLAVE_EXPIRED.getCode()); //$NON-NLS-1$
			response.sendRedirect("error.jsp?code=" + OperationError.LOGIN_CLAVE_EXPIRED.getCode()); //$NON-NLS-1$
			return;
		}

		if (session.getAttribute(SessionParams.INIT_WITH_CLAVE) == null) {
			LOGGER.warn("Error {}: Se intenta acceder a traves del servicio de resultado de Clave sin iniciar sesion con Cl@ve", OperationError.LOGIN_CLAVE_EXPIRED.getCode()); //$NON-NLS-1$
			SessionCollector.removeSession(session);
			response.sendRedirect("error.jsp?code=" + OperationError.LOGIN_CLAVE_EXPIRED.getCode()); //$NON-NLS-1$
			return;
		}

		// Obtenemos el parametro con la respuesta SAML
		final String samlResponse = request.getParameter(REQUEST_PARAM_RESPONSE);

		// Conectamos con el Portafirmas web para descifrarla
		final MobileService service = new MobileService_Service(ConfigManager.getSignfolderUrl()).getMobileServicePort();

		if (ConfigManager.getSignfolderUsername() != null &&  ConfigManager.getSignfolderPassword() != null) {
			addSecuityHeaders(service);
		}

		String dni;
		try {
			dni = service.procesarRespuestaClave(samlResponse, "https://www.prueba.es");	// XXX: Esta URL ahora carece de utilidad //$NON-NLS-1$
		} catch (final MobileException e) {
			LOGGER.warn("Error al solicitar al Portafirmas que procese la respuesta SAML de Clave", e); //$NON-NLS-1$
			SessionCollector.removeSession(session);
			final OperationError error = identifyError(e);
			LOGGER.warn("Error identificado a partir de la respuesta del Portafirmas: {}", error.getCode()); //$NON-NLS-1$
			response.sendRedirect("error.jsp?code=" + error.getCode()); //$NON-NLS-1$
			return;
		}

		LOGGER.debug("DNI del usuario logueado: " + dni); //$NON-NLS-1$

		// Se guarda el DNI en sesion para realizar peticiones
		session.setAttribute(SessionParams.DNI, dni);
		session.setAttribute(SessionParams.VALID_SESSION, Boolean.TRUE.toString());

		// Se eliminan atributos de sesion innecesarios
		session.removeAttribute(SessionParams.INIT_WITH_CLAVE);
		session.removeAttribute(SessionParams.CLAVE_REQUEST_TOKEN);
		session.removeAttribute(SessionParams.CLAVE_EXCLUDED_IDPS);
		session.removeAttribute(SessionParams.CLAVE_FORCED_IDP);

		SessionCollector.updateSession(session);

		response.sendRedirect("ok.jsp?dni=" + dni); //$NON-NLS-1$
	}

	/**
	 * Identifica el tipo de error producido a partir de la excepci&oacute;n devuelta por el
	 * servicio de an&aacute;lisis del SAML de Cl@ve. Se acepta cualquier error devuelto por el
	 * Portafirmas, pero deber&iacute;an limitarse a:
	 * <ul>
	 *  <li>COD_015: No se ha podido validar el token de la SAML Response.</li>
	 *  <li>COD_016: El token indica que el usuario no es válido, las credenciales son erróneas.</li>
	 *  <li>COD_017: El usuario no autenticado en Portafirmas.</li>
	 *  <li>COD_018: El campo SAML es nulo.</li>
	 *  <li>COD_000: Cualquier otro error. (este se traducira al COD_101)</li>
	 * </ul>
	 * @param pfException Excepci&oacute;n devuelta por el servicio de procesarRespuestaClave.
	 * @return Error identificado.
	 */
	private static OperationError identifyError(final MobileException pfException) {
		OperationError error = OperationError.LOGIN_CLAVE_INTERNAL_ERROR;
		final MobileError faultInfo = pfException.getFaultInfo();
		if (faultInfo != null) {
			final String code = faultInfo.getCode();
			final String msg = faultInfo.getMessage();
			LOGGER.warn("Error extraido del servicio de procesamiento de la respuesta de Cl@ve: {}: {}", code, msg); //$NON-NLS-1$
			switch (faultInfo.getCode()) {
			case "COD_015": //$NON-NLS-1$
			case "COD_016": //$NON-NLS-1$
			case "COD_018": //$NON-NLS-1$
			case "COD_000": //$NON-NLS-1$
				error = OperationError.LOGIN_CLAVE_INTERNAL_ERROR;
				break;
			case "COD_017": //$NON-NLS-1$
				error = OperationError.LOGIN_CLAVE_UNKNOWN_USER;
				break;
			default:
				error = OperationError.LOGIN_CLAVE_INTERNAL_ERROR;
				break;
			}
		}
		return error;
	}


	/**
	 * Agrega las cabeceras de seguridad para la conexi&oacute;n con los
	 * servicios web del Portafirmas.
	 * @param servicePort Puerto para el envio de las peticiones al
	 * portafirmas.
	 */
	private static void addSecuityHeaders(final MobileService servicePort) {

		final BindingProvider bindingProvider = (BindingProvider) servicePort;

		final Handler handler = new SecurityHandler(ConfigManager.getSignfolderUsername(), ConfigManager.getSignfolderPassword());

        final Binding binding = bindingProvider.getBinding();
        binding.setHandlerChain(Arrays.asList(handler));
	}
}
