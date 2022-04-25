package es.gob.afirma.signfolder.server.proxy;

import java.util.List;

/**
 * Resultado de la llamada al servicio de b&uacute;squeda de usuarios.
 */
public class FindUserResult {

	static final int ERROR_TYPE_COMMUNICATION = 1;

	/**
	 * Lista de usuarios de la respuesta del servicio.
	 */
	private List<GenericUser> users;

	/**
	 * Tipo de error.
	 */
	private int errorType = 0;

	/**
	 * Constructor for the success cases.
	 * @param users List of users.
	 */
	public FindUserResult(final List<GenericUser> users) {
		this.users = users;
	}

	/**
	 * Constructor for the fails cases.
	 * @param errorType Error type.
	 */
	public FindUserResult(final int errorType) {
		setErrorType(errorType);
	}

    /**
     * Get method for the <i>users</i> attribute.
     * @return the value of the attribute.
     */
	public List<GenericUser> getUsers() {
		return this.users;
	}

    /**
     * Set method for the <i>users</i> attribute.
     * @param users new value of the attribute.
     */
	public void setUsers(final List<GenericUser> users) {
		this.users = users;
	}

    /**
     * Get method for the <i>error</i> attribute.
     * @return the value of the attribute.
     */
	public boolean isError() {
		return this.errorType != 0;
	}

    /**
     * Get method for the <i>errorType</i> attribute.
     * @return the value of the attribute.
     */
	public int getErrorType() {
		return this.errorType;
	}

    /**
     * Set method for the <i>errorType</i> attribute.
     * @param errorType new value of the attribute.
     */
	public void setErrorType(final int errorType) {
		this.errorType = errorType;
	}

}
