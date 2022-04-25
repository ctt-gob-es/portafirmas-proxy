
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
 *         &lt;element name="validador" type="{urn:juntadeandalucia:cice:pfirma:mobile:type:v2.0}mobileValidador"/>
 *         &lt;element name="accion" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "validador",
    "accion"
})
@XmlRootElement(name = "salvarValidadorMobile")
public class SalvarValidadorMobile {

    @XmlElement(required = true)
    protected byte[] dni;
    @XmlElement(required = true)
    protected MobileValidador validador;
    @XmlElement(required = true)
    protected String accion;

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
     * Obtiene el valor de la propiedad validador.
     * 
     * @return
     *     possible object is
     *     {@link MobileValidador }
     *     
     */
    public MobileValidador getValidador() {
        return validador;
    }

    /**
     * Define el valor de la propiedad validador.
     * 
     * @param value
     *     allowed object is
     *     {@link MobileValidador }
     *     
     */
    public void setValidador(MobileValidador value) {
        this.validador = value;
    }

    /**
     * Obtiene el valor de la propiedad accion.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAccion() {
        return accion;
    }

    /**
     * Define el valor de la propiedad accion.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAccion(String value) {
        this.accion = value;
    }

}
