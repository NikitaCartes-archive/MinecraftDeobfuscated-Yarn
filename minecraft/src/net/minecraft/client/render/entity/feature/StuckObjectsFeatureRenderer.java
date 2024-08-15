package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.random.Random;

@Environment(EnvType.CLIENT)
public abstract class StuckObjectsFeatureRenderer<M extends PlayerEntityModel> extends FeatureRenderer<PlayerEntityRenderState, M> {
	private final Model model;
	private final Identifier texture;
	private final StuckObjectsFeatureRenderer.RenderPosition renderPosition;

	public StuckObjectsFeatureRenderer(
		LivingEntityRenderer<?, PlayerEntityRenderState, M> entityRenderer,
		Model model,
		Identifier texture,
		StuckObjectsFeatureRenderer.RenderPosition renderPosition
	) {
		super(entityRenderer);
		this.model = model;
		this.texture = texture;
		this.renderPosition = renderPosition;
	}

	protected abstract int getObjectCount(PlayerEntityRenderState playerRenderState);

	private void renderObject(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float f, float directionX, float directionY) {
		float g = MathHelper.sqrt(f * f + directionY * directionY);
		float h = (float)(Math.atan2((double)f, (double)directionY) * 180.0F / (float)Math.PI);
		float i = (float)(Math.atan2((double)directionX, (double)g) * 180.0F / (float)Math.PI);
		matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(h - 90.0F));
		matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(i));
		this.model.render(matrices, vertexConsumers.getBuffer(this.model.getLayer(this.texture)), light, OverlayTexture.DEFAULT_UV);
	}

	public void render(
		MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, PlayerEntityRenderState playerEntityRenderState, float f, float g
	) {
		int j = this.getObjectCount(playerEntityRenderState);
		if (j > 0) {
			Random random = Random.create((long)playerEntityRenderState.id);

			for (int k = 0; k < j; k++) {
				matrixStack.push();
				ModelPart modelPart = this.getContextModel().getRandomPart(random);
				ModelPart.Cuboid cuboid = modelPart.getRandomCuboid(random);
				modelPart.rotate(matrixStack);
				float h = random.nextFloat();
				float l = random.nextFloat();
				float m = random.nextFloat();
				if (this.renderPosition == StuckObjectsFeatureRenderer.RenderPosition.ON_SURFACE) {
					int n = random.nextInt(3);
					switch (n) {
						case 0:
							h = method_62597(h);
							break;
						case 1:
							l = method_62597(l);
							break;
						default:
							m = method_62597(m);
					}
				}

				matrixStack.translate(
					MathHelper.lerp(h, cuboid.minX, cuboid.maxX) / 16.0F,
					MathHelper.lerp(l, cuboid.minY, cuboid.maxY) / 16.0F,
					MathHelper.lerp(m, cuboid.minZ, cuboid.maxZ) / 16.0F
				);
				this.renderObject(matrixStack, vertexConsumerProvider, i, -(h * 2.0F - 1.0F), -(l * 2.0F - 1.0F), -(m * 2.0F - 1.0F));
				matrixStack.pop();
			}
		}
	}

	private static float method_62597(float f) {
		return f > 0.5F ? 1.0F : 0.5F;
	}

	@Environment(EnvType.CLIENT)
	public static enum RenderPosition {
		IN_CUBE,
		ON_SURFACE;
	}
}
