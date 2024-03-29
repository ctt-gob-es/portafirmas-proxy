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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementaci&oacute;n de cach&eacute; de documentos que carga y guarda los documentos en disco.
 * @author carlos.gamuci
 */
public class FileSystemDocumentCache implements DocumentCache {

	/** Propiedad en la que se almacenara el directorio temporal para el guardado de documentos en cache. */
	private static final String CONFIG_PROPERTY_CACHE_DIR = "cache.filesystem.dir"; //$NON-NLS-1$

	/** Tama&ntilde:o de la cabecera. */
	private static final int HEADER_SIZE = 50;

    private static final int BUFFER_SIZE = 4096;

    private static final String DEFAULT_CACHE_DIR = "proxy_cache"; //$NON-NLS-1$

	private static final Charset CHARSET = StandardCharsets.UTF_8;

	private static final String FILENAME_REF_SEP = "_"; //$NON-NLS-1$

	private static final String HEADER_ENTRIES_SEP = "%"; //$NON-NLS-1$
	private static final String HEADER_ENTRY_PREFIX_COP = "c:"; //$NON-NLS-1$

	private static final Logger LOGGER = LoggerFactory.getLogger(FileSystemDocumentCache.class);

	private static File cacheDir = null;

	@Override
	public void saveDocument(final String requestRef, final String docId, final String cop, final byte[] content) throws IOException {

		final File dir = getCacheDir();
		final File tempFile = new File(dir, requestRef + FILENAME_REF_SEP + docId);

		// Guardamos el fichero en disco anteponiendo a su contenido una cabecera con
		// los datos que deseamos
		try (OutputStream fos = new FileOutputStream(tempFile)) {
			final byte[] headerContent = (HEADER_ENTRY_PREFIX_COP + cop + HEADER_ENTRIES_SEP).getBytes(CHARSET);
			final byte[] header = new byte[HEADER_SIZE];
			System.arraycopy(headerContent, 0, header, 0, headerContent.length);
			fos.write(header);
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

		byte[] content;
		final byte[] header = new byte[HEADER_SIZE];
		try (InputStream fis = new FileInputStream(tempFile);
			 InputStream bis = new BufferedInputStream(fis)) {
			// Leemos la cabecera de tamano fijo
			if (bis.read(header) < header.length) {
				throw new IOException("El documento almacenado en cache no alcanzaba el tamano minimo"); //$NON-NLS-1$
			}
			// Leemos el resto del fichero, que sera el contenido del documento
			content = readData(bis);
		}

		// Extraemos la operacion cryptografica de la cache
		final String cop = readCryptoOperation(header);

		if (delete) {
			if (!Files.deleteIfExists(tempFile.toPath())) {
				LOGGER.debug("No se ha podido eliminar de cache el fichero {}", tempFile.getName()); //$NON-NLS-1$
			}
		}

		return new CachedDocument(cop, content);
	}


	/**
	 * Lee la operaci&oacute;n criptogr&aacute;fica de entre los datos de cabecera.
	 * @param header Datos de cabecera.
	 * @return Operaci&oacute;n criptogr&aacute;fica o {@code null} si no se identifica.
	 */
    private static String readCryptoOperation(final byte[] header) {
		final String headerContent = new String(header, CHARSET);
		final String[] headerEntries = headerContent.split(HEADER_ENTRIES_SEP);

		String cop = null;
		for (final String headerEntry : headerEntries) {
			// Operacion criptografica
			if (headerEntry.startsWith(HEADER_ENTRY_PREFIX_COP)) {
				cop = headerEntry.substring(HEADER_ENTRY_PREFIX_COP.length());
			}
		}
		return cop;
	}

	/** Lee un flujo de datos de entrada y los recupera en forma de array de
     * bytes. Este m&eacute;todo consume, pero no cierra el flujo de datos de
     * entrada.
     * @param input Flujo de donde se toman los datos.
     * @return Los datos obtenidos del flujo.
     * @throws IOException Cuando ocurre un problema durante la lectura. */
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
