package net.minecraft;

import java.util.Arrays;
import java.util.Comparator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public enum class_4061 {
	field_18151(0, "options.off"),
	field_18152(1, "options.attack.crosshair"),
	field_18153(2, "options.attack.hotbar");

	private static final class_4061[] field_18154 = (class_4061[])Arrays.stream(values())
		.sorted(Comparator.comparingInt(class_4061::method_18487))
		.toArray(class_4061[]::new);
	private final int field_18155;
	private final String field_18156;

	private class_4061(int j, String string2) {
		this.field_18155 = j;
		this.field_18156 = string2;
	}

	public int method_18487() {
		return this.field_18155;
	}

	public String method_18489() {
		return this.field_18156;
	}

	public static class_4061 method_18488(int i) {
		return field_18154[class_3532.method_15387(i, field_18154.length)];
	}
}
