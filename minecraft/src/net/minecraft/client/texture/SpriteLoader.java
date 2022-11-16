package net.minecraft.client.texture;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.client.texture.atlas.AtlasLoader;
import net.minecraft.resource.Resource;
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

	public SpriteLoader(Identifier id, int maxTextureSize) {
		this.id = id;
		this.maxTextureSize = maxTextureSize;
	}

	public static SpriteLoader fromAtlas(SpriteAtlasTexture atlasTexture) {
		return new SpriteLoader(atlasTexture.getId(), atlasTexture.getMaxTextureSize());
	}

	public SpriteLoader.StitchResult method_47663(List<SpriteContents> list, int i, Executor executor) {
		int j = this.maxTextureSize;
		TextureStitcher<SpriteContents> textureStitcher = new TextureStitcher<>(j, j, i);
		int k = Integer.MAX_VALUE;
		int l = 1 << i;

		for (SpriteContents spriteContents : list) {
			k = Math.min(k, Math.min(spriteContents.getWidth(), spriteContents.getHeight()));
			int m = Math.min(Integer.lowestOneBit(spriteContents.getWidth()), Integer.lowestOneBit(spriteContents.getHeight()));
			if (m < l) {
				LOGGER.warn(
					"Texture {} with size {}x{} limits mip level from {} to {}",
					spriteContents.getId(),
					spriteContents.getWidth(),
					spriteContents.getHeight(),
					MathHelper.floorLog2(l),
					MathHelper.floorLog2(m)
				);
				l = m;
			}

			textureStitcher.add(spriteContents);
		}

		int n = Math.min(k, l);
		int o = MathHelper.floorLog2(n);
		int m;
		if (o < i) {
			LOGGER.warn("{}: dropping miplevel from {} to {}, because of minimum power of two: {}", this.id, i, o, n);
			m = o;
		} else {
			m = i;
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
	}

	public static CompletableFuture<List<SpriteContents>> method_47664(List<Supplier<SpriteContents>> list, Executor executor) {
		List<CompletableFuture<SpriteContents>> list2 = list.stream().map(supplier -> CompletableFuture.supplyAsync(supplier, executor)).toList();
		return Util.combineSafe(list2).thenApply(listx -> listx.stream().filter(Objects::nonNull).toList());
	}

	public CompletableFuture<SpriteLoader.StitchResult> method_47661(ResourceManager resourceManager, Identifier identifier, int i, Executor executor) {
		return CompletableFuture.supplyAsync(() -> AtlasLoader.of(resourceManager, identifier).loadSources(resourceManager), executor)
			.thenCompose(list -> method_47664(list, executor))
			.thenApply(list -> this.method_47663(list, i, executor));
	}

	@Nullable
	public static SpriteContents load(Identifier id, Resource resource) {
		AnimationResourceMetadata animationResourceMetadata;
		try {
			animationResourceMetadata = (AnimationResourceMetadata)resource.getMetadata()
				.decode(AnimationResourceMetadata.READER)
				.orElse(AnimationResourceMetadata.EMPTY);
		} catch (Exception var8) {
			LOGGER.error("Unable to parse metadata from {}", id, var8);
			return null;
		}

		NativeImage nativeImage;
		try {
			InputStream inputStream = resource.getInputStream();

			try {
				nativeImage = NativeImage.read(inputStream);
			} catch (Throwable var9) {
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (Throwable var7) {
						var9.addSuppressed(var7);
					}
				}

				throw var9;
			}

			if (inputStream != null) {
				inputStream.close();
			}
		} catch (IOException var10) {
			LOGGER.error("Using missing texture, unable to load {}", id, var10);
			return null;
		}

		SpriteDimensions spriteDimensions = animationResourceMetadata.getSize(nativeImage.getWidth(), nativeImage.getHeight());
		if (MathHelper.isMultipleOf(nativeImage.getWidth(), spriteDimensions.width()) && MathHelper.isMultipleOf(nativeImage.getHeight(), spriteDimensions.height())) {
			return new SpriteContents(id, spriteDimensions, nativeImage, animationResourceMetadata);
		} else {
			LOGGER.error(
				"Image {} size {},{} is not multiple of frame size {},{}",
				id,
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

	@Environment(EnvType.CLIENT)
	public static record StitchResult(int width, int height, int mipLevel, Sprite missing, Map<Identifier, Sprite> regions, CompletableFuture<Void> readyForUpload) {
		public CompletableFuture<SpriteLoader.StitchResult> whenComplete() {
			return this.readyForUpload.thenApply(void_ -> this);
		}
	}
}
