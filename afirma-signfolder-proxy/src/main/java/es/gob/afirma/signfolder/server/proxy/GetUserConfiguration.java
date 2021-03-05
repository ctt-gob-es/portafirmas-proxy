package es.gob.afirma.signfolder.server.proxy;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Clase que analiza la estructura de las peticiones del servicio "getUserByRole".
 */
public class GetUserConfiguration {

	/**
	 * Constante que define el nombre del elemento principal de la petici&oacute;n.
	 */
	private static final String REQUEST_NODE = "rqsrcnfg"; //$NON-NLS-1$

	/**
	 * M&eacute;todo que parsea la petici&oacute;n recibida como documento y comprueba si tiene una estructura v&aacute;lida.
	 * @param doc Petici&oacute;n recibida.
	 */
	static void parse(final Document doc) {
		if (doc == null) {
			throw new IllegalArgumentException("El documento proporcionado no puede ser nulo"); //$NON-NLS-1$
		}

		final Element xmlNode = doc.getDocumentElement();

		if (!REQUEST_NODE.equalsIgnoreCase(xmlNode.getNodeName())) {
			throw new IllegalArgumentException("El elemento raiz del XML debe ser '" + REQUEST_NODE + "' y aparece: " //$NON-NLS-1$ //$NON-NLS-2$
					+ doc.getDocumentElement().getNodeName());
		}
	}

}
