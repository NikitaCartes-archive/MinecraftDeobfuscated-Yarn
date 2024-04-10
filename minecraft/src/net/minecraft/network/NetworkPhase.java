package net.minecraft.network;

public enum NetworkPhase {
	HANDSHAKING("handshake"),
	PLAY("play"),
	STATUS("status"),
	LOGIN("login"),
	CONFIGURATION("configuration");

	private final String id;

	private NetworkPhase(final String id) {
		this.id = id;
	}

	public String getId() {
		return this.id;
	}
}
