package es.gob.afirma.signfolder.server.proxy;

import java.io.IOException;
import java.util.Date;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class SaveAuthorizationRequestParser {

	private static final String ROOT_NODE = "rqsaveauth"; //$NON-NLS-1$
	private static final String REQUEST_TYPE_ATTRIBUTE = "type"; //$NON-NLS-1$

	private static final String AUTH_USER_NODE = "authuser"; //$NON-NLS-1$
	private static final String START_DATE_NODE = "startdate"; //$NON-NLS-1$
	private static final String EXPIRATION_DATE_NODE = "expdate"; //$NON-NLS-1$
	private static final String OBSERVATIONS_NODE = "observations"; //$NON-NLS-1$

	private SaveAuthorizationRequestParser() {
		// Se evita el uso del constructor
	}

	/**
	 * Analiza un documento XML y, en caso de tener el formato correcto, obtiene
	 * de &eacute;l la informaci&oacute;n sobre la solicitud de guardado de una
	 * autorizaci&oacute;n.
	 * @param builder Constructor para el an&aacute;lisis del XML.
	 * @param doc Documento XML.
	 * @return Solicitud de guardado de la autorizaci&oacute;n.
	 * @throws IOException Cuando se produce un error de lectura.
	 * @throws SAXException Cuando el XML no est&aacute; bien formado.
	 * @throws IllegalArgumentException Cuando el XML no tiene el formato esperado.
	 */
	static Authorization parse(final Document doc)
			throws SAXException, IOException, IllegalArgumentException {

		if (doc == null) {
			throw new IllegalArgumentException("El documento proporcionado no puede ser nulo"); //$NON-NLS-1$
		}

		final Element rootElement = doc.getDocumentElement();

		if (!ROOT_NODE.equalsIgnoreCase(rootElement.getNodeName())) {
			throw new IllegalArgumentException("El elemento raiz del XML debe ser '" //$NON-NLS-1$
					+ ROOT_NODE + "' y aparece: " + rootElement.getNodeName()); //$NON-NLS-1$
		}

		// Recuperamos el tipo de autorizacion
		final String type = rootElement.getAttribute(REQUEST_TYPE_ATTRIBUTE);
		if (type == null) {
			throw new IllegalArgumentException("No se ha indicado el atributo " + //$NON-NLS-1$
					REQUEST_TYPE_ATTRIBUTE + " con el tipo de autorizacion"); //$NON-NLS-1$
		}

		// Preparamos las variables para ir listado las propiedades de la peticion
		int i = -1;
		final NodeList nodes = rootElement.getChildNodes();

		// Nodo con el usuario al que se desea autorizar
		i = XmlUtils.nextNodeElementIndex(nodes, 0);
		if (i == -1 || !nodes.item(i).getNodeName().equals(AUTH_USER_NODE)) {
			throw new IllegalArgumentException("No se ha encontrado el nodo " + //$NON-NLS-1$
					AUTH_USER_NODE + " con el usuario autorizado"); //$NON-NLS-1$
		}
		final GenericUser authUser = GenericUserParser.parse((Element) nodes.item(i), true);

		// Comienzan los nodos opcionales. Vamos buscandolos uno tras otro
		i = XmlUtils.nextNodeElementIndex(nodes, ++i);

		// Nodo con la fecha de inicio de la autorizacion
		Date startDate = null;
		if (i != -1 && nodes.item(i).getNodeName().equals(START_DATE_NODE)) {
			try {
				final String startDateParam = nodes.item(i).getTextContent();
				if (startDateParam != null && !startDateParam.trim().isEmpty()) {
					startDate = DateTimeFormatter.getAppFormatterInstance().parse(startDateParam.trim());
				}
			}
			catch (final Exception e) {
				throw new IllegalArgumentException("El formato de la fecha de inicio no era correcto", e); //$NON-NLS-1$
			}
			i = XmlUtils.nextNodeElementIndex(nodes, ++i);
		}

		// Nodo con la fecha de fin de la autorizacion
		Date revocationDate = null;
		if (i != -1 && nodes.item(i).getNodeName().equals(EXPIRATION_DATE_NODE)) {
			try {
				final String revocationDateParam = nodes.item(i).getTextContent();
				if (revocationDateParam != null && !revocationDateParam.trim().isEmpty()) {
					revocationDate = DateTimeFormatter.getAppFormatterInstance().parse(revocationDateParam.trim());
				}
			}
			catch (final Exception e) {
				throw new IllegalArgumentException("El formato de la fecha de fin no era correcto", e); //$NON-NLS-1$
			}
			i = XmlUtils.nextNodeElementIndex(nodes, ++i);
		}

		// Nodo con las observaciones
		String observations = null;
		if (i != -1 && nodes.item(i).getNodeName().equals(OBSERVATIONS_NODE)) {
			observations = nodes.item(i).getTextContent();
			if (observations != null && observations.trim().isEmpty()) {
				observations = null;
			}
			//i = XmlUtils.nextNodeElementIndex(nodes, ++i);
		}

		final Authorization auth = new Authorization();
		auth.setType(type);
		auth.setSended(true);
		auth.setAuthorizedUser(authUser);
		auth.setStartDate(startDate);
		if (revocationDate != null) {
			auth.setRevocationDate(revocationDate);
		}
		if (observations != null) {
			auth.setObservations(observations);
		}

		return auth;
	}
}
