package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_472 extends class_465<class_1708> {
	private static final class_2960 field_2823 = new class_2960("textures/gui/container/brewing_stand.png");
	private static final int[] field_2824 = new int[]{29, 24, 20, 16, 11, 6, 0};

	public class_472(class_1708 arg, class_1661 arg2, class_2561 arg3) {
		super(arg, arg2, arg3);
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		super.render(i, j, f);
		this.method_2380(i, j);
	}

	@Override
	protected void method_2388(int i, int j) {
		this.font.method_1729(this.title.method_10863(), (float)(this.field_2792 / 2 - this.font.method_1727(this.title.method_10863()) / 2), 6.0F, 4210752);
		this.font.method_1729(this.field_17410.method_5476().method_10863(), 8.0F, (float)(this.field_2779 - 96 + 2), 4210752);
	}

	@Override
	protected void method_2389(float f, int i, int j) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.method_1531().method_4618(field_2823);
		int k = (this.width - this.field_2792) / 2;
		int l = (this.height - this.field_2779) / 2;
		this.blit(k, l, 0, 0, this.field_2792, this.field_2779);
		int m = this.field_2797.method_17377();
		int n = class_3532.method_15340((18 * m + 20 - 1) / 20, 0, 18);
		if (n > 0) {
			this.blit(k + 60, l + 44, 176, 29, n, 4);
		}

		int o = this.field_2797.method_17378();
		if (o > 0) {
			int p = (int)(28.0F * (1.0F - (float)o / 400.0F));
			if (p > 0) {
				this.blit(k + 97, l + 16, 176, 0, 9, p);
			}

			p = field_2824[o / 2 % 7];
			if (p > 0) {
				this.blit(k + 63, l + 14 + 29 - p, 185, 29 - p, 12, p);
			}
		}
	}
}
