package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_908 extends class_927<class_1570, class_572<class_1570>> {
	private static final class_2960 field_4710 = new class_2960("textures/entity/zombie/zombie.png");
	private final float field_4711;

	public class_908(class_898 arg, float f) {
		super(arg, new class_3969(), 0.5F * f);
		this.field_4711 = f;
		this.method_4046(new class_989<>(this));
		this.method_4046(new class_987<>(this, new class_3969(0.5F, true), new class_3969(1.0F, true)));
	}

	protected void method_3980(class_1570 arg, float f) {
		GlStateManager.scalef(this.field_4711, this.field_4711, this.field_4711);
	}

	protected class_2960 method_3981(class_1570 arg) {
		return field_4710;
	}
}
