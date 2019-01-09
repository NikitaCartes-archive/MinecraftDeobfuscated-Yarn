package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_567<T extends class_1297> extends class_583<T> {
	private final class_630 field_3373;
	private final class_630[] field_3372 = new class_630[9];

	public class_567() {
		int i = -16;
		this.field_3373 = new class_630(this, 0, 0);
		this.field_3373.method_2844(-8.0F, -8.0F, -8.0F, 16, 16, 16);
		this.field_3373.field_3656 += 8.0F;
		Random random = new Random(1660L);

		for (int j = 0; j < this.field_3372.length; j++) {
			this.field_3372[j] = new class_630(this, 0, 0);
			float f = (((float)(j % 3) - (float)(j / 3 % 2) * 0.5F + 0.25F) / 2.0F * 2.0F - 1.0F) * 5.0F;
			float g = ((float)(j / 3) / 2.0F * 2.0F - 1.0F) * 5.0F;
			int k = random.nextInt(7) + 8;
			this.field_3372[j].method_2844(-1.0F, 0.0F, -1.0F, 2, k, 2);
			this.field_3372[j].field_3657 = f;
			this.field_3372[j].field_3655 = g;
			this.field_3372[j].field_3656 = 15.0F;
		}
	}

	@Override
	public void method_17080(T arg, float f, float g, float h, float i, float j, float k) {
		for (int l = 0; l < this.field_3372.length; l++) {
			this.field_3372[l].field_3654 = 0.2F * class_3532.method_15374(h * 0.3F + (float)l) + 0.4F;
		}
	}

	@Override
	public void method_2819(T arg, float f, float g, float h, float i, float j, float k) {
		this.method_17080(arg, f, g, h, i, j, k);
		GlStateManager.pushMatrix();
		GlStateManager.translatef(0.0F, 0.6F, 0.0F);
		this.field_3373.method_2846(k);

		for (class_630 lv : this.field_3372) {
			lv.method_2846(k);
		}

		GlStateManager.popMatrix();
	}
}
