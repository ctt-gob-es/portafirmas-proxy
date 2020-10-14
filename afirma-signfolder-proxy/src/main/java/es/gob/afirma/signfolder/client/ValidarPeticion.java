
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
 *         &lt;element name="dniValidador" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/>
 *         &lt;element name="requestTagId" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "dniValidador",
    "requestTagId"
})
@XmlRootElement(name = "validarPeticion")
public class ValidarPeticion {

    @XmlElement(required = true)
    protected byte[] dniValidador;
    @XmlElement(required = true)
    protected String requestTagId;

    /**
     * Obtiene el valor de la propiedad dniValidador.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getDniValidador() {
        return dniValidador;
    }

    /**
     * Define el valor de la propiedad dniValidador.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setDniValidador(byte[] value) {
        this.dniValidador = value;
    }

    /**
     * Obtiene el valor de la propiedad requestTagId.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRequestTagId() {
        return requestTagId;
    }

    /**
     * Define el valor de la propiedad requestTagId.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRequestTagId(String value) {
        this.requestTagId = value;
    }

}
