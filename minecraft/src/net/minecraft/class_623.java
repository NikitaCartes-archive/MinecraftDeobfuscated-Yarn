package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_623<T extends class_1642> extends class_572<T> {
	public class_623() {
		this(0.0F, false);
	}

	protected class_623(float f, float g, int i, int j) {
		super(f, g, i, j);
	}

	public class_623(float f, boolean bl) {
		super(f, 0.0F, 64, bl ? 32 : 64);
	}

	public void method_17134(T arg, float f, float g, float h, float i, float j, float k) {
		super.method_17087(arg, f, g, h, i, j, k);
		boolean bl = arg.method_6999();
		float l = class_3532.method_15374(this.field_3447 * (float) Math.PI);
		float m = class_3532.method_15374((1.0F - (1.0F - this.field_3447) * (1.0F - this.field_3447)) * (float) Math.PI);
		this.field_3401.field_3674 = 0.0F;
		this.field_3390.field_3674 = 0.0F;
		this.field_3401.field_3675 = -(0.1F - l * 0.6F);
		this.field_3390.field_3675 = 0.1F - l * 0.6F;
		float n = (float) -Math.PI / (bl ? 1.5F : 2.25F);
		this.field_3401.field_3654 = n;
		this.field_3390.field_3654 = n;
		this.field_3401.field_3654 += l * 1.2F - m * 0.4F;
		this.field_3390.field_3654 += l * 1.2F - m * 0.4F;
		this.field_3401.field_3674 = this.field_3401.field_3674 + class_3532.method_15362(h * 0.09F) * 0.05F + 0.05F;
		this.field_3390.field_3674 = this.field_3390.field_3674 - (class_3532.method_15362(h * 0.09F) * 0.05F + 0.05F);
		this.field_3401.field_3654 = this.field_3401.field_3654 + class_3532.method_15374(h * 0.067F) * 0.05F;
		this.field_3390.field_3654 = this.field_3390.field_3654 - class_3532.method_15374(h * 0.067F) * 0.05F;
	}
}
