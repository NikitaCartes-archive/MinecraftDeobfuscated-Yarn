package net.minecraft.client.render.entity.model;

import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public abstract class EntityModel<T extends Entity> extends Model {
	public float handSwingProgress;
	public boolean isRiding;
	public boolean isChild = true;

	protected EntityModel() {
		this(RenderLayer::getEntityCutoutNoCull);
	}

	protected EntityModel(Function<Identifier, RenderLayer> function) {
		super(function);
	}

	public abstract void setAngles(T entity, float f, float g, float h, float i, float j, float k);

	public void animateModel(T entity, float f, float g, float h) {
	}

	public void copyStateTo(EntityModel<T> entityModel) {
		entityModel.handSwingProgress = this.handSwingProgress;
		entityModel.isRiding = this.isRiding;
		entityModel.isChild = this.isChild;
	}
}
