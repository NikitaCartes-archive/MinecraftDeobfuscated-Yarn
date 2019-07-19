package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class BoatEntityModel extends EntityModel<BoatEntity> {
	private final ModelPart[] body = new ModelPart[5];
	private final ModelPart[] paddles = new ModelPart[2];
	private final ModelPart field_3326;

	public BoatEntityModel() {
		this.body[0] = new ModelPart(this, 0, 0).setTextureSize(128, 64);
		this.body[1] = new ModelPart(this, 0, 19).setTextureSize(128, 64);
		this.body[2] = new ModelPart(this, 0, 27).setTextureSize(128, 64);
		this.body[3] = new ModelPart(this, 0, 35).setTextureSize(128, 64);
		this.body[4] = new ModelPart(this, 0, 43).setTextureSize(128, 64);
		int i = 32;
		int j = 6;
		int k = 20;
		int l = 4;
		int m = 28;
		this.body[0].addCuboid(-14.0F, -9.0F, -3.0F, 28, 16, 3, 0.0F);
		this.body[0].setPivot(0.0F, 3.0F, 1.0F);
		this.body[1].addCuboid(-13.0F, -7.0F, -1.0F, 18, 6, 2, 0.0F);
		this.body[1].setPivot(-15.0F, 4.0F, 4.0F);
		this.body[2].addCuboid(-8.0F, -7.0F, -1.0F, 16, 6, 2, 0.0F);
		this.body[2].setPivot(15.0F, 4.0F, 0.0F);
		this.body[3].addCuboid(-14.0F, -7.0F, -1.0F, 28, 6, 2, 0.0F);
		this.body[3].setPivot(0.0F, 4.0F, -9.0F);
		this.body[4].addCuboid(-14.0F, -7.0F, -1.0F, 28, 6, 2, 0.0F);
		this.body[4].setPivot(0.0F, 4.0F, 9.0F);
		this.body[0].pitch = (float) (Math.PI / 2);
		this.body[1].yaw = (float) (Math.PI * 3.0 / 2.0);
		this.body[2].yaw = (float) (Math.PI / 2);
		this.body[3].yaw = (float) Math.PI;
		this.paddles[0] = this.makePaddle(true);
		this.paddles[0].setPivot(3.0F, -5.0F, 9.0F);
		this.paddles[1] = this.makePaddle(false);
		this.paddles[1].setPivot(3.0F, -5.0F, -9.0F);
		this.paddles[1].yaw = (float) Math.PI;
		this.paddles[0].roll = (float) (Math.PI / 16);
		this.paddles[1].roll = (float) (Math.PI / 16);
		this.field_3326 = new ModelPart(this, 0, 0).setTextureSize(128, 64);
		this.field_3326.addCuboid(-14.0F, -9.0F, -3.0F, 28, 16, 3, 0.0F);
		this.field_3326.setPivot(0.0F, -3.0F, 1.0F);
		this.field_3326.pitch = (float) (Math.PI / 2);
	}

	public void render(BoatEntity boatEntity, float f, float g, float h, float i, float j, float k) {
		GlStateManager.rotatef(90.0F, 0.0F, 1.0F, 0.0F);
		this.setAngles(boatEntity, f, g, h, i, j, k);

		for (int l = 0; l < 5; l++) {
			this.body[l].render(k);
		}

		this.renderPaddle(boatEntity, 0, k, f);
		this.renderPaddle(boatEntity, 1, k, f);
	}

	public void renderPass(Entity entity, float tickDelta, float f, float g, float h, float i, float j) {
		GlStateManager.rotatef(90.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.colorMask(false, false, false, false);
		this.field_3326.render(j);
		GlStateManager.colorMask(true, true, true, true);
	}

	protected ModelPart makePaddle(boolean isLeft) {
		ModelPart modelPart = new ModelPart(this, 62, isLeft ? 0 : 20).setTextureSize(128, 64);
		int i = 20;
		int j = 7;
		int k = 6;
		float f = -5.0F;
		modelPart.addCuboid(-1.0F, 0.0F, -5.0F, 2, 2, 18);
		modelPart.addCuboid(isLeft ? -1.001F : 0.001F, -3.0F, 8.0F, 1, 6, 7);
		return modelPart;
	}

	protected void renderPaddle(BoatEntity boat, int paddle, float scale, float partialTick) {
		float f = boat.interpolatePaddlePhase(paddle, partialTick);
		ModelPart modelPart = this.paddles[paddle];
		modelPart.pitch = (float)MathHelper.clampedLerp((float) (-Math.PI / 3), (float) (-Math.PI / 12), (double)((MathHelper.sin(-f) + 1.0F) / 2.0F));
		modelPart.yaw = (float)MathHelper.clampedLerp((float) (-Math.PI / 4), (float) (Math.PI / 4), (double)((MathHelper.sin(-f + 1.0F) + 1.0F) / 2.0F));
		if (paddle == 1) {
			modelPart.yaw = (float) Math.PI - modelPart.yaw;
		}

		modelPart.render(scale);
	}
}
