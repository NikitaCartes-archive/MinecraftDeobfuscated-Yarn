package net.minecraft.client.render.model;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
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
public class BakedModelManager implements ResourceReloadListener {
	private Map<Identifier, BakedModel> modelMap;
	private final SpriteAtlasTexture spriteAtlas;
	private final BlockModels blockStateMaps;
	private BakedModel missingModel;

	public BakedModelManager(SpriteAtlasTexture spriteAtlasTexture) {
		this.spriteAtlas = spriteAtlasTexture;
		this.blockStateMaps = new BlockModels(this);
	}

	@Override
	public CompletableFuture<Void> apply(
		ResourceReloadListener.Helper helper, ResourceManager resourceManager, Profiler profiler, Profiler profiler2, Executor executor, Executor executor2
	) {
		return CompletableFuture.supplyAsync(() -> {
			profiler.startTick();
			ModelLoader modelLoader = new ModelLoader(resourceManager, this.spriteAtlas, profiler);
			profiler.endTick();
			return modelLoader;
		}, executor).thenCompose(helper::waitForAll).thenAcceptAsync(modelLoader -> {
			profiler2.startTick();
			profiler2.push("upload");
			modelLoader.method_18177(profiler2);
			this.modelMap = modelLoader.getBakedModelMap();
			this.missingModel = (BakedModel)this.modelMap.get(ModelLoader.MISSING);
			profiler2.swap("cache");
			this.blockStateMaps.reload();
			profiler2.pop();
			profiler2.endTick();
		}, executor2);
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
