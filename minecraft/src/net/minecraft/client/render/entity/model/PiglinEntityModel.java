package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class PiglinEntityModel<T extends MobEntity> extends PlayerEntityModel<T> {
	/**
	 * Maybe the ears are swapped
	 */
	public final ModelPart rightEar;
	public final ModelPart leftEar;

	public PiglinEntityModel(float scale, int textureWidth, int textureHeight) {
		super(scale, false);
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
		this.torso = new ModelPart(this, 16, 16);
		this.torso.addCuboid(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, scale);
		this.head = new ModelPart(this);
		this.head.setTextureOffset(0, 0).addCuboid(-5.0F, -8.0F, -4.0F, 10.0F, 8.0F, 8.0F, scale);
		this.head.setTextureOffset(31, 1).addCuboid(-2.0F, -4.0F, -5.0F, 4.0F, 4.0F, 1.0F, scale);
		this.head.setTextureOffset(2, 4).addCuboid(2.0F, -2.0F, -5.0F, 1.0F, 2.0F, 1.0F, scale);
		this.head.setTextureOffset(2, 0).addCuboid(-3.0F, -2.0F, -5.0F, 1.0F, 2.0F, 1.0F, scale);
		this.rightEar = new ModelPart(this);
		this.rightEar.setPivot(4.5F, -6.0F, 0.0F);
		this.rightEar.setTextureOffset(51, 6).addCuboid(0.0F, 0.0F, -2.0F, 1.0F, 5.0F, 4.0F, scale);
		this.head.addChild(this.rightEar);
		this.leftEar = new ModelPart(this);
		this.leftEar.setPivot(-4.5F, -6.0F, 0.0F);
		this.leftEar.setTextureOffset(39, 6).addCuboid(-1.0F, 0.0F, -2.0F, 1.0F, 5.0F, 4.0F, scale);
		this.head.addChild(this.leftEar);
		this.helmet = new ModelPart(this);
	}

	public void setAngles(T mobEntity, float f, float g, float h, float i, float j) {
		super.setAngles(mobEntity, f, g, h, i, j);
		float k = (float) (Math.PI / 6);
		float l = h * 0.1F + f * 0.5F;
		float m = 0.08F + g * 0.4F;
		this.rightEar.roll = (float) (-Math.PI / 6) - MathHelper.cos(l * 1.2F) * m;
		this.leftEar.roll = (float) (Math.PI / 6) + MathHelper.cos(l) * m;
		if (mobEntity instanceof PiglinEntity) {
			PiglinEntity piglinEntity = (PiglinEntity)mobEntity;
			PiglinEntity.Activity activity = piglinEntity.getActivity();
			if (activity == PiglinEntity.Activity.CROSSBOW_HOLD) {
				CrossbowPosing.hold(this.rightArm, this.leftArm, this.head, !mobEntity.isLeftHanded());
			} else if (activity == PiglinEntity.Activity.CROSSBOW_CHARGE) {
				CrossbowPosing.charge(this.rightArm, this.leftArm, mobEntity, !mobEntity.isLeftHanded());
			} else if (activity == PiglinEntity.Activity.ADMIRING_ITEM) {
				this.head.pitch = 0.5F;
				this.head.yaw = 0.0F;
				if (mobEntity.isLeftHanded()) {
					this.rightArm.yaw = -0.5F;
					this.rightArm.pitch = -0.9F;
				} else {
					this.leftArm.yaw = 0.5F;
					this.leftArm.pitch = -0.9F;
				}
			}
		}
	}
}
