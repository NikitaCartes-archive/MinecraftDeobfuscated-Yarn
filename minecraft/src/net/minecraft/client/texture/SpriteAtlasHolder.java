package net.minecraft.client.texture;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

@Environment(EnvType.CLIENT)
public abstract class SpriteAtlasHolder implements ResourceReloader, AutoCloseable {
	private final SpriteAtlasTexture atlas;
	private final Identifier sourcePath;
	private final Set<ResourceMetadataReader<?>> metadataReaders;

	public SpriteAtlasHolder(TextureManager textureManager, Identifier atlasId, Identifier sourcePath) {
		this(textureManager, atlasId, sourcePath, SpriteLoader.METADATA_READERS);
	}

	public SpriteAtlasHolder(TextureManager textureManager, Identifier atlasId, Identifier sourcePath, Set<ResourceMetadataReader<?>> metadataReaders) {
		this.sourcePath = sourcePath;
		this.atlas = new SpriteAtlasTexture(atlasId);
		textureManager.registerTexture(this.atlas.getId(), this.atlas);
		this.metadataReaders = metadataReaders;
	}

	protected Sprite getSprite(Identifier objectId) {
		return this.atlas.getSprite(objectId);
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
		return SpriteLoader.fromAtlas(this.atlas)
			.load(manager, this.sourcePath, 0, prepareExecutor, this.metadataReaders)
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
