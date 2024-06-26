package es.gob.afirma.signfolder.server.proxy.sessions;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.http.HttpSession;

import es.gob.afirma.signfolder.server.proxy.ConfigManager;

/**
 * Clase que almacena informaci&oacute;n de una transacci&oacute;n y puede volcarla
 * en una sesi&oacute;n HTTP.
 */
public class SessionInfo extends HashMap<String, Object> {

	/** Serial number. */
	private static final long serialVersionUID = 4145239095069112792L;

	private long expirationDate;

	public SessionInfo() {
		// La fecha de expiracion por defecto es la actual mas el tiempo maximo de configurado.
		// El tiempo se configura en minutos, por lo que se pasa a milisegundos.
		this.expirationDate = System.currentTimeMillis() +
				ConfigManager.getSessionTimeoutInactivity() * 60000;
	}

	/**
	 * Carga la informaci&oacute;n de la transacci&oacute;n desde la sesi&oacute;n
	 * indicada.
	 * @param session Objeto de sesi&oacute;n desde el que obtener la informaci&oacute;n.
	 */
	public void load(final HttpSession session) {
		final Enumeration<String> attrs = session.getAttributeNames();
		while (attrs.hasMoreElements()) {
			final String attr = attrs.nextElement();
			put(attr, session.getAttribute(attr));
		}
	}

	/**
	 * Vuelca la informaci&oacute;n de la transacci&oacute;n en la sesi&oacute;n
	 * indicada.
	 * @param session Objeto de sesi&oacute;n al que volcar la informaci&oacute;n.
	 */
	public void save(final HttpSession session) {

		// Eliminamos todas las propiedades de la sesion
		final Enumeration<String> attrs = session.getAttributeNames();
		while (attrs.hasMoreElements()) {
			session.removeAttribute(attrs.nextElement());
		}

		// Cargamos las nuevas
		final Iterator<String> it = keySet().iterator();
		while(it.hasNext()) {
			final String key = it.next();
			session.setAttribute(key, get(key));
		}
	}

	/**
	 * Recupera la fecha de caducidad de la sesi&oacute;n si esta establecida.
	 * @return Fecha de caducidad.
	 */
	public long getExpirationDate() {
		return this.expirationDate;
	}

	/**
	 * Establece la fecha de caducidad de la sesi&oacute;n.
	 * @param expirationDate Fecha de caducidad.
	 */
	public void setExpirationDate(final long expirationDate) {
		this.expirationDate = expirationDate;
	}
}
