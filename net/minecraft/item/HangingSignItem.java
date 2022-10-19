/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.WallHangingSignBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.VerticallyAttachableBlockItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class HangingSignItem
extends VerticallyAttachableBlockItem {
    public HangingSignItem(Block hangingSign, Block wallHangingSign, Item.Settings settings) {
        super(hangingSign, wallHangingSign, settings, Direction.UP);
    }

    @Override
    protected boolean canPlaceAt(WorldView world, BlockState state, BlockPos pos) {
        WallHangingSignBlock wallHangingSignBlock;
        Block block = state.getBlock();
        if (block instanceof WallHangingSignBlock && !(wallHangingSignBlock = (WallHangingSignBlock)block).canAttachAt(state, world, pos)) {
            return false;
        }
        return super.canPlaceAt(world, state, pos);
    }

    @Override
    protected boolean postPlacement(BlockPos pos, World world, @Nullable PlayerEntity player, ItemStack stack, BlockState state) {
        BlockEntity blockEntity;
        boolean bl = super.postPlacement(pos, world, player, stack, state);
        if (!world.isClient && !bl && player != null && (blockEntity = world.getBlockEntity(pos)) instanceof SignBlockEntity) {
            SignBlockEntity signBlockEntity = (SignBlockEntity)blockEntity;
            player.openEditSignScreen(signBlockEntity);
        }
        return bl;
    }
}

