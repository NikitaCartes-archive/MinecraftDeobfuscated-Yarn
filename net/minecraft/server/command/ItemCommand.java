/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.command;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.command.argument.ItemSlotArgumentType;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.CommandItemSlot;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionManager;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;

public class ItemCommand {
    static final Dynamic3CommandExceptionType NOT_A_CONTAINER_TARGET_EXCEPTION = new Dynamic3CommandExceptionType((object, object2, object3) -> new TranslatableText("commands.item.target.not_a_container", object, object2, object3));
    private static final Dynamic3CommandExceptionType NOT_A_CONTAINER_SOURCE_EXCEPTION = new Dynamic3CommandExceptionType((object, object2, object3) -> new TranslatableText("commands.item.source.not_a_container", object, object2, object3));
    static final DynamicCommandExceptionType NO_SUCH_SLOT_TARGET_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("commands.item.target.no_such_slot", object));
    private static final DynamicCommandExceptionType NO_SUCH_SLOT_SOURCE_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("commands.item.source.no_such_slot", object));
    private static final DynamicCommandExceptionType NO_CHANGES_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("commands.item.target.no_changes", object));
    private static final Dynamic2CommandExceptionType KNOWN_ITEM_EXCEPTION = new Dynamic2CommandExceptionType((object, object2) -> new TranslatableText("commands.item.target.no_changed.known_item", object, object2));
    private static final SuggestionProvider<ServerCommandSource> MODIFIER_SUGGESTION_PROVIDER = (commandContext, suggestionsBuilder) -> {
        LootFunctionManager lootFunctionManager = ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getItemModifierManager();
        return CommandSource.suggestIdentifiers(lootFunctionManager.getFunctionIds(), suggestionsBuilder);
    };

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("item").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).then(CommandManager.literal("block").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("pos", BlockPosArgumentType.blockPos()).then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("slot", ItemSlotArgumentType.itemSlot()).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.literal("replace").then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)CommandManager.argument("item", ItemStackArgumentType.itemStack()).executes(commandContext -> ItemCommand.executeBlockReplace((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos(commandContext, "pos"), ItemSlotArgumentType.getItemSlot(commandContext, "slot"), ItemStackArgumentType.getItemStackArgument(commandContext, "item").createStack(1, false)))).then(CommandManager.argument("count", IntegerArgumentType.integer(1, 64)).executes(commandContext -> ItemCommand.executeBlockReplace((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos(commandContext, "pos"), ItemSlotArgumentType.getItemSlot(commandContext, "slot"), ItemStackArgumentType.getItemStackArgument(commandContext, "item").createStack(IntegerArgumentType.getInteger(commandContext, "count"), true))))))).then(CommandManager.literal("modify").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("modifier", IdentifierArgumentType.identifier()).suggests(MODIFIER_SUGGESTION_PROVIDER).executes(commandContext -> ItemCommand.executeBlockModify((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos(commandContext, "pos"), ItemSlotArgumentType.getItemSlot(commandContext, "slot"), IdentifierArgumentType.getItemModifierArgument(commandContext, "modifier")))))).then(((LiteralArgumentBuilder)CommandManager.literal("copy").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.literal("block").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("source", BlockPosArgumentType.blockPos()).then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)CommandManager.argument("sourceSlot", ItemSlotArgumentType.itemSlot()).executes(commandContext -> ItemCommand.executeBlockCopyBlock((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos(commandContext, "source"), ItemSlotArgumentType.getItemSlot(commandContext, "sourceSlot"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "pos"), ItemSlotArgumentType.getItemSlot(commandContext, "slot")))).then(CommandManager.argument("modifier", IdentifierArgumentType.identifier()).suggests(MODIFIER_SUGGESTION_PROVIDER).executes(commandContext -> ItemCommand.executeBlockCopyBlock((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos(commandContext, "source"), ItemSlotArgumentType.getItemSlot(commandContext, "sourceSlot"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "pos"), ItemSlotArgumentType.getItemSlot(commandContext, "slot"), IdentifierArgumentType.getItemModifierArgument(commandContext, "modifier")))))))).then(CommandManager.literal("entity").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("source", EntityArgumentType.entity()).then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)CommandManager.argument("sourceSlot", ItemSlotArgumentType.itemSlot()).executes(commandContext -> ItemCommand.executeBlockCopyEntity((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntity(commandContext, "source"), ItemSlotArgumentType.getItemSlot(commandContext, "sourceSlot"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "pos"), ItemSlotArgumentType.getItemSlot(commandContext, "slot")))).then(CommandManager.argument("modifier", IdentifierArgumentType.identifier()).suggests(MODIFIER_SUGGESTION_PROVIDER).executes(commandContext -> ItemCommand.executeBlockCopyEntity((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntity(commandContext, "source"), ItemSlotArgumentType.getItemSlot(commandContext, "sourceSlot"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "pos"), ItemSlotArgumentType.getItemSlot(commandContext, "slot"), IdentifierArgumentType.getItemModifierArgument(commandContext, "modifier")))))))))))).then(CommandManager.literal("entity").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("targets", EntityArgumentType.entities()).then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("slot", ItemSlotArgumentType.itemSlot()).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.literal("replace").then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)CommandManager.argument("item", ItemStackArgumentType.itemStack()).executes(commandContext -> ItemCommand.executeEntityReplace((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntities(commandContext, "targets"), ItemSlotArgumentType.getItemSlot(commandContext, "slot"), ItemStackArgumentType.getItemStackArgument(commandContext, "item").createStack(1, false)))).then(CommandManager.argument("count", IntegerArgumentType.integer(1, 64)).executes(commandContext -> ItemCommand.executeEntityReplace((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntities(commandContext, "targets"), ItemSlotArgumentType.getItemSlot(commandContext, "slot"), ItemStackArgumentType.getItemStackArgument(commandContext, "item").createStack(IntegerArgumentType.getInteger(commandContext, "count"), true))))))).then(CommandManager.literal("modify").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("modifier", IdentifierArgumentType.identifier()).suggests(MODIFIER_SUGGESTION_PROVIDER).executes(commandContext -> ItemCommand.executeEntityModify((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntities(commandContext, "targets"), ItemSlotArgumentType.getItemSlot(commandContext, "slot"), IdentifierArgumentType.getItemModifierArgument(commandContext, "modifier")))))).then(((LiteralArgumentBuilder)CommandManager.literal("copy").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.literal("block").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("source", BlockPosArgumentType.blockPos()).then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)CommandManager.argument("sourceSlot", ItemSlotArgumentType.itemSlot()).executes(commandContext -> ItemCommand.executeEntityCopyBlock((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos(commandContext, "source"), ItemSlotArgumentType.getItemSlot(commandContext, "sourceSlot"), EntityArgumentType.getEntities(commandContext, "targets"), ItemSlotArgumentType.getItemSlot(commandContext, "slot")))).then(CommandManager.argument("modifier", IdentifierArgumentType.identifier()).suggests(MODIFIER_SUGGESTION_PROVIDER).executes(commandContext -> ItemCommand.executeEntityCopyBlock((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos(commandContext, "source"), ItemSlotArgumentType.getItemSlot(commandContext, "sourceSlot"), EntityArgumentType.getEntities(commandContext, "targets"), ItemSlotArgumentType.getItemSlot(commandContext, "slot"), IdentifierArgumentType.getItemModifierArgument(commandContext, "modifier")))))))).then(CommandManager.literal("entity").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("source", EntityArgumentType.entity()).then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)CommandManager.argument("sourceSlot", ItemSlotArgumentType.itemSlot()).executes(commandContext -> ItemCommand.executeEntityCopyEntity((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntity(commandContext, "source"), ItemSlotArgumentType.getItemSlot(commandContext, "sourceSlot"), EntityArgumentType.getEntities(commandContext, "targets"), ItemSlotArgumentType.getItemSlot(commandContext, "slot")))).then(CommandManager.argument("modifier", IdentifierArgumentType.identifier()).suggests(MODIFIER_SUGGESTION_PROVIDER).executes(commandContext -> ItemCommand.executeEntityCopyEntity((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntity(commandContext, "source"), ItemSlotArgumentType.getItemSlot(commandContext, "sourceSlot"), EntityArgumentType.getEntities(commandContext, "targets"), ItemSlotArgumentType.getItemSlot(commandContext, "slot"), IdentifierArgumentType.getItemModifierArgument(commandContext, "modifier"))))))))))));
    }

    private static int executeBlockModify(ServerCommandSource source, BlockPos pos, int slot, LootFunction modifier) throws CommandSyntaxException {
        Inventory inventory = ItemCommand.getInventoryAtPos(source, pos, NOT_A_CONTAINER_TARGET_EXCEPTION);
        if (slot < 0 || slot >= inventory.size()) {
            throw NO_SUCH_SLOT_TARGET_EXCEPTION.create(slot);
        }
        ItemStack itemStack = ItemCommand.getStackWithModifier(source, modifier, inventory.getStack(slot));
        inventory.setStack(slot, itemStack);
        source.sendFeedback(new TranslatableText("commands.item.block.set.success", pos.getX(), pos.getY(), pos.getZ(), itemStack.toHoverableText()), true);
        return 1;
    }

    private static int executeEntityModify(ServerCommandSource source, Collection<? extends Entity> targets, int slot, LootFunction modifier) throws CommandSyntaxException {
        HashMap<Entity, ItemStack> map = Maps.newHashMapWithExpectedSize(targets.size());
        for (Entity entity : targets) {
            ItemStack itemStack;
            CommandItemSlot commandItemSlot;
            if (entity instanceof ServerPlayerEntity) {
                ((ServerPlayerEntity)entity).playerScreenHandler.sendContentUpdates();
            }
            if ((commandItemSlot = entity.getCommandItemSlot(slot)) == CommandItemSlot.EMPTY || !commandItemSlot.set(itemStack = ItemCommand.getStackWithModifier(source, modifier, commandItemSlot.get().copy()))) continue;
            map.put(entity, itemStack);
            if (!(entity instanceof ServerPlayerEntity)) continue;
            ((ServerPlayerEntity)entity).playerScreenHandler.sendContentUpdates();
        }
        if (map.isEmpty()) {
            throw NO_CHANGES_EXCEPTION.create(slot);
        }
        if (map.size() == 1) {
            Map.Entry entry = map.entrySet().iterator().next();
            source.sendFeedback(new TranslatableText("commands.item.entity.set.success.single", ((Entity)entry.getKey()).getDisplayName(), ((ItemStack)entry.getValue()).toHoverableText()), true);
        } else {
            source.sendFeedback(new TranslatableText("commands.item.entity.set.success.multiple", map.size()), true);
        }
        return map.size();
    }

    private static int executeBlockReplace(ServerCommandSource source, BlockPos pos, int slot, ItemStack stack) throws CommandSyntaxException {
        Inventory inventory = ItemCommand.getInventoryAtPos(source, pos, NOT_A_CONTAINER_TARGET_EXCEPTION);
        if (slot < 0 || slot >= inventory.size()) {
            throw NO_SUCH_SLOT_TARGET_EXCEPTION.create(slot);
        }
        inventory.setStack(slot, stack);
        source.sendFeedback(new TranslatableText("commands.item.block.set.success", pos.getX(), pos.getY(), pos.getZ(), stack.toHoverableText()), true);
        return 1;
    }

    private static Inventory getInventoryAtPos(ServerCommandSource source, BlockPos pos, Dynamic3CommandExceptionType exception) throws CommandSyntaxException {
        BlockEntity blockEntity = source.getWorld().getBlockEntity(pos);
        if (!(blockEntity instanceof Inventory)) {
            throw exception.create(pos.getX(), pos.getY(), pos.getZ());
        }
        return (Inventory)((Object)blockEntity);
    }

    private static int executeEntityReplace(ServerCommandSource source, Collection<? extends Entity> targets, int slot, ItemStack stack) throws CommandSyntaxException {
        ArrayList<Entity> list = Lists.newArrayListWithCapacity(targets.size());
        for (Entity entity : targets) {
            CommandItemSlot commandItemSlot;
            if (entity instanceof ServerPlayerEntity) {
                ((ServerPlayerEntity)entity).playerScreenHandler.sendContentUpdates();
            }
            if ((commandItemSlot = entity.getCommandItemSlot(slot)) == CommandItemSlot.EMPTY || !commandItemSlot.set(stack.copy())) continue;
            list.add(entity);
            if (!(entity instanceof ServerPlayerEntity)) continue;
            ((ServerPlayerEntity)entity).playerScreenHandler.sendContentUpdates();
        }
        if (list.isEmpty()) {
            throw KNOWN_ITEM_EXCEPTION.create(stack.toHoverableText(), slot);
        }
        if (list.size() == 1) {
            source.sendFeedback(new TranslatableText("commands.item.entity.set.success.single", ((Entity)list.iterator().next()).getDisplayName(), stack.toHoverableText()), true);
        } else {
            source.sendFeedback(new TranslatableText("commands.item.entity.set.success.multiple", list.size(), stack.toHoverableText()), true);
        }
        return list.size();
    }

    private static int executeEntityCopyBlock(ServerCommandSource source, BlockPos sourcePos, int sourceSlot, Collection<? extends Entity> targets, int slot) throws CommandSyntaxException {
        return ItemCommand.executeEntityReplace(source, targets, slot, ItemCommand.getStackInSlotFromInventoryAt(source, sourcePos, sourceSlot));
    }

    private static int executeEntityCopyBlock(ServerCommandSource source, BlockPos sourcePos, int sourceSlot, Collection<? extends Entity> targets, int slot, LootFunction modifier) throws CommandSyntaxException {
        return ItemCommand.executeEntityReplace(source, targets, slot, ItemCommand.getStackWithModifier(source, modifier, ItemCommand.getStackInSlotFromInventoryAt(source, sourcePos, sourceSlot)));
    }

    private static int executeBlockCopyBlock(ServerCommandSource source, BlockPos sourcePos, int sourceSlot, BlockPos pos, int slot) throws CommandSyntaxException {
        return ItemCommand.executeBlockReplace(source, pos, slot, ItemCommand.getStackInSlotFromInventoryAt(source, sourcePos, sourceSlot));
    }

    private static int executeBlockCopyBlock(ServerCommandSource source, BlockPos sourcePos, int sourceSlot, BlockPos pos, int slot, LootFunction modifier) throws CommandSyntaxException {
        return ItemCommand.executeBlockReplace(source, pos, slot, ItemCommand.getStackWithModifier(source, modifier, ItemCommand.getStackInSlotFromInventoryAt(source, sourcePos, sourceSlot)));
    }

    private static int executeBlockCopyEntity(ServerCommandSource source, Entity sourceEntity, int sourceSlot, BlockPos pos, int slot) throws CommandSyntaxException {
        return ItemCommand.executeBlockReplace(source, pos, slot, ItemCommand.getStackInSlot(sourceEntity, sourceSlot));
    }

    private static int executeBlockCopyEntity(ServerCommandSource source, Entity sourceEntity, int sourceSlot, BlockPos pos, int slot, LootFunction modifier) throws CommandSyntaxException {
        return ItemCommand.executeBlockReplace(source, pos, slot, ItemCommand.getStackWithModifier(source, modifier, ItemCommand.getStackInSlot(sourceEntity, sourceSlot)));
    }

    private static int executeEntityCopyEntity(ServerCommandSource source, Entity sourceEntity, int sourceSlot, Collection<? extends Entity> targets, int slot) throws CommandSyntaxException {
        return ItemCommand.executeEntityReplace(source, targets, slot, ItemCommand.getStackInSlot(sourceEntity, sourceSlot));
    }

    private static int executeEntityCopyEntity(ServerCommandSource source, Entity sourceEntity, int sourceSlot, Collection<? extends Entity> targets, int slot, LootFunction modifier) throws CommandSyntaxException {
        return ItemCommand.executeEntityReplace(source, targets, slot, ItemCommand.getStackWithModifier(source, modifier, ItemCommand.getStackInSlot(sourceEntity, sourceSlot)));
    }

    private static ItemStack getStackWithModifier(ServerCommandSource source, LootFunction modifier, ItemStack stack) {
        ServerWorld serverWorld = source.getWorld();
        LootContext.Builder builder = new LootContext.Builder(serverWorld).parameter(LootContextParameters.ORIGIN, source.getPosition()).optionalParameter(LootContextParameters.THIS_ENTITY, source.getEntity());
        return (ItemStack)modifier.apply(stack, builder.build(LootContextTypes.COMMAND));
    }

    private static ItemStack getStackInSlot(Entity entity, int slotId) throws CommandSyntaxException {
        CommandItemSlot commandItemSlot = entity.getCommandItemSlot(slotId);
        if (commandItemSlot == CommandItemSlot.EMPTY) {
            throw NO_SUCH_SLOT_SOURCE_EXCEPTION.create(slotId);
        }
        return commandItemSlot.get().copy();
    }

    private static ItemStack getStackInSlotFromInventoryAt(ServerCommandSource source, BlockPos pos, int slotId) throws CommandSyntaxException {
        Inventory inventory = ItemCommand.getInventoryAtPos(source, pos, NOT_A_CONTAINER_SOURCE_EXCEPTION);
        if (slotId < 0 || slotId >= inventory.size()) {
            throw NO_SUCH_SLOT_SOURCE_EXCEPTION.create(slotId);
        }
        return inventory.getStack(slotId).copy();
    }
}

