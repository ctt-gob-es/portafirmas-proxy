package es.gob.afirma.signfolder.server.proxy;

import java.io.IOException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class SaveValidatorRequestParser {

	private static final String ROOT_NODE = "rqsavevalid"; //$NON-NLS-1$
	private static final String VALIDATOR_NODE = "validator"; //$NON-NLS-1$

	private SaveValidatorRequestParser() {
		// Se evita el uso del constructor
	}

	/**
	 * Analiza un documento XML y, en caso de tener el formato correcto, obtiene
	 * de &eacute;l la informaci&oacute;n sobre la solicitud de alta de un validador.
	 * @param builder Constructor para el an&aacute;lisis del XML.
	 * @param doc Documento XML.
	 * @return Solicitud de alta del validador.
	 * @throws IOException Cuando se produce un error de lectura.
	 * @throws SAXException Cuando el XML no est&aacute; bien formado.
	 * @throws IllegalArgumentException Cuando el XML no tiene el formato esperado.
	 */
	static Validator parse(final Document doc)
			throws SAXException, IOException, IllegalArgumentException {

		if (doc == null) {
			throw new IllegalArgumentException("El documento proporcionado no puede ser nulo"); //$NON-NLS-1$
		}

		final Element rootElement = doc.getDocumentElement();

		if (!ROOT_NODE.equalsIgnoreCase(rootElement.getNodeName())) {
			throw new IllegalArgumentException("El elemento raiz del XML debe ser '" //$NON-NLS-1$
					+ ROOT_NODE + "' y aparece: " + rootElement.getNodeName()); //$NON-NLS-1$
		}

		// Preparamos las variables para ir listado las propiedades de la peticion
		int i = -1;
		final NodeList nodes = rootElement.getChildNodes();

		// Nodo con el usuario al que se desea autorizar
		i = XmlUtils.nextNodeElementIndex(nodes, 0);
		if (i == -1 || !nodes.item(i).getNodeName().equals(VALIDATOR_NODE)) {
			throw new IllegalArgumentException("No se ha encontrado el nodo " + //$NON-NLS-1$
					VALIDATOR_NODE + " con el usuario validador"); //$NON-NLS-1$
		}
		final GenericUser validatorUser = GenericUserParser.parse((Element) nodes.item(i));

		// Construimos la informacion del validador
		final Validator validator = new Validator();
		validator.setValidatorForApps(false);
		validator.setUser(validatorUser);

		return validator;
	}
}
