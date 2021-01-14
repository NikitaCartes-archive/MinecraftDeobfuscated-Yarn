package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.mob.ZombieEntity;

@Environment(EnvType.CLIENT)
public class ZombieVillagerEntityModel<T extends ZombieEntity> extends BipedEntityModel<T> implements ModelWithHat {
	private ModelPart hatRim;

	public ZombieVillagerEntityModel(float scale, boolean bl) {
		super(scale, 0.0F, 64, bl ? 32 : 64);
		if (bl) {
			this.head = new ModelPart(this, 0, 0);
			this.head.addCuboid(-4.0F, -10.0F, -4.0F, 8.0F, 8.0F, 8.0F, scale);
			this.body = new ModelPart(this, 16, 16);
			this.body.addCuboid(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, scale + 0.1F);
			this.rightLeg = new ModelPart(this, 0, 16);
			this.rightLeg.setPivot(-2.0F, 12.0F, 0.0F);
			this.rightLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, scale + 0.1F);
			this.leftLeg = new ModelPart(this, 0, 16);
			this.leftLeg.mirror = true;
			this.leftLeg.setPivot(2.0F, 12.0F, 0.0F);
			this.leftLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, scale + 0.1F);
		} else {
			this.head = new ModelPart(this, 0, 0);
			this.head.setTextureOffset(0, 0).addCuboid(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, scale);
			this.head.setTextureOffset(24, 0).addCuboid(-1.0F, -3.0F, -6.0F, 2.0F, 4.0F, 2.0F, scale);
			this.hat = new ModelPart(this, 32, 0);
			this.hat.addCuboid(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, scale + 0.5F);
			this.hatRim = new ModelPart(this);
			this.hatRim.setTextureOffset(30, 47).addCuboid(-8.0F, -8.0F, -6.0F, 16.0F, 16.0F, 1.0F, scale);
			this.hatRim.pitch = (float) (-Math.PI / 2);
			this.hat.addChild(this.hatRim);
			this.body = new ModelPart(this, 16, 20);
			this.body.addCuboid(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F, scale);
			this.body.setTextureOffset(0, 38).addCuboid(-4.0F, 0.0F, -3.0F, 8.0F, 18.0F, 6.0F, scale + 0.05F);
			this.rightArm = new ModelPart(this, 44, 22);
			this.rightArm.addCuboid(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, scale);
			this.rightArm.setPivot(-5.0F, 2.0F, 0.0F);
			this.leftArm = new ModelPart(this, 44, 22);
			this.leftArm.mirror = true;
			this.leftArm.addCuboid(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, scale);
			this.leftArm.setPivot(5.0F, 2.0F, 0.0F);
			this.rightLeg = new ModelPart(this, 0, 22);
			this.rightLeg.setPivot(-2.0F, 12.0F, 0.0F);
			this.rightLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, scale);
			this.leftLeg = new ModelPart(this, 0, 22);
			this.leftLeg.mirror = true;
			this.leftLeg.setPivot(2.0F, 12.0F, 0.0F);
			this.leftLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, scale);
		}
	}

	public void setAngles(T zombieEntity, float f, float g, float h, float i, float j) {
		super.setAngles(zombieEntity, f, g, h, i, j);
		CrossbowPosing.method_29352(this.leftArm, this.rightArm, zombieEntity.isAttacking(), this.handSwingProgress, h);
	}

	@Override
	public void setHatVisible(boolean visible) {
		this.head.visible = visible;
		this.hat.visible = visible;
		this.hatRim.visible = visible;
	}
}
