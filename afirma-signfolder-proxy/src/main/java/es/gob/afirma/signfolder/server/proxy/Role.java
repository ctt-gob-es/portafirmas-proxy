/**
 *
 */
package es.gob.afirma.signfolder.server.proxy;

/**
 * Clase que representa el rol de usuario del portafirmas.
 */
public class Role {

	private final String id;

	private final String name;

	private String userId = null;

	private String userName = null;

	public Role(final String idRole, final String nameRole) {
		this.id = idRole;
		this.name = nameRole;
	}

	public String getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}
	public String getUserId() {
		return this.userId;
	}

	public void setUserId(final String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(final String userName) {
		this.userName = userName;
	}
}
