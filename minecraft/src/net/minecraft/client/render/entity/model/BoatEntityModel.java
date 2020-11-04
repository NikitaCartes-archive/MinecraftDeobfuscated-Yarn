package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5603;
import net.minecraft.class_5606;
import net.minecraft.class_5607;
import net.minecraft.class_5609;
import net.minecraft.class_5610;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class BoatEntityModel extends CompositeEntityModel<BoatEntity> {
	private final ModelPart field_27396;
	private final ModelPart field_27397;
	private final ModelPart bottom;
	private final ImmutableList<ModelPart> parts;

	public BoatEntityModel(ModelPart modelPart) {
		this.field_27396 = modelPart.method_32086("left_paddle");
		this.field_27397 = modelPart.method_32086("right_paddle");
		this.bottom = modelPart.method_32086("water_patch");
		this.parts = ImmutableList.of(
			modelPart.method_32086("bottom"),
			modelPart.method_32086("back"),
			modelPart.method_32086("front"),
			modelPart.method_32086("right"),
			modelPart.method_32086("left"),
			this.field_27396,
			this.field_27397
		);
	}

	public static class_5607 method_31985() {
		class_5609 lv = new class_5609();
		class_5610 lv2 = lv.method_32111();
		int i = 32;
		int j = 6;
		int k = 20;
		int l = 4;
		int m = 28;
		lv2.method_32117(
			"bottom",
			class_5606.method_32108().method_32101(0, 0).method_32097(-14.0F, -9.0F, -3.0F, 28.0F, 16.0F, 3.0F),
			class_5603.method_32091(0.0F, 3.0F, 1.0F, (float) (Math.PI / 2), 0.0F, 0.0F)
		);
		lv2.method_32117(
			"back",
			class_5606.method_32108().method_32101(0, 19).method_32097(-13.0F, -7.0F, -1.0F, 18.0F, 6.0F, 2.0F),
			class_5603.method_32091(-15.0F, 4.0F, 4.0F, 0.0F, (float) (Math.PI * 3.0 / 2.0), 0.0F)
		);
		lv2.method_32117(
			"front",
			class_5606.method_32108().method_32101(0, 27).method_32097(-8.0F, -7.0F, -1.0F, 16.0F, 6.0F, 2.0F),
			class_5603.method_32091(15.0F, 4.0F, 0.0F, 0.0F, (float) (Math.PI / 2), 0.0F)
		);
		lv2.method_32117(
			"right",
			class_5606.method_32108().method_32101(0, 35).method_32097(-14.0F, -7.0F, -1.0F, 28.0F, 6.0F, 2.0F),
			class_5603.method_32091(0.0F, 4.0F, -9.0F, 0.0F, (float) Math.PI, 0.0F)
		);
		lv2.method_32117(
			"left", class_5606.method_32108().method_32101(0, 43).method_32097(-14.0F, -7.0F, -1.0F, 28.0F, 6.0F, 2.0F), class_5603.method_32090(0.0F, 4.0F, 9.0F)
		);
		int n = 20;
		int o = 7;
		int p = 6;
		float f = -5.0F;
		lv2.method_32117(
			"left_paddle",
			class_5606.method_32108().method_32101(62, 0).method_32097(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F).method_32097(-1.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F),
			class_5603.method_32091(3.0F, -5.0F, 9.0F, 0.0F, 0.0F, (float) (Math.PI / 16))
		);
		lv2.method_32117(
			"right_paddle",
			class_5606.method_32108().method_32101(62, 20).method_32097(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F).method_32097(0.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F),
			class_5603.method_32091(3.0F, -5.0F, -9.0F, 0.0F, (float) Math.PI, (float) (Math.PI / 16))
		);
		lv2.method_32117(
			"water_patch",
			class_5606.method_32108().method_32101(0, 0).method_32097(-14.0F, -9.0F, -3.0F, 28.0F, 16.0F, 3.0F),
			class_5603.method_32091(0.0F, -3.0F, 1.0F, (float) (Math.PI / 2), 0.0F, 0.0F)
		);
		return class_5607.method_32110(lv, 128, 64);
	}

	public void setAngles(BoatEntity boatEntity, float f, float g, float h, float i, float j) {
		setPaddleAngle(boatEntity, 0, this.field_27396, f);
		setPaddleAngle(boatEntity, 1, this.field_27397, f);
	}

	public ImmutableList<ModelPart> getParts() {
		return this.parts;
	}

	public ModelPart getBottom() {
		return this.bottom;
	}

	private static void setPaddleAngle(BoatEntity boatEntity, int i, ModelPart modelPart, float angle) {
		float f = boatEntity.interpolatePaddlePhase(i, angle);
		modelPart.pitch = (float)MathHelper.clampedLerp((float) (-Math.PI / 3), (float) (-Math.PI / 12), (double)((MathHelper.sin(-f) + 1.0F) / 2.0F));
		modelPart.yaw = (float)MathHelper.clampedLerp((float) (-Math.PI / 4), (float) (Math.PI / 4), (double)((MathHelper.sin(-f + 1.0F) + 1.0F) / 2.0F));
		if (i == 1) {
			modelPart.yaw = (float) Math.PI - modelPart.yaw;
		}
	}
}
