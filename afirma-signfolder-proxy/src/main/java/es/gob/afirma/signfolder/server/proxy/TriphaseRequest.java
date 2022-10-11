package es.gob.afirma.signfolder.server.proxy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Petici&oacute;n de fase de firma de documentos.
 * @author Carlos Gamuci Mill&aacute;n */
public class TriphaseRequest extends ArrayList<TriphaseSignDocumentRequest> {

	/** Serial id. */
	private static final long serialVersionUID = 1L;

	/** Referencia de la petici&oacute;n. */
	private final String ref;

	/** Resultado de la petici&oacute;n de la petici&oacute;n. */
	private boolean statusOk = true;

	/** Excepcion detectada durante la peticion, en caso de existir. */
	private Throwable throwable = null;

	/** Indica si es necesaria confirmaci&oacute;n del usuario. */
	private boolean needConfirmation = false;

	/** Conjunto con los indicadores de las confirmaciones requeridas del usuario. */
	private Set<String> requirement = null;

	/**
	 * Construye un objeto de petici&oacute;n de prefirma o postfirma de documentos.
	 * @param reference Referencia &uacute;nica de la petici&oacute;n.
	 * @param documementsRequest Listado de documentos para los que se solicita la operaci&oacute;n.
	 */
	TriphaseRequest(final String reference, final List<TriphaseSignDocumentRequest> documementsRequest) {
		this.ref = reference;
		if (documementsRequest != null) {
			this.addAll(documementsRequest);
		}
	}

	/** Construye un objeto de petici&oacute;n de firma de documentos.
	 * @param reference Referencia &uacute;nica de la petici&oacute;n.
	 * @param statusOk Estado de la petici&oacute;n.
	 * @param documementsRequest Listado de documentos para los que se solicita la firma.
	 */
	TriphaseRequest(final String reference, final boolean statusOk, final List<TriphaseSignDocumentRequest> documementsRequest) {
		this.ref = reference;
		this.statusOk = statusOk;
		if (documementsRequest != null) {
			this.addAll(documementsRequest);
		}
	}

	/** Recupera la referencia de la petici&oacute;n firma de documentos.
	 * @return Referencia de la petici&oacute;n. */
	public String getRef() {
		return this.ref;
	}

	/** Indica si el estado de la petici&oacute;n es OK.
	 * @return Indicador del estado de la petici&oacute;n. */
	public boolean isStatusOk() {
		return this.statusOk;
	}

	/** Estable si el estado de la petici&oacute;n es OK.
	 * @param statusOk Es {@code true} en casode que la petici&oacute;n de firma progrese
	 * correctamente, {@code false} en caso contrario. */
	public void setStatusOk(final boolean statusOk) {
		this.statusOk = statusOk;
	}

	/**
	 * Recupera el error detectado durante la operaci&oacute;n en caso de haberse producido.
	 * @return Excepci&oacute;n/error que hizo fallar la operaci&oacute;n.
	 */
	public Throwable getThrowable() {
		return this.throwable;
	}

	/**
	 * Establece el error que se haya producido durante la operaci&oacute;n.
	 * @param t Excepci&oacute;n/error que hizo fallar la operaci&oacute;n.
	 */
	public void setThrowable(final Throwable t) {
		this.throwable = t;
	}

	/**
	 * Indica si es necesario que el usuario confirme algo para la firma de la
	 * petici&oacute;n.
	 * @return {@code true} si es necesaria alguna confirmaci&oacute;n del usuario,
	 * {@code false} en caso contrario.
	 */
	public boolean isNeedConfirmation() {
		return this.needConfirmation;
	}

	/**
	 * Obtiene los requisitos de confirmaci&oacute;n que debe atender el usuario.
	 * @return Requisitos de confirmaci&oacute;n o {@code null} si no hay ninguno.
	 */
	public Set<String> getRequirement() {
		return this.requirement;
	}

	/**
	 * Agrega una solicitud de confirmaci&oacute;n que deber&aacute; atender el usuario.
	 * @param requestorText Texto identificativo de la solicitud de confirmaci&oacute;n.
	 */
	public void addConfirmationRequirement(final String requestorText) {
		if (this.requirement == null) {
			this.requirement = new HashSet<>();
			this.needConfirmation = true;
		}
		this.requirement.add(requestorText);
	}
}
