package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public abstract class EntityModel<T extends Entity> extends Model {
	public float swingProgress;
	public boolean isRiding;
	public boolean isChild = true;

	public void render(T entity, float f, float g, float h, float i, float j, float k) {
	}

	public void setAngles(T entity, float f, float g, float h, float i, float j, float k) {
	}

	public void animateModel(T entity, float f, float g, float h) {
	}

	public void method_17081(EntityModel<T> entityModel) {
		entityModel.swingProgress = this.swingProgress;
		entityModel.isRiding = this.isRiding;
		entityModel.isChild = this.isChild;
	}
}
