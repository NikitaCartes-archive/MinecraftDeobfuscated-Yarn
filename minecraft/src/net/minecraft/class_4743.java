package net.minecraft;

import java.util.Arrays;
import java.util.Comparator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public enum class_4743 {
	field_21826(0, "options.off"),
	field_21827(1, "options.attack.crosshair"),
	field_21828(2, "options.attack.hotbar");

	private static final class_4743[] field_21829 = (class_4743[])Arrays.stream(values())
		.sorted(Comparator.comparingInt(class_4743::method_24239))
		.toArray(class_4743[]::new);
	private final int field_21830;
	private final String field_21831;

	private class_4743(int j, String string2) {
		this.field_21830 = j;
		this.field_21831 = string2;
	}

	public int method_24239() {
		return this.field_21830;
	}

	public String method_24241() {
		return this.field_21831;
	}

	public static class_4743 method_24240(int i) {
		return field_21829[MathHelper.floorMod(i, field_21829.length)];
	}
}
