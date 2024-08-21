package net.minecraft.client.render.model;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.json.MultipartModelComponent;
import net.minecraft.client.render.model.json.WeightedUnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.state.StateManager;

@Environment(EnvType.CLIENT)
public class MultipartUnbakedModel implements GroupableModel {
	private final List<MultipartUnbakedModel.Selector> selectors;

	MultipartUnbakedModel(List<MultipartUnbakedModel.Selector> selectors) {
		this.selectors = selectors;
	}

	@Override
	public Object getEqualityGroup(BlockState state) {
		IntList intList = new IntArrayList();

		for (int i = 0; i < this.selectors.size(); i++) {
			if (((MultipartUnbakedModel.Selector)this.selectors.get(i)).predicate.test(state)) {
				intList.add(i);
			}
		}

		@Environment(EnvType.CLIENT)
		record EqualityGroup(MultipartUnbakedModel model, IntList selectors) {
			EqualityGroup(IntList selectors) {
				this.selectors = selectors;
			}
		}

		return new EqualityGroup(intList);
	}

	@Override
	public void resolve(UnbakedModel.Resolver resolver, UnbakedModel.ModelType currentlyResolvingType) {
		this.selectors.forEach(selector -> selector.variant.resolve(resolver, currentlyResolvingType));
	}

	@Nullable
	@Override
	public BakedModel bake(Baker baker, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer) {
		MultipartBakedModel.Builder builder = new MultipartBakedModel.Builder();

		for (MultipartUnbakedModel.Selector selector : this.selectors) {
			BakedModel bakedModel = selector.variant.bake(baker, textureGetter, rotationContainer);
			if (bakedModel != null) {
				builder.addComponent(selector.predicate, bakedModel);
			}
		}

		return builder.build();
	}

	@Environment(EnvType.CLIENT)
	public static class Deserializer implements JsonDeserializer<MultipartUnbakedModel.Serialized> {
		public MultipartUnbakedModel.Serialized deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			return new MultipartUnbakedModel.Serialized(this.deserializeComponents(jsonDeserializationContext, jsonElement.getAsJsonArray()));
		}

		private List<MultipartModelComponent> deserializeComponents(JsonDeserializationContext context, JsonArray array) {
			List<MultipartModelComponent> list = Lists.<MultipartModelComponent>newArrayList();

			for (JsonElement jsonElement : array) {
				list.add((MultipartModelComponent)context.deserialize(jsonElement, MultipartModelComponent.class));
			}

			return list;
		}
	}

	@Environment(EnvType.CLIENT)
	static record Selector(Predicate<BlockState> predicate, WeightedUnbakedModel variant) {
	}

	@Environment(EnvType.CLIENT)
	public static record Serialized(List<MultipartModelComponent> selectors) {
		public MultipartUnbakedModel toModel(StateManager<Block, BlockState> stateManager) {
			List<MultipartUnbakedModel.Selector> list = this.selectors
				.stream()
				.map(selector -> new MultipartUnbakedModel.Selector(selector.getPredicate(stateManager), selector.getModel()))
				.toList();
			return new MultipartUnbakedModel(list);
		}

		public Set<WeightedUnbakedModel> getBackingModels() {
			return (Set<WeightedUnbakedModel>)this.selectors.stream().map(MultipartModelComponent::getModel).collect(Collectors.toSet());
		}
	}
}
