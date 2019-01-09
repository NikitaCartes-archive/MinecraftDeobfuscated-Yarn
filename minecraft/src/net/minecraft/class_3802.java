package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_3802 extends class_465<class_3803> {
	private static final class_2960 field_16769 = new class_2960("textures/gui/container/grindstone.png");

	public class_3802(class_3803 arg, class_1661 arg2, class_2561 arg3) {
		super(arg, arg2, arg3);
	}

	@Override
	protected void method_2388(int i, int j) {
		this.field_2554.method_1729(this.field_17411.method_10863(), 8.0F, 6.0F, 4210752);
		this.field_2554.method_1729(this.field_17410.method_5476().method_10863(), 8.0F, (float)(this.field_2779 - 96 + 2), 4210752);
	}

	@Override
	public void method_2214(int i, int j, float f) {
		this.method_2240();
		this.method_2389(f, i, j);
		super.method_2214(i, j, f);
		this.method_2380(i, j);
	}

	@Override
	protected void method_2389(float f, int i, int j) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.field_2563.method_1531().method_4618(field_16769);
		int k = (this.field_2561 - this.field_2792) / 2;
		int l = (this.field_2559 - this.field_2779) / 2;
		this.method_1788(k, l, 0, 0, this.field_2792, this.field_2779);
		if ((this.field_2797.method_7611(0).method_7681() || this.field_2797.method_7611(1).method_7681()) && !this.field_2797.method_7611(2).method_7681()) {
			this.method_1788(k + 92, l + 31, this.field_2792, 0, 28, 21);
		}
	}
}
