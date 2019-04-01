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

	public class_361(int i, int j, int k, int l, boolean bl) {
		super(i, j, k, l, "");
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
		this.x = i;
		this.y = j;
	}

	@Override
	public void renderButton(int i, int j, float f) {
		class_310 lv = class_310.method_1551();
		lv.method_1531().method_4618(this.field_2193);
		GlStateManager.disableDepthTest();
		int k = this.field_2192;
		int l = this.field_2191;
		if (this.field_2194) {
			k += this.field_2190;
		}

		if (this.isHovered()) {
			l += this.field_2189;
		}

		this.blit(this.x, this.y, k, l, this.width, this.height);
		GlStateManager.enableDepthTest();
	}
}
