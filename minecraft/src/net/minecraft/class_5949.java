package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public enum class_5949 {
	field_29551("eventLoops"),
	field_29552("mailBoxes");

	private final String field_29553;

	private class_5949(String string2) {
		this.field_29553 = string2;
	}

	public String method_34700() {
		return this.field_29553;
	}
}
