/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ScaffoldingBlock;
import net.minecraft.client.network.packet.ChatMessageS2CPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.network.MessageType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
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
            Direction direction = itemPlacementContext.shouldCancelInteraction() ? (itemPlacementContext.hitsInsideBlock() ? itemPlacementContext.getSide().getOpposite() : itemPlacementContext.getSide()) : (itemPlacementContext.getSide() == Direction.UP ? itemPlacementContext.getPlayerFacing() : Direction.UP);
            int i = 0;
            BlockPos.Mutable mutable = new BlockPos.Mutable(blockPos).setOffset(direction);
            while (i < 7) {
                if (!world.isClient && !World.isValid(mutable)) {
                    PlayerEntity playerEntity = itemPlacementContext.getPlayer();
                    int j = world.getHeight();
                    if (!(playerEntity instanceof ServerPlayerEntity) || mutable.getY() < j) break;
                    ChatMessageS2CPacket chatMessageS2CPacket = new ChatMessageS2CPacket(new TranslatableText("build.tooHigh", j).formatted(Formatting.RED), MessageType.GAME_INFO);
                    ((ServerPlayerEntity)playerEntity).networkHandler.sendPacket(chatMessageS2CPacket);
                    break;
                }
                blockState = world.getBlockState(mutable);
                if (blockState.getBlock() != this.getBlock()) {
                    if (!blockState.canReplace(itemPlacementContext)) break;
                    return ItemPlacementContext.offset(itemPlacementContext, mutable, direction);
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
    protected boolean checkStatePlacement() {
        return false;
    }
}

