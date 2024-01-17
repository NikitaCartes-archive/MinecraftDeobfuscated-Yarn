package net.minecraft.network;

public enum NetworkState {
	HANDSHAKING("handshake"),
	PLAY("play"),
	STATUS("status"),
	LOGIN("login"),
	CONFIGURATION("configuration");

	private final String stateId;

	private NetworkState(String stateId) {
		this.stateId = stateId;
	}

	public String getId() {
		return this.stateId;
	}
}
