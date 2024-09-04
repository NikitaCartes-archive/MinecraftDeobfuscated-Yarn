package net.minecraft.client.render.model.json;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.GroupableModel;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.WeightedBakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.collection.DataPool;

@Environment(EnvType.CLIENT)
public record WeightedUnbakedModel(List<ModelVariant> variants) implements GroupableModel {
	public WeightedUnbakedModel(List<ModelVariant> variants) {
		if (variants.isEmpty()) {
			throw new IllegalArgumentException("Variant list must contain at least one element");
		} else {
			this.variants = variants;
		}
	}

	@Override
	public Object getEqualityGroup(BlockState state) {
		return this;
	}

	@Override
	public void resolve(UnbakedModel.Resolver resolver) {
		this.variants.forEach(variant -> resolver.resolve(variant.getLocation()));
	}

	@Override
	public BakedModel bake(Baker baker, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer) {
		if (this.variants.size() == 1) {
			ModelVariant modelVariant = (ModelVariant)this.variants.getFirst();
			return baker.bake(modelVariant.getLocation(), modelVariant);
		} else {
			DataPool.Builder<BakedModel> builder = DataPool.builder();

			for (ModelVariant modelVariant2 : this.variants) {
				BakedModel bakedModel = baker.bake(modelVariant2.getLocation(), modelVariant2);
				builder.add(bakedModel, modelVariant2.getWeight());
			}

			return new WeightedBakedModel(builder.build());
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Deserializer implements JsonDeserializer<WeightedUnbakedModel> {
		public WeightedUnbakedModel deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			List<ModelVariant> list = Lists.<ModelVariant>newArrayList();
			if (jsonElement.isJsonArray()) {
				JsonArray jsonArray = jsonElement.getAsJsonArray();
				if (jsonArray.isEmpty()) {
					throw new JsonParseException("Empty variant array");
				}

				for (JsonElement jsonElement2 : jsonArray) {
					list.add((ModelVariant)jsonDeserializationContext.deserialize(jsonElement2, ModelVariant.class));
				}
			} else {
				list.add((ModelVariant)jsonDeserializationContext.deserialize(jsonElement, ModelVariant.class));
			}

			return new WeightedUnbakedModel(list);
		}
	}
}
