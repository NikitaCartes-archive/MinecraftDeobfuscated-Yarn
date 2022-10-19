package net.minecraft.client.texture;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

@Environment(EnvType.CLIENT)
public abstract class SpriteAtlasHolder implements ResourceReloader, AutoCloseable {
	private final SpriteAtlasTexture atlas;
	private final String pathPrefix;

	public SpriteAtlasHolder(TextureManager textureManager, Identifier atlasId, String pathPrefix) {
		this.pathPrefix = pathPrefix;
		this.atlas = new SpriteAtlasTexture(atlasId);
		textureManager.registerTexture(this.atlas.getId(), this.atlas);
	}

	protected Sprite getSprite(Identifier objectId) {
		return this.atlas.getSprite(this.toSpriteId(objectId));
	}

	private Identifier toSpriteId(Identifier objectId) {
		return objectId.withPrefixedPath(this.pathPrefix + "/");
	}

	@Override
	public final CompletableFuture<Void> reload(
		ResourceReloader.Synchronizer synchronizer,
		ResourceManager manager,
		Profiler prepareProfiler,
		Profiler applyProfiler,
		Executor prepareExecutor,
		Executor applyExecutor
	) {
		return CompletableFuture.supplyAsync(() -> SpriteLoader.findAllResources(manager, this.pathPrefix), prepareExecutor)
			.thenCompose(resources -> SpriteLoader.fromAtlas(this.atlas).stitch(resources, 0, prepareExecutor))
			.thenCompose(SpriteLoader.StitchResult::whenComplete)
			.thenCompose(synchronizer::whenPrepared)
			.thenAcceptAsync(stitchResult -> this.afterReload(stitchResult, applyProfiler), applyExecutor);
	}

	private void afterReload(SpriteLoader.StitchResult stitchResult, Profiler profiler) {
		profiler.startTick();
		profiler.push("upload");
		this.atlas.upload(stitchResult);
		profiler.pop();
		profiler.endTick();
	}

	public void close() {
		this.atlas.clear();
	}
}
