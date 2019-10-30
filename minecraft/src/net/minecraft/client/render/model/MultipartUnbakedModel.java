package net.minecraft.client.render.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.json.ModelVariantMap;
import net.minecraft.client.render.model.json.MultipartModelComponent;
import net.minecraft.client.render.model.json.WeightedUnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.state.StateManager;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class MultipartUnbakedModel implements UnbakedModel {
	private final StateManager<Block, BlockState> stateFactory;
	private final List<MultipartModelComponent> components;

	public MultipartUnbakedModel(StateManager<Block, BlockState> stateFactory, List<MultipartModelComponent> components) {
		this.stateFactory = stateFactory;
		this.components = components;
	}

	public List<MultipartModelComponent> getComponents() {
		return this.components;
	}

	public Set<WeightedUnbakedModel> getModels() {
		Set<WeightedUnbakedModel> set = Sets.<WeightedUnbakedModel>newHashSet();

		for (MultipartModelComponent multipartModelComponent : this.components) {
			set.add(multipartModelComponent.getModel());
		}

		return set;
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (!(o instanceof MultipartUnbakedModel)) {
			return false;
		} else {
			MultipartUnbakedModel multipartUnbakedModel = (MultipartUnbakedModel)o;
			return Objects.equals(this.stateFactory, multipartUnbakedModel.stateFactory) && Objects.equals(this.components, multipartUnbakedModel.components);
		}
	}

	public int hashCode() {
		return Objects.hash(new Object[]{this.stateFactory, this.components});
	}

	@Override
	public Collection<Identifier> getModelDependencies() {
		return (Collection<Identifier>)this.getComponents()
			.stream()
			.flatMap(multipartModelComponent -> multipartModelComponent.getModel().getModelDependencies().stream())
			.collect(Collectors.toSet());
	}

	@Override
	public Collection<Identifier> getTextureDependencies(Function<Identifier, UnbakedModel> unbakedModelGetter, Set<String> unresolvedTextureReferences) {
		return (Collection<Identifier>)this.getComponents()
			.stream()
			.flatMap(multipartModelComponent -> multipartModelComponent.getModel().getTextureDependencies(unbakedModelGetter, unresolvedTextureReferences).stream())
			.collect(Collectors.toSet());
	}

	@Nullable
	@Override
	public BakedModel bake(ModelLoader loader, Function<Identifier, Sprite> textureGetter, ModelBakeSettings rotationContainer, Identifier identifier) {
		MultipartBakedModel.Builder builder = new MultipartBakedModel.Builder();

		for (MultipartModelComponent multipartModelComponent : this.getComponents()) {
			BakedModel bakedModel = multipartModelComponent.getModel().bake(loader, textureGetter, rotationContainer, identifier);
			if (bakedModel != null) {
				builder.addComponent(multipartModelComponent.getPredicate(this.stateFactory), bakedModel);
			}
		}

		return builder.build();
	}

	@Environment(EnvType.CLIENT)
	public static class Deserializer implements JsonDeserializer<MultipartUnbakedModel> {
		private final ModelVariantMap.DeserializationContext context;

		public Deserializer(ModelVariantMap.DeserializationContext context) {
			this.context = context;
		}

		public MultipartUnbakedModel method_3523(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
			return new MultipartUnbakedModel(this.context.getStateFactory(), this.deserializeComponents(context, element.getAsJsonArray()));
		}

		private List<MultipartModelComponent> deserializeComponents(JsonDeserializationContext context, JsonArray array) {
			List<MultipartModelComponent> list = Lists.<MultipartModelComponent>newArrayList();

			for (JsonElement jsonElement : array) {
				list.add(context.deserialize(jsonElement, MultipartModelComponent.class));
			}

			return list;
		}
	}
}
