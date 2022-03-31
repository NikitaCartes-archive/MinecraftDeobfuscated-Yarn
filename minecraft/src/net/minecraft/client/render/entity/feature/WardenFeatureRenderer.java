package net.minecraft.client.render.entity.feature;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.WardenEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class WardenFeatureRenderer<T extends WardenEntity, M extends WardenEntityModel<T>> extends FeatureRenderer<T, M> {
	private final Identifier texture;
	private final WardenFeatureRenderer.AnimationAngleAdjuster<T> animationAngleAdjuster;
	private final WardenFeatureRenderer.class_7311<T, M> field_38464;

	public WardenFeatureRenderer(
		FeatureRendererContext<T, M> context,
		Identifier texture,
		WardenFeatureRenderer.AnimationAngleAdjuster<T> animationAngleAdjuster,
		WardenFeatureRenderer.class_7311<T, M> arg
	) {
		super(context);
		this.texture = texture;
		this.animationAngleAdjuster = animationAngleAdjuster;
		this.field_38464 = arg;
	}

	public void render(
		MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T wardenEntity, float f, float g, float h, float j, float k, float l
	) {
		if (!wardenEntity.isInvisible()) {
			this.method_42746();
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
			this.method_42748();
		}
	}

	private void method_42746() {
		List<ModelPart> list = this.field_38464.getPartsToDraw(this.getContextModel());
		this.getContextModel().getPart().traverse().forEach(modelPart -> modelPart.field_38456 = true);
		list.forEach(modelPart -> modelPart.field_38456 = false);
	}

	private void method_42748() {
		this.getContextModel().getPart().traverse().forEach(modelPart -> modelPart.field_38456 = false);
	}

	@Environment(EnvType.CLIENT)
	public interface AnimationAngleAdjuster<T extends WardenEntity> {
		float apply(T warden, float tickDelta, float animationProgress);
	}

	@Environment(EnvType.CLIENT)
	public interface class_7311<T extends WardenEntity, M extends EntityModel<T>> {
		List<ModelPart> getPartsToDraw(M entityModel);
	}
}
