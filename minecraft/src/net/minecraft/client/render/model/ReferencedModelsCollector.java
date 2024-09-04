package net.minecraft.client.render.model;

import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.BundleItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class ReferencedModelsCollector {
	static final Logger LOGGER = LogUtils.getLogger();
	public static final String ITEM_DIRECTORY = "item/";
	private final Map<Identifier, UnbakedModel> inputs;
	final UnbakedModel missingModel;
	private final Map<ModelIdentifier, UnbakedModel> topLevelModels = new HashMap();
	private final Map<Identifier, UnbakedModel> resolvedModels = new HashMap();

	public ReferencedModelsCollector(Map<Identifier, UnbakedModel> inputs, UnbakedModel missingModel) {
		this.inputs = inputs;
		this.missingModel = missingModel;
		this.addTopLevelModel(MissingModel.MODEL_ID, missingModel);
		this.resolvedModels.put(MissingModel.ID, missingModel);
	}

	private static Set<ModelIdentifier> getRequiredModels() {
		Set<ModelIdentifier> set = new HashSet();
		Registries.ITEM.streamEntries().forEach(entry -> {
			Identifier identifier = ((Item)entry.value()).getComponents().get(DataComponentTypes.ITEM_MODEL);
			if (identifier != null) {
				set.add(ModelIdentifier.ofInventoryVariant(identifier));
			}

			if (entry.value() instanceof BundleItem bundleItem) {
				set.add(ModelIdentifier.ofInventoryVariant(bundleItem.getOpenFrontTexture()));
				set.add(ModelIdentifier.ofInventoryVariant(bundleItem.getOpenBackTexture()));
			}
		});
		set.add(ItemRenderer.TRIDENT);
		set.add(ItemRenderer.SPYGLASS);
		return set;
	}

	private void addTopLevelModel(ModelIdentifier modelId, UnbakedModel model) {
		this.topLevelModels.put(modelId, model);
	}

	public void addBlockStates(BlockStatesLoader.BlockStateDefinition definition) {
		this.resolvedModels.put(Models.GENERATED, Models.GENERATION_MARKER);
		this.resolvedModels.put(Models.ENTITY, Models.BLOCK_ENTITY_MARKER);
		Set<ModelIdentifier> set = getRequiredModels();
		definition.models().forEach((id, model) -> {
			this.addTopLevelModel(id, model.model());
			set.remove(id);
		});
		this.inputs.keySet().forEach(id -> {
			if (id.getPath().startsWith("item/")) {
				ModelIdentifier modelIdentifier = ModelIdentifier.ofInventoryVariant(id.withPath((UnaryOperator<String>)(path -> path.substring("item/".length()))));
				this.addTopLevelModel(modelIdentifier, new ItemModel(id));
				set.remove(modelIdentifier);
			}
		});
		if (!set.isEmpty()) {
			LOGGER.warn("Missing mandatory models: {}", set.stream().map(id -> "\n\t" + id).collect(Collectors.joining()));
		}
	}

	public void resolveAll() {
		this.topLevelModels.values().forEach(model -> model.resolve(new ReferencedModelsCollector.ResolverImpl()));
	}

	public Map<ModelIdentifier, UnbakedModel> getTopLevelModels() {
		return this.topLevelModels;
	}

	public Map<Identifier, UnbakedModel> getResolvedModels() {
		return this.resolvedModels;
	}

	UnbakedModel computeResolvedModel(Identifier id) {
		return (UnbakedModel)this.resolvedModels.computeIfAbsent(id, this::getModel);
	}

	private UnbakedModel getModel(Identifier id) {
		UnbakedModel unbakedModel = (UnbakedModel)this.inputs.get(id);
		if (unbakedModel == null) {
			LOGGER.warn("Missing block model: '{}'", id);
			return this.missingModel;
		} else {
			return unbakedModel;
		}
	}

	@Environment(EnvType.CLIENT)
	class ResolverImpl implements UnbakedModel.Resolver {
		private final List<Identifier> stack = new ArrayList();
		private final Set<Identifier> visited = new HashSet();

		@Override
		public UnbakedModel resolve(Identifier id) {
			if (this.stack.contains(id)) {
				ReferencedModelsCollector.LOGGER.warn("Detected model loading loop: {}->{}", this.getPath(), id);
				return ReferencedModelsCollector.this.missingModel;
			} else {
				UnbakedModel unbakedModel = ReferencedModelsCollector.this.computeResolvedModel(id);
				if (this.visited.add(id)) {
					this.stack.add(id);
					unbakedModel.resolve(this);
					this.stack.remove(id);
				}

				return unbakedModel;
			}
		}

		private String getPath() {
			return (String)this.stack.stream().map(Identifier::toString).collect(Collectors.joining("->"));
		}
	}
}
