package es.gob.afirma.signfolder.server.proxy;

/**
 * Clase que representa el objeto asociado al resultado de la petición de
 * validación de petición.
 */
public class VerifyPetitionResult {

	static final int ERROR_TYPE_COMMUNICATION = 1;
	static final int ERROR_TYPE_REQUEST = 2;
	static final int ERROR_TYPE_DOCUMENT = 3;

	/**
	 * Cadena que representa el resultado de la operación.
	 */
	private String result;
	
	/**
	 * Cadena que representa el identificador de la petición.
	 */
	private String id;

	/**
	 * Indica si se ha producido un error durante la operación.
	 */
	private int errorType;

	/**
	 * Constructor para resultados recibidos del portafirmas-web.
	 * @param resultParam Resultado recibido.
	 * @param idParam Identificador de petición..
	 */
	public VerifyPetitionResult(String resultParam, String idParam) {
		this.result = resultParam;
		this.id = idParam;
	}

	/**
	 * Constructor para errores.
	 * @param errorParam
	 * @param idParam Identificador de petición.
	 */
	public VerifyPetitionResult(int errorParam, String idParam) {
		this.errorType = errorParam;
		this.id = idParam;
	}

	/**
	 * @return the result
	 */
	public String getResult() {
		return result;
	}

	/**
	 * @param result
	 *            the result to set
	 */
	public void setResult(String result) {
		this.result = result;
	}
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the errorType
	 */
	public int getErrorType() {
		return errorType;
	}

	/**
	 * @param errorType
	 *            the errorType to set
	 */
	public void setErrorType(int errorType) {
		this.errorType = errorType;
	}

}
