package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.mob.MobEntity;

@Environment(EnvType.CLIENT)
public abstract class MobEntityRenderer<T extends MobEntity, M extends EntityModel<T>> extends LivingEntityRenderer<T, M> {
	public MobEntityRenderer(EntityRendererFactory.Context context, M entityModel, float f) {
		super(context, entityModel, f);
	}

	protected boolean hasLabel(T mobEntity) {
		return super.hasLabel(mobEntity) && (mobEntity.shouldRenderName() || mobEntity.hasCustomName() && mobEntity == this.dispatcher.targetedEntity);
	}

	protected float getShadowRadius(T mobEntity) {
		return super.getShadowRadius(mobEntity) * mobEntity.getScaleFactor();
	}
}
