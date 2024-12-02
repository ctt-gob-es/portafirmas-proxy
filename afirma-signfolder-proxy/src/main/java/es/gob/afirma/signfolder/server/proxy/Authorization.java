package es.gob.afirma.signfolder.server.proxy;

import java.util.Date;

/**
 * Infomaci&oacute;n sobre una autorizaci&oacute;n relacionada con el usuario.
 */
public class Authorization {

	public static final String REMOTE_TYPE_DELEGATE = "DELEGADO"; //$NON-NLS-1$
	public static final String FORMATED_TYPE_DELEGATE = "DELEGADO"; //$NON-NLS-1$
	public static final String REMOTE_TYPE_SUSTITUTE = "SUSTITUTO"; //$NON-NLS-1$
	public static final String FORMATED_TYPE_SUSTITUTE = "SUSTITUTO"; //$NON-NLS-1$
	public static final String FORMATED_TYPE_UNKNOWN = "DESCONOCIDO"; //$NON-NLS-1$


	private static final String REMOTE_STATE_PENDING = "unresolvedAuthorization"; //$NON-NLS-1$
	private static final String FORMATED_STATE_PENDING = "pending"; //$NON-NLS-1$
	private static final String REMOTE_STATE_ACCEPTED = "accepted"; //$NON-NLS-1$
	private static final String FORMATED_STATE_ACCEPTED = "accepted"; //$NON-NLS-1$
	private static final String REMOTE_STATE_REVOKED = "revoked"; //$NON-NLS-1$
	private static final String FORMATED_STATE_REVOKED = "revoked"; //$NON-NLS-1$
	private static final String REMOTE_STATE_REJECTED = "rejected"; //$NON-NLS-1$
	private static final String FORMATED_STATE_REJECTED = "rejected"; //$NON-NLS-1$
	private static final String REMOTE_STATE_CANCELLED = "cancelled"; //$NON-NLS-1$
	private static final String FORMATED_STATE_CANCELLED = "cancelled"; //$NON-NLS-1$
	private static final String REMOTE_STATE_EXPIRED = "expired"; //$NON-NLS-1$
	private static final String FORMATED_STATE_EXPIRED = "expired"; //$NON-NLS-1$
	private static final String REMOTE_STATE_ERROR = "withError"; //$NON-NLS-1$
	private static final String FORMATED_STATE_ERROR = "error"; //$NON-NLS-1$

	/** Identificador de la autorizaci&oacute;n. */
	private String id;

	/** Tipo de autorizaci&oacute;n. */
	private String type;

	/** Usuario que emiti&oacute; la autorizaci&oacute;n. */
	private GenericUser user;

	/** Usuario autorizado. */
	private GenericUser authorizedUser;

	/** Estado de la autorizaci&oacute;n. */
	private String state;

	/** Fecha de inicio de la autorizaci&oacute;n. */
	private Date startDate;

	/** Fecha de fin de la autorizaci&oacute;n. */
	private Date revocationDate = null;

	/** Observaciones. */
	private String observations = null;

	/** Se&ntilde;ala si esta autorizaci&oacute;n se envio ({@code true}) o si se recibi&oacute; ({@code false}). */
	private boolean sended;

	/**
	 * Recupera el identificador de la autorizaci&oacute;n.
	 * @return Identificador de la autorizaci&oacute;n.
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Establece el identificador de la autorizaci&oacute;n.
	 * @param id Identificador de la autorizaci&oacute;n.
	 */
	public void setId(final String id) {
		this.id = id;
	}

	/**
	 * Recupera el tipo de la autorizaci&oacute;n.
	 * @return Tipo de autorizaci&oacute;n.
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * Establece el identificador de la autorizaci&oacute;n.
	 * @param type Tipo de autorizaci&oacute;n.
	 */
	public void setType(final String type) {
		this.type = type;
	}

	/**
	 * Recupera el usuario que emiti&oacute; la autorizaci&oacute;n.
	 * @return Usuario que emiti&oacute; la autorizaci&oacute;n.
	 */
	public GenericUser getUser() {
		return this.user;
	}

	/**
	 * Establece el usuario que emiti&oacute; la autorizaci&oacute;n.
	 * @param user Usuario que emiti&oacute; la autorizaci&oacute;n.
	 */
	public void setUser(final GenericUser user) {
		this.user = user;
	}

	/**
	 * Recupera el usuario al que se ha autorizado.
	 * @return Usuario al que se ha autorizado.
	 */
	public GenericUser getAuthorizedUser() {
		return this.authorizedUser;
	}

