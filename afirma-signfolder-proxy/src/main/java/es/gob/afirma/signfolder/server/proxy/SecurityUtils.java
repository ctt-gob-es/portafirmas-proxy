package es.gob.afirma.signfolder.server.proxy;

public class SecurityUtils {

	/**
	 * Limpia un texto de caracteres peligrosos para un texto XML.
	 * @param text Texto a limpiar.
	 * @return Texto limpio.
	 */
	public static String cleanTextToXml(final String text) {
		return text
				.replace("\"", "").replace("'", "").replace("\t", "") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
				.replace("\r", "").replace("\n", "").replace("]]>", "").trim(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
	}
}
