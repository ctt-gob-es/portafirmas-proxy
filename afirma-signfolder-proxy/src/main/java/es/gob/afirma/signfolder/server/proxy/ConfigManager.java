package es.gob.afirma.signfolder.server.proxy;


import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manejador que gestiona la configuraci&oacute;n de la aplicaci&oacute;n.
 */
public class ConfigManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigManager.class);

	/** Nombre del fichero de configuraci&oacute;n. */
	private static final String CONFIG_FILE = "pfmovil.properties" ; //$NON-NLS-1$

	/** Variable de entorno que determina el directorio en el que buscar el fichero de configuraci&oacute;n. */
	private static final String ENVIRONMENT_VAR_CONFIG_DIR = "pfmovil.config.path"; //$NON-NLS-1$

	private static final String SYS_PROP_PREFIX = "${"; //$NON-NLS-1$

	private static final String SYS_PROP_SUFIX = "}"; //$NON-NLS-1$

	/** Propiedad que establece los extraparams que deben forzarse en las operaciones de firma. */
	private static final String PROPERTY_FORCED_EXTRAPARAMS = "forced.extraparams"; //$NON-NLS-1$

	/** Propiedad que establece la ruta del directorio temporal. */
	private static final String PROPERTY_PROXY_BASE_URL = "proxy.server.url"; //$NON-NLS-1$

	/** Propiedad que establece el endpoint del servicio del Portafirmas. */
	private static final String PROPERTY_SIGNFOLDER_URL = "signfolder.ws.url"; //$NON-NLS-1$

	/** Propiedad que establece el usuario para el acceso al servicio de portafirmas. */
	private static final String PROPERTY_SIGNFOLDER_USERNAME = "signfolder.ws.username"; //$NON-NLS-1$

	/** Propiedad que establece la contrase&ntilde;a para el acceso al servicio de portafirmas. */
	private static final String PROPERTY_SIGNFOLDER_PASSWORD = "signfolder.ws.password"; //$NON-NLS-1$

	/** Propiedad que establece si deben compartirse las sesiones. */
	private static final String PROPERTY_SHARED_ENABLED = "share.sessions.enable"; //$NON-NLS-1$

	/** Propiedad que establece si deben compartirse las sesiones cuando usan certificado local. */
	private static final String PROPERTY_SHARED_WITH_CERT_ENABLED = "share.sessions.withcertificate.enable"; //$NON-NLS-1$

	/** Propiedad que establece la ruta del directorio en el que almacenar la informaci&oacute;n de
	 * las sesiones compartidas. */
	private static final String PROPERTY_SHARED_DIR = "share.sessions.dir"; //$NON-NLS-1$

	/** Propiedad que establece el n&uacute;mero de accesos que se pueden realizar al directorio de
	 * sesiones compartidas antes de lanzar el proceso de limpieza. */
	private static final String PROPERTY_REQUESTS_TO_CLEAN = "share.sessions.requeststoclean"; //$NON-NLS-1$

	/** Propiedad que establece si se debe utilizar cach&eacute; para el guardado temporal de los
	 * documentos del Portafirmas durante el proceso de firma. */
	private static final String PROPERTY_CACHE_ENABLED = "cache.enabled"; //$NON-NLS-1$

	/** Propiedad que establece el nombre de la clase que gestiona la cach&eacute; temporal para el
	 * guardado de los documentos Portafirmas durante el proceso de firma. */
	private static final String PROPERTY_CACHE_SYSTEM_CLASS = "cache.system.class"; //$NON-NLS-1$

	/** Propiedad que establece el numero de milisegundos a partir del cual se considera caducado
	 * un documento en cache. */
	private static final String PROPERTY_CACHE_SYSTEM_EXPIRATION_TIME = "cache.system.expirationtime"; //$NON-NLS-1$

	/** Propiedad que indica el n&uacute;mero m&aacute;ximo de p&aacute;ginas para comprobar un posible PDF Shadow Attack */
	private static final String PROPERTY_MAX_PAGES_TO_CHECK_PSA = "maxPagesToCheckShadowAttack"; //$NON-NLS-1$

	/** N&uacute;mero de accesos que, por defecto, se pueden realizar al directorio de sesiones
	 * compartidas antes de lanzar el proceso de limpieza. */
	private static final int DEFAULT_VALUE_REQUESTS_TO_CLEAN = 1000;

	/** N&uacute;mero de milisegundos que por defecto tardan en caducar los ficheros en cache. */
	private static final int DEFAULT_EXPIRATION_TIME = 1000;

	/** N&uacute;mero de p&aacute;ginas por defecto en las que comprobar el PSA. */
	private static final int DEFAULT_MAX_PAGES_TO_CHECK_PSA = 10;

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
					LOGGER.warn(
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
			LOGGER.error("No se ha encontrado el fichero de configuracion: " + e); //$NON-NLS-1$
			if (is != null) {
				try { is.close(); } catch (final Exception ex) { /* No hacemos nada */ }
			}
			throw new RuntimeException("No se ha encontrado el fichero de propiedades " + CONFIG_FILE, e); //$NON-NLS-1$
		}
		catch (final Exception e) {
			LOGGER.error("No se pudo cargar el fichero de configuracion " + CONFIG_FILE); //$NON-NLS-1$
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
			LOGGER.error("No se ha encontrado el fichero de configuracion de la conexion"); //$NON-NLS-1$
			throw new RuntimeException("No se ha encontrado el fichero de configuracion de la conexion"); //$NON-NLS-1$
		}

		try {
			if (getSignfolderUrl() == null) {
				LOGGER.error("No se ha establecido la URL del servicio del Portafirmas"); //$NON-NLS-1$
				throw new RuntimeException("No se ha establecido la URL del servicio del Portafirmas"); //$NON-NLS-1$
			}
		} catch (final Exception e) {
			LOGGER.error("La URL del servicio del Portafirmas no esta bien formada"); //$NON-NLS-1$
			throw new RuntimeException("La URL del servicio del Portafirmas no esta bien formada"); //$NON-NLS-1$
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
			return new URL(getProperty(PROPERTY_SIGNFOLDER_URL));
		}
		catch (final Exception e) {
			throw new RuntimeException("La URL del servicio del portafirmas no esta bien formada", e); //$NON-NLS-1$
		}
	}

	/**
	 * Devuelve la cadena de configuraci&oacute;n con los ExtraParams que deben
	 * aplicarse a todas las operaciones de firma.
	 * @return Cadena con los ExtraParams en forma clave=valor y separados por '\n'.
	 */
	public static String getForcedExtraParams(){
		return getProperty(PROPERTY_FORCED_EXTRAPARAMS);
	}

	/**
	 * Devuelve la URL base en la que se despliega el servicio proxy.
	 * @return URL base del servicio terminada en '/' o {@code null} si no se configur&oacute; la URL base.
	 */
	public static String getProxyBaseUrl() {
		final String url = getProperty(PROPERTY_PROXY_BASE_URL);
		return url != null ? url.endsWith("/") ? url : url + "/" : null; //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Indica si la compartici&oacute;n de sesiones entre nodos esta habilitada.
	 * @return {@code true} si la comparticion de sesiones esta habilitada, {@code false} en caso contrario.
	 */
	public static boolean isShareSessionEnabled(){
		return Boolean.parseBoolean(getProperty(PROPERTY_SHARED_ENABLED));
	}

	/**
	 * Indica si la compartici&oacute;n de sesiones entre nodos esta habilitada.
	 * @return {@code true} si la comparticion de sesiones esta habilitada, {@code false} en caso contrario.
	 */
	public static boolean isShareSessionWithCertEnabled(){
		return Boolean.parseBoolean(getProperty(PROPERTY_SHARED_WITH_CERT_ENABLED));
	}

	/**
	 * Devuelve la ruta del directorio temporal compartido.
	 * @return Ruta del directorio temporal compartido o {@code null} si no se ha configurado.
	 */
	public static String getTempDir() {
		return getProperty(PROPERTY_SHARED_DIR);
	}

	/**
	 * Devuelve el numero de peticiones que acceso al directorio de sesiones compartidas que se pueden realizar antes de lanzar
	 * el proceso de limpieza de sesiones.
	 * @return Ruta del directorio temporal compartido o {@code null} si no se ha configurado.
	 */
	public static int getRequestToClean() {
		int numRequests;
		final String request = getProperty(PROPERTY_REQUESTS_TO_CLEAN);
		try {
			numRequests = Integer.parseInt(request);
		} catch (final Exception e) {
			LOGGER.warn("No se ha indicado un numero valido de peticiones antes de la limpieza del directorio temporal (" + //$NON-NLS-1$
					PROPERTY_REQUESTS_TO_CLEAN + "). Se usara el valor por defecto (" + DEFAULT_VALUE_REQUESTS_TO_CLEAN  + ")."); //$NON-NLS-1$ //$NON-NLS-2$
			numRequests = DEFAULT_VALUE_REQUESTS_TO_CLEAN;
		}
		return numRequests;
	}

	/**
	 * Obtiene una propiedad del fichero de configuraci&oacute;n.
	 * @param prop Nombre de la propiedad.
	 * @return Valor de la propiedad o {@code null} si no est&aacute; definida.
	 */
	public static String getProperty(final String prop) {
		return config != null ? mapSystemProperties(config.getProperty(prop)) : null;
	}

	/**
	 * Mapea las propiedades del sistema que haya en el texto que se referencien en
	 * la forma: ${propiedad}
	 * @param text Texto en el que se pueden encontrar las referencias a las propiedades
	 * del sistema.
	 * @return Cadena con las particulas traducidas a los valores indicados como propiedades
	 * del sistema. Si no se encuentra la propiedad definida, no se modificar&aacute;
	 */
	private static String mapSystemProperties(final String text) {

		if (text == null) {
			return null;
		}

		int pos = -1;
		int pos2 = 0;
		String mappedText = text;
		while ((pos = mappedText.indexOf(SYS_PROP_PREFIX, pos + 1)) > -1 && pos2 > -1) {
			pos2 = mappedText.indexOf(SYS_PROP_SUFIX, pos + SYS_PROP_PREFIX.length());
			if (pos2 > pos) {
				final String prop = mappedText.substring(pos + SYS_PROP_PREFIX.length(), pos2);
				final String value = System.getProperty(prop, null);
				if (value != null) {
					mappedText = mappedText.replace(SYS_PROP_PREFIX + prop + SYS_PROP_SUFIX, value);
				}
			}
		}
		return mappedText;
	}

	/**
	 * Devuelve el nombre de usuario para el acceso a los servicios del Portafirmas web.
	 * @return Nombre de usuario o {@code null} si no se defini&oacute;.
	 */
	public static String getSignfolderUsername() {
		final String username = getProperty(PROPERTY_SIGNFOLDER_USERNAME);
		if (username != null && username.isEmpty()) {
			return null;
		}
		return username;
	}

	/**
	 * Devuelve la contrase&ntilde;a para el acceso a los servicios del Portafirmas web.
	 * @return Contrase&ntilde;a o {@code null} si no se defini&oacute;.
	 */
	public static String getSignfolderPassword() {
		final String passw = getProperty(PROPERTY_SIGNFOLDER_PASSWORD);
		if (passw != null && passw.isEmpty()) {
			return null;
		}
		return passw;
	}

	/**
	 * Indica si se debe usar el sistema de cache o no.
	 * @return {@code true} si se debe usar la cach&eacute;, no en caso contrario.
	 */
	public static boolean isCacheEnabled() {
		final String cacheEnabledValue = getProperty(PROPERTY_CACHE_ENABLED);
		return Boolean.parseBoolean(cacheEnabledValue);
	}

	/**
	 * Clase que define el sistema de cach&eacute; que se debe utilizar.
	 * @return Nombre completo de la clase para la gestion de cach&eacute;.
	 */
	public static String getCacheSystemClass() {
		final String cacheSystemClassValue = getProperty(PROPERTY_CACHE_SYSTEM_CLASS);
		if (cacheSystemClassValue != null && cacheSystemClassValue.isEmpty()) {
			return null;
		}
		return cacheSystemClassValue;
	}

	/**
	 * Obtiene el tiempo de caducidad de los documentos en cach&eacute;.
	 * @return Milisegundos que duran los documentos en cach&eacute; sin eliminar.
	 */
	public static long getCacheExpirationTime() {

		long expirationTime = DEFAULT_EXPIRATION_TIME;
		final String expirationTimeValue = getProperty(PROPERTY_CACHE_SYSTEM_EXPIRATION_TIME);
		if (expirationTimeValue != null || expirationTimeValue.isEmpty()) {
			try {
				final long configuredTime = Long.parseLong(expirationTimeValue);
				if (configuredTime > 0) {
					expirationTime = configuredTime;
				}
			}
			catch (final Exception e) {
				LOGGER.warn("No se ha indicado un tiempo valido de expiracion de la cache {}", expirationTimeValue); //$NON-NLS-1$
			}
		}
		return expirationTime;
	}

	public static int getMaxPagesToCheckPSA() {
		int numRequests;
		final String request = getProperty(PROPERTY_MAX_PAGES_TO_CHECK_PSA);
		try {
			numRequests = Integer.parseInt(request);
		} catch (final Exception e) {
			LOGGER.warn("No se ha indicado un numero valido de paginas sobre las que comprobar si se ha realizado un PDF Shadow Attack" //$NON-NLS-1$
					+ "({}). Se usara el valor por defecto ({}).", PROPERTY_MAX_PAGES_TO_CHECK_PSA, DEFAULT_MAX_PAGES_TO_CHECK_PSA); //$NON-NLS-1$
			numRequests = DEFAULT_MAX_PAGES_TO_CHECK_PSA;
		}
		return numRequests;
	}
}
