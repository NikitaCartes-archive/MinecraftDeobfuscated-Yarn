package net.minecraft;

import java.util.Arrays;
import java.util.Comparator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public enum class_4060 {
	field_18144(0, "options.ao.off"),
	field_18145(1, "options.ao.min"),
	field_18146(2, "options.ao.max");

	private static final class_4060[] field_18147 = (class_4060[])Arrays.stream(values())
		.sorted(Comparator.comparingInt(class_4060::method_18483))
		.toArray(class_4060[]::new);
	private final int field_18148;
	private final String field_18149;

	private class_4060(int j, String string2) {
		this.field_18148 = j;
		this.field_18149 = string2;
	}

	public int method_18483() {
		return this.field_18148;
	}

	public String method_18485() {
		return this.field_18149;
	}

	public static class_4060 method_18484(int i) {
		return field_18147[class_3532.method_15387(i, field_18147.length)];
	}
}
