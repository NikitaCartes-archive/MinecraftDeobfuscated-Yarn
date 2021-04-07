/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.JigsawFeature;
import net.minecraft.world.gen.feature.StructurePoolFeatureConfig;

public class BastionRemnantFeature
extends JigsawFeature {
    private static final int STRUCTURE_START_Y = 33;

    public BastionRemnantFeature(Codec<StructurePoolFeatureConfig> codec) {
        super(codec, 33, false, false);
    }

    @Override
    protected boolean shouldStartAt(ChunkGenerator chunkGenerator, BiomeSource biomeSource, long l, ChunkRandom chunkRandom, ChunkPos chunkPos, Biome biome, ChunkPos chunkPos2, StructurePoolFeatureConfig structurePoolFeatureConfig, HeightLimitView heightLimitView) {
        return chunkRandom.nextInt(5) >= 2;
    }
}

