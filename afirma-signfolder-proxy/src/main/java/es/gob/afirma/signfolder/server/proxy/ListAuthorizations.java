package es.gob.afirma.signfolder.server.proxy;

import java.util.List;

/**
 * Resultado del proceso de listar las autorizaciones de un usuario.
 */
public class ListAuthorizations {

	/** Error de comunicaci&oacute;n con el servicio remoto. */
	static final int ERROR_TYPE_COMMUNICATION = 1;

	/** Listadode autorizaciones. */
	private List<Authorization> authorizationsList = null;

	/** Indica si se ha producido un error durante la operaci&oacute;n. */
	private int errorType = 0;

	/**
	 * Crea el objeto con el resultado de listar las autorizaciones.
	 * @param authorizationsList Listado de autorizaci&oacute;nes del usuario.
	 */
	public ListAuthorizations(final List<Authorization> authorizationsList) {
		this.authorizationsList = authorizationsList;
	}

	/**
	 * Crea el objeto con el resultado de listar las autorizaciones.
	 * @param errorType Tipo de error producido durante el listado de las autorizaciones.
	 */
	public ListAuthorizations(final int errorType) {
		this.errorType = errorType;
	}

	/**
	 * Obtiene el listado de la autorizaciones de usuario.
	 * @return Listado de la autorizaciones de usuario.
	 */
	public List<Authorization> getAuthorizationsList() {
		return this.authorizationsList;
	}

	/**
	 * Obtiene el tipo de error producido al listar las autorizaciones.
	 * @return Tipo de error producido.
	 */
	public int getErrorType() {
		return this.errorType;
	}

	/**
	 * Indica si se produjo un error durante el listado de autorizaciones.
	 * @return {@code true} si se produjo un error, {@code false} en caso contrario.
	 */
	public boolean isError() {
		return this.errorType != 0;
	}
}
