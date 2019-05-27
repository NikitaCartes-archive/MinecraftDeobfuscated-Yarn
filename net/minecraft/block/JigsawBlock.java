/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.FacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.JigsawBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.structure.Structure;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class JigsawBlock
extends FacingBlock
implements BlockEntityProvider {
    protected JigsawBlock(Block.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)this.stateFactory.getDefaultState()).with(FACING, Direction.UP));
    }

    @Override
    protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState rotate(BlockState blockState, BlockRotation blockRotation) {
        return (BlockState)blockState.with(FACING, blockRotation.rotate(blockState.get(FACING)));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
        return (BlockState)this.getDefaultState().with(FACING, itemPlacementContext.getSide());
    }

    @Override
    @Nullable
    public BlockEntity createBlockEntity(BlockView blockView) {
        return new JigsawBlockEntity();
    }

    @Override
    public boolean activate(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
        BlockEntity blockEntity = world.getBlockEntity(blockPos);
        if (blockEntity instanceof JigsawBlockEntity && playerEntity.isCreativeLevelTwoOp()) {
            playerEntity.openJigsawScreen((JigsawBlockEntity)blockEntity);
            return true;
        }
        return false;
    }

    public static boolean attachmentMatches(Structure.StructureBlockInfo structureBlockInfo, Structure.StructureBlockInfo structureBlockInfo2) {
        return structureBlockInfo.state.get(FACING) == structureBlockInfo2.state.get(FACING).getOpposite() && structureBlockInfo.tag.getString("attachement_type").equals(structureBlockInfo2.tag.getString("attachement_type"));
    }
}

