package es.gob.afirma.signfolder.server.proxy;

/**
 * Infomaci&oacute;n sobre un validador del usuario.
 */
public class Validator {

	/** Usuario validador. */
	private GenericUser user;

	/** Se&ntilde;ala si es un validador s&oacute;lo para algunas aplicaciones ({@code true}) o un validador global ({@code false}). */
	private boolean validatorForApps = false;

	/**
	 * Recupera los datos del usuario validador.
	 * @return Usuario validador.
	 */
	public GenericUser getUser() {
		return this.user;
	}

	/**
	 * Establece los datos del usuario validador.
	 * @param user Usuario validador.
	 */
	public void setUser(final GenericUser user) {
		this.user = user;
	}

	/**
	 * Indica si es validador s&oacute;lo para algunas aplicaciones o general.
	 * @return ({@code true}) si es validador s&oacute;lo para algunas aplicaciones, ({@code false}) si es general.
	 */
	public boolean isValidatorForApps() {
		return this.validatorForApps;
	}

	/**
	 * Establece si la autorizaci&oacute;n se env&oacute; o si se recibi&oacute;.
	 * @param validatorForApps ({@code true}) si la autorizaci&oacute;n se envi&oacute;, ({@code false}) si se recibi&oacute;.
	 */
	public void setValidatorForApps(final boolean validatorForApps) {
		this.validatorForApps = validatorForApps;
	}
}
