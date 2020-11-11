package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloadListener;

@Environment(EnvType.CLIENT)
public class EntityModelLoader implements SynchronousResourceReloadListener {
	private Map<EntityModelLayer, ModelPart> modelParts = ImmutableMap.of();

	public ModelPart getModelPart(EntityModelLayer layer) {
		ModelPart modelPart = (ModelPart)this.modelParts.get(layer);
		if (modelPart == null) {
			throw new IllegalArgumentException("No model for layer " + layer);
		} else {
			return modelPart;
		}
	}

	@Override
	public void apply(ResourceManager manager) {
		this.modelParts = ImmutableMap.copyOf(EntityModels.getModels());
	}
}
