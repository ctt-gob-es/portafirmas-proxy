package es.gob.afirma.signfolder.server.proxy;

import java.util.List;

/**
 * Clase que representa el resultado del servicio "getUserConfiguration".
 */
public class GetUserConfigResult {

	public static final int ERROR_TYPE_COMMUNICATION = 1;
	public static final int ERROR_TYPE_RESPONSE = 2;

	private List<GenericFilter> yearsFilters = null;
	private List<GenericFilter> periodsFilters = null;
	private List<GenericFilter> applicationsFilters = null;
	private List<GenericFilter> typesFilters = null;
	private List<Role> userRoles = null;
	private Boolean validatorAssigned = null;
	private Boolean simConfigured = null;
	private Boolean notificationActivated = null;

	private int errorType = 0;

	public GetUserConfigResult() {
		// Constructor vacio
	}

	public GetUserConfigResult(final int errorType) {
		this.errorType = errorType;
	}

	public boolean isError() {
		return this.errorType != 0;
	}

	public int getErrorType() {
		return this.errorType;
	}

	public List<GenericFilter> getYearsFilters() {
		return this.yearsFilters;
	}
	public void setYearsFilters(final List<GenericFilter> yearsFilters) {
		this.yearsFilters = yearsFilters;
	}
	public List<GenericFilter> getPeriodsFilters() {
		return this.periodsFilters;
	}
	public void setPeriodsFilters(final List<GenericFilter> periodsFilters) {
		this.periodsFilters = periodsFilters;
	}
	public List<GenericFilter> getApplicationsFilters() {
		return this.applicationsFilters;
	}
	public void setApplicationsFilters(final List<GenericFilter> applicationsFilters) {
		this.applicationsFilters = applicationsFilters;
	}
	public List<GenericFilter> getTypesFilters() {
		return this.typesFilters;
	}
	public void setTypesFilters(final List<GenericFilter> typesFilters) {
		this.typesFilters = typesFilters;
	}
	public List<Role> getUserRoles() {
		return this.userRoles;
	}
	public void setUserRoles(final List<Role> userRoles) {
		this.userRoles = userRoles;
	}
	public Boolean isValidatorAssigned() {
		return this.validatorAssigned;
	}
	public void setValidatorAssigned(final Boolean validatorAssigned) {
		this.validatorAssigned = validatorAssigned;
	}
	public Boolean isSimConfigured() {
		return this.simConfigured;
	}
	public void setSimConfigured(final Boolean simConfigured) {
		this.simConfigured = simConfigured;
	}
	public Boolean isNotificationActivated() {
		return this.notificationActivated;
	}
	public void setNotificationActivated(final Boolean notificationActivated) {
		this.notificationActivated = notificationActivated;
	}
}
