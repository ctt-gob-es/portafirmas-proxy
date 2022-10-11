package es.gob.afirma.signfolder.server.proxy;

/**
 * Clase que representa el objeto asociado al resultado de la petici&oacute;n de
 * validaci&oacute;n de petici&oacute;n.
 */
public class VerifyPetitionResult {

	static final int ERROR_TYPE_COMMUNICATION = 1;
	static final int ERROR_TYPE_REQUEST = 2;
	static final int ERROR_TYPE_DOCUMENT = 3;

	/**
	 * Cadena que representa el resultado de la operaci&oacute;n.
	 */
	private String result;

	/**
	 * Cadena que representa el identificador de la petici&oacute;n.
	 */
	private String id;

	/**
	 * Indica si se ha producido un error durante la operaci&oacute;n.
	 */
	private int errorType;

	/**
	 * Constructor para resultados recibidos del portafirmas-web.
	 * @param resultParam Resultado recibido.
	 * @param idParam Identificador de petici&oacute;n..
	 */
	public VerifyPetitionResult(final String resultParam, final String idParam) {
		this.result = resultParam;
		this.id = idParam;
	}

	/**
	 * Constructor para errores.
	 * @param errorParam Identificador del tipo de error.
	 * @param idParam Identificador de petici&oacute;n.
	 */
	public VerifyPetitionResult(final int errorParam, final String idParam) {
		this.errorType = errorParam;
		this.id = idParam;
	}

	/**
	 * @return the result
	 */
	public String getResult() {
		return this.result;
	}

	/**
	 * @param result
	 *            the result to set
	 */
	public void setResult(final String result) {
		this.result = result;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(final String id) {
		this.id = id;
	}

	/**
	 * @return the errorType
	 */
	public int getErrorType() {
		return this.errorType;
	}

	/**
	 * @param errorType
	 *            the errorType to set
	 */
	public void setErrorType(final int errorType) {
		this.errorType = errorType;
	}

}
