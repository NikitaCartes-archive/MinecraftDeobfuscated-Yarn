package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ModelData {
	private final ModelPartData data = new ModelPartData(ImmutableList.of(), ModelTransform.NONE);

	public ModelPartData getRoot() {
		return this.data;
	}
}
