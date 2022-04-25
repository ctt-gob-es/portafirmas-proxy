package es.gob.afirma.signfolder.server.proxy;

/**
 * Usuario encontrado como resultado de b&uacute;squeda.
 */
public class GenericUser {

	private final String id;

	private final String dni;

	private final String name;

	/**
	 * Construye el objeto con la informaci&oacute;n del usuario.
	 * @param id Id del usuario encontrado.
	 * @param dni DNI del usuario encontrado.
	 * @param name Nombre del usuario encontrado.
	 */
	public GenericUser(final String id, final String dni, final String name) {
		this.id = id;
		this.dni = dni;
		this.name = name;
	}

	/**
	 * Devuelve el identificador del usuario encontrado.
	 * @return Identificador del usuario encontrado.
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Devuelve el DNI del usuario encontrado.
	 * @return DNI del usuario encontrado.
	 */
	public String getDni() {
		return this.dni;
	}

	/**
	 * Devuelve el nombre del usuario encontrado.
	 * @return Nombre del usuario encontrado.
	 */
	public String getName() {
		return this.name;
	}
}
