package es.gob.afirma.signfolder.server.proxy;

/**
 * L&iacute;nea de firma. Contiene el listado de firmantes de la linea, el tipo
 * de operaci&oacute;n que deben llevar a cabo los "firmantes" (firma o visto bueno)
 * y si est&aacute; procesada o no.
 */
public class SignLine {

	/**
	 * Define los tipos de l&iacute;neas de firma existentes.
	 */
	enum SignLineType {
		/** L&iacute;nea de visto bueno. */
		VISTOBUENO,
		/** L&iacute;nea de firma. */
		FIRMA;
	}

	private final String[] signers;
	private SignLineType type = SignLineType.FIRMA;
	private boolean processed = false;

	public SignLine(String[] signers) {
		this.signers = signers;
	}

	public String[] getSigners() {
		return this.signers;
	}

	public boolean isProcessed() {
		return this.processed;
	}

	public void setProcessed(boolean processed) {
		this.processed = processed;
	}

	public SignLineType getType() {
		return this.type;
	}

	public void setType(SignLineType type) {
		this.type = type;
	}
}
