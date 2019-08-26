package es.gob.afirma.signfolder.server.proxy;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import es.gob.afirma.signfolder.client.MobileException;
import es.gob.afirma.signfolder.client.MobileService;
import es.gob.afirma.signfolder.client.MobileService_Service;

/**
 * Servicio para la obtenci&oacute;n del resultado del inicio de sesi&oacute;n con Cl@ve.
 */
public class ClaveResultService extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = Logger.getLogger(ClaveResultService.class.getName());

	private static final String REQUEST_PARAM_RESPONSE = "SAMLResponse"; //$NON-NLS-1$

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

		final HttpSession session = request.getSession(false);
		if (session == null) {
			LOGGER.warning("Se intenta acceder a traves del servicio de resultado de Clave sin iniciar sesion"); //$NON-NLS-1$
			response.sendRedirect("error.jsp?type=session"); //$NON-NLS-1$
			return;
		}

		if (session.getAttribute(SessionParams.INIT_WITH_CLAVE) == null) {
			LOGGER.warning("Se intenta acceder a traves del servicio de resultado de Clave sin iniciar sesion con Clave"); //$NON-NLS-1$
			session.invalidate();
			response.sendRedirect("error.jsp?type=session"); //$NON-NLS-1$
			return;
		}

		// Obtenemos el parametro con la respuesta SAML
		final String samlResponse = request.getParameter(REQUEST_PARAM_RESPONSE);

		// Conectamos con el Portafirmas web para descifrarla
		final MobileService service = new MobileService_Service(ConfigManager.getSignfolderUrl()).getMobileServicePort();
		String dni;
		try {
			dni = service.procesarRespuestaClave(samlResponse, "https://www.google.es");	// XXX: Esta URL carece de utilidad
		} catch (final MobileException e) {
			session.invalidate();
			response.sendRedirect("error.jsp?type=validation"); //$NON-NLS-1$
			return;
		}

		// Se guarda el DNI en sesion para realizar peticiones
		session.setAttribute(SessionParams.DNI, dni);
		session.setAttribute(SessionParams.VALID_SESSION, Boolean.TRUE.toString());
		session.removeAttribute(SessionParams.INIT_WITH_CLAVE);

		response.sendRedirect("ok.jsp?dni=" + dni); //$NON-NLS-1$
	}
}
