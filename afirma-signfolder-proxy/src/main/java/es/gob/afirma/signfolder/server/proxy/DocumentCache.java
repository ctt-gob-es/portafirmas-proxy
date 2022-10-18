package es.gob.afirma.signfolder.server.proxy;

import java.io.IOException;

/**
 * Cach&eacute; de documentos.
 * @author carlos.gamuci
 */
public interface DocumentCache {

	/**
	 * Guarda un documento en la cach&eacute;, as&iacute; como la operaci&oacute;n
	 * criptogr&aacute;fica que realizar sobre &eacute;l.
	 * @param requestRef Requerencia de la petici&oacute;n de firma del documento.
	 * @param docId Identificador del documento.
	 * @param cop Operaci&oacute;n criptogr&aacute;fica que realizar.
	 * @param content Contenido del documento.
	 * @throws IOException Cuando ocurre un error durante el guardado de los datos.
	 */
	void saveDocument(String requestRef, String docId, String cop, byte[] content) throws IOException;

	/**
	 * Carga un documento de la cach&eacute;.
	 * @param requestRef Requerencia de la petici&oacute;n de firma del documento.
	 * @param docId Identificador del documento.
	 * @param delete Indica si se deberia eliminar el documento de cach&eacute; despu&eacute;s
	 * de recuperarlo.
	 * @return Documento cargado de cach&eacute;.
	 * @throws IOException Cuando ocurre un error durante la carga de los datos.
	 */
	CachedDocument loadDocument(String requestRef, String docId, boolean delete) throws IOException;


	/**
	 * Elimina de cach&eacute; los documentos que hay sobrepasado un cierto tiempo.
	 * @param timeMillis Tiempo m&aacute;ximo en cach&eacute; de un documento para no ser eliminado.
	 */
	void cleanExpiredFiles(long timeMillis);

	/**
	 * Elimina de cach&eacute; un documento de una petici&oacute;n.
	 * @param requestRef Referencia de la petici&oacute;n de firma del documento.
	 * @param docId Identificador del documento.
	 * @throws IOException Cuando ocurre un error durante la eliminaci&oacute;n del documento.
	 */
	void removeDocument(String requestRef, String docId) throws IOException;
}
