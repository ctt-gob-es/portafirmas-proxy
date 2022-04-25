
package es.gob.afirma.signfolder.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para mobileValidador complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="mobileValidador">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="pfValidadorUser" type="{urn:juntadeandalucia:cice:pfirma:mobile:type:v2.0}mobileUsuarioGenerico" minOccurs="0"/>
 *         &lt;element name="aplicacionesValidadas" type="{urn:juntadeandalucia:cice:pfirma:mobile:type:v2.0}mobileApplicationList" minOccurs="0"/>
 *         &lt;element name="validadorPorAplicacion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mobileValidador", namespace = "urn:juntadeandalucia:cice:pfirma:mobile:type:v2.0", propOrder = {
    "pfValidadorUser",
    "aplicacionesValidadas",
    "validadorPorAplicacion"
})
public class MobileValidador {

    protected MobileUsuarioGenerico pfValidadorUser;
    protected MobileApplicationList aplicacionesValidadas;
    protected String validadorPorAplicacion;

    /**
     * Obtiene el valor de la propiedad pfValidadorUser.
     * 
     * @return
     *     possible object is
     *     {@link MobileUsuarioGenerico }
     *     
     */
    public MobileUsuarioGenerico getPfValidadorUser() {
        return pfValidadorUser;
    }

    /**
     * Define el valor de la propiedad pfValidadorUser.
     * 
     * @param value
     *     allowed object is
     *     {@link MobileUsuarioGenerico }
     *     
     */
    public void setPfValidadorUser(MobileUsuarioGenerico value) {
        this.pfValidadorUser = value;
    }

    /**
     * Obtiene el valor de la propiedad aplicacionesValidadas.
     * 
     * @return
     *     possible object is
     *     {@link MobileApplicationList }
     *     
     */
    public MobileApplicationList getAplicacionesValidadas() {
        return aplicacionesValidadas;
    }

    /**
     * Define el valor de la propiedad aplicacionesValidadas.
     * 
     * @param value
     *     allowed object is
     *     {@link MobileApplicationList }
     *     
     */
    public void setAplicacionesValidadas(MobileApplicationList value) {
        this.aplicacionesValidadas = value;
    }

    /**
     * Obtiene el valor de la propiedad validadorPorAplicacion.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValidadorPorAplicacion() {
        return validadorPorAplicacion;
    }

    /**
     * Define el valor de la propiedad validadorPorAplicacion.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValidadorPorAplicacion(String value) {
        this.validadorPorAplicacion = value;
    }

}
