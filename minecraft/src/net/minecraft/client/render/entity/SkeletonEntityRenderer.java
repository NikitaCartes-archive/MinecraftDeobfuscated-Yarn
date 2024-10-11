package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.SkeletonEntityRenderState;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SkeletonEntityRenderer extends AbstractSkeletonEntityRenderer<SkeletonEntity, SkeletonEntityRenderState> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/skeleton/skeleton.png");

	public SkeletonEntityRenderer(EntityRendererFactory.Context context) {
		super(context, EntityModelLayers.SKELETON, EntityModelLayers.SKELETON_INNER_ARMOR, EntityModelLayers.SKELETON_OUTER_ARMOR);
	}

	public Identifier getTexture(SkeletonEntityRenderState skeletonEntityRenderState) {
		return TEXTURE;
	}

	public SkeletonEntityRenderState createRenderState() {
		return new SkeletonEntityRenderState();
	}
}
