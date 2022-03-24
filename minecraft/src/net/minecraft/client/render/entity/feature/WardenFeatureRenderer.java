package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class WardenFeatureRenderer<T extends WardenEntity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {
	private final Identifier texture;
	private final WardenFeatureRenderer.AnimationAngleAdjuster<T> animationAngleAdjuster;

	public WardenFeatureRenderer(FeatureRendererContext<T, M> context, Identifier texture, WardenFeatureRenderer.AnimationAngleAdjuster<T> animationAngleAdjuster) {
		super(context);
		this.texture = texture;
		this.animationAngleAdjuster = animationAngleAdjuster;
	}

	public void render(
		MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T wardenEntity, float f, float g, float h, float j, float k, float l
	) {
		if (!wardenEntity.isInvisible()) {
			VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucentEmissive(this.texture));
			this.getContextModel()
				.render(
					matrixStack,
					vertexConsumer,
					i,
					LivingEntityRenderer.getOverlay(wardenEntity, 0.0F),
					1.0F,
					1.0F,
					1.0F,
					this.animationAngleAdjuster.apply(wardenEntity, h, j)
				);
		}
	}

	@Environment(EnvType.CLIENT)
	public interface AnimationAngleAdjuster<T extends WardenEntity> {
		float apply(T warden, float tickDelta, float animationProgress);
	}
}
