package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_1116 implements class_1104 {
	private final class_746 field_5481;
	private final class_1144 field_5479;
	private int field_5480 = 0;

	public class_1116(class_746 arg, class_1144 arg2) {
		this.field_5481 = arg;
		this.field_5479 = arg2;
	}

	@Override
	public void method_4756() {
		this.field_5480--;
		if (this.field_5480 <= 0 && this.field_5481.method_5869()) {
			float f = this.field_5481.field_6002.field_9229.nextFloat();
			if (f < 1.0E-4F) {
				this.field_5480 = 0;
				this.field_5479.method_4873(new class_1118.class_1119(this.field_5481, class_3417.field_15178));
			} else if (f < 0.001F) {
				this.field_5480 = 0;
				this.field_5479.method_4873(new class_1118.class_1119(this.field_5481, class_3417.field_15068));
			} else if (f < 0.01F) {
				this.field_5480 = 0;
				this.field_5479.method_4873(new class_1118.class_1119(this.field_5481, class_3417.field_15028));
			}
		}
	}
}
