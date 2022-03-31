/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.structure.processor;

import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class LavaSubmergedBlockStructureProcessor
extends StructureProcessor {
    public static final Codec<LavaSubmergedBlockStructureProcessor> CODEC = Codec.unit(() -> INSTANCE);
    public static final LavaSubmergedBlockStructureProcessor INSTANCE = new LavaSubmergedBlockStructureProcessor();

    @Override
    @Nullable
    public Structure.StructureBlockInfo process(WorldView world, BlockPos pos, BlockPos pivot, Structure.StructureBlockInfo originalBlockInfo, Structure.StructureBlockInfo currentBlockInfo, StructurePlacementData data) {
        BlockPos blockPos = currentBlockInfo.pos;
        boolean bl = world.getBlockState(blockPos).isOf(Blocks.LAVA);
        if (bl && !Block.isShapeFullCube(currentBlockInfo.state.getOutlineShape(world, blockPos))) {
            return new Structure.StructureBlockInfo(blockPos, Blocks.LAVA.getDefaultState(), currentBlockInfo.nbt);
        }
        return currentBlockInfo;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return StructureProcessorType.LAVA_SUBMERGED_BLOCK;
    }
}

