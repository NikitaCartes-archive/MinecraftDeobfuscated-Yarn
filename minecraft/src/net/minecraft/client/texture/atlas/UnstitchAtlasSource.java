package net.minecraft.client.texture.atlas;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.SpriteContents;
import net.minecraft.client.texture.SpriteDimensions;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.MathHelper;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class UnstitchAtlasSource implements AtlasSource {
	static final Logger LOGGER = LogUtils.getLogger();
	private final ResourceFinder FINDER = new ResourceFinder("textures", ".png");
	public static final Codec<UnstitchAtlasSource> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Identifier.CODEC.fieldOf("resource").forGetter(unstitchAtlasSource -> unstitchAtlasSource.resource),
					Codecs.nonEmptyList(UnstitchAtlasSource.Region.CODEC.listOf()).fieldOf("regions").forGetter(unstitchAtlasSource -> unstitchAtlasSource.regions),
					Codec.DOUBLE.optionalFieldOf("divisor_x", Double.valueOf(1.0)).forGetter(unstitchAtlasSource -> unstitchAtlasSource.divisorX),
					Codec.DOUBLE.optionalFieldOf("divisor_y", Double.valueOf(1.0)).forGetter(unstitchAtlasSource -> unstitchAtlasSource.divisorY)
				)
				.apply(instance, UnstitchAtlasSource::new)
	);
	private final Identifier resource;
	private final List<UnstitchAtlasSource.Region> regions;
	private final double divisorX;
	private final double divisorY;

	public UnstitchAtlasSource(Identifier resource, List<UnstitchAtlasSource.Region> regions, double divisorX, double divisorY) {
		this.resource = resource;
		this.regions = regions;
		this.divisorX = divisorX;
		this.divisorY = divisorY;
	}

	@Override
	public void load(ResourceManager resourceManager, AtlasSource.SpriteRegions regions) {
		Identifier identifier = this.FINDER.toResourcePath(this.resource);
		Optional<Resource> optional = resourceManager.getResource(identifier);
		if (optional.isPresent()) {
			UnstitchAtlasSource.Sprite sprite = new UnstitchAtlasSource.Sprite(identifier, (Resource)optional.get(), this.regions.size());

			for (UnstitchAtlasSource.Region region : this.regions) {
				regions.add(region.sprite, new UnstitchAtlasSource.SpriteRegion(sprite, region, this.divisorX, this.divisorY));
			}
		} else {
			LOGGER.warn("Missing sprite: {}", identifier);
		}
	}

	@Override
	public AtlasSourceType getType() {
		return AtlasSourceManager.UNSTITCH;
	}

	@Environment(EnvType.CLIENT)
	static record Region(Identifier sprite, double x, double y, double width, double height) {
		public static final Codec<UnstitchAtlasSource.Region> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						Identifier.CODEC.fieldOf("sprite").forGetter(UnstitchAtlasSource.Region::sprite),
						Codec.DOUBLE.fieldOf("x").forGetter(UnstitchAtlasSource.Region::x),
						Codec.DOUBLE.fieldOf("y").forGetter(UnstitchAtlasSource.Region::y),
						Codec.DOUBLE.fieldOf("width").forGetter(UnstitchAtlasSource.Region::width),
						Codec.DOUBLE.fieldOf("height").forGetter(UnstitchAtlasSource.Region::height)
					)
					.apply(instance, UnstitchAtlasSource.Region::new)
		);
	}

	@Environment(EnvType.CLIENT)
	static class Sprite {
		private final Identifier id;
		private final Resource resource;
		private final AtomicReference<NativeImage> image = new AtomicReference();
		private final AtomicInteger regionCount;

		Sprite(Identifier id, Resource resource, int regionCount) {
			this.id = id;
			this.resource = resource;
			this.regionCount = new AtomicInteger(regionCount);
		}

		public NativeImage read() throws IOException {
			NativeImage nativeImage = (NativeImage)this.image.get();
			if (nativeImage == null) {
				synchronized (this) {
					nativeImage = (NativeImage)this.image.get();
					if (nativeImage == null) {
						try {
							InputStream inputStream = this.resource.getInputStream();

							try {
								nativeImage = NativeImage.read(inputStream);
								this.image.set(nativeImage);
							} catch (Throwable var8) {
								if (inputStream != null) {
									try {
										inputStream.close();
									} catch (Throwable var7) {
										var8.addSuppressed(var7);
									}
								}

								throw var8;
							}

							if (inputStream != null) {
								inputStream.close();
							}
						} catch (IOException var9) {
							throw new IOException("Failed to load image " + this.id, var9);
						}
					}
				}
			}

			return nativeImage;
		}

		public void close() {
			int i = this.regionCount.decrementAndGet();
			if (i <= 0) {
				NativeImage nativeImage = (NativeImage)this.image.getAndSet(null);
				if (nativeImage != null) {
					nativeImage.close();
				}
			}
		}
	}

	@Environment(EnvType.CLIENT)
	static class SpriteRegion implements AtlasSource.SpriteRegion {
		private final UnstitchAtlasSource.Sprite sprite;
		private final UnstitchAtlasSource.Region region;
		private final double divisorX;
		private final double divisorY;

		SpriteRegion(UnstitchAtlasSource.Sprite sprite, UnstitchAtlasSource.Region region, double divisorX, double divisorY) {
			this.sprite = sprite;
			this.region = region;
			this.divisorX = divisorX;
			this.divisorY = divisorY;
		}

		public SpriteContents get() {
			try {
				NativeImage nativeImage = this.sprite.read();
				double d = (double)nativeImage.getWidth() / this.divisorX;
				double e = (double)nativeImage.getHeight() / this.divisorY;
				int i = MathHelper.floor(this.region.x * d);
				int j = MathHelper.floor(this.region.y * e);
				int k = MathHelper.floor(this.region.width * d);
				int l = MathHelper.floor(this.region.height * e);
				NativeImage nativeImage2 = new NativeImage(NativeImage.Format.RGBA, k, l, false);
				nativeImage.copyRect(nativeImage2, i, j, 0, 0, k, l, false, false);
				return new SpriteContents(this.region.sprite, new SpriteDimensions(k, l), nativeImage2, AnimationResourceMetadata.EMPTY);
			} catch (Exception var15) {
				UnstitchAtlasSource.LOGGER.error("Failed to unstitch region {}", this.region.sprite, var15);
			} finally {
				this.sprite.close();
			}

			return MissingSprite.createSpriteContents();
		}

		@Override
		public void close() {
			this.sprite.close();
		}
	}
}
