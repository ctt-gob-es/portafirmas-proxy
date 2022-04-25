package es.gob.afirma.signfolder.server.proxy;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Clase que analiza la estructura del servicio de recuperación del estado de las notificaciones push.
 */
public class GetPushStatusParser {

	/**
	 * Constante que define el nombre del elemento principal de la petición.
	 */
	private static final String REQUEST_NODE = "pshsttsrq"; //$NON-NLS-1$

	/**
	 * Método que parsea la petición recibida como objeto Document y comprueba si tiene una estructura válida.
	 * @param doc Documento que representa la petición recibida.
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
