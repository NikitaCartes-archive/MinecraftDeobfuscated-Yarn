/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiecesGenerator;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.FeatureConfig;

@FunctionalInterface
public interface class_6834<C extends FeatureConfig> {
    public Optional<StructurePiecesGenerator<C>> createGenerator(class_6835<C> var1);

    public static <C extends FeatureConfig> class_6834<C> simple(Predicate<class_6835<C>> predicate, StructurePiecesGenerator<C> structurePiecesGenerator) {
        Optional optional = Optional.of(structurePiecesGenerator);
        return arg -> predicate.test(arg) ? optional : Optional.empty();
    }

    public static <C extends FeatureConfig> Predicate<class_6835<C>> checkForBiomeOnTop(Heightmap.Type type) {
        return arg -> arg.method_39848(type);
    }

    public record class_6835<C extends FeatureConfig>(ChunkGenerator chunkGenerator, BiomeSource biomeSource, long seed, ChunkPos chunkPos, C config, HeightLimitView heightAccessor, Predicate<Biome> validBiome, StructureManager structureManager, DynamicRegistryManager registryAccess) {
        public boolean method_39848(Heightmap.Type type) {
            int i = this.chunkPos.getCenterX();
            int j = this.chunkPos.getCenterZ();
            int k = this.chunkGenerator.getHeightInGround(i, j, type, this.heightAccessor);
            Biome biome = this.chunkGenerator.getBiomeForNoiseGen(BiomeCoords.fromBlock(i), BiomeCoords.fromBlock(k), BiomeCoords.fromBlock(j));
            return this.validBiome.test(biome);
        }

        public int[] method_39847(int i, int j, int k, int l) {
            return new int[]{this.chunkGenerator.getHeightInGround(i, k, Heightmap.Type.WORLD_SURFACE_WG, this.heightAccessor), this.chunkGenerator.getHeightInGround(i, k + l, Heightmap.Type.WORLD_SURFACE_WG, this.heightAccessor), this.chunkGenerator.getHeightInGround(i + j, k, Heightmap.Type.WORLD_SURFACE_WG, this.heightAccessor), this.chunkGenerator.getHeightInGround(i + j, k + l, Heightmap.Type.WORLD_SURFACE_WG, this.heightAccessor)};
        }

        public int method_39846(int i, int j) {
            int k = this.chunkPos.getStartX();
            int l = this.chunkPos.getStartZ();
            int[] is = this.method_39847(k, i, l, j);
            return Math.min(Math.min(is[0], is[1]), Math.min(is[2], is[3]));
        }
    }
}

