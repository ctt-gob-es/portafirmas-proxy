package es.gob.afirma.signfolder.server.proxy;

/**
 * Configuraci&oacute;n de cambio de estado de una autorizaci&oacute;n.
 */
public class AuthorizationStatusChange {

	static final String AUTH_ACTION_ACCEPT = "aceptar"; //$NON-NLS-1$
	static final String AUTH_ACTION_REJECT = "rechazar"; //$NON-NLS-1$
	static final String AUTH_ACTION_REVOKE = "revocar"; //$NON-NLS-1$

	/** Identificador de la autorizaci&oacute;n. */
	private final String authId;

	/** Operaci&oacute;n de cambio de estado. */
	private final String operation;

	/**
	 * Construye la configuraci&oacute;n de cambio de estado de una autorizaci&oacute;n.
	 * @param authId Identificador de la autorizaci&oacute;n.
	 * @param operation Identificador de la operaci&oacute;n de cambio de estado.
	 */
	public AuthorizationStatusChange(final String authId, final String operation) {
		this.authId = authId;
		this.operation = operation;
	}

	/**
	 * Obtiene el identificador de la autorizaci&oacute;n a la que cambiar el estado.
	 * @return Identificador de autorizaci&oacute;n.
	 */
	public String getAuthId() {
		return this.authId;
	}

	/**
	 * Obtiene el identificador de la operaci&oacute;n de cambio de estado.
	 * @return Identificador de la operaci&oacute;n de cambio de estado.
	 */
	public String getOperation() {
		return this.operation;
	}
}
