package net.minecraft.client.render.block;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Map.Entry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

@Environment(EnvType.CLIENT)
public class BlockModels {
	private final Map<BlockState, BakedModel> models = Maps.<BlockState, BakedModel>newIdentityHashMap();
	private final BakedModelManager field_4163;

	public BlockModels(BakedModelManager bakedModelManager) {
		this.field_4163 = bakedModelManager;
	}

	public Sprite method_3339(BlockState blockState) {
		return this.method_3335(blockState).getSprite();
	}

	public BakedModel method_3335(BlockState blockState) {
		BakedModel bakedModel = (BakedModel)this.models.get(blockState);
		if (bakedModel == null) {
			bakedModel = this.field_4163.getMissingModel();
		}

		return bakedModel;
	}

	public BakedModelManager method_3333() {
		return this.field_4163;
	}

	public void reload() {
		this.models.clear();

		for (Block block : Registry.BLOCK) {
			block.method_9595().getStates().forEach(blockState -> {
				BakedModel var10000 = (BakedModel)this.models.put(blockState, this.field_4163.method_4742(method_3340(blockState)));
			});
		}
	}

	public static ModelIdentifier method_3340(BlockState blockState) {
		return method_3336(Registry.BLOCK.method_10221(blockState.getBlock()), blockState);
	}

	public static ModelIdentifier method_3336(Identifier identifier, BlockState blockState) {
		return new ModelIdentifier(identifier, propertyMapToString(blockState.getEntries()));
	}

	public static String propertyMapToString(Map<Property<?>, Comparable<?>> map) {
		StringBuilder stringBuilder = new StringBuilder();

		for (Entry<Property<?>, Comparable<?>> entry : map.entrySet()) {
			if (stringBuilder.length() != 0) {
				stringBuilder.append(',');
			}

			Property<?> property = (Property<?>)entry.getKey();
			stringBuilder.append(property.getName());
			stringBuilder.append('=');
			stringBuilder.append(propertyValueToString(property, (Comparable<?>)entry.getValue()));
		}

		return stringBuilder.toString();
	}

	private static <T extends Comparable<T>> String propertyValueToString(Property<T> property, Comparable<?> comparable) {
		return property.getValueAsString((T)comparable);
	}
}
