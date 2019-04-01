package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_562<T extends class_1297> extends class_583<T> {
	private final class_630 field_3360;
	private final class_630 field_3362;
	private final class_630 field_3361;
	private final class_630 field_3359;
	private final class_630 field_3358;
	private final class_630 field_3363;
	private final class_630 field_3357;

	public class_562() {
		this(0.0F);
	}

	public class_562(float f) {
		int i = 6;
		this.field_3360 = new class_630(this, 0, 0);
		this.field_3360.method_2856(-4.0F, -8.0F, -4.0F, 8, 8, 8, f);
		this.field_3360.method_2851(0.0F, 6.0F, 0.0F);
		this.field_3362 = new class_630(this, 32, 0);
		this.field_3362.method_2856(-4.0F, -8.0F, -4.0F, 8, 8, 8, f + 0.5F);
		this.field_3362.method_2851(0.0F, 6.0F, 0.0F);
		this.field_3361 = new class_630(this, 16, 16);
		this.field_3361.method_2856(-4.0F, 0.0F, -2.0F, 8, 12, 4, f);
		this.field_3361.method_2851(0.0F, 6.0F, 0.0F);
		this.field_3359 = new class_630(this, 0, 16);
		this.field_3359.method_2856(-2.0F, 0.0F, -2.0F, 4, 6, 4, f);
		this.field_3359.method_2851(-2.0F, 18.0F, 4.0F);
		this.field_3358 = new class_630(this, 0, 16);
		this.field_3358.method_2856(-2.0F, 0.0F, -2.0F, 4, 6, 4, f);
		this.field_3358.method_2851(2.0F, 18.0F, 4.0F);
		this.field_3363 = new class_630(this, 0, 16);
		this.field_3363.method_2856(-2.0F, 0.0F, -2.0F, 4, 6, 4, f);
		this.field_3363.method_2851(-2.0F, 18.0F, -4.0F);
		this.field_3357 = new class_630(this, 0, 16);
		this.field_3357.method_2856(-2.0F, 0.0F, -2.0F, 4, 6, 4, f);
		this.field_3357.method_2851(2.0F, 18.0F, -4.0F);
	}

	@Override
	public void method_2819(T arg, float f, float g, float h, float i, float j, float k) {
		this.method_17080(arg, f, g, h, i, j, k);
		this.field_3360.method_2846(k);
		this.field_3361.method_2846(k);
		this.field_3359.method_2846(k);
		this.field_3358.method_2846(k);
		this.field_3363.method_2846(k);
		this.field_3357.method_2846(k);
	}

	@Override
	public void method_17080(T arg, float f, float g, float h, float i, float j, float k) {
		this.field_3360.field_3675 = i * (float) (Math.PI / 180.0);
		this.field_3360.field_3654 = j * (float) (Math.PI / 180.0);
		this.field_3359.field_3654 = class_3532.method_15362(f * 0.6662F) * 1.4F * g;
		this.field_3358.field_3654 = class_3532.method_15362(f * 0.6662F + (float) Math.PI) * 1.4F * g;
		this.field_3363.field_3654 = class_3532.method_15362(f * 0.6662F + (float) Math.PI) * 1.4F * g;
		this.field_3357.field_3654 = class_3532.method_15362(f * 0.6662F) * 1.4F * g;
	}
}
