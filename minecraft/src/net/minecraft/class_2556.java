package net.minecraft;

public enum class_2556 {
	field_11737((byte)0),
	field_11735((byte)1),
	field_11733((byte)2);

	private final byte field_11736;

	private class_2556(byte b) {
		this.field_11736 = b;
	}

	public byte method_10843() {
		return this.field_11736;
	}

	public static class_2556 method_10842(byte b) {
		for (class_2556 lv : values()) {
			if (b == lv.field_11736) {
				return lv;
			}
		}

		return field_11737;
	}
}
