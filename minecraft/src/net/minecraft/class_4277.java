package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_4277 implements class_1104 {
	private final class_746 field_19192;
	private boolean field_19193;
	private boolean field_19194 = true;

	public class_4277(class_746 arg) {
		this.field_19192 = arg;
	}

	@Override
	public void method_4756() {
		class_1937 lv = this.field_19192.field_6002;
		class_2680 lv2 = lv.method_8475(this.field_19192.method_5829().method_1009(0.0, -0.4F, 0.0).method_1011(0.001), class_2246.field_10422);
		if (lv2 != null) {
			if (!this.field_19193 && !this.field_19194 && lv2.method_11614() == class_2246.field_10422 && !this.field_19192.method_7325()) {
				boolean bl = (Boolean)lv2.method_11654(class_2258.field_10680);
				if (bl) {
					this.field_19192.method_5783(class_3417.field_19196, 1.0F, 1.0F);
				} else {
					this.field_19192.method_5783(class_3417.field_19195, 1.0F, 1.0F);
				}
			}

			this.field_19193 = true;
		} else {
			this.field_19193 = false;
		}

		this.field_19194 = false;
	}
}
