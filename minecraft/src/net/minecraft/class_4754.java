package net.minecraft;

import java.util.Arrays;
import java.util.Comparator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public enum class_4754 {
	field_21891(0, "options.off"),
	field_21892(1, "options.attack.crosshair"),
	field_21893(2, "options.attack.hotbar");

	private static final class_4754[] field_21894 = (class_4754[])Arrays.stream(values())
		.sorted(Comparator.comparingInt(class_4754::method_24336))
		.toArray(class_4754[]::new);
	private final int field_21895;
	private final String field_21896;

	private class_4754(int j, String string2) {
		this.field_21895 = j;
		this.field_21896 = string2;
	}

	public int method_24336() {
		return this.field_21895;
	}

	public String method_24338() {
		return this.field_21896;
	}

	public static class_4754 method_24337(int i) {
		return field_21894[MathHelper.floorMod(i, field_21894.length)];
	}
}
