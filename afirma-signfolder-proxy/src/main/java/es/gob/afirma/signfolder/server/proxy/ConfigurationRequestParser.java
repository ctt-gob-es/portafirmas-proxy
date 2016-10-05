package es.gob.afirma.signfolder.server.proxy;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import es.gob.afirma.core.misc.Base64;

/**
 * Analiza un documento XML para obtener una petici&oacute;n de previsualizaci&oacute;n
 * de documento.
 * @author Carlos Gamuci
 */
public class ConfigurationRequestParser {

	private static final String CONFIGURATION_REQUEST_NODE = "rqtconf"; //$NON-NLS-1$
	private static final String CERT_NODE = "cert"; //$NON-NLS-1$

	private ConfigurationRequestParser() {
		// Se evita el uso del constructor
	}

	/** Analiza un documento XML y, en caso de tener el formato correcto, obtiene de &eacute;l
	 * un identificador de documento.
	 * @param doc Documento XML.
	 * @return Identificador de documento.
	 * @throws IllegalArgumentException Cuando el XML no tiene el formato esperado.	 */
	static ConfigurationRequest parse(final Document doc) {

		if (doc == null) {
			throw new IllegalArgumentException("El documento proporcionado no puede ser nulo");  //$NON-NLS-1$
		}

		if (!CONFIGURATION_REQUEST_NODE.equalsIgnoreCase(doc.getDocumentElement().getNodeName())) {
			throw new IllegalArgumentException("El elemento raiz del XML debe ser '" + //$NON-NLS-1$
					CONFIGURATION_REQUEST_NODE + "' y aparece: " + //$NON-NLS-1$
					doc.getDocumentElement().getNodeName());
		}

		final NodeList nodes = doc.getDocumentElement().getChildNodes();
		final int nodeIndex = XmlUtils.nextNodeElementIndex(nodes, 0);
		if (nodeIndex == -1) {
			throw new IllegalArgumentException(
					"No se ha indicado el certificado necesario para la autenticacion en el nodo " + //$NON-NLS-1$
							CERT_NODE);
		}
		final Element certNode = (Element) nodes.item(nodeIndex);
		if (!CERT_NODE.equalsIgnoreCase(certNode.getNodeName())) {
			throw new IllegalArgumentException(
					"No se ha encontrado el nodo " + CERT_NODE + //$NON-NLS-1$
					" en su lugar se encontro " + certNode.getNodeName()); //$NON-NLS-1$
		}

		final byte[] certEncoded;
		try {
			certEncoded = Base64.decode(certNode.getTextContent().trim());
		} catch (Exception e) {
			throw new IllegalArgumentException(
					"No se ha podido obtener la codificacion del certificado a partir del XML: " + e); //$NON-NLS-1$
		}
		
		return new ConfigurationRequest(certEncoded);
	}
}
