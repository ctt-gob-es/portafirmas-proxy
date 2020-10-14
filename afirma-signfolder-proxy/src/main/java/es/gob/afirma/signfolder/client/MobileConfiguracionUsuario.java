
package es.gob.afirma.signfolder.client;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para mobileConfiguracionUsuario complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="mobileConfiguracionUsuario">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="mobileFiltroApplicationList" type="{urn:juntadeandalucia:cice:pfirma:mobile:type:v2.0}mobileApplicationList" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="valorNotifyPush" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="rolesList" type="{urn:juntadeandalucia:cice:pfirma:mobile:type:v2.0}mobileRoleList" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="parametrosSIMConfigurados" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="usuarioConValidadores" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="mobileFiltroTipoList" type="{urn:juntadeandalucia:cice:pfirma:mobile:type:v2.0}mobileFiltroTipoList" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="mobileFiltroEtiquetaList" type="{urn:juntadeandalucia:cice:pfirma:mobile:type:v2.0}mobileFiltroEtiquetaList" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="mobileFiltroMesList" type="{urn:juntadeandalucia:cice:pfirma:mobile:type:v2.0}mobileFiltroMesList" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="mobileFiltroAnioList" type="{urn:juntadeandalucia:cice:pfirma:mobile:type:v2.0}mobileFiltroAnioList" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mobileConfiguracionUsuario", namespace = "urn:juntadeandalucia:cice:pfirma:mobile:type:v2.0", propOrder = {
    "mobileFiltroApplicationList",
    "valorNotifyPush",
    "rolesList",
    "parametrosSIMConfigurados",
    "usuarioConValidadores",
    "mobileFiltroTipoList",
    "mobileFiltroEtiquetaList",
    "mobileFiltroMesList",
    "mobileFiltroAnioList"
})
public class MobileConfiguracionUsuario {

    protected List<MobileApplicationList> mobileFiltroApplicationList;
    protected List<String> valorNotifyPush;
    protected List<MobileRoleList> rolesList;
    protected List<String> parametrosSIMConfigurados;
    protected List<String> usuarioConValidadores;
    protected List<MobileFiltroTipoList> mobileFiltroTipoList;
    protected List<MobileFiltroEtiquetaList> mobileFiltroEtiquetaList;
    protected List<MobileFiltroMesList> mobileFiltroMesList;
    protected List<MobileFiltroAnioList> mobileFiltroAnioList;

    /**
     * Gets the value of the mobileFiltroApplicationList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the mobileFiltroApplicationList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMobileFiltroApplicationList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MobileApplicationList }
     * 
     * 
     */
    public List<MobileApplicationList> getMobileFiltroApplicationList() {
        if (mobileFiltroApplicationList == null) {
            mobileFiltroApplicationList = new ArrayList<MobileApplicationList>();
        }
        return this.mobileFiltroApplicationList;
    }

    /**
     * Gets the value of the valorNotifyPush property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the valorNotifyPush property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getValorNotifyPush().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getValorNotifyPush() {
        if (valorNotifyPush == null) {
            valorNotifyPush = new ArrayList<String>();
        }
        return this.valorNotifyPush;
    }

    /**
     * Gets the value of the rolesList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the rolesList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRolesList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MobileRoleList }
     * 
     * 
     */
    public List<MobileRoleList> getRolesList() {
        if (rolesList == null) {
            rolesList = new ArrayList<MobileRoleList>();
        }
        return this.rolesList;
    }

    /**
     * Gets the value of the parametrosSIMConfigurados property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the parametrosSIMConfigurados property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getParametrosSIMConfigurados().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getParametrosSIMConfigurados() {
        if (parametrosSIMConfigurados == null) {
            parametrosSIMConfigurados = new ArrayList<String>();
        }
        return this.parametrosSIMConfigurados;
    }

    /**
     * Gets the value of the usuarioConValidadores property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the usuarioConValidadores property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUsuarioConValidadores().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getUsuarioConValidadores() {
        if (usuarioConValidadores == null) {
            usuarioConValidadores = new ArrayList<String>();
        }
        return this.usuarioConValidadores;
    }

    /**
     * Gets the value of the mobileFiltroTipoList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the mobileFiltroTipoList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMobileFiltroTipoList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MobileFiltroTipoList }
     * 
     * 
     */
    public List<MobileFiltroTipoList> getMobileFiltroTipoList() {
        if (mobileFiltroTipoList == null) {
            mobileFiltroTipoList = new ArrayList<MobileFiltroTipoList>();
        }
        return this.mobileFiltroTipoList;
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

    /**
     * Gets the value of the mobileFiltroMesList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the mobileFiltroMesList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMobileFiltroMesList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MobileFiltroMesList }
     * 
     * 
     */
    public List<MobileFiltroMesList> getMobileFiltroMesList() {
        if (mobileFiltroMesList == null) {
            mobileFiltroMesList = new ArrayList<MobileFiltroMesList>();
        }
        return this.mobileFiltroMesList;
    }

    /**
     * Gets the value of the mobileFiltroAnioList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the mobileFiltroAnioList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMobileFiltroAnioList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MobileFiltroAnioList }
     * 
     * 
     */
    public List<MobileFiltroAnioList> getMobileFiltroAnioList() {
        if (mobileFiltroAnioList == null) {
            mobileFiltroAnioList = new ArrayList<MobileFiltroAnioList>();
        }
        return this.mobileFiltroAnioList;
    }

}
