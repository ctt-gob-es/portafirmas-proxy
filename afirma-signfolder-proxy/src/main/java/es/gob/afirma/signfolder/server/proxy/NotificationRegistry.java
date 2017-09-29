package es.gob.afirma.signfolder.server.proxy;

class NotificationRegistry {

	private final String deviceId;
	private final String certificate;
	private final String platform;
	private final String idReg;

	NotificationRegistry(String certificate, String deviceId, String platform, String idReg) {
		this.deviceId = deviceId;
		this.certificate = certificate;
		this.platform = platform;
		this.idReg = idReg;
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
	
	public String getIdRegistry() {
		return this.idReg;
	}
}
