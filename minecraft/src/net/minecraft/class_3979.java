package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_3979 extends class_465<class_3971> {
	private static final class_2960 field_17673 = new class_2960("textures/gui/container/stonecutter.png");
	private float field_17674;
	private boolean field_17670;
	private int field_17671;
	private boolean field_17672;

	public class_3979(class_3971 arg, class_1661 arg2, class_2561 arg3) {
		super(arg, arg2, arg3);
		arg.method_17859(this::method_17955);
	}

	@Override
	public void render(int i, int j, float f) {
		super.render(i, j, f);
		this.method_2380(i, j);
	}

	@Override
	protected void method_2388(int i, int j) {
		this.font.method_1729(this.title.method_10863(), 8.0F, 4.0F, 4210752);
		this.font.method_1729(this.field_17410.method_5476().method_10863(), 8.0F, (float)(this.field_2779 - 94), 4210752);
	}

	@Override
	protected void method_2389(float f, int i, int j) {
		this.renderBackground();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.method_1531().method_4618(field_17673);
		int k = this.field_2776;
		int l = this.field_2800;
		this.blit(k, l, 0, 0, this.field_2792, this.field_2779);
		int m = (int)(41.0F * this.field_17674);
		this.blit(k + 119, l + 15 + m, 176 + (this.method_17954() ? 0 : 12), 0, 12, 15);
		int n = this.field_2776 + 52;
		int o = this.field_2800 + 14;
		int p = this.field_17671 + 12;
		this.method_17952(i, j, n, o, p);
		this.method_17951(n, o, p);
	}

	private void method_17952(int i, int j, int k, int l, int m) {
		for (int n = this.field_17671; n < m && n < this.field_2797.method_17864(); n++) {
			int o = n - this.field_17671;
			int p = k + o % 4 * 16;
			int q = o / 4;
			int r = l + q * 18 + 2;
			int s = this.field_2779;
			if (n == this.field_2797.method_17862()) {
				s += 18;
			} else if (i >= p && j >= r && i < p + 16 && j < r + 18) {
				s += 36;
			}

			this.blit(p, r - 1, 0, s, 16, 18);
		}
	}

	private void method_17951(int i, int j, int k) {
		class_308.method_1453();
		List<class_3975> list = this.field_2797.method_17863();

		for (int l = this.field_17671; l < k && l < this.field_2797.method_17864(); l++) {
			int m = l - this.field_17671;
			int n = i + m % 4 * 16;
			int o = m / 4;
			int p = j + o * 18 + 2;
			this.minecraft.method_1480().method_4023(((class_3975)list.get(l)).method_8110(), n, p);
		}

		class_308.method_1450();
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		this.field_17670 = false;
		if (this.field_17672) {
			int j = this.field_2776 + 52;
			int k = this.field_2800 + 14;
			int l = this.field_17671 + 12;

			for (int m = this.field_17671; m < l; m++) {
				int n = m - this.field_17671;
				double f = d - (double)(j + n % 4 * 16);
				double g = e - (double)(k + n / 4 * 18);
				if (f >= 0.0 && g >= 0.0 && f < 16.0 && g < 18.0 && this.field_2797.method_7604(this.minecraft.field_1724, m)) {
					class_310.method_1551().method_1483().method_4873(class_1109.method_4758(class_3417.field_17711, 1.0F));
					this.minecraft.field_1761.method_2900(this.field_2797.field_7763, m);
					return true;
				}
			}

			j = this.field_2776 + 119;
			k = this.field_2800 + 9;
			if (d >= (double)j && d < (double)(j + 12) && e >= (double)k && e < (double)(k + 54)) {
				this.field_17670 = true;
			}
		}

		return super.mouseClicked(d, e, i);
	}

	@Override
	public boolean mouseDragged(double d, double e, int i, double f, double g) {
		if (this.field_17670 && this.method_17954()) {
			int j = this.field_2800 + 14;
			int k = j + 54;
			this.field_17674 = ((float)e - (float)j - 7.5F) / ((float)(k - j) - 15.0F);
			this.field_17674 = class_3532.method_15363(this.field_17674, 0.0F, 1.0F);
			this.field_17671 = (int)((double)(this.field_17674 * (float)this.method_17953()) + 0.5) * 4;
			return true;
		} else {
			return super.mouseDragged(d, e, i, f, g);
		}
	}

	@Override
	public boolean mouseScrolled(double d, double e, double f) {
		if (this.method_17954()) {
			int i = this.method_17953();
			this.field_17674 = (float)((double)this.field_17674 - f / (double)i);
			this.field_17674 = class_3532.method_15363(this.field_17674, 0.0F, 1.0F);
			this.field_17671 = (int)((double)(this.field_17674 * (float)i) + 0.5) * 4;
		}

		return true;
	}

	private boolean method_17954() {
		return this.field_17672 && this.field_2797.method_17864() > 12;
	}

	protected int method_17953() {
		return (this.field_2797.method_17864() + 4 - 1) / 4 - 3;
	}

	private void method_17955() {
		this.field_17672 = this.field_2797.method_17865();
		if (!this.field_17672) {
			this.field_17674 = 0.0F;
			this.field_17671 = 0;
		}
	}
}
