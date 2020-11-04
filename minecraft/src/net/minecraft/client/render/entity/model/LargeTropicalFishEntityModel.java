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
public class LargeTropicalFishEntityModel<T extends Entity> extends TintableCompositeModel<T> {
	private final ModelPart field_27524;
	private final ModelPart field_3599;

	public LargeTropicalFishEntityModel(ModelPart modelPart) {
		this.field_27524 = modelPart;
		this.field_3599 = modelPart.method_32086("tail");
	}

	public static class_5607 method_32061(class_5605 arg) {
		class_5609 lv = new class_5609();
		class_5610 lv2 = lv.method_32111();
		int i = 19;
		lv2.method_32117(
			"body", class_5606.method_32108().method_32101(0, 20).method_32098(-1.0F, -3.0F, -3.0F, 2.0F, 6.0F, 6.0F, arg), class_5603.method_32090(0.0F, 19.0F, 0.0F)
		);
		lv2.method_32117(
			"tail", class_5606.method_32108().method_32101(21, 16).method_32098(0.0F, -3.0F, 0.0F, 0.0F, 6.0F, 5.0F, arg), class_5603.method_32090(0.0F, 19.0F, 3.0F)
		);
		lv2.method_32117(
			"right_fin",
			class_5606.method_32108().method_32101(2, 16).method_32098(-2.0F, 0.0F, 0.0F, 2.0F, 2.0F, 0.0F, arg),
			class_5603.method_32091(-1.0F, 20.0F, 0.0F, 0.0F, (float) (Math.PI / 4), 0.0F)
		);
		lv2.method_32117(
			"left_fin",
			class_5606.method_32108().method_32101(2, 12).method_32098(0.0F, 0.0F, 0.0F, 2.0F, 2.0F, 0.0F, arg),
			class_5603.method_32091(1.0F, 20.0F, 0.0F, 0.0F, (float) (-Math.PI / 4), 0.0F)
		);
		lv2.method_32117(
			"top_fin",
			class_5606.method_32108().method_32101(20, 11).method_32098(0.0F, -4.0F, 0.0F, 0.0F, 4.0F, 6.0F, arg),
			class_5603.method_32090(0.0F, 16.0F, -3.0F)
		);
		lv2.method_32117(
			"bottom_fin",
			class_5606.method_32108().method_32101(20, 21).method_32098(0.0F, 0.0F, 0.0F, 0.0F, 4.0F, 6.0F, arg),
			class_5603.method_32090(0.0F, 22.0F, -3.0F)
		);
		return class_5607.method_32110(lv, 32, 32);
	}

	@Override
	public ModelPart method_32008() {
		return this.field_27524;
	}

	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
		float f = 1.0F;
		if (!entity.isTouchingWater()) {
			f = 1.5F;
		}

		this.field_3599.yaw = -f * 0.45F * MathHelper.sin(0.6F * animationProgress);
	}
}
