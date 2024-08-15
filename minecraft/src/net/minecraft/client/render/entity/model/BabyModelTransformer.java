package net.minecraft.client.render.entity.model;

import java.util.Set;
import java.util.Map.Entry;
import java.util.function.UnaryOperator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;

@Environment(EnvType.CLIENT)
public record BabyModelTransformer(
	boolean scaleHead, float babyYHeadOffset, float babyZHeadOffset, float babyHeadScale, float babyBodyScale, float bodyYOffset, Set<String> headParts
) implements ModelTransformer {
	public BabyModelTransformer(Set<String> headParts) {
		this(false, 5.0F, 2.0F, headParts);
	}

	public BabyModelTransformer(boolean scaleHead, float babyYHeadOffset, float babyZHeadOffset, Set<String> headParts) {
		this(scaleHead, babyYHeadOffset, babyZHeadOffset, 2.0F, 2.0F, 24.0F, headParts);
	}

	@Override
	public ModelData apply(ModelData modelData) {
		float f = this.scaleHead ? 1.5F / this.babyHeadScale : 1.0F;
		float g = 1.0F / this.babyBodyScale;
		UnaryOperator<ModelTransform> unaryOperator = modelTransform -> modelTransform.addPivot(0.0F, this.babyYHeadOffset, this.babyZHeadOffset).scaled(f);
		UnaryOperator<ModelTransform> unaryOperator2 = modelTransform -> modelTransform.addPivot(0.0F, this.bodyYOffset, 0.0F).scaled(g);
		ModelData modelData2 = new ModelData();

		for (Entry<String, ModelPartData> entry : modelData.getRoot().getChildren()) {
			String string = (String)entry.getKey();
			ModelPartData modelPartData = (ModelPartData)entry.getValue();
			modelData2.getRoot().addChild(string, modelPartData.applyTransformer(this.headParts.contains(string) ? unaryOperator : unaryOperator2));
		}

		return modelData2;
	}
}
