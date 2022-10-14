package es.gob.afirma.signfolder.soap.security;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import es.gob.afirma.core.misc.Base64;

/**
 * Agrega las cabeceras de seguridad para el env&iacute;n de las peticiones SOAP.
 */
public final class SecurityHandler implements SOAPHandler<SOAPMessageContext> {

	private static final Logger LOGGER = Logger.getLogger("es.gob.afirma"); //$NON-NLS-1$

	private static final String WSSE_NAMESPACE = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd"; //$NON-NLS-1$
	private static final String WSU_NAMESPACE = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd"; //$NON-NLS-1$

	private final String username;
	private final String password;

	private final SimpleDateFormat dateFormatter;

	/**
	 * Construye el manejador que agrega las cabeceras de seguridad a las peticiones
	 * SOAP.
	 * @param username Nombre de usuario para el acceso al servicio.
	 * @param password Contrase&ntilde;a para el acceso al servicio.
	 */
	public SecurityHandler(final String username, final String password) {
		this.username = username;
		this.password = password;

		this.dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");  //$NON-NLS-1$
	}

    @Override
    public boolean handleMessage(final SOAPMessageContext context) {

        try {
            if ((Boolean) context.get("javax.xml.ws.handler.message.outbound")) { //$NON-NLS-1$

            	final SOAPEnvelope envelope = context.getMessage().getSOAPPart().getEnvelope();
            	SOAPHeader soapHeader = envelope.getHeader();
            	if (soapHeader == null) {
            		soapHeader = envelope.addHeader();
            	}

                final SOAPElement token = soapHeader
                		.addChildElement(new QName(WSSE_NAMESPACE, "Security")) //$NON-NLS-1$
//                		.addNamespaceDeclaration("wsse", WSSE_NAMESPACE) //$NON-NLS-1$
//                		.addNamespaceDeclaration("wsu", WSU_NAMESPACE) //$NON-NLS-1$
                		.addChildElement(new QName(WSSE_NAMESPACE, "UsernameToken")); //$NON-NLS-1$

                token.addChildElement(new QName(WSSE_NAMESPACE, "Username")) //$NON-NLS-1$
                	.addTextNode(this.username);

                token.addChildElement(new QName(WSSE_NAMESPACE, "Password")) //$NON-NLS-1$
                	.addAttribute(new QName("Type"), "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText") //$NON-NLS-1$ //$NON-NLS-2$
                	.addTextNode(this.password);

                final byte[] nonce = UUID.randomUUID().toString().getBytes();
                token.addChildElement(new QName(WSSE_NAMESPACE, "Nonce")) //$NON-NLS-1$
            		.addAttribute(new QName("EncodingType"), "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-soap-message-security-1.0#Base64Binary") //$NON-NLS-1$ //$NON-NLS-2$
            		.addTextNode(Base64.encode(nonce));

                final String currentDate = this.dateFormatter.format(new Date());
                token.addChildElement(new QName(WSU_NAMESPACE, "Created")) //$NON-NLS-1$
        			.addTextNode(currentDate);
            }
        }
        catch(final Exception e) {
        	LOGGER.log(Level.WARNING, "Error en la composicion de la cabecera de seguridad", e); //$NON-NLS-1$
        }

        return true;
    }

    @Override
    public boolean handleFault(final SOAPMessageContext context) {
    	LOGGER.warning("SecurityFault en la conexion con el servicio web"); //$NON-NLS-1$
    	return false;
    }

    @Override
    public void close(final MessageContext context) {
        // do nothing
    }

    @Override
    public Set<QName> getHeaders() {
        return null;
    }
}


