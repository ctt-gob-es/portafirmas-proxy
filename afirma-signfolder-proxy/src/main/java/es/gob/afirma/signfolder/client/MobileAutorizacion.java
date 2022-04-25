
package es.gob.afirma.signfolder.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para mobileAutorizacion complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="mobileAutorizacion">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="pfAuthorizedUser" type="{urn:juntadeandalucia:cice:pfirma:mobile:type:v2.0}mobileUsuarioGenerico" minOccurs="0"/>
 *         &lt;element name="pfAuthorizationType" type="{urn:juntadeandalucia:cice:pfirma:mobile:type:v2.0}mobileTipoGenerico" minOccurs="0"/>
 *         &lt;element name="pfUser" type="{urn:juntadeandalucia:cice:pfirma:mobile:type:v2.0}mobileUsuarioGenerico" minOccurs="0"/>
 *         &lt;element name="frequest" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="fauthorization" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="frevocation" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="observations" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="estado" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mobileAutorizacion", namespace = "urn:juntadeandalucia:cice:pfirma:mobile:type:v2.0", propOrder = {
    "id",
    "pfAuthorizedUser",
    "pfAuthorizationType",
    "pfUser",
    "frequest",
    "fauthorization",
    "frevocation",
    "observations",
    "estado"
})
public class MobileAutorizacion {

    protected String id;
    protected MobileUsuarioGenerico pfAuthorizedUser;
    protected MobileTipoGenerico pfAuthorizationType;
    protected MobileUsuarioGenerico pfUser;
    protected String frequest;
    protected String fauthorization;
    protected String frevocation;
    protected String observations;
    protected String estado;

    /**
     * Obtiene el valor de la propiedad id.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Define el valor de la propiedad id.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Obtiene el valor de la propiedad pfAuthorizedUser.
     * 
     * @return
     *     possible object is
     *     {@link MobileUsuarioGenerico }
     *     
     */
    public MobileUsuarioGenerico getPfAuthorizedUser() {
        return pfAuthorizedUser;
    }

    /**
     * Define el valor de la propiedad pfAuthorizedUser.
     * 
     * @param value
     *     allowed object is
     *     {@link MobileUsuarioGenerico }
     *     
     */
    public void setPfAuthorizedUser(MobileUsuarioGenerico value) {
        this.pfAuthorizedUser = value;
    }

    /**
     * Obtiene el valor de la propiedad pfAuthorizationType.
     * 
     * @return
     *     possible object is
     *     {@link MobileTipoGenerico }
     *     
     */
    public MobileTipoGenerico getPfAuthorizationType() {
        return pfAuthorizationType;
    }

    /**
     * Define el valor de la propiedad pfAuthorizationType.
     * 
     * @param value
     *     allowed object is
     *     {@link MobileTipoGenerico }
     *     
     */
    public void setPfAuthorizationType(MobileTipoGenerico value) {
        this.pfAuthorizationType = value;
    }

    /**
     * Obtiene el valor de la propiedad pfUser.
     * 
     * @return
     *     possible object is
     *     {@link MobileUsuarioGenerico }
     *     
     */
    public MobileUsuarioGenerico getPfUser() {
        return pfUser;
    }

    /**
     * Define el valor de la propiedad pfUser.
     * 
     * @param value
     *     allowed object is
     *     {@link MobileUsuarioGenerico }
     *     
     */
    public void setPfUser(MobileUsuarioGenerico value) {
        this.pfUser = value;
    }

    /**
     * Obtiene el valor de la propiedad frequest.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFrequest() {
        return frequest;
    }

    /**
     * Define el valor de la propiedad frequest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFrequest(String value) {
        this.frequest = value;
    }

    /**
     * Obtiene el valor de la propiedad fauthorization.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFauthorization() {
        return fauthorization;
    }

    /**
     * Define el valor de la propiedad fauthorization.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFauthorization(String value) {
        this.fauthorization = value;
    }

    /**
     * Obtiene el valor de la propiedad frevocation.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFrevocation() {
        return frevocation;
    }

    /**
     * Define el valor de la propiedad frevocation.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFrevocation(String value) {
        this.frevocation = value;
    }

    /**
     * Obtiene el valor de la propiedad observations.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getObservations() {
        return observations;
    }

    /**
     * Define el valor de la propiedad observations.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setObservations(String value) {
        this.observations = value;
    }

    /**
     * Obtiene el valor de la propiedad estado.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEstado() {
        return estado;
    }

    /**
     * Define el valor de la propiedad estado.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEstado(String value) {
        this.estado = value;
    }

}
