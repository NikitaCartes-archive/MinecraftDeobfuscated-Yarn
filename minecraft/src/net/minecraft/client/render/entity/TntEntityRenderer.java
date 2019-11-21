package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.TntEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class TntEntityRenderer extends EntityRenderer<TntEntity> {
	public TntEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
		this.field_4673 = 0.5F;
	}

	public void render(TntEntity tntEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		matrixStack.push();
		matrixStack.translate(0.0, 0.5, 0.0);
		if ((float)tntEntity.getFuseTimer() - g + 1.0F < 10.0F) {
			float h = 1.0F - ((float)tntEntity.getFuseTimer() - g + 1.0F) / 10.0F;
			h = MathHelper.clamp(h, 0.0F, 1.0F);
			h *= h;
			h *= h;
			float j = 1.0F + h * 0.3F;
			matrixStack.scale(j, j, j);
		}

		matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-90.0F));
		matrixStack.translate(-0.5, -0.5, 0.5);
		TntMinecartEntityRenderer.method_23190(Blocks.TNT.getDefaultState(), matrixStack, vertexConsumerProvider, i, tntEntity.getFuseTimer() / 5 % 2 == 0);
		matrixStack.pop();
		super.render(tntEntity, f, g, matrixStack, vertexConsumerProvider, i);
	}

	public Identifier getTexture(TntEntity tntEntity) {
		return SpriteAtlasTexture.BLOCK_ATLAS_TEX;
	}
}
