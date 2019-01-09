package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class class_346 extends class_339 {
	public class_346(int i, int j, int k) {
		super(i, j, k, 20, 20, "");
	}

	@Override
	public void method_1824(int i, int j, float f) {
		if (this.field_2076) {
			class_310.method_1551().method_1531().method_4618(class_339.field_2072);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			boolean bl = i >= this.field_2069 && j >= this.field_2068 && i < this.field_2069 + this.field_2071 && j < this.field_2068 + this.field_2070;
			int k = 106;
			if (bl) {
				k += this.field_2070;
			}

			this.method_1788(this.field_2069, this.field_2068, 0, k, this.field_2071, this.field_2070);
		}
	}
}
