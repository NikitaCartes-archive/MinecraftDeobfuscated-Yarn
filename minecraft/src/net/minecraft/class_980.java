package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_980<T extends class_1642> extends class_3887<T, class_564<T>> {
	private static final class_2960 field_4854 = new class_2960("textures/entity/zombie/drowned_outer_layer.png");
	private final class_564<T> field_4855 = new class_564<>(0.25F, 0.0F, 64, 64);

	public class_980(class_3883<T, class_564<T>> arg) {
		super(arg);
	}

	public void method_4182(T arg, float f, float g, float h, float i, float j, float k, float l) {
		if (!arg.method_5767()) {
			this.method_17165().method_2818(this.field_4855);
			this.field_4855.method_17077(arg, f, g, h);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.method_17164(field_4854);
			this.field_4855.method_17088(arg, f, g, i, j, k, l);
		}
	}

	@Override
	public boolean method_4200() {
		return true;
	}
}
