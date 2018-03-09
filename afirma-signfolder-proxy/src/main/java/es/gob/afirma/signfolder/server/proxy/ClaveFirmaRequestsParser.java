package es.gob.afirma.signfolder.server.proxy;

import java.io.IOException;
import java.security.cert.CertificateException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


final class ClaveFirmaRequestsParser {

	private static final String CLAVEFIRMA_POSTSIGN_REQUEST_NODE = "rqtcf"; //$NON-NLS-1$
	private static final String TRANSACTION_ID_ATTR = "trid"; //$NON-NLS-1$

	private ClaveFirmaRequestsParser() {
		// No instanciable
	}

	/** Analiza un documento XML y, en caso de tener el formato correcto, obtiene de &eacute;l
	 * un objeto de tipo {@link es.gob.afirma.signfolder.server.proxy.TriphaseRequestBean} con
	 * la informacion correspondiente a un listado de peticiones de firma con varios documentos
	 * cada una.
	 * @param doc Documento XML.
	 * @param certEncoded Certificado codificado.
	 * @return Objeto con los datos del XML.
	 * @throws IOException Si ocurren problemas decodificando el certificado desde Base64
	 * @throws CertificateException Si ocurren problemas creando el certificado
	 * @throws IllegalArgumentException Cuando el XML no tiene el formato esperado.	 */
	static FirePreSignResult parse(final Document doc, byte[] certEncoded) throws CertificateException, IOException {

		if (doc == null) {
			throw new IllegalArgumentException("El documento proporcionado no puede ser nulo");  //$NON-NLS-1$
		}

		final Element docElement = doc.getDocumentElement();
		if (!CLAVEFIRMA_POSTSIGN_REQUEST_NODE.equalsIgnoreCase(docElement.getNodeName())) {
			throw new IllegalArgumentException("El elemento raiz del XML debe ser '" + //$NON-NLS-1$
					CLAVEFIRMA_POSTSIGN_REQUEST_NODE + "' y aparece: " + //$NON-NLS-1$
					docElement.getNodeName());
		}

		final String trId = docElement.getAttribute(TRANSACTION_ID_ATTR);
		if (trId == null) {
			throw new IOException("No se encontro el identificador de la transaccion"); //$NON-NLS-1$
		}

		return new FirePreSignResult(
				trId,
				null,
				SignRequestsParser.parse((Element) docElement.getFirstChild(), certEncoded));
	}
}
