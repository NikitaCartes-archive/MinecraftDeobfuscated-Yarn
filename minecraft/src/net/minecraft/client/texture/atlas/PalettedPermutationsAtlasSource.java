package net.minecraft.client.texture.atlas;

import com.google.common.base.Suppliers;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
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
import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.SpriteContents;
import net.minecraft.client.texture.SpriteDimensions;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class PalettedPermutationsAtlasSource implements AtlasSource {
	static final Logger LOGGER = LogUtils.getLogger();
	public static final Codec<PalettedPermutationsAtlasSource> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codec.list(Identifier.CODEC).fieldOf("textures").forGetter(palettedPermutationsAtlasSource -> palettedPermutationsAtlasSource.textures),
					Identifier.CODEC.fieldOf("palette_key").forGetter(palettedPermutationsAtlasSource -> palettedPermutationsAtlasSource.paletteKey),
					Codec.unboundedMap(Codec.STRING, Identifier.CODEC)
						.fieldOf("permutations")
						.forGetter(palettedPermutationsAtlasSource -> palettedPermutationsAtlasSource.permutations)
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
		Supplier<int[]> supplier = Suppliers.memoize(() -> method_48486(resourceManager, this.paletteKey));
		Map<String, Supplier<IntUnaryOperator>> map = new HashMap();
		this.permutations
			.forEach((string, identifierx) -> map.put(string, Suppliers.memoize(() -> method_48492((int[])supplier.get(), method_48486(resourceManager, identifierx)))));

		for (Identifier identifier : this.textures) {
			Identifier identifier2 = RESOURCE_FINDER.toResourcePath(identifier);
			Optional<Resource> optional = resourceManager.getResource(identifier2);
			if (optional.isEmpty()) {
				LOGGER.warn("Unable to find texture {}", identifier2);
			} else {
				Sprite sprite = new Sprite(identifier2, (Resource)optional.get(), map.size());

				for (Entry<String, Supplier<IntUnaryOperator>> entry : map.entrySet()) {
					Identifier identifier3 = identifier.withSuffixedPath("_" + (String)entry.getKey());
					regions.add(identifier3, new PalettedPermutationsAtlasSource.PalettedSpriteRegion(sprite, (Supplier<IntUnaryOperator>)entry.getValue(), identifier3));
				}
			}
		}
	}

	private static IntUnaryOperator method_48492(int[] is, int[] js) {
		if (js.length != is.length) {
			LOGGER.warn("Palette mapping has different sizes: {} and {}", is.length, js.length);
			throw new IllegalArgumentException();
		} else {
			Int2IntMap int2IntMap = new Int2IntOpenHashMap(js.length);

			for (int i = 0; i < is.length; i++) {
				int j = is[i];
				if (ColorHelper.Abgr.getAlpha(j) != 0) {
					int2IntMap.put(ColorHelper.Abgr.getBgr(j), js[i]);
				}
			}

			return ix -> {
				int jx = ColorHelper.Abgr.getAlpha(ix);
				if (jx == 0) {
					return ix;
				} else {
					int k = ColorHelper.Abgr.getBgr(ix);
					int l = int2IntMap.getOrDefault(k, ColorHelper.Abgr.toOpaque(k));
					int m = ColorHelper.Abgr.getAlpha(l);
					return ColorHelper.Abgr.withAlpha(jx * m / 255, l);
				}
			};
		}
	}

	public static int[] method_48486(ResourceManager resourceManager, Identifier identifier) {
		Optional<Resource> optional = resourceManager.getResource(RESOURCE_FINDER.toResourcePath(identifier));
		if (optional.isEmpty()) {
			LOGGER.error("Failed to load palette image {}", identifier);
			throw new IllegalArgumentException();
		} else {
			try {
				InputStream inputStream = ((Resource)optional.get()).getInputStream();

				int[] var5;
				try (NativeImage nativeImage = NativeImage.read(inputStream)) {
					var5 = nativeImage.copyPixelsRgba();
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
				LOGGER.error("Couldn't load texture {}", identifier, var11);
				throw new IllegalArgumentException();
			}
		}
	}

	@Override
	public AtlasSourceType getType() {
		return AtlasSourceManager.PALETTED_PERMUTATIONS;
	}

	@Environment(EnvType.CLIENT)
	static record PalettedSpriteRegion(Sprite baseImage, Supplier<IntUnaryOperator> palette, Identifier permutationLocation) implements AtlasSource.SpriteRegion {
		@Nullable
		public SpriteContents get() {
			Object var2;
			try {
				NativeImage nativeImage = this.baseImage.read().apply((IntUnaryOperator)this.palette.get());
				return new SpriteContents(
					this.permutationLocation, new SpriteDimensions(nativeImage.getWidth(), nativeImage.getHeight()), nativeImage, AnimationResourceMetadata.EMPTY
				);
			} catch (IllegalArgumentException | IOException var6) {
				PalettedPermutationsAtlasSource.LOGGER.error("unable to apply palette to {}", this.permutationLocation, var6);
				var2 = null;
			} finally {
				this.baseImage.close();
			}

			return (SpriteContents)var2;
		}

		@Override
		public void close() {
			this.baseImage.close();
		}
	}
}
