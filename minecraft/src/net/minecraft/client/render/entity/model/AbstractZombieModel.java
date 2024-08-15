package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.state.ZombieEntityRenderState;

@Environment(EnvType.CLIENT)
public abstract class AbstractZombieModel<S extends ZombieEntityRenderState> extends BipedEntityModel<S> {
	protected AbstractZombieModel(ModelPart modelPart) {
		super(modelPart);
	}

	public void setAngles(S zombieEntityRenderState) {
		super.setAngles(zombieEntityRenderState);
		float f = zombieEntityRenderState.handSwingProgress;
		CrossbowPosing.meleeAttack(this.leftArm, this.rightArm, zombieEntityRenderState.attacking, f, zombieEntityRenderState.age);
	}
}
