
package es.gob.afirma.signfolder.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para anonymous complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="dni" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/>
 *         &lt;element name="register" type="{urn:juntadeandalucia:cice:pfirma:mobile:type:v2.0}mobileSIMUser"/>
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
    "dni",
    "register"
})
@XmlRootElement(name = "registerSIMUser")
public class RegisterSIMUser {

    @XmlElement(required = true)
    protected byte[] dni;
    @XmlElement(required = true)
    protected MobileSIMUser register;

    /**
     * Obtiene el valor de la propiedad dni.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getDni() {
        return dni;
    }

    /**
     * Define el valor de la propiedad dni.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setDni(byte[] value) {
        this.dni = value;
    }

    /**
     * Obtiene el valor de la propiedad register.
     * 
     * @return
     *     possible object is
     *     {@link MobileSIMUser }
     *     
     */
    public MobileSIMUser getRegister() {
        return register;
    }

    /**
     * Define el valor de la propiedad register.
     * 
     * @param value
     *     allowed object is
     *     {@link MobileSIMUser }
     *     
     */
    public void setRegister(MobileSIMUser value) {
        this.register = value;
    }

}
