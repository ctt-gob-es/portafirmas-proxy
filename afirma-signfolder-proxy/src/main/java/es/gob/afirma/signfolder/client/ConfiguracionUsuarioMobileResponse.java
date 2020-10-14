
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
 *         &lt;element name="mobileConfiguracionUsuario" type="{urn:juntadeandalucia:cice:pfirma:mobile:type:v2.0}mobileConfiguracionUsuario"/>
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
    "mobileConfiguracionUsuario"
})
@XmlRootElement(name = "configuracionUsuarioMobileResponse")
public class ConfiguracionUsuarioMobileResponse {

    @XmlElement(required = true)
    protected MobileConfiguracionUsuario mobileConfiguracionUsuario;

    /**
     * Obtiene el valor de la propiedad mobileConfiguracionUsuario.
     * 
     * @return
     *     possible object is
     *     {@link MobileConfiguracionUsuario }
     *     
     */
    public MobileConfiguracionUsuario getMobileConfiguracionUsuario() {
        return mobileConfiguracionUsuario;
    }

    /**
     * Define el valor de la propiedad mobileConfiguracionUsuario.
     * 
     * @param value
     *     allowed object is
     *     {@link MobileConfiguracionUsuario }
     *     
     */
    public void setMobileConfiguracionUsuario(MobileConfiguracionUsuario value) {
        this.mobileConfiguracionUsuario = value;
    }

}
