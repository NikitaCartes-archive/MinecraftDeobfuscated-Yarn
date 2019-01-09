package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class class_339 extends class_332 implements class_364 {
	protected static final class_2960 field_2072 = new class_2960("textures/gui/widgets.png");
	protected int field_2071 = 200;
	protected int field_2070 = 20;
	public int field_2069;
	public int field_2068;
	public String field_2074;
	public final int field_2077;
	public boolean field_2078 = true;
	public boolean field_2076 = true;
	protected boolean field_2075;
	private boolean field_2073;

	public class_339(int i, int j, int k, String string) {
		this(i, j, k, 200, 20, string);
	}

	public class_339(int i, int j, int k, int l, int m, String string) {
		this.field_2077 = i;
		this.field_2069 = j;
		this.field_2068 = k;
		this.field_2071 = l;
		this.field_2070 = m;
		this.field_2074 = string;
	}

	protected int method_1830(boolean bl) {
		int i = 1;
		if (!this.field_2078) {
			i = 0;
		} else if (bl) {
			i = 2;
		}

		return i;
	}

	public void method_1824(int i, int j, float f) {
		if (this.field_2076) {
			class_310 lv = class_310.method_1551();
			class_327 lv2 = lv.field_1772;
			lv.method_1531().method_4618(field_2072);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.field_2075 = i >= this.field_2069 && j >= this.field_2068 && i < this.field_2069 + this.field_2071 && j < this.field_2068 + this.field_2070;
			int k = this.method_1830(this.field_2075);
			GlStateManager.enableBlend();
			GlStateManager.blendFuncSeparate(
				GlStateManager.class_1033.field_5138, GlStateManager.class_1027.field_5088, GlStateManager.class_1033.field_5140, GlStateManager.class_1027.field_5084
			);
			GlStateManager.blendFunc(GlStateManager.class_1033.field_5138, GlStateManager.class_1027.field_5088);
			this.method_1788(this.field_2069, this.field_2068, 0, 46 + k * 20, this.field_2071 / 2, this.field_2070);
			this.method_1788(this.field_2069 + this.field_2071 / 2, this.field_2068, 200 - this.field_2071 / 2, 46 + k * 20, this.field_2071 / 2, this.field_2070);
			this.method_1827(lv, i, j);
			int l = 14737632;
			if (!this.field_2078) {
				l = 10526880;
			} else if (this.field_2075) {
				l = 16777120;
			}

			this.method_1789(lv2, this.field_2074, this.field_2069 + this.field_2071 / 2, this.field_2068 + (this.field_2070 - 8) / 2, l);
		}
	}

	protected void method_1827(class_310 arg, int i, int j) {
	}

	public void method_1826(double d, double e) {
		this.field_2073 = true;
	}

	public void method_1831(double d, double e) {
		this.field_2073 = false;
	}

	protected void method_1822(double d, double e, double f, double g) {
	}

	@Override
	public boolean method_16807(double d, double e, int i) {
		if (i == 0) {
			boolean bl = this.method_1829(d, e);
			if (bl) {
				this.method_1832(class_310.method_1551().method_1483());
				this.method_1826(d, e);
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean method_16804(double d, double e, int i) {
		if (i == 0) {
			this.method_1831(d, e);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean method_16801(double d, double e, int i, double f, double g) {
		if (i == 0) {
			this.method_1822(d, e, f, g);
			return true;
		} else {
			return false;
		}
	}

	protected boolean method_1829(double d, double e) {
		return this.field_2078
			&& this.field_2076
			&& d >= (double)this.field_2069
			&& e >= (double)this.field_2068
			&& d < (double)(this.field_2069 + this.field_2071)
			&& e < (double)(this.field_2068 + this.field_2070);
	}

	public boolean method_1828() {
		return this.field_2075;
	}

	public void method_1823(int i, int j) {
	}

	public void method_1832(class_1144 arg) {
		arg.method_4873(class_1109.method_4758(class_3417.field_15015, 1.0F));
	}

	public int method_1825() {
		return this.field_2071;
	}

	public void method_1821(int i) {
		this.field_2071 = i;
	}
}
