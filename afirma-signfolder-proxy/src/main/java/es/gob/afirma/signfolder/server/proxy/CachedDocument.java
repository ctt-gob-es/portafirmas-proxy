package es.gob.afirma.signfolder.server.proxy;

/**
 * Informaci&oacute;n del documento guardado en cach&eacute;.
 */
public class CachedDocument {

	private final String cryptoOperation;
	private final String digestAlgorithm;
	private final String params;
	private final byte[] content;

	/**
	 * Construye la informaci&oacute;n del documento guardado en cach&eacute;.
	 * @param cryptoOperation Operaci&oacute;n criptogr&aacute;fica a realizar sobre el documento.
	 * @param digestAlgorithm Algoritmo de huella con el que realizar la firma del documento.
	 * @param params Configuraci&oacute;n de firma.
	 * @param content Contenido del documento.
	 */
	public CachedDocument(final String cryptoOperation, final String digestAlgorithm,
			final String params, final byte[] content) {
		this.cryptoOperation = cryptoOperation;
		this.digestAlgorithm = digestAlgorithm;
		this.params = params;
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
	 * Recupera el algoritmo de huella digital que se debe usar para la firma del documento.
	 * @return Algoritmo de huella.
	 */
	public String getDigestAlgorithm() {
		return this.digestAlgorithm;
	}

	/**
	 * Recupera la configuraci&oacute;n del formato de firma que se debe usar para la firma
	 * del documento.
	 * @return Configuraci&oacute;n de firma.
	 */
	public String getParams() {
		return this.params;
	}

	/**
	 * Recupera el contenido del documento.
	 * @return Contenido del documento.
	 */
	public byte[] getContent() {
		return this.content;
	}
}
