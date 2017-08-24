package es.gob.afirma.signfolder.server.proxy;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import es.gob.afirma.core.misc.AOUtil;

/**
 * Servicio para la obtenci&oacute;n de la versi&oacute;n del servicio Proxy.
 */
public class VersionService extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = Logger.getLogger("es.gob.afirma"); //$NON-NLS-1$

    /**
     * @see HttpServlet#HttpServlet()
     */
    public VersionService() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    	LOGGER.info("Solicitud de la version del Proxy"); //$NON-NLS-1$
    	String version;
    	try {
    		version = new String(
    				AOUtil.getDataFromInputStream(
    						VersionService.class.getResourceAsStream("/version"))); //$NON-NLS-1$
    	}
    	catch (final Exception e) {
    		LOGGER.warning("No se pudo obtener la version del proxy"); //$NON-NLS-1$
    		version = "No se pudo obtener la version del proxy"; //$NON-NLS-1$
    	}

    	response.getWriter().println(version);
    	response.flushBuffer();
    }
}
