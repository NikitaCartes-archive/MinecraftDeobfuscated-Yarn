/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.LinkedList;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.structure.StructurePiecesGenerator;
import net.minecraft.structure.StructurePiecesList;
import net.minecraft.structure.WoodlandMansionGenerator;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

public class WoodlandMansionFeature
extends StructureFeature<DefaultFeatureConfig> {
    public WoodlandMansionFeature(Codec<DefaultFeatureConfig> configCodec) {
        super(configCodec, WoodlandMansionFeature::addPieces, WoodlandMansionFeature::postPlace);
    }

    @Override
    protected boolean isUniformDistribution() {
        return false;
    }

    private static void addPieces(StructurePiecesCollector collector, DefaultFeatureConfig config, StructurePiecesGenerator.Context context) {
        BlockRotation blockRotation = BlockRotation.random(context.random());
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
        int k = context.chunkPos().getOffsetX(7);
        int l = context.chunkPos().getOffsetZ(7);
        int[] is = context.getHeightsInGround(k, i, l, j);
        int m = Math.min(Math.min(is[0], is[1]), Math.min(is[2], is[3]));
        if (m < 60) {
            return;
        }
        if (!context.biomeLimit().test(context.chunkGenerator().getBiomeForNoiseGen(BiomeCoords.fromBlock(k), BiomeCoords.fromBlock(is[0]), BiomeCoords.fromBlock(l)))) {
            return;
        }
        BlockPos blockPos = new BlockPos(context.chunkPos().getCenterX(), m + 1, context.chunkPos().getCenterZ());
        LinkedList<WoodlandMansionGenerator.Piece> list = Lists.newLinkedList();
        WoodlandMansionGenerator.addPieces(context.structureManager(), blockPos, blockRotation, list, context.random());
        list.forEach(collector::addPiece);
    }

    private static void postPlace(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, StructurePiecesList children) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        int i = world.getBottomY();
        BlockBox blockBox = children.getBoundingBox();
        int j = blockBox.getMinY();
        for (int k = chunkBox.getMinX(); k <= chunkBox.getMaxX(); ++k) {
            block1: for (int l = chunkBox.getMinZ(); l <= chunkBox.getMaxZ(); ++l) {
                mutable.set(k, j, l);
                if (world.isAir(mutable) || !blockBox.contains(mutable) || !children.contains(mutable)) continue;
                for (int m = j - 1; m > i; --m) {
                    mutable.setY(m);
                    if (!world.isAir(mutable) && !world.getBlockState(mutable).getMaterial().isLiquid()) continue block1;
                    world.setBlockState(mutable, Blocks.COBBLESTONE.getDefaultState(), Block.NOTIFY_LISTENERS);
                }
            }
        }
    }
}

