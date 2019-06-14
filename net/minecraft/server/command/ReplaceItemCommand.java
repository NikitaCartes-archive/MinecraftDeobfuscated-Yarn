/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.ArrayList;
import java.util.Collection;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.command.arguments.BlockPosArgumentType;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.command.arguments.ItemSlotArgumentType;
import net.minecraft.command.arguments.ItemStackArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;

public class ReplaceItemCommand {
    public static final SimpleCommandExceptionType BLOCK_FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.replaceitem.block.failed", new Object[0]));
    public static final DynamicCommandExceptionType SLOT_INAPPLICABLE_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("commands.replaceitem.slot.inapplicable", object));
    public static final Dynamic2CommandExceptionType ENTITY_FAILED_EXCEPTION = new Dynamic2CommandExceptionType((object, object2) -> new TranslatableText("commands.replaceitem.entity.failed", object, object2));

    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("replaceitem").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).then(CommandManager.literal("block").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("pos", BlockPosArgumentType.create()).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("slot", ItemSlotArgumentType.create()).then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)CommandManager.argument("item", ItemStackArgumentType.create()).executes(commandContext -> ReplaceItemCommand.executeBlock((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos(commandContext, "pos"), ItemSlotArgumentType.getItemSlot(commandContext, "slot"), ItemStackArgumentType.getItemStackArgument(commandContext, "item").createStack(1, false)))).then(CommandManager.argument("count", IntegerArgumentType.integer(1, 64)).executes(commandContext -> ReplaceItemCommand.executeBlock((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos(commandContext, "pos"), ItemSlotArgumentType.getItemSlot(commandContext, "slot"), ItemStackArgumentType.getItemStackArgument(commandContext, "item").createStack(IntegerArgumentType.getInteger(commandContext, "count"), true))))))))).then(CommandManager.literal("entity").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("targets", EntityArgumentType.entities()).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("slot", ItemSlotArgumentType.create()).then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)CommandManager.argument("item", ItemStackArgumentType.create()).executes(commandContext -> ReplaceItemCommand.executeEntity((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntities(commandContext, "targets"), ItemSlotArgumentType.getItemSlot(commandContext, "slot"), ItemStackArgumentType.getItemStackArgument(commandContext, "item").createStack(1, false)))).then(CommandManager.argument("count", IntegerArgumentType.integer(1, 64)).executes(commandContext -> ReplaceItemCommand.executeEntity((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntities(commandContext, "targets"), ItemSlotArgumentType.getItemSlot(commandContext, "slot"), ItemStackArgumentType.getItemStackArgument(commandContext, "item").createStack(IntegerArgumentType.getInteger(commandContext, "count"), true)))))))));
    }

    private static int executeBlock(ServerCommandSource serverCommandSource, BlockPos blockPos, int i, ItemStack itemStack) throws CommandSyntaxException {
        BlockEntity blockEntity = serverCommandSource.getWorld().getBlockEntity(blockPos);
        if (!(blockEntity instanceof Inventory)) {
            throw BLOCK_FAILED_EXCEPTION.create();
        }
        Inventory inventory = (Inventory)((Object)blockEntity);
        if (i < 0 || i >= inventory.getInvSize()) {
            throw SLOT_INAPPLICABLE_EXCEPTION.create(i);
        }
        inventory.setInvStack(i, itemStack);
        serverCommandSource.sendFeedback(new TranslatableText("commands.replaceitem.block.success", blockPos.getX(), blockPos.getY(), blockPos.getZ(), itemStack.toHoverableText()), true);
        return 1;
    }

    private static int executeEntity(ServerCommandSource serverCommandSource, Collection<? extends Entity> collection, int i, ItemStack itemStack) throws CommandSyntaxException {
        ArrayList<Entity> list = Lists.newArrayListWithCapacity(collection.size());
        for (Entity entity : collection) {
            if (entity instanceof ServerPlayerEntity) {
                ((ServerPlayerEntity)entity).playerContainer.sendContentUpdates();
            }
            if (!entity.equip(i, itemStack.copy())) continue;
            list.add(entity);
            if (!(entity instanceof ServerPlayerEntity)) continue;
            ((ServerPlayerEntity)entity).playerContainer.sendContentUpdates();
        }
        if (list.isEmpty()) {
            throw ENTITY_FAILED_EXCEPTION.create(itemStack.toHoverableText(), i);
        }
        if (list.size() == 1) {
            serverCommandSource.sendFeedback(new TranslatableText("commands.replaceitem.entity.success.single", ((Entity)list.iterator().next()).getDisplayName(), itemStack.toHoverableText()), true);
        } else {
            serverCommandSource.sendFeedback(new TranslatableText("commands.replaceitem.entity.success.multiple", list.size(), itemStack.toHoverableText()), true);
        }
        return list.size();
    }
}

