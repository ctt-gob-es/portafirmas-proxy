package es.gob.afirma.signfolder.server.proxy;


import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Manejador que gestiona la configuraci&oacute;n de la aplicaci&oacute;n.
 */
public class ConfigManager {

	private static final Logger LOGGER = Logger.getLogger(ConfigManager.class.getName());

	/** Nombre del fichero de configuraci&oacute;n. */
	private static final String CONFIG_FILE = "pfmovil.properties" ; //$NON-NLS-1$

	/** Variable de entorno que determina el directorio en el que buscar el fichero de configuraci&oacute;n. */
	private static final String ENVIRONMENT_VAR_CONFIG_DIR = "pfmovil.config.path"; //$NON-NLS-1$

	private static final String JAVA_HTTP_PORT_VARIABLE = "tomcat.httpport"; //$NON-NLS-1$
	private static final String TOMCAT_HTTP_PORT_VARIABLE = "${" + JAVA_HTTP_PORT_VARIABLE + "}"; //$NON-NLS-1$ //$NON-NLS-2$

	/** Propiedad que establece los extraparams que deben forzarse en las operaciones de firma. */
	private static final String PROPERTY_FORCED_EXTRAPARAMS = "forced.extraparams"; //$NON-NLS-1$

	/** Propiedad que establece la ruta del directorio temporal. */
	private static final String PROPERTY_TEMP_DIR = "temp.dir"; //$NON-NLS-1$

	/** Propiedad que establece el endpoint del servicio del Portafirmas. */
	private static final String PROPERTY_SIGNFOLDER_URL = "signfolder.ws.url"; //$NON-NLS-1$

	/** Propiedad que establece la URL del servicio de firma trif&aacute;sica. */
	private static final String PROPERTY_TRIPHASE_SERVICE_URL = "triphase.server.url"; //$NON-NLS-1$

	private static Properties config = null;

	/**
	 * Carga el fichero de configuraci&oacute;n del m&oacute;dulo.
	 */
	private static void loadConfig() {

		if (config != null) {
			return;
		}

		InputStream is = null;
		try {
			String configDir = System.getProperty(ENVIRONMENT_VAR_CONFIG_DIR);
			if (configDir == null) {
				configDir = System.getenv(ENVIRONMENT_VAR_CONFIG_DIR);
			}
			if (configDir != null) {
				final File configFile = new File(configDir, CONFIG_FILE).getCanonicalFile();
				if (!configFile.isFile() || !configFile.canRead()) {
					LOGGER.warning(
							"No se encontro el fichero " + CONFIG_FILE + " en el directorio configurado en la variable " + //$NON-NLS-1$ //$NON-NLS-2$
									ENVIRONMENT_VAR_CONFIG_DIR + ": " + configFile.getAbsolutePath() + //$NON-NLS-1$
									"\nSe buscara en el CLASSPATH."); //$NON-NLS-1$
				}
				else {
					is = new FileInputStream(configFile);
				}
			}

			if (is == null) {
				is = ConfigManager.class.getResourceAsStream('/' + CONFIG_FILE);
			}

			config = new Properties();
			config.load(is);
			is.close();
		}
		catch(final NullPointerException e){
			LOGGER.severe("No se ha encontrado el fichero de configuracion: " + e); //$NON-NLS-1$
			if (is != null) {
				try { is.close(); } catch (final Exception ex) { /* No hacemos nada */ }
			}
			throw new RuntimeException("No se ha encontrado el fichero de propiedades " + CONFIG_FILE, e); //$NON-NLS-1$
		}
		catch (final Exception e) {
			LOGGER.severe("No se pudo cargar el fichero de configuracion " + CONFIG_FILE); //$NON-NLS-1$
			if (is != null) {
				try { is.close(); } catch (final Exception ex) { /* No hacemos nada */ }
			}
			throw new RuntimeException("No se pudo cargar el fichero de configuracion " + CONFIG_FILE, e); //$NON-NLS-1$
		}
		finally {
			if (is != null) {
				try { is.close(); } catch (final Exception ex) { /* No hacemos nada */ }
			}
		}
	}

