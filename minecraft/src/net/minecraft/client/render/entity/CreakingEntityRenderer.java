package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.feature.EmissiveFeatureRenderer;
import net.minecraft.client.render.entity.model.CreakingEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.CreakingEntityRenderState;
import net.minecraft.entity.mob.CreakingEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class CreakingEntityRenderer<T extends CreakingEntity> extends MobEntityRenderer<T, CreakingEntityRenderState, CreakingEntityModel> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/creaking/creaking.png");
	private static final Identifier EYES_TEXTURE = Identifier.ofVanilla("textures/entity/creaking/creaking_eyes.png");

	public CreakingEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new CreakingEntityModel(context.getPart(EntityModelLayers.CREAKING)), 0.7F);
		this.addFeature(new EmissiveFeatureRenderer<>(this, EYES_TEXTURE, (state, tickDelta) -> 1.0F, CreakingEntityModel::getEmissiveParts, RenderLayer::getEyes));
	}

	public Identifier getTexture(CreakingEntityRenderState creakingEntityRenderState) {
		return TEXTURE;
	}

	public CreakingEntityRenderState getRenderState() {
		return new CreakingEntityRenderState();
	}

	public void updateRenderState(T creakingEntity, CreakingEntityRenderState creakingEntityRenderState, float f) {
		super.updateRenderState(creakingEntity, creakingEntityRenderState, f);
		creakingEntityRenderState.attackAnimationState.copyFrom(creakingEntity.attackAnimationState);
		creakingEntityRenderState.invulnerableAnimationState.copyFrom(creakingEntity.invulnerableAnimationState);
		creakingEntityRenderState.active = creakingEntity.isActive();
		creakingEntityRenderState.unrooted = creakingEntity.isUnrooted();
	}
}
