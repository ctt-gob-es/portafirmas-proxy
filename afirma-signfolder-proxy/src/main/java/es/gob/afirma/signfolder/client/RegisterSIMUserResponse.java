
package es.gob.afirma.signfolder.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="registerStatus" type="{urn:juntadeandalucia:cice:pfirma:mobile:type:v2.0}mobileSIMUserStatus"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "registerStatus"
})
@XmlRootElement(name = "registerSIMUserResponse")
public class RegisterSIMUserResponse {

    @XmlElement(required = true)
    protected MobileSIMUserStatus registerStatus;

    /**
     * Gets the value of the registerStatus property.
     * 
     * @return
     *     possible object is
     *     {@link MobileSIMUserStatus }
     *     
     */
    public MobileSIMUserStatus getRegisterStatus() {
        return registerStatus;
    }

    /**
     * Sets the value of the registerStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link MobileSIMUserStatus }
     *     
     */
    public void setRegisterStatus(MobileSIMUserStatus value) {
        this.registerStatus = value;
    }

}
