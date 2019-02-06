package net.minecraft.client.render.model;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

@Environment(EnvType.CLIENT)
public class BakedModelManager implements ResourceReloadListener<ModelLoader> {
	private Map<Identifier, BakedModel> modelMap;
	private final SpriteAtlasTexture spriteAtlas;
	private final BlockModels blockStateMaps;
	private BakedModel missingModel;

	public BakedModelManager(SpriteAtlasTexture spriteAtlasTexture) {
		this.spriteAtlas = spriteAtlasTexture;
		this.blockStateMaps = new BlockModels(this);
	}

	@Override
	public CompletableFuture<ModelLoader> prepare(ResourceManager resourceManager, Profiler profiler) {
		return CompletableFuture.supplyAsync(() -> {
			profiler.startTick();
			ModelLoader modelLoader = new ModelLoader(resourceManager, this.spriteAtlas, profiler);
			profiler.endTick();
			return modelLoader;
		});
	}

	public void method_18179(ResourceManager resourceManager, ModelLoader modelLoader, Profiler profiler) {
		profiler.startTick();
		profiler.push("upload");
		modelLoader.method_18177(profiler);
		this.modelMap = modelLoader.getBakedModelMap();
		this.missingModel = (BakedModel)this.modelMap.get(ModelLoader.MISSING);
		profiler.swap("cache");
		this.blockStateMaps.reload();
		profiler.pop();
		profiler.endTick();
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
}
