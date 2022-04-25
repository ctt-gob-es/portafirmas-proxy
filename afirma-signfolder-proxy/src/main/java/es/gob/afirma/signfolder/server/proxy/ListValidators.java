package es.gob.afirma.signfolder.server.proxy;

import java.util.List;

/**
 * Resultado del proceso de listar los validadores del usuario.
 */
public class ListValidators {

	/** Error de comunicaci&oacute;n con el servicio remoto. */
	static final int ERROR_TYPE_COMMUNICATION = 1;

	/** Listado de validadores. */
	private List<Validator> validatorsList = null;

	/** Indica si se ha producido un error durante la operaci&oacute;n. */
	private int errorType = 0;

	/**
	 * Crea el objeto con el resultado de listar los validadores.
	 * @param validatorsList Listado de validadores del usuario.
	 */
	public ListValidators(final List<Validator> validatorsList) {
		this.validatorsList = validatorsList;
	}

	/**
	 * Crea el objeto con el resultado de listar los validadores.
	 * @param errorType Tipo de error producido durante el listado de los validadores.
	 */
	public ListValidators(final int errorType) {
		this.errorType = errorType;
	}

	/**
	 * Obtiene el listado de la autorizaciones de usuario.
	 * @return Listado de la autorizaciones de usuario.
	 */
	public List<Validator> getValidatorsList() {
		return this.validatorsList;
	}

	/**
	 * Obtiene el tipo de error producido al listar los validadores.
	 * @return Tipo de error producido.
	 */
	public int getErrorType() {
		return this.errorType;
	}

	/**
	 * Indica si se produjo un error durante el listado de validadores.
	 * @return {@code true} si se produjo un error, {@code false} en caso contrario.
	 */
	public boolean isError() {
		return this.errorType != 0;
	}
}
