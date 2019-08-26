package es.gob.afirma.signfolder.server.proxy;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servicio para obtener los datos de sesion de
 */
public class ClaveLoginService extends HttpServlet {

	/** Serial Id. */
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = Logger.getLogger(ClaveLoginService.class.getName());

	private static final String PARAM_ID = "id"; //$NON-NLS-1$

	@Override
	protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {

		// Comprobamos el tener sesion
		final HttpSession session = req.getSession(false);
		if (session == null) {
			LOGGER.warning("Se intenta acceder a traves del servicio de resultado de Cl@ve sin iniciar sesion"); //$NON-NLS-1$
			resp.sendRedirect("error.jsp?type=session"); //$NON-NLS-1$
			return;
		}

		// Comprobamos el que no se haya informado de un error en el login
		final boolean success = Boolean.parseBoolean(req.getParameter("r")); //$NON-NLS-1$
		if (!success) {
			final String errorType = req.getParameter("type"); //$NON-NLS-1$
			LOGGER.warning("Error de acceso a traves de clave de tipo: " + errorType); //$NON-NLS-1$
			session.invalidate();
			resp.sendRedirect("error.jsp?type=" + errorType); //$NON-NLS-1$
			return;
		}

		// Comprobamos que se solicitase el inicio de sesion con Cl@ve
		if (session.getAttribute(SessionParams.INIT_WITH_CLAVE) == null) {
			LOGGER.warning("Se intenta acceder a traves del servicio Cl@ve sin llamar al metodo de inicio de sesion"); //$NON-NLS-1$
			session.invalidate();
			resp.sendRedirect("error.jsp?type=session"); //$NON-NLS-1$
			return;
		}

		// Comprobamos que se haya proporcionado el identificador de inicializacion y que sea valido
		final String id = req.getParameter(PARAM_ID);
		if (id == null || id.isEmpty()) {
	 		LOGGER.warning("No se proporciono el identificador de inicializacion"); //$NON-NLS-1$
	 		session.invalidate();
	 		resp.sendRedirect("error.jsp?type=session"); //$NON-NLS-1$
			return;
		}

		if (!id.equals(session.getAttribute(SessionParams.CLAVE_AUTHENTICATION_ID))) {
			LOGGER.warning("El identificador de autenticacion proporcionado no coincide con el de la sesion"); //$NON-NLS-1$
			session.invalidate();
			resp.sendRedirect("error.jsp?type=session"); //$NON-NLS-1$
			return;
		}

		// Componemos la llamada a Cl@ve
		final String url = (String) session.getAttribute(SessionParams.CLAVE_URL);

		resp.sendRedirect(url);
	}
}
