/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import java.util.Collection;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.ChatMessageType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class DebugStickItem
extends Item {
    public DebugStickItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean hasEnchantmentGlint(ItemStack itemStack) {
        return true;
    }

    @Override
    public boolean canMine(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity) {
        if (!world.isClient) {
            this.use(playerEntity, blockState, world, blockPos, false, playerEntity.getStackInHand(Hand.MAIN_HAND));
        }
        return false;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext itemUsageContext) {
        PlayerEntity playerEntity = itemUsageContext.getPlayer();
        World world = itemUsageContext.getWorld();
        if (!world.isClient && playerEntity != null) {
            BlockPos blockPos = itemUsageContext.getBlockPos();
            this.use(playerEntity, world.getBlockState(blockPos), world, blockPos, true, itemUsageContext.getStack());
        }
        return ActionResult.SUCCESS;
    }

    private void use(PlayerEntity playerEntity, BlockState blockState, IWorld iWorld, BlockPos blockPos, boolean bl, ItemStack itemStack) {
        if (!playerEntity.isCreativeLevelTwoOp()) {
            return;
        }
        Block block = blockState.getBlock();
        StateFactory<Block, BlockState> stateFactory = block.getStateFactory();
        Collection<Property<?>> collection = stateFactory.getProperties();
        String string = Registry.BLOCK.getId(block).toString();
        if (collection.isEmpty()) {
            DebugStickItem.sendMessage(playerEntity, new TranslatableComponent(this.getTranslationKey() + ".empty", string));
            return;
        }
        CompoundTag compoundTag = itemStack.getOrCreateSubTag("DebugProperty");
        String string2 = compoundTag.getString(string);
        Property<?> property = stateFactory.getProperty(string2);
        if (bl) {
            if (property == null) {
                property = collection.iterator().next();
            }
            BlockState blockState2 = DebugStickItem.cycle(blockState, property, playerEntity.isSneaking());
            iWorld.setBlockState(blockPos, blockState2, 18);
            DebugStickItem.sendMessage(playerEntity, new TranslatableComponent(this.getTranslationKey() + ".update", property.getName(), DebugStickItem.getValueString(blockState2, property)));
        } else {
            property = DebugStickItem.cycle(collection, property, playerEntity.isSneaking());
            String string3 = property.getName();
            compoundTag.putString(string, string3);
            DebugStickItem.sendMessage(playerEntity, new TranslatableComponent(this.getTranslationKey() + ".select", string3, DebugStickItem.getValueString(blockState, property)));
        }
    }

    private static <T extends Comparable<T>> BlockState cycle(BlockState blockState, Property<T> property, boolean bl) {
        return (BlockState)blockState.with(property, (Comparable)DebugStickItem.cycle(property.getValues(), blockState.get(property), bl));
    }

    private static <T> T cycle(Iterable<T> iterable, @Nullable T object, boolean bl) {
        return bl ? SystemUtil.previous(iterable, object) : SystemUtil.next(iterable, object);
    }

    private static void sendMessage(PlayerEntity playerEntity, Component component) {
        ((ServerPlayerEntity)playerEntity).sendChatMessage(component, ChatMessageType.GAME_INFO);
    }

    private static <T extends Comparable<T>> String getValueString(BlockState blockState, Property<T> property) {
        return property.getName(blockState.get(property));
    }
}

