package net.minecraft.client.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.ModelTransformer;

@Environment(EnvType.CLIENT)
public class TexturedModelData {
	private final ModelData data;
	private final TextureDimensions dimensions;

	private TexturedModelData(ModelData data, TextureDimensions dimensions) {
		this.data = data;
		this.dimensions = dimensions;
	}

	public TexturedModelData transform(ModelTransformer transformer) {
		return new TexturedModelData(transformer.apply(this.data), this.dimensions);
	}

	public ModelPart createModel() {
		return this.data.getRoot().createPart(this.dimensions.width, this.dimensions.height);
	}

	public static TexturedModelData of(ModelData partData, int textureWidth, int textureHeight) {
		return new TexturedModelData(partData, new TextureDimensions(textureWidth, textureHeight));
	}
}
