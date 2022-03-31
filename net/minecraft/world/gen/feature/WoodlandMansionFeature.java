/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.structure.StructurePiecesList;
import net.minecraft.structure.StructureType;
import net.minecraft.structure.WoodlandMansionGenerator;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.StructureFeature;

public class WoodlandMansionFeature
extends StructureFeature {
    public static final Codec<WoodlandMansionFeature> CODEC = WoodlandMansionFeature.createCodec(WoodlandMansionFeature::new);

    public WoodlandMansionFeature(StructureFeature.Config config) {
        super(config);
    }

    @Override
    public Optional<StructureFeature.StructurePosition> getStructurePosition(StructureFeature.Context context) {
        BlockRotation blockRotation = BlockRotation.random(context.random());
        BlockPos blockPos = this.getShiftedPos(context, blockRotation);
        if (blockPos.getY() < 60) {
            return Optional.empty();
        }
        return Optional.of(new StructureFeature.StructurePosition(blockPos, structurePiecesCollector -> this.method_41696((StructurePiecesCollector)structurePiecesCollector, context, blockPos, blockRotation)));
    }

    private void method_41696(StructurePiecesCollector structurePiecesCollector, StructureFeature.Context context, BlockPos blockPos, BlockRotation blockRotation) {
        LinkedList<WoodlandMansionGenerator.Piece> list = Lists.newLinkedList();
        WoodlandMansionGenerator.addPieces(context.structureManager(), blockPos, blockRotation, list, context.random());
        list.forEach(structurePiecesCollector::addPiece);
    }

    @Override
    public void postPlace(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox box, ChunkPos chunkPos, StructurePiecesList pieces) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        int i = world.getBottomY();
        BlockBox blockBox = pieces.getBoundingBox();
        int j = blockBox.getMinY();
        for (int k = box.getMinX(); k <= box.getMaxX(); ++k) {
            block1: for (int l = box.getMinZ(); l <= box.getMaxZ(); ++l) {
                mutable.set(k, j, l);
                if (world.isAir(mutable) || !blockBox.contains(mutable) || !pieces.contains(mutable)) continue;
                for (int m = j - 1; m > i; --m) {
                    mutable.setY(m);
                    if (!world.isAir(mutable) && !world.getBlockState(mutable).getMaterial().isLiquid()) continue block1;
                    world.setBlockState(mutable, Blocks.COBBLESTONE.getDefaultState(), Block.NOTIFY_LISTENERS);
                }
            }
        }
    }

    @Override
    public StructureType<?> getType() {
        return StructureType.WOODLAND_MANSION;
    }
}

