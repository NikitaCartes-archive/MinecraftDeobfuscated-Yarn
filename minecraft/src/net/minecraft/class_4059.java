package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_4059 extends class_1792 {
	private final int field_18136;
	private final String field_18137;

	public class_4059(int i, String string, class_1792.class_1793 arg) {
		super(arg);
		this.field_18136 = i;
		this.field_18137 = "textures/entity/horse/armor/horse_armor_" + string + ".png";
	}

	@Environment(EnvType.CLIENT)
	public class_2960 method_18454() {
		return new class_2960(this.field_18137);
	}

	public int method_18455() {
		return this.field_18136;
	}
}
