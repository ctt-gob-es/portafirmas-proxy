/* Copyright (C) 2017 [Gobierno de Espana]
 * This file is part of FIRe.
 * FIRe is free software; you can redistribute it and/or modify it under the terms of:
 *   - the GNU General Public License as published by the Free Software Foundation;
 *     either version 2 of the License, or (at your option) any later version.
 *   - or The European Software License; either version 1.1 or (at your option) any later version.
 * Date: 08/09/2017
 * You may contact the copyright holder at: soporte.afirma@correo.gob.es
 */
package es.gob.afirma.signfolder.server.proxy.sessions;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import es.gob.afirma.signfolder.server.proxy.ConfigManager;
import es.gob.afirma.signfolder.server.proxy.SessionParams;

/**
 * Gestiona las transacciones de firma de la aplicaciones almacenando los datos de cada
 * una en sesiones. Las sesiones pueden guardarse tanto en memoria como en un espacio
 * compartido por varias instancias de la aplicaci&oacute;n a trav&eacute;s de un DAO.
 * Esta clase gestiona autom&aacute;ticamente el borrado de sesiones caducadas en memoria y
 * en el espacio compartido. Los datos temporales de las sesiones caducadas solo se
 * eliminar&aacute;n a partir de las sesiones almacenadas en memoria, ya que, de hacerlo
 * para las sesiones del espacio compartido, se solicitar&iacute;a su borrado desde cada uno
 * de los nodos del sistema.
 */
public final class SessionCollector {

	private static final Logger LOGGER = Logger.getLogger(SessionCollector.class.getName());

	/** Peri&oacute;do m&aacute;ximo de inactividad. */
	private static final int MAX_INACTIVE_INTERVAL = 8 * 60 * 60; // 8 Horas

	private static final SessionDAO dao;

	private static boolean shareSession;

    static {

    	try {
    		ConfigManager.checkInitialized();
    	}
    	catch (final Exception e) {
    		LOGGER.log(Level.SEVERE, "No se pudo cargar la configuracion del servicio proxy", e); //$NON-NLS-1$
    		throw e;
		}

    	try {
    		dao = SessionDAO.getInstance();
    	}
    	catch (final Exception e) {
    		LOGGER.log(Level.SEVERE, "No se puedo inicializar el gestion de sesiones", e); //$NON-NLS-1$
    		throw e;
		}

    	try {
    		shareSession = ConfigManager.isShareSessionEnabled();
    	}
    	catch (final Exception e) {
    		LOGGER.log(Level.WARNING, "No se pudo comprobar si se desea compartir la sesion entre nodos", e); //$NON-NLS-1$
    		shareSession = false;
    	}
    }

    /**
	 * Crea una nueva sesi&oacute;n (si no existia ya).
	 * @param request Petici&oacute;n enviada por el cliente para el que se desea
	 * crear la sesi&oacute;n.
	 * @return Sesi&oacute;n creada.
	 */
	public static HttpSession createSession(final HttpServletRequest request) {
    	final HttpSession session = request.getSession();
    	session.setMaxInactiveInterval(MAX_INACTIVE_INTERVAL);
    	return session;
	}

    /**
	 * Crea una nueva sesi&oacute;n compartida.
	 * @param session Sesi&oacute;n que se desea compartir.
	 * @return Identificador de la sesi&oacute;n compartida.
	 * @throws IOException Cuando no se puede compartir la sesi&oacute;n.
	 */
	public static String createSharedSession(final HttpSession session) throws IOException {

		// Creamos la sesion compartida y guardamos en la misma los datos que haya actualmente y
		// el identificador que se le asigna
		final String ssid = dao.createSharedSession();
		session.setAttribute(SessionParams.SHARED_SESSION_ID, ssid);
		dao.writeSession(session, ssid);

		return ssid;
	}

	/**
	 * Si se creo una sesion compartida, se actualiza los datos de sesi&oacute;n.
	 * @param session Datos de la sesi&oacute;n.
	 */
	public static void updateSession(final HttpSession session) {
		if (shareSession) {
			final String ssid = (String) session.getAttribute(SessionParams.SHARED_SESSION_ID);
			if (ssid != null) {
				try {
					dao.writeSession(session, ssid);
				} catch (final IOException e) {
					LOGGER.warning("Error al actualizar la sesion compartida " + ssid); //$NON-NLS-1$
				}
			}
		}
	}

	/**
	 * Recupera la sesion del usuario de la memoria o del espacio compartido de sesiones, priorizando
	 * el uso de las sesiones en disco. Si la sesion no existe en alguno de ambos entornos, se
	 * devuelve {@code null}.
	 * @param request Petici&oacute;n realizada.
	 * @param ssid Identificador de sesi&oacute;n compartida si aplica.
	 * @return Sesi&oacute;n de la operaci&oacute;n o {@code null} si no se pudo cargar de memoria
	 * y, estando habilitadas las sesiones compartida, si no se encontr&oacute; compartida.
	 */
	public static HttpSession getSession(final HttpServletRequest request, final String ssid) {

		HttpSession session = request.getSession(false);

		if (ssid != null && shareSession && !isValidatedSession(session)) {

			SessionInfo sessionInfo;
			try {
				sessionInfo = dao.recoverSessionInfo(ssid);
			}
			catch (final Exception e) {
				// Si no se puede cargar, usamos la de memoria exista o no
				return session;
			}

			// Si se carga de disco, se crea una nueva sesion en memoria o se carga la existente se
			// actualiza con los datos a disco
			if (session == null) {
				session = request.getSession();
				final long interval = sessionInfo.getExpirationDate() - System.currentTimeMillis();
				if (interval <= 0) {
					LOGGER.warning("La sesion de la peticion " + ssid + " ha caducado"); //$NON-NLS-1$ //$NON-NLS-2$
					return null;
				}
				session.setMaxInactiveInterval(MAX_INACTIVE_INTERVAL);
			}

			sessionInfo.save(session);
		}
		return session;
	}

	/**
	 * Indica si una sesi&oacute;n con certificado local se ha validado.
	 * @param session Sesi&oacute;n que se desea comprobar.
	 * @return {@code true} si la sesi&oacute;n est&aacute; validada, {@code false} si la
	 * sesi&oacute;n es nula, si se inicio con certificado en la nube o si a&uacute;n no se ha
	 * validado.
	 */
	private static boolean isValidatedSession(final HttpSession session) {
		return session != null &&
				Boolean.parseBoolean((String) session.getAttribute(SessionParams.VALID_SESSION));
	}

    /**
     * Busca una sesion en el pool de sesiones para eliminarla junto con sus datos temporales.
     * Si se establecio tambien un DAO de sesiones compartidas, se elimina tambi&eacute;n del mismo.
     * @param id Identificador de la sesi&oacute;n.
     */
    public static void removeSession(final HttpSession session) {
    	if (session == null) {
    		return;
    	}

    	// Obtenemos el identificador de sesion compartida (si lo hay)
    	final String ssid = (String) session.getAttribute(SessionParams.SHARED_SESSION_ID);

    	// Invalidamos la sesion
    	session.invalidate();

    	// Eliminamos la sesion compartida
    	if (ssid != null) {
    		removeSharedSession(ssid);
    	}
    }

    static void removeSharedSession(final String ssid) {
    	LOGGER.info("Ejecutamos el borrado del directorio compartido"); //$NON-NLS-1$
    	if (ssid != null) {
    		dao.removeSession(ssid);
    	}
    }
}
