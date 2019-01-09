package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_624<T extends class_1493> extends class_583<T> {
	private final class_630 field_3621;
	private final class_630 field_3623;
	private final class_630 field_3622;
	private final class_630 field_3620;
	private final class_630 field_3618;
	private final class_630 field_3624;
	private final class_630 field_3617;
	private final class_630 field_3619;

	public class_624() {
		float f = 0.0F;
		float g = 13.5F;
		this.field_3621 = new class_630(this, 0, 0);
		this.field_3621.method_2856(-2.0F, -3.0F, -2.0F, 6, 6, 4, 0.0F);
		this.field_3621.method_2851(-1.0F, 13.5F, -7.0F);
		this.field_3623 = new class_630(this, 18, 14);
		this.field_3623.method_2856(-3.0F, -2.0F, -3.0F, 6, 9, 6, 0.0F);
		this.field_3623.method_2851(0.0F, 14.0F, 2.0F);
		this.field_3619 = new class_630(this, 21, 0);
		this.field_3619.method_2856(-3.0F, -3.0F, -3.0F, 8, 6, 7, 0.0F);
		this.field_3619.method_2851(-1.0F, 14.0F, 2.0F);
		this.field_3622 = new class_630(this, 0, 18);
		this.field_3622.method_2856(0.0F, 0.0F, -1.0F, 2, 8, 2, 0.0F);
		this.field_3622.method_2851(-2.5F, 16.0F, 7.0F);
		this.field_3620 = new class_630(this, 0, 18);
		this.field_3620.method_2856(0.0F, 0.0F, -1.0F, 2, 8, 2, 0.0F);
		this.field_3620.method_2851(0.5F, 16.0F, 7.0F);
		this.field_3618 = new class_630(this, 0, 18);
		this.field_3618.method_2856(0.0F, 0.0F, -1.0F, 2, 8, 2, 0.0F);
		this.field_3618.method_2851(-2.5F, 16.0F, -4.0F);
		this.field_3624 = new class_630(this, 0, 18);
		this.field_3624.method_2856(0.0F, 0.0F, -1.0F, 2, 8, 2, 0.0F);
		this.field_3624.method_2851(0.5F, 16.0F, -4.0F);
		this.field_3617 = new class_630(this, 9, 18);
		this.field_3617.method_2856(0.0F, 0.0F, -1.0F, 2, 8, 2, 0.0F);
		this.field_3617.method_2851(-1.0F, 12.0F, 8.0F);
		this.field_3621.method_2850(16, 14).method_2856(-2.0F, -5.0F, 0.0F, 2, 2, 1, 0.0F);
		this.field_3621.method_2850(16, 14).method_2856(2.0F, -5.0F, 0.0F, 2, 2, 1, 0.0F);
		this.field_3621.method_2850(0, 10).method_2856(-0.5F, 0.0F, -5.0F, 3, 3, 4, 0.0F);
	}

	public void method_17132(T arg, float f, float g, float h, float i, float j, float k) {
		super.method_2819(arg, f, g, h, i, j, k);
		this.method_17133(arg, f, g, h, i, j, k);
		if (this.field_3448) {
			float l = 2.0F;
			GlStateManager.pushMatrix();
			GlStateManager.translatef(0.0F, 5.0F * k, 2.0F * k);
			this.field_3621.method_2852(k);
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			GlStateManager.scalef(0.5F, 0.5F, 0.5F);
			GlStateManager.translatef(0.0F, 24.0F * k, 0.0F);
			this.field_3623.method_2846(k);
			this.field_3622.method_2846(k);
			this.field_3620.method_2846(k);
			this.field_3618.method_2846(k);
			this.field_3624.method_2846(k);
			this.field_3617.method_2852(k);
			this.field_3619.method_2846(k);
			GlStateManager.popMatrix();
		} else {
			this.field_3621.method_2852(k);
			this.field_3623.method_2846(k);
			this.field_3622.method_2846(k);
			this.field_3620.method_2846(k);
			this.field_3618.method_2846(k);
			this.field_3624.method_2846(k);
			this.field_3617.method_2852(k);
			this.field_3619.method_2846(k);
		}
	}

	public void method_17131(T arg, float f, float g, float h) {
		if (arg.method_6709()) {
			this.field_3617.field_3675 = 0.0F;
		} else {
			this.field_3617.field_3675 = class_3532.method_15362(f * 0.6662F) * 1.4F * g;
		}

		if (arg.method_6172()) {
			this.field_3619.method_2851(-1.0F, 16.0F, -3.0F);
			this.field_3619.field_3654 = (float) (Math.PI * 2.0 / 5.0);
			this.field_3619.field_3675 = 0.0F;
			this.field_3623.method_2851(0.0F, 18.0F, 0.0F);
			this.field_3623.field_3654 = (float) (Math.PI / 4);
			this.field_3617.method_2851(-1.0F, 21.0F, 6.0F);
			this.field_3622.method_2851(-2.5F, 22.0F, 2.0F);
			this.field_3622.field_3654 = (float) (Math.PI * 3.0 / 2.0);
			this.field_3620.method_2851(0.5F, 22.0F, 2.0F);
			this.field_3620.field_3654 = (float) (Math.PI * 3.0 / 2.0);
			this.field_3618.field_3654 = 5.811947F;
			this.field_3618.method_2851(-2.49F, 17.0F, -4.0F);
			this.field_3624.field_3654 = 5.811947F;
			this.field_3624.method_2851(0.51F, 17.0F, -4.0F);
		} else {
			this.field_3623.method_2851(0.0F, 14.0F, 2.0F);
			this.field_3623.field_3654 = (float) (Math.PI / 2);
			this.field_3619.method_2851(-1.0F, 14.0F, -3.0F);
			this.field_3619.field_3654 = this.field_3623.field_3654;
			this.field_3617.method_2851(-1.0F, 12.0F, 8.0F);
			this.field_3622.method_2851(-2.5F, 16.0F, 7.0F);
			this.field_3620.method_2851(0.5F, 16.0F, 7.0F);
			this.field_3618.method_2851(-2.5F, 16.0F, -4.0F);
			this.field_3624.method_2851(0.5F, 16.0F, -4.0F);
			this.field_3622.field_3654 = class_3532.method_15362(f * 0.6662F) * 1.4F * g;
			this.field_3620.field_3654 = class_3532.method_15362(f * 0.6662F + (float) Math.PI) * 1.4F * g;
			this.field_3618.field_3654 = class_3532.method_15362(f * 0.6662F + (float) Math.PI) * 1.4F * g;
			this.field_3624.field_3654 = class_3532.method_15362(f * 0.6662F) * 1.4F * g;
		}

		this.field_3621.field_3674 = arg.method_6719(h) + arg.method_6715(h, 0.0F);
		this.field_3619.field_3674 = arg.method_6715(h, -0.08F);
		this.field_3623.field_3674 = arg.method_6715(h, -0.16F);
		this.field_3617.field_3674 = arg.method_6715(h, -0.2F);
	}

	public void method_17133(T arg, float f, float g, float h, float i, float j, float k) {
		super.method_17080(arg, f, g, h, i, j, k);
		this.field_3621.field_3654 = j * (float) (Math.PI / 180.0);
		this.field_3621.field_3675 = i * (float) (Math.PI / 180.0);
		this.field_3617.field_3654 = h;
	}
}
