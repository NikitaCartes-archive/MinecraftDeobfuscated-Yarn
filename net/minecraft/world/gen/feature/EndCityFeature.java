/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.Random;
import java.util.function.Predicate;
import net.minecraft.structure.EndCityGenerator;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.random.ChunkRandom;

public class EndCityFeature
extends StructureFeature<DefaultFeatureConfig> {
    private static final int field_31502 = 10387313;

    public EndCityFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec);
    }

    @Override
    protected boolean isUniformDistribution() {
        return false;
    }

    @Override
    protected boolean shouldStartAt(ChunkGenerator chunkGenerator, BiomeSource biomeSource, long l, ChunkRandom chunkRandom, ChunkPos chunkPos, ChunkPos chunkPos2, DefaultFeatureConfig defaultFeatureConfig, HeightLimitView heightLimitView) {
        return EndCityFeature.getGenerationHeight(chunkPos, chunkGenerator, heightLimitView) >= 60;
    }

    @Override
    public StructureFeature.StructureStartFactory<DefaultFeatureConfig> getStructureStartFactory() {
        return Start::new;
    }

    static int getGenerationHeight(ChunkPos chunkPos, ChunkGenerator chunkGenerator, HeightLimitView heightLimitView) {
        Random random = new Random(chunkPos.x + chunkPos.z * 10387313);
        BlockRotation blockRotation = BlockRotation.random(random);
        int i = 5;
        int j = 5;
        if (blockRotation == BlockRotation.CLOCKWISE_90) {
            i = -5;
        } else if (blockRotation == BlockRotation.CLOCKWISE_180) {
            i = -5;
            j = -5;
        } else if (blockRotation == BlockRotation.COUNTERCLOCKWISE_90) {
            j = -5;
        }
        int k = chunkPos.getOffsetX(7);
        int l = chunkPos.getOffsetZ(7);
        int m = chunkGenerator.getHeightInGround(k, l, Heightmap.Type.WORLD_SURFACE_WG, heightLimitView);
        int n = chunkGenerator.getHeightInGround(k, l + j, Heightmap.Type.WORLD_SURFACE_WG, heightLimitView);
        int o = chunkGenerator.getHeightInGround(k + i, l, Heightmap.Type.WORLD_SURFACE_WG, heightLimitView);
        int p = chunkGenerator.getHeightInGround(k + i, l + j, Heightmap.Type.WORLD_SURFACE_WG, heightLimitView);
        return Math.min(Math.min(m, n), Math.min(o, p));
    }

    public static class Start
    extends StructureStart<DefaultFeatureConfig> {
        public Start(StructureFeature<DefaultFeatureConfig> structureFeature, ChunkPos chunkPos, int i, long l) {
            super(structureFeature, chunkPos, i, l);
        }

        @Override
        public void init(DynamicRegistryManager dynamicRegistryManager, ChunkGenerator chunkGenerator, StructureManager structureManager, ChunkPos chunkPos, DefaultFeatureConfig defaultFeatureConfig, HeightLimitView heightLimitView, Predicate<Biome> predicate) {
            BlockRotation blockRotation = BlockRotation.random(this.random);
            int i = EndCityFeature.getGenerationHeight(chunkPos, chunkGenerator, heightLimitView);
            if (i < 60) {
                return;
            }
            BlockPos blockPos = chunkPos.getCenterAtY(i);
            if (!predicate.test(chunkGenerator.getBiomeForNoiseGen(BiomeCoords.fromBlock(blockPos.getX()), BiomeCoords.fromBlock(blockPos.getY()), BiomeCoords.fromBlock(blockPos.getZ())))) {
                return;
            }
            ArrayList<StructurePiece> list = Lists.newArrayList();
            EndCityGenerator.addPieces(structureManager, blockPos, blockRotation, list, this.random);
            list.forEach(this::addPiece);
        }
    }
}

