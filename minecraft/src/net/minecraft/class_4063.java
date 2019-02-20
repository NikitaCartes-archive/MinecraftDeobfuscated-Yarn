package net.minecraft;

import java.util.Arrays;
import java.util.Comparator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public enum class_4063 {
	field_18162(0, "options.off"),
	field_18163(1, "options.clouds.fast"),
	field_18164(2, "options.clouds.fancy");

	private static final class_4063[] field_18165 = (class_4063[])Arrays.stream(values())
		.sorted(Comparator.comparingInt(class_4063::method_18496))
		.toArray(class_4063[]::new);
	private final int field_18166;
	private final String field_18167;

	private class_4063(int j, String string2) {
		this.field_18166 = j;
		this.field_18167 = string2;
	}

	public int method_18496() {
		return this.field_18166;
	}

	public String method_18498() {
		return this.field_18167;
	}

	public static class_4063 method_18497(int i) {
		return field_18165[MathHelper.floorMod(i, field_18165.length)];
	}
}
