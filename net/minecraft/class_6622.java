/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import java.util.function.Predicate;
import net.minecraft.class_6626;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.random.ChunkRandom;

@FunctionalInterface
public interface class_6622<C extends FeatureConfig> {
    public void generatePieces(class_6626 var1, C var2, class_6623 var3);

    public record class_6623(DynamicRegistryManager registryAccess, ChunkGenerator chunkGenerator, StructureManager structureManager, ChunkPos chunkPos, Predicate<Biome> validBiome, HeightLimitView heightAccessor, ChunkRandom random, long seed) {
        public boolean method_38707(Heightmap.Type type) {
            int i = this.chunkPos.getCenterX();
            int j = this.chunkPos.getCenterZ();
            int k = this.chunkGenerator.getHeightInGround(i, j, type, this.heightAccessor);
            Biome biome = this.chunkGenerator.getBiomeForNoiseGen(BiomeCoords.fromBlock(i), BiomeCoords.fromBlock(k), BiomeCoords.fromBlock(j));
            return this.validBiome.test(biome);
        }

        public int method_38705(int i, int j) {
            int k = this.chunkPos.getStartX();
            int l = this.chunkPos.getStartZ();
            int[] is = this.method_38706(k, i, l, j);
            return Math.min(Math.min(is[0], is[1]), Math.min(is[2], is[3]));
        }

        public int[] method_38706(int i, int j, int k, int l) {
            return new int[]{this.chunkGenerator.getHeightInGround(i, k, Heightmap.Type.WORLD_SURFACE_WG, this.heightAccessor), this.chunkGenerator.getHeightInGround(i, k + l, Heightmap.Type.WORLD_SURFACE_WG, this.heightAccessor), this.chunkGenerator.getHeightInGround(i + j, k, Heightmap.Type.WORLD_SURFACE_WG, this.heightAccessor), this.chunkGenerator.getHeightInGround(i + j, k + l, Heightmap.Type.WORLD_SURFACE_WG, this.heightAccessor)};
        }
    }
}

