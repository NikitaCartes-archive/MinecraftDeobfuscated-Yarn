package net.minecraft.network.packet.c2s.handshake;

public enum ConnectionIntent {
	STATUS,
	LOGIN,
	TRANSFER;

	private static final int STATUS_ID = 1;
	private static final int LOGIN_ID = 2;
	private static final int TRANSFER_ID = 3;

	public static ConnectionIntent byId(int id) {
		return switch (id) {
			case 1 -> STATUS;
			case 2 -> LOGIN;
			case 3 -> TRANSFER;
			default -> throw new IllegalArgumentException("Unknown connection intent: " + id);
		};
	}

	public int getId() {
		return switch (this) {
			case STATUS -> 1;
			case LOGIN -> 2;
			case TRANSFER -> 3;
		};
	}
}
