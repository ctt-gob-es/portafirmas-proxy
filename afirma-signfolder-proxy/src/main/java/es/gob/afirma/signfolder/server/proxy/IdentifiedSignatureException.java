package es.gob.afirma.signfolder.server.proxy;

/**
 * Excepci&oacute;n que se&ntilde;ala un tipo de error conocido durante la operaci&oacute;n
 * de firma.
 */
public class IdentifiedSignatureException extends Exception {

	/** Serial Id. */
	private static final long serialVersionUID = -5355252343325977867L;

	private final String errorCode;

	/**
	 * Construye la excepci&oacute;n.
	 * @param error Error de firma.
	 */
	public IdentifiedSignatureException(final OperationError error) {
		super(error.getDescription());
		this.errorCode = error.getCode();
	}

	/**
	 * Construye la excepci&oacute;n.
	 * @param error Error de firma.
	 * @param cause Excepci&oacute;n que caus&oacute; el error de firma.
	 */
	public IdentifiedSignatureException(final OperationError error, final Throwable cause) {
		super(error.getDescription(), cause);
		this.errorCode = error.getCode();
	}

	/**
	 * Obtiene el c&oacute;digo del error.
	 * @return C&oacute;digo del error.
	 */
	public String getErrorCode() {
		return this.errorCode;
	}

	@Override
	public String toString() {
		return this.errorCode + ": " + getMessage(); //$NON-NLS-1$
	}
}
