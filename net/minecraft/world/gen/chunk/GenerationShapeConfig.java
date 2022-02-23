/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.chunk;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Function;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.biome.source.util.VanillaTerrainParameters;
import net.minecraft.world.biome.source.util.VanillaTerrainParametersCreator;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.NoiseSamplingConfig;
import net.minecraft.world.gen.chunk.SlideConfig;

public record GenerationShapeConfig(int minimumY, int height, NoiseSamplingConfig sampling, SlideConfig topSlide, SlideConfig bottomSlide, int horizontalSize, int verticalSize, VanillaTerrainParameters terrainParameters) {
    public static final Codec<GenerationShapeConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.intRange(DimensionType.MIN_HEIGHT, DimensionType.MAX_COLUMN_HEIGHT).fieldOf("min_y")).forGetter(GenerationShapeConfig::minimumY), ((MapCodec)Codec.intRange(0, DimensionType.MAX_HEIGHT).fieldOf("height")).forGetter(GenerationShapeConfig::height), ((MapCodec)NoiseSamplingConfig.CODEC.fieldOf("sampling")).forGetter(GenerationShapeConfig::sampling), ((MapCodec)SlideConfig.CODEC.fieldOf("top_slide")).forGetter(GenerationShapeConfig::topSlide), ((MapCodec)SlideConfig.CODEC.fieldOf("bottom_slide")).forGetter(GenerationShapeConfig::bottomSlide), ((MapCodec)Codec.intRange(1, 4).fieldOf("size_horizontal")).forGetter(GenerationShapeConfig::horizontalSize), ((MapCodec)Codec.intRange(1, 4).fieldOf("size_vertical")).forGetter(GenerationShapeConfig::verticalSize), ((MapCodec)VanillaTerrainParameters.CODEC.fieldOf("terrain_shaper")).forGetter(GenerationShapeConfig::terrainParameters)).apply((Applicative<GenerationShapeConfig, ?>)instance, GenerationShapeConfig::new)).comapFlatMap(GenerationShapeConfig::checkHeight, Function.identity());
    static final GenerationShapeConfig field_37138 = GenerationShapeConfig.create(0, 128, new NoiseSamplingConfig(1.0, 3.0, 80.0, 60.0), new SlideConfig(0.9375, 3, 0), new SlideConfig(2.5, 4, -1), 1, 2, VanillaTerrainParametersCreator.createNetherParameters());
    static final GenerationShapeConfig field_37139 = GenerationShapeConfig.create(0, 128, new NoiseSamplingConfig(2.0, 1.0, 80.0, 160.0), new SlideConfig(-23.4375, 64, -46), new SlideConfig(-0.234375, 7, 1), 2, 1, VanillaTerrainParametersCreator.createEndParameters());
    static final GenerationShapeConfig field_37140 = GenerationShapeConfig.create(-64, 192, new NoiseSamplingConfig(1.0, 3.0, 80.0, 60.0), new SlideConfig(0.9375, 3, 0), new SlideConfig(2.5, 4, -1), 1, 2, VanillaTerrainParametersCreator.createCavesParameters());
    static final GenerationShapeConfig field_37141 = GenerationShapeConfig.create(0, 256, new NoiseSamplingConfig(2.0, 1.0, 80.0, 160.0), new SlideConfig(-23.4375, 64, -46), new SlideConfig(-0.234375, 7, 1), 2, 1, VanillaTerrainParametersCreator.createFloatingIslandsParameters());

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

    public static GenerationShapeConfig create(int minimumY, int height, NoiseSamplingConfig sampling, SlideConfig topSlide, SlideConfig bottomSlide, int horizontalSize, int verticalSize, VanillaTerrainParameters vanillaTerrainParameters) {
        GenerationShapeConfig generationShapeConfig = new GenerationShapeConfig(minimumY, height, sampling, topSlide, bottomSlide, horizontalSize, verticalSize, vanillaTerrainParameters);
        GenerationShapeConfig.checkHeight(generationShapeConfig).error().ifPresent(partialResult -> {
            throw new IllegalStateException(partialResult.message());
        });
        return generationShapeConfig;
    }

    static GenerationShapeConfig method_41126(boolean bl) {
        return GenerationShapeConfig.create(-64, 384, new NoiseSamplingConfig(1.0, 1.0, 80.0, 160.0), new SlideConfig(-0.078125, 2, bl ? 0 : 8), new SlideConfig(bl ? 0.4 : 0.1171875, 3, 0), 1, 2, VanillaTerrainParametersCreator.createSurfaceParameters(bl));
    }

    public int verticalBlockSize() {
        return BiomeCoords.toBlock(this.verticalSize());
    }

    public int horizontalBlockSize() {
        return BiomeCoords.toBlock(this.horizontalSize());
    }

    public int verticalBlockCount() {
        return this.height() / this.verticalBlockSize();
    }

    public int minimumBlockY() {
        return MathHelper.floorDiv(this.minimumY(), this.verticalBlockSize());
    }
}

