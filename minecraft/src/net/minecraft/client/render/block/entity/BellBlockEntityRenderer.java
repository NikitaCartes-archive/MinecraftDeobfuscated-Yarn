package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BellBlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.model.BellBlockModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class BellBlockEntityRenderer implements BlockEntityRenderer<BellBlockEntity> {
	public static final SpriteIdentifier BELL_BODY_TEXTURE = new SpriteIdentifier(
		SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, Identifier.ofVanilla("entity/bell/bell_body")
	);
	private final BellBlockModel bellBody;

	public BellBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
		this.bellBody = new BellBlockModel(context.getLayerModelPart(EntityModelLayers.BELL));
	}

	public void render(BellBlockEntity bellBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
		VertexConsumer vertexConsumer = BELL_BODY_TEXTURE.getVertexConsumer(vertexConsumerProvider, RenderLayer::getEntitySolid);
		this.bellBody.update(bellBlockEntity, f);
		this.bellBody.render(matrixStack, vertexConsumer, i, j);
	}
}
