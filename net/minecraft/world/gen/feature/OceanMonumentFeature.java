/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.entity.EntityType;
import net.minecraft.structure.OceanMonumentGenerator;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.collection.Weighted;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.random.ChunkRandom;

public class OceanMonumentFeature
extends StructureFeature<DefaultFeatureConfig> {
    private static final Pool<SpawnSettings.SpawnEntry> MONSTER_SPAWNS = Pool.of((Weighted[])new SpawnSettings.SpawnEntry[]{new SpawnSettings.SpawnEntry(EntityType.GUARDIAN, 1, 2, 4)});

    public OceanMonumentFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec);
    }

    @Override
    protected boolean isUniformDistribution() {
        return false;
    }

    @Override
    protected boolean shouldStartAt(ChunkGenerator chunkGenerator, BiomeSource biomeSource, long l, ChunkRandom chunkRandom, ChunkPos chunkPos, ChunkPos chunkPos2, DefaultFeatureConfig defaultFeatureConfig, HeightLimitView heightLimitView) {
        int i = chunkPos.getOffsetX(9);
        int j = chunkPos.getOffsetZ(9);
        Set<Biome> set = biomeSource.getBiomesInArea(i, chunkGenerator.getSeaLevel(), j, 29, chunkGenerator.method_38276());
        for (Biome biome : set) {
            if (biome.getCategory() == Biome.Category.OCEAN || biome.getCategory() == Biome.Category.RIVER) continue;
            return false;
        }
        return true;
    }

    @Override
    public StructureFeature.StructureStartFactory<DefaultFeatureConfig> getStructureStartFactory() {
        return Start::new;
    }

    @Override
    public Pool<SpawnSettings.SpawnEntry> getMonsterSpawns() {
        return MONSTER_SPAWNS;
    }

    public static class Start
    extends StructureStart<DefaultFeatureConfig> {
        private boolean field_33415;

        public Start(StructureFeature<DefaultFeatureConfig> structureFeature, ChunkPos chunkPos, int i, long l) {
            super(structureFeature, chunkPos, i, l);
        }

        @Override
        public void init(DynamicRegistryManager dynamicRegistryManager, ChunkGenerator chunkGenerator, StructureManager structureManager, ChunkPos chunkPos, DefaultFeatureConfig defaultFeatureConfig, HeightLimitView heightLimitView, Predicate<Biome> predicate) {
            this.method_36216(chunkGenerator, heightLimitView, predicate, chunkPos);
        }

        private void method_36216(ChunkGenerator chunkGenerator, HeightLimitView heightLimitView, Predicate<Biome> predicate, ChunkPos chunkPos) {
            if (!StructureFeature.checkBiome(chunkGenerator, heightLimitView, predicate, Heightmap.Type.OCEAN_FLOOR_WG, chunkPos.getCenterX(), chunkPos.getCenterZ())) {
                return;
            }
            int i = chunkPos.getStartX() - 29;
            int j = chunkPos.getStartZ() - 29;
            Direction direction = Direction.Type.HORIZONTAL.random(this.random);
            this.addPiece(new OceanMonumentGenerator.Base(this.random, i, j, direction));
            this.field_33415 = true;
        }

        @Override
        public void generateStructure(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, Predicate<Biome> predicate, BlockBox blockBox, ChunkPos chunkPos) {
            if (!this.field_33415) {
                this.children.clear();
                this.method_36216(chunkGenerator, world, predicate, this.getPos());
            }
            super.generateStructure(world, structureAccessor, chunkGenerator, random, predicate, blockBox, chunkPos);
        }
    }
}

