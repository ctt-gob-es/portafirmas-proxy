package es.gob.afirma.signfolder.server.proxy;

import java.io.IOException;
import java.security.cert.CertificateException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/** Analizador de XML para la generaci&oacute;n de objetos de tipo
 * {@link es.gob.afirma.signfolder.server.proxy.TriphaseRequestBean} a partir
 * de un XML de petici&oacute;n de firma trif&aacute;sica. *
 * @author Carlos Gamuci */
final class FireSignRequestParser {

	private static final String FIRE_SIGN_NODE = "cfrq"; //$NON-NLS-1$

	private FireSignRequestParser() {
		// No instanciable
	}

	/** Analiza un documento XML y, en caso de tener el formato correcto, obtiene de &eacute;l
	 * un objeto de tipo {@link es.gob.afirma.signfolder.server.proxy.TriphaseRequestBean} con
	 * la informacion correspondiente a un listado de peticiones de firma con varios documentos
	 * cada una.
	 * @param doc Documento XML.
	 * @return Identificador de transacci&oacute;n.
	 * @throws IOException Si ocurren problemas decodificando el certificado desde Base64
	 * @throws CertificateException Si ocurren problemas creando el certificado
	 * @throws IllegalArgumentException Cuando el XML no tiene el formato esperado.	 */
	static void parse(final Document doc) throws CertificateException, IOException {

		if (doc == null) {
			throw new IllegalArgumentException("El documento proporcionado no puede ser nulo");  //$NON-NLS-1$
		}

		final Element xmlNode = doc.getDocumentElement();

		if (!FIRE_SIGN_NODE.equalsIgnoreCase(xmlNode.getNodeName())) {
			throw new IllegalArgumentException("El elemento raiz del XML debe ser '" + //$NON-NLS-1$
					FIRE_SIGN_NODE + "' y aparece: " + //$NON-NLS-1$
					doc.getDocumentElement().getNodeName());
		}
	}
}
