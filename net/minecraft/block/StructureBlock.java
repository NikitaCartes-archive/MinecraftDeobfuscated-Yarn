/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class StructureBlock
extends BlockWithEntity {
    public static final EnumProperty<StructureBlockMode> MODE = Properties.STRUCTURE_BLOCK_MODE;

    protected StructureBlock(Block.Settings settings) {
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView blockView) {
        return new StructureBlockBlockEntity();
    }

    @Override
    public boolean onUse(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
        BlockEntity blockEntity = world.getBlockEntity(blockPos);
        if (blockEntity instanceof StructureBlockBlockEntity) {
            return ((StructureBlockBlockEntity)blockEntity).openScreen(playerEntity);
        }
        return false;
    }

    @Override
    public void onPlaced(World world, BlockPos blockPos, BlockState blockState, @Nullable LivingEntity livingEntity, ItemStack itemStack) {
        BlockEntity blockEntity;
        if (world.isClient) {
            return;
        }
        if (livingEntity != null && (blockEntity = world.getBlockEntity(blockPos)) instanceof StructureBlockBlockEntity) {
            ((StructureBlockBlockEntity)blockEntity).setAuthor(livingEntity);
        }
    }

    @Override
    public BlockRenderType getRenderType(BlockState blockState) {
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
        return (BlockState)this.getDefaultState().with(MODE, StructureBlockMode.DATA);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(MODE);
    }

    @Override
    public void neighborUpdate(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl) {
        if (world.isClient) {
            return;
        }
        BlockEntity blockEntity = world.getBlockEntity(blockPos);
        if (!(blockEntity instanceof StructureBlockBlockEntity)) {
            return;
        }
        StructureBlockBlockEntity structureBlockBlockEntity = (StructureBlockBlockEntity)blockEntity;
        boolean bl2 = world.isReceivingRedstonePower(blockPos);
        boolean bl3 = structureBlockBlockEntity.isPowered();
        if (bl2 && !bl3) {
            structureBlockBlockEntity.setPowered(true);
            this.doAction(structureBlockBlockEntity);
        } else if (!bl2 && bl3) {
            structureBlockBlockEntity.setPowered(false);
        }
    }

    private void doAction(StructureBlockBlockEntity structureBlockBlockEntity) {
        switch (structureBlockBlockEntity.getMode()) {
            case SAVE: {
                structureBlockBlockEntity.saveStructure(false);
                break;
            }
            case LOAD: {
                structureBlockBlockEntity.loadStructure(false);
                break;
            }
            case CORNER: {
                structureBlockBlockEntity.unloadStructure();
                break;
            }
            case DATA: {
                break;
            }
        }
    }
}

