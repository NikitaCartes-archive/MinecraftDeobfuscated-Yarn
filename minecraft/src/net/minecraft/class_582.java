package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_582<T extends class_1297> extends class_583<T> {
	protected final class_630 field_3439;
	protected final class_630 field_3441;
	protected final class_630 field_3440;
	protected final class_630 field_3438;
	protected final class_630 field_3436;
	protected final class_630 field_3442;
	protected final class_630 field_3435;
	protected final class_630 field_3437;
	protected int field_3434 = 1;

	public class_582(float f) {
		this.field_3435 = new class_630(this, "head");
		this.field_3435.method_2848("main", -2.5F, -2.0F, -3.0F, 5, 4, 5, f, 0, 0);
		this.field_3435.method_2848("nose", -1.5F, 0.0F, -4.0F, 3, 2, 2, f, 0, 24);
		this.field_3435.method_2848("ear1", -2.0F, -3.0F, 0.0F, 1, 1, 2, f, 0, 10);
		this.field_3435.method_2848("ear2", 1.0F, -3.0F, 0.0F, 1, 1, 2, f, 6, 10);
		this.field_3435.method_2851(0.0F, 15.0F, -9.0F);
		this.field_3437 = new class_630(this, 20, 0);
		this.field_3437.method_2856(-2.0F, 3.0F, -8.0F, 4, 16, 6, f);
		this.field_3437.method_2851(0.0F, 12.0F, -10.0F);
		this.field_3436 = new class_630(this, 0, 15);
		this.field_3436.method_2856(-0.5F, 0.0F, 0.0F, 1, 8, 1, f);
		this.field_3436.field_3654 = 0.9F;
		this.field_3436.method_2851(0.0F, 15.0F, 8.0F);
		this.field_3442 = new class_630(this, 4, 15);
		this.field_3442.method_2856(-0.5F, 0.0F, 0.0F, 1, 8, 1, f);
		this.field_3442.method_2851(0.0F, 20.0F, 14.0F);
		this.field_3439 = new class_630(this, 8, 13);
		this.field_3439.method_2856(-1.0F, 0.0F, 1.0F, 2, 6, 2, f);
		this.field_3439.method_2851(1.1F, 18.0F, 5.0F);
		this.field_3441 = new class_630(this, 8, 13);
		this.field_3441.method_2856(-1.0F, 0.0F, 1.0F, 2, 6, 2, f);
		this.field_3441.method_2851(-1.1F, 18.0F, 5.0F);
		this.field_3440 = new class_630(this, 40, 0);
		this.field_3440.method_2856(-1.0F, 0.0F, 0.0F, 2, 10, 2, f);
		this.field_3440.method_2851(1.2F, 13.8F, -5.0F);
		this.field_3438 = new class_630(this, 40, 0);
		this.field_3438.method_2856(-1.0F, 0.0F, 0.0F, 2, 10, 2, f);
		this.field_3438.method_2851(-1.2F, 13.8F, -5.0F);
	}

	@Override
	public void method_2819(T arg, float f, float g, float h, float i, float j, float k) {
		this.method_17080(arg, f, g, h, i, j, k);
		if (this.field_3448) {
			float l = 2.0F;
			GlStateManager.pushMatrix();
			GlStateManager.scalef(0.75F, 0.75F, 0.75F);
			GlStateManager.translatef(0.0F, 10.0F * k, 4.0F * k);
			this.field_3435.method_2846(k);
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			GlStateManager.scalef(0.5F, 0.5F, 0.5F);
			GlStateManager.translatef(0.0F, 24.0F * k, 0.0F);
			this.field_3437.method_2846(k);
			this.field_3439.method_2846(k);
			this.field_3441.method_2846(k);
			this.field_3440.method_2846(k);
			this.field_3438.method_2846(k);
			this.field_3436.method_2846(k);
			this.field_3442.method_2846(k);
			GlStateManager.popMatrix();
		} else {
			this.field_3435.method_2846(k);
			this.field_3437.method_2846(k);
			this.field_3436.method_2846(k);
			this.field_3442.method_2846(k);
			this.field_3439.method_2846(k);
			this.field_3441.method_2846(k);
			this.field_3440.method_2846(k);
			this.field_3438.method_2846(k);
		}
	}

	@Override
	public void method_17080(T arg, float f, float g, float h, float i, float j, float k) {
		this.field_3435.field_3654 = j * (float) (Math.PI / 180.0);
		this.field_3435.field_3675 = i * (float) (Math.PI / 180.0);
		if (this.field_3434 != 3) {
			this.field_3437.field_3654 = (float) (Math.PI / 2);
			if (this.field_3434 == 2) {
				this.field_3439.field_3654 = class_3532.method_15362(f * 0.6662F) * g;
				this.field_3441.field_3654 = class_3532.method_15362(f * 0.6662F + 0.3F) * g;
				this.field_3440.field_3654 = class_3532.method_15362(f * 0.6662F + (float) Math.PI + 0.3F) * g;
				this.field_3438.field_3654 = class_3532.method_15362(f * 0.6662F + (float) Math.PI) * g;
				this.field_3442.field_3654 = 1.7278761F + (float) (Math.PI / 10) * class_3532.method_15362(f) * g;
			} else {
				this.field_3439.field_3654 = class_3532.method_15362(f * 0.6662F) * g;
				this.field_3441.field_3654 = class_3532.method_15362(f * 0.6662F + (float) Math.PI) * g;
				this.field_3440.field_3654 = class_3532.method_15362(f * 0.6662F + (float) Math.PI) * g;
				this.field_3438.field_3654 = class_3532.method_15362(f * 0.6662F) * g;
				if (this.field_3434 == 1) {
					this.field_3442.field_3654 = 1.7278761F + (float) (Math.PI / 4) * class_3532.method_15362(f) * g;
				} else {
					this.field_3442.field_3654 = 1.7278761F + 0.47123894F * class_3532.method_15362(f) * g;
				}
			}
		}
	}

	@Override
	public void method_2816(T arg, float f, float g, float h) {
		this.field_3437.field_3656 = 12.0F;
		this.field_3437.field_3655 = -10.0F;
		this.field_3435.field_3656 = 15.0F;
		this.field_3435.field_3655 = -9.0F;
		this.field_3436.field_3656 = 15.0F;
		this.field_3436.field_3655 = 8.0F;
		this.field_3442.field_3656 = 20.0F;
		this.field_3442.field_3655 = 14.0F;
		this.field_3440.field_3656 = 13.8F;
		this.field_3440.field_3655 = -5.0F;
		this.field_3438.field_3656 = 13.8F;
		this.field_3438.field_3655 = -5.0F;
		this.field_3439.field_3656 = 18.0F;
		this.field_3439.field_3655 = 5.0F;
		this.field_3441.field_3656 = 18.0F;
		this.field_3441.field_3655 = 5.0F;
		this.field_3436.field_3654 = 0.9F;
		if (arg.method_5715()) {
			this.field_3437.field_3656++;
			this.field_3435.field_3656 += 2.0F;
			this.field_3436.field_3656++;
			this.field_3442.field_3656 += -4.0F;
			this.field_3442.field_3655 += 2.0F;
			this.field_3436.field_3654 = (float) (Math.PI / 2);
			this.field_3442.field_3654 = (float) (Math.PI / 2);
			this.field_3434 = 0;
		} else if (arg.method_5624()) {
			this.field_3442.field_3656 = this.field_3436.field_3656;
			this.field_3442.field_3655 += 2.0F;
			this.field_3436.field_3654 = (float) (Math.PI / 2);
			this.field_3442.field_3654 = (float) (Math.PI / 2);
			this.field_3434 = 2;
		} else {
			this.field_3434 = 1;
		}
	}
}