	/**
	 * Lanza una excepci&oacute;n en caso de que no encuentre el fichero de configuraci&oacute;n.
	 */
	public static void checkInitialized() {

		loadConfig();

		if (config == null) {
			LOGGER.severe("No se ha encontrado el fichero de configuracion de la conexion"); //$NON-NLS-1$
			throw new RuntimeException("No se ha encontrado el fichero de configuracion de la conexion"); //$NON-NLS-1$
		}

		try {
			if (getSignfolderUrl() == null) {
				LOGGER.severe("No se ha establecido la URL del servicio del Portafirmas"); //$NON-NLS-1$
				throw new RuntimeException("No se ha establecido la URL del servicio del Portafirmas"); //$NON-NLS-1$
			}
		} catch (final Exception e) {
			LOGGER.severe("La URL del servicio del Portafirmas no esta bien formada"); //$NON-NLS-1$
			throw new RuntimeException("La URL del servicio del Portafirmas no esta bien formada"); //$NON-NLS-1$
		}

		if (getTriphaseServiceUrl() == null) {
			LOGGER.severe("No se ha establecido la URL del servicio de firma trifasica"); //$NON-NLS-1$
			throw new RuntimeException("No se ha establecido la URL del servicio de firma trifasica"); //$NON-NLS-1$
		}
	}

	/**
	 * Devuelve la URL del servicio del portafirmas m&oacute;vil.
	 * @return URL del servicio del portafirmas m&oacute;vil o {@code null} si no se ha establecido.
	 */
	public static URL getSignfolderUrl() {
		if (!config.containsKey(PROPERTY_SIGNFOLDER_URL)) {
			return null;
		}
		try {
			return new URL(config.getProperty(PROPERTY_SIGNFOLDER_URL));
		}
		catch (final Exception e) {
			throw new RuntimeException("La URL del servicio del portafirmas no esta bien formada", e); //$NON-NLS-1$
		}
	}

	/**
	 * Devuelve la URL del servicio de firma trif&aacute;sica.
	 * @return URL del servicio de firma trif&aacute;sica o {@code null} si no se ha establecido.
	 */
	public static String getTriphaseServiceUrl(){
		String url = config.getProperty(PROPERTY_TRIPHASE_SERVICE_URL);
		if (url.contains(TOMCAT_HTTP_PORT_VARIABLE)) {

			final String configuredPort = System.getProperty(JAVA_HTTP_PORT_VARIABLE);
			if (configuredPort == null) {
				LOGGER.severe("Se ha utilizado la expresion de configuracion del puerto del servidor y no se ha encontrado en el sistema la definicion de la variable: " + JAVA_HTTP_PORT_VARIABLE); //$NON-NLS-1$
				throw new RuntimeException("Se ha utilizado la expresion de configuracion del puerto del servidor y no se ha encontrado en el sistema la definicion de la variable: " + JAVA_HTTP_PORT_VARIABLE); //$NON-NLS-1$
			}
			url = url.replace(TOMCAT_HTTP_PORT_VARIABLE, configuredPort);
		}
		return url;
	}

	/**
	 * Devuelve la URL del servicio de firma trif&aacute;sica.
	 * @param triphaseServiceUrl URL del servicio de firma trif&aacute;sica.
	 */
	public static void setTriphaseServiceUrl(final String triphaseServiceUrl){
		config.setProperty(PROPERTY_TRIPHASE_SERVICE_URL, triphaseServiceUrl);
	}

	/**
	 * Devuelve la cadena de configuraci&oacute;n con los ExtraParams que deben
	 * aplicarse a todas las operaciones de firma.
	 * @return Cadena con los ExtraParams en forma clave=valor y separados por '\n'.
	 */
	public static String getForcedExtraParams(){
		return config.getProperty(PROPERTY_FORCED_EXTRAPARAMS);
	}

	/**
	 * Devuelve el directorio temporal establecido en el fichero de
	 * configuraci&oacute;n.
	 * @return Ruta del directorio temporal o {@code null} si no se
	 * estableci&oacute;.
	 */
	public static String getTempDir(){
		return config.getProperty(PROPERTY_TEMP_DIR);
	}
}
