package es.gob.afirma.signfolder.server.proxy;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Parser gen&eacute;rico para los XML de petici&oacute;n que no proporcionan informaci&oacute;n. &Uacute;nicamente
 * es necesario que sea un XML con un nodo principal determinado.
 * @author carlos.gamuci
 *
 */
public class GenericRequestParser {

	private static final String ROOT_NODE = "rqt"; //$NON-NLS-1$

	static void parse(final Document doc) throws IllegalArgumentException {

		if (doc == null) {
			throw new IllegalArgumentException("El documento proporcionado no puede ser nulo"); //$NON-NLS-1$
		}

		final Element rootElement = doc.getDocumentElement();

		if (!ROOT_NODE.equalsIgnoreCase(rootElement.getNodeName())) {
			throw new IllegalArgumentException("El elemento raiz del XML debe ser '" + ROOT_NODE + "' y aparece: " //$NON-NLS-1$ //$NON-NLS-2$
					+ rootElement.getNodeName());
		}
	}
}
