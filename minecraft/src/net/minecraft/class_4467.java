package net.minecraft;

import java.util.Arrays;
import java.util.Comparator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public enum class_4467 {
	field_20361(0, "options.off"),
	field_20362(1, "options.attack.crosshair"),
	field_20363(2, "options.attack.hotbar");

	private static final class_4467[] field_20364 = (class_4467[])Arrays.stream(values())
		.sorted(Comparator.comparingInt(class_4467::method_21774))
		.toArray(class_4467[]::new);
	private final int field_20365;
	private final String field_20366;

	private class_4467(int j, String string2) {
		this.field_20365 = j;
		this.field_20366 = string2;
	}

	public int method_21774() {
		return this.field_20365;
	}

	public String method_21776() {
		return this.field_20366;
	}

	public static class_4467 method_21775(int i) {
		return field_20364[MathHelper.floorMod(i, field_20364.length)];
	}
}
