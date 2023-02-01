/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.texture.atlas;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.IntUnaryOperator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.SpriteContents;
import net.minecraft.client.texture.SpriteDimensions;
import net.minecraft.client.texture.atlas.AtlasSource;
import net.minecraft.client.texture.atlas.AtlasSourceManager;
import net.minecraft.client.texture.atlas.AtlasSourceType;
import net.minecraft.client.texture.atlas.Sprite;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class PalettedPermutationsAtlasSource
implements AtlasSource {
    static final Logger LOGGER = LogUtils.getLogger();
    public static final Codec<PalettedPermutationsAtlasSource> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.list(Identifier.CODEC).fieldOf("textures")).forGetter(palettedPermutationsAtlasSource -> palettedPermutationsAtlasSource.textures), ((MapCodec)Identifier.CODEC.fieldOf("palette_key")).forGetter(palettedPermutationsAtlasSource -> palettedPermutationsAtlasSource.paletteKey), ((MapCodec)Codec.unboundedMap(Codec.STRING, Identifier.CODEC).fieldOf("permutations")).forGetter(palettedPermutationsAtlasSource -> palettedPermutationsAtlasSource.permutations)).apply((Applicative<PalettedPermutationsAtlasSource, ?>)instance, PalettedPermutationsAtlasSource::new));
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
        Supplier<int[]> supplier = Suppliers.memoize(() -> PalettedPermutationsAtlasSource.method_48486(resourceManager, this.paletteKey));
        HashMap map = new HashMap();
        this.permutations.forEach((string, identifier) -> map.put(string, Suppliers.memoize(() -> PalettedPermutationsAtlasSource.method_48491((java.util.function.Supplier)supplier, resourceManager, identifier))));
        for (Identifier identifier2 : this.textures) {
            Identifier identifier22 = RESOURCE_FINDER.toResourcePath(identifier2);
            Optional<Resource> optional = resourceManager.getResource(identifier22);
            if (optional.isEmpty()) {
                LOGGER.warn("Unable to find texture {}", (Object)identifier22);
                continue;
            }
            Sprite sprite = new Sprite(identifier22, optional.get(), map.size());
            for (Map.Entry entry : map.entrySet()) {
                Identifier identifier3 = identifier2.withSuffixedPath("_" + (String)entry.getKey());
                regions.add(identifier3, new PalettedSpriteRegion(sprite, (java.util.function.Supplier)entry.getValue(), identifier3));
            }
        }
    }

    private static IntUnaryOperator method_48492(int[] is, int[] js) {
        if (js.length != is.length) {
            LOGGER.warn("Palette mapping has different sizes: {} and {}", (Object)is.length, (Object)js.length);
            throw new IllegalArgumentException();
        }
        Int2IntOpenHashMap int2IntMap = new Int2IntOpenHashMap(js.length);
        for (int i2 = 0; i2 < is.length; ++i2) {
            int j = is[i2];
            if (ColorHelper.Abgr.getAlpha(j) == 0) continue;
            int2IntMap.put(ColorHelper.Abgr.getBgr(j), js[i2]);
        }
        return i -> {
            int j = ColorHelper.Abgr.getAlpha(i);
            if (j == 0) {
                return i;
            }
            int k = ColorHelper.Abgr.getBgr(i);
            int l = int2IntMap.getOrDefault(k, ColorHelper.Abgr.toOpaque(k));
            int m = ColorHelper.Abgr.getAlpha(l);
            return ColorHelper.Abgr.withAlpha(j * m / 255, l);
        };
    }

    /*
     * Enabled aggressive exception aggregation
     */
    public static int[] method_48486(ResourceManager resourceManager, Identifier identifier) {
        Optional<Resource> optional = resourceManager.getResource(RESOURCE_FINDER.toResourcePath(identifier));
        if (optional.isEmpty()) {
            LOGGER.error("Failed to load palette image {}", (Object)identifier);
            throw new IllegalArgumentException();
        }
        try (InputStream inputStream = optional.get().getInputStream();){
            NativeImage nativeImage = NativeImage.read(inputStream);
            try {
                int[] nArray = nativeImage.copyPixelsRgba();
                if (nativeImage != null) {
                    nativeImage.close();
                }
                return nArray;
            } catch (Throwable throwable) {
                if (nativeImage != null) {
                    try {
                        nativeImage.close();
                    } catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                }
                throw throwable;
            }
        } catch (Exception exception) {
            LOGGER.error("Couldn't load texture {}", (Object)identifier, (Object)exception);
            throw new IllegalArgumentException();
        }
    }

    @Override
    public AtlasSourceType getType() {
        return AtlasSourceManager.PALETTED_PERMUTATIONS;
    }

    private static /* synthetic */ IntUnaryOperator method_48491(java.util.function.Supplier supplier, ResourceManager resourceManager, Identifier identifier) {
        return PalettedPermutationsAtlasSource.method_48492((int[])supplier.get(), PalettedPermutationsAtlasSource.method_48486(resourceManager, identifier));
    }

    @Environment(value=EnvType.CLIENT)
    record PalettedSpriteRegion(Sprite baseImage, java.util.function.Supplier<IntUnaryOperator> palette, Identifier permutationLocation) implements AtlasSource.SpriteRegion
    {
        @Override
        @Nullable
        public SpriteContents get() {
            try {
                NativeImage nativeImage = this.baseImage.read().apply(this.palette.get());
                SpriteContents spriteContents = new SpriteContents(this.permutationLocation, new SpriteDimensions(nativeImage.getWidth(), nativeImage.getHeight()), nativeImage, AnimationResourceMetadata.EMPTY);
                return spriteContents;
            } catch (IOException | IllegalArgumentException exception) {
                LOGGER.error("unable to apply palette to {}", (Object)this.permutationLocation, (Object)exception);
                SpriteContents spriteContents = null;
                return spriteContents;
            } finally {
                this.baseImage.close();
            }
        }

        @Override
        public void close() {
            this.baseImage.close();
        }

        @Override
        @Nullable
        public /* synthetic */ Object get() {
            return this.get();
        }
    }
}

