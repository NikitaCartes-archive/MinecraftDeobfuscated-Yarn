package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import java.util.function.UnaryOperator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ModelData {
	private final ModelPartData data;

	public ModelData() {
		this(new ModelPartData(ImmutableList.of(), ModelTransform.NONE));
	}

	private ModelData(ModelPartData data) {
		this.data = data;
	}

	public ModelPartData getRoot() {
		return this.data;
	}

	public ModelData transform(UnaryOperator<ModelTransform> transformer) {
		return new ModelData(this.data.applyTransformer(transformer));
	}
}
