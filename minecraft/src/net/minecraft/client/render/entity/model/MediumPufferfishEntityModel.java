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
public class MediumPufferfishEntityModel<T extends Entity> extends class_5597<T> {
	private final ModelPart field_27470;
	private final ModelPart field_27471;
	private final ModelPart field_27472;

	public MediumPufferfishEntityModel(ModelPart modelPart) {
		this.field_27470 = modelPart;
		this.field_27471 = modelPart.method_32086("left_blue_fin");
		this.field_27472 = modelPart.method_32086("right_blue_fin");
	}

	public static class_5607 method_32031() {
		class_5609 lv = new class_5609();
		class_5610 lv2 = lv.method_32111();
		int i = 22;
		lv2.method_32117(
			"body", class_5606.method_32108().method_32101(12, 22).method_32097(-2.5F, -5.0F, -2.5F, 5.0F, 5.0F, 5.0F), class_5603.method_32090(0.0F, 22.0F, 0.0F)
		);
		lv2.method_32117(
			"right_blue_fin",
			class_5606.method_32108().method_32101(24, 0).method_32097(-2.0F, 0.0F, 0.0F, 2.0F, 0.0F, 2.0F),
			class_5603.method_32090(-2.5F, 17.0F, -1.5F)
		);
		lv2.method_32117(
			"left_blue_fin", class_5606.method_32108().method_32101(24, 3).method_32097(0.0F, 0.0F, 0.0F, 2.0F, 0.0F, 2.0F), class_5603.method_32090(2.5F, 17.0F, -1.5F)
		);
		lv2.method_32117(
			"top_front_fin",
			class_5606.method_32108().method_32101(15, 16).method_32097(-2.5F, -1.0F, 0.0F, 5.0F, 1.0F, 1.0F),
			class_5603.method_32091(0.0F, 17.0F, -2.5F, (float) (Math.PI / 4), 0.0F, 0.0F)
		);
		lv2.method_32117(
			"top_back_fin",
			class_5606.method_32108().method_32101(10, 16).method_32097(-2.5F, -1.0F, -1.0F, 5.0F, 1.0F, 1.0F),
			class_5603.method_32091(0.0F, 17.0F, 2.5F, (float) (-Math.PI / 4), 0.0F, 0.0F)
		);
		lv2.method_32117(
			"right_front_fin",
			class_5606.method_32108().method_32101(8, 16).method_32097(-1.0F, -5.0F, 0.0F, 1.0F, 5.0F, 1.0F),
			class_5603.method_32091(-2.5F, 22.0F, -2.5F, 0.0F, (float) (-Math.PI / 4), 0.0F)
		);
		lv2.method_32117(
			"right_back_fin",
			class_5606.method_32108().method_32101(8, 16).method_32097(-1.0F, -5.0F, 0.0F, 1.0F, 5.0F, 1.0F),
			class_5603.method_32091(-2.5F, 22.0F, 2.5F, 0.0F, (float) (Math.PI / 4), 0.0F)
		);
		lv2.method_32117(
			"left_back_fin",
			class_5606.method_32108().method_32101(4, 16).method_32097(0.0F, -5.0F, 0.0F, 1.0F, 5.0F, 1.0F),
			class_5603.method_32091(2.5F, 22.0F, 2.5F, 0.0F, (float) (-Math.PI / 4), 0.0F)
		);
		lv2.method_32117(
			"left_front_fin",
			class_5606.method_32108().method_32101(0, 16).method_32097(0.0F, -5.0F, 0.0F, 1.0F, 5.0F, 1.0F),
			class_5603.method_32091(2.5F, 22.0F, -2.5F, 0.0F, (float) (Math.PI / 4), 0.0F)
		);
		lv2.method_32117(
			"bottom_back_fin",
			class_5606.method_32108().method_32101(8, 22).method_32097(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F),
			class_5603.method_32091(0.5F, 22.0F, 2.5F, (float) (Math.PI / 4), 0.0F, 0.0F)
		);
		lv2.method_32117(
			"bottom_front_fin",
			class_5606.method_32108().method_32101(17, 21).method_32097(-2.5F, 0.0F, 0.0F, 5.0F, 1.0F, 1.0F),
			class_5603.method_32091(0.0F, 22.0F, -2.5F, (float) (-Math.PI / 4), 0.0F, 0.0F)
		);
		return class_5607.method_32110(lv, 32, 32);
	}

	@Override
	public ModelPart method_32008() {
		return this.field_27470;
	}

	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
		this.field_27472.roll = -0.2F + 0.4F * MathHelper.sin(animationProgress * 0.2F);
		this.field_27471.roll = 0.2F - 0.4F * MathHelper.sin(animationProgress * 0.2F);
	}
}
