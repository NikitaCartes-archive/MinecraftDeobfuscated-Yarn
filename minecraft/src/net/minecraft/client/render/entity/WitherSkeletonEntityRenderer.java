package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class WitherSkeletonEntityRenderer extends SkeletonEntityRenderer {
	private static final Identifier SKIN = new Identifier("textures/entity/skeleton/wither_skeleton.png");

	public WitherSkeletonEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
	}

	@Override
	public Identifier method_4119(AbstractSkeletonEntity abstractSkeletonEntity) {
		return SKIN;
	}

	protected void method_4161(AbstractSkeletonEntity abstractSkeletonEntity, MatrixStack matrixStack, float f) {
		matrixStack.scale(1.2F, 1.2F, 1.2F);
	}
}
