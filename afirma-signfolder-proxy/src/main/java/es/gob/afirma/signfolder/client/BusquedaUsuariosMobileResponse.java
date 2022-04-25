
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
 *         &lt;element name="mobileUsuariosList" type="{urn:juntadeandalucia:cice:pfirma:mobile:type:v2.0}mobileUsuariosList"/>
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
    "mobileUsuariosList"
})
@XmlRootElement(name = "busquedaUsuariosMobileResponse")
public class BusquedaUsuariosMobileResponse {

    @XmlElement(required = true)
    protected MobileUsuariosList mobileUsuariosList;

    /**
     * Obtiene el valor de la propiedad mobileUsuariosList.
     * 
     * @return
     *     possible object is
     *     {@link MobileUsuariosList }
     *     
     */
    public MobileUsuariosList getMobileUsuariosList() {
        return mobileUsuariosList;
    }

    /**
     * Define el valor de la propiedad mobileUsuariosList.
     * 
     * @param value
     *     allowed object is
     *     {@link MobileUsuariosList }
     *     
     */
    public void setMobileUsuariosList(MobileUsuariosList value) {
        this.mobileUsuariosList = value;
    }

}
