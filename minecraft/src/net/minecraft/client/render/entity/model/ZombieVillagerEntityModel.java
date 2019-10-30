package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class ZombieVillagerEntityModel<T extends ZombieEntity> extends BipedEntityModel<T> implements ModelWithHat {
	private ModelPart hat;

	public ZombieVillagerEntityModel(float f, boolean bl) {
		super(f, 0.0F, 64, bl ? 32 : 64);
		if (bl) {
			this.head = new ModelPart(this, 0, 0);
			this.head.addCuboid(-4.0F, -10.0F, -4.0F, 8.0F, 8.0F, 8.0F, f);
			this.body = new ModelPart(this, 16, 16);
			this.body.addCuboid(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, f + 0.1F);
			this.rightLeg = new ModelPart(this, 0, 16);
			this.rightLeg.setPivot(-2.0F, 12.0F, 0.0F);
			this.rightLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, f + 0.1F);
			this.leftLeg = new ModelPart(this, 0, 16);
			this.leftLeg.mirror = true;
			this.leftLeg.setPivot(2.0F, 12.0F, 0.0F);
			this.leftLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, f + 0.1F);
		} else {
			this.head = new ModelPart(this, 0, 0);
			this.head.setTextureOffset(0, 0).addCuboid(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, f);
			this.head.setTextureOffset(24, 0).addCuboid(-1.0F, -3.0F, -6.0F, 2.0F, 4.0F, 2.0F, f);
			this.headwear = new ModelPart(this, 32, 0);
			this.headwear.addCuboid(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, f + 0.5F);
			this.hat = new ModelPart(this);
			this.hat.setTextureOffset(30, 47).addCuboid(-8.0F, -8.0F, -6.0F, 16.0F, 16.0F, 1.0F, f);
			this.hat.pitch = (float) (-Math.PI / 2);
			this.headwear.addChild(this.hat);
			this.body = new ModelPart(this, 16, 20);
			this.body.addCuboid(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F, f);
			this.body.setTextureOffset(0, 38).addCuboid(-4.0F, 0.0F, -3.0F, 8.0F, 18.0F, 6.0F, f + 0.05F);
			this.rightArm = new ModelPart(this, 44, 22);
			this.rightArm.addCuboid(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, f);
			this.rightArm.setPivot(-5.0F, 2.0F, 0.0F);
			this.leftArm = new ModelPart(this, 44, 22);
			this.leftArm.mirror = true;
			this.leftArm.addCuboid(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, f);
			this.leftArm.setPivot(5.0F, 2.0F, 0.0F);
			this.rightLeg = new ModelPart(this, 0, 22);
			this.rightLeg.setPivot(-2.0F, 12.0F, 0.0F);
			this.rightLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, f);
			this.leftLeg = new ModelPart(this, 0, 22);
			this.leftLeg.mirror = true;
			this.leftLeg.setPivot(2.0F, 12.0F, 0.0F);
			this.leftLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, f);
		}
	}

	public void method_17135(T zombieEntity, float f, float g, float h, float i, float j, float k) {
		super.method_17087(zombieEntity, f, g, h, i, j, k);
		float l = MathHelper.sin(this.handSwingProgress * (float) Math.PI);
		float m = MathHelper.sin((1.0F - (1.0F - this.handSwingProgress) * (1.0F - this.handSwingProgress)) * (float) Math.PI);
		this.rightArm.roll = 0.0F;
		this.leftArm.roll = 0.0F;
		this.rightArm.yaw = -(0.1F - l * 0.6F);
		this.leftArm.yaw = 0.1F - l * 0.6F;
		float n = (float) -Math.PI / (zombieEntity.isAttacking() ? 1.5F : 2.25F);
		this.rightArm.pitch = n;
		this.leftArm.pitch = n;
		this.rightArm.pitch += l * 1.2F - m * 0.4F;
		this.leftArm.pitch += l * 1.2F - m * 0.4F;
		this.rightArm.roll = this.rightArm.roll + MathHelper.cos(h * 0.09F) * 0.05F + 0.05F;
		this.leftArm.roll = this.leftArm.roll - (MathHelper.cos(h * 0.09F) * 0.05F + 0.05F);
		this.rightArm.pitch = this.rightArm.pitch + MathHelper.sin(h * 0.067F) * 0.05F;
		this.leftArm.pitch = this.leftArm.pitch - MathHelper.sin(h * 0.067F) * 0.05F;
	}

	@Override
	public void setHatVisible(boolean visible) {
		this.head.visible = visible;
		this.headwear.visible = visible;
		this.hat.visible = visible;
	}
}
