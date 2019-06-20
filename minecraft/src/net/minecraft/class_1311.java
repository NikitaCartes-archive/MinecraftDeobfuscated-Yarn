package net.minecraft;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum class_1311 {
	field_6302("monster", 70, false, false),
	field_6294("creature", 10, true, true),
	field_6303("ambient", 15, true, false),
	field_6300("water_creature", 15, true, false),
	field_17715("misc", 15, true, false);

	private static final Map<String, class_1311> field_6296 = (Map<String, class_1311>)Arrays.stream(values())
		.collect(Collectors.toMap(class_1311::method_6133, arg -> arg));
	private final int field_6297;
	private final boolean field_6298;
	private final boolean field_6295;
	private final String field_6304;

	private class_1311(String string2, int j, boolean bl, boolean bl2) {
		this.field_6304 = string2;
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
}
