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

	public abstract void setAngles(T entity, float limbAngle, float limbDistance, float age, float headYaw, float headPitch, float scale);

	public void animateModel(T entity, float limbAngle, float limbDistance, float tickDelta) {
	}

	public void copyStateTo(EntityModel<T> copy) {
		copy.handSwingProgress = this.handSwingProgress;
		copy.isRiding = this.isRiding;
		copy.isChild = this.isChild;
	}
}
