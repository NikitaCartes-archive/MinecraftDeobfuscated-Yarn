package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.systems.RenderSystem;
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
	private final ModelPart bottom;

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
		this.body[0].addCuboid(-14.0F, -9.0F, -3.0F, 28.0F, 16.0F, 3.0F, 0.0F);
		this.body[0].setRotationPoint(0.0F, 3.0F, 1.0F);
		this.body[1].addCuboid(-13.0F, -7.0F, -1.0F, 18.0F, 6.0F, 2.0F, 0.0F);
		this.body[1].setRotationPoint(-15.0F, 4.0F, 4.0F);
		this.body[2].addCuboid(-8.0F, -7.0F, -1.0F, 16.0F, 6.0F, 2.0F, 0.0F);
		this.body[2].setRotationPoint(15.0F, 4.0F, 0.0F);
		this.body[3].addCuboid(-14.0F, -7.0F, -1.0F, 28.0F, 6.0F, 2.0F, 0.0F);
		this.body[3].setRotationPoint(0.0F, 4.0F, -9.0F);
		this.body[4].addCuboid(-14.0F, -7.0F, -1.0F, 28.0F, 6.0F, 2.0F, 0.0F);
		this.body[4].setRotationPoint(0.0F, 4.0F, 9.0F);
		this.body[0].pitch = (float) (Math.PI / 2);
		this.body[1].yaw = (float) (Math.PI * 3.0 / 2.0);
		this.body[2].yaw = (float) (Math.PI / 2);
		this.body[3].yaw = (float) Math.PI;
		this.paddles[0] = this.makePaddle(true);
		this.paddles[0].setRotationPoint(3.0F, -5.0F, 9.0F);
		this.paddles[1] = this.makePaddle(false);
		this.paddles[1].setRotationPoint(3.0F, -5.0F, -9.0F);
		this.paddles[1].yaw = (float) Math.PI;
		this.paddles[0].roll = (float) (Math.PI / 16);
		this.paddles[1].roll = (float) (Math.PI / 16);
		this.bottom = new ModelPart(this, 0, 0).setTextureSize(128, 64);
		this.bottom.addCuboid(-14.0F, -9.0F, -3.0F, 28.0F, 16.0F, 3.0F, 0.0F);
		this.bottom.setRotationPoint(0.0F, -3.0F, 1.0F);
		this.bottom.pitch = (float) (Math.PI / 2);
	}

	public void method_17071(BoatEntity boatEntity, float f, float g, float h, float i, float j, float k) {
		RenderSystem.rotatef(90.0F, 0.0F, 1.0F, 0.0F);
		this.setAngles(boatEntity, f, g, h, i, j, k);

		for (int l = 0; l < 5; l++) {
			this.body[l].render(k);
		}

		this.renderPaddle(boatEntity, 0, k, f);
		this.renderPaddle(boatEntity, 1, k, f);
	}

	public void renderPass(Entity entity, float f, float g, float h, float i, float j, float k) {
		RenderSystem.rotatef(90.0F, 0.0F, 1.0F, 0.0F);
		RenderSystem.colorMask(false, false, false, false);
		this.bottom.render(k);
		RenderSystem.colorMask(true, true, true, true);
	}

	protected ModelPart makePaddle(boolean bl) {
		ModelPart modelPart = new ModelPart(this, 62, bl ? 0 : 20).setTextureSize(128, 64);
		int i = 20;
		int j = 7;
		int k = 6;
		float f = -5.0F;
		modelPart.addCuboid(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F);
		modelPart.addCuboid(bl ? -1.001F : 0.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F);
		return modelPart;
	}

	protected void renderPaddle(BoatEntity boatEntity, int i, float f, float g) {
		float h = boatEntity.interpolatePaddlePhase(i, g);
		ModelPart modelPart = this.paddles[i];
		modelPart.pitch = (float)MathHelper.clampedLerp((float) (-Math.PI / 3), (float) (-Math.PI / 12), (double)((MathHelper.sin(-h) + 1.0F) / 2.0F));
		modelPart.yaw = (float)MathHelper.clampedLerp((float) (-Math.PI / 4), (float) (Math.PI / 4), (double)((MathHelper.sin(-h + 1.0F) + 1.0F) / 2.0F));
		if (i == 1) {
			modelPart.yaw = (float) Math.PI - modelPart.yaw;
		}

		modelPart.render(f);
	}
}
