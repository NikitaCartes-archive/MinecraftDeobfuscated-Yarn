package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class WitherSkeletonEntityRenderer extends SkeletonEntityRenderer {
	private static final Identifier SKIN = new Identifier("textures/entity/skeleton/wither_skeleton.png");

	public WitherSkeletonEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
	}

	@Override
	public Identifier getTexture(AbstractSkeletonEntity abstractSkeletonEntity) {
		return SKIN;
	}

	protected void scale(AbstractSkeletonEntity abstractSkeletonEntity, MatrixStack matrixStack, float f) {
		matrixStack.scale(1.2F, 1.2F, 1.2F);
	}
}
