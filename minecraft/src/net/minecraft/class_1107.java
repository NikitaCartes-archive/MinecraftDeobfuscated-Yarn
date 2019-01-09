package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_1107 extends class_1101 {
	private final class_1657 field_5457;
	private final class_1688 field_5456;

	public class_1107(class_1657 arg, class_1688 arg2) {
		super(class_3417.field_14832, class_3419.field_15254);
		this.field_5457 = arg;
		this.field_5456 = arg2;
		this.field_5440 = class_1113.class_1114.field_5478;
		this.field_5446 = true;
		this.field_5451 = 0;
	}

	@Override
	public void method_16896() {
		if (!this.field_5456.field_5988 && this.field_5457.method_5765() && this.field_5457.method_5854() == this.field_5456) {
			float f = class_3532.method_15368(this.field_5456.field_5967 * this.field_5456.field_5967 + this.field_5456.field_6006 * this.field_5456.field_6006);
			if ((double)f >= 0.01) {
				this.field_5442 = 0.0F + class_3532.method_15363(f, 0.0F, 1.0F) * 0.75F;
			} else {
				this.field_5442 = 0.0F;
			}
		} else {
			this.field_5438 = true;
		}
	}
}
