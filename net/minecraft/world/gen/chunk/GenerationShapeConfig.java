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
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.biome.source.util.VanillaTerrainParameters;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.NoiseSamplingConfig;
import net.minecraft.world.gen.chunk.SlideConfig;

public record GenerationShapeConfig(int minimumY, int height, NoiseSamplingConfig sampling, SlideConfig topSlide, SlideConfig bottomSlide, int horizontalSize, int verticalSize, boolean islandNoiseOverride, boolean amplified, boolean largeBiomes, VanillaTerrainParameters terrainParameters) {
    public static final Codec<GenerationShapeConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.intRange(DimensionType.MIN_HEIGHT, DimensionType.MAX_COLUMN_HEIGHT).fieldOf("min_y")).forGetter(GenerationShapeConfig::minimumY), ((MapCodec)Codec.intRange(0, DimensionType.MAX_HEIGHT).fieldOf("height")).forGetter(GenerationShapeConfig::height), ((MapCodec)NoiseSamplingConfig.CODEC.fieldOf("sampling")).forGetter(GenerationShapeConfig::sampling), ((MapCodec)SlideConfig.CODEC.fieldOf("top_slide")).forGetter(GenerationShapeConfig::topSlide), ((MapCodec)SlideConfig.CODEC.fieldOf("bottom_slide")).forGetter(GenerationShapeConfig::bottomSlide), ((MapCodec)Codec.intRange(1, 4).fieldOf("size_horizontal")).forGetter(GenerationShapeConfig::horizontalSize), ((MapCodec)Codec.intRange(1, 4).fieldOf("size_vertical")).forGetter(GenerationShapeConfig::verticalSize), Codec.BOOL.optionalFieldOf("island_noise_override", false, Lifecycle.experimental()).forGetter(GenerationShapeConfig::islandNoiseOverride), Codec.BOOL.optionalFieldOf("amplified", false, Lifecycle.experimental()).forGetter(GenerationShapeConfig::amplified), Codec.BOOL.optionalFieldOf("large_biomes", false, Lifecycle.experimental()).forGetter(GenerationShapeConfig::largeBiomes), ((MapCodec)VanillaTerrainParameters.field_35456.fieldOf("terrain_shaper")).forGetter(GenerationShapeConfig::terrainParameters)).apply((Applicative<GenerationShapeConfig, ?>)instance, GenerationShapeConfig::new)).comapFlatMap(GenerationShapeConfig::checkHeight, Function.identity());

    private static DataResult<GenerationShapeConfig> checkHeight(GenerationShapeConfig config) {
        if (config.minimumY() + config.height() > DimensionType.MAX_COLUMN_HEIGHT + 1) {
            return DataResult.error("min_y + height cannot be higher than: " + (DimensionType.MAX_COLUMN_HEIGHT + 1));
        }
        if (config.height() % 16 != 0) {
            return DataResult.error("height has to be a multiple of 16");
        }
        if (config.minimumY() % 16 != 0) {
            return DataResult.error("min_y has to be a multiple of 16");
        }
        return DataResult.success(config);
    }

    public static GenerationShapeConfig create(int minimumY, int height, NoiseSamplingConfig sampling, SlideConfig topSlide, SlideConfig bottomSlide, int horizontalSize, int verticalSize, boolean islandNoiseOverride, boolean amplified, boolean largeBiomes, VanillaTerrainParameters terrainParameters) {
        GenerationShapeConfig generationShapeConfig = new GenerationShapeConfig(minimumY, height, sampling, topSlide, bottomSlide, horizontalSize, verticalSize, islandNoiseOverride, amplified, largeBiomes, terrainParameters);
        GenerationShapeConfig.checkHeight(generationShapeConfig).error().ifPresent(partialResult -> {
            throw new IllegalStateException(partialResult.message());
        });
        return generationShapeConfig;
    }

    @Deprecated
    public boolean islandNoiseOverride() {
        return this.islandNoiseOverride;
    }

    @Deprecated
    public boolean amplified() {
        return this.amplified;
    }

    @Deprecated
    public boolean largeBiomes() {
        return this.largeBiomes;
    }

    public int method_39545() {
        return BiomeCoords.toBlock(this.verticalSize());
    }

    public int method_39546() {
        return BiomeCoords.toBlock(this.horizontalSize());
    }

    public int method_39547() {
        return this.height() / this.method_39545();
    }

    public int method_39548() {
        return MathHelper.floorDiv(this.minimumY(), this.method_39545());
    }
}

