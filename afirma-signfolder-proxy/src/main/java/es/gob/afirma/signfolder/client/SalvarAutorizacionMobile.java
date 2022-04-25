
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
 *         &lt;element name="mobileAutorizacion" type="{urn:juntadeandalucia:cice:pfirma:mobile:type:v2.0}mobileAutorizacion"/>
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
    "mobileAutorizacion"
})
@XmlRootElement(name = "salvarAutorizacionMobile")
public class SalvarAutorizacionMobile {

    @XmlElement(required = true)
    protected MobileAutorizacion mobileAutorizacion;

    /**
     * Obtiene el valor de la propiedad mobileAutorizacion.
     * 
     * @return
     *     possible object is
     *     {@link MobileAutorizacion }
     *     
     */
    public MobileAutorizacion getMobileAutorizacion() {
        return mobileAutorizacion;
    }

    /**
     * Define el valor de la propiedad mobileAutorizacion.
     * 
     * @param value
     *     allowed object is
     *     {@link MobileAutorizacion }
     *     
     */
    public void setMobileAutorizacion(MobileAutorizacion value) {
        this.mobileAutorizacion = value;
    }

}
