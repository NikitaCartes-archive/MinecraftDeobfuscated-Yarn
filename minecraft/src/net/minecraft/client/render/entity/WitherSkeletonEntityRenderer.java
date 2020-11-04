package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5617;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class WitherSkeletonEntityRenderer extends SkeletonEntityRenderer {
	private static final Identifier TEXTURE = new Identifier("textures/entity/skeleton/wither_skeleton.png");

	public WitherSkeletonEntityRenderer(class_5617.class_5618 arg) {
		super(arg, EntityModelLayers.WITHER_SKELETON, EntityModelLayers.WITHER_SKELETON_INNER_ARMOR, EntityModelLayers.WITHER_SKELETON_OUTER_ARMOR);
	}

	@Override
	public Identifier getTexture(AbstractSkeletonEntity abstractSkeletonEntity) {
		return TEXTURE;
	}

	protected void scale(AbstractSkeletonEntity abstractSkeletonEntity, MatrixStack matrixStack, float f) {
		matrixStack.scale(1.2F, 1.2F, 1.2F);
	}
}
