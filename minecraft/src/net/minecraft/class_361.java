package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_361 extends class_339 {
	protected class_2960 field_2193;
	protected boolean field_2194;
	protected int field_2192;
	protected int field_2191;
	protected int field_2190;
	protected int field_2189;

	public class_361(int i, int j, int k, int l, int m, boolean bl) {
		super(i, j, k, l, m, "");
		this.field_2194 = bl;
	}

	public void method_1962(int i, int j, int k, int l, class_2960 arg) {
		this.field_2192 = i;
		this.field_2191 = j;
		this.field_2190 = k;
		this.field_2189 = l;
		this.field_2193 = arg;
	}

	public void method_1964(boolean bl) {
		this.field_2194 = bl;
	}

	public boolean method_1965() {
		return this.field_2194;
	}

	public void method_1963(int i, int j) {
		this.field_2069 = i;
		this.field_2068 = j;
	}

	@Override
	public void method_1824(int i, int j, float f) {
		if (this.field_2076) {
			this.field_2075 = i >= this.field_2069 && j >= this.field_2068 && i < this.field_2069 + this.field_2071 && j < this.field_2068 + this.field_2070;
			class_310 lv = class_310.method_1551();
			lv.method_1531().method_4618(this.field_2193);
			GlStateManager.disableDepthTest();
			int k = this.field_2192;
			int l = this.field_2191;
			if (this.field_2194) {
				k += this.field_2190;
			}

			if (this.field_2075) {
				l += this.field_2189;
			}

			this.method_1788(this.field_2069, this.field_2068, k, l, this.field_2071, this.field_2070);
			GlStateManager.enableDepthTest();
		}
	}
}
