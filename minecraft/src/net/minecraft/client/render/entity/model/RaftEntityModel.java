package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
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
public class RaftEntityModel extends CompositeEntityModel<BoatEntity> {
	private static final String LEFT_PADDLE = "left_paddle";
	private static final String RIGHT_PADDLE = "right_paddle";
	private static final String BOTTOM = "bottom";
	private final ModelPart leftPaddle;
	private final ModelPart rightPaddle;
	private final ImmutableList<ModelPart> parts;

	public RaftEntityModel(ModelPart root) {
		this.leftPaddle = root.getChild("left_paddle");
		this.rightPaddle = root.getChild("right_paddle");
		this.parts = this.getParts(root).build();
	}

	protected Builder<ModelPart> getParts(ModelPart root) {
		Builder<ModelPart> builder = new Builder<>();
		builder.add(root.getChild("bottom"), this.leftPaddle, this.rightPaddle);
		return builder;
	}

	public static void addParts(ModelPartData modelPartData) {
		modelPartData.addChild(
			"bottom",
			ModelPartBuilder.create().uv(0, 0).cuboid(-14.0F, -11.0F, -4.0F, 28.0F, 20.0F, 4.0F).uv(0, 0).cuboid(-14.0F, -9.0F, -8.0F, 28.0F, 16.0F, 4.0F),
			ModelTransform.of(0.0F, -2.1F, 1.0F, 1.5708F, 0.0F, 0.0F)
		);
		int i = 20;
		int j = 7;
		int k = 6;
		float f = -5.0F;
		modelPartData.addChild(
			"left_paddle",
			ModelPartBuilder.create().uv(0, 24).cuboid(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F).cuboid(-1.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F),
			ModelTransform.of(3.0F, -4.0F, 9.0F, 0.0F, 0.0F, (float) (Math.PI / 16))
		);
		modelPartData.addChild(
			"right_paddle",
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

	public void setAngles(BoatEntity boatEntity, float f, float g, float h, float i, float j) {
		setPaddleAngle(boatEntity, 0, this.leftPaddle, f);
		setPaddleAngle(boatEntity, 1, this.rightPaddle, f);
	}

	public ImmutableList<ModelPart> getParts() {
		return this.parts;
	}

	private static void setPaddleAngle(BoatEntity entity, int sigma, ModelPart part, float angle) {
		float f = entity.interpolatePaddlePhase(sigma, angle);
		part.pitch = MathHelper.clampedLerp((float) (-Math.PI / 3), (float) (-Math.PI / 12), (MathHelper.sin(-f) + 1.0F) / 2.0F);
		part.yaw = MathHelper.clampedLerp((float) (-Math.PI / 4), (float) (Math.PI / 4), (MathHelper.sin(-f + 1.0F) + 1.0F) / 2.0F);
		if (sigma == 1) {
			part.yaw = (float) Math.PI - part.yaw;
		}
	}
}
