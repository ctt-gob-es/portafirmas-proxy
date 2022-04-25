package es.gob.afirma.signfolder.server.proxy;


import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Clase con m&eacute;todos de utilidad para el parseo de documentos XML mediante DOM
 * manteniendo la compatibilidad con JME.
 * @author Carlos Gamuci
 * @author Tom&aacute;s Garc&iacute;a-Mer&aacute;s
 */
final class XmlUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(XmlUtils.class);

	private XmlUtils() {
		// No instanciable
	}

	/** Recupera el &iacute;ndice siguiente nodo de la lista de tipo Element. Empieza a comprobar
	 * los nodos a partir del &iacute;ndice marcado. Si no encuentra un nodo de tipo elemento,
	 * devuelve -1.
	 * @param nodes Listado de nodos.
	 * @param currentIndex &Iacute;ndice del listado a partir del cual se empieza la comprobaci&oacute;n.
	 * @return &Iacute;ndice del siguiente node de tipo Element o -1 si no se encontr&oacute;.
	 */
	static int nextNodeElementIndex(final NodeList nodes, final int currentIndex) {
		Node node;
		int i = currentIndex;
		while (i < nodes.getLength()) {
			node = nodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				return i;
			}
			i++;
		}
		return -1;
	}

	public static DocumentBuilderFactory getSecureDocumentBuilderFactory() {
		final DocumentBuilderFactory secureDocBuilderFactory = DocumentBuilderFactory.newInstance();
		try {
			secureDocBuilderFactory.setFeature(javax.xml.XMLConstants.FEATURE_SECURE_PROCESSING, Boolean.TRUE.booleanValue());
		}
		catch (final Exception e) {
			LOGGER.warn("No se ha podido establecer el procesado seguro en la factoria XML: " + e); //$NON-NLS-1$
		}

		// Los siguientes atributos deberia establececerlos automaticamente la implementacion de
		// la biblioteca al habilitar la caracteristica anterior. Por si acaso, los establecemos
		// expresamente
		final String[] securityProperties = new String[] {
				javax.xml.XMLConstants.ACCESS_EXTERNAL_DTD,
				javax.xml.XMLConstants.ACCESS_EXTERNAL_SCHEMA,
				javax.xml.XMLConstants.ACCESS_EXTERNAL_STYLESHEET
		};
		for (final String securityProperty : securityProperties) {
			try {
				secureDocBuilderFactory.setAttribute(securityProperty, ""); //$NON-NLS-1$
			}
			catch (final Exception e) {
				// Podemos las trazas en debug ya que estas propiedades son adicionales
				// a la activacion de el procesado seguro
				LOGGER.debug("No se ha podido establecer una propiedad de seguridad '" + securityProperty + "' en la factoria XML"); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}

		secureDocBuilderFactory.setValidating(false);

		return secureDocBuilderFactory;
	}

}
