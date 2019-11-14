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
	private Map<Identifier, BakedModel> models;
	private final SpriteAtlasTexture spriteAtlas;
	private final BlockModels blockModelCache;
	private final BlockColors colorMap;
	private BakedModel missingModel;
	private Object2IntMap<BlockState> stateLookup;

	public BakedModelManager(SpriteAtlasTexture spriteAtlas, BlockColors colorMap) {
		this.spriteAtlas = spriteAtlas;
		this.colorMap = colorMap;
		this.blockModelCache = new BlockModels(this);
	}

	public BakedModel getModel(ModelIdentifier id) {
		return (BakedModel)this.models.getOrDefault(id, this.missingModel);
	}

	public BakedModel getMissingModel() {
		return this.missingModel;
	}

	public BlockModels getBlockModels() {
		return this.blockModelCache;
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
		this.models = modelLoader.getBakedModelMap();
		this.stateLookup = modelLoader.getStateLookup();
		this.missingModel = (BakedModel)this.models.get(ModelLoader.MISSING);
		profiler.swap("cache");
		this.blockModelCache.reload();
		profiler.pop();
		profiler.endTick();
	}

	public boolean shouldRerender(BlockState from, BlockState to) {
		if (from == to) {
			return false;
		} else {
			int i = this.stateLookup.getInt(from);
			if (i != -1) {
				int j = this.stateLookup.getInt(to);
				if (i == j) {
					FluidState fluidState = from.getFluidState();
					FluidState fluidState2 = to.getFluidState();
					return fluidState != fluidState2;
				}
			}

			return true;
		}
	}
}
