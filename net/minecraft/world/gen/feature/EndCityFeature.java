/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;
import net.minecraft.structure.EndCityGenerator;
import net.minecraft.structure.StructureGeneratorFactory;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePiecesGenerator;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

public class EndCityFeature
extends StructureFeature<DefaultFeatureConfig> {
    private static final int Z_SEED_MULTIPLIER = 10387313;

    public EndCityFeature(Codec<DefaultFeatureConfig> configCodec) {
        super(configCodec, EndCityFeature::addPieces);
    }

    private static int getGenerationHeight(ChunkPos pos, ChunkGenerator chunkGenerator, HeightLimitView world) {
        Random random = new Random(pos.x + pos.z * 10387313);
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
        int k = pos.getOffsetX(7);
        int l = pos.getOffsetZ(7);
        int m = chunkGenerator.getHeightInGround(k, l, Heightmap.Type.WORLD_SURFACE_WG, world);
        int n = chunkGenerator.getHeightInGround(k, l + j, Heightmap.Type.WORLD_SURFACE_WG, world);
        int o = chunkGenerator.getHeightInGround(k + i, l, Heightmap.Type.WORLD_SURFACE_WG, world);
        int p = chunkGenerator.getHeightInGround(k + i, l + j, Heightmap.Type.WORLD_SURFACE_WG, world);
        return Math.min(Math.min(m, n), Math.min(o, p));
    }

    private static Optional<StructurePiecesGenerator<DefaultFeatureConfig>> addPieces(StructureGeneratorFactory.Context<DefaultFeatureConfig> context2) {
        int i = EndCityFeature.getGenerationHeight(context2.chunkPos(), context2.chunkGenerator(), context2.world());
        if (i < 60) {
            return Optional.empty();
        }
        BlockPos blockPos = context2.chunkPos().getCenterAtY(i);
        if (!context2.validBiome().test(context2.chunkGenerator().getBiomeForNoiseGen(BiomeCoords.fromBlock(blockPos.getX()), BiomeCoords.fromBlock(blockPos.getY()), BiomeCoords.fromBlock(blockPos.getZ())))) {
            return Optional.empty();
        }
        return Optional.of((collector, context) -> {
            BlockRotation blockRotation = BlockRotation.random(context.random());
            ArrayList<StructurePiece> list = Lists.newArrayList();
            EndCityGenerator.addPieces(context.structureManager(), blockPos, blockRotation, list, context.random());
            list.forEach(collector::addPiece);
        });
    }
}

