package net.minecraft;

import java.util.Arrays;
import java.util.Comparator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public enum class_4065 {
	field_18176(0, "options.narrator.off"),
	field_18177(1, "options.narrator.all"),
	field_18178(2, "options.narrator.chat"),
	field_18179(3, "options.narrator.system");

	private static final class_4065[] field_18180 = (class_4065[])Arrays.stream(values())
		.sorted(Comparator.comparingInt(class_4065::method_18509))
		.toArray(class_4065[]::new);
	private final int field_18181;
	private final String field_18182;

	private class_4065(int j, String string2) {
		this.field_18181 = j;
		this.field_18182 = string2;
	}

	public int method_18509() {
		return this.field_18181;
	}

	public String method_18511() {
		return this.field_18182;
	}

	public static class_4065 method_18510(int i) {
		return field_18180[MathHelper.floorMod(i, field_18180.length)];
	}
}
