package net.minecraft.network.packet.c2s.handshake;

import net.minecraft.network.NetworkState;

public enum ConnectionIntent {
	STATUS,
	LOGIN;

	private static final int STATUS_ID = 1;
	private static final int LOGIN_ID = 2;

	public static ConnectionIntent byId(int id) {
		return switch (id) {
			case 1 -> STATUS;
			case 2 -> LOGIN;
			default -> throw new IllegalArgumentException("Unknown connection intent: " + id);
		};
	}

	public int getId() {
		return switch (this) {
			case STATUS -> 1;
			case LOGIN -> 2;
		};
	}

	public NetworkState getState() {
		return switch (this) {
			case STATUS -> NetworkState.STATUS;
			case LOGIN -> NetworkState.LOGIN;
		};
	}
}
