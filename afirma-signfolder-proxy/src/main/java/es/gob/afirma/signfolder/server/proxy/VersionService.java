package es.gob.afirma.signfolder.server.proxy;

import java.io.IOException;
import java.util.logging.Level;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.gob.afirma.core.misc.AOUtil;

/**
 * Servicio para la obtenci&oacute;n de la versi&oacute;n del servicio Proxy.
 */
public class VersionService extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.getLogger(VersionService.class);

	private static final String VERSION_SEPARATOR = ":"; //$NON-NLS-1$

	private static String version = null;

	private static String versionCode = null;

	static {
		// Configuramos el manejador de log, redirigiendo los logs de Java (JUL) a SLF4J
		try {
			java.util.logging.Logger.getLogger("es.gob.afirma").setLevel(Level.INFO); //$NON-NLS-1$
		}
		catch (final Exception e) {
			// No hacemos nada
			LOGGER.warn("No se ha podido redirigir los logs de Java a SLF4J", e); //$NON-NLS-1$
		}
	}

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
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {

    	LOGGER.info("Solicitud de la version del Proxy"); //$NON-NLS-1$

    	final String result = getVersion();

    	response.getWriter().println(result);
    	response.flushBuffer();
    }

    private static void loadVersions() {
    	try {
    		final String versionData = new String(
    				AOUtil.getDataFromInputStream(
    						VersionService.class.getResourceAsStream("/version"))); //$NON-NLS-1$
    		final String[] versions = versionData.split(VERSION_SEPARATOR);
    		versionCode = versions[0];
    		version = versions[1];
    	}
    	catch (final Exception e) {
    		LOGGER.warn("No se ha podido identificar la version del servicio", e); //$NON-NLS-1$
    		versionCode = "0"; //$NON-NLS-1$
    		version = "0"; //$NON-NLS-1$
		}
    }

    /**
     * Obtiene el texto de versi&oacute;n del servicio.
     * @return Texto de versi&oacute;n.
     */
    static String getVersion() {
    	if (version == null) {
    		loadVersions();
    	}
    	return version;
    }

    /**
     * Obtiene el c&oacute;digo de versi&oacute;n del servicio.
     * @return C&oacute;digo de versi&oacute;n.
     */
    static String getVersionCode() {
    	if (versionCode == null) {
    		loadVersions();
    	}
    	return versionCode;
    }

}
