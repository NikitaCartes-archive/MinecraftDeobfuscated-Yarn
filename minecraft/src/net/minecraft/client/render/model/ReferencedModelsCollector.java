package net.minecraft.client.render.model;

import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.item.BundleItem;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class ReferencedModelsCollector {
	static final Logger LOGGER = LogUtils.getLogger();
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

	private void addInventoryItem(Identifier id) {
		ModelIdentifier modelIdentifier = ModelIdentifier.ofInventoryVariant(id);
		Identifier identifier = id.withPrefixedPath("item/");
		UnbakedModel unbakedModel = this.computeResolvedModel(identifier);
		this.addTopLevelModel(modelIdentifier, unbakedModel);
	}

	private void addItem(ModelIdentifier id) {
		Identifier identifier = id.id().withPrefixedPath("item/");
		UnbakedModel unbakedModel = this.computeResolvedModel(identifier);
		this.addTopLevelModel(id, unbakedModel);
	}

	private void addTopLevelModel(ModelIdentifier modelId, UnbakedModel model) {
		this.topLevelModels.put(modelId, model);
	}

	public void addBlockStates(BlockStatesLoader.BlockStateDefinition definition) {
		this.resolvedModels.put(Models.GENERATED, Models.GENERATION_MARKER);
		this.resolvedModels.put(Models.ENTITY, Models.BLOCK_ENTITY_MARKER);
		definition.models().forEach((modelId, model) -> this.addTopLevelModel(modelId, model.model()));

		for (Identifier identifier : Registries.ITEM.getIds()) {
			this.addInventoryItem(identifier);
		}

		this.addItem(ItemRenderer.TRIDENT_IN_HAND);
		this.addItem(ItemRenderer.SPYGLASS_IN_HAND);
		this.addItem(ItemRenderer.getBundleOpenFrontModelId((BundleItem)Items.BUNDLE));
		this.addItem(ItemRenderer.getBundleOpenBackModelId((BundleItem)Items.BUNDLE));
	}

	public void resolveAll() {
		this.topLevelModels.values().forEach(model -> model.resolve(new ReferencedModelsCollector.ResolverImpl(), UnbakedModel.ModelType.TOP));
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
		private UnbakedModel.ModelType currentlyResolvingType = UnbakedModel.ModelType.TOP;

		@Override
		public UnbakedModel resolve(Identifier id) {
			return this.resolve(id, false);
		}

		@Override
		public UnbakedModel resolveOverride(Identifier id) {
			if (this.currentlyResolvingType == UnbakedModel.ModelType.OVERRIDE) {
				ReferencedModelsCollector.LOGGER.warn("Re-entrant override in {}->{}", this.getPath(), id);
			}

			this.currentlyResolvingType = UnbakedModel.ModelType.OVERRIDE;
			UnbakedModel unbakedModel = this.resolve(id, true);
			this.currentlyResolvingType = UnbakedModel.ModelType.TOP;
			return unbakedModel;
		}

		private boolean isRecursive(Identifier id, boolean override) {
			if (this.stack.isEmpty()) {
				return false;
			} else if (!this.stack.contains(id)) {
				return false;
			} else if (override) {
				Identifier identifier = (Identifier)this.stack.getLast();
				return !identifier.equals(id);
			} else {
				return true;
			}
		}

		private UnbakedModel resolve(Identifier id, boolean override) {
			if (this.isRecursive(id, override)) {
				ReferencedModelsCollector.LOGGER.warn("Detected model loading loop: {}->{}", this.getPath(), id);
				return ReferencedModelsCollector.this.missingModel;
			} else {
				UnbakedModel unbakedModel = ReferencedModelsCollector.this.computeResolvedModel(id);
				if (this.visited.add(id)) {
					this.stack.add(id);
					unbakedModel.resolve(this, this.currentlyResolvingType);
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
