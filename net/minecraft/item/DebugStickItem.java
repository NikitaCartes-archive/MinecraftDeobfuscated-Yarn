/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import java.util.Collection;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.MessageType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class DebugStickItem
extends Item {
    public DebugStickItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }

    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        if (!world.isClient) {
            this.use(miner, state, world, pos, false, miner.getStackInHand(Hand.MAIN_HAND));
        }
        return false;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockPos blockPos;
        PlayerEntity playerEntity = context.getPlayer();
        World world = context.getWorld();
        if (!world.isClient && playerEntity != null && !this.use(playerEntity, world.getBlockState(blockPos = context.getBlockPos()), world, blockPos, true, context.getStack())) {
            return ActionResult.FAIL;
        }
        return ActionResult.success(world.isClient);
    }

    private boolean use(PlayerEntity player, BlockState state, WorldAccess world, BlockPos pos, boolean update, ItemStack stack) {
        if (!player.isCreativeLevelTwoOp()) {
            return false;
        }
        Block block = state.getBlock();
        StateManager<Block, BlockState> stateManager = block.getStateManager();
        Collection<Property<?>> collection = stateManager.getProperties();
        String string = Registry.BLOCK.getId(block).toString();
        if (collection.isEmpty()) {
            DebugStickItem.sendMessage(player, Text.method_43469(this.getTranslationKey() + ".empty", string));
            return false;
        }
        NbtCompound nbtCompound = stack.getOrCreateSubNbt("DebugProperty");
        String string2 = nbtCompound.getString(string);
        Property<?> property = stateManager.getProperty(string2);
        if (update) {
            if (property == null) {
                property = collection.iterator().next();
            }
            BlockState blockState = DebugStickItem.cycle(state, property, player.shouldCancelInteraction());
            world.setBlockState(pos, blockState, Block.NOTIFY_LISTENERS | Block.FORCE_STATE);
            DebugStickItem.sendMessage(player, Text.method_43469(this.getTranslationKey() + ".update", property.getName(), DebugStickItem.getValueString(blockState, property)));
        } else {
            property = DebugStickItem.cycle(collection, property, player.shouldCancelInteraction());
            String string3 = property.getName();
            nbtCompound.putString(string, string3);
            DebugStickItem.sendMessage(player, Text.method_43469(this.getTranslationKey() + ".select", string3, DebugStickItem.getValueString(state, property)));
        }
        return true;
    }

    private static <T extends Comparable<T>> BlockState cycle(BlockState state, Property<T> property, boolean inverse) {
        return (BlockState)state.with(property, (Comparable)DebugStickItem.cycle(property.getValues(), state.get(property), inverse));
    }

    private static <T> T cycle(Iterable<T> elements, @Nullable T current, boolean inverse) {
        return inverse ? Util.previous(elements, current) : Util.next(elements, current);
    }

    private static void sendMessage(PlayerEntity player, Text message) {
        ((ServerPlayerEntity)player).sendMessage(message, MessageType.GAME_INFO, Util.NIL_UUID);
    }

    private static <T extends Comparable<T>> String getValueString(BlockState state, Property<T> property) {
        return property.name(state.get(property));
    }
}

