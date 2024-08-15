package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.model.IllagerEntityModel;
import net.minecraft.client.render.entity.state.IllagerEntityRenderState;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.item.CrossbowItem;

@Environment(EnvType.CLIENT)
public abstract class IllagerEntityRenderer<T extends IllagerEntity, S extends IllagerEntityRenderState> extends MobEntityRenderer<T, S, IllagerEntityModel<S>> {
	protected IllagerEntityRenderer(EntityRendererFactory.Context ctx, IllagerEntityModel<S> model, float shadowRadius) {
		super(ctx, model, shadowRadius);
		this.addFeature(new HeadFeatureRenderer<>(this, ctx.getModelLoader(), ctx.getItemRenderer()));
	}

	public void updateRenderState(T illagerEntity, S illagerEntityRenderState, float f) {
		super.updateRenderState(illagerEntity, illagerEntityRenderState, f);
		illagerEntityRenderState.hasVehicle = illagerEntity.hasVehicle();
		illagerEntityRenderState.illagerMainArm = illagerEntity.getMainArm();
		illagerEntityRenderState.illagerState = illagerEntity.getState();
		illagerEntityRenderState.crossbowPullTime = illagerEntityRenderState.illagerState == IllagerEntity.State.CROSSBOW_CHARGE
			? CrossbowItem.getPullTime(illagerEntity.getActiveItem(), illagerEntity)
			: 0;
		illagerEntityRenderState.itemUseTime = illagerEntity.getItemUseTime();
		illagerEntityRenderState.handSwingProgress = illagerEntity.getHandSwingProgress(f);
		illagerEntityRenderState.attacking = illagerEntity.isAttacking();
	}
}
