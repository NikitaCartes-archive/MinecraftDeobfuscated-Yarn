/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.class_5434;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.StructurePoolFeatureConfig;

public class PillagerOutpostFeature
extends class_5434 {
    private static final List<Biome.SpawnEntry> MONSTER_SPAWNS = Lists.newArrayList(new Biome.SpawnEntry(EntityType.PILLAGER, 1, 1, 1));

    public PillagerOutpostFeature(Codec<StructurePoolFeatureConfig> codec) {
        super(codec, 0, true, true);
    }

    @Override
    public List<Biome.SpawnEntry> getMonsterSpawns() {
        return MONSTER_SPAWNS;
    }

    @Override
    protected boolean shouldStartAt(ChunkGenerator chunkGenerator, BiomeSource biomeSource, long l, ChunkRandom chunkRandom, int i, int j, Biome biome, ChunkPos chunkPos, StructurePoolFeatureConfig structurePoolFeatureConfig) {
        int k = i >> 4;
        int m = j >> 4;
        chunkRandom.setSeed((long)(k ^ m << 4) ^ l);
        chunkRandom.nextInt();
        if (chunkRandom.nextInt(5) != 0) {
            return false;
        }
        for (int n = i - 10; n <= i + 10; ++n) {
            for (int o = j - 10; o <= j + 10; ++o) {
                ChunkPos chunkPos2 = StructureFeature.VILLAGE.getStartChunk(chunkGenerator.getConfig().getForType(StructureFeature.VILLAGE), l, chunkRandom, n, o);
                if (n != chunkPos2.x || o != chunkPos2.z) continue;
                return false;
            }
        }
        return true;
    }
}

