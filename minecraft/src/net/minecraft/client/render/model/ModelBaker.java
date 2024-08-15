package net.minecraft.client.render.model;

import com.mojang.logging.LogUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.model.json.ItemModelGenerator;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.AffineTransformation;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class ModelBaker {
	public static final SpriteIdentifier FIRE_0 = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, Identifier.ofVanilla("block/fire_0"));
	public static final SpriteIdentifier FIRE_1 = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, Identifier.ofVanilla("block/fire_1"));
	public static final SpriteIdentifier LAVA_FLOW = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, Identifier.ofVanilla("block/lava_flow"));
	public static final SpriteIdentifier WATER_FLOW = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, Identifier.ofVanilla("block/water_flow"));
	public static final SpriteIdentifier WATER_OVERLAY = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, Identifier.ofVanilla("block/water_overlay"));
	public static final SpriteIdentifier BANNER_BASE = new SpriteIdentifier(
		TexturedRenderLayers.BANNER_PATTERNS_ATLAS_TEXTURE, Identifier.ofVanilla("entity/banner_base")
	);
	public static final SpriteIdentifier SHIELD_BASE = new SpriteIdentifier(
		TexturedRenderLayers.SHIELD_PATTERNS_ATLAS_TEXTURE, Identifier.ofVanilla("entity/shield_base")
	);
	public static final SpriteIdentifier SHIELD_BASE_NO_PATTERN = new SpriteIdentifier(
		TexturedRenderLayers.SHIELD_PATTERNS_ATLAS_TEXTURE, Identifier.ofVanilla("entity/shield_base_nopattern")
	);
	public static final int field_32983 = 10;
	public static final List<Identifier> BLOCK_DESTRUCTION_STAGES = (List<Identifier>)IntStream.range(0, 10)
		.mapToObj(stage -> Identifier.ofVanilla("block/destroy_stage_" + stage))
		.collect(Collectors.toList());
	public static final List<Identifier> BLOCK_DESTRUCTION_STAGE_TEXTURES = (List<Identifier>)BLOCK_DESTRUCTION_STAGES.stream()
		.map(id -> id.withPath((UnaryOperator<String>)(path -> "textures/" + path + ".png")))
		.collect(Collectors.toList());
	public static final List<RenderLayer> BLOCK_DESTRUCTION_RENDER_LAYERS = (List<RenderLayer>)BLOCK_DESTRUCTION_STAGE_TEXTURES.stream()
		.map(RenderLayer::getBlockBreaking)
		.collect(Collectors.toList());
	static final Logger LOGGER = LogUtils.getLogger();
	static final ItemModelGenerator ITEM_MODEL_GENERATOR = new ItemModelGenerator();
	final Map<ModelBaker.BakedModelCacheKey, BakedModel> bakedModelCache = new HashMap();
	private final Map<ModelIdentifier, BakedModel> bakedModels = new HashMap();
	private final Map<ModelIdentifier, UnbakedModel> models;
	final Map<Identifier, UnbakedModel> field_53663;
	final UnbakedModel missingModel;

	public ModelBaker(Map<ModelIdentifier, UnbakedModel> models, Map<Identifier, UnbakedModel> map, UnbakedModel missingModel) {
		this.models = models;
		this.field_53663 = map;
		this.missingModel = missingModel;
	}

	public void bake(ModelBaker.SpriteGetter spriteGetter) {
		this.models.forEach((id, model) -> {
			BakedModel bakedModel = null;

			try {
				bakedModel = new ModelBaker.BakerImpl(spriteGetter, id).bake(model, ModelRotation.X0_Y0);
			} catch (Exception var6) {
				LOGGER.warn("Unable to bake model: '{}': {}", id, var6);
			}

			if (bakedModel != null) {
				this.bakedModels.put(id, bakedModel);
			}
		});
	}

	public Map<ModelIdentifier, BakedModel> getBakedModels() {
		return this.bakedModels;
	}

	@Environment(EnvType.CLIENT)
	static record BakedModelCacheKey(Identifier id, AffineTransformation transformation, boolean isUvLocked) {
	}

	@Environment(EnvType.CLIENT)
	class BakerImpl implements Baker {
		private final Function<SpriteIdentifier, Sprite> textureGetter;

		BakerImpl(final ModelBaker.SpriteGetter spriteGetter, final ModelIdentifier modelIdentifier) {
			this.textureGetter = spriteId -> spriteGetter.get(modelIdentifier, spriteId);
		}

		@Override
		public UnbakedModel getModel(Identifier id) {
			UnbakedModel unbakedModel = (UnbakedModel)ModelBaker.this.field_53663.get(id);
			if (unbakedModel == null) {
				ModelBaker.LOGGER.warn("Requested a model that was not discovered previously: {}", id);
				return ModelBaker.this.missingModel;
			} else {
				return unbakedModel;
			}
		}

		@Override
		public BakedModel bake(Identifier id, ModelBakeSettings settings) {
			ModelBaker.BakedModelCacheKey bakedModelCacheKey = new ModelBaker.BakedModelCacheKey(id, settings.getRotation(), settings.isUvLocked());
			BakedModel bakedModel = (BakedModel)ModelBaker.this.bakedModelCache.get(bakedModelCacheKey);
			if (bakedModel != null) {
				return bakedModel;
			} else {
				UnbakedModel unbakedModel = this.getModel(id);
				BakedModel bakedModel2 = this.bake(unbakedModel, settings);
				ModelBaker.this.bakedModelCache.put(bakedModelCacheKey, bakedModel2);
				return bakedModel2;
			}
		}

		@Nullable
		BakedModel bake(UnbakedModel model, ModelBakeSettings settings) {
			if (model instanceof JsonUnbakedModel jsonUnbakedModel && jsonUnbakedModel.getRootModel() == Models.GENERATION_MARKER) {
				return ModelBaker.ITEM_MODEL_GENERATOR.create(this.textureGetter, jsonUnbakedModel).bake(this, jsonUnbakedModel, this.textureGetter, settings, false);
			}

			return model.bake(this, this.textureGetter, settings);
		}
	}

	@FunctionalInterface
	@Environment(EnvType.CLIENT)
	public interface SpriteGetter {
		Sprite get(ModelIdentifier modelId, SpriteIdentifier spriteId);
	}
}
