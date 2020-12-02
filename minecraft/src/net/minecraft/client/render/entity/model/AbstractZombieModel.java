package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.mob.HostileEntity;

@Environment(EnvType.CLIENT)
public abstract class AbstractZombieModel<T extends HostileEntity> extends BipedEntityModel<T> {
	protected AbstractZombieModel(ModelPart modelPart) {
		super(modelPart);
	}

	public void setAngles(T hostileEntity, float f, float g, float h, float i, float j) {
		super.setAngles(hostileEntity, f, g, h, i, j);
		CrossbowPosing.meleeAttack(this.leftArm, this.rightArm, this.isAttacking(hostileEntity), this.handSwingProgress, h);
	}

	public abstract boolean isAttacking(T entity);
}
