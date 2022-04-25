package es.gob.afirma.signfolder.server.proxy;

import org.w3c.dom.Element;

/**
 * Parse para la obtenci&oacute;n de la informaci&oacute;n de un usuario gen&eacute;rico.
 */
public class GenericUserParser {

	private static final String USER_ID_ATTR = "id"; //$NON-NLS-1$
	private static final String USER_DNI_ATTR = "dni"; //$NON-NLS-1$

	private GenericUserParser() {
		// No permitimos la instanciacion de la clase
	}

	/**
	 * Parsea la informaci&oacute;n de un usuario gen&eacute;rico.
	 * @param userNode Nodo de usuario.
	 * @return Informaci&oacute;n del usuario.
	 */
	public static GenericUser parse(final Element userElement) {
		return parse(userElement, false);
	}

	/**
	 * Parsea la informaci&oacute;n de un usuario gen&eacute;rico.
	 * @param userNode Nodo de usuario.
	 * @param relaxed Indica si se debe realizar una an&aacute;lisis laxo, con lo cual basta con
	 * encontrar el DNI del usuario.
	 * @return Informaci&oacute;n del usuario.
	 */
	public static GenericUser parse(final Element userElement, final boolean relaxed) {

		String id = userElement.getAttribute(USER_ID_ATTR);
		if (!relaxed && (id == null || id.trim().isEmpty())) {
			throw new IllegalArgumentException("No se ha proporcionado el identificador del usuario"); //$NON-NLS-1$
		} else if (id != null) {
			id = id.trim();
		}

		String dni = userElement.getAttribute(USER_DNI_ATTR);
		if (dni == null || dni.trim().isEmpty()) {
			throw new IllegalArgumentException("No se ha proporcionado el DNI del usuario"); //$NON-NLS-1$
		}
		dni = dni.trim();

		String name = userElement.getTextContent();
		if (!relaxed && (name == null || name.trim().isEmpty())) {
			throw new IllegalArgumentException("No se ha proporcionado el nombre del usuario"); //$NON-NLS-1$
		} else if (name != null) {
			name = name.trim();
		}

		return new GenericUser(id, dni, name);
	}
}
