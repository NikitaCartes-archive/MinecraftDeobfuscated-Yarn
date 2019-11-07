package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public abstract class AbstractZombieModel<T extends HostileEntity> extends BipedEntityModel<T> {
	protected AbstractZombieModel(float f, float g, int i, int j) {
		super(f, g, i, j);
	}

	public void method_17791(T hostileEntity, float f, float g, float h, float i, float j) {
		super.method_17087(hostileEntity, f, g, h, i, j);
		boolean bl = this.isAttacking(hostileEntity);
		float k = MathHelper.sin(this.handSwingProgress * (float) Math.PI);
		float l = MathHelper.sin((1.0F - (1.0F - this.handSwingProgress) * (1.0F - this.handSwingProgress)) * (float) Math.PI);
		this.rightArm.roll = 0.0F;
		this.leftArm.roll = 0.0F;
		this.rightArm.yaw = -(0.1F - k * 0.6F);
		this.leftArm.yaw = 0.1F - k * 0.6F;
		float m = (float) -Math.PI / (bl ? 1.5F : 2.25F);
		this.rightArm.pitch = m;
		this.leftArm.pitch = m;
		this.rightArm.pitch += k * 1.2F - l * 0.4F;
		this.leftArm.pitch += k * 1.2F - l * 0.4F;
		this.rightArm.roll = this.rightArm.roll + MathHelper.cos(h * 0.09F) * 0.05F + 0.05F;
		this.leftArm.roll = this.leftArm.roll - (MathHelper.cos(h * 0.09F) * 0.05F + 0.05F);
		this.rightArm.pitch = this.rightArm.pitch + MathHelper.sin(h * 0.067F) * 0.05F;
		this.leftArm.pitch = this.leftArm.pitch - MathHelper.sin(h * 0.067F) * 0.05F;
	}

	public abstract boolean isAttacking(T hostileEntity);
}
