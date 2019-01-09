package net.minecraft;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum class_1311 {
	field_6302("monster", class_1569.class, 70, false, false),
	field_6294("creature", class_1429.class, 10, true, true),
	field_6303("ambient", class_1421.class, 15, true, false),
	field_6300("water_creature", class_1480.class, 15, true, false);

	private static final Map<String, class_1311> field_6296 = (Map<String, class_1311>)Arrays.stream(values())
		.collect(Collectors.toMap(class_1311::method_6133, arg -> arg));
	private final Class<? extends class_1298> field_6299;
	private final int field_6297;
	private final boolean field_6298;
	private final boolean field_6295;
	private final String field_6304;

	private class_1311(String string2, Class<? extends class_1298> class_, int j, boolean bl, boolean bl2) {
		this.field_6304 = string2;
		this.field_6299 = class_;
		this.field_6297 = j;
		this.field_6298 = bl;
		this.field_6295 = bl2;
	}

	public String method_6133() {
		return this.field_6304;
	}

	public int method_6134() {
		return this.field_6297;
	}

	public boolean method_6136() {
		return this.field_6298;
	}

	public boolean method_6135() {
		return this.field_6295;
	}

	public static class_1311 method_17350(class_1297 arg) {
		for (class_1311 lv : values()) {
			if (lv.field_6299.isAssignableFrom(arg.getClass())) {
				return lv;
			}
		}

		return field_6302;
	}
}
