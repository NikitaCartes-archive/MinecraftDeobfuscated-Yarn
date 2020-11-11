package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class BoatEntityModel extends CompositeEntityModel<BoatEntity> {
	private final ModelPart field_27396;
	private final ModelPart field_27397;
	private final ModelPart bottom;
	private final ImmutableList<ModelPart> parts;

	public BoatEntityModel(ModelPart modelPart) {
		this.field_27396 = modelPart.getChild("left_paddle");
		this.field_27397 = modelPart.getChild("right_paddle");
		this.bottom = modelPart.getChild("water_patch");
		this.parts = ImmutableList.of(
			modelPart.getChild("bottom"),
			modelPart.getChild("back"),
			modelPart.getChild("front"),
			modelPart.getChild("right"),
			modelPart.getChild("left"),
			this.field_27396,
			this.field_27397
		);
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		int i = 32;
		int j = 6;
		int k = 20;
		int l = 4;
		int m = 28;
		modelPartData.addChild(
			"bottom",
			ModelPartBuilder.create().uv(0, 0).cuboid(-14.0F, -9.0F, -3.0F, 28.0F, 16.0F, 3.0F),
			ModelTransform.of(0.0F, 3.0F, 1.0F, (float) (Math.PI / 2), 0.0F, 0.0F)
		);
		modelPartData.addChild(
			"back",
			ModelPartBuilder.create().uv(0, 19).cuboid(-13.0F, -7.0F, -1.0F, 18.0F, 6.0F, 2.0F),
			ModelTransform.of(-15.0F, 4.0F, 4.0F, 0.0F, (float) (Math.PI * 3.0 / 2.0), 0.0F)
		);
		modelPartData.addChild(
			"front",
			ModelPartBuilder.create().uv(0, 27).cuboid(-8.0F, -7.0F, -1.0F, 16.0F, 6.0F, 2.0F),
			ModelTransform.of(15.0F, 4.0F, 0.0F, 0.0F, (float) (Math.PI / 2), 0.0F)
		);
		modelPartData.addChild(
			"right",
			ModelPartBuilder.create().uv(0, 35).cuboid(-14.0F, -7.0F, -1.0F, 28.0F, 6.0F, 2.0F),
			ModelTransform.of(0.0F, 4.0F, -9.0F, 0.0F, (float) Math.PI, 0.0F)
		);
		modelPartData.addChild("left", ModelPartBuilder.create().uv(0, 43).cuboid(-14.0F, -7.0F, -1.0F, 28.0F, 6.0F, 2.0F), ModelTransform.pivot(0.0F, 4.0F, 9.0F));
		int n = 20;
		int o = 7;
		int p = 6;
		float f = -5.0F;
		modelPartData.addChild(
			"left_paddle",
			ModelPartBuilder.create().uv(62, 0).cuboid(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F).cuboid(-1.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F),
			ModelTransform.of(3.0F, -5.0F, 9.0F, 0.0F, 0.0F, (float) (Math.PI / 16))
		);
		modelPartData.addChild(
			"right_paddle",
			ModelPartBuilder.create().uv(62, 20).cuboid(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F).cuboid(0.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F),
			ModelTransform.of(3.0F, -5.0F, -9.0F, 0.0F, (float) Math.PI, (float) (Math.PI / 16))
		);
		modelPartData.addChild(
			"water_patch",
			ModelPartBuilder.create().uv(0, 0).cuboid(-14.0F, -9.0F, -3.0F, 28.0F, 16.0F, 3.0F),
			ModelTransform.of(0.0F, -3.0F, 1.0F, (float) (Math.PI / 2), 0.0F, 0.0F)
		);
		return TexturedModelData.of(modelData, 128, 64);
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
