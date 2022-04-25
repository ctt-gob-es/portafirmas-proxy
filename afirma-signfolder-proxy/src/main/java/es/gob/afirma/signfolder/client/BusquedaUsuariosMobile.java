
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
 *         &lt;element name="dni" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/>
 *         &lt;element name="filtroBusqueda" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="modoBusqueda" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="parametros" type="{urn:juntadeandalucia:cice:pfirma:mobile:type:v2.0}mobileParameterList"/>
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
    "dni",
    "filtroBusqueda",
    "modoBusqueda",
    "parametros"
})
@XmlRootElement(name = "busquedaUsuariosMobile")
public class BusquedaUsuariosMobile {

    @XmlElement(required = true)
    protected byte[] dni;
    @XmlElement(required = true)
    protected String filtroBusqueda;
    @XmlElement(required = true)
    protected String modoBusqueda;
    @XmlElement(required = true)
    protected MobileParameterList parametros;

    /**
     * Obtiene el valor de la propiedad dni.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getDni() {
        return dni;
    }

    /**
     * Define el valor de la propiedad dni.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setDni(byte[] value) {
        this.dni = value;
    }

    /**
     * Obtiene el valor de la propiedad filtroBusqueda.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFiltroBusqueda() {
        return filtroBusqueda;
    }

    /**
     * Define el valor de la propiedad filtroBusqueda.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFiltroBusqueda(String value) {
        this.filtroBusqueda = value;
    }

    /**
     * Obtiene el valor de la propiedad modoBusqueda.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getModoBusqueda() {
        return modoBusqueda;
    }

    /**
     * Define el valor de la propiedad modoBusqueda.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setModoBusqueda(String value) {
        this.modoBusqueda = value;
    }

    /**
     * Obtiene el valor de la propiedad parametros.
     * 
     * @return
     *     possible object is
     *     {@link MobileParameterList }
     *     
     */
    public MobileParameterList getParametros() {
        return parametros;
    }

    /**
     * Define el valor de la propiedad parametros.
     * 
     * @param value
     *     allowed object is
     *     {@link MobileParameterList }
     *     
     */
    public void setParametros(MobileParameterList value) {
        this.parametros = value;
    }

}
