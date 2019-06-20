package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_4041<T extends class_4019> extends class_583<T> {
	public final class_630 field_18015;
	private final class_630 field_18016;
	private final class_630 field_18017;
	private final class_630 field_18018;
	private final class_630 field_18019;
	private final class_630 field_18020;
	private final class_630 field_18021;
	private final class_630 field_18022;
	private final class_630 field_18023;
	private final class_630 field_18024;
	private float field_18025;

	public class_4041() {
		this.field_17138 = 48;
		this.field_17139 = 32;
		this.field_18015 = new class_630(this, 1, 5);
		this.field_18015.method_2844(-3.0F, -2.0F, -5.0F, 8, 6, 6);
		this.field_18015.method_2851(-1.0F, 16.5F, -3.0F);
		this.field_18016 = new class_630(this, 8, 1);
		this.field_18016.method_2844(-3.0F, -4.0F, -4.0F, 2, 2, 1);
		this.field_18017 = new class_630(this, 15, 1);
		this.field_18017.method_2844(3.0F, -4.0F, -4.0F, 2, 2, 1);
		this.field_18018 = new class_630(this, 6, 18);
		this.field_18018.method_2844(-1.0F, 2.01F, -8.0F, 4, 2, 3);
		this.field_18015.method_2845(this.field_18016);
		this.field_18015.method_2845(this.field_18017);
		this.field_18015.method_2845(this.field_18018);
		this.field_18019 = new class_630(this, 24, 15);
		this.field_18019.method_2844(-3.0F, 3.999F, -3.5F, 6, 11, 6);
		this.field_18019.method_2851(0.0F, 16.0F, -6.0F);
		float f = 0.001F;
		this.field_18020 = new class_630(this, 13, 24);
		this.field_18020.method_2856(2.0F, 0.5F, -1.0F, 2, 6, 2, 0.001F);
		this.field_18020.method_2851(-5.0F, 17.5F, 7.0F);
		this.field_18021 = new class_630(this, 4, 24);
		this.field_18021.method_2856(2.0F, 0.5F, -1.0F, 2, 6, 2, 0.001F);
		this.field_18021.method_2851(-1.0F, 17.5F, 7.0F);
		this.field_18022 = new class_630(this, 13, 24);
		this.field_18022.method_2856(2.0F, 0.5F, -1.0F, 2, 6, 2, 0.001F);
		this.field_18022.method_2851(-5.0F, 17.5F, 0.0F);
		this.field_18023 = new class_630(this, 4, 24);
		this.field_18023.method_2856(2.0F, 0.5F, -1.0F, 2, 6, 2, 0.001F);
		this.field_18023.method_2851(-1.0F, 17.5F, 0.0F);
		this.field_18024 = new class_630(this, 30, 0);
		this.field_18024.method_2844(2.0F, 0.0F, -1.0F, 4, 9, 5);
		this.field_18024.method_2851(-4.0F, 15.0F, -1.0F);
		this.field_18019.method_2845(this.field_18024);
	}

	public void method_18330(T arg, float f, float g, float h) {
		this.field_18019.field_3654 = (float) (Math.PI / 2);
		this.field_18024.field_3654 = -0.05235988F;
		this.field_18020.field_3654 = class_3532.method_15362(f * 0.6662F) * 1.4F * g;
		this.field_18021.field_3654 = class_3532.method_15362(f * 0.6662F + (float) Math.PI) * 1.4F * g;
		this.field_18022.field_3654 = class_3532.method_15362(f * 0.6662F + (float) Math.PI) * 1.4F * g;
		this.field_18023.field_3654 = class_3532.method_15362(f * 0.6662F) * 1.4F * g;
		this.field_18015.method_2851(-1.0F, 16.5F, -3.0F);
		this.field_18015.field_3675 = 0.0F;
		this.field_18015.field_3674 = arg.method_18298(h);
		this.field_18020.field_3665 = true;
		this.field_18021.field_3665 = true;
		this.field_18022.field_3665 = true;
		this.field_18023.field_3665 = true;
		this.field_18019.method_2851(0.0F, 16.0F, -6.0F);
		this.field_18019.field_3674 = 0.0F;
		this.field_18020.method_2851(-5.0F, 17.5F, 7.0F);
		this.field_18021.method_2851(-1.0F, 17.5F, 7.0F);
		if (arg.method_18276()) {
			this.field_18019.field_3654 = 1.6755161F;
			float i = arg.method_18300(h);
			this.field_18019.method_2851(0.0F, 16.0F + arg.method_18300(h), -6.0F);
			this.field_18015.method_2851(-1.0F, 16.5F + i, -3.0F);
			this.field_18015.field_3675 = 0.0F;
		} else if (arg.method_6113()) {
			this.field_18019.field_3674 = (float) (-Math.PI / 2);
			this.field_18019.method_2851(0.0F, 21.0F, -6.0F);
			this.field_18024.field_3654 = (float) (-Math.PI * 5.0 / 6.0);
			if (this.field_3448) {
				this.field_18024.field_3654 = -2.1816616F;
				this.field_18019.method_2851(0.0F, 21.0F, -2.0F);
			}

			this.field_18015.method_2851(1.0F, 19.49F, -3.0F);
			this.field_18015.field_3654 = 0.0F;
			this.field_18015.field_3675 = (float) (-Math.PI * 2.0 / 3.0);
			this.field_18015.field_3674 = 0.0F;
			this.field_18020.field_3665 = false;
			this.field_18021.field_3665 = false;
			this.field_18022.field_3665 = false;
			this.field_18023.field_3665 = false;
		} else if (arg.method_18272()) {
			this.field_18019.field_3654 = (float) (Math.PI / 6);
			this.field_18019.method_2851(0.0F, 9.0F, -3.0F);
			this.field_18024.field_3654 = (float) (Math.PI / 4);
			this.field_18024.method_2851(-4.0F, 15.0F, -2.0F);
			this.field_18015.method_2851(-1.0F, 10.0F, -0.25F);
			this.field_18015.field_3654 = 0.0F;
			this.field_18015.field_3675 = 0.0F;
			if (this.field_3448) {
				this.field_18015.method_2851(-1.0F, 13.0F, -3.75F);
			}

			this.field_18020.field_3654 = (float) (-Math.PI * 5.0 / 12.0);
			this.field_18020.method_2851(-5.0F, 21.5F, 6.75F);
			this.field_18021.field_3654 = (float) (-Math.PI * 5.0 / 12.0);
			this.field_18021.method_2851(-1.0F, 21.5F, 6.75F);
			this.field_18022.field_3654 = (float) (-Math.PI / 12);
			this.field_18023.field_3654 = (float) (-Math.PI / 12);
		}
	}

	public void method_18331(T arg, float f, float g, float h, float i, float j, float k) {
		super.method_2819(arg, f, g, h, i, j, k);
		this.method_18332(arg, f, g, h, i, j, k);
		if (this.field_3448) {
			GlStateManager.pushMatrix();
			float l = 0.75F;
			GlStateManager.scalef(0.75F, 0.75F, 0.75F);
			GlStateManager.translatef(0.0F, 8.0F * k, 3.35F * k);
			this.field_18015.method_2846(k);
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			float m = 0.5F;
			GlStateManager.scalef(0.5F, 0.5F, 0.5F);
			GlStateManager.translatef(0.0F, 24.0F * k, 0.0F);
			this.field_18019.method_2846(k);
			this.field_18020.method_2846(k);
			this.field_18021.method_2846(k);
			this.field_18022.method_2846(k);
			this.field_18023.method_2846(k);
			GlStateManager.popMatrix();
		} else {
			GlStateManager.pushMatrix();
			this.field_18015.method_2846(k);
			this.field_18019.method_2846(k);
			this.field_18020.method_2846(k);
			this.field_18021.method_2846(k);
			this.field_18022.method_2846(k);
			this.field_18023.method_2846(k);
			GlStateManager.popMatrix();
		}
	}

	public void method_18332(T arg, float f, float g, float h, float i, float j, float k) {
		super.method_17080(arg, f, g, h, i, j, k);
		if (!arg.method_6113() && !arg.method_18273() && !arg.method_18276()) {
			this.field_18015.field_3654 = j * (float) (Math.PI / 180.0);
			this.field_18015.field_3675 = i * (float) (Math.PI / 180.0);
		}

		if (arg.method_6113()) {
			this.field_18015.field_3654 = 0.0F;
			this.field_18015.field_3675 = (float) (-Math.PI * 2.0 / 3.0);
			this.field_18015.field_3674 = class_3532.method_15362(h * 0.027F) / 22.0F;
		}

		if (arg.method_18276()) {
			float l = class_3532.method_15362(h) * 0.01F;
			this.field_18019.field_3675 = l;
			this.field_18020.field_3674 = l;
			this.field_18021.field_3674 = l;
			this.field_18022.field_3674 = l / 2.0F;
			this.field_18023.field_3674 = l / 2.0F;
		}

		if (arg.method_18273()) {
			float l = 0.1F;
			this.field_18025 += 0.67F;
			this.field_18020.field_3654 = class_3532.method_15362(this.field_18025 * 0.4662F) * 0.1F;
			this.field_18021.field_3654 = class_3532.method_15362(this.field_18025 * 0.4662F + (float) Math.PI) * 0.1F;
			this.field_18022.field_3654 = class_3532.method_15362(this.field_18025 * 0.4662F + (float) Math.PI) * 0.1F;
			this.field_18023.field_3654 = class_3532.method_15362(this.field_18025 * 0.4662F) * 0.1F;
		}
	}
}
