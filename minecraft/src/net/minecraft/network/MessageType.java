package net.minecraft.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public enum MessageType {
	field_11737((byte)0, false),
	field_11735((byte)1, true),
	field_11733((byte)2, true);

	private final byte id;
	private final boolean interruptsNarration;

	private MessageType(byte id, boolean interruptsNarration) {
		this.id = id;
		this.interruptsNarration = interruptsNarration;
	}

	public byte getId() {
		return this.id;
	}

	public static MessageType byId(byte id) {
		for (MessageType messageType : values()) {
			if (id == messageType.id) {
				return messageType;
			}
		}

		return field_11737;
	}

	@Environment(EnvType.CLIENT)
	public boolean interruptsNarration() {
		return this.interruptsNarration;
	}
}
