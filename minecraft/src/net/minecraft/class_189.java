package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public enum class_189 {
	field_1254("task", 0, class_124.field_1060),
	field_1250("challenge", 26, class_124.field_1064),
	field_1249("goal", 52, class_124.field_1060);

	private final String field_1251;
	private final int field_1252;
	private final class_124 field_1255;

	private class_189(String string2, int j, class_124 arg) {
		this.field_1251 = string2;
		this.field_1252 = j;
		this.field_1255 = arg;
	}

	public String method_831() {
		return this.field_1251;
	}

	@Environment(EnvType.CLIENT)
	public int method_832() {
		return this.field_1252;
	}

	public static class_189 method_833(String string) {
		for (class_189 lv : values()) {
			if (lv.field_1251.equals(string)) {
				return lv;
			}
		}

		throw new IllegalArgumentException("Unknown frame type '" + string + "'");
	}

	public class_124 method_830() {
		return this.field_1255;
	}
}
