package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class class_344 extends class_339 {
	private final class_2960 field_2127;
	private final int field_2126;
	private final int field_2125;
	private final int field_2124;

	public class_344(int i, int j, int k, int l, int m, int n, int o, int p, class_2960 arg) {
		super(i, j, k, l, m, "");
		this.field_2126 = n;
		this.field_2125 = o;
		this.field_2124 = p;
		this.field_2127 = arg;
	}

	public void method_1893(int i, int j) {
		this.field_2069 = i;
		this.field_2068 = j;
	}

	@Override
	public void method_1824(int i, int j, float f) {
		if (this.field_2076) {
			this.field_2075 = i >= this.field_2069 && j >= this.field_2068 && i < this.field_2069 + this.field_2071 && j < this.field_2068 + this.field_2070;
			class_310 lv = class_310.method_1551();
			lv.method_1531().method_4618(this.field_2127);
			GlStateManager.disableDepthTest();
			int k = this.field_2125;
			if (this.field_2075) {
				k += this.field_2124;
			}

			this.method_1788(this.field_2069, this.field_2068, this.field_2126, k, this.field_2071, this.field_2070);
			GlStateManager.enableDepthTest();
		}
	}
}
