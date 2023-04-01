package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_8293;
import net.minecraft.class_8464;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.SquidEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.GlowSquidEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public class GlowSquidEntityRenderer extends SquidEntityRenderer<GlowSquidEntity> {
	public static final class_8464.class_8465[] field_44411 = class_8464.method_51053(2);
	private static final Identifier TEXTURE = new Identifier("textures/entity/squid/glow_squid.png");

	public GlowSquidEntityRenderer(EntityRendererFactory.Context context, SquidEntityModel<GlowSquidEntity> squidEntityModel) {
		super(context, squidEntityModel);
	}

	public void render(GlowSquidEntity glowSquidEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		if (class_8293.field_43661.method_50116()) {
			float h = this.getAnimationProgress(glowSquidEntity, g) * 3.0F;
			if (h > 0.0F) {
				int j = ColorHelper.Argb.getArgb(32, 0, 192, 255);
				matrixStack.push();
				matrixStack.scale(h, h, h);
				Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();
				class_8464.method_51054(field_44411, matrix4f, vertexConsumerProvider, j);
				matrixStack.pop();
			}
		}

		super.render(glowSquidEntity, f, g, matrixStack, vertexConsumerProvider, i);
	}

	public Identifier getTexture(GlowSquidEntity glowSquidEntity) {
		return TEXTURE;
	}

	protected int getBlockLight(GlowSquidEntity glowSquidEntity, BlockPos blockPos) {
		int i = (int)MathHelper.clampedLerp(0.0F, 15.0F, 1.0F - (float)glowSquidEntity.getDarkTicksRemaining() / 10.0F);
		return i == 15 ? 15 : Math.max(i, super.getBlockLight(glowSquidEntity, blockPos));
	}
}
