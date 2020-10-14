package es.gob.afirma.signfolder.server.proxy;

import es.gob.afirma.signfolder.client.MobileConfiguracionUsuario;

/**
 * Clase que representa el resultado del servicio "getUserConfiguration".
 */
public class GetUserConfigResult {

	static final int ERROR_TYPE_COMMUNICATION = 1;

	static final int ERROR_TYPE_REQUEST = 2;

	static final int ERROR_TYPE_DOCUMENT = 3;
	
	/** 
	 * Configuración de usuario. 
	 */
	private MobileConfiguracionUsuario configuration;

	/** 
	 * Indica si se ha producido un error durante la operación. 
	 */
	private boolean error = false;

	/** 
	 * Tipo de error.
	 */
	private int errorType = 0;
	
	/**
	 * Constructor for the success cases.
	 * @param response Configuration user.
	 */
	public GetUserConfigResult(final MobileConfiguracionUsuario response) {
		this.configuration = response;
	}

	/**
	 * Constructor for the fails cases.
	 * @param errorType Error type.
	 */
	public GetUserConfigResult(final int errorType) {
		setErrorType(errorType);
	}

    /**
     * Get method for the <i>configuration</i> attribute.
     * @return the value of the attribute.
     */
	public MobileConfiguracionUsuario getConfiguration() {
		return configuration;
	}

    /**
     * Set method for the <i>configuration</i> attribute.
     * @param config new value of the attribute.
     */
	public void setonfiguration(MobileConfiguracionUsuario config) {
		this.configuration = config;
	}

    /**
     * Get method for the <i>error</i> attribute.
     * @return the value of the attribute.
     */
	public boolean isError() {
		return error;
	}

    /**
     * Set method for the <i>error</i> attribute.
     * @param error new value of the attribute.
     */
	public void setError(boolean error) {
		this.error = error;
	}

    /**
     * Get method for the <i>errorType</i> attribute.
     * @return the value of the attribute.
     */
	public int getErrorType() {
		return errorType;
	}

    /**
     * Set method for the <i>errorType</i> attribute.
     * @param errorType new value of the attribute.
     */
	public void setErrorType(int errorType) {
		this.errorType = errorType;
	}
	
}
