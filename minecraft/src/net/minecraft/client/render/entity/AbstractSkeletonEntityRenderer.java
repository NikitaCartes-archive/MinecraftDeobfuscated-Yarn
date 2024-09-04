package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.SkeletonEntityModel;
import net.minecraft.client.render.entity.state.SkeletonEntityRenderState;
import net.minecraft.entity.mob.AbstractSkeletonEntity;

@Environment(EnvType.CLIENT)
public abstract class AbstractSkeletonEntityRenderer<T extends AbstractSkeletonEntity, S extends SkeletonEntityRenderState>
	extends BipedEntityRenderer<T, S, SkeletonEntityModel<S>> {
	public AbstractSkeletonEntityRenderer(
		EntityRendererFactory.Context context, EntityModelLayer layer, EntityModelLayer armorInnerLayer, EntityModelLayer armorOuterLayer
	) {
		this(context, armorInnerLayer, armorOuterLayer, new SkeletonEntityModel<>(context.getPart(layer)));
	}

	public AbstractSkeletonEntityRenderer(
		EntityRendererFactory.Context context, EntityModelLayer armorInnerLayer, EntityModelLayer armorOuterLayer, SkeletonEntityModel<S> model
	) {
		super(context, model, 0.5F);
		this.addFeature(
			new ArmorFeatureRenderer<>(
				this, new SkeletonEntityModel(context.getPart(armorInnerLayer)), new SkeletonEntityModel(context.getPart(armorOuterLayer)), context.getEquipmentRenderer()
			)
		);
	}

	public void updateRenderState(T abstractSkeletonEntity, S skeletonEntityRenderState, float f) {
		super.updateRenderState(abstractSkeletonEntity, skeletonEntityRenderState, f);
		skeletonEntityRenderState.attacking = abstractSkeletonEntity.isAttacking();
		skeletonEntityRenderState.shaking = abstractSkeletonEntity.isShaking();
	}

	protected boolean isShaking(S skeletonEntityRenderState) {
		return skeletonEntityRenderState.shaking;
	}
}
