package net.minecraft;

import java.util.Arrays;
import java.util.Comparator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public enum class_4066 {
	field_18197(0, "options.particles.all"),
	field_18198(1, "options.particles.decreased"),
	field_18199(2, "options.particles.minimal");

	private static final class_4066[] field_18200 = (class_4066[])Arrays.stream(values())
		.sorted(Comparator.comparingInt(class_4066::method_18609))
		.toArray(class_4066[]::new);
	private final int field_18201;
	private final String field_18202;

	private class_4066(int j, String string2) {
		this.field_18201 = j;
		this.field_18202 = string2;
	}

	public String method_18607() {
		return this.field_18202;
	}

	public int method_18609() {
		return this.field_18201;
	}

	public static class_4066 method_18608(int i) {
		return field_18200[class_3532.method_15387(i, field_18200.length)];
	}
}
