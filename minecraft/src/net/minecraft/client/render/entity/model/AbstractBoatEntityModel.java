package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.state.BoatEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public abstract class AbstractBoatEntityModel extends EntityModel<BoatEntityRenderState> {
	private final ModelPart leftPaddle;
	private final ModelPart rightPaddle;

	public AbstractBoatEntityModel(ModelPart modelPart) {
		super(modelPart);
		this.leftPaddle = modelPart.getChild(EntityModelPartNames.LEFT_PADDLE);
		this.rightPaddle = modelPart.getChild(EntityModelPartNames.RIGHT_PADDLE);
	}

	public void setAngles(BoatEntityRenderState boatEntityRenderState) {
		super.setAngles(boatEntityRenderState);
		setPaddleAngles(boatEntityRenderState.leftPaddleAngle, 0, this.leftPaddle);
		setPaddleAngles(boatEntityRenderState.rightPaddleAngle, 1, this.rightPaddle);
	}

	private static void setPaddleAngles(float angle, int paddle, ModelPart modelPart) {
		modelPart.pitch = MathHelper.clampedLerp((float) (-Math.PI / 3), (float) (-Math.PI / 12), (MathHelper.sin(-angle) + 1.0F) / 2.0F);
		modelPart.yaw = MathHelper.clampedLerp((float) (-Math.PI / 4), (float) (Math.PI / 4), (MathHelper.sin(-angle + 1.0F) + 1.0F) / 2.0F);
		if (paddle == 1) {
			modelPart.yaw = (float) Math.PI - modelPart.yaw;
		}
	}
}
