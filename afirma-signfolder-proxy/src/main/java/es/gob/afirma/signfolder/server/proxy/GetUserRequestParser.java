package es.gob.afirma.signfolder.server.proxy;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Clase que analiza la estructura de las peticiones del servicio "getUsers".
 */
public class GetUserRequestParser {
	
	/**
	 * Constante que define el nombre del elemento principal de la petición.
	 */
	private static final String REQUEST_NODE = "rquserls";

	/**
	 * Método que parsea la respuesta recibida como documento y comprueba si tiene una estructura válida.
	 * @param doc Petición recibida.
	 */
	static void parse(final Document doc) {
		if (doc == null) {
			throw new IllegalArgumentException("El documento proporcionado no puede ser nulo");
		}

		final Element xmlNode = doc.getDocumentElement();

		if (!REQUEST_NODE.equalsIgnoreCase(xmlNode.getNodeName())) {
			throw new IllegalArgumentException("El elemento raiz del XML debe ser '" + REQUEST_NODE + "' y aparece: "
					+ doc.getDocumentElement().getNodeName());
		}
	}

}
