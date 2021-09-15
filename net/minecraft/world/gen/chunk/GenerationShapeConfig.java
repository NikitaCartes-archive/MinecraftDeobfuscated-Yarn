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
    public static final Codec<GenerationShapeConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.intRange(DimensionType.MIN_HEIGHT, DimensionType.MAX_COLUMN_HEIGHT).fieldOf("min_y")).forGetter(GenerationShapeConfig::getMinimumY), ((MapCodec)Codec.intRange(0, DimensionType.MAX_HEIGHT).fieldOf("height")).forGetter(GenerationShapeConfig::getHeight), ((MapCodec)NoiseSamplingConfig.CODEC.fieldOf("sampling")).forGetter(GenerationShapeConfig::getSampling), ((MapCodec)SlideConfig.CODEC.fieldOf("top_slide")).forGetter(GenerationShapeConfig::getTopSlide), ((MapCodec)SlideConfig.CODEC.fieldOf("bottom_slide")).forGetter(GenerationShapeConfig::getBottomSlide), ((MapCodec)Codec.intRange(1, 4).fieldOf("size_horizontal")).forGetter(GenerationShapeConfig::getSizeHorizontal), ((MapCodec)Codec.intRange(1, 4).fieldOf("size_vertical")).forGetter(GenerationShapeConfig::getSizeVertical), ((MapCodec)Codec.DOUBLE.fieldOf("density_factor")).forGetter(GenerationShapeConfig::getDensityFactor), ((MapCodec)Codec.DOUBLE.fieldOf("density_offset")).forGetter(GenerationShapeConfig::getDensityOffset), ((MapCodec)Codec.BOOL.fieldOf("simplex_surface_noise")).forGetter(GenerationShapeConfig::hasSimplexSurfaceNoise), Codec.BOOL.optionalFieldOf("random_density_offset", false, Lifecycle.experimental()).forGetter(GenerationShapeConfig::hasRandomDensityOffset), Codec.BOOL.optionalFieldOf("island_noise_override", false, Lifecycle.experimental()).forGetter(GenerationShapeConfig::hasIslandNoiseOverride), Codec.BOOL.optionalFieldOf("amplified", false, Lifecycle.experimental()).forGetter(GenerationShapeConfig::isAmplified), Codec.BOOL.optionalFieldOf("use_legacy_random", false, Lifecycle.experimental()).forGetter(GenerationShapeConfig::method_38413)).apply((Applicative<GenerationShapeConfig, ?>)instance, GenerationShapeConfig::new)).comapFlatMap(GenerationShapeConfig::checkHeight, Function.identity());
    private final int minimumY;
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
    private final boolean field_34687;

    private static DataResult<GenerationShapeConfig> checkHeight(GenerationShapeConfig config) {
        if (config.getMinimumY() + config.getHeight() > DimensionType.MAX_COLUMN_HEIGHT + 1) {
            return DataResult.error("min_y + height cannot be higher than: " + (DimensionType.MAX_COLUMN_HEIGHT + 1));
        }
        if (config.getHeight() % 16 != 0) {
            return DataResult.error("height has to be a multiple of 16");
        }
        if (config.getMinimumY() % 16 != 0) {
            return DataResult.error("min_y has to be a multiple of 16");
        }
        return DataResult.success(config);
    }

    private GenerationShapeConfig(int minimumY, int height, NoiseSamplingConfig sampling, SlideConfig topSlide, SlideConfig bottomSlide, int horizontalSize, int verticalSize, double densityFactor, double densityOffset, boolean simplexSurfaceNoise, boolean randomDensityOffset, boolean islandNoiseOverride, boolean amplified, boolean bl) {
        this.minimumY = minimumY;
        this.height = height;
        this.sampling = sampling;
        this.topSlide = topSlide;
        this.bottomSlide = bottomSlide;
        this.horizontalSize = horizontalSize;
        this.verticalSize = verticalSize;
        this.densityFactor = densityFactor;
        this.densityOffset = densityOffset;
        this.simplexSurfaceNoise = simplexSurfaceNoise;
        this.randomDensityOffset = randomDensityOffset;
        this.islandNoiseOverride = islandNoiseOverride;
        this.amplified = amplified;
        this.field_34687 = bl;
    }

    public static GenerationShapeConfig create(int minimumY, int height, NoiseSamplingConfig sampling, SlideConfig topSlide, SlideConfig bottomSlide, int horizontalSize, int verticalSize, double densityFactor, double densityOffset, boolean simplexSurfaceNoise, boolean randomDensityOffset, boolean islandNoiseOverride, boolean amplified, boolean bl) {
        GenerationShapeConfig generationShapeConfig = new GenerationShapeConfig(minimumY, height, sampling, topSlide, bottomSlide, horizontalSize, verticalSize, densityFactor, densityOffset, simplexSurfaceNoise, randomDensityOffset, islandNoiseOverride, amplified, bl);
        GenerationShapeConfig.checkHeight(generationShapeConfig).error().ifPresent(partialResult -> {
            throw new IllegalStateException(partialResult.message());
        });
        return generationShapeConfig;
    }

    public int getMinimumY() {
        return this.minimumY;
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

    public boolean method_38413() {
        return this.field_34687;
    }
}

