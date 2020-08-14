package net.minecraft;

import java.util.Arrays;
import java.util.Comparator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public enum class_5512 {
	field_26810(0, "options.off"),
	field_26811(1, "options.attack.crosshair"),
	field_26812(2, "options.attack.hotbar");

	private static final class_5512[] field_26813 = (class_5512[])Arrays.stream(values())
		.sorted(Comparator.comparingInt(class_5512::method_31258))
		.toArray(class_5512[]::new);
	private final int field_26814;
	private final String field_26815;

	private class_5512(int j, String string2) {
		this.field_26814 = j;
		this.field_26815 = string2;
	}

	public int method_31258() {
		return this.field_26814;
	}

	public String method_31260() {
		return this.field_26815;
	}

	public static class_5512 method_31259(int i) {
		return field_26813[MathHelper.floorMod(i, field_26813.length)];
	}
}
