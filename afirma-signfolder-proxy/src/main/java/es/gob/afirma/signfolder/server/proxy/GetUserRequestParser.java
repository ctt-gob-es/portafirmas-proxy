package es.gob.afirma.signfolder.server.proxy;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Clase que extrae la informaci&oacute;n de las peticiones del servicio
 * de b&uacute;squeda de usuarios.
 */
public class GetUserRequestParser {

	/** Nombre del elemento principal de la petici&oacute;n. */
	private static final String REQUEST_NODE = "rqfinduser"; //$NON-NLS-1$

	/** Atributo con el modo de firma. */
	private static final String ATTR_MODE = "mode"; //$NON-NLS-1$

	/** Atributo con el modo de firma. */
	private static final String MODE_VALIDATOR = "validadores"; //$NON-NLS-1$

	/** Atributo con el modo de firma. */
	private static final String MODE_AUTHENTICATION = "autorizados"; //$NON-NLS-1$

	/**
	 * M&eacute;todo que parsea la respuesta recibida como documento y comprueba si tiene una estructura v&aacute;lida.
	 * @param doc Petici&oacute;n recibida.
	 */
	static FindUserRequest parse(final Document doc) {
		if (doc == null) {
			throw new IllegalArgumentException("El documento proporcionado no puede ser nulo"); //$NON-NLS-1$
		}

		final Element xmlNode = doc.getDocumentElement();

		// Comprobamos el nodo raiz
		if (!REQUEST_NODE.equalsIgnoreCase(xmlNode.getNodeName())) {
			throw new IllegalArgumentException("El elemento raiz del XML debe ser '" + REQUEST_NODE + "' y aparece: " //$NON-NLS-1$ //$NON-NLS-2$
					+ doc.getDocumentElement().getNodeName());
		}

		// Comprobamos el modo de busqueda
		String mode = xmlNode.getAttribute(ATTR_MODE);
		if (mode == null || mode.trim().isEmpty()) {
			throw new IllegalArgumentException("No se ha proporcionado el modo de busqueda"); //$NON-NLS-1$
		}

		// Canonicalizamos el modo
		if (MODE_VALIDATOR.equalsIgnoreCase(mode.trim())) {
			mode = MODE_VALIDATOR;
		}
		else if (MODE_AUTHENTICATION.equalsIgnoreCase(mode.trim())) {
			mode = MODE_AUTHENTICATION;
		}
		else {
			throw new IllegalArgumentException("No se ha proporcionado el modo de busqueda"); //$NON-NLS-1$
		}

		// Comprobamos el texto de busqueda
		String text = xmlNode.getTextContent();
		if (text == null || text.trim().isEmpty()) {
			throw new IllegalArgumentException("No se ha proporcionado texto para la busqueda"); //$NON-NLS-1$
		}
		text = SecurityUtils.cleanTextToXml(text);

		return new FindUserRequest(mode,  text.trim());
	}

}
