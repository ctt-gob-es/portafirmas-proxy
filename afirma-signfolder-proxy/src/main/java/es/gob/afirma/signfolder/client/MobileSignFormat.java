
package es.gob.afirma.signfolder.client;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for mobileSignFormat.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="mobileSignFormat">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="PKCS7"/>
 *     &lt;enumeration value="CMS"/>
 *     &lt;enumeration value="CADES"/>
 *     &lt;enumeration value="XADES"/>
 *     &lt;enumeration value="XADES IMPLICITO"/>
 *     &lt;enumeration value="XADES EXPLICITO"/>
 *     &lt;enumeration value="XADES ENVELOPING"/>
 *     &lt;enumeration value="XADES ENVELOPED"/>
 *     &lt;enumeration value="PDF"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "mobileSignFormat", namespace = "urn:juntadeandalucia:cice:pfirma:mobile:type:v2.0")
@XmlEnum
public enum MobileSignFormat {

	/** PKCS7 */
	@XmlEnumValue("PKCS7")
    PKCS_7("PKCS7"), //$NON-NLS-1$

    /** CMS */
    CMS("CMS"), //$NON-NLS-1$

    /** CAdESS */
    CADES("CADES"), //$NON-NLS-1$

    /** XAdES */
    XADES("XADES"), //$NON-NLS-1$

    /** XADES IMPLICITO */
    @XmlEnumValue("XADES IMPLICITO")
    XADES_IMPLICITO("XADES IMPLICITO"), //$NON-NLS-1$

    /** XADES EXPLICITO */
    @XmlEnumValue("XADES EXPLICITO")
    XADES_EXPLICITO("XADES EXPLICITO"), //$NON-NLS-1$

    /** XADES ENVELOPING */
    @XmlEnumValue("XADES ENVELOPING")
    XADES_ENVELOPING("XADES ENVELOPING"), //$NON-NLS-1$

    /** XADES ENVELOPED */
    @XmlEnumValue("XADES ENVELOPED")
    XADES_ENVELOPED("XADES ENVELOPED"), //$NON-NLS-1$
    /** PDF */
    PDF("PDF"); //$NON-NLS-1$

    private final String value;

    MobileSignFormat(final String v) {
        this.value = v;
    }

    /**
     * Obtiene el valor del elemento.
     * @return Valor.
     */
    public String value() {
        return this.value;
    }

    /**
     * Obtiene un elemento a partir de su valor.
     * @param v Valor.
     * @return Elemento enumerado.
     */
    public static MobileSignFormat fromValue(final String v) {
        for (final MobileSignFormat c: MobileSignFormat.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
