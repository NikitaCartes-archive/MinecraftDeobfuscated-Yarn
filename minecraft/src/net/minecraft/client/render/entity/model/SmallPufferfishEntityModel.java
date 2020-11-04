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
public class SmallPufferfishEntityModel<T extends Entity> extends class_5597<T> {
	private final ModelPart field_27473;
	private final ModelPart field_27474;
	private final ModelPart field_27475;

	public SmallPufferfishEntityModel(ModelPart modelPart) {
		this.field_27473 = modelPart;
		this.field_27474 = modelPart.method_32086("left_fin");
		this.field_27475 = modelPart.method_32086("right_fin");
	}

	public static class_5607 method_32032() {
		class_5609 lv = new class_5609();
		class_5610 lv2 = lv.method_32111();
		int i = 23;
		lv2.method_32117(
			"body", class_5606.method_32108().method_32101(0, 27).method_32097(-1.5F, -2.0F, -1.5F, 3.0F, 2.0F, 3.0F), class_5603.method_32090(0.0F, 23.0F, 0.0F)
		);
		lv2.method_32117(
			"right_eye", class_5606.method_32108().method_32101(24, 6).method_32097(-1.5F, 0.0F, -1.5F, 1.0F, 1.0F, 1.0F), class_5603.method_32090(0.0F, 20.0F, 0.0F)
		);
		lv2.method_32117(
			"left_eye", class_5606.method_32108().method_32101(28, 6).method_32097(0.5F, 0.0F, -1.5F, 1.0F, 1.0F, 1.0F), class_5603.method_32090(0.0F, 20.0F, 0.0F)
		);
		lv2.method_32117(
			"back_fin", class_5606.method_32108().method_32101(-3, 0).method_32097(-1.5F, 0.0F, 0.0F, 3.0F, 0.0F, 3.0F), class_5603.method_32090(0.0F, 22.0F, 1.5F)
		);
		lv2.method_32117(
			"right_fin", class_5606.method_32108().method_32101(25, 0).method_32097(-1.0F, 0.0F, 0.0F, 1.0F, 0.0F, 2.0F), class_5603.method_32090(-1.5F, 22.0F, -1.5F)
		);
		lv2.method_32117(
			"left_fin", class_5606.method_32108().method_32101(25, 0).method_32097(0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 2.0F), class_5603.method_32090(1.5F, 22.0F, -1.5F)
		);
		return class_5607.method_32110(lv, 32, 32);
	}

	@Override
	public ModelPart method_32008() {
		return this.field_27473;
	}

	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
		this.field_27475.roll = -0.2F + 0.4F * MathHelper.sin(animationProgress * 0.2F);
		this.field_27474.roll = 0.2F - 0.4F * MathHelper.sin(animationProgress * 0.2F);
	}
}
