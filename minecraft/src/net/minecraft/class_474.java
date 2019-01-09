package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
abstract class class_474 extends class_339 {
	private final boolean field_2851;

	public class_474(int i, int j, int k, boolean bl) {
		super(i, j, k, 23, 13, "");
		this.field_2851 = bl;
	}

	@Override
	public void method_1824(int i, int j, float f) {
		if (this.field_2076) {
			boolean bl = i >= this.field_2069 && j >= this.field_2068 && i < this.field_2069 + this.field_2071 && j < this.field_2068 + this.field_2070;
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			class_310.method_1551().method_1531().method_4618(class_3872.field_17117);
			int k = 0;
			int l = 192;
			if (bl) {
				k += 23;
			}

			if (!this.field_2851) {
				l += 13;
			}

			this.method_1788(this.field_2069, this.field_2068, k, l, 23, 13);
		}
	}
}
