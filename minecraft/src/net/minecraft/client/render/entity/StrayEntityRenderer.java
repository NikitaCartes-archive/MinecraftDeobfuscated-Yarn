package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.SkeletonOverlayFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.SkeletonEntityRenderState;
import net.minecraft.entity.mob.StrayEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class StrayEntityRenderer extends AbstractSkeletonEntityRenderer<StrayEntity, SkeletonEntityRenderState> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/skeleton/stray.png");
	private static final Identifier OVERLAY_TEXTURE = Identifier.ofVanilla("textures/entity/skeleton/stray_overlay.png");

	public StrayEntityRenderer(EntityRendererFactory.Context context) {
		super(context, EntityModelLayers.STRAY, EntityModelLayers.STRAY_INNER_ARMOR, EntityModelLayers.STRAY_OUTER_ARMOR);
		this.addFeature(new SkeletonOverlayFeatureRenderer<>(this, context.getModelLoader(), EntityModelLayers.STRAY_OUTER, OVERLAY_TEXTURE));
	}

	public Identifier getTexture(SkeletonEntityRenderState skeletonEntityRenderState) {
		return TEXTURE;
	}

	public SkeletonEntityRenderState getRenderState() {
		return new SkeletonEntityRenderState();
	}
}
