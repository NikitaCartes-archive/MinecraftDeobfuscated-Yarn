package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.WardenFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.WardenEntityModel;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class WardenEntityRenderer extends MobEntityRenderer<WardenEntity, WardenEntityModel<WardenEntity>> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/warden/warden.png");
	private static final Identifier BIOLUMINESCENT_LAYER_TEXTURE = Identifier.ofVanilla("textures/entity/warden/warden_bioluminescent_layer.png");
	private static final Identifier HEART_TEXTURE = Identifier.ofVanilla("textures/entity/warden/warden_heart.png");
	private static final Identifier PULSATING_SPOTS_1_TEXTURE = Identifier.ofVanilla("textures/entity/warden/warden_pulsating_spots_1.png");
	private static final Identifier PULSATING_SPOTS_2_TEXTURE = Identifier.ofVanilla("textures/entity/warden/warden_pulsating_spots_2.png");

	public WardenEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new WardenEntityModel<>(context.getPart(EntityModelLayers.WARDEN)), 0.9F);
		this.addFeature(
			new WardenFeatureRenderer<>(this, BIOLUMINESCENT_LAYER_TEXTURE, (warden, tickDelta, animationProgress) -> 1.0F, WardenEntityModel::getHeadAndLimbs)
		);
		this.addFeature(
			new WardenFeatureRenderer<>(
				this,
				PULSATING_SPOTS_1_TEXTURE,
				(warden, tickDelta, animationProgress) -> Math.max(0.0F, MathHelper.cos(animationProgress * 0.045F) * 0.25F),
				WardenEntityModel::getBodyHeadAndLimbs
			)
		);
		this.addFeature(
			new WardenFeatureRenderer<>(
				this,
				PULSATING_SPOTS_2_TEXTURE,
				(warden, tickDelta, animationProgress) -> Math.max(0.0F, MathHelper.cos(animationProgress * 0.045F + (float) Math.PI) * 0.25F),
				WardenEntityModel::getBodyHeadAndLimbs
			)
		);
		this.addFeature(
			new WardenFeatureRenderer<>(this, TEXTURE, (warden, tickDelta, animationProgress) -> warden.getTendrilPitch(tickDelta), WardenEntityModel::getTendrils)
		);
		this.addFeature(
			new WardenFeatureRenderer<>(this, HEART_TEXTURE, (warden, tickDelta, animationProgress) -> warden.getHeartPitch(tickDelta), WardenEntityModel::getBody)
		);
	}

	public Identifier getTexture(WardenEntity wardenEntity) {
		return TEXTURE;
	}
}
