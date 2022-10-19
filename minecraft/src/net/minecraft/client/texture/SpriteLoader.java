package net.minecraft.client.texture;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.MathHelper;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class SpriteLoader {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final Identifier id;
	private final int maxTextureSize;
	private static final ResourceFinder FINDER = new ResourceFinder("textures", ".png");

	public SpriteLoader(Identifier id, int maxTextureSize) {
		this.id = id;
		this.maxTextureSize = maxTextureSize;
	}

	public static SpriteLoader fromAtlas(SpriteAtlasTexture atlasTexture) {
		return new SpriteLoader(atlasTexture.getId(), atlasTexture.getMaxTextureSize());
	}

	public CompletableFuture<SpriteLoader.StitchResult> stitch(Map<Identifier, Resource> sprites, int mipmapLevels, Executor executor) {
		return this.loadAll(sprites, executor)
			.thenApplyAsync(
				spriteContents -> {
					int j = this.maxTextureSize;
					TextureStitcher<SpriteContents> textureStitcher = new TextureStitcher<>(j, j, mipmapLevels);
					int k = Integer.MAX_VALUE;
					int l = 1 << mipmapLevels;

					for (SpriteContents spriteContents2 : spriteContents) {
						k = Math.min(k, Math.min(spriteContents2.getWidth(), spriteContents2.getHeight()));
						int m = Math.min(Integer.lowestOneBit(spriteContents2.getWidth()), Integer.lowestOneBit(spriteContents2.getHeight()));
						if (m < l) {
							LOGGER.warn(
								"Texture {} with size {}x{} limits mip level from {} to {}",
								spriteContents2.getId(),
								spriteContents2.getWidth(),
								spriteContents2.getHeight(),
								MathHelper.floorLog2(l),
								MathHelper.floorLog2(m)
							);
							l = m;
						}

						textureStitcher.add(spriteContents2);
					}

					int n = Math.min(k, l);
					int o = MathHelper.floorLog2(n);
					int m;
					if (o < mipmapLevels) {
						LOGGER.warn("{}: dropping miplevel from {} to {}, because of minimum power of two: {}", this.id, mipmapLevels, o, n);
						m = o;
					} else {
						m = mipmapLevels;
					}

					try {
						textureStitcher.stitch();
					} catch (TextureStitcherCannotFitException var14) {
						CrashReport crashReport = CrashReport.create(var14, "Stitching");
						CrashReportSection crashReportSection = crashReport.addElement("Stitcher");
						crashReportSection.add(
							"Sprites",
							var14.getSprites()
								.stream()
								.map(spritex -> String.format(Locale.ROOT, "%s[%dx%d]", spritex.getId(), spritex.getWidth(), spritex.getHeight()))
								.collect(Collectors.joining(","))
						);
						crashReportSection.add("Max Texture Size", j);
						throw new CrashException(crashReport);
					}

					Map<Identifier, Sprite> map = this.collectStitchedSprites(textureStitcher);
					Sprite sprite = (Sprite)map.get(MissingSprite.getMissingSpriteId());
					CompletableFuture<Void> completableFuture;
					if (m > 0) {
						completableFuture = CompletableFuture.runAsync(() -> map.values().forEach(spritex -> spritex.getContents().generateMipmaps(m)), executor);
					} else {
						completableFuture = CompletableFuture.completedFuture(null);
					}

					return new SpriteLoader.StitchResult(textureStitcher.getWidth(), textureStitcher.getHeight(), m, sprite, map, completableFuture);
				},
				executor
			);
	}

	private CompletableFuture<List<SpriteContents>> loadAll(Map<Identifier, Resource> sprites, Executor executor) {
		List<CompletableFuture<SpriteContents>> list = new ArrayList();
		list.add(CompletableFuture.supplyAsync(MissingSprite::createSpriteContents, executor));
		sprites.forEach((id, resource) -> list.add(CompletableFuture.supplyAsync(() -> this.load(id, resource), executor)));
		return Util.combineSafe(list).thenApply(spriteContents -> spriteContents.stream().filter(Objects::nonNull).toList());
	}

	@Nullable
	private SpriteContents load(Identifier id, Resource resource) {
		AnimationResourceMetadata animationResourceMetadata;
		try {
			animationResourceMetadata = (AnimationResourceMetadata)resource.getMetadata()
				.decode(AnimationResourceMetadata.READER)
				.orElse(AnimationResourceMetadata.EMPTY);
		} catch (Exception var9) {
			LOGGER.error("Unable to parse metadata from {} : {}", this.id, var9);
			return null;
		}

		NativeImage nativeImage;
		try {
			InputStream inputStream = resource.getInputStream();

			try {
				nativeImage = NativeImage.read(inputStream);
			} catch (Throwable var10) {
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (Throwable var8) {
						var10.addSuppressed(var8);
					}
				}

				throw var10;
			}

			if (inputStream != null) {
				inputStream.close();
			}
		} catch (IOException var11) {
			LOGGER.error("Using missing texture, unable to load {} : {}", this.id, var11);
			return null;
		}

		SpriteDimensions spriteDimensions = animationResourceMetadata.getSize(nativeImage.getWidth(), nativeImage.getHeight());
		if (MathHelper.isMultipleOf(nativeImage.getWidth(), spriteDimensions.width()) && MathHelper.isMultipleOf(nativeImage.getHeight(), spriteDimensions.height())) {
			return new SpriteContents(id, spriteDimensions, nativeImage, animationResourceMetadata);
		} else {
			LOGGER.error(
				"Image {} size {},{} is not multiple of frame size {},{}",
				this.id,
				nativeImage.getWidth(),
				nativeImage.getHeight(),
				spriteDimensions.width(),
				spriteDimensions.height()
			);
			nativeImage.close();
			return null;
		}
	}

	private Map<Identifier, Sprite> collectStitchedSprites(TextureStitcher<SpriteContents> stitcher) {
		Map<Identifier, Sprite> map = new HashMap();
		int i = stitcher.getWidth();
		int j = stitcher.getHeight();
		stitcher.getStitchedSprites((info, width, height) -> map.put(info.getId(), new Sprite(this.id, info, i, j, width, height)));
		return map;
	}

	public static void addResource(ResourceManager resourceManager, Identifier id, BiConsumer<Identifier, Resource> adder) {
		Identifier identifier = FINDER.toResourcePath(id);
		Optional<Resource> optional = resourceManager.getResource(identifier);
		if (optional.isPresent()) {
			adder.accept(id, (Resource)optional.get());
		} else {
			LOGGER.warn("Missing sprite: {}", identifier);
		}
	}

	public static void addResources(ResourceManager resourceManager, String textureId, BiConsumer<Identifier, Resource> adder) {
		addResources(resourceManager, "textures/" + textureId, textureId + "/", adder);
	}

	public static void addResources(ResourceManager resourceManager, String textureId, String prefix, BiConsumer<Identifier, Resource> adder) {
		ResourceFinder resourceFinder = new ResourceFinder(textureId, ".png");
		resourceFinder.findResources(resourceManager).forEach((id, resource) -> {
			Identifier identifier = resourceFinder.toResourceId(id).withPrefixedPath(prefix);
			adder.accept(identifier, resource);
		});
	}

	public static Map<Identifier, Resource> findAllResources(ResourceManager resourceManager, String textureId) {
		return findAllResources(resourceManager, "textures/" + textureId, textureId + "/");
	}

	public static Map<Identifier, Resource> findAllResources(ResourceManager resourceManager, String textureId, String prefix) {
		Map<Identifier, Resource> map = new HashMap();
		addResources(resourceManager, textureId, prefix, map::put);
		return map;
	}

	@Environment(EnvType.CLIENT)
	public static record StitchResult(int width, int height, int mipLevel, Sprite missing, Map<Identifier, Sprite> regions, CompletableFuture<Void> readyForUpload) {
		public CompletableFuture<SpriteLoader.StitchResult> whenComplete() {
			return this.readyForUpload.thenApply(void_ -> this);
		}
	}
}
