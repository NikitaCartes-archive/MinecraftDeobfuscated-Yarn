package net.minecraft;

public enum class_1304 {
	field_6173(class_1304.class_1305.field_6177, 0, 0, "mainhand"),
	field_6171(class_1304.class_1305.field_6177, 1, 5, "offhand"),
	field_6166(class_1304.class_1305.field_6178, 0, 1, "feet"),
	field_6172(class_1304.class_1305.field_6178, 1, 2, "legs"),
	field_6174(class_1304.class_1305.field_6178, 2, 3, "chest"),
	field_6169(class_1304.class_1305.field_6178, 3, 4, "head");

	private final class_1304.class_1305 field_6170;
	private final int field_6168;
	private final int field_6167;
	private final String field_6175;

	private class_1304(class_1304.class_1305 arg, int j, int k, String string2) {
		this.field_6170 = arg;
		this.field_6168 = j;
		this.field_6167 = k;
		this.field_6175 = string2;
	}

	public class_1304.class_1305 method_5925() {
		return this.field_6170;
	}

	public int method_5927() {
		return this.field_6168;
	}

	public int method_5926() {
		return this.field_6167;
	}

	public String method_5923() {
		return this.field_6175;
	}

	public static class_1304 method_5924(String string) {
		for (class_1304 lv : values()) {
			if (lv.method_5923().equals(string)) {
				return lv;
			}
		}

		throw new IllegalArgumentException("Invalid slot '" + string + "'");
	}

	public static class_1304 method_20234(class_1304.class_1305 arg, int i) {
		for (class_1304 lv : values()) {
			if (lv.method_5925() == arg && lv.method_5927() == i) {
				return lv;
			}
		}

		throw new IllegalArgumentException("Invalid slot '" + arg + "': " + i);
	}

	public static enum class_1305 {
		field_6177,
		field_6178;
	}
}
