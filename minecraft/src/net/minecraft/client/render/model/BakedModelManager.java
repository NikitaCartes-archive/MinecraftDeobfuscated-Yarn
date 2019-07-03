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
	private final BlockColors field_20277;
	private BakedModel missingModel;
	private Object2IntMap<BlockState> field_20278;

	public BakedModelManager(SpriteAtlasTexture spriteAtlasTexture, BlockColors blockColors) {
		this.spriteAtlas = spriteAtlasTexture;
		this.field_20277 = blockColors;
		this.blockStateMaps = new BlockModels(this);
	}

	public BakedModel getModel(ModelIdentifier modelIdentifier) {
		return (BakedModel)this.modelMap.getOrDefault(modelIdentifier, this.missingModel);
	}

	public BakedModel getMissingModel() {
		return this.missingModel;
	}

	public BlockModels getBlockStateMaps() {
		return this.blockStateMaps;
	}

	protected ModelLoader method_18178(ResourceManager resourceManager, Profiler profiler) {
		profiler.startTick();
		ModelLoader modelLoader = new ModelLoader(resourceManager, this.spriteAtlas, this.field_20277, profiler);
		profiler.endTick();
		return modelLoader;
	}

	protected void method_18179(ModelLoader modelLoader, ResourceManager resourceManager, Profiler profiler) {
		profiler.startTick();
		profiler.push("upload");
		modelLoader.upload(profiler);
		this.modelMap = modelLoader.getBakedModelMap();
		this.field_20278 = modelLoader.method_21605();
		this.missingModel = (BakedModel)this.modelMap.get(ModelLoader.MISSING);
		profiler.swap("cache");
		this.blockStateMaps.reload();
		profiler.pop();
		profiler.endTick();
	}

	public boolean method_21611(BlockState blockState, BlockState blockState2) {
		if (blockState == blockState2) {
			return false;
		} else {
			int i = this.field_20278.getInt(blockState);
			if (i != -1) {
				int j = this.field_20278.getInt(blockState2);
				if (i == j) {
					FluidState fluidState = blockState.getFluidState();
					FluidState fluidState2 = blockState2.getFluidState();
					return fluidState != fluidState2;
				}
			}

			return true;
		}
	}
}
