package net.minecraft.client.render.model;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.SpriteLoader;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SpriteAtlasManager implements AutoCloseable {
	private final Map<Identifier, SpriteAtlasManager.Atlas> atlases;

	public SpriteAtlasManager(Map<Identifier, SpriteAtlasManager.SpriteResourceLoader> loaders, TextureManager textureManager) {
		this.atlases = (Map<Identifier, SpriteAtlasManager.Atlas>)loaders.entrySet().stream().collect(Collectors.toMap(Entry::getKey, entry -> {
			SpriteAtlasTexture spriteAtlasTexture = new SpriteAtlasTexture((Identifier)entry.getKey());
			textureManager.registerTexture((Identifier)entry.getKey(), spriteAtlasTexture);
			return new SpriteAtlasManager.Atlas(spriteAtlasTexture, (SpriteAtlasManager.SpriteResourceLoader)entry.getValue());
		}));
	}

	public SpriteAtlasTexture getAtlas(Identifier id) {
		return ((SpriteAtlasManager.Atlas)this.atlases.get(id)).atlas();
	}

	public void close() {
		this.atlases.values().forEach(SpriteAtlasManager.Atlas::close);
		this.atlases.clear();
	}

	public Map<Identifier, CompletableFuture<SpriteAtlasManager.AtlasPreparation>> reload(ResourceManager resourceManager, int mipmapLevels, Executor executor) {
		return (Map<Identifier, CompletableFuture<SpriteAtlasManager.AtlasPreparation>>)this.atlases
			.entrySet()
			.stream()
			.collect(
				Collectors.toMap(
					Entry::getKey,
					entry -> {
						SpriteAtlasManager.Atlas atlas = (SpriteAtlasManager.Atlas)entry.getValue();
						return CompletableFuture.supplyAsync(() -> (Map)atlas.loader.apply(resourceManager), executor)
							.thenCompose(sprites -> SpriteLoader.fromAtlas(atlas.atlas).stitch(sprites, mipmapLevels, executor))
							.thenApply(stitchResult -> new SpriteAtlasManager.AtlasPreparation(atlas.atlas, stitchResult));
					}
				)
			);
	}

	@Environment(EnvType.CLIENT)
	static record Atlas(SpriteAtlasTexture atlas, SpriteAtlasManager.SpriteResourceLoader loader) implements AutoCloseable {

		public void close() {
			this.atlas.clear();
		}
	}

	@Environment(EnvType.CLIENT)
	public static class AtlasPreparation {
		private final SpriteAtlasTexture atlasTexture;
		private final SpriteLoader.StitchResult stitchResult;

		public AtlasPreparation(SpriteAtlasTexture atlasTexture, SpriteLoader.StitchResult stitchResult) {
			this.atlasTexture = atlasTexture;
			this.stitchResult = stitchResult;
		}

		@Nullable
		public Sprite getSprite(Identifier id) {
			return (Sprite)this.stitchResult.regions().get(id);
		}

		public Sprite getMissingSprite() {
			return this.stitchResult.missing();
		}

		public CompletableFuture<Void> whenComplete() {
			return this.stitchResult.readyForUpload();
		}

		public void upload() {
			this.atlasTexture.upload(this.stitchResult);
		}
	}

	@FunctionalInterface
	@Environment(EnvType.CLIENT)
	public interface SpriteResourceLoader extends Function<ResourceManager, Map<Identifier, Resource>> {
	}
}
