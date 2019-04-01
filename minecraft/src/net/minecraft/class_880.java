package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_880 extends class_949<class_1549> {
	private static final class_2960 field_4646 = new class_2960("textures/entity/spider/cave_spider.png");

	public class_880(class_898 arg) {
		super(arg);
		this.field_4673 *= 0.7F;
	}

	protected void method_3886(class_1549 arg, float f) {
		GlStateManager.scalef(0.7F, 0.7F, 0.7F);
	}

	protected class_2960 method_3885(class_1549 arg) {
		return field_4646;
	}
}
