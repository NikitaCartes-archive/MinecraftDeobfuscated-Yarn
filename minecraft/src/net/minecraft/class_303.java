package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_303 {
	private final int field_1650;
	private final class_2561 field_1651;
	private final int field_1649;

	public class_303(int i, class_2561 arg, int j) {
		this.field_1651 = arg;
		this.field_1650 = i;
		this.field_1649 = j;
	}

	public class_2561 method_1412() {
		return this.field_1651;
	}

	public int method_1414() {
		return this.field_1650;
	}

	public int method_1413() {
		return this.field_1649;
	}
}
