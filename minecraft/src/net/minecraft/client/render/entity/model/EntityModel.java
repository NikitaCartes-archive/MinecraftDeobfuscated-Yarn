package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public abstract class EntityModel<T extends Entity> extends Model {
	public float handSwingProgress;
	public boolean riding;
	public boolean child = true;

	public void render(T entity, float limbAngle, float limbDistance, float age, float headYaw, float headPitch, float scale) {
	}

	public void setAngles(T entity, float limbAngle, float limbDistance, float age, float headYaw, float headPitch, float scale) {
	}

	public void animateModel(T entity, float limbAngle, float limbDistance, float tickDelta) {
	}

	public void copyStateTo(EntityModel<T> copy) {
		copy.handSwingProgress = this.handSwingProgress;
		copy.riding = this.riding;
		copy.child = this.child;
	}
}
