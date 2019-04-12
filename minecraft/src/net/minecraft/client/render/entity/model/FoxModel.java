package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class FoxModel<T extends FoxEntity> extends EntityModel<T> {
	public final Cuboid head;
	private final Cuboid leftEar;
	private final Cuboid rightEar;
	private final Cuboid nose;
	private final Cuboid body;
	private final Cuboid frontLeftLeg;
	private final Cuboid frontRightLeg;
	private final Cuboid rearLeftLeg;
	private final Cuboid rearRightLeg;
	private final Cuboid tail;
	private float field_18025;

	public FoxModel() {
		this.textureWidth = 48;
		this.textureHeight = 32;
		this.head = new Cuboid(this, 1, 5);
		this.head.addBox(-3.0F, -2.0F, -5.0F, 8, 6, 6);
		this.head.setRotationPoint(-1.0F, 16.5F, -3.0F);
		this.leftEar = new Cuboid(this, 8, 1);
		this.leftEar.addBox(-3.0F, -4.0F, -4.0F, 2, 2, 1);
		this.rightEar = new Cuboid(this, 15, 1);
		this.rightEar.addBox(3.0F, -4.0F, -4.0F, 2, 2, 1);
		this.nose = new Cuboid(this, 6, 18);
		this.nose.addBox(-1.0F, 2.01F, -8.0F, 4, 2, 3);
		this.head.addChild(this.leftEar);
		this.head.addChild(this.rightEar);
		this.head.addChild(this.nose);
		this.body = new Cuboid(this, 24, 15);
		this.body.addBox(-3.0F, 3.999F, -3.5F, 6, 11, 6);
		this.body.setRotationPoint(0.0F, 16.0F, -6.0F);
		float f = 0.001F;
		this.frontLeftLeg = new Cuboid(this, 13, 24);
		this.frontLeftLeg.addBox(2.0F, 0.5F, -1.0F, 2, 6, 2, 0.001F);
		this.frontLeftLeg.setRotationPoint(-5.0F, 17.5F, 7.0F);
		this.frontRightLeg = new Cuboid(this, 4, 24);
		this.frontRightLeg.addBox(2.0F, 0.5F, -1.0F, 2, 6, 2, 0.001F);
		this.frontRightLeg.setRotationPoint(-1.0F, 17.5F, 7.0F);
		this.rearLeftLeg = new Cuboid(this, 13, 24);
		this.rearLeftLeg.addBox(2.0F, 0.5F, -1.0F, 2, 6, 2, 0.001F);
		this.rearLeftLeg.setRotationPoint(-5.0F, 17.5F, 0.0F);
		this.rearRightLeg = new Cuboid(this, 4, 24);
		this.rearRightLeg.addBox(2.0F, 0.5F, -1.0F, 2, 6, 2, 0.001F);
		this.rearRightLeg.setRotationPoint(-1.0F, 17.5F, 0.0F);
		this.tail = new Cuboid(this, 30, 0);
		this.tail.addBox(2.0F, 0.0F, -1.0F, 4, 9, 5);
		this.tail.setRotationPoint(-4.0F, 15.0F, -1.0F);
		this.body.addChild(this.tail);
	}

	public void method_18330(T foxEntity, float f, float g, float h) {
		this.body.pitch = (float) (Math.PI / 2);
		this.tail.pitch = -0.05235988F;
		this.frontLeftLeg.pitch = MathHelper.cos(f * 0.6662F) * 1.4F * g;
		this.frontRightLeg.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 1.4F * g;
		this.rearLeftLeg.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 1.4F * g;
		this.rearRightLeg.pitch = MathHelper.cos(f * 0.6662F) * 1.4F * g;
		this.head.roll = foxEntity.getHeadRoll(h);
		this.frontLeftLeg.visible = true;
		this.frontRightLeg.visible = true;
		this.rearLeftLeg.visible = true;
		this.rearRightLeg.visible = true;
		this.head.setRotationPoint(-1.0F, 16.5F, -3.0F);
		this.body.setRotationPoint(0.0F, 16.0F, -6.0F);
		this.body.roll = 0.0F;
		this.head.yaw = 0.0F;
		this.frontLeftLeg.setRotationPoint(-5.0F, 17.5F, 7.0F);
		this.frontRightLeg.setRotationPoint(-1.0F, 17.5F, 7.0F);
		if (foxEntity.isCrouching()) {
			this.body.pitch = 1.6755161F;
			float i = foxEntity.getBodyRotationHeightOffset(h);
			this.body.setRotationPoint(0.0F, 16.0F + foxEntity.getBodyRotationHeightOffset(h), -6.0F);
			this.head.setRotationPoint(-1.0F, 16.5F + i, -3.0F);
		} else if (foxEntity.isSleeping()) {
			this.body.roll = (float) (-Math.PI / 2);
			this.body.setRotationPoint(0.0F, 21.0F + foxEntity.getBodyRotationHeightOffset(h), -6.0F);
			this.tail.pitch = (float) (-Math.PI * 5.0 / 6.0);
			if (this.isChild) {
				this.tail.pitch = -2.1816616F;
				this.body.setRotationPoint(0.0F, 21.0F + foxEntity.getBodyRotationHeightOffset(h), -2.0F);
			}

			this.head.setRotationPoint(1.0F, 19.49F, -3.0F);
			this.head.yaw = (float) (-Math.PI * 2.0 / 3.0);
			this.frontLeftLeg.visible = false;
			this.frontRightLeg.visible = false;
			this.rearLeftLeg.visible = false;
			this.rearRightLeg.visible = false;
		} else if (foxEntity.isSitting()) {
			this.body.pitch = (float) (Math.PI / 6);
			this.body.setRotationPoint(0.0F, 9.0F, -3.0F);
			this.tail.pitch = (float) (Math.PI / 4);
			this.tail.setRotationPoint(-4.0F, 15.0F, -2.0F);
			this.head.setRotationPoint(-1.0F, 10.0F, -0.25F);
			this.head.pitch = 0.0F;
			if (this.isChild) {
				this.head.setRotationPoint(-1.0F, 13.0F, -3.75F);
			}

			this.frontLeftLeg.pitch = (float) (-Math.PI * 5.0 / 12.0);
			this.frontLeftLeg.setRotationPoint(-5.0F, 21.5F, 6.75F);
			this.frontRightLeg.pitch = (float) (-Math.PI * 5.0 / 12.0);
			this.frontRightLeg.setRotationPoint(-1.0F, 21.5F, 6.75F);
			this.rearLeftLeg.pitch = (float) (-Math.PI / 12);
			this.rearRightLeg.pitch = (float) (-Math.PI / 12);
		}
	}

	public void method_18331(T foxEntity, float f, float g, float h, float i, float j, float k) {
		super.render(foxEntity, f, g, h, i, j, k);
		this.method_18332(foxEntity, f, g, h, i, j, k);
		if (this.isChild) {
			GlStateManager.pushMatrix();
			float l = 0.75F;
			GlStateManager.scalef(0.75F, 0.75F, 0.75F);
			GlStateManager.translatef(0.0F, 8.0F * k, 3.35F * k);
			this.head.render(k);
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			float m = 0.5F;
			GlStateManager.scalef(0.5F, 0.5F, 0.5F);
			GlStateManager.translatef(0.0F, 24.0F * k, 0.0F);
			this.body.render(k);
			this.frontLeftLeg.render(k);
			this.frontRightLeg.render(k);
			this.rearLeftLeg.render(k);
			this.rearRightLeg.render(k);
			GlStateManager.popMatrix();
		} else {
			GlStateManager.pushMatrix();
			this.head.render(k);
			this.body.render(k);
			this.frontLeftLeg.render(k);
			this.frontRightLeg.render(k);
			this.rearLeftLeg.render(k);
			this.rearRightLeg.render(k);
			GlStateManager.popMatrix();
		}
	}

	public void method_18332(T foxEntity, float f, float g, float h, float i, float j, float k) {
		super.setAngles(foxEntity, f, g, h, i, j, k);
		if (!foxEntity.isSleeping() && !foxEntity.isWalking() && !foxEntity.isCrouching()) {
			this.head.pitch = j * (float) (Math.PI / 180.0);
			this.head.yaw = i * (float) (Math.PI / 180.0);
		}

		if (foxEntity.isSleeping()) {
			this.head.roll = MathHelper.cos(h * 0.027F) / 22.0F;
		}

		if (foxEntity.isCrouching()) {
			float l = MathHelper.cos(h) * 0.01F;
			this.body.yaw = l;
			this.frontLeftLeg.roll = l;
			this.frontRightLeg.roll = l;
			this.rearLeftLeg.roll = l / 2.0F;
			this.rearRightLeg.roll = l / 2.0F;
		}

		if (foxEntity.isWalking()) {
			float l = 0.1F;
			this.field_18025 += 0.67F;
			this.frontLeftLeg.pitch = MathHelper.cos(this.field_18025 * 0.4662F) * 0.1F;
			this.frontRightLeg.pitch = MathHelper.cos(this.field_18025 * 0.4662F + (float) Math.PI) * 0.1F;
			this.rearLeftLeg.pitch = MathHelper.cos(this.field_18025 * 0.4662F + (float) Math.PI) * 0.1F;
			this.rearRightLeg.pitch = MathHelper.cos(this.field_18025 * 0.4662F) * 0.1F;
		}
	}
}
