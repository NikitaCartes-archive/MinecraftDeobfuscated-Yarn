package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5597;
import net.minecraft.class_5603;
import net.minecraft.class_5606;
import net.minecraft.class_5607;
import net.minecraft.class_5609;
import net.minecraft.class_5610;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class SalmonEntityModel<T extends Entity> extends class_5597<T> {
	private final ModelPart field_27494;
	private final ModelPart tail;

	public SalmonEntityModel(ModelPart modelPart) {
		this.field_27494 = modelPart;
		this.tail = modelPart.method_32086("body_back");
	}

	public static class_5607 method_32036() {
		class_5609 lv = new class_5609();
		class_5610 lv2 = lv.method_32111();
		int i = 20;
		class_5610 lv3 = lv2.method_32117(
			"body_front", class_5606.method_32108().method_32101(0, 0).method_32097(-1.5F, -2.5F, 0.0F, 3.0F, 5.0F, 8.0F), class_5603.method_32090(0.0F, 20.0F, 0.0F)
		);
		class_5610 lv4 = lv2.method_32117(
			"body_back", class_5606.method_32108().method_32101(0, 13).method_32097(-1.5F, -2.5F, 0.0F, 3.0F, 5.0F, 8.0F), class_5603.method_32090(0.0F, 20.0F, 8.0F)
		);
		lv2.method_32117(
			"head", class_5606.method_32108().method_32101(22, 0).method_32097(-1.0F, -2.0F, -3.0F, 2.0F, 4.0F, 3.0F), class_5603.method_32090(0.0F, 20.0F, 0.0F)
		);
		lv4.method_32117(
			"back_fin", class_5606.method_32108().method_32101(20, 10).method_32097(0.0F, -2.5F, 0.0F, 0.0F, 5.0F, 6.0F), class_5603.method_32090(0.0F, 0.0F, 8.0F)
		);
		lv3.method_32117(
			"top_front_fin", class_5606.method_32108().method_32101(2, 1).method_32097(0.0F, 0.0F, 0.0F, 0.0F, 2.0F, 3.0F), class_5603.method_32090(0.0F, -4.5F, 5.0F)
		);
		lv4.method_32117(
			"top_back_fin", class_5606.method_32108().method_32101(0, 2).method_32097(0.0F, 0.0F, 0.0F, 0.0F, 2.0F, 4.0F), class_5603.method_32090(0.0F, -4.5F, -1.0F)
		);
		lv2.method_32117(
			"right_fin",
			class_5606.method_32108().method_32101(-4, 0).method_32097(-2.0F, 0.0F, 0.0F, 2.0F, 0.0F, 2.0F),
			class_5603.method_32091(-1.5F, 21.5F, 0.0F, 0.0F, 0.0F, (float) (-Math.PI / 4))
		);
		lv2.method_32117(
			"left_fin",
			class_5606.method_32108().method_32101(0, 0).method_32097(0.0F, 0.0F, 0.0F, 2.0F, 0.0F, 2.0F),
			class_5603.method_32091(1.5F, 21.5F, 0.0F, 0.0F, 0.0F, (float) (Math.PI / 4))
		);
		return class_5607.method_32110(lv, 32, 32);
	}

	@Override
	public ModelPart method_32008() {
		return this.field_27494;
	}

	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
		float f = 1.0F;
		float g = 1.0F;
		if (!entity.isTouchingWater()) {
			f = 1.3F;
			g = 1.7F;
		}

		this.tail.yaw = -f * 0.25F * MathHelper.sin(g * 0.6F * animationProgress);
	}
}
