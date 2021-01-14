package net.minecraft.client.render.model;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.fluid.FluidState;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

@Environment(EnvType.CLIENT)
public class BakedModelManager extends SinglePreparationResourceReloader<ModelLoader> implements AutoCloseable {
	private Map<Identifier, BakedModel> models;
	@Nullable
	private SpriteAtlasManager atlasManager;
	private final BlockModels blockModelCache;
	private final TextureManager textureManager;
	private final BlockColors colorMap;
	private int mipmapLevels;
	private BakedModel missingModel;
	private Object2IntMap<BlockState> stateLookup;

	public BakedModelManager(TextureManager textureManager, BlockColors colorMap, int mipmap) {
		this.textureManager = textureManager;
		this.colorMap = colorMap;
		this.mipmapLevels = mipmap;
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

	protected ModelLoader prepare(ResourceManager resourceManager, Profiler profiler) {
		profiler.startTick();
		ModelLoader modelLoader = new ModelLoader(resourceManager, this.colorMap, profiler, this.mipmapLevels);
		profiler.endTick();
		return modelLoader;
	}

	protected void apply(ModelLoader modelLoader, ResourceManager resourceManager, Profiler profiler) {
		profiler.startTick();
		profiler.push("upload");
		if (this.atlasManager != null) {
			this.atlasManager.close();
		}

		this.atlasManager = modelLoader.upload(this.textureManager, profiler);
		this.models = modelLoader.getBakedModelMap();
		this.stateLookup = modelLoader.getStateLookup();
		this.missingModel = (BakedModel)this.models.get(ModelLoader.MISSING_ID);
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

	public SpriteAtlasTexture method_24153(Identifier identifier) {
		return this.atlasManager.getAtlas(identifier);
	}

	public void close() {
		if (this.atlasManager != null) {
			this.atlasManager.close();
		}
	}

	public void setMipmapLevels(int mipmapLevels) {
		this.mipmapLevels = mipmapLevels;
	}
}
