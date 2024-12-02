package es.gob.afirma.signfolder.server.proxy;

public enum OperationError {
	//GENERAL_BAD_REQUEST("COD_000", "Peticion mal formada"), //$NON-NLS-1$ //$NON-NLS-2$
	LOGIN_CERT_INTERNAL_ERROR("COD_001", "Error interno"), //$NON-NLS-1$ //$NON-NLS-2$
	LOGIN_CERT_VALIDATION("COD_002", "Error en el servicio de validación de Portafirmas"), //$NON-NLS-1$ //$NON-NLS-2$
	LOGIN_CERT_UNKNOWN_USER("COD_003", "El usuario no dispone de cuenta en este Portafirmas"), //$NON-NLS-1$ //$NON-NLS-2$
	LOGIN_CERT_EXPIRED_SESSION("COD_004", "Sesion expirada"), //$NON-NLS-1$ //$NON-NLS-2$
	LOGIN_CERT_CERT_EXPIRED("COD_021", "Certificado expirado"), //$NON-NLS-1$ //$NON-NLS-2$
	LOGIN_CERT_CERT_REVOKED("COD_022", "Certificado revocado"), //$NON-NLS-1$ //$NON-NLS-2$
	LOGIN_CLAVE_INTERNAL_ERROR("COD_101", "Error interno"), //$NON-NLS-1$ //$NON-NLS-2$
	LOGIN_CLAVE_CONNECTION("COD_102", "Error de conexion"), //$NON-NLS-1$ //$NON-NLS-2$
	LOGIN_CLAVE_UNKNOWN_USER("COD_103", "Usuario desconocido"), //$NON-NLS-1$ //$NON-NLS-2$
	LOGIN_CLAVE_EXPIRED("COD_104", "Clave caducada"), //$NON-NLS-1$ //$NON-NLS-2$
	LOGIN_CLAVE_REVOKED("COD_105", "Clave revocada"), //$NON-NLS-1$ //$NON-NLS-2$
	LOGIN_CLAVE_EXPIRED_SESSION("COD_106", "Sesion expirada"), //$NON-NLS-1$ //$NON-NLS-2$
	SIGN_INTERNAL_ERROR("COD_301", "Error interno"), //$NON-NLS-1$ //$NON-NLS-2$
	SIGN_INVALID_SIGNATURE("COD_302", "Firma invalida"), //$NON-NLS-1$ //$NON-NLS-2$
	SIGN_INVALID_STATE_DOCUMENT("COD_303", "Documento en mal estado"), //$NON-NLS-1$ //$NON-NLS-2$
	SIGN_CERTIFIED_PDF("COD_304", "PDF certificado"), //$NON-NLS-1$ //$NON-NLS-2$
	SIGN_UNREGISTER_SIGN_PDF("COD_305", "Firma PDF mal registrada"), //$NON-NLS-1$ //$NON-NLS-2$
	SIGN_PROTECTED_PDF("COD_306", "PDF protegido por contrasena"), //$NON-NLS-1$ //$NON-NLS-2$
	SIGN_MODIFIED_SUSPECT_PDF("COD_307", "PDF sospechoso de haber sido modificado"), //$NON-NLS-1$ //$NON-NLS-2$
	SIGN_MODIFIED_FORM_PDF("COD_308", "PDF con formulario modificado"), //$NON-NLS-1$ //$NON-NLS-2$
	SIGN_LTS_SIGNATURE("COD_309", "Firma longeva"); //$NON-NLS-1$ //$NON-NLS-2$

	private final String code;
	private final String description;

	OperationError(final String code, final String description) {
		this.code = code;
		this.description = description;
	}

	public String getCode() {
		return this.code;
	}

	public String getDescription() {
		return this.description;
	}

	@Override
	public String toString() {
		return this.code + ": " + this.description; //$NON-NLS-1$
	}

	/**
	 * Recupera el error con el c&oacute;digo indicado.
	 * @param code C&oacute;digo identificador del error.
	 * @return El error con el c&oacute;digo indicado o {@code null} si no existe.
	 */
	public static OperationError getValueByCode(final String code) {
		return getValueByCode(code, null);
	}

	/**
	 * Recupera el error con el c&oacute;digo indicado.
	 * @param code C&oacute;digo identificador del error.
	 * @param defaultError Valor que devolver si no existe ninguno con el c&oacute;digo indicado.
	 * @return El error con el c&oacute;digo indicado o el valor por defecto si no existe.
	 */
	public static OperationError getValueByCode(final String code, final OperationError defaultError) {
		for (final OperationError error : values()) {
			if (error.getCode().equals(code)) {
				return error;
			}
		}
		return defaultError;
	}
}
