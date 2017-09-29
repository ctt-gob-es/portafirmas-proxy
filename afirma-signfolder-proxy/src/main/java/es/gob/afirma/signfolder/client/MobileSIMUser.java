
package es.gob.afirma.signfolder.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for mobileSIMUser complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="mobileSIMUser">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="IdRegistro" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Plataforma" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="IdDispositivo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mobileSIMUser", namespace = "urn:juntadeandalucia:cice:pfirma:mobile:type:v2.0", propOrder = {
    "idRegistro",
    "plataforma",
    "idDispositivo"
})
public class MobileSIMUser {

    @XmlElement(name = "IdRegistro")
    protected String idRegistro;
    @XmlElement(name = "Plataforma")
    protected String plataforma;
    @XmlElement(name = "IdDispositivo")
    protected String idDispositivo;

    /**
     * Gets the value of the idRegistro property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdRegistro() {
        return idRegistro;
    }

    /**
     * Sets the value of the idRegistro property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdRegistro(String value) {
        this.idRegistro = value;
    }

    /**
     * Gets the value of the plataforma property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPlataforma() {
        return plataforma;
    }

    /**
     * Sets the value of the plataforma property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPlataforma(String value) {
        this.plataforma = value;
    }

    /**
     * Gets the value of the idDispositivo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdDispositivo() {
        return idDispositivo;
    }

    /**
     * Sets the value of the idDispositivo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdDispositivo(String value) {
        this.idDispositivo = value;
    }

}
