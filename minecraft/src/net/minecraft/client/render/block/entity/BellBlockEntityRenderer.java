package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BellBlockEntity;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class BellBlockEntityRenderer extends BlockEntityRenderer<BellBlockEntity> {
	public static final Identifier BELL_BODY_TEXTURE = new Identifier("entity/bell/bell_body");
	private final ModelPart field_20816 = new ModelPart(32, 32, 0, 0);

	public BellBlockEntityRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
		super(blockEntityRenderDispatcher);
		this.field_20816.addCuboid(-3.0F, -6.0F, -3.0F, 6.0F, 7.0F, 6.0F);
		this.field_20816.setPivot(8.0F, 12.0F, 8.0F);
		ModelPart modelPart = new ModelPart(32, 32, 0, 13);
		modelPart.addCuboid(4.0F, 4.0F, 4.0F, 8.0F, 2.0F, 8.0F);
		modelPart.setPivot(-8.0F, -12.0F, -8.0F);
		this.field_20816.addChild(modelPart);
	}

	public void method_17139(
		BellBlockEntity bellBlockEntity,
		double d,
		double e,
		double f,
		float g,
		MatrixStack matrixStack,
		LayeredVertexConsumerStorage layeredVertexConsumerStorage,
		int i
	) {
		float h = (float)bellBlockEntity.ringTicks + g;
		float j = 0.0F;
		float k = 0.0F;
		if (bellBlockEntity.isRinging) {
			float l = MathHelper.sin(h / (float) Math.PI) / (4.0F + h / 3.0F);
			if (bellBlockEntity.lastSideHit == Direction.NORTH) {
				j = -l;
			} else if (bellBlockEntity.lastSideHit == Direction.SOUTH) {
				j = l;
			} else if (bellBlockEntity.lastSideHit == Direction.EAST) {
				k = -l;
			} else if (bellBlockEntity.lastSideHit == Direction.WEST) {
				k = l;
			}
		}

		this.field_20816.pitch = j;
		this.field_20816.roll = k;
		VertexConsumer vertexConsumer = layeredVertexConsumerStorage.getBuffer(RenderLayer.SOLID);
		this.field_20816.render(matrixStack, vertexConsumer, 0.0625F, i, this.getSprite(BELL_BODY_TEXTURE));
	}
}
