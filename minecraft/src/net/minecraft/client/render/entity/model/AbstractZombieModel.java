package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.mob.HostileEntity;

@Environment(EnvType.CLIENT)
public abstract class AbstractZombieModel<T extends HostileEntity> extends BipedEntityModel<T> {
	protected AbstractZombieModel(float f, float g, int i, int j) {
		super(f, g, i, j);
	}

	public void setAngles(T hostileEntity, float f, float g, float h, float i, float j) {
		super.setAngles(hostileEntity, f, g, h, i, j);
		CrossbowPosing.method_29352(this.leftArm, this.rightArm, this.isAttacking(hostileEntity), this.handSwingProgress, h);
	}

	public abstract boolean isAttacking(T hostileEntity);
}
