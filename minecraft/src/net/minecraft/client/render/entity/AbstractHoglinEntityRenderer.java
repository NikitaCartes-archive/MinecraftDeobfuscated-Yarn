package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.HoglinEntityModel;
import net.minecraft.client.render.entity.state.HoglinEntityRenderState;
import net.minecraft.entity.mob.Hoglin;
import net.minecraft.entity.mob.MobEntity;

@Environment(EnvType.CLIENT)
public abstract class AbstractHoglinEntityRenderer<T extends MobEntity & Hoglin>
	extends AgeableMobEntityRenderer<T, HoglinEntityRenderState, HoglinEntityModel> {
	public AbstractHoglinEntityRenderer(EntityRendererFactory.Context context, EntityModelLayer layer, EntityModelLayer babyLayer, float scale) {
		super(context, new HoglinEntityModel(context.getPart(layer)), new HoglinEntityModel(context.getPart(babyLayer)), scale);
	}

	public HoglinEntityRenderState getRenderState() {
		return new HoglinEntityRenderState();
	}

	public void updateRenderState(T mobEntity, HoglinEntityRenderState hoglinEntityRenderState, float f) {
		super.updateRenderState(mobEntity, hoglinEntityRenderState, f);
		hoglinEntityRenderState.movementCooldownTicks = mobEntity.getMovementCooldownTicks();
	}
}
