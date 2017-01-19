package es.gob.afirma.signfolder.server.proxy;


/**
 * Resultado del proceso de validaci&oacute;n del acceso en la aplicaci&oacute;n.
 * @author Carlos Gamuci
 */
class ValidateLoginResult {

	private boolean logged = false;

	private String error = null;

	/** Construye el resultado de la validaci&oacute;n indicando que se ha completado correctamente. */
	public ValidateLoginResult() {
		this.logged = true;
	}

	/** Construye el resultado de la validaci&oacute;n indicando que se ha producido un error.
	 * @param error Mensaje de error. */
	public ValidateLoginResult(final String error) {
		this.error = error;
	}

	boolean isLogged() {
		return this.logged;
	}

	Object getError() {
		return this.error;
	}
}
