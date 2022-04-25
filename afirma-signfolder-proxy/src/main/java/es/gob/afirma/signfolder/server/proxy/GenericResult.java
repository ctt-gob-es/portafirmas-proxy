package es.gob.afirma.signfolder.server.proxy;

/**
 * Resultado gen&eacute;rico de una petici&oacute;n al servicio proxy. &Uacte;nicamente indica
 * si la operaci&oacute;n finaliz&oacute; correctamente o si se produjo un error.
 */
public class GenericResult {

	static final int ERROR_TYPE_COMMUNICATION = 1;
	static final int ERROR_TYPE_REQUEST = 2;
	static final int ERROR_TYPE_DOCUMENT = 3;

	/**
	 * Bandera que indica si la operaci&oacute;n se ha realizado correctamente
	 * <i>true</i> o no <i>false</i>.
	 */
	private boolean success = false;

	/**
	 * Tipo de error producido durante la operaci&oacute;n.
	 */
	private int errorType = 0;

	/**
	 * Mensaje de error establecido.
	 */
	private String errorMessage = null;

	/**
	 * Construye el objeto indicando que la operaci&oacute;n finaliz&oacute; con &eacute;xito.
	 * @param success Bandera que indica si el resultado ha sido correcto.
	 */
	public GenericResult(final boolean success) {
		this.success = success;
	}

	/**
	 * Construye el objeto indicando que se produjo un error.
	 * @param errorType N&uacute;mero que indica el tipo de error producido.
	 */
	public GenericResult(final int errorType) {
		this.errorType = errorType;
	}

	/**
	 * Construye el objeto indicando que se produjo un error.
	 * @param errorType N&uacute;mero que indica el tipo de error producido.
	 * @param message Mensaje de error.
	 */
	public GenericResult(final int errorType, final String message) {
		this.errorType = errorType;
		this.errorMessage = message;
	}

	/**
	 * Indica si la operaci&oacute;n tuvo &eacute;xito o no.
	 * @return {@code true} si la operaci&oacute;n tuvo &eacute;xito, {@code false}
	 * en caso contrario.
	 */
	public boolean isSuccess() {
		return this.success;
	}

	/**
	 * Establece si la operaci&oacute;n tuvo &eacute;xito.
	 * @param success {@code true} si la operaci&oacute;n tuvo &eacute;xito, {@code false}
	 */
	public void setSuccess(final boolean success) {
		this.success = success;
	}

	/**
	 * Recupera el tipo de error producido. Si se devuelve 0, no se produjo ning&uacute;n error.
	 * @return Tipo de error.
	 */
	public int getErrorType() {
		return this.errorType;
	}

	/**
	 * Establece el tipo de error producido.
	 * @param errorType Tipo de error.
	 */
	public void setErrorType(final int errorType) {
		this.errorType = errorType;
	}

	/**
	 * Recupera el mensaje de error producido. Si se devuelve {@code null}, no se ha establecido ning&uacute;n mensaje.
	 * @return Mensaje de error.
	 */
	public String getErrorMessage() {
		return this.errorMessage;
	}

	/**
	 * Establece el mensaje de error producido.
	 * @param errorMessage Mensaje de error.
	 */
	public void setErrorMessage(final String errorMessage) {
		this.errorMessage = errorMessage;
	}
}
