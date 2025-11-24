package es.gob.afirma.signfolder.server.proxy;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Date;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementaci&oacute;n de cach&eacute; de documentos que carga y guarda los documentos en disco.
 * El documento guardado en disco va precedido de una cabecera de longitud variable. En la cabecera
 * se encuentran una serie de metadatos que definen la configuraci&oacute;n de firma del documento.
 * La cabecera est&aacute; compuesta por tuplas clave:valor, separados por el signo de pocentaje
 * ('%') entre ellas y por dos signos de porcentaje ("%%") del contenido del propio documento.
 * Las claves de la configuraci&oacute;n que se almacena en la cabecera son:
 *  - c: Operaci&oacute;n criptogr&aacute;fica.
 *  - d: Algoritmo de huella que se debe usar para la firma.
 *  - p: ExtraParams de configuraci&oacute;n de la firma.
 * @author carlos.gamuci
 */
public class FileSystemDocumentCache implements DocumentCache {

	/** Propiedad en la que se almacenara el directorio temporal para el guardado de documentos en cache. */
	private static final String CONFIG_PROPERTY_CACHE_DIR = "cache.filesystem.dir"; //$NON-NLS-1$

    private static final int BUFFER_SIZE = 4096;

    private static final String DEFAULT_CACHE_DIR = "proxy_cache"; //$NON-NLS-1$

	private static final Charset CHARSET = StandardCharsets.UTF_8;

	private static final String FILENAME_REF_SEP = "_"; //$NON-NLS-1$


	private static final String HEADER_SEP = ":"; //$NON-NLS-1$
	private static final String HEADER_ENTRIES_SEP = "%"; //$NON-NLS-1$
	private static final String HEADER_EOF = "%%"; //$NON-NLS-1$
	private static final String HEADER_PROP_COP = "c"; //$NON-NLS-1$
	private static final String HEADER_PROP_DIGEST_ALGO = "d"; //$NON-NLS-1$
	private static final String HEADER_PROP_EXTRA_PARAMS = "p"; //$NON-NLS-1$



	private static final Logger LOGGER = LoggerFactory.getLogger(FileSystemDocumentCache.class);

	private static File cacheDir = null;

	@Override
	public void saveDocument(final String requestRef, final String docId, final String cop,
			final String digestAlgorithm, final String params, final byte[] content) throws IOException {

		final File dir = getCacheDir();
		final File tempFile = new File(dir, requestRef + FILENAME_REF_SEP + docId);

		// Guardamos el fichero en disco anteponiendo a su contenido una cabecera con
		// los datos que deseamos
		try (OutputStream fos = new FileOutputStream(tempFile)) {

			String header = HEADER_PROP_COP + HEADER_SEP + cop + HEADER_ENTRIES_SEP
					 + HEADER_PROP_DIGEST_ALGO + HEADER_SEP + digestAlgorithm;

			if (params != null) {
				header += HEADER_ENTRIES_SEP + HEADER_PROP_EXTRA_PARAMS + HEADER_SEP
						+ params;
			}
			header += HEADER_EOF;

			final byte[] headerContent = header.getBytes(CHARSET);
			fos.write(headerContent);
			fos.write(content);
		}
	}

	/**
	 * Obtiene el directorio en el que almacenar los ficheros en cach&eacute;. Si no esta configurado,
	 * se creara un directorio especifico dentro de los temporales del usuario. Si no fuese posible,
	 * se usara el propio directorio de temporales de usuario.
	 * @return Directorio de cach&eeacute;.
	 */
	private static File getCacheDir() {
		if (cacheDir == null) {
			final String cacheDirValue = ConfigManager.getProperty(CONFIG_PROPERTY_CACHE_DIR);
			if (cacheDirValue == null || !new File(cacheDirValue).isDirectory()) {
				final String tempDir = System.getProperty("java.io.tmpdir"); //$NON-NLS-1$
				cacheDir = new File(tempDir, DEFAULT_CACHE_DIR);
				if (!cacheDir.exists()) {
					if (!cacheDir.mkdirs()) {
						cacheDir = new File(tempDir);
					}
				}
				else if (cacheDir.isFile()) {
					cacheDir = new File(tempDir);
				}
				LOGGER.warn("No se ha configurado un directorio valido en la propiedad '{}' para el guardado de datos en cache. Se utilizara el directorio temporal por defecto: {}", //$NON-NLS-1$
						CONFIG_PROPERTY_CACHE_DIR, tempDir);
			}
			else {
				cacheDir = new File(cacheDirValue);
			}
		}
		return cacheDir;
	}

