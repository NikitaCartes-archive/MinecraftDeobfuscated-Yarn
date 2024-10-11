package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.feature.EmissiveFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.WardenEntityModel;
import net.minecraft.client.render.entity.state.WardenEntityRenderState;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class WardenEntityRenderer extends MobEntityRenderer<WardenEntity, WardenEntityRenderState, WardenEntityModel> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/warden/warden.png");
	private static final Identifier BIOLUMINESCENT_LAYER_TEXTURE = Identifier.ofVanilla("textures/entity/warden/warden_bioluminescent_layer.png");
	private static final Identifier HEART_TEXTURE = Identifier.ofVanilla("textures/entity/warden/warden_heart.png");
	private static final Identifier PULSATING_SPOTS_1_TEXTURE = Identifier.ofVanilla("textures/entity/warden/warden_pulsating_spots_1.png");
	private static final Identifier PULSATING_SPOTS_2_TEXTURE = Identifier.ofVanilla("textures/entity/warden/warden_pulsating_spots_2.png");

	public WardenEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new WardenEntityModel(context.getPart(EntityModelLayers.WARDEN)), 0.9F);
		this.addFeature(
			new EmissiveFeatureRenderer<>(
				this, BIOLUMINESCENT_LAYER_TEXTURE, (state, tickDelta) -> 1.0F, WardenEntityModel::getHeadAndLimbs, RenderLayer::getEntityTranslucentEmissive
			)
		);
		this.addFeature(
			new EmissiveFeatureRenderer<>(
				this,
				PULSATING_SPOTS_1_TEXTURE,
				(state, tickDelta) -> Math.max(0.0F, MathHelper.cos(tickDelta * 0.045F) * 0.25F),
				WardenEntityModel::getBodyHeadAndLimbs,
				RenderLayer::getEntityTranslucentEmissive
			)
		);
		this.addFeature(
			new EmissiveFeatureRenderer<>(
				this,
				PULSATING_SPOTS_2_TEXTURE,
				(state, tickDelta) -> Math.max(0.0F, MathHelper.cos(tickDelta * 0.045F + (float) Math.PI) * 0.25F),
				WardenEntityModel::getBodyHeadAndLimbs,
				RenderLayer::getEntityTranslucentEmissive
			)
		);
		this.addFeature(
			new EmissiveFeatureRenderer<>(
				this, TEXTURE, (state, tickDelta) -> state.tendrilPitch, WardenEntityModel::getTendrils, RenderLayer::getEntityTranslucentEmissive
			)
		);
		this.addFeature(
			new EmissiveFeatureRenderer<>(
				this, HEART_TEXTURE, (state, tickDelta) -> state.heartPitch, WardenEntityModel::getBody, RenderLayer::getEntityTranslucentEmissive
			)
		);
	}

	public Identifier getTexture(WardenEntityRenderState wardenEntityRenderState) {
		return TEXTURE;
	}

	public WardenEntityRenderState createRenderState() {
		return new WardenEntityRenderState();
	}

	public void updateRenderState(WardenEntity wardenEntity, WardenEntityRenderState wardenEntityRenderState, float f) {
		super.updateRenderState(wardenEntity, wardenEntityRenderState, f);
		wardenEntityRenderState.tendrilPitch = wardenEntity.getTendrilPitch(f);
		wardenEntityRenderState.heartPitch = wardenEntity.getHeartPitch(f);
		wardenEntityRenderState.roaringAnimationState.copyFrom(wardenEntity.roaringAnimationState);
		wardenEntityRenderState.sniffingAnimationState.copyFrom(wardenEntity.sniffingAnimationState);
		wardenEntityRenderState.emergingAnimationState.copyFrom(wardenEntity.emergingAnimationState);
		wardenEntityRenderState.diggingAnimationState.copyFrom(wardenEntity.diggingAnimationState);
		wardenEntityRenderState.attackingAnimationState.copyFrom(wardenEntity.attackingAnimationState);
		wardenEntityRenderState.chargingSonicBoomAnimationState.copyFrom(wardenEntity.chargingSonicBoomAnimationState);
	}
}
