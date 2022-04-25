
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
 *         &lt;element name="mobileValidadorList" type="{urn:juntadeandalucia:cice:pfirma:mobile:type:v2.0}mobileValidadorList"/>
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
    "mobileValidadorList"
})
@XmlRootElement(name = "recuperarValidadoresMobileResponse")
public class RecuperarValidadoresMobileResponse {

    @XmlElement(required = true)
    protected MobileValidadorList mobileValidadorList;

    /**
     * Obtiene el valor de la propiedad mobileValidadorList.
     * 
     * @return
     *     possible object is
     *     {@link MobileValidadorList }
     *     
     */
    public MobileValidadorList getMobileValidadorList() {
        return mobileValidadorList;
    }

    /**
     * Define el valor de la propiedad mobileValidadorList.
     * 
     * @param value
     *     allowed object is
     *     {@link MobileValidadorList }
     *     
     */
    public void setMobileValidadorList(MobileValidadorList value) {
        this.mobileValidadorList = value;
    }

}
