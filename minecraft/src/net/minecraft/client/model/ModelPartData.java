package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ModelPartData {
	private final List<ModelCuboidData> cuboidData;
	private final ModelTransform rotationData;
	private final Map<String, ModelPartData> children = Maps.<String, ModelPartData>newHashMap();

	ModelPartData(List<ModelCuboidData> cuboidData, ModelTransform rotationData) {
		this.cuboidData = cuboidData;
		this.rotationData = rotationData;
	}

	public ModelPartData addChild(String name, ModelPartBuilder builder, ModelTransform rotationData) {
		ModelPartData modelPartData = new ModelPartData(builder.build(), rotationData);
		ModelPartData modelPartData2 = (ModelPartData)this.children.put(name, modelPartData);
		if (modelPartData2 != null) {
			modelPartData.children.putAll(modelPartData2.children);
		}

		return modelPartData;
	}

	public ModelPart createPart(int textureWidth, int textureHeight) {
		Object2ObjectArrayMap<String, ModelPart> object2ObjectArrayMap = (Object2ObjectArrayMap<String, ModelPart>)this.children
			.entrySet()
			.stream()
			.collect(
				Collectors.toMap(
					Entry::getKey,
					entry -> ((ModelPartData)entry.getValue()).createPart(textureWidth, textureHeight),
					(modelPartx, modelPart2) -> modelPartx,
					Object2ObjectArrayMap::new
				)
			);
		List<ModelPart.Cuboid> list = (List<ModelPart.Cuboid>)this.cuboidData
			.stream()
			.map(modelCuboidData -> modelCuboidData.createCuboid(textureWidth, textureHeight))
			.collect(ImmutableList.toImmutableList());
		ModelPart modelPart = new ModelPart(list, object2ObjectArrayMap);
		modelPart.setDefaultTransform(this.rotationData);
		modelPart.setTransform(this.rotationData);
		return modelPart;
	}

	public ModelPartData getChild(String name) {
		return (ModelPartData)this.children.get(name);
	}
}
