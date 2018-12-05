package net.minecraft.client.sortme;

public enum ChatMessageType {
	field_11737((byte)0),
	field_11735((byte)1),
	field_11733((byte)2);

	private final byte id;

	private ChatMessageType(byte b) {
		this.id = b;
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
}
