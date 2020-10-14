
package es.gob.afirma.signfolder.client;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para mobileRole complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="mobileRole">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="idRole" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nameRole" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nameUsuario" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dniUsuario" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="mobileFiltroEtiquetaList" type="{urn:juntadeandalucia:cice:pfirma:mobile:type:v2.0}mobileFiltroEtiquetaList" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mobileRole", namespace = "urn:juntadeandalucia:cice:pfirma:mobile:type:v2.0", propOrder = {
    "idRole",
    "nameRole",
    "nameUsuario",
    "dniUsuario",
    "mobileFiltroEtiquetaList"
})
public class MobileRole {

    protected String idRole;
    protected String nameRole;
    protected String nameUsuario;
    protected String dniUsuario;
    protected List<MobileFiltroEtiquetaList> mobileFiltroEtiquetaList;

    /**
     * Obtiene el valor de la propiedad idRole.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdRole() {
        return idRole;
    }

    /**
     * Define el valor de la propiedad idRole.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdRole(String value) {
        this.idRole = value;
    }

    /**
     * Obtiene el valor de la propiedad nameRole.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNameRole() {
        return nameRole;
    }

    /**
     * Define el valor de la propiedad nameRole.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNameRole(String value) {
        this.nameRole = value;
    }

    /**
     * Obtiene el valor de la propiedad nameUsuario.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNameUsuario() {
        return nameUsuario;
    }

    /**
     * Define el valor de la propiedad nameUsuario.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNameUsuario(String value) {
        this.nameUsuario = value;
    }

    /**
     * Obtiene el valor de la propiedad dniUsuario.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDniUsuario() {
        return dniUsuario;
    }

    /**
     * Define el valor de la propiedad dniUsuario.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDniUsuario(String value) {
        this.dniUsuario = value;
    }

    /**
     * Gets the value of the mobileFiltroEtiquetaList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the mobileFiltroEtiquetaList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMobileFiltroEtiquetaList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MobileFiltroEtiquetaList }
     * 
     * 
     */
    public List<MobileFiltroEtiquetaList> getMobileFiltroEtiquetaList() {
        if (mobileFiltroEtiquetaList == null) {
            mobileFiltroEtiquetaList = new ArrayList<MobileFiltroEtiquetaList>();
        }
        return this.mobileFiltroEtiquetaList;
    }

}
