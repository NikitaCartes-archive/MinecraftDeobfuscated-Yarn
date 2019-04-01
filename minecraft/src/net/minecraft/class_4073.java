package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_4073 extends class_3887<class_1498, class_549<class_1498>> {
	private final class_549<class_1498> field_18228 = new class_549<>(0.1F);

	public class_4073(class_3883<class_1498, class_549<class_1498>> arg) {
		super(arg);
	}

	public void method_18658(class_1498 arg, float f, float g, float h, float i, float j, float k, float l) {
		class_1799 lv = arg.method_6786();
		if (lv.method_7909() instanceof class_4059) {
			class_4059 lv2 = (class_4059)lv.method_7909();
			this.method_17165().method_17081(this.field_18228);
			this.field_18228.method_17084(arg, f, g, h);
			this.method_17164(lv2.method_18454());
			if (lv2 instanceof class_4058) {
				int m = ((class_4058)lv2).method_7800(lv);
				float n = (float)(m >> 16 & 0xFF) / 255.0F;
				float o = (float)(m >> 8 & 0xFF) / 255.0F;
				float p = (float)(m & 0xFF) / 255.0F;
				GlStateManager.color4f(n, o, p, 1.0F);
				this.field_18228.method_17085(arg, f, g, i, j, k, l);
				return;
			}

			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.field_18228.method_17085(arg, f, g, i, j, k, l);
		}
	}

	@Override
	public boolean method_4200() {
		return false;
	}
}
