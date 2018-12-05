package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public enum class_455 {
	field_2701(0),
	field_2699(1);

	private final int field_2700;

	private class_455(int j) {
		this.field_2700 = j;
	}

	public int method_2320() {
		return this.field_2700;
	}
}
