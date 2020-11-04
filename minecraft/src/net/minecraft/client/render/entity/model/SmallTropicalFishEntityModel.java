package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5603;
import net.minecraft.class_5605;
import net.minecraft.class_5606;
import net.minecraft.class_5607;
import net.minecraft.class_5609;
import net.minecraft.class_5610;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class SmallTropicalFishEntityModel<T extends Entity> extends TintableCompositeModel<T> {
	private final ModelPart field_27522;
	private final ModelPart field_27523;

	public SmallTropicalFishEntityModel(ModelPart modelPart) {
		this.field_27522 = modelPart;
		this.field_27523 = modelPart.method_32086("tail");
	}

	public static class_5607 method_32060(class_5605 arg) {
		class_5609 lv = new class_5609();
		class_5610 lv2 = lv.method_32111();
		int i = 22;
		lv2.method_32117(
			"body", class_5606.method_32108().method_32101(0, 0).method_32098(-1.0F, -1.5F, -3.0F, 2.0F, 3.0F, 6.0F, arg), class_5603.method_32090(0.0F, 22.0F, 0.0F)
		);
		lv2.method_32117(
			"tail", class_5606.method_32108().method_32101(22, -6).method_32098(0.0F, -1.5F, 0.0F, 0.0F, 3.0F, 6.0F, arg), class_5603.method_32090(0.0F, 22.0F, 3.0F)
		);
		lv2.method_32117(
			"right_fin",
			class_5606.method_32108().method_32101(2, 16).method_32098(-2.0F, -1.0F, 0.0F, 2.0F, 2.0F, 0.0F, arg),
			class_5603.method_32091(-1.0F, 22.5F, 0.0F, 0.0F, (float) (Math.PI / 4), 0.0F)
		);
		lv2.method_32117(
			"left_fin",
			class_5606.method_32108().method_32101(2, 12).method_32098(0.0F, -1.0F, 0.0F, 2.0F, 2.0F, 0.0F, arg),
			class_5603.method_32091(1.0F, 22.5F, 0.0F, 0.0F, (float) (-Math.PI / 4), 0.0F)
		);
		lv2.method_32117(
			"top_fin",
			class_5606.method_32108().method_32101(10, -5).method_32098(0.0F, -3.0F, 0.0F, 0.0F, 3.0F, 6.0F, arg),
			class_5603.method_32090(0.0F, 20.5F, -3.0F)
		);
		return class_5607.method_32110(lv, 32, 32);
	}

	@Override
	public ModelPart method_32008() {
		return this.field_27522;
	}

	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
		float f = 1.0F;
		if (!entity.isTouchingWater()) {
			f = 1.5F;
		}

		this.field_27523.yaw = -f * 0.45F * MathHelper.sin(0.6F * animationProgress);
	}
}