	@Override
	public CachedDocument loadDocument(final String requestRef, final String docId, final boolean delete) throws IOException {

		final File dir = getCacheDir();
		final File tempFile = new File(dir, requestRef + FILENAME_REF_SEP + docId);
		if (!tempFile.isFile()) {
			throw new FileNotFoundException("No se ha encontrado el documento " + docId + " en cache"); //$NON-NLS-1$ //$NON-NLS-2$
		}

		byte[] header;
		byte[] content;
		try (InputStream fis = new FileInputStream(tempFile);
			 InputStream bis = new BufferedInputStream(fis)) {

			// Leemos la cabecera con la configuracion de firma
			header = readHeader(bis);

			// Leemos el resto del fichero, que sera el contenido del documento
			content = readData(bis);
		}

		// Extraemos la operacion cryptografica de la cache
		final Properties cryptoConfig = readCryptoConfig(header);

		if (delete) {
			if (!Files.deleteIfExists(tempFile.toPath())) {
				LOGGER.debug("No se ha podido eliminar de cache el fichero {}", tempFile.getName()); //$NON-NLS-1$
			}
		}

		final String cop = cryptoConfig.getProperty(HEADER_PROP_COP);
		final String digestAlgorithm = cryptoConfig.getProperty(HEADER_PROP_DIGEST_ALGO);
		String params = null;
		if (cryptoConfig.containsKey(HEADER_PROP_EXTRA_PARAMS)) {
			params = cryptoConfig.getProperty(HEADER_PROP_EXTRA_PARAMS);
		}

		return new CachedDocument(cop, digestAlgorithm, params, content);
	}

	/**
	 * Lee la configuraci&oacute;n de la operaci&oacute;n de los datos en la cabecera
	 * de la informaci&oacute;n en cach&eacute;.
	 * @param header Datos de cabecera.
	 * @return Conjunto de propiedades para la firma del documento.
	 */
    private static Properties readCryptoConfig(final byte[] header) {
		final String headerContent = new String(header, CHARSET);

		final String[] headerEntries = headerContent.split(HEADER_ENTRIES_SEP);

		final Properties headers = new Properties();

		for (final String headerEntry : headerEntries) {
			final String[] headerParts = headerEntry.split(HEADER_SEP);
			headers.setProperty(headerParts[0], headerParts[1]);
		}
		return headers;
	}

    /**
     * Lee el flujo de entrada hasta leer la configuraci&oacute;n de firma
     * de la cabecera del documento.
     * @param input Flujo de donde se toman los datos.
     * @return Los datos obtenidos del flujo.
     * @throws IOException Cuando ocurre un problema durante la lectura.
     */
    private static byte[] readHeader(final InputStream bis) throws IOException {

    	final char[] EOF_CHARS = HEADER_EOF.toCharArray();

    	boolean headerComplete = false;
    	byte[] header;

    	try (final ByteArrayOutputStream baos = new ByteArrayOutputStream();) {
    		while (!headerComplete) {
    			final int c = bis.read();
    			if (c == -1) {
    				headerComplete = true;
    			}
    			else if (c != EOF_CHARS[0]) {
    				baos.write(c);
    			} else {
    				final int c2 = bis.read();
    				if (c2 != EOF_CHARS[1]) {
    					baos.write(c);
    					baos.write(c2);
    				}
    				else {
    					headerComplete = true;
    				}
    			}
    		}
    		header = baos.toByteArray();
    	}

		return header;
	}

	/**
	 * Lee un flujo de datos de entrada y los recupera en forma de array de
     * bytes. Este m&eacute;todo consume, pero no cierra el flujo de datos de
     * entrada.
     * @param input Flujo de donde se toman los datos.
     * @return Los datos obtenidos del flujo.
     * @throws IOException Cuando ocurre un problema durante la lectura.
     */
    private static byte[] readData(final InputStream input) throws IOException {
        if (input == null) {
            return new byte[0];
        }
        int nBytes;
        final byte[] buffer = new byte[BUFFER_SIZE];
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        while ((nBytes = input.read(buffer)) != -1) {
            baos.write(buffer, 0, nBytes);
        }
        return baos.toByteArray();
    }

	@Override
	public void cleanExpiredFiles(final long timeMillis) {

		final long now = new Date().getTime();
		final long expirationTime = now - ConfigManager.getCacheExpirationTime();
		final File dir = getCacheDir();
		for (final File expiredFile : dir.listFiles(new ExpiredFilesFilter(expirationTime))) {
			try {
				if (Files.deleteIfExists(expiredFile.toPath())) {
					LOGGER.warn("No se pudo eliminar de cache el fichero caducado {}", expiredFile.getName()); //$NON-NLS-1$
				}
			}
			catch (final Exception e) {
				LOGGER.warn("Error al eliminar de cache el fichero caducado {}", expiredFile.getName()); //$NON-NLS-1$
			}
		}
	}

	@Override
	public void removeDocument(final String requestRef, final String docId) throws IOException {
		final File dir = getCacheDir();
		final File tempFile = new File(dir, requestRef + FILENAME_REF_SEP + docId);
		if (!Files.deleteIfExists(tempFile.toPath())) {
			throw new IOException("No se pudo eliminar el documento " + docId + " de la cache"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/**
	 * Filtro de ficheros caducados.
	 */
	private static class ExpiredFilesFilter implements FileFilter {

		private final long expirationDate;

		/**
		 * Construye el filtro indicando la fecha antes de la cual estan caducados
		 * los ficheros.
		 * @param expirationDate Fecha de expiraci&oacute;n en milisegundos.
		 */
		public ExpiredFilesFilter(final long expirationDate) {
			this.expirationDate = expirationDate;
		}

		@Override
		public boolean accept(final File pathname) {
			if (pathname.isFile() && pathname.lastModified() < this.expirationDate) {
				return true;
			}
			return false;
		}
	}


}
