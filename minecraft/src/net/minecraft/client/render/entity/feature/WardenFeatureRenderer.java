package net.minecraft.client.render.entity.feature;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.WardenEntityModel;
import net.minecraft.client.render.entity.state.WardenEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class WardenFeatureRenderer extends FeatureRenderer<WardenEntityRenderState, WardenEntityModel> {
	private final Identifier texture;
	private final WardenFeatureRenderer.AnimationAngleAdjuster animationAngleAdjuster;
	private final WardenFeatureRenderer.ModelPartVisibility modelPartVisibility;

	public WardenFeatureRenderer(
		FeatureRendererContext<WardenEntityRenderState, WardenEntityModel> context,
		Identifier texture,
		WardenFeatureRenderer.AnimationAngleAdjuster animationAngleAdjuster,
		WardenFeatureRenderer.ModelPartVisibility modelPartVisibility
	) {
		super(context);
		this.texture = texture;
		this.animationAngleAdjuster = animationAngleAdjuster;
		this.modelPartVisibility = modelPartVisibility;
	}

	public void render(
		MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, WardenEntityRenderState wardenEntityRenderState, float f, float g
	) {
		if (!wardenEntityRenderState.invisible) {
			this.updateModelPartVisibility();
			VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucentEmissive(this.texture));
			float h = this.animationAngleAdjuster.apply(wardenEntityRenderState, wardenEntityRenderState.age);
			int j = ColorHelper.getArgb(MathHelper.floor(h * 255.0F), 255, 255, 255);
			this.getContextModel().render(matrixStack, vertexConsumer, i, LivingEntityRenderer.getOverlay(wardenEntityRenderState, 0.0F), j);
			this.unhideAllModelParts();
		}
	}

	private void updateModelPartVisibility() {
		List<ModelPart> list = this.modelPartVisibility.getPartsToDraw(this.getContextModel());
		this.getContextModel().getParts().forEach(part -> part.hidden = true);
		list.forEach(part -> part.hidden = false);
	}

	private void unhideAllModelParts() {
		this.getContextModel().getParts().forEach(part -> part.hidden = false);
	}

	@Environment(EnvType.CLIENT)
	public interface AnimationAngleAdjuster {
		float apply(WardenEntityRenderState state, float tickDelta);
	}

	@Environment(EnvType.CLIENT)
	public interface ModelPartVisibility {
		List<ModelPart> getPartsToDraw(WardenEntityModel model);
	}
}
