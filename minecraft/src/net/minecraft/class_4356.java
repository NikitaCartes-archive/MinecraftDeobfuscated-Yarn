package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_4356 extends class_4355 {
	public final int field_19608;

	public class_4356(int i) {
		super(503, "Retry operation", -1, "");
		if (i >= 0 && i <= 120) {
			this.field_19608 = i;
		} else {
			this.field_19608 = 5;
		}
	}
}
