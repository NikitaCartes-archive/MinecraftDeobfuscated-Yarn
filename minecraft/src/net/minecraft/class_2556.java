package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public enum class_2556 {
	field_11737((byte)0, false),
	field_11735((byte)1, true),
	field_11733((byte)2, true);

	private final byte field_11736;
	private final boolean field_18797;

	private class_2556(byte b, boolean bl) {
		this.field_11736 = b;
		this.field_18797 = bl;
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

	@Environment(EnvType.CLIENT)
	public boolean method_19457() {
		return this.field_18797;
	}
}
