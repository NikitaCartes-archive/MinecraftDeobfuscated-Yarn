package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.state.BoatEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class RaftEntityModel extends EntityModel<BoatEntityRenderState> {
	private final ModelPart root;
	private final ModelPart leftPaddle;
	private final ModelPart rightPaddle;

	public RaftEntityModel(ModelPart root) {
		this.root = root;
		this.leftPaddle = root.getChild(EntityModelPartNames.LEFT_PADDLE);
		this.rightPaddle = root.getChild(EntityModelPartNames.RIGHT_PADDLE);
	}

	public static void addParts(ModelPartData modelPartData) {
		modelPartData.addChild(
			EntityModelPartNames.BOTTOM,
			ModelPartBuilder.create().uv(0, 0).cuboid(-14.0F, -11.0F, -4.0F, 28.0F, 20.0F, 4.0F).uv(0, 0).cuboid(-14.0F, -9.0F, -8.0F, 28.0F, 16.0F, 4.0F),
			ModelTransform.of(0.0F, -2.1F, 1.0F, 1.5708F, 0.0F, 0.0F)
		);
		int i = 20;
		int j = 7;
		int k = 6;
		float f = -5.0F;
		modelPartData.addChild(
			EntityModelPartNames.LEFT_PADDLE,
			ModelPartBuilder.create().uv(0, 24).cuboid(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F).cuboid(-1.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F),
			ModelTransform.of(3.0F, -4.0F, 9.0F, 0.0F, 0.0F, (float) (Math.PI / 16))
		);
		modelPartData.addChild(
			EntityModelPartNames.RIGHT_PADDLE,
			ModelPartBuilder.create().uv(40, 24).cuboid(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F).cuboid(0.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F),
			ModelTransform.of(3.0F, -4.0F, -9.0F, 0.0F, (float) Math.PI, (float) (Math.PI / 16))
		);
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		addParts(modelPartData);
		return TexturedModelData.of(modelData, 128, 64);
	}

	public static TexturedModelData getChestTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		addParts(modelPartData);
		modelPartData.addChild(
			EntityModelPartNames.CHEST_BOTTOM,
			ModelPartBuilder.create().uv(0, 76).cuboid(0.0F, 0.0F, 0.0F, 12.0F, 8.0F, 12.0F),
			ModelTransform.of(-2.0F, -10.1F, -6.0F, 0.0F, (float) (-Math.PI / 2), 0.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.CHEST_LID,
			ModelPartBuilder.create().uv(0, 59).cuboid(0.0F, 0.0F, 0.0F, 12.0F, 4.0F, 12.0F),
			ModelTransform.of(-2.0F, -14.1F, -6.0F, 0.0F, (float) (-Math.PI / 2), 0.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.CHEST_LOCK,
			ModelPartBuilder.create().uv(0, 59).cuboid(0.0F, 0.0F, 0.0F, 2.0F, 4.0F, 1.0F),
			ModelTransform.of(-1.0F, -11.1F, -1.0F, 0.0F, (float) (-Math.PI / 2), 0.0F)
		);
		return TexturedModelData.of(modelData, 128, 128);
	}

	public void setAngles(BoatEntityRenderState boatEntityRenderState) {
		setPaddleAngle(boatEntityRenderState.leftPaddleAngle, 0, this.leftPaddle);
		setPaddleAngle(boatEntityRenderState.rightPaddleAngle, 1, this.rightPaddle);
	}

	@Override
	public ModelPart getPart() {
		return this.root;
	}

	private static void setPaddleAngle(float f, int sigma, ModelPart part) {
		part.pitch = MathHelper.clampedLerp((float) (-Math.PI / 3), (float) (-Math.PI / 12), (MathHelper.sin(-f) + 1.0F) / 2.0F);
		part.yaw = MathHelper.clampedLerp((float) (-Math.PI / 4), (float) (Math.PI / 4), (MathHelper.sin(-f + 1.0F) + 1.0F) / 2.0F);
		if (sigma == 1) {
			part.yaw = (float) Math.PI - part.yaw;
		}
	}
}
