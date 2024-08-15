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
	private final List<MultipartUnbakedModel.class_9983> components;

	MultipartUnbakedModel(List<MultipartUnbakedModel.class_9983> list) {
		this.components = list;
	}

	@Override
	public Object getEqualityGroup(BlockState state) {
		IntList intList = new IntArrayList();

		for (int i = 0; i < this.components.size(); i++) {
			if (((MultipartUnbakedModel.class_9983)this.components.get(i)).predicate.test(state)) {
				intList.add(i);
			}
		}

		@Environment(EnvType.CLIENT)
		record class_9981(MultipartUnbakedModel model, IntList selectors) {
			class_9981(IntList selectors) {
				this.selectors = selectors;
			}
		}

		return new class_9981(intList);
	}

	@Override
	public void resolve(UnbakedModel.Resolver resolver, UnbakedModel.ModelType currentlyResolvingType) {
		this.components.forEach(arg -> arg.variant.resolve(resolver, currentlyResolvingType));
	}

	@Nullable
	@Override
	public BakedModel bake(Baker baker, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer) {
		MultipartBakedModel.Builder builder = new MultipartBakedModel.Builder();

		for (MultipartUnbakedModel.class_9983 lv : this.components) {
			BakedModel bakedModel = lv.variant.bake(baker, textureGetter, rotationContainer);
			if (bakedModel != null) {
				builder.addComponent(lv.predicate, bakedModel);
			}
		}

		return builder.build();
	}

	@Environment(EnvType.CLIENT)
	public static class Deserializer implements JsonDeserializer<MultipartUnbakedModel.class_9982> {
		public MultipartUnbakedModel.class_9982 deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			return new MultipartUnbakedModel.class_9982(this.deserializeComponents(jsonDeserializationContext, jsonElement.getAsJsonArray()));
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
	public static record class_9982(List<MultipartModelComponent> selectors) {
		public MultipartUnbakedModel method_62339(StateManager<Block, BlockState> stateManager) {
			List<MultipartUnbakedModel.class_9983> list = this.selectors
				.stream()
				.map(
					multipartModelComponent -> new MultipartUnbakedModel.class_9983(multipartModelComponent.getPredicate(stateManager), multipartModelComponent.getModel())
				)
				.toList();
			return new MultipartUnbakedModel(list);
		}

		public Set<WeightedUnbakedModel> method_62338() {
			return (Set<WeightedUnbakedModel>)this.selectors.stream().map(MultipartModelComponent::getModel).collect(Collectors.toSet());
		}
	}

	@Environment(EnvType.CLIENT)
	static record class_9983(Predicate<BlockState> predicate, WeightedUnbakedModel variant) {
	}
}
