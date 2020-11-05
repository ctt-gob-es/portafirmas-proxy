package es.gob.afirma.signfolder.server.proxy;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Clase que analiza la estructura del servicio de actualizaci&oacute;n del estado de las notificaciones push.
 */
public class UpdatePushStatusParser {

	/**
	 * Constante que define el nombre del elemento principal de la petici&oacute;n.
	 */
	private static final String REQUEST_NODE = "pdtpshsttsrq"; //$NON-NLS-1$

	/**
	 * M&eacute;todo que parsea la petici&oacute;n recibida como objeto Document y comprueba si
	 * tiene una estructura v&aacute;lida.
	 * @param doc Documento que representa la petici&oacute;n recibida.
	 * @return {@code true} si se deben activar las notificaciones, {@code false} si se deben
	 * desactivar.
	 */
	static boolean parse(final Document doc) {
		if (doc == null) {
			throw new IllegalArgumentException("El documento proporcionado no puede ser nulo"); //$NON-NLS-1$
		}

		final Element rootElement = doc.getDocumentElement();

		if (!REQUEST_NODE.equalsIgnoreCase(rootElement.getNodeName())) {
			throw new IllegalArgumentException("El elemento raiz del XML debe ser '" + REQUEST_NODE + "' y aparece: " //$NON-NLS-1$ //$NON-NLS-2$
					+ doc.getDocumentElement().getNodeName());
		}

		return Boolean.parseBoolean(rootElement.getTextContent());
	}


}
