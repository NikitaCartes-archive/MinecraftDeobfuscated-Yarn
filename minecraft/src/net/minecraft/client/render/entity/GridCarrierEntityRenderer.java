package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.GridCarrierEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class GridCarrierEntityRenderer extends EntityRenderer<GridCarrierEntity> {
	public GridCarrierEntityRenderer(EntityRendererFactory.Context context) {
		super(context);
	}

	public void render(GridCarrierEntity gridCarrierEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
	}

	public Identifier getTexture(GridCarrierEntity gridCarrierEntity) {
		return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
	}
}
