package es.gob.afirma.signfolder.server.proxy;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import es.gob.afirma.core.misc.Base64;

/**
 * Analiza un documento XML para obtener una solicitud de validacion de
 * inicio de sesi&oacute;n.
 * @author Carlos Gamuci
 */
public class ValidateLoginRequestParser {

	private static final String VALIDATE_LOGIN_REQUEST_NODE = "rqtvl"; //$NON-NLS-1$

	private static final String SIGNATURE_NODE = "sg"; //$NON-NLS-1$

	static ValidateLoginRequest parse(Document doc) {

		if (doc == null) {
			throw new IllegalArgumentException("El documento proporcionado no puede ser nulo");  //$NON-NLS-1$
		}

		if (!VALIDATE_LOGIN_REQUEST_NODE.equalsIgnoreCase(doc.getDocumentElement().getNodeName())) {
			throw new IllegalArgumentException("El elemento raiz del XML debe ser '" + //$NON-NLS-1$
					VALIDATE_LOGIN_REQUEST_NODE + "' y aparece: " + //$NON-NLS-1$
					doc.getDocumentElement().getNodeName());
		}

		final NodeList nodes = doc.getDocumentElement().getChildNodes();
		final int nodeIndex = XmlUtils.nextNodeElementIndex(nodes, 0);
		if (nodeIndex == -1) {
			throw new IllegalArgumentException(
					"No se ha indicado el certificado necesario para la autenticacion en el nodo " + //$NON-NLS-1$
							SIGNATURE_NODE);
		}
		final Element signatureNode = (Element) nodes.item(nodeIndex);
		if (!SIGNATURE_NODE.equalsIgnoreCase(signatureNode.getNodeName())) {
			throw new IllegalArgumentException(
					"No se ha encontrado el nodo " + SIGNATURE_NODE + //$NON-NLS-1$
					" en su lugar se encontro " + signatureNode.getNodeName()); //$NON-NLS-1$
		}

		final byte[] signature;
		try {
			signature = Base64.decode(signatureNode.getTextContent().trim());
		} catch (final Exception e) {
			throw new IllegalArgumentException(
					"No se ha podido obtener la codificacion del certificado a partir del XML: " + e); //$NON-NLS-1$
		}

		return new ValidateLoginRequest(signature);
	}

}
