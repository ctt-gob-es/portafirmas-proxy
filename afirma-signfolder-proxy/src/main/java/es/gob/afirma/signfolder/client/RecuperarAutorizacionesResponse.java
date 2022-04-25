
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
 *         &lt;element name="autorizacionesList" type="{urn:juntadeandalucia:cice:pfirma:mobile:type:v2.0}mobileAutorizacionesList"/>
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
    "autorizacionesList"
})
@XmlRootElement(name = "recuperarAutorizacionesResponse")
public class RecuperarAutorizacionesResponse {

    @XmlElement(required = true)
    protected MobileAutorizacionesList autorizacionesList;

    /**
     * Obtiene el valor de la propiedad autorizacionesList.
     * 
     * @return
     *     possible object is
     *     {@link MobileAutorizacionesList }
     *     
     */
    public MobileAutorizacionesList getAutorizacionesList() {
        return autorizacionesList;
    }

    /**
     * Define el valor de la propiedad autorizacionesList.
     * 
     * @param value
     *     allowed object is
     *     {@link MobileAutorizacionesList }
     *     
     */
    public void setAutorizacionesList(MobileAutorizacionesList value) {
        this.autorizacionesList = value;
    }

}
