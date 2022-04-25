package es.gob.afirma.signfolder.server.proxy;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Clase que analiza la estructura del servicio de validaci&oacute;n de peticiones.
 */
public class VerifyPetitionParser {

	/**
	 * Constante que define el nombre del elemento principal de la petici&oacute;n.
	 */
	private static final String ROOT_NODE = "verfreq"; //$NON-NLS-1$

	/**
	 * Constante que define el nombre del elemento que designa el certificado del usuario (no se utiliza).
	 */
	private static final String CERT_NODE = "cert"; //$NON-NLS-1$

	/**
	 * Constante que define el nombre del elemento que designa al listado de peticiones.
	 */
	private static final String REQUESTS_NODE = "reqs"; //$NON-NLS-1$

	/**
	 * Constante que define el nombre del elemento que designa a una petici&oacute;n.
	 */
	private static final String REQUEST_NODE = "r"; //$NON-NLS-1$

	/**
	 * M&eacute;todo que parsea la petici&oacute;n recibida como objeto Document y comprueba si tiene una estructura v&aacute;lida.
	 * @param doc Documento que representa la petici&oacute;n recibida.
	 * @
	 */
	static List<String> parse(final Document doc) {
		if (doc == null) {
			throw new IllegalArgumentException("El documento proporcionado no puede ser nulo"); //$NON-NLS-1$
		}

		if (!ROOT_NODE.equalsIgnoreCase(doc.getDocumentElement().getNodeName())) {
			throw new IllegalArgumentException("El elemento raiz del XML debe ser '" + ROOT_NODE + "' y aparece: " //$NON-NLS-1$ //$NON-NLS-2$
					+ doc.getDocumentElement().getNodeName());
		}

		final NodeList nodes = doc.getDocumentElement().getChildNodes();
		int nodeIndex = XmlUtils.nextNodeElementIndex(nodes, 0);

		if (nodeIndex == -1) {
			throw new IllegalArgumentException(
					"No se ha encontrado el nodo " + REQUESTS_NODE + //$NON-NLS-1$
					" en la peticion"); //$NON-NLS-1$
		}

		Element currentNode = (Element) nodes.item(nodeIndex);

		// Si nos proporcionan el nodo con el certificado (modo de funcionamiento antiguo),
		// lo ignoramos y pasamos al siguiente nodo
		if (CERT_NODE.equalsIgnoreCase(currentNode.getNodeName())) {
			nodeIndex = XmlUtils.nextNodeElementIndex(nodes, ++nodeIndex);
			currentNode = (Element) nodes.item(nodeIndex);
		}

		if (currentNode == null || !REQUESTS_NODE.equalsIgnoreCase(currentNode.getNodeName())) {
			throw new IllegalArgumentException(
					"No se ha encontrado el nodo " + REQUESTS_NODE + //$NON-NLS-1$
					" en su lugar se encontro " + (currentNode != null ? currentNode.getNodeName() : null)); //$NON-NLS-1$
		}

		final List<String> requestIds = new ArrayList<>();

		Node requestNode = null;
		final NodeList requestNodes = currentNode.getChildNodes();
		int requestIdx = XmlUtils.nextNodeElementIndex(requestNodes, 0);
		while (requestIdx != -1) {
			requestNode = requestNodes.item(requestIdx);
			if (!REQUEST_NODE.equalsIgnoreCase(requestNode.getNodeName())) {
				throw new IllegalArgumentException(
						"No se ha encontrado el nodo " + REQUEST_NODE + //$NON-NLS-1$
						" en su lugar se encontro " + requestNode.getNodeName()); //$NON-NLS-1$
			}
			final Node idAttr = requestNode.getAttributes().getNamedItem("id"); //$NON-NLS-1$
			if (idAttr == null) {
				throw new IllegalArgumentException(
						"No se ha encontrado el identificador de una peticion a validar"); //$NON-NLS-1$
			}
			requestIds.add(idAttr.getNodeValue());

			requestIdx = XmlUtils.nextNodeElementIndex(requestNodes, requestIdx + 1);
		}

		return requestIds;
	}


}
