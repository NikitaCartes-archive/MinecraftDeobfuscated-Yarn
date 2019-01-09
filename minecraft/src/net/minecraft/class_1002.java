package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_1002<T extends class_1309 & class_1603, M extends class_583<T>> extends class_3887<T, M> {
	private static final class_2960 field_4907 = new class_2960("textures/entity/skeleton/stray_overlay.png");
	private final class_606<T> field_4908 = new class_606<>(0.25F, true);

	public class_1002(class_3883<T, M> arg) {
		super(arg);
	}

	public void method_4206(T arg, float f, float g, float h, float i, float j, float k, float l) {
		this.method_17165().method_17081(this.field_4908);
		this.field_4908.method_17086(arg, f, g, h);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.method_17164(field_4907);
		this.field_4908.method_17088(arg, f, g, i, j, k, l);
	}

	@Override
	public boolean method_4200() {
		return true;
	}
}
