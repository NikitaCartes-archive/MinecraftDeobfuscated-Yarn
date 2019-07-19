package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class TurtleEntityModel<T extends TurtleEntity> extends QuadrupedEntityModel<T> {
	private final ModelPart field_3594;

	public TurtleEntityModel(float f) {
		super(12, f);
		this.textureWidth = 128;
		this.textureHeight = 64;
		this.head = new ModelPart(this, 3, 0);
		this.head.addCuboid(-3.0F, -1.0F, -3.0F, 6, 5, 6, 0.0F);
		this.head.setPivot(0.0F, 19.0F, -10.0F);
		this.torso = new ModelPart(this);
		this.torso.setTextureOffset(7, 37).addCuboid(-9.5F, 3.0F, -10.0F, 19, 20, 6, 0.0F);
		this.torso.setTextureOffset(31, 1).addCuboid(-5.5F, 3.0F, -13.0F, 11, 18, 3, 0.0F);
		this.torso.setPivot(0.0F, 11.0F, -10.0F);
		this.field_3594 = new ModelPart(this);
		this.field_3594.setTextureOffset(70, 33).addCuboid(-4.5F, 3.0F, -14.0F, 9, 18, 1, 0.0F);
		this.field_3594.setPivot(0.0F, 11.0F, -10.0F);
		int i = 1;
		this.backRightLeg = new ModelPart(this, 1, 23);
		this.backRightLeg.addCuboid(-2.0F, 0.0F, 0.0F, 4, 1, 10, 0.0F);
		this.backRightLeg.setPivot(-3.5F, 22.0F, 11.0F);
		this.backLeftLeg = new ModelPart(this, 1, 12);
		this.backLeftLeg.addCuboid(-2.0F, 0.0F, 0.0F, 4, 1, 10, 0.0F);
		this.backLeftLeg.setPivot(3.5F, 22.0F, 11.0F);
		this.frontRightLeg = new ModelPart(this, 27, 30);
		this.frontRightLeg.addCuboid(-13.0F, 0.0F, -2.0F, 13, 1, 5, 0.0F);
		this.frontRightLeg.setPivot(-5.0F, 21.0F, -4.0F);
		this.frontLeftLeg = new ModelPart(this, 27, 24);
		this.frontLeftLeg.addCuboid(0.0F, 0.0F, -2.0F, 13, 1, 5, 0.0F);
		this.frontLeftLeg.setPivot(5.0F, 21.0F, -4.0F);
	}

	public void render(T turtleEntity, float f, float g, float h, float i, float j, float k) {
		this.setAngles(turtleEntity, f, g, h, i, j, k);
		if (this.child) {
			float l = 6.0F;
			GlStateManager.pushMatrix();
			GlStateManager.scalef(0.16666667F, 0.16666667F, 0.16666667F);
			GlStateManager.translatef(0.0F, 120.0F * k, 0.0F);
			this.head.render(k);
			this.torso.render(k);
			this.backRightLeg.render(k);
			this.backLeftLeg.render(k);
			this.frontRightLeg.render(k);
			this.frontLeftLeg.render(k);
			GlStateManager.popMatrix();
		} else {
			GlStateManager.pushMatrix();
			if (turtleEntity.hasEgg()) {
				GlStateManager.translatef(0.0F, -0.08F, 0.0F);
			}

			this.head.render(k);
			this.torso.render(k);
			GlStateManager.pushMatrix();
			this.backRightLeg.render(k);
			this.backLeftLeg.render(k);
			GlStateManager.popMatrix();
			this.frontRightLeg.render(k);
			this.frontLeftLeg.render(k);
			if (turtleEntity.hasEgg()) {
				this.field_3594.render(k);
			}

			GlStateManager.popMatrix();
		}
	}

	public void setAngles(T turtleEntity, float f, float g, float h, float i, float j, float k) {
		super.setAngles(turtleEntity, f, g, h, i, j, k);
		this.backRightLeg.pitch = MathHelper.cos(f * 0.6662F * 0.6F) * 0.5F * g;
		this.backLeftLeg.pitch = MathHelper.cos(f * 0.6662F * 0.6F + (float) Math.PI) * 0.5F * g;
		this.frontRightLeg.roll = MathHelper.cos(f * 0.6662F * 0.6F + (float) Math.PI) * 0.5F * g;
		this.frontLeftLeg.roll = MathHelper.cos(f * 0.6662F * 0.6F) * 0.5F * g;
		this.frontRightLeg.pitch = 0.0F;
		this.frontLeftLeg.pitch = 0.0F;
		this.frontRightLeg.yaw = 0.0F;
		this.frontLeftLeg.yaw = 0.0F;
		this.backRightLeg.yaw = 0.0F;
		this.backLeftLeg.yaw = 0.0F;
		this.field_3594.pitch = (float) (Math.PI / 2);
		if (!turtleEntity.isTouchingWater() && turtleEntity.onGround) {
			float l = turtleEntity.isDiggingSand() ? 4.0F : 1.0F;
			float m = turtleEntity.isDiggingSand() ? 2.0F : 1.0F;
			float n = 5.0F;
			this.frontRightLeg.yaw = MathHelper.cos(l * f * 5.0F + (float) Math.PI) * 8.0F * g * m;
			this.frontRightLeg.roll = 0.0F;
			this.frontLeftLeg.yaw = MathHelper.cos(l * f * 5.0F) * 8.0F * g * m;
			this.frontLeftLeg.roll = 0.0F;
			this.backRightLeg.yaw = MathHelper.cos(f * 5.0F + (float) Math.PI) * 3.0F * g;
			this.backRightLeg.pitch = 0.0F;
			this.backLeftLeg.yaw = MathHelper.cos(f * 5.0F) * 3.0F * g;
			this.backLeftLeg.pitch = 0.0F;
		}
	}
}
