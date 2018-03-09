package es.gob.afirma.signfolder.server.proxy;

/**
 * Respuesta de url para firma mediante Cl@ve Firma.
 * @author Sergio Martinez Rico
 */
public class FirePreSignResult {

	/** Referencia de la petici&oacute;n. */
	private final String transactionId;

	/** URL de autenticacion para la firma. */
	private String url;

	private final TriphaseRequestBean triphaseRequestBean;

	/**
	 * Construye un objeto de petici&oacute;n de prefirma con Cl@ve Firma.
	 * @param transactionId Referencia &uacute;nica de la petici&oacute;n.
	 * @param url URL de autenticacion para la firma.
	 * @param triphaseRequestBean Informaci&oacute;n de las peticiones de
	 * firma y su resultado parcial.
	 */
	public FirePreSignResult(final String transactionId, final String url, final TriphaseRequestBean triphaseRequestBean) {
		this.transactionId = transactionId;
		this.url = url;
		this.triphaseRequestBean = triphaseRequestBean;
	}

	/**
	 * Recupera la referencia de la transaccion de FIRe para la firma de documentos.
	 * @return Identificador de la transacci&oacute;n.
	 */
	public String getTransactionId() {
		return this.transactionId;
	}

	/**
	 * Recupera la URL de autenticacion para la firma.
	 * @return URL de autenticacion para la firma.
	 */
	public String getURL() {
		return this.url;
	}

	/**
	 * Establece la URL del servicio de autorizaci&oacute;n.
	 * @param url URL al servicio de autorizaci&oacute;n.
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * Obtiene las firmas informaci&oacute;n de las trif&aacute;sicas parciales
	 * de la petici&oacute;n.
	 * @return Listado de resultados parciales de las firmas trif&aacute;sicas.
	 */
	public TriphaseRequestBean getTriphaseRequestBean() {
		return this.triphaseRequestBean;
	}
}
