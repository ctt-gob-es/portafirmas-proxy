
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
 *         &lt;element name="accion" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="idAutorizacion" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "accion",
    "idAutorizacion"
})
@XmlRootElement(name = "cambiarEstadoAutorizacionMobile")
public class CambiarEstadoAutorizacionMobile {

    @XmlElement(required = true)
    protected byte[] dni;
    @XmlElement(required = true)
    protected String accion;
    @XmlElement(required = true)
    protected String idAutorizacion;

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
     * Obtiene el valor de la propiedad accion.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAccion() {
        return accion;
    }

    /**
     * Define el valor de la propiedad accion.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAccion(String value) {
        this.accion = value;
    }

    /**
     * Obtiene el valor de la propiedad idAutorizacion.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdAutorizacion() {
        return idAutorizacion;
    }

    /**
     * Define el valor de la propiedad idAutorizacion.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdAutorizacion(String value) {
        this.idAutorizacion = value;
    }

}
