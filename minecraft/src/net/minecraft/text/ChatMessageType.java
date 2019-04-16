package net.minecraft.text;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public enum ChatMessageType {
	field_11737((byte)0, false),
	field_11735((byte)1, true),
	field_11733((byte)2, true);

	private final byte id;
	private final boolean interruptsNarration;

	private ChatMessageType(byte b, boolean bl) {
		this.id = b;
		this.interruptsNarration = bl;
	}

	public byte getId() {
		return this.id;
	}

	public static ChatMessageType byId(byte b) {
		for (ChatMessageType chatMessageType : values()) {
			if (b == chatMessageType.id) {
				return chatMessageType;
			}
		}

		return field_11737;
	}

	@Environment(EnvType.CLIENT)
	public boolean interruptsNarration() {
		return this.interruptsNarration;
	}
}
