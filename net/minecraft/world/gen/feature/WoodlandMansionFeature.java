/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.Blocks;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructureStart;
import net.minecraft.structure.WoodlandMansionGenerator;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

public class WoodlandMansionFeature
extends StructureFeature<DefaultFeatureConfig> {
    public WoodlandMansionFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec);
    }

    @Override
    protected boolean isUniformDistribution() {
        return false;
    }

    @Override
    protected boolean shouldStartAt(ChunkGenerator chunkGenerator, BiomeSource biomeSource, long l, ChunkRandom chunkRandom, int i, int j, Biome biome, ChunkPos chunkPos, DefaultFeatureConfig defaultFeatureConfig) {
        Set<Biome> set = biomeSource.getBiomesInArea(i * 16 + 9, chunkGenerator.getSeaLevel(), j * 16 + 9, 32);
        for (Biome biome2 : set) {
            if (biome2.getGenerationSettings().hasStructureFeature(this)) continue;
            return false;
        }
        return true;
    }

    @Override
    public StructureFeature.StructureStartFactory<DefaultFeatureConfig> getStructureStartFactory() {
        return Start::new;
    }

    public static class Start
    extends StructureStart<DefaultFeatureConfig> {
        public Start(StructureFeature<DefaultFeatureConfig> structureFeature, int i, int j, BlockBox blockBox, int k, long l) {
            super(structureFeature, i, j, blockBox, k, l);
        }

        @Override
        public void init(DynamicRegistryManager dynamicRegistryManager, ChunkGenerator chunkGenerator, StructureManager structureManager, int i, int j, Biome biome, DefaultFeatureConfig defaultFeatureConfig) {
            BlockRotation blockRotation = BlockRotation.random(this.random);
            int k = 5;
            int l = 5;
            if (blockRotation == BlockRotation.CLOCKWISE_90) {
                k = -5;
            } else if (blockRotation == BlockRotation.CLOCKWISE_180) {
                k = -5;
                l = -5;
            } else if (blockRotation == BlockRotation.COUNTERCLOCKWISE_90) {
                l = -5;
            }
            int m = (i << 4) + 7;
            int n = (j << 4) + 7;
            int o = chunkGenerator.getHeightInGround(m, n, Heightmap.Type.WORLD_SURFACE_WG);
            int p = chunkGenerator.getHeightInGround(m, n + l, Heightmap.Type.WORLD_SURFACE_WG);
            int q = chunkGenerator.getHeightInGround(m + k, n, Heightmap.Type.WORLD_SURFACE_WG);
            int r = chunkGenerator.getHeightInGround(m + k, n + l, Heightmap.Type.WORLD_SURFACE_WG);
            int s = Math.min(Math.min(o, p), Math.min(q, r));
            if (s < 60) {
                return;
            }
            BlockPos blockPos = new BlockPos(i * 16 + 8, s + 1, j * 16 + 8);
            LinkedList<WoodlandMansionGenerator.Piece> list = Lists.newLinkedList();
            WoodlandMansionGenerator.addPieces(structureManager, blockPos, blockRotation, list, this.random);
            this.children.addAll(list);
            this.setBoundingBoxFromChildren();
        }

        @Override
        public void generateStructure(StructureWorldAccess structureWorldAccess, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox blockBox, ChunkPos chunkPos) {
            super.generateStructure(structureWorldAccess, structureAccessor, chunkGenerator, random, blockBox, chunkPos);
            int i = this.boundingBox.minY;
            for (int j = blockBox.minX; j <= blockBox.maxX; ++j) {
                for (int k = blockBox.minZ; k <= blockBox.maxZ; ++k) {
                    BlockPos blockPos2;
                    BlockPos blockPos = new BlockPos(j, i, k);
                    if (structureWorldAccess.isAir(blockPos) || !this.boundingBox.contains(blockPos)) continue;
                    boolean bl = false;
                    for (StructurePiece structurePiece : this.children) {
                        if (!structurePiece.getBoundingBox().contains(blockPos)) continue;
                        bl = true;
                        break;
                    }
                    if (!bl) continue;
                    for (int l = i - 1; l > 1 && (structureWorldAccess.isAir(blockPos2 = new BlockPos(j, l, k)) || structureWorldAccess.getBlockState(blockPos2).getMaterial().isLiquid()); --l) {
                        structureWorldAccess.setBlockState(blockPos2, Blocks.COBBLESTONE.getDefaultState(), 2);
                    }
                }
            }
        }
    }
}

