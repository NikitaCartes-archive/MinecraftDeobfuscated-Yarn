/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import net.minecraft.ChatFormat;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ScaffoldingBlock;
import net.minecraft.client.network.packet.ChatMessageS2CPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.network.chat.ChatMessageType;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ScaffoldingItem
extends BlockItem {
    public ScaffoldingItem(Block block, Item.Settings settings) {
        super(block, settings);
    }

    @Override
    @Nullable
    public ItemPlacementContext getPlacementContext(ItemPlacementContext itemPlacementContext) {
        BlockPos blockPos = itemPlacementContext.getBlockPos();
        World world = itemPlacementContext.getWorld();
        BlockState blockState = world.getBlockState(blockPos);
        Block block = this.getBlock();
        if (blockState.getBlock() == block) {
            Direction direction = itemPlacementContext.isPlayerSneaking() ? (itemPlacementContext.method_17699() ? itemPlacementContext.getFacing().getOpposite() : itemPlacementContext.getFacing()) : (itemPlacementContext.getFacing() == Direction.UP ? itemPlacementContext.getPlayerHorizontalFacing() : Direction.UP);
            int i = 0;
            BlockPos.Mutable mutable = new BlockPos.Mutable(blockPos).setOffset(direction);
            while (i < 7) {
                if (!world.isClient && !World.isValid(mutable)) {
                    PlayerEntity playerEntity = itemPlacementContext.getPlayer();
                    int j = world.getHeight();
                    if (!(playerEntity instanceof ServerPlayerEntity) || mutable.getY() < j) break;
                    ChatMessageS2CPacket chatMessageS2CPacket = new ChatMessageS2CPacket(new TranslatableComponent("build.tooHigh", j).applyFormat(ChatFormat.RED), ChatMessageType.GAME_INFO);
                    ((ServerPlayerEntity)playerEntity).networkHandler.sendPacket(chatMessageS2CPacket);
                    break;
                }
                blockState = world.getBlockState(mutable);
                if (blockState.getBlock() != this.getBlock()) {
                    if (!blockState.canReplace(itemPlacementContext)) break;
                    return ItemPlacementContext.create(itemPlacementContext, mutable, direction);
                }
                mutable.setOffset(direction);
                if (!direction.getAxis().isHorizontal()) continue;
                ++i;
            }
            return null;
        }
        if (ScaffoldingBlock.calculateDistance(world, blockPos) == 7) {
            return null;
        }
        return itemPlacementContext;
    }

    @Override
    protected boolean shouldCheckIfStateAllowsPlacement() {
        return false;
    }
}

