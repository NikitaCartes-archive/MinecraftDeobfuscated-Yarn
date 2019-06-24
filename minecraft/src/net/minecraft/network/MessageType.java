package net.minecraft.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public enum MessageType {
	CHAT((byte)0, false),
	SYSTEM((byte)1, true),
	GAME_INFO((byte)2, true);

	private final byte id;
	private final boolean interruptsNarration;

	private MessageType(byte b, boolean bl) {
		this.id = b;
		this.interruptsNarration = bl;
	}

	public byte getId() {
		return this.id;
	}

	public static MessageType byId(byte b) {
		for (MessageType messageType : values()) {
			if (b == messageType.id) {
				return messageType;
			}
		}

		return CHAT;
	}

	@Environment(EnvType.CLIENT)
	public boolean interruptsNarration() {
		return this.interruptsNarration;
	}
}
