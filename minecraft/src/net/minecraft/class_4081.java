package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public enum class_4081 {
	field_18271(class_124.field_1078),
	field_18272(class_124.field_1061),
	field_18273(class_124.field_1078);

	private final class_124 field_18274;

	private class_4081(class_124 arg) {
		this.field_18274 = arg;
	}

	@Environment(EnvType.CLIENT)
	public class_124 method_18793() {
		return this.field_18274;
	}
}
