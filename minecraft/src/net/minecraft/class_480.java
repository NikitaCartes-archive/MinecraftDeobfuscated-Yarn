package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_480 extends class_465<class_1716> {
	private static final class_2960 field_2885 = new class_2960("textures/gui/container/dispenser.png");

	public class_480(class_1716 arg, class_1661 arg2, class_2561 arg3) {
		super(arg, arg2, arg3);
	}

	@Override
	public void method_2214(int i, int j, float f) {
		this.method_2240();
		super.method_2214(i, j, f);
		this.method_2380(i, j);
	}

	@Override
	protected void method_2388(int i, int j) {
		String string = this.field_17411.method_10863();
		this.field_2554.method_1729(string, (float)(this.field_2792 / 2 - this.field_2554.method_1727(string) / 2), 6.0F, 4210752);
		this.field_2554.method_1729(this.field_17410.method_5476().method_10863(), 8.0F, (float)(this.field_2779 - 96 + 2), 4210752);
	}

	@Override
	protected void method_2389(float f, int i, int j) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.field_2563.method_1531().method_4618(field_2885);
		int k = (this.field_2561 - this.field_2792) / 2;
		int l = (this.field_2559 - this.field_2779) / 2;
		this.method_1788(k, l, 0, 0, this.field_2792, this.field_2779);
	}
}
