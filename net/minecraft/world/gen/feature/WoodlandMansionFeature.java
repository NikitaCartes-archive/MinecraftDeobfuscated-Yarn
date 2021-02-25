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
import net.minecraft.world.HeightLimitView;
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
    protected boolean shouldStartAt(ChunkGenerator chunkGenerator, BiomeSource biomeSource, long l, ChunkRandom chunkRandom, ChunkPos chunkPos, Biome biome, ChunkPos chunkPos2, DefaultFeatureConfig defaultFeatureConfig, HeightLimitView heightLimitView) {
        Set<Biome> set = biomeSource.getBiomesInArea(chunkPos.getOffsetX(9), chunkGenerator.getSeaLevel(), chunkPos.getOffsetZ(9), 32);
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
        public Start(StructureFeature<DefaultFeatureConfig> structureFeature, ChunkPos chunkPos, BlockBox blockBox, int i, long l) {
            super(structureFeature, chunkPos, blockBox, i, l);
        }

        @Override
        public void init(DynamicRegistryManager dynamicRegistryManager, ChunkGenerator chunkGenerator, StructureManager structureManager, ChunkPos chunkPos, Biome biome, DefaultFeatureConfig defaultFeatureConfig, HeightLimitView heightLimitView) {
            BlockRotation blockRotation = BlockRotation.random(this.random);
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
            int q = Math.min(Math.min(m, n), Math.min(o, p));
            if (q < 60) {
                return;
            }
            BlockPos blockPos = new BlockPos(chunkPos.getOffsetX(8), q + 1, chunkPos.getOffsetZ(8));
            LinkedList<WoodlandMansionGenerator.Piece> list = Lists.newLinkedList();
            WoodlandMansionGenerator.addPieces(structureManager, blockPos, blockRotation, list, this.random);
            this.children.addAll(list);
            this.setBoundingBoxFromChildren();
        }

        @Override
        public void generateStructure(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox box, ChunkPos chunkPos) {
            super.generateStructure(world, structureAccessor, chunkGenerator, random, box, chunkPos);
            int i = this.boundingBox.minY;
            for (int j = box.minX; j <= box.maxX; ++j) {
                for (int k = box.minZ; k <= box.maxZ; ++k) {
                    BlockPos blockPos2;
                    BlockPos blockPos = new BlockPos(j, i, k);
                    if (world.isAir(blockPos) || !this.boundingBox.contains(blockPos)) continue;
                    boolean bl = false;
                    for (StructurePiece structurePiece : this.children) {
                        if (!structurePiece.getBoundingBox().contains(blockPos)) continue;
                        bl = true;
                        break;
                    }
                    if (!bl) continue;
                    for (int l = i - 1; l > 1 && (world.isAir(blockPos2 = new BlockPos(j, l, k)) || world.getBlockState(blockPos2).getMaterial().isLiquid()); --l) {
                        world.setBlockState(blockPos2, Blocks.COBBLESTONE.getDefaultState(), 2);
                    }
                }
            }
        }
    }
}

