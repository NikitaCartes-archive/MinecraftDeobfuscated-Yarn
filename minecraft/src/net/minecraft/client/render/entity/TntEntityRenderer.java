package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
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

	public void method_4135(
		TntEntity tntEntity, double d, double e, double f, float g, float h, MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage
	) {
		matrixStack.push();
		matrixStack.translate(0.0, 0.5, 0.0);
		if ((float)tntEntity.getFuseTimer() - h + 1.0F < 10.0F) {
			float i = 1.0F - ((float)tntEntity.getFuseTimer() - h + 1.0F) / 10.0F;
			i = MathHelper.clamp(i, 0.0F, 1.0F);
			i *= i;
			i *= i;
			float j = 1.0F + i * 0.3F;
			matrixStack.scale(j, j, j);
		}

		int k = tntEntity.getLightmapCoordinates();
		matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(-90.0F));
		matrixStack.translate(-0.5, -0.5, 0.5);
		TntMinecartEntityRenderer.method_23190(Blocks.TNT.getDefaultState(), matrixStack, layeredVertexConsumerStorage, k, tntEntity.getFuseTimer() / 5 % 2 == 0);
		matrixStack.pop();
		super.render(tntEntity, d, e, f, g, h, matrixStack, layeredVertexConsumerStorage);
	}

	public Identifier method_4136(TntEntity tntEntity) {
		return SpriteAtlasTexture.BLOCK_ATLAS_TEX;
	}
}
