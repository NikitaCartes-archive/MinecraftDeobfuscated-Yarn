package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class DragonFireballEntityRenderer extends EntityRenderer<DragonFireballEntity, EntityRenderState> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/enderdragon/dragon_fireball.png");
	private static final RenderLayer LAYER = RenderLayer.getEntityCutoutNoCull(TEXTURE);

	public DragonFireballEntityRenderer(EntityRendererFactory.Context context) {
		super(context);
	}

	protected int getBlockLight(DragonFireballEntity dragonFireballEntity, BlockPos blockPos) {
		return 15;
	}

	@Override
	public void render(EntityRenderState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
		matrices.push();
		matrices.scale(2.0F, 2.0F, 2.0F);
		matrices.multiply(this.dispatcher.getRotation());
		MatrixStack.Entry entry = matrices.peek();
		VertexConsumer vertexConsumer = vertexConsumers.getBuffer(LAYER);
		produceVertex(vertexConsumer, entry, light, 0.0F, 0, 0, 1);
		produceVertex(vertexConsumer, entry, light, 1.0F, 0, 1, 1);
		produceVertex(vertexConsumer, entry, light, 1.0F, 1, 1, 0);
		produceVertex(vertexConsumer, entry, light, 0.0F, 1, 0, 0);
		matrices.pop();
		super.render(state, matrices, vertexConsumers, light);
	}

	private static void produceVertex(VertexConsumer vertexConsumer, MatrixStack.Entry matrix, int light, float x, int z, int textureU, int textureV) {
		vertexConsumer.vertex(matrix, x - 0.5F, (float)z - 0.25F, 0.0F)
			.color(Colors.WHITE)
			.texture((float)textureU, (float)textureV)
			.overlay(OverlayTexture.DEFAULT_UV)
			.light(light)
			.normal(matrix, 0.0F, 1.0F, 0.0F);
	}

	@Override
	public EntityRenderState getRenderState() {
		return new EntityRenderState();
	}
}
