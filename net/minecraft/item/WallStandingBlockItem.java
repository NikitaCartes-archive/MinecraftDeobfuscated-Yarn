/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class WallStandingBlockItem
extends BlockItem {
    protected final Block wallBlock;

    public WallStandingBlockItem(Block block, Block block2, Item.Settings settings) {
        super(block, settings);
        this.wallBlock = block2;
    }

    @Override
    @Nullable
    protected BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
        BlockState blockState = this.wallBlock.getPlacementState(itemPlacementContext);
        BlockState blockState2 = null;
        World collisionView = itemPlacementContext.getWorld();
        BlockPos blockPos = itemPlacementContext.getBlockPos();
        for (Direction direction : itemPlacementContext.getPlacementDirections()) {
            BlockState blockState3;
            if (direction == Direction.UP) continue;
            BlockState blockState4 = blockState3 = direction == Direction.DOWN ? this.getBlock().getPlacementState(itemPlacementContext) : blockState;
            if (blockState3 == null || !blockState3.canPlaceAt(collisionView, blockPos)) continue;
            blockState2 = blockState3;
            break;
        }
        return blockState2 != null && collisionView.canPlace(blockState2, blockPos, EntityContext.absent()) ? blockState2 : null;
    }

    @Override
    public void appendBlocks(Map<Block, Item> map, Item item) {
        super.appendBlocks(map, item);
        map.put(this.wallBlock, item);
    }
}

