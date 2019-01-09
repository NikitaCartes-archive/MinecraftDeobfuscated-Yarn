package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_983<T extends class_1657> extends class_3887<T, class_591<T>> {
	private final class_584 field_17154 = new class_584();

	public class_983(class_3883<T, class_591<T>> arg) {
		super(arg);
	}

	public void method_4185(T arg, float f, float g, float h, float i, float j, float k, float l) {
		GlStateManager.enableRescaleNormal();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.method_4186(arg, f, g, h, j, k, l, true);
		this.method_4186(arg, f, g, h, j, k, l, false);
		GlStateManager.disableRescaleNormal();
	}

	private void method_4186(T arg, float f, float g, float h, float i, float j, float k, boolean bl) {
		class_2487 lv = bl ? arg.method_7356() : arg.method_7308();
		if (class_1299.method_5898(lv.method_10558("id")) == class_1299.field_6104) {
			GlStateManager.pushMatrix();
			GlStateManager.translatef(bl ? 0.4F : -0.4F, arg.method_5715() ? -1.3F : -1.5F, 0.0F);
			this.method_17164(class_930.field_4754[lv.method_10550("Variant")]);
			this.field_17154.method_17106(f, g, i, j, k, arg.field_6012);
			GlStateManager.popMatrix();
		}
	}

	@Override
	public boolean method_4200() {
		return false;
	}
}
