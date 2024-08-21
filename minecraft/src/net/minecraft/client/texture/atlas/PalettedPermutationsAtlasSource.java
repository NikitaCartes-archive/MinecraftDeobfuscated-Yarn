package net.minecraft.client.texture.atlas;

import com.google.common.base.Suppliers;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.IntUnaryOperator;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.SpriteContents;
import net.minecraft.client.texture.SpriteDimensions;
import net.minecraft.client.texture.SpriteOpener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.metadata.ResourceMetadata;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class PalettedPermutationsAtlasSource implements AtlasSource {
	static final Logger LOGGER = LogUtils.getLogger();
	public static final MapCodec<PalettedPermutationsAtlasSource> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					Codec.list(Identifier.CODEC).fieldOf("textures").forGetter(source -> source.textures),
					Identifier.CODEC.fieldOf("palette_key").forGetter(source -> source.paletteKey),
					Codec.unboundedMap(Codec.STRING, Identifier.CODEC).fieldOf("permutations").forGetter(source -> source.permutations)
				)
				.apply(instance, PalettedPermutationsAtlasSource::new)
	);
	private final List<Identifier> textures;
	private final Map<String, Identifier> permutations;
	private final Identifier paletteKey;

	private PalettedPermutationsAtlasSource(List<Identifier> textures, Identifier paletteKey, Map<String, Identifier> permutations) {
		this.textures = textures;
		this.permutations = permutations;
		this.paletteKey = paletteKey;
	}

	@Override
	public void load(ResourceManager resourceManager, AtlasSource.SpriteRegions regions) {
		Supplier<int[]> supplier = Suppliers.memoize(() -> open(resourceManager, this.paletteKey));
		Map<String, Supplier<IntUnaryOperator>> map = new HashMap();
		this.permutations.forEach((key, texture) -> map.put(key, Suppliers.memoize(() -> toMapper((int[])supplier.get(), open(resourceManager, texture)))));

		for (Identifier identifier : this.textures) {
			Identifier identifier2 = RESOURCE_FINDER.toResourcePath(identifier);
			Optional<Resource> optional = resourceManager.getResource(identifier2);
			if (optional.isEmpty()) {
				LOGGER.warn("Unable to find texture {}", identifier2);
			} else {
				AtlasSprite atlasSprite = new AtlasSprite(identifier2, (Resource)optional.get(), map.size());

				for (Entry<String, Supplier<IntUnaryOperator>> entry : map.entrySet()) {
					Identifier identifier3 = identifier.withSuffixedPath("_" + (String)entry.getKey());
					regions.add(identifier3, new PalettedPermutationsAtlasSource.PalettedSpriteRegion(atlasSprite, (Supplier<IntUnaryOperator>)entry.getValue(), identifier3));
				}
			}
		}
	}

	private static IntUnaryOperator toMapper(int[] from, int[] to) {
		if (to.length != from.length) {
			LOGGER.warn("Palette mapping has different sizes: {} and {}", from.length, to.length);
			throw new IllegalArgumentException();
		} else {
			Int2IntMap int2IntMap = new Int2IntOpenHashMap(to.length);

			for (int i = 0; i < from.length; i++) {
				int j = from[i];
				if (ColorHelper.getAlpha(j) != 0) {
					int2IntMap.put(ColorHelper.zeroAlpha(j), to[i]);
				}
			}

			return color -> {
				int ix = ColorHelper.getAlpha(color);
				if (ix == 0) {
					return color;
				} else {
					int jx = ColorHelper.zeroAlpha(color);
					int k = int2IntMap.getOrDefault(jx, ColorHelper.fullAlpha(jx));
					int l = ColorHelper.getAlpha(k);
					return ColorHelper.withAlpha(ix * l / 255, k);
				}
			};
		}
	}

	private static int[] open(ResourceManager resourceManager, Identifier texture) {
		Optional<Resource> optional = resourceManager.getResource(RESOURCE_FINDER.toResourcePath(texture));
		if (optional.isEmpty()) {
			LOGGER.error("Failed to load palette image {}", texture);
			throw new IllegalArgumentException();
		} else {
			try {
				InputStream inputStream = ((Resource)optional.get()).getInputStream();

				int[] var5;
				try (NativeImage nativeImage = NativeImage.read(inputStream)) {
					var5 = nativeImage.copyPixelsArgb();
				} catch (Throwable var10) {
					if (inputStream != null) {
						try {
							inputStream.close();
						} catch (Throwable var7) {
							var10.addSuppressed(var7);
						}
					}

					throw var10;
				}

				if (inputStream != null) {
					inputStream.close();
				}

				return var5;
			} catch (Exception var11) {
				LOGGER.error("Couldn't load texture {}", texture, var11);
				throw new IllegalArgumentException();
			}
		}
	}

	@Override
	public AtlasSourceType getType() {
		return AtlasSourceManager.PALETTED_PERMUTATIONS;
	}

	@Environment(EnvType.CLIENT)
	static record PalettedSpriteRegion(AtlasSprite baseImage, Supplier<IntUnaryOperator> palette, Identifier permutationLocation)
		implements AtlasSource.SpriteRegion {
		@Nullable
		public SpriteContents apply(SpriteOpener spriteOpener) {
			Object var3;
			try {
				NativeImage nativeImage = this.baseImage.read().applyToCopy((IntUnaryOperator)this.palette.get());
				return new SpriteContents(
					this.permutationLocation, new SpriteDimensions(nativeImage.getWidth(), nativeImage.getHeight()), nativeImage, ResourceMetadata.NONE
				);
			} catch (IllegalArgumentException | IOException var7) {
				PalettedPermutationsAtlasSource.LOGGER.error("unable to apply palette to {}", this.permutationLocation, var7);
				var3 = null;
			} finally {
				this.baseImage.close();
			}

			return (SpriteContents)var3;
		}

		@Override
		public void close() {
			this.baseImage.close();
		}
	}
}
