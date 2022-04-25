package es.gob.afirma.signfolder.server.proxy;

import java.io.IOException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * Analizador XML para la eliminaci&oacute;n de un usuario validador.
 */
public class RemoveValidatorRequestParser {

	private static final String ROOT_NODE = "rqrevvalidator"; //$NON-NLS-1$

	private static final String VALIDATOR_ID_ATTRIBUTE = "id"; //$NON-NLS-1$

	private RemoveValidatorRequestParser() {
		// Se evita el uso del constructor
	}

	/** Analiza un documento XML y, en caso de tener el formato correcto, obtiene de &eacute;l
	 * un identificador de validador.
	 * @param doc Documento XML.
	 * @return Identificador de validador.
	 * @throws IOException Cuando falla la carga del XML.
	 * @throws SAXException Cuando el XML no est&eacute; bien formado.
	 * @throws IllegalArgumentException Cuando el XML no tiene el formato esperado.	 */
	static String parse(final Document doc)
			throws SAXException, IOException, IllegalArgumentException {

		if (doc == null) {
			throw new IllegalArgumentException("El documento proporcionado no puede ser nulo");  //$NON-NLS-1$
		}

		final Element rootElement = doc.getDocumentElement();

		if (!ROOT_NODE.equalsIgnoreCase(rootElement.getNodeName())) {
			throw new IllegalArgumentException("El elemento raiz del XML debe ser '" //$NON-NLS-1$
					+ ROOT_NODE + "' y aparece: " //$NON-NLS-1$
					+ rootElement.getNodeName());
		}

		// Recuperamos el identificador de la solicitud
		final String id = doc.getDocumentElement().getAttribute(VALIDATOR_ID_ATTRIBUTE);
		if (id == null || id.trim().isEmpty()) {
			throw new IllegalArgumentException("No se ha indicado el atributo " //$NON-NLS-1$
					+ VALIDATOR_ID_ATTRIBUTE + " con el identificador la solicitud"); //$NON-NLS-1$
		}

		return id;
	}
}
