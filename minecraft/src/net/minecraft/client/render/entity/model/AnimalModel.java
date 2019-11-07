package net.minecraft.client.render.entity.model;

import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public abstract class AnimalModel<E extends Entity> extends EntityModel<E> {
	private final boolean headScaled;
	private final float childHeadYOffset;
	private final float childHeadZOffset;
	private final float invertedChildHeadScale;
	private final float invertedChildBodyScale;
	private final float childBodyYOffset;

	protected AnimalModel(boolean headScaled, float childHeadYOffset, float childHeadZOffset) {
		this(headScaled, childHeadYOffset, childHeadZOffset, 2.0F, 2.0F, 24.0F);
	}

	protected AnimalModel(
		boolean headScaled, float childHeadYOffset, float childHeadZOffset, float invertedChildHeadScale, float invertedChildBodyScale, float childBodyYOffset
	) {
		this(RenderLayer::getEntityCutoutNoCull, headScaled, childHeadYOffset, childHeadZOffset, invertedChildHeadScale, invertedChildBodyScale, childBodyYOffset);
	}

	protected AnimalModel(
		Function<Identifier, RenderLayer> function,
		boolean headScaled,
		float childHeadYOffset,
		float childHeadZOffset,
		float invertedChildHeadScale,
		float invertedChildBodyScale,
		float childBodyYOffset
	) {
		super(function);
		this.headScaled = headScaled;
		this.childHeadYOffset = childHeadYOffset;
		this.childHeadZOffset = childHeadZOffset;
		this.invertedChildHeadScale = invertedChildHeadScale;
		this.invertedChildBodyScale = invertedChildBodyScale;
		this.childBodyYOffset = childBodyYOffset;
	}

	protected AnimalModel() {
		this(false, 5.0F, 2.0F);
	}

	@Override
	public void render(MatrixStack matrixStack, VertexConsumer vertexConsumer, int i, int j, float r, float g, float b) {
		if (this.isChild) {
			matrixStack.push();
			if (this.headScaled) {
				float f = 1.5F / this.invertedChildHeadScale;
				matrixStack.scale(f, f, f);
			}

			matrixStack.translate(0.0, (double)(this.childHeadYOffset / 16.0F), (double)(this.childHeadZOffset / 16.0F));
			this.getHeadParts().forEach(modelPart -> modelPart.render(matrixStack, vertexConsumer, i, j, null, r, g, b));
			matrixStack.pop();
			matrixStack.push();
			float f = 1.0F / this.invertedChildBodyScale;
			matrixStack.scale(f, f, f);
			matrixStack.translate(0.0, (double)(this.childBodyYOffset / 16.0F), 0.0);
			this.getBodyParts().forEach(modelPart -> modelPart.render(matrixStack, vertexConsumer, i, j, null, r, g, b));
			matrixStack.pop();
		} else {
			this.getHeadParts().forEach(modelPart -> modelPart.render(matrixStack, vertexConsumer, i, j, null, r, g, b));
			this.getBodyParts().forEach(modelPart -> modelPart.render(matrixStack, vertexConsumer, i, j, null, r, g, b));
		}
	}

	protected abstract Iterable<ModelPart> getHeadParts();

	protected abstract Iterable<ModelPart> getBodyParts();
}
