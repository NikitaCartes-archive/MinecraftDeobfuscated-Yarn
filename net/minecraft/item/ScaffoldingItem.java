/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ScaffoldingBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.network.MessageType;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
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
    public ItemPlacementContext getPlacementContext(ItemPlacementContext context) {
        Block block;
        BlockPos blockPos = context.getBlockPos();
        World world = context.getWorld();
        BlockState blockState = world.getBlockState(blockPos);
        if (blockState.isOf(block = this.getBlock())) {
            Direction direction = context.shouldCancelInteraction() ? (context.hitsInsideBlock() ? context.getSide().getOpposite() : context.getSide()) : (context.getSide() == Direction.UP ? context.getPlayerFacing() : Direction.UP);
            int i = 0;
            BlockPos.Mutable mutable = blockPos.mutableCopy().move(direction);
            while (i < 7) {
                if (!world.isClient && !World.isInBuildLimit(mutable)) {
                    PlayerEntity playerEntity = context.getPlayer();
                    int j = world.getHeight();
                    if (!(playerEntity instanceof ServerPlayerEntity) || mutable.getY() < j) break;
                    GameMessageS2CPacket gameMessageS2CPacket = new GameMessageS2CPacket(new TranslatableText("build.tooHigh", j).formatted(Formatting.RED), MessageType.GAME_INFO, Util.NIL_UUID);
                    ((ServerPlayerEntity)playerEntity).networkHandler.sendPacket(gameMessageS2CPacket);
                    break;
                }
                blockState = world.getBlockState(mutable);
                if (!blockState.isOf(this.getBlock())) {
                    if (!blockState.canReplace(context)) break;
                    return ItemPlacementContext.offset(context, mutable, direction);
                }
                mutable.move(direction);
                if (!direction.getAxis().isHorizontal()) continue;
                ++i;
            }
            return null;
        }
        if (ScaffoldingBlock.calculateDistance(world, blockPos) == 7) {
            return null;
        }
        return context;
    }

    @Override
    protected boolean checkStatePlacement() {
        return false;
    }
}

