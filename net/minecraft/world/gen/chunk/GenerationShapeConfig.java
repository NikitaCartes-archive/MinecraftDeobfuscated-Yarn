/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.chunk;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Function;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.NoiseSamplingConfig;
import net.minecraft.world.gen.chunk.SlideConfig;

public class GenerationShapeConfig {
    public static final Codec<GenerationShapeConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.intRange(DimensionType.field_28136, DimensionType.field_28135).fieldOf("min_y")).forGetter(GenerationShapeConfig::method_32993), ((MapCodec)Codec.intRange(0, DimensionType.field_28134).fieldOf("height")).forGetter(GenerationShapeConfig::getHeight), ((MapCodec)NoiseSamplingConfig.CODEC.fieldOf("sampling")).forGetter(GenerationShapeConfig::getSampling), ((MapCodec)SlideConfig.CODEC.fieldOf("top_slide")).forGetter(GenerationShapeConfig::getTopSlide), ((MapCodec)SlideConfig.CODEC.fieldOf("bottom_slide")).forGetter(GenerationShapeConfig::getBottomSlide), ((MapCodec)Codec.intRange(1, 4).fieldOf("size_horizontal")).forGetter(GenerationShapeConfig::getSizeHorizontal), ((MapCodec)Codec.intRange(1, 4).fieldOf("size_vertical")).forGetter(GenerationShapeConfig::getSizeVertical), ((MapCodec)Codec.DOUBLE.fieldOf("density_factor")).forGetter(GenerationShapeConfig::getDensityFactor), ((MapCodec)Codec.DOUBLE.fieldOf("density_offset")).forGetter(GenerationShapeConfig::getDensityOffset), ((MapCodec)Codec.BOOL.fieldOf("simplex_surface_noise")).forGetter(GenerationShapeConfig::hasSimplexSurfaceNoise), Codec.BOOL.optionalFieldOf("random_density_offset", false, Lifecycle.experimental()).forGetter(GenerationShapeConfig::hasRandomDensityOffset), Codec.BOOL.optionalFieldOf("island_noise_override", false, Lifecycle.experimental()).forGetter(GenerationShapeConfig::hasIslandNoiseOverride), Codec.BOOL.optionalFieldOf("amplified", false, Lifecycle.experimental()).forGetter(GenerationShapeConfig::isAmplified)).apply((Applicative<GenerationShapeConfig, ?>)instance, GenerationShapeConfig::new)).comapFlatMap(GenerationShapeConfig::method_32995, Function.identity());
    private final int field_28202;
    private final int height;
    private final NoiseSamplingConfig sampling;
    private final SlideConfig topSlide;
    private final SlideConfig bottomSlide;
    private final int horizontalSize;
    private final int verticalSize;
    private final double densityFactor;
    private final double densityOffset;
    private final boolean simplexSurfaceNoise;
    private final boolean randomDensityOffset;
    private final boolean islandNoiseOverride;
    private final boolean amplified;

    private static DataResult<GenerationShapeConfig> method_32995(GenerationShapeConfig generationShapeConfig) {
        if (generationShapeConfig.method_32993() + generationShapeConfig.getHeight() > DimensionType.field_28135) {
            return DataResult.error("min_y + height cannot be higher than: " + DimensionType.field_28135);
        }
        if (generationShapeConfig.getHeight() % 16 != 0) {
            return DataResult.error("height has to be a multiple of 16");
        }
        if (generationShapeConfig.method_32993() % 16 != 0) {
            return DataResult.error("min_y has to be a multiple of 16");
        }
        return DataResult.success(generationShapeConfig);
    }

    private GenerationShapeConfig(int height, int i, NoiseSamplingConfig noiseSamplingConfig, SlideConfig slideConfig, SlideConfig slideConfig2, int j, int k, double d, double e, boolean bl, boolean bl2, boolean bl3, boolean bl4) {
        this.field_28202 = height;
        this.height = i;
        this.sampling = noiseSamplingConfig;
        this.topSlide = slideConfig;
        this.bottomSlide = slideConfig2;
        this.horizontalSize = j;
        this.verticalSize = k;
        this.densityFactor = d;
        this.densityOffset = e;
        this.simplexSurfaceNoise = bl;
        this.randomDensityOffset = bl2;
        this.islandNoiseOverride = bl3;
        this.amplified = bl4;
    }

    public static GenerationShapeConfig method_32994(int i, int j, NoiseSamplingConfig noiseSamplingConfig, SlideConfig slideConfig, SlideConfig slideConfig2, int k, int l, double d, double e, boolean bl, boolean bl2, boolean bl3, boolean bl4) {
        GenerationShapeConfig generationShapeConfig = new GenerationShapeConfig(i, j, noiseSamplingConfig, slideConfig, slideConfig2, k, l, d, e, bl, bl2, bl3, bl4);
        GenerationShapeConfig.method_32995(generationShapeConfig).error().ifPresent(partialResult -> {
            throw new IllegalStateException(partialResult.message());
        });
        return generationShapeConfig;
    }

    public int method_32993() {
        return this.field_28202;
    }

    public int getHeight() {
        return this.height;
    }

    public NoiseSamplingConfig getSampling() {
        return this.sampling;
    }

    public SlideConfig getTopSlide() {
        return this.topSlide;
    }

    public SlideConfig getBottomSlide() {
        return this.bottomSlide;
    }

    public int getSizeHorizontal() {
        return this.horizontalSize;
    }

    public int getSizeVertical() {
        return this.verticalSize;
    }

    public double getDensityFactor() {
        return this.densityFactor;
    }

    public double getDensityOffset() {
        return this.densityOffset;
    }

    @Deprecated
    public boolean hasSimplexSurfaceNoise() {
        return this.simplexSurfaceNoise;
    }

    @Deprecated
    public boolean hasRandomDensityOffset() {
        return this.randomDensityOffset;
    }

    @Deprecated
    public boolean hasIslandNoiseOverride() {
        return this.islandNoiseOverride;
    }

    @Deprecated
    public boolean isAmplified() {
        return this.amplified;
    }
}

