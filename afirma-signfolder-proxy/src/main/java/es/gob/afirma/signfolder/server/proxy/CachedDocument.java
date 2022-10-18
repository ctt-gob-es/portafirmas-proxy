package es.gob.afirma.signfolder.server.proxy;

/**
 * Informaci&oacute;n del documento guardado en cach&eacute;.
 */
public class CachedDocument {

	private final String cryptoOperation;
	private final byte[] content;

	/**
	 * Construye la informaci&oacute;n del documento guardado en cach&eacute;.
	 * @param cryptoOperation Operaci&oacute;n criptogr&aacute;fica a realizar sobre el documento.
	 * @param content Contenido del documento.
	 */
	public CachedDocument(final String cryptoOperation, final byte[] content) {
		this.cryptoOperation = cryptoOperation;
		this.content = content;
	}

	/**
	 * Recupera la operaci&oacute;n criptogr&aacute;fica a realizar sobre el documento.
	 * @return Operaci&oacute;n criptogr&aacute;fica (sign, cosign, countersign).
	 */
	public String getCryptoOperation() {
		return this.cryptoOperation;
	}

	/**
	 * Recupera el contenido del documento.
	 * @return Contenido del documento.
	 */
	public byte[] getContent() {
		return this.content;
	}
}
