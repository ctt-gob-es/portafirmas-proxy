
package es.gob.afirma.signfolder.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for mobileSIMUserStatus complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="mobileSIMUserStatus">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="statusCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="statusText" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="details" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="idDispositivo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mobileSIMUserStatus", namespace = "urn:juntadeandalucia:cice:pfirma:mobile:type:v2.0", propOrder = {
    "statusCode",
    "statusText",
    "details",
    "idDispositivo"
})
public class MobileSIMUserStatus {

    protected String statusCode;
    protected String statusText;
    protected String details;
    protected String idDispositivo;

    /**
     * Gets the value of the statusCode property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getStatusCode() {
        return this.statusCode;
    }

    /**
     * Sets the value of the statusCode property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setStatusCode(String value) {
        this.statusCode = value;
    }

    /**
     * Gets the value of the statusText property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getStatusText() {
        return this.statusText;
    }

    /**
     * Sets the value of the statusText property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setStatusText(String value) {
        this.statusText = value;
    }

    /**
     * Gets the value of the details property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDetails() {
        return this.details;
    }

    /**
     * Sets the value of the details property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDetails(String value) {
        this.details = value;
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
        return this.idDispositivo;
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
