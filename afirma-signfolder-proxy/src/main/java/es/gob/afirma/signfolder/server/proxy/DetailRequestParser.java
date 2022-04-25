package es.gob.afirma.signfolder.server.proxy;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Analiza un documento XML para obtener un identificador de una solicitud de la
 * que queremos obtener el detalle.
 *
 * @author Carlos Gamuci
 */
public class DetailRequestParser {

	private static final String DETAIL_REQUEST_NODE = "rqtdtl"; //$NON-NLS-1$
	private static final String REQUEST_ID_ATTRIBUTE = "id"; //$NON-NLS-1$
	private static final String OWNER_ID_ATTRIBUTE = "ownerId"; //$NON-NLS-1$

	private DetailRequestParser() {
		// Se evita el uso del constructor
	}

	/**
	 * Analiza un documento XML y, en caso de tener el formato correcto, obtiene
	 * de &eacute;l un identificador de solicitud de firma.
	 *
	 * @param doc
	 *            Documento XML.
	 * @return Identificador de solicitud de firma.
	 * @throws IllegalArgumentException
	 *             Cuando el XML no tiene el formato esperado.
	 */
	static DetailRequest parse(final Document doc) {

		if (doc == null) {
			throw new IllegalArgumentException("El documento proporcionado no puede ser nulo"); //$NON-NLS-1$
		}

		final Element rootElement = doc.getDocumentElement();

		if (!DETAIL_REQUEST_NODE.equalsIgnoreCase(rootElement.getNodeName())) {
			throw new IllegalArgumentException("El elemento raiz del XML debe ser '" + //$NON-NLS-1$
					DETAIL_REQUEST_NODE + "' y aparece: " + //$NON-NLS-1$
					rootElement.getNodeName());
		}

		// Recuperamos el identificador de la solicitud
		final String id = rootElement.getAttribute(REQUEST_ID_ATTRIBUTE);
		if (id == null) {
			throw new IllegalArgumentException("No se ha indicado el atributo " + //$NON-NLS-1$
					REQUEST_ID_ATTRIBUTE + " con el identificador la solicitud"); //$NON-NLS-1$
		}

		// Recuperamos el DNI del propietario de la solicitud.
		String ownerId = rootElement.getAttribute(OWNER_ID_ATTRIBUTE);
		if (ownerId != null && ownerId.isEmpty()) {
			ownerId = null;
		}

		return new DetailRequest(id, ownerId);
	}
}
