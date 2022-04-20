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
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.command.argument.ItemSlotArgumentType;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ItemCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class LootCommand {
    public static final SuggestionProvider<ServerCommandSource> SUGGESTION_PROVIDER = (context, builder) -> {
        LootManager lootManager = ((ServerCommandSource)context.getSource()).getServer().getLootManager();
        return CommandSource.suggestIdentifiers(lootManager.getTableIds(), builder);
    };
    private static final DynamicCommandExceptionType NO_HELD_ITEMS_EXCEPTION = new DynamicCommandExceptionType(entityName -> Text.method_43469("commands.drop.no_held_items", entityName));
    private static final DynamicCommandExceptionType NO_LOOT_TABLE_EXCEPTION = new DynamicCommandExceptionType(entityName -> Text.method_43469("commands.drop.no_loot_table", entityName));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess) {
        dispatcher.register(LootCommand.addTargetArguments((LiteralArgumentBuilder)CommandManager.literal("loot").requires(source -> source.hasPermissionLevel(2)), (builder, constructor) -> ((ArgumentBuilder)((ArgumentBuilder)((ArgumentBuilder)builder.then(CommandManager.literal("fish").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("loot_table", IdentifierArgumentType.identifier()).suggests(SUGGESTION_PROVIDER).then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("pos", BlockPosArgumentType.blockPos()).executes(context -> LootCommand.executeFish(context, IdentifierArgumentType.getIdentifier(context, "loot_table"), BlockPosArgumentType.getLoadedBlockPos(context, "pos"), ItemStack.EMPTY, constructor))).then(CommandManager.argument("tool", ItemStackArgumentType.itemStack(commandRegistryAccess)).executes(context -> LootCommand.executeFish(context, IdentifierArgumentType.getIdentifier(context, "loot_table"), BlockPosArgumentType.getLoadedBlockPos(context, "pos"), ItemStackArgumentType.getItemStackArgument(context, "tool").createStack(1, false), constructor)))).then(CommandManager.literal("mainhand").executes(context -> LootCommand.executeFish(context, IdentifierArgumentType.getIdentifier(context, "loot_table"), BlockPosArgumentType.getLoadedBlockPos(context, "pos"), LootCommand.getHeldItem((ServerCommandSource)context.getSource(), EquipmentSlot.MAINHAND), constructor)))).then(CommandManager.literal("offhand").executes(context -> LootCommand.executeFish(context, IdentifierArgumentType.getIdentifier(context, "loot_table"), BlockPosArgumentType.getLoadedBlockPos(context, "pos"), LootCommand.getHeldItem((ServerCommandSource)context.getSource(), EquipmentSlot.OFFHAND), constructor))))))).then(CommandManager.literal("loot").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("loot_table", IdentifierArgumentType.identifier()).suggests(SUGGESTION_PROVIDER).executes(context -> LootCommand.executeLoot(context, IdentifierArgumentType.getIdentifier(context, "loot_table"), constructor))))).then(CommandManager.literal("kill").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("target", EntityArgumentType.entity()).executes(context -> LootCommand.executeKill(context, EntityArgumentType.getEntity(context, "target"), constructor))))).then(CommandManager.literal("mine").then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("pos", BlockPosArgumentType.blockPos()).executes(context -> LootCommand.executeMine(context, BlockPosArgumentType.getLoadedBlockPos(context, "pos"), ItemStack.EMPTY, constructor))).then(CommandManager.argument("tool", ItemStackArgumentType.itemStack(commandRegistryAccess)).executes(context -> LootCommand.executeMine(context, BlockPosArgumentType.getLoadedBlockPos(context, "pos"), ItemStackArgumentType.getItemStackArgument(context, "tool").createStack(1, false), constructor)))).then(CommandManager.literal("mainhand").executes(context -> LootCommand.executeMine(context, BlockPosArgumentType.getLoadedBlockPos(context, "pos"), LootCommand.getHeldItem((ServerCommandSource)context.getSource(), EquipmentSlot.MAINHAND), constructor)))).then(CommandManager.literal("offhand").executes(context -> LootCommand.executeMine(context, BlockPosArgumentType.getLoadedBlockPos(context, "pos"), LootCommand.getHeldItem((ServerCommandSource)context.getSource(), EquipmentSlot.OFFHAND), constructor)))))));
    }

    private static <T extends ArgumentBuilder<ServerCommandSource, T>> T addTargetArguments(T rootArgument, SourceConstructor sourceConstructor) {
        return ((ArgumentBuilder)((ArgumentBuilder)((ArgumentBuilder)rootArgument.then(((LiteralArgumentBuilder)CommandManager.literal("replace").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.literal("entity").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("entities", EntityArgumentType.entities()).then((ArgumentBuilder<ServerCommandSource, ?>)sourceConstructor.construct(CommandManager.argument("slot", ItemSlotArgumentType.itemSlot()), (context, stacks, messageSender) -> LootCommand.executeReplace(EntityArgumentType.getEntities(context, "entities"), ItemSlotArgumentType.getItemSlot(context, "slot"), stacks.size(), stacks, messageSender)).then(sourceConstructor.construct(CommandManager.argument("count", IntegerArgumentType.integer(0)), (context, stacks, messageSender) -> LootCommand.executeReplace(EntityArgumentType.getEntities(context, "entities"), ItemSlotArgumentType.getItemSlot(context, "slot"), IntegerArgumentType.getInteger(context, "count"), stacks, messageSender))))))).then(CommandManager.literal("block").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("targetPos", BlockPosArgumentType.blockPos()).then((ArgumentBuilder<ServerCommandSource, ?>)sourceConstructor.construct(CommandManager.argument("slot", ItemSlotArgumentType.itemSlot()), (context, stacks, messageSender) -> LootCommand.executeBlock((ServerCommandSource)context.getSource(), BlockPosArgumentType.getLoadedBlockPos(context, "targetPos"), ItemSlotArgumentType.getItemSlot(context, "slot"), stacks.size(), stacks, messageSender)).then(sourceConstructor.construct(CommandManager.argument("count", IntegerArgumentType.integer(0)), (context, stacks, messageSender) -> LootCommand.executeBlock((ServerCommandSource)context.getSource(), BlockPosArgumentType.getLoadedBlockPos(context, "targetPos"), IntegerArgumentType.getInteger(context, "slot"), IntegerArgumentType.getInteger(context, "count"), stacks, messageSender)))))))).then(CommandManager.literal("insert").then(sourceConstructor.construct(CommandManager.argument("targetPos", BlockPosArgumentType.blockPos()), (context, stacks, messageSender) -> LootCommand.executeInsert((ServerCommandSource)context.getSource(), BlockPosArgumentType.getLoadedBlockPos(context, "targetPos"), stacks, messageSender))))).then(CommandManager.literal("give").then(sourceConstructor.construct(CommandManager.argument("players", EntityArgumentType.players()), (context, stacks, messageSender) -> LootCommand.executeGive(EntityArgumentType.getPlayers(context, "players"), stacks, messageSender))))).then(CommandManager.literal("spawn").then(sourceConstructor.construct(CommandManager.argument("targetPos", Vec3ArgumentType.vec3()), (context, stacks, messageSender) -> LootCommand.executeSpawn((ServerCommandSource)context.getSource(), Vec3ArgumentType.getVec3(context, "targetPos"), stacks, messageSender))));
    }

    private static Inventory getBlockInventory(ServerCommandSource source, BlockPos pos) throws CommandSyntaxException {
        BlockEntity blockEntity = source.getWorld().getBlockEntity(pos);
        if (!(blockEntity instanceof Inventory)) {
            throw ItemCommand.NOT_A_CONTAINER_TARGET_EXCEPTION.create(pos.getX(), pos.getY(), pos.getZ());
        }
        return (Inventory)((Object)blockEntity);
    }

    private static int executeInsert(ServerCommandSource source, BlockPos targetPos, List<ItemStack> stacks, FeedbackMessage messageSender) throws CommandSyntaxException {
        Inventory inventory = LootCommand.getBlockInventory(source, targetPos);
        ArrayList<ItemStack> list = Lists.newArrayListWithCapacity(stacks.size());
        for (ItemStack itemStack : stacks) {
            if (!LootCommand.insert(inventory, itemStack.copy())) continue;
            inventory.markDirty();
            list.add(itemStack);
        }
        messageSender.accept(list);
        return list.size();
    }

    private static boolean insert(Inventory inventory, ItemStack stack) {
        boolean bl = false;
        for (int i = 0; i < inventory.size() && !stack.isEmpty(); ++i) {
            ItemStack itemStack = inventory.getStack(i);
            if (!inventory.isValid(i, stack)) continue;
            if (itemStack.isEmpty()) {
                inventory.setStack(i, stack);
                bl = true;
                break;
            }
            if (!LootCommand.itemsMatch(itemStack, stack)) continue;
            int j = stack.getMaxCount() - itemStack.getCount();
            int k = Math.min(stack.getCount(), j);
            stack.decrement(k);
            itemStack.increment(k);
            bl = true;
        }
        return bl;
    }

    private static int executeBlock(ServerCommandSource source, BlockPos targetPos, int slot, int stackCount, List<ItemStack> stacks, FeedbackMessage messageSender) throws CommandSyntaxException {
        Inventory inventory = LootCommand.getBlockInventory(source, targetPos);
        int i = inventory.size();
        if (slot < 0 || slot >= i) {
            throw ItemCommand.NO_SUCH_SLOT_TARGET_EXCEPTION.create(slot);
        }
        ArrayList<ItemStack> list = Lists.newArrayListWithCapacity(stacks.size());
        for (int j = 0; j < stackCount; ++j) {
            ItemStack itemStack;
            int k = slot + j;
            ItemStack itemStack2 = itemStack = j < stacks.size() ? stacks.get(j) : ItemStack.EMPTY;
            if (!inventory.isValid(k, itemStack)) continue;
            inventory.setStack(k, itemStack);
            list.add(itemStack);
        }
        messageSender.accept(list);
        return list.size();
    }

    private static boolean itemsMatch(ItemStack first, ItemStack second) {
        return first.isOf(second.getItem()) && first.getDamage() == second.getDamage() && first.getCount() <= first.getMaxCount() && Objects.equals(first.getNbt(), second.getNbt());
    }

    private static int executeGive(Collection<ServerPlayerEntity> players, List<ItemStack> stacks, FeedbackMessage messageSender) throws CommandSyntaxException {
        ArrayList<ItemStack> list = Lists.newArrayListWithCapacity(stacks.size());
        for (ItemStack itemStack : stacks) {
            for (ServerPlayerEntity serverPlayerEntity : players) {
                if (!serverPlayerEntity.getInventory().insertStack(itemStack.copy())) continue;
                list.add(itemStack);
            }
        }
        messageSender.accept(list);
        return list.size();
    }

    private static void replace(Entity entity, List<ItemStack> stacks, int slot, int stackCount, List<ItemStack> addedStacks) {
        for (int i = 0; i < stackCount; ++i) {
            ItemStack itemStack = i < stacks.size() ? stacks.get(i) : ItemStack.EMPTY;
            StackReference stackReference = entity.getStackReference(slot + i);
            if (stackReference == StackReference.EMPTY || !stackReference.set(itemStack.copy())) continue;
            addedStacks.add(itemStack);
        }
    }

    private static int executeReplace(Collection<? extends Entity> targets, int slot, int stackCount, List<ItemStack> stacks, FeedbackMessage messageSender) throws CommandSyntaxException {
        ArrayList<ItemStack> list = Lists.newArrayListWithCapacity(stacks.size());
        for (Entity entity : targets) {
            if (entity instanceof ServerPlayerEntity) {
                ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)entity;
                LootCommand.replace(entity, stacks, slot, stackCount, list);
                serverPlayerEntity.currentScreenHandler.sendContentUpdates();
                continue;
            }
            LootCommand.replace(entity, stacks, slot, stackCount, list);
        }
        messageSender.accept(list);
        return list.size();
    }

    private static int executeSpawn(ServerCommandSource source, Vec3d pos, List<ItemStack> stacks, FeedbackMessage messageSender) throws CommandSyntaxException {
        ServerWorld serverWorld = source.getWorld();
        stacks.forEach(stack -> {
            ItemEntity itemEntity = new ItemEntity(serverWorld, vec3d.x, vec3d.y, vec3d.z, stack.copy());
            itemEntity.setToDefaultPickupDelay();
            serverWorld.spawnEntity(itemEntity);
        });
        messageSender.accept(stacks);
        return stacks.size();
    }

    private static void sendDroppedFeedback(ServerCommandSource source, List<ItemStack> stacks) {
        if (stacks.size() == 1) {
            ItemStack itemStack = stacks.get(0);
            source.sendFeedback(Text.method_43469("commands.drop.success.single", itemStack.getCount(), itemStack.toHoverableText()), false);
        } else {
            source.sendFeedback(Text.method_43469("commands.drop.success.multiple", stacks.size()), false);
        }
    }

    private static void sendDroppedFeedback(ServerCommandSource source, List<ItemStack> stacks, Identifier lootTable) {
        if (stacks.size() == 1) {
            ItemStack itemStack = stacks.get(0);
            source.sendFeedback(Text.method_43469("commands.drop.success.single_with_table", itemStack.getCount(), itemStack.toHoverableText(), lootTable), false);
        } else {
            source.sendFeedback(Text.method_43469("commands.drop.success.multiple_with_table", stacks.size(), lootTable), false);
        }
    }

    private static ItemStack getHeldItem(ServerCommandSource source, EquipmentSlot slot) throws CommandSyntaxException {
        Entity entity = source.getEntityOrThrow();
        if (entity instanceof LivingEntity) {
            return ((LivingEntity)entity).getEquippedStack(slot);
        }
        throw NO_HELD_ITEMS_EXCEPTION.create(entity.getDisplayName());
    }

    private static int executeMine(CommandContext<ServerCommandSource> context, BlockPos pos, ItemStack stack, Target constructor) throws CommandSyntaxException {
        ServerCommandSource serverCommandSource = context.getSource();
        ServerWorld serverWorld = serverCommandSource.getWorld();
        BlockState blockState = serverWorld.getBlockState(pos);
        BlockEntity blockEntity = serverWorld.getBlockEntity(pos);
        LootContext.Builder builder = new LootContext.Builder(serverWorld).parameter(LootContextParameters.ORIGIN, Vec3d.ofCenter(pos)).parameter(LootContextParameters.BLOCK_STATE, blockState).optionalParameter(LootContextParameters.BLOCK_ENTITY, blockEntity).optionalParameter(LootContextParameters.THIS_ENTITY, serverCommandSource.getEntity()).parameter(LootContextParameters.TOOL, stack);
        List<ItemStack> list = blockState.getDroppedStacks(builder);
        return constructor.accept(context, list, stacks -> LootCommand.sendDroppedFeedback(serverCommandSource, stacks, blockState.getBlock().getLootTableId()));
    }

    private static int executeKill(CommandContext<ServerCommandSource> context, Entity entity, Target constructor) throws CommandSyntaxException {
        if (!(entity instanceof LivingEntity)) {
            throw NO_LOOT_TABLE_EXCEPTION.create(entity.getDisplayName());
        }
        Identifier identifier = ((LivingEntity)entity).getLootTable();
        ServerCommandSource serverCommandSource = context.getSource();
        LootContext.Builder builder = new LootContext.Builder(serverCommandSource.getWorld());
        Entity entity2 = serverCommandSource.getEntity();
        if (entity2 instanceof PlayerEntity) {
            builder.parameter(LootContextParameters.LAST_DAMAGE_PLAYER, (PlayerEntity)entity2);
        }
        builder.parameter(LootContextParameters.DAMAGE_SOURCE, DamageSource.MAGIC);
        builder.optionalParameter(LootContextParameters.DIRECT_KILLER_ENTITY, entity2);
        builder.optionalParameter(LootContextParameters.KILLER_ENTITY, entity2);
        builder.parameter(LootContextParameters.THIS_ENTITY, entity);
        builder.parameter(LootContextParameters.ORIGIN, serverCommandSource.getPosition());
        LootTable lootTable = serverCommandSource.getServer().getLootManager().getTable(identifier);
        ObjectArrayList<ItemStack> list = lootTable.generateLoot(builder.build(LootContextTypes.ENTITY));
        return constructor.accept(context, list, stacks -> LootCommand.sendDroppedFeedback(serverCommandSource, stacks, identifier));
    }

    private static int executeLoot(CommandContext<ServerCommandSource> context, Identifier lootTable, Target constructor) throws CommandSyntaxException {
        ServerCommandSource serverCommandSource = context.getSource();
        LootContext.Builder builder = new LootContext.Builder(serverCommandSource.getWorld()).optionalParameter(LootContextParameters.THIS_ENTITY, serverCommandSource.getEntity()).parameter(LootContextParameters.ORIGIN, serverCommandSource.getPosition());
        return LootCommand.getFeedbackMessageSingle(context, lootTable, builder.build(LootContextTypes.CHEST), constructor);
    }

    private static int executeFish(CommandContext<ServerCommandSource> context, Identifier lootTable, BlockPos pos, ItemStack stack, Target constructor) throws CommandSyntaxException {
        ServerCommandSource serverCommandSource = context.getSource();
        LootContext lootContext = new LootContext.Builder(serverCommandSource.getWorld()).parameter(LootContextParameters.ORIGIN, Vec3d.ofCenter(pos)).parameter(LootContextParameters.TOOL, stack).optionalParameter(LootContextParameters.THIS_ENTITY, serverCommandSource.getEntity()).build(LootContextTypes.FISHING);
        return LootCommand.getFeedbackMessageSingle(context, lootTable, lootContext, constructor);
    }

    private static int getFeedbackMessageSingle(CommandContext<ServerCommandSource> context, Identifier lootTable, LootContext lootContext, Target constructor) throws CommandSyntaxException {
        ServerCommandSource serverCommandSource = context.getSource();
        LootTable lootTable2 = serverCommandSource.getServer().getLootManager().getTable(lootTable);
        ObjectArrayList<ItemStack> list = lootTable2.generateLoot(lootContext);
        return constructor.accept(context, list, stacks -> LootCommand.sendDroppedFeedback(serverCommandSource, stacks));
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

