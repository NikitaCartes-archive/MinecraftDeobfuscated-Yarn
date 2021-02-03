/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.StructureConfig;
import net.minecraft.world.gen.feature.JigsawFeature;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.StructurePoolFeatureConfig;

public class PillagerOutpostFeature
extends JigsawFeature {
    private static final List<SpawnSettings.SpawnEntry> MONSTER_SPAWNS = ImmutableList.of(new SpawnSettings.SpawnEntry(EntityType.PILLAGER, 1, 1, 1));

    public PillagerOutpostFeature(Codec<StructurePoolFeatureConfig> codec) {
        super(codec, 0, true, true);
    }

    @Override
    public List<SpawnSettings.SpawnEntry> getMonsterSpawns() {
        return MONSTER_SPAWNS;
    }

    @Override
    protected boolean shouldStartAt(ChunkGenerator chunkGenerator, BiomeSource biomeSource, long l, ChunkRandom chunkRandom, int i, int j, Biome biome, ChunkPos chunkPos, StructurePoolFeatureConfig structurePoolFeatureConfig, HeightLimitView heightLimitView) {
        int k = ChunkSectionPos.getSectionCoord(i);
        int m = ChunkSectionPos.getSectionCoord(j);
        chunkRandom.setSeed((long)(k ^ m << 4) ^ l);
        chunkRandom.nextInt();
        if (chunkRandom.nextInt(5) != 0) {
            return false;
        }
        return !this.method_30845(chunkGenerator, l, chunkRandom, i, j);
    }

    private boolean method_30845(ChunkGenerator chunkGenerator, long l, ChunkRandom chunkRandom, int i, int j) {
        StructureConfig structureConfig = chunkGenerator.getStructuresConfig().getForType(StructureFeature.VILLAGE);
        if (structureConfig == null) {
            return false;
        }
        for (int k = i - 10; k <= i + 10; ++k) {
            for (int m = j - 10; m <= j + 10; ++m) {
                ChunkPos chunkPos = StructureFeature.VILLAGE.getStartChunk(structureConfig, l, chunkRandom, k, m);
                if (k != chunkPos.x || m != chunkPos.z) continue;
                return true;
            }
        }
        return false;
    }
}

