
package juntadeandalucia.cice.pfirma.mobile.v2;

import javax.xml.ws.WebFault;
import juntadeandalucia.cice.pfirma.mobile.type.v2.MobileError;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.4-b01
 * Generated source version: 2.2
 * 
 */
@WebFault(name = "mobileError", targetNamespace = "urn:juntadeandalucia:cice:pfirma:mobile:type:v2.0")
public class MobileException
    extends Exception
{

    /**
     * Java type that goes as soapenv:Fault detail element.
     * 
     */
    private MobileError faultInfo;

    /**
     * 
     * @param message
     * @param faultInfo
     */
    public MobileException(String message, MobileError faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @param message
     * @param faultInfo
     * @param cause
     */
    public MobileException(String message, MobileError faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @return
     *     returns fault bean: juntadeandalucia.cice.pfirma.mobile.type.v2.MobileError
     */
    public MobileError getFaultInfo() {
        return faultInfo;
    }

}
