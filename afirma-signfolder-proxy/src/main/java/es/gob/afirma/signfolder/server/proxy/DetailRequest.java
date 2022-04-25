package es.gob.afirma.signfolder.server.proxy;

/**
 * Petici&oacute;n del detalle de una solicitud de firma.
 */
public class DetailRequest {

	private final String id;
	private final String ownerId;

	/**
	 * Construye una petici&oacute;n de una solicitud de firma.
	 * @param id Identificador de la petici&oacute;n.
	 * @param ownerId DNI del propietario de la petici&oacute;n.
	 */
	public DetailRequest(final String id, final String ownerId) {
		this.id = id;
		this.ownerId = ownerId;
	}

	/**
	 * Recupera el identificador de la petici&oacute;n solicitada.
	 * @return Identificador.
	 */
	public String getRequestId() {
		return this.id;
	}

	/**
	 * @return the ownerId
	 */
	public String getOwnerId() {
		return this.ownerId;
	}

}
