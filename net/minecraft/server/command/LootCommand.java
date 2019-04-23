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
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.command.arguments.BlockPosArgumentType;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.command.arguments.IdentifierArgumentType;
import net.minecraft.command.arguments.ItemSlotArgumentType;
import net.minecraft.command.arguments.ItemStackArgumentType;
import net.minecraft.command.arguments.Vec3ArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ReplaceItemCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.loot.LootManager;
import net.minecraft.world.loot.LootSupplier;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.context.LootContextParameters;
import net.minecraft.world.loot.context.LootContextTypes;

public class LootCommand {
    public static final SuggestionProvider<ServerCommandSource> SUGGESTION_PROVIDER = (commandContext, suggestionsBuilder) -> {
        LootManager lootManager = ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getLootManager();
        return CommandSource.suggestIdentifiers(lootManager.getSupplierNames(), suggestionsBuilder);
    };
    private static final DynamicCommandExceptionType NO_HELD_ITEMS_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableComponent("commands.drop.no_held_items", object));
    private static final DynamicCommandExceptionType NO_LOOT_TABLE_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableComponent("commands.drop.no_loot_table", object));

    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)LootCommand.addTargetArguments(CommandManager.literal("loot").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2)), (argumentBuilder, target) -> ((ArgumentBuilder)((ArgumentBuilder)((ArgumentBuilder)argumentBuilder.then(CommandManager.literal("fish").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("loot_table", IdentifierArgumentType.create()).suggests(SUGGESTION_PROVIDER).then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("pos", BlockPosArgumentType.create()).executes(commandContext -> LootCommand.executeFish(commandContext, IdentifierArgumentType.getIdentifier(commandContext, "loot_table"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "pos"), ItemStack.EMPTY, target))).then(CommandManager.argument("tool", ItemStackArgumentType.create()).executes(commandContext -> LootCommand.executeFish(commandContext, IdentifierArgumentType.getIdentifier(commandContext, "loot_table"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "pos"), ItemStackArgumentType.getItemStackArgument(commandContext, "tool").createStack(1, false), target)))).then(CommandManager.literal("mainhand").executes(commandContext -> LootCommand.executeFish(commandContext, IdentifierArgumentType.getIdentifier(commandContext, "loot_table"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "pos"), LootCommand.getHeldItem((ServerCommandSource)commandContext.getSource(), EquipmentSlot.MAINHAND), target)))).then(CommandManager.literal("offhand").executes(commandContext -> LootCommand.executeFish(commandContext, IdentifierArgumentType.getIdentifier(commandContext, "loot_table"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "pos"), LootCommand.getHeldItem((ServerCommandSource)commandContext.getSource(), EquipmentSlot.OFFHAND), target))))))).then(CommandManager.literal("loot").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("loot_table", IdentifierArgumentType.create()).suggests(SUGGESTION_PROVIDER).executes(commandContext -> LootCommand.executeLoot(commandContext, IdentifierArgumentType.getIdentifier(commandContext, "loot_table"), target))))).then(CommandManager.literal("kill").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("target", EntityArgumentType.entity()).executes(commandContext -> LootCommand.executeKill(commandContext, EntityArgumentType.getEntity(commandContext, "target"), target))))).then(CommandManager.literal("mine").then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("pos", BlockPosArgumentType.create()).executes(commandContext -> LootCommand.executeMine(commandContext, BlockPosArgumentType.getLoadedBlockPos(commandContext, "pos"), ItemStack.EMPTY, target))).then(CommandManager.argument("tool", ItemStackArgumentType.create()).executes(commandContext -> LootCommand.executeMine(commandContext, BlockPosArgumentType.getLoadedBlockPos(commandContext, "pos"), ItemStackArgumentType.getItemStackArgument(commandContext, "tool").createStack(1, false), target)))).then(CommandManager.literal("mainhand").executes(commandContext -> LootCommand.executeMine(commandContext, BlockPosArgumentType.getLoadedBlockPos(commandContext, "pos"), LootCommand.getHeldItem((ServerCommandSource)commandContext.getSource(), EquipmentSlot.MAINHAND), target)))).then(CommandManager.literal("offhand").executes(commandContext -> LootCommand.executeMine(commandContext, BlockPosArgumentType.getLoadedBlockPos(commandContext, "pos"), LootCommand.getHeldItem((ServerCommandSource)commandContext.getSource(), EquipmentSlot.OFFHAND), target)))))));
    }

    private static <T extends ArgumentBuilder<ServerCommandSource, T>> T addTargetArguments(T argumentBuilder, SourceConstructor sourceConstructor) {
        return ((ArgumentBuilder)((ArgumentBuilder)((ArgumentBuilder)argumentBuilder.then(((LiteralArgumentBuilder)CommandManager.literal("replace").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.literal("entity").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("entities", EntityArgumentType.entities()).then((ArgumentBuilder<ServerCommandSource, ?>)sourceConstructor.construct(CommandManager.argument("slot", ItemSlotArgumentType.create()), (commandContext, list, feedbackMessage) -> LootCommand.executeReplace(EntityArgumentType.getEntities(commandContext, "entities"), ItemSlotArgumentType.getItemSlot(commandContext, "slot"), list.size(), list, feedbackMessage)).then(sourceConstructor.construct(CommandManager.argument("count", IntegerArgumentType.integer(0)), (commandContext, list, feedbackMessage) -> LootCommand.executeReplace(EntityArgumentType.getEntities(commandContext, "entities"), ItemSlotArgumentType.getItemSlot(commandContext, "slot"), IntegerArgumentType.getInteger(commandContext, "count"), list, feedbackMessage))))))).then(CommandManager.literal("block").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("targetPos", BlockPosArgumentType.create()).then((ArgumentBuilder<ServerCommandSource, ?>)sourceConstructor.construct(CommandManager.argument("slot", ItemSlotArgumentType.create()), (commandContext, list, feedbackMessage) -> LootCommand.executeBlock((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos(commandContext, "targetPos"), ItemSlotArgumentType.getItemSlot(commandContext, "slot"), list.size(), list, feedbackMessage)).then(sourceConstructor.construct(CommandManager.argument("count", IntegerArgumentType.integer(0)), (commandContext, list, feedbackMessage) -> LootCommand.executeBlock((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos(commandContext, "targetPos"), IntegerArgumentType.getInteger(commandContext, "slot"), IntegerArgumentType.getInteger(commandContext, "count"), list, feedbackMessage)))))))).then(CommandManager.literal("insert").then(sourceConstructor.construct(CommandManager.argument("targetPos", BlockPosArgumentType.create()), (commandContext, list, feedbackMessage) -> LootCommand.executeInsert((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos(commandContext, "targetPos"), list, feedbackMessage))))).then(CommandManager.literal("give").then(sourceConstructor.construct(CommandManager.argument("players", EntityArgumentType.players()), (commandContext, list, feedbackMessage) -> LootCommand.executeGive(EntityArgumentType.getPlayers(commandContext, "players"), list, feedbackMessage))))).then(CommandManager.literal("spawn").then(sourceConstructor.construct(CommandManager.argument("targetPos", Vec3ArgumentType.create()), (commandContext, list, feedbackMessage) -> LootCommand.executeSpawn((ServerCommandSource)commandContext.getSource(), Vec3ArgumentType.getVec3(commandContext, "targetPos"), list, feedbackMessage))));
    }

    private static Inventory getBlockInventory(ServerCommandSource serverCommandSource, BlockPos blockPos) throws CommandSyntaxException {
        BlockEntity blockEntity = serverCommandSource.getWorld().getBlockEntity(blockPos);
        if (!(blockEntity instanceof Inventory)) {
            throw ReplaceItemCommand.BLOCK_FAILED_EXCEPTION.create();
        }
        return (Inventory)((Object)blockEntity);
    }

    private static int executeInsert(ServerCommandSource serverCommandSource, BlockPos blockPos, List<ItemStack> list, FeedbackMessage feedbackMessage) throws CommandSyntaxException {
        Inventory inventory = LootCommand.getBlockInventory(serverCommandSource, blockPos);
        ArrayList<ItemStack> list2 = Lists.newArrayListWithCapacity(list.size());
        for (ItemStack itemStack : list) {
            if (!LootCommand.insert(inventory, itemStack.copy())) continue;
            inventory.markDirty();
            list2.add(itemStack);
        }
        feedbackMessage.accept(list2);
        return list2.size();
    }

    private static boolean insert(Inventory inventory, ItemStack itemStack) {
        boolean bl = false;
        for (int i = 0; i < inventory.getInvSize() && !itemStack.isEmpty(); ++i) {
            ItemStack itemStack2 = inventory.getInvStack(i);
            if (!inventory.isValidInvStack(i, itemStack)) continue;
            if (itemStack2.isEmpty()) {
                inventory.setInvStack(i, itemStack);
                bl = true;
                break;
            }
            if (!LootCommand.itemsMatch(itemStack2, itemStack)) continue;
            int j = itemStack.getMaxAmount() - itemStack2.getAmount();
            int k = Math.min(itemStack.getAmount(), j);
            itemStack.subtractAmount(k);
            itemStack2.addAmount(k);
            bl = true;
        }
        return bl;
    }

    private static int executeBlock(ServerCommandSource serverCommandSource, BlockPos blockPos, int i, int j, List<ItemStack> list, FeedbackMessage feedbackMessage) throws CommandSyntaxException {
        Inventory inventory = LootCommand.getBlockInventory(serverCommandSource, blockPos);
        int k = inventory.getInvSize();
        if (i < 0 || i >= k) {
            throw ReplaceItemCommand.SLOT_INAPPLICABLE_EXCEPTION.create(i);
        }
        ArrayList<ItemStack> list2 = Lists.newArrayListWithCapacity(list.size());
        for (int l = 0; l < j; ++l) {
            ItemStack itemStack;
            int m = i + l;
            ItemStack itemStack2 = itemStack = l < list.size() ? list.get(l) : ItemStack.EMPTY;
            if (!inventory.isValidInvStack(m, itemStack)) continue;
            inventory.setInvStack(m, itemStack);
            list2.add(itemStack);
        }
        feedbackMessage.accept(list2);
        return list2.size();
    }

    private static boolean itemsMatch(ItemStack itemStack, ItemStack itemStack2) {
        return itemStack.getItem() == itemStack2.getItem() && itemStack.getDamage() == itemStack2.getDamage() && itemStack.getAmount() <= itemStack.getMaxAmount() && Objects.equals(itemStack.getTag(), itemStack2.getTag());
    }

    private static int executeGive(Collection<ServerPlayerEntity> collection, List<ItemStack> list, FeedbackMessage feedbackMessage) throws CommandSyntaxException {
        ArrayList<ItemStack> list2 = Lists.newArrayListWithCapacity(list.size());
        for (ItemStack itemStack : list) {
            for (ServerPlayerEntity serverPlayerEntity : collection) {
                if (!serverPlayerEntity.inventory.insertStack(itemStack.copy())) continue;
                list2.add(itemStack);
            }
        }
        feedbackMessage.accept(list2);
        return list2.size();
    }

    private static void replace(Entity entity, List<ItemStack> list, int i, int j, List<ItemStack> list2) {
        for (int k = 0; k < j; ++k) {
            ItemStack itemStack;
            ItemStack itemStack2 = itemStack = k < list.size() ? list.get(k) : ItemStack.EMPTY;
            if (!entity.equip(i + k, itemStack.copy())) continue;
            list2.add(itemStack);
        }
    }

    private static int executeReplace(Collection<? extends Entity> collection, int i, int j, List<ItemStack> list, FeedbackMessage feedbackMessage) throws CommandSyntaxException {
        ArrayList<ItemStack> list2 = Lists.newArrayListWithCapacity(list.size());
        for (Entity entity : collection) {
            if (entity instanceof ServerPlayerEntity) {
                ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)entity;
                serverPlayerEntity.playerContainer.sendContentUpdates();
                LootCommand.replace(entity, list, i, j, list2);
                serverPlayerEntity.playerContainer.sendContentUpdates();
                continue;
            }
            LootCommand.replace(entity, list, i, j, list2);
        }
        feedbackMessage.accept(list2);
        return list2.size();
    }

    private static int executeSpawn(ServerCommandSource serverCommandSource, Vec3d vec3d, List<ItemStack> list, FeedbackMessage feedbackMessage) throws CommandSyntaxException {
        ServerWorld serverWorld = serverCommandSource.getWorld();
        list.forEach(itemStack -> {
            ItemEntity itemEntity = new ItemEntity(serverWorld, vec3d.x, vec3d.y, vec3d.z, itemStack.copy());
            itemEntity.setToDefaultPickupDelay();
            serverWorld.spawnEntity(itemEntity);
        });
        feedbackMessage.accept(list);
        return list.size();
    }

    private static void sendDroppedFeedback(ServerCommandSource serverCommandSource, List<ItemStack> list) {
        if (list.size() == 1) {
            ItemStack itemStack = list.get(0);
            serverCommandSource.sendFeedback(new TranslatableComponent("commands.drop.success.single", itemStack.getAmount(), itemStack.toTextComponent()), false);
        } else {
            serverCommandSource.sendFeedback(new TranslatableComponent("commands.drop.success.multiple", list.size()), false);
        }
    }

    private static void sendDroppedFeedback(ServerCommandSource serverCommandSource, List<ItemStack> list, Identifier identifier) {
        if (list.size() == 1) {
            ItemStack itemStack = list.get(0);
            serverCommandSource.sendFeedback(new TranslatableComponent("commands.drop.success.single_with_table", itemStack.getAmount(), itemStack.toTextComponent(), identifier), false);
        } else {
            serverCommandSource.sendFeedback(new TranslatableComponent("commands.drop.success.multiple_with_table", list.size(), identifier), false);
        }
    }

    private static ItemStack getHeldItem(ServerCommandSource serverCommandSource, EquipmentSlot equipmentSlot) throws CommandSyntaxException {
        Entity entity = serverCommandSource.getEntityOrThrow();
        if (entity instanceof LivingEntity) {
            return ((LivingEntity)entity).getEquippedStack(equipmentSlot);
        }
        throw NO_HELD_ITEMS_EXCEPTION.create(entity.getDisplayName());
    }

    private static int executeMine(CommandContext<ServerCommandSource> commandContext, BlockPos blockPos, ItemStack itemStack, Target target) throws CommandSyntaxException {
        ServerCommandSource serverCommandSource = commandContext.getSource();
        ServerWorld serverWorld = serverCommandSource.getWorld();
        BlockState blockState = serverWorld.getBlockState(blockPos);
        BlockEntity blockEntity = serverWorld.getBlockEntity(blockPos);
        LootContext.Builder builder = new LootContext.Builder(serverWorld).put(LootContextParameters.POSITION, blockPos).put(LootContextParameters.BLOCK_STATE, blockState).putNullable(LootContextParameters.BLOCK_ENTITY, blockEntity).putNullable(LootContextParameters.THIS_ENTITY, serverCommandSource.getEntity()).put(LootContextParameters.TOOL, itemStack);
        List<ItemStack> list2 = blockState.getDroppedStacks(builder);
        return target.accept(commandContext, list2, list -> LootCommand.sendDroppedFeedback(serverCommandSource, list, blockState.getBlock().getDropTableId()));
    }

    private static int executeKill(CommandContext<ServerCommandSource> commandContext, Entity entity, Target target) throws CommandSyntaxException {
        if (!(entity instanceof LivingEntity)) {
            throw NO_LOOT_TABLE_EXCEPTION.create(entity.getDisplayName());
        }
        Identifier identifier = ((LivingEntity)entity).getLootTable();
        ServerCommandSource serverCommandSource = commandContext.getSource();
        LootContext.Builder builder = new LootContext.Builder(serverCommandSource.getWorld());
        Entity entity2 = serverCommandSource.getEntity();
        if (entity2 instanceof PlayerEntity) {
            builder.put(LootContextParameters.LAST_DAMAGE_PLAYER, (PlayerEntity)entity2);
        }
        builder.put(LootContextParameters.DAMAGE_SOURCE, DamageSource.MAGIC);
        builder.putNullable(LootContextParameters.DIRECT_KILLER_ENTITY, entity2);
        builder.putNullable(LootContextParameters.KILLER_ENTITY, entity2);
        builder.put(LootContextParameters.THIS_ENTITY, entity);
        builder.put(LootContextParameters.POSITION, new BlockPos(serverCommandSource.getPosition()));
        LootSupplier lootSupplier = serverCommandSource.getMinecraftServer().getLootManager().getSupplier(identifier);
        List<ItemStack> list2 = lootSupplier.getDrops(builder.build(LootContextTypes.ENTITY));
        return target.accept(commandContext, list2, list -> LootCommand.sendDroppedFeedback(serverCommandSource, list, identifier));
    }

    private static int executeLoot(CommandContext<ServerCommandSource> commandContext, Identifier identifier, Target target) throws CommandSyntaxException {
        ServerCommandSource serverCommandSource = commandContext.getSource();
        LootContext.Builder builder = new LootContext.Builder(serverCommandSource.getWorld()).putNullable(LootContextParameters.THIS_ENTITY, serverCommandSource.getEntity()).put(LootContextParameters.POSITION, new BlockPos(serverCommandSource.getPosition()));
        return LootCommand.getFeedbackMessageSingle(commandContext, identifier, builder.build(LootContextTypes.CHEST), target);
    }

    private static int executeFish(CommandContext<ServerCommandSource> commandContext, Identifier identifier, BlockPos blockPos, ItemStack itemStack, Target target) throws CommandSyntaxException {
        ServerCommandSource serverCommandSource = commandContext.getSource();
        LootContext lootContext = new LootContext.Builder(serverCommandSource.getWorld()).put(LootContextParameters.POSITION, blockPos).put(LootContextParameters.TOOL, itemStack).build(LootContextTypes.FISHING);
        return LootCommand.getFeedbackMessageSingle(commandContext, identifier, lootContext, target);
    }

    private static int getFeedbackMessageSingle(CommandContext<ServerCommandSource> commandContext, Identifier identifier, LootContext lootContext, Target target) throws CommandSyntaxException {
        ServerCommandSource serverCommandSource = commandContext.getSource();
        LootSupplier lootSupplier = serverCommandSource.getMinecraftServer().getLootManager().getSupplier(identifier);
        List<ItemStack> list2 = lootSupplier.getDrops(lootContext);
        return target.accept(commandContext, list2, list -> LootCommand.sendDroppedFeedback(serverCommandSource, list));
    }

    @FunctionalInterface
    static interface SourceConstructor {
        public ArgumentBuilder<ServerCommandSource, ?> construct(ArgumentBuilder<ServerCommandSource, ?> var1, Target var2);
    }

    @FunctionalInterface
    static interface Target {
        public int accept(CommandContext<ServerCommandSource> var1, List<ItemStack> var2, FeedbackMessage var3) throws CommandSyntaxException;
    }

    @FunctionalInterface
    static interface FeedbackMessage {
        public void accept(List<ItemStack> var1) throws CommandSyntaxException;
    }
}

