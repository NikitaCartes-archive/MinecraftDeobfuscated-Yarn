package net.minecraft;

import java.util.Arrays;
import java.util.Comparator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public enum class_5365 {
	field_25427(0, "options.graphics.fast"),
	field_25428(1, "options.graphics.fancy"),
	field_25429(2, "options.graphics.fabulous");

	private static final class_5365[] field_25430 = (class_5365[])Arrays.stream(values())
		.sorted(Comparator.comparingInt(class_5365::method_29591))
		.toArray(class_5365[]::new);
	private final int field_25431;
	private final String field_25432;

	private class_5365(int j, String string2) {
		this.field_25431 = j;
		this.field_25432 = string2;
	}

	public int method_29591() {
		return this.field_25431;
	}

	public String method_29593() {
		return this.field_25432;
	}

	public class_5365 method_29595() {
		return method_29592(this.method_29591() + 1);
	}

	public class_5365 method_29596() {
		return method_29592(this.method_29591() - 1);
	}

	public String toString() {
		switch (this) {
			case field_25427:
				return "fast";
			case field_25428:
				return "fancy";
			case field_25429:
				return "fabulous";
			default:
				throw new IllegalArgumentException();
		}
	}

	public static class_5365 method_29592(int i) {
		return field_25430[MathHelper.floorMod(i, field_25430.length)];
	}
}
