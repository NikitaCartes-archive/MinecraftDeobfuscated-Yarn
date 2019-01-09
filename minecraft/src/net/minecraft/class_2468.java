package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2468 extends class_2346 {
	private final int field_11462;

	public class_2468(int i, class_2248.class_2251 arg) {
		super(arg);
		this.field_11462 = i;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public int method_10130(class_2680 arg) {
		return this.field_11462;
	}
}
