package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_344 extends class_4185 {
	private final class_2960 field_2127;
	private final int field_2126;
	private final int field_2125;
	private final int field_19079;
	private final int field_2124;
	private final int field_19080;

	public class_344(int i, int j, int k, int l, int m, int n, int o, class_2960 arg, class_4185.class_4241 arg2) {
		this(i, j, k, l, m, n, o, arg, 256, 256, arg2);
	}

	public class_344(int i, int j, int k, int l, int m, int n, int o, class_2960 arg, int p, int q, class_4185.class_4241 arg2) {
		this(i, j, k, l, m, n, o, arg, p, q, arg2, "");
	}

	public class_344(int i, int j, int k, int l, int m, int n, int o, class_2960 arg, int p, int q, class_4185.class_4241 arg2, String string) {
		super(i, j, k, l, string, arg2);
		this.field_2124 = p;
		this.field_19080 = q;
		this.field_2126 = m;
		this.field_2125 = n;
		this.field_19079 = o;
		this.field_2127 = arg;
	}

	public void method_1893(int i, int j) {
		this.field_23658 = i;
		this.field_23659 = j;
	}

	@Override
	public void renderButton(int i, int j, float f) {
		class_310 lv = class_310.method_1551();
		lv.method_1531().method_4618(this.field_2127);
		GlStateManager.disableDepthTest();
		int k = this.field_2125;
		if (this.isHovered()) {
			k += this.field_19079;
		}

		blit(this.field_23658, this.field_23659, (float)this.field_2126, (float)k, this.width, this.height, this.field_2124, this.field_19080);
		GlStateManager.enableDepthTest();
	}
}
