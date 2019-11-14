package net.minecraft.client.render.model.json;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.WeightedBakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class WeightedUnbakedModel implements UnbakedModel {
	private final List<ModelVariant> variants;

	public WeightedUnbakedModel(List<ModelVariant> variants) {
		this.variants = variants;
	}

	public List<ModelVariant> getVariants() {
		return this.variants;
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (o instanceof WeightedUnbakedModel) {
			WeightedUnbakedModel weightedUnbakedModel = (WeightedUnbakedModel)o;
			return this.variants.equals(weightedUnbakedModel.variants);
		} else {
			return false;
		}
	}

	public int hashCode() {
		return this.variants.hashCode();
	}

	@Override
	public Collection<Identifier> getModelDependencies() {
		return (Collection<Identifier>)this.getVariants().stream().map(ModelVariant::getLocation).collect(Collectors.toSet());
	}

	@Override
	public Collection<Identifier> getTextureDependencies(Function<Identifier, UnbakedModel> unbakedModelGetter, Set<String> unresolvedTextureReferences) {
		return (Collection<Identifier>)this.getVariants()
			.stream()
			.map(ModelVariant::getLocation)
			.distinct()
			.flatMap(identifier -> ((UnbakedModel)unbakedModelGetter.apply(identifier)).getTextureDependencies(unbakedModelGetter, unresolvedTextureReferences).stream())
			.collect(Collectors.toSet());
	}

	@Nullable
	@Override
	public BakedModel bake(ModelLoader loader, Function<Identifier, Sprite> textureGetter, ModelBakeSettings rotationContainer, Identifier modelId) {
		if (this.getVariants().isEmpty()) {
			return null;
		} else {
			WeightedBakedModel.Builder builder = new WeightedBakedModel.Builder();

			for (ModelVariant modelVariant : this.getVariants()) {
				BakedModel bakedModel = loader.bake(modelVariant.getLocation(), modelVariant);
				builder.add(bakedModel, modelVariant.getWeight());
			}

			return builder.getFirst();
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Deserializer implements JsonDeserializer<WeightedUnbakedModel> {
		public WeightedUnbakedModel method_3499(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
			List<ModelVariant> list = Lists.<ModelVariant>newArrayList();
			if (element.isJsonArray()) {
				JsonArray jsonArray = element.getAsJsonArray();
				if (jsonArray.size() == 0) {
					throw new JsonParseException("Empty variant array");
				}

				for (JsonElement jsonElement : jsonArray) {
					list.add(context.deserialize(jsonElement, ModelVariant.class));
				}
			} else {
				list.add(context.deserialize(element, ModelVariant.class));
			}

			return new WeightedUnbakedModel(list);
		}
	}
}
