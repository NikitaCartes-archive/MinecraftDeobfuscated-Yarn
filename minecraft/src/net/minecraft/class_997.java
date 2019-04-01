package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_997<T extends class_1297> extends class_3887<T, class_609<T>> {
	private final class_583<T> field_4895 = new class_609<>(0);

	public class_997(class_3883<T, class_609<T>> arg) {
		super(arg);
	}

	@Override
	public void method_4199(T arg, float f, float g, float h, float i, float j, float k, float l) {
		if (!arg.method_5767()) {
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.enableNormalize();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			this.method_17165().method_17081(this.field_4895);
			this.field_4895.method_2819(arg, f, g, i, j, k, l);
			GlStateManager.disableBlend();
			GlStateManager.disableNormalize();
		}
	}

	@Override
	public boolean method_4200() {
		return true;
	}
}
