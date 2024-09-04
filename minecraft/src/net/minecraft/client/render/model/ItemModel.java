package net.minecraft.client.render.model;

import java.util.List;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelOverride;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ItemModel implements UnbakedModel {
	private final Identifier id;
	private List<ModelOverride> overrides = List.of();

	public ItemModel(Identifier id) {
		this.id = id;
	}

	@Override
	public void resolve(UnbakedModel.Resolver resolver) {
		if (resolver.resolve(this.id) instanceof JsonUnbakedModel jsonUnbakedModel) {
			this.overrides = jsonUnbakedModel.getOverrides();
			this.overrides.forEach(override -> resolver.resolve(override.modelId()));
		}
	}

	@Override
	public BakedModel bake(Baker baker, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer) {
		BakedModel bakedModel = baker.bake(this.id, rotationContainer);
		if (this.overrides.isEmpty()) {
			return bakedModel;
		} else {
			ModelOverrideList modelOverrideList = new ModelOverrideList(baker, this.overrides);
			return new ItemModel.BakedItemModel(bakedModel, modelOverrideList);
		}
	}

	@Environment(EnvType.CLIENT)
	static class BakedItemModel extends WrapperBakedModel {
		private final ModelOverrideList overrides;

		public BakedItemModel(BakedModel wrapped, ModelOverrideList overrides) {
			super(wrapped);
			this.overrides = overrides;
		}

		@Override
		public ModelOverrideList getOverrides() {
			return this.overrides;
		}
	}
}
