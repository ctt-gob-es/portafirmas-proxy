package es.gob.afirma.signfolder.server.proxy;

class NotificationRegistry {

	private final String deviceId;
	private final String certificate;
	private final String platform;

	NotificationRegistry(String certificate, String deviceId, String platform) {
		this.deviceId = deviceId;
		this.certificate = certificate;
		this.platform = platform;
	}

	public String getDeviceId() {
		return this.deviceId;
	}

	public String getCertificate() {
		return this.certificate;
	}

	public String getPlatform() {
		return this.platform;
	}
}
