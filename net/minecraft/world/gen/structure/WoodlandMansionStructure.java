/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.structure;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.LinkedList;
import java.util.Optional;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.structure.StructurePiecesList;
import net.minecraft.structure.WoodlandMansionGenerator;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.structure.StructureType;

public class WoodlandMansionStructure
extends StructureType {
    public static final Codec<WoodlandMansionStructure> CODEC = WoodlandMansionStructure.createCodec(WoodlandMansionStructure::new);

    public WoodlandMansionStructure(StructureType.Config config) {
        super(config);
    }

    @Override
    public Optional<StructureType.StructurePosition> getStructurePosition(StructureType.Context context) {
        BlockRotation blockRotation = BlockRotation.random(context.random());
        BlockPos blockPos = this.getShiftedPos(context, blockRotation);
        if (blockPos.getY() < 60) {
            return Optional.empty();
        }
        return Optional.of(new StructureType.StructurePosition(blockPos, collector -> this.addPieces((StructurePiecesCollector)collector, context, blockPos, blockRotation)));
    }

    private void addPieces(StructurePiecesCollector collector, StructureType.Context context, BlockPos pos, BlockRotation rotation) {
        LinkedList<WoodlandMansionGenerator.Piece> list = Lists.newLinkedList();
        WoodlandMansionGenerator.addPieces(context.structureManager(), pos, rotation, list, context.random());
        list.forEach(collector::addPiece);
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
    public net.minecraft.structure.StructureType<?> getType() {
        return net.minecraft.structure.StructureType.WOODLAND_MANSION;
    }
}

