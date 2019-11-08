package net.minecraft.client.render.model;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.fluid.FluidState;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloadListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

@Environment(EnvType.CLIENT)
public class BakedModelManager extends SinglePreparationResourceReloadListener<ModelLoader> {
	private Map<Identifier, BakedModel> modelMap;
	private final SpriteAtlasTexture spriteAtlas;
	private final BlockModels blockStateMaps;
	private final BlockColors colorMap;
	private BakedModel missingModel;
	private Object2IntMap<BlockState> stateToModelIndex;

	public BakedModelManager(SpriteAtlasTexture spriteAtlas, BlockColors colorMap) {
		this.spriteAtlas = spriteAtlas;
		this.colorMap = colorMap;
		this.blockStateMaps = new BlockModels(this);
	}

	public BakedModel getModel(ModelIdentifier id) {
		return (BakedModel)this.modelMap.getOrDefault(id, this.missingModel);
	}

	public BakedModel getMissingModel() {
		return this.missingModel;
	}

	public BlockModels getBlockStateMaps() {
		return this.blockStateMaps;
	}

	protected ModelLoader method_18178(ResourceManager resourceManager, Profiler profiler) {
		profiler.startTick();
		ModelLoader modelLoader = new ModelLoader(resourceManager, this.spriteAtlas, this.colorMap, profiler);
		profiler.endTick();
		return modelLoader;
	}

	protected void method_18179(ModelLoader modelLoader, ResourceManager resourceManager, Profiler profiler) {
		profiler.startTick();
		profiler.push("upload");
		modelLoader.upload(profiler);
		this.modelMap = modelLoader.getBakedModelMap();
		this.stateToModelIndex = modelLoader.getStateToModelIndex();
		this.missingModel = (BakedModel)this.modelMap.get(ModelLoader.MISSING);
		profiler.swap("cache");
		this.blockStateMaps.reload();
		profiler.pop();
		profiler.endTick();
	}

	public boolean shouldRerender(BlockState old, BlockState updated) {
		if (old == updated) {
			return false;
		} else {
			int i = this.stateToModelIndex.getInt(old);
			if (i != -1) {
				int j = this.stateToModelIndex.getInt(updated);
				if (i == j) {
					FluidState fluidState = old.getFluidState();
					FluidState fluidState2 = updated.getFluidState();
					return fluidState != fluidState2;
				}
			}

			return true;
		}
	}
}