	/**
	 * Establece el usuario al que se ha autorizado.
	 * @param authorizedUser Usuario al que se ha autorizado.
	 */
	public void setAuthorizedUser(final GenericUser authorizedUser) {
		this.authorizedUser = authorizedUser;
	}

	/**
	 * Recupera el estado de la autorizaci&oacute;n.
	 * @return Estado de la autorizaci&oacute;n.
	 */
	public String getState() {
		return this.state;
	}

	/**
	 * Establece el estado de la autorizaci&oacute;n.
	 * @param state Estado de la autorizaci&oacute;n.
	 */
	public void setState(final String state) {
		this.state = state;
	}

	/**
	 * Recupera la fecha de inicio de la autorizaci&oacute;n.
	 * @return Fecha de inicio de la autorizaci&oacute;n.
	 */
	public Date getStartDate() {
		return this.startDate;
	}

	/**
	 * Establece la fecha de inicio de la autorizaci&oacute;n.
	 * @param startDate Fecha de inicio de la autorizaci&oacute;n.
	 */
	public void setStartDate(final Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * Recupera la fecha de fin de la autorizaci&oacute;n.
	 * @return Fecha de fin de la autorizaci&oacute;n.
	 */
	public Date getRevocationDate() {
		return this.revocationDate;
	}

	/**
	 * Establece la fecha de fin de la autorizaci&oacute;n.
	 * @param revocationDate Fecha de fin de la autorizaci&oacute;n.
	 */
	public void setRevocationDate(final Date revocationDate) {
		this.revocationDate = revocationDate;
	}

	/**
	 * Recupera las observaciones asociadas a la la autorizaci&oacute;n.
	 * @return Observaciones de la autorizaci&oacute;n o {@code null} si no las tiene.
	 */
	public String getObservations() {
		return this.observations;
	}

	/**
	 * Establece las observaciones asociadas a la la autorizaci&oacute;n.
	 * @param observations Observaciones de la autorizaci&oacute;n.
	 */
	public void setObservations(final String observations) {
		this.observations = observations;
	}

	/**
	 * Indica si la autorizaci&oacute;n se env&oacute; o si se recibi&oacute;.
	 * @return ({@code true}) si la autorizaci&oacute;n se envi&oacute;, ({@code false}) si se recibi&oacute;.
	 */
	public boolean isSended() {
		return this.sended;
	}

	/**
	 * Establece si la autorizaci&oacute;n se env&oacute; o si se recibi&oacute;.
	 * @param sended ({@code true}) si la autorizaci&oacute;n se envi&oacute;, ({@code false}) si se recibi&oacute;.
	 */
	public void setSended(final boolean sended) {
		this.sended = sended;
	}

	/**
	 * Devuelve el identificador de un tipo de autorizaci&oacute;n.
	 * @param code C&oacute;digo asociado al tipo de autorizaci&oacute;n.
	 * @return Identificador del tipo de autorizaci&oacute;n.
	 */
	public static String getFormatedType(final String code) {
		switch (code) {
		case REMOTE_TYPE_DELEGATE:
			return FORMATED_TYPE_DELEGATE;
		case REMOTE_TYPE_SUSTITUTE:
			return FORMATED_TYPE_SUSTITUTE;
		default:
			return FORMATED_TYPE_UNKNOWN;
		}
	}

	/**
	 * Traduce los identificadores de estado proporcionados por el portafirmas
	 * por los usuados en la aplicaci&oacute;n m&oacute;vil.
	 * @param state Identificador de estado indicado por Portafirmas.
	 * @return Identificador de estado de la aplicaci&oacute;n m&oacute;vil.
	 */
	public static String getFormatedState(final String state) {
		switch (state) {
		case REMOTE_STATE_PENDING:
			return FORMATED_STATE_PENDING;
		case REMOTE_STATE_ACCEPTED:
			return FORMATED_STATE_ACCEPTED;
		case REMOTE_STATE_REVOKED:
			return FORMATED_STATE_REVOKED;
		case REMOTE_STATE_REJECTED:
			return FORMATED_STATE_REJECTED;
		case REMOTE_STATE_CANCELLED:
			return FORMATED_STATE_CANCELLED;
		case REMOTE_STATE_EXPIRED:
			return FORMATED_STATE_EXPIRED;
		case REMOTE_STATE_ERROR:
			return FORMATED_STATE_ERROR;
		default:
			return FORMATED_STATE_REVOKED;
		}
	}
}
