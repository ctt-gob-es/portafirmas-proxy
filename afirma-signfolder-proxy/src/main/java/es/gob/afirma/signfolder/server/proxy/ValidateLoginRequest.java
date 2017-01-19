package es.gob.afirma.signfolder.server.proxy;

/**
 * Datos obtenidos de una petici&oacute;n de validaci&oacute;n de acceso.
 * @author Carlos Gamuci
 */
public class ValidateLoginRequest {

	private final byte[] signature;

	/**
	 * Construye los datos de la validaci&oacute;n de acceso.
	 * @param signature Firma electr&oacute;nica CAdES implicita.
	 */
	public ValidateLoginRequest(final byte[] signature) {
		this.signature = signature;
	}

	/**
	 *Recupera la firma de la validaci&oacute;n.
	 * @return Firma de acceso.
	 */
	public byte[] getSignature() {
		return this.signature;
	}

}
