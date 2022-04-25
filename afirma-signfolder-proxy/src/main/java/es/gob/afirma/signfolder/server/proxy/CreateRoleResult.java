package es.gob.afirma.signfolder.server.proxy;

/**
 * Clase que representa el objeto asociado al resultado de la petición de
 * creación de roles.
 */
public class CreateRoleResult {

	static final int ERROR_TYPE_COMMUNICATION = 1;
	static final int ERROR_TYPE_REQUEST = 2;
	static final int ERROR_TYPE_DOCUMENT = 3;

	/**
	 * Bandera que indica si la operaci&oacute;n se ha realizado correctamente
	 * <i>true</i> o no <i>false</i>.
	 */
	private boolean success;

	/**
	 * Indica si se ha producido un error durante la operaci&oacute;n.
	 */
	private int errorType;

	/**
	 * Constructor.
	 * @param success Bandera que indica si el resultado ha sido correcto.
	 */
	public CreateRoleResult(final boolean success) {
		super();
		this.success = success;
	}

	/**
	 * Constructor.
	 * @param errorType N&uacute;mero que indica el tipo de error producido.
	 */
	public CreateRoleResult(final int errorType) {
		super();
		this.errorType = errorType;
	}



	/**
	 * @return the success
	 */
	public boolean isSuccess() {
		return this.success;
	}

	/**
	 * @param success the success to set
	 */
	public void setSuccess(final boolean success) {
		this.success = success;
	}

	/**
	 * @return the errorType
	 */
	public int getErrorType() {
		return this.errorType;
	}

	/**
	 * @param errorType the errorType to set
	 */
	public void setErrorType(final int errorType) {
		this.errorType = errorType;
	}


}
