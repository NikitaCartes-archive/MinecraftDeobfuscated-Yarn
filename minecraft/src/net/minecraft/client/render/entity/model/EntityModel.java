package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public abstract class EntityModel<T extends Entity> extends Model {
	public float handSwingProgress;
	public boolean isRiding;
	public boolean isChild = true;

	public void render(T entity, float f, float g, float h, float i, float j, float k) {
	}

	public void setAngles(T entity, float f, float g, float h, float i, float j, float k) {
	}

	public void animateModel(T entity, float f, float g, float h) {
	}

	public void copyStateTo(EntityModel<T> entityModel) {
		entityModel.handSwingProgress = this.handSwingProgress;
		entityModel.isRiding = this.isRiding;
		entityModel.isChild = this.isChild;
	}
}
