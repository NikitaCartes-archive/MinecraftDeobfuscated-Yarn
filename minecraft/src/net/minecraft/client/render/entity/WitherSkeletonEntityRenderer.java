package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.SkeletonEntityRenderState;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class WitherSkeletonEntityRenderer extends AbstractSkeletonEntityRenderer<WitherSkeletonEntity, SkeletonEntityRenderState> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/skeleton/wither_skeleton.png");

	public WitherSkeletonEntityRenderer(EntityRendererFactory.Context context) {
		super(context, EntityModelLayers.WITHER_SKELETON, EntityModelLayers.WITHER_SKELETON_INNER_ARMOR, EntityModelLayers.WITHER_SKELETON_OUTER_ARMOR);
	}

	public Identifier getTexture(SkeletonEntityRenderState skeletonEntityRenderState) {
		return TEXTURE;
	}

	public SkeletonEntityRenderState getRenderState() {
		return new SkeletonEntityRenderState();
	}
}
