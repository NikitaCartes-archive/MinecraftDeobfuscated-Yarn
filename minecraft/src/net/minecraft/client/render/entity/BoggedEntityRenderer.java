package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.SkeletonOverlayFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class BoggedEntityRenderer extends SkeletonEntityRenderer {
	private static final Identifier TEXTURE = new Identifier("textures/entity/skeleton/bogged.png");
	private static final Identifier OVERLAY_TEXTURE = new Identifier("textures/entity/skeleton/bogged_overlay.png");

	public BoggedEntityRenderer(EntityRendererFactory.Context context) {
		super(context, EntityModelLayers.BOGGED, EntityModelLayers.BOGGED_INNER_ARMOR, EntityModelLayers.BOGGED_OUTER_ARMOR);
		this.addFeature(new SkeletonOverlayFeatureRenderer<>(this, context.getModelLoader(), EntityModelLayers.BOGGED_OUTER, OVERLAY_TEXTURE));
	}

	@Override
	public Identifier getTexture(AbstractSkeletonEntity abstractSkeletonEntity) {
		return TEXTURE;
	}
}
