package es.gob.afirma.signfolder.server.proxy;

/**
 * Petici&oacute;n de b&uacute;squeda de usuario.
 */
public class FindUserRequest {

	private final String mode;

	private final String text;

	/**
	 * Crea la petici&oacute;n con el modo de b&uacute;squeda (la finalidad para la que se busca al usuario) y
	 * el texto por el que buscar.
	 * @param mode Modo de b&uacute;squeda. Inicialmente se soporta {@code autorizados} y {@code validadores}.
	 * @param text Texto por el que buscar (parte del nombre o el DNI del usuario).
	 */
	public FindUserRequest(final String mode, final String text) {
		this.mode = mode;
		this.text = text;
	}

	/**
	 * Recupera el modo de b&uacute;squeda declarado.
	 * @return Modo de b&uacute;squeda.
	 */
	public String getMode() {
		return this.mode;
	}

	/**
	 * Recupera el texto de b&uacute;squeda.
	 * @return Texto de b&uacute;squeda.
	 */
	public String getText() {
		return this.text;
	}
}
