package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5603;
import net.minecraft.class_5606;
import net.minecraft.class_5607;
import net.minecraft.class_5609;
import net.minecraft.class_5610;
import net.minecraft.block.entity.BellBlockEntity;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class BellBlockEntityRenderer implements BlockEntityRenderer<BellBlockEntity> {
	public static final SpriteIdentifier BELL_BODY_TEXTURE = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier("entity/bell/bell_body"));
	private final ModelPart field_20816;

	public BellBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
		ModelPart modelPart = context.getLayerModelPart(EntityModelLayers.BELL);
		this.field_20816 = modelPart.method_32086("bell_body");
	}

	public static class_5607 method_32138() {
		class_5609 lv = new class_5609();
		class_5610 lv2 = lv.method_32111();
		class_5610 lv3 = lv2.method_32117(
			"bell_body", class_5606.method_32108().method_32101(0, 0).method_32097(-3.0F, -6.0F, -3.0F, 6.0F, 7.0F, 6.0F), class_5603.method_32090(8.0F, 12.0F, 8.0F)
		);
		lv3.method_32117(
			"bell_base", class_5606.method_32108().method_32101(0, 13).method_32097(4.0F, 4.0F, 4.0F, 8.0F, 2.0F, 8.0F), class_5603.method_32090(-8.0F, -12.0F, -8.0F)
		);
		return class_5607.method_32110(lv, 32, 32);
	}

	public void render(BellBlockEntity bellBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
		float g = (float)bellBlockEntity.ringTicks + f;
		float h = 0.0F;
		float k = 0.0F;
		if (bellBlockEntity.ringing) {
			float l = MathHelper.sin(g / (float) Math.PI) / (4.0F + g / 3.0F);
			if (bellBlockEntity.lastSideHit == Direction.NORTH) {
				h = -l;
			} else if (bellBlockEntity.lastSideHit == Direction.SOUTH) {
				h = l;
			} else if (bellBlockEntity.lastSideHit == Direction.EAST) {
				k = -l;
			} else if (bellBlockEntity.lastSideHit == Direction.WEST) {
				k = l;
			}
		}

		this.field_20816.pitch = h;
		this.field_20816.roll = k;
		VertexConsumer vertexConsumer = BELL_BODY_TEXTURE.getVertexConsumer(vertexConsumerProvider, RenderLayer::getEntitySolid);
		this.field_20816.render(matrixStack, vertexConsumer, i, j);
	}
}
