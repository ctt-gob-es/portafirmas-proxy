package es.gob.afirma.signfolder.server.proxy;

import org.w3c.dom.Document;

/**
 * Analiza un documento XML para obtener una solicitud de token de autenticaci&oacute;n para
 * el inicio de sesi&oacute;n.
 * @author Carlos Gamuci
 */
public class LoginRequestParser {

	private static final String LOGIN_REQUEST_NODE = "lgnrq"; //$NON-NLS-1$

	private static final String CERT_DIGEST_ATTRIBUTE = "cd"; //$NON-NLS-1$

	static LoginRequest parse(Document doc) {

		if (doc == null) {
			throw new IllegalArgumentException("El documento proporcionado no puede ser nulo");  //$NON-NLS-1$
		}

		if (!LOGIN_REQUEST_NODE.equalsIgnoreCase(doc.getDocumentElement().getNodeName())) {
			throw new IllegalArgumentException("El elemento raiz del XML debe ser '" + //$NON-NLS-1$
					LOGIN_REQUEST_NODE + "' y aparece: " + //$NON-NLS-1$
					doc.getDocumentElement().getNodeName());
		}

		// Configuramos el estado de las peticiones deseadas
		final String certDigestB64 = doc.getDocumentElement().getAttribute(CERT_DIGEST_ATTRIBUTE);
		if (certDigestB64 == null) {
			throw new IllegalArgumentException(
					"No se ha encontrado el certificado para la autenticacion de la peticion de solicitudes de firma"); //$NON-NLS-1$
		}

		return new LoginRequest(certDigestB64);
	}

}
