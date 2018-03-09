package es.gob.afirma.signfolder.server.proxy;

import java.util.Properties;

import es.gob.fire.client.FireClient;

public class FireConnector {

	private static final String FIRE_APP_ID = "70B717931013"; //$NON-NLS-1$

	private final FireClient fireClient;

	public FireConnector() {

		final Properties fireConfig = new Properties();
		fireConfig.setProperty("fireUrl", "https://des-clavefirma.desarrollo.redsara.es/fire-signature/fireService"); //$NON-NLS-1$ //$NON-NLS-2$
		fireConfig.setProperty("javax.net.ssl.keyStore", "/opt/usuarios/portafirmasage/certificados/fire_client_ssl.jks"); //$NON-NLS-1$ //$NON-NLS-2$
		fireConfig.setProperty("javax.net.ssl.keyStorePassword", "12341234"); //$NON-NLS-1$ //$NON-NLS-2$
		fireConfig.setProperty("javax.net.ssl.keyStoreType", "JKS"); //$NON-NLS-1$ //$NON-NLS-2$
		fireConfig.setProperty("javax.net.ssl.trustStore", "all"); //$NON-NLS-1$ //$NON-NLS-2$

		this.fireClient = new FireClient(FIRE_APP_ID, fireConfig);
	}

	public FireClient getFireClient() {
		return this.fireClient;
	}

	public Properties getBatchConfig() {
		final Properties batchConfig = new Properties();
		batchConfig.setProperty("certOrigin", "claveFirma"); //$NON-NLS-1$ //$NON-NLS-2$
		batchConfig.setProperty("appName", "Portafirmas"); //$NON-NLS-1$ //$NON-NLS-2$
		batchConfig.setProperty("redirectOkUrl", "ok"); //$NON-NLS-1$ //$NON-NLS-2$
		batchConfig.setProperty("redirectErrorUrl", "error"); //$NON-NLS-1$ //$NON-NLS-2$

		return batchConfig;
	}
}
