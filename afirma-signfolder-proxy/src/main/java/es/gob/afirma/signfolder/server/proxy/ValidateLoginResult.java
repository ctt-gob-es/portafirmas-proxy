package es.gob.afirma.signfolder.server.proxy;


/**
 * Resultado del proceso de validaci&oacute;n del acceso en la aplicaci&oacute;n.
 * @author Carlos Gamuci
 */
class ValidateLoginResult {

	private boolean logged = false;

	private OperationError error = null;
	private String errorMessage = null;

	private String dni = null;

	/** Construye el resultado de la validaci&oacute;n indicando que se ha completado correctamente. */
	public ValidateLoginResult() {
		this.logged = true;
	}

	boolean isLogged() {
		return this.logged;
	}

	void setDni(final String dni) {
		this.dni = dni;
	}

	public String getDni() {
		return this.dni;
	}

	void setError(final OperationError error) {
		this.error = error;
		this.errorMessage = null;
		this.logged = false;
	}

	void setError(final OperationError error, final String errorMessage) {
		this.error = error;
		this.errorMessage = errorMessage;
		this.logged = false;
	}

	OperationError getError() {
		return this.error;
	}

	public String getErrorMessage() {
		return this.errorMessage;
	}
}
