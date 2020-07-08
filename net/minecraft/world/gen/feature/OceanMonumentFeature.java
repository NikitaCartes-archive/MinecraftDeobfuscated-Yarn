/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.entity.EntityType;
import net.minecraft.structure.OceanMonumentGenerator;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

public class OceanMonumentFeature
extends StructureFeature<DefaultFeatureConfig> {
    private static final List<Biome.SpawnEntry> MONSTER_SPAWNS = Lists.newArrayList(new Biome.SpawnEntry(EntityType.GUARDIAN, 1, 2, 4));

    public OceanMonumentFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec);
    }

    @Override
    protected boolean isUniformDistribution() {
        return false;
    }

    @Override
    protected boolean shouldStartAt(ChunkGenerator chunkGenerator, BiomeSource biomeSource, long l, ChunkRandom chunkRandom, int i, int j, Biome biome, ChunkPos chunkPos, DefaultFeatureConfig defaultFeatureConfig) {
        Set<Biome> set = biomeSource.getBiomesInArea(i * 16 + 9, chunkGenerator.getSeaLevel(), j * 16 + 9, 16);
        for (Biome biome2 : set) {
            if (biome2.hasStructureFeature(this)) continue;
            return false;
        }
        Set<Biome> set2 = biomeSource.getBiomesInArea(i * 16 + 9, chunkGenerator.getSeaLevel(), j * 16 + 9, 29);
        for (Biome biome3 : set2) {
            if (biome3.getCategory() == Biome.Category.OCEAN || biome3.getCategory() == Biome.Category.RIVER) continue;
            return false;
        }
        return true;
    }

    @Override
    public StructureFeature.StructureStartFactory<DefaultFeatureConfig> getStructureStartFactory() {
        return Start::new;
    }

    @Override
    public List<Biome.SpawnEntry> getMonsterSpawns() {
        return MONSTER_SPAWNS;
    }

    public static class Start
    extends StructureStart<DefaultFeatureConfig> {
        private boolean field_13717;

        public Start(StructureFeature<DefaultFeatureConfig> structureFeature, int i, int j, BlockBox blockBox, int k, long l) {
            super(structureFeature, i, j, blockBox, k, l);
        }

        @Override
        public void init(DynamicRegistryManager dynamicRegistryManager, ChunkGenerator chunkGenerator, StructureManager structureManager, int i, int j, Biome biome, DefaultFeatureConfig defaultFeatureConfig) {
            this.method_16588(i, j);
        }

        private void method_16588(int chunkX, int chunkZ) {
            int i = chunkX * 16 - 29;
            int j = chunkZ * 16 - 29;
            Direction direction = Direction.Type.HORIZONTAL.random(this.random);
            this.children.add(new OceanMonumentGenerator.Base(this.random, i, j, direction));
            this.setBoundingBoxFromChildren();
            this.field_13717 = true;
        }

        @Override
        public void generateStructure(ServerWorldAccess serverWorldAccess, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox blockBox, ChunkPos chunkPos) {
            if (!this.field_13717) {
                this.children.clear();
                this.method_16588(this.getChunkX(), this.getChunkZ());
            }
            super.generateStructure(serverWorldAccess, structureAccessor, chunkGenerator, random, blockBox, chunkPos);
        }
    }
}

