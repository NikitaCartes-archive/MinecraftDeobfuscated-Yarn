package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public enum class_3281 {
	field_14223("old"),
	field_14220("new"),
	field_14224("compatible");

	private final class_2561 field_14219;
	private final class_2561 field_14222;

	private class_3281(String string2) {
		this.field_14219 = new class_2588("resourcePack.incompatible." + string2);
		this.field_14222 = new class_2588("resourcePack.incompatible.confirm." + string2);
	}

	public boolean method_14437() {
		return this == field_14224;
	}

	public static class_3281 method_14436(int i) {
		if (i < class_155.method_16673().getPackVersion()) {
			return field_14223;
		} else {
			return i > class_155.method_16673().getPackVersion() ? field_14220 : field_14224;
		}
	}

	@Environment(EnvType.CLIENT)
	public class_2561 method_14439() {
		return this.field_14219;
	}

	@Environment(EnvType.CLIENT)
	public class_2561 method_14438() {
		return this.field_14222;
	}
}
