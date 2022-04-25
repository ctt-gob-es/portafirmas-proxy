
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
 *         &lt;element name="tiposAutorizacionesList" type="{urn:juntadeandalucia:cice:pfirma:mobile:type:v2.0}mobileTiposAutorizacionList"/>
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
    "tiposAutorizacionesList"
})
@XmlRootElement(name = "tiposAutorizacionResponse")
public class TiposAutorizacionResponse {

    @XmlElement(required = true)
    protected MobileTiposAutorizacionList tiposAutorizacionesList;

    /**
     * Obtiene el valor de la propiedad tiposAutorizacionesList.
     * 
     * @return
     *     possible object is
     *     {@link MobileTiposAutorizacionList }
     *     
     */
    public MobileTiposAutorizacionList getTiposAutorizacionesList() {
        return tiposAutorizacionesList;
    }

    /**
     * Define el valor de la propiedad tiposAutorizacionesList.
     * 
     * @param value
     *     allowed object is
     *     {@link MobileTiposAutorizacionList }
     *     
     */
    public void setTiposAutorizacionesList(MobileTiposAutorizacionList value) {
        this.tiposAutorizacionesList = value;
    }

}
