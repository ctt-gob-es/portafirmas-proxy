package es.gob.afirma.signfolder.server.proxy;

import java.util.HashMap;
import java.util.Map;

import es.gob.afirma.signfolder.client.MobileException;
import es.gob.afirma.signfolder.client.MobileService;
import es.gob.afirma.signfolder.client.MobileTipoGenerico;
import es.gob.afirma.signfolder.client.MobileTiposAutorizacionList;

public class AuthorizationUtils {

	private static final String AUTH_TYPE_DELEGATE = "DELEGADO"; //$NON-NLS-1$
	private static final String AUTH_TYPE_SUSTITUTE = "SUSTITUTO"; //$NON-NLS-1$

	private static final String REMOTE_AUTH_TYPE_DELEGATE = "DELEGADO"; //$NON-NLS-1$
	private static final String REMOTE_AUTH_TYPE_SUSTITUTE = "SUSTITUTO"; //$NON-NLS-1$

	private static Map<String, String> authTypes = null;


	/**
	 * Traduce un c&oacute;digo de tipo de autorizaci&oacute;n utilizado por las aplicaciones
	 * m&oacute;viles al identificador con el que se refieren a ese tipo en el servicio
	 * portafirmas.
	 * @param service Servicio de consulta al Portafirmas.
	 * @param dni DNI del usuario, necesario para hacer la llamada al servicio Portafirmas.
	 * @param code C&oacute;digo del tipo de autorizaci&oacute;n.
	 * @return Identificador del tipo de autorizaci&oacute;n.
	 * @throws MobileException Cuando no se puede conectar con el Portafirmas para identificar
	 * los tipos de autorizaci&oacute;n.
	 * @throws IllegalArgumentException Cuando se proporciona un c&oacute;digo no reconocido.
	 */
	static String translateAuthorizationCodeToId(final MobileService service, final byte[] dni, final String code) throws MobileException {
		if (authTypes == null) {
			authTypes = initAuthorizationTypesMap(service, dni);
		}

		final String id = authTypes.get(code);
		if (id == null) {
			throw new IllegalArgumentException("Codigo de tipo de autenticacion no reconocido"); //$NON-NLS-1$
		}
		return id;
	}

	/**
	 * Inicializa la tabla que permite la conversion entre los c&oacute;digos de tipos de
	 * autorizaci&oacute;n conocidos y los identificadores dados de alta en la instancia
	 * del Portafirmas.
	 * @param service Servicio de consulta al Portafirmas.
	 * @param dni DNI del usuario que realiza la consulta.
	 * @return Tabla de correlacion entre los c&oacute;digos y los identificadores de tipos
	 * de autorizaci&oacute;n.
	 * @throws MobileException Cuando falla la llamada al servicio.
	 */
	private static Map<String, String> initAuthorizationTypesMap(final MobileService service, final byte[] dni)
			throws MobileException {
		final MobileTiposAutorizacionList list = service.tiposAutorizacion(dni);
		final Map<String, String> authTypesMap = new HashMap<>();
		for (final MobileTipoGenerico remoteType : list.getTiposAutorizacion()) {
			authTypesMap.put(translateCode(remoteType.getCodigo()), remoteType.getId());
		}

		return authTypesMap;
	}

	/**
	 * Traduce los c&oacute;digos de tipo de autorizaci&oacute;n usados en servidor por los
	 * c&oacute;digos usados por la aplicaci&oacute;n m&oacute;vil.
	 * @param codigo
	 * @return
	 */
	private static String translateCode(final String codigo) {
		if (REMOTE_AUTH_TYPE_DELEGATE.equals(codigo)) {
			return AUTH_TYPE_DELEGATE;
		} else if (REMOTE_AUTH_TYPE_SUSTITUTE.equals(codigo)) {
			return AUTH_TYPE_SUSTITUTE;
		}
		return codigo;
	}


}
