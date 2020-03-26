package net.minecraft.server.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
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
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class LootCommand {
	public static final SuggestionProvider<ServerCommandSource> SUGGESTION_PROVIDER = (commandContext, suggestionsBuilder) -> {
		LootManager lootManager = commandContext.getSource().getMinecraftServer().getLootManager();
		return CommandSource.suggestIdentifiers(lootManager.getTableIds(), suggestionsBuilder);
	};
	private static final DynamicCommandExceptionType NO_HELD_ITEMS_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableText("commands.drop.no_held_items", object)
	);
	private static final DynamicCommandExceptionType NO_LOOT_TABLE_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableText("commands.drop.no_loot_table", object)
	);

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			addTargetArguments(
				CommandManager.literal("loot").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2)),
				(argumentBuilder, target) -> argumentBuilder.then(
							CommandManager.literal("fish")
								.then(
									CommandManager.argument("loot_table", IdentifierArgumentType.identifier())
										.suggests(SUGGESTION_PROVIDER)
										.then(
											CommandManager.argument("pos", BlockPosArgumentType.blockPos())
												.executes(
													commandContext -> executeFish(
															commandContext,
															IdentifierArgumentType.getIdentifier(commandContext, "loot_table"),
															BlockPosArgumentType.getLoadedBlockPos(commandContext, "pos"),
															ItemStack.EMPTY,
															target
														)
												)
												.then(
													CommandManager.argument("tool", ItemStackArgumentType.itemStack())
														.executes(
															commandContext -> executeFish(
																	commandContext,
																	IdentifierArgumentType.getIdentifier(commandContext, "loot_table"),
																	BlockPosArgumentType.getLoadedBlockPos(commandContext, "pos"),
																	ItemStackArgumentType.getItemStackArgument(commandContext, "tool").createStack(1, false),
																	target
																)
														)
												)
												.then(
													CommandManager.literal("mainhand")
														.executes(
															commandContext -> executeFish(
																	commandContext,
																	IdentifierArgumentType.getIdentifier(commandContext, "loot_table"),
																	BlockPosArgumentType.getLoadedBlockPos(commandContext, "pos"),
																	getHeldItem(commandContext.getSource(), EquipmentSlot.MAINHAND),
																	target
																)
														)
												)
												.then(
													CommandManager.literal("offhand")
														.executes(
															commandContext -> executeFish(
																	commandContext,
																	IdentifierArgumentType.getIdentifier(commandContext, "loot_table"),
																	BlockPosArgumentType.getLoadedBlockPos(commandContext, "pos"),
																	getHeldItem(commandContext.getSource(), EquipmentSlot.OFFHAND),
																	target
																)
														)
												)
										)
								)
						)
						.then(
							CommandManager.literal("loot")
								.then(
									CommandManager.argument("loot_table", IdentifierArgumentType.identifier())
										.suggests(SUGGESTION_PROVIDER)
										.executes(commandContext -> executeLoot(commandContext, IdentifierArgumentType.getIdentifier(commandContext, "loot_table"), target))
								)
						)
						.then(
							CommandManager.literal("kill")
								.then(
									CommandManager.argument("target", EntityArgumentType.entity())
										.executes(commandContext -> executeKill(commandContext, EntityArgumentType.getEntity(commandContext, "target"), target))
								)
						)
						.then(
							CommandManager.literal("mine")
								.then(
									CommandManager.argument("pos", BlockPosArgumentType.blockPos())
										.executes(commandContext -> executeMine(commandContext, BlockPosArgumentType.getLoadedBlockPos(commandContext, "pos"), ItemStack.EMPTY, target))
										.then(
											CommandManager.argument("tool", ItemStackArgumentType.itemStack())
												.executes(
													commandContext -> executeMine(
															commandContext,
															BlockPosArgumentType.getLoadedBlockPos(commandContext, "pos"),
															ItemStackArgumentType.getItemStackArgument(commandContext, "tool").createStack(1, false),
															target
														)
												)
										)
										.then(
											CommandManager.literal("mainhand")
												.executes(
													commandContext -> executeMine(
															commandContext,
															BlockPosArgumentType.getLoadedBlockPos(commandContext, "pos"),
															getHeldItem(commandContext.getSource(), EquipmentSlot.MAINHAND),
															target
														)
												)
										)
										.then(
											CommandManager.literal("offhand")
												.executes(
													commandContext -> executeMine(
															commandContext,
															BlockPosArgumentType.getLoadedBlockPos(commandContext, "pos"),
															getHeldItem(commandContext.getSource(), EquipmentSlot.OFFHAND),
															target
														)
												)
										)
								)
						)
			)
		);
	}

	private static <T extends ArgumentBuilder<ServerCommandSource, T>> T addTargetArguments(T rootArgument, LootCommand.SourceConstructor sourceConstructor) {
		return rootArgument.then(
				CommandManager.literal("replace")
					.then(
						CommandManager.literal("entity")
							.then(
								CommandManager.argument("entities", EntityArgumentType.entities())
									.then(
										sourceConstructor.construct(
												CommandManager.argument("slot", ItemSlotArgumentType.itemSlot()),
												(commandContext, list, feedbackMessage) -> executeReplace(
														EntityArgumentType.getEntities(commandContext, "entities"),
														ItemSlotArgumentType.getItemSlot(commandContext, "slot"),
														list.size(),
														list,
														feedbackMessage
													)
											)
											.then(
												sourceConstructor.construct(
													CommandManager.argument("count", IntegerArgumentType.integer(0)),
													(commandContext, list, feedbackMessage) -> executeReplace(
															EntityArgumentType.getEntities(commandContext, "entities"),
															ItemSlotArgumentType.getItemSlot(commandContext, "slot"),
															IntegerArgumentType.getInteger(commandContext, "count"),
															list,
															feedbackMessage
														)
												)
											)
									)
							)
					)
					.then(
						CommandManager.literal("block")
							.then(
								CommandManager.argument("targetPos", BlockPosArgumentType.blockPos())
									.then(
										sourceConstructor.construct(
												CommandManager.argument("slot", ItemSlotArgumentType.itemSlot()),
												(commandContext, list, feedbackMessage) -> executeBlock(
														commandContext.getSource(),
														BlockPosArgumentType.getLoadedBlockPos(commandContext, "targetPos"),
														ItemSlotArgumentType.getItemSlot(commandContext, "slot"),
														list.size(),
														list,
														feedbackMessage
													)
											)
											.then(
												sourceConstructor.construct(
													CommandManager.argument("count", IntegerArgumentType.integer(0)),
													(commandContext, list, feedbackMessage) -> executeBlock(
															commandContext.getSource(),
															BlockPosArgumentType.getLoadedBlockPos(commandContext, "targetPos"),
															IntegerArgumentType.getInteger(commandContext, "slot"),
															IntegerArgumentType.getInteger(commandContext, "count"),
															list,
															feedbackMessage
														)
												)
											)
									)
							)
					)
			)
			.then(
				CommandManager.literal("insert")
					.then(
						sourceConstructor.construct(
							CommandManager.argument("targetPos", BlockPosArgumentType.blockPos()),
							(commandContext, list, feedbackMessage) -> executeInsert(
									commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos(commandContext, "targetPos"), list, feedbackMessage
								)
						)
					)
			)
			.then(
				CommandManager.literal("give")
					.then(
						sourceConstructor.construct(
							CommandManager.argument("players", EntityArgumentType.players()),
							(commandContext, list, feedbackMessage) -> executeGive(EntityArgumentType.getPlayers(commandContext, "players"), list, feedbackMessage)
						)
					)
			)
			.then(
				CommandManager.literal("spawn")
					.then(
						sourceConstructor.construct(
							CommandManager.argument("targetPos", Vec3ArgumentType.vec3()),
							(commandContext, list, feedbackMessage) -> executeSpawn(
									commandContext.getSource(), Vec3ArgumentType.getVec3(commandContext, "targetPos"), list, feedbackMessage
								)
						)
					)
			);
	}

	private static Inventory getBlockInventory(ServerCommandSource source, BlockPos pos) throws CommandSyntaxException {
		BlockEntity blockEntity = source.getWorld().getBlockEntity(pos);
		if (!(blockEntity instanceof Inventory)) {
			throw ReplaceItemCommand.BLOCK_FAILED_EXCEPTION.create();
		} else {
			return (Inventory)blockEntity;
		}
	}

	private static int executeInsert(ServerCommandSource source, BlockPos targetPos, List<ItemStack> stacks, LootCommand.FeedbackMessage messageSender) throws CommandSyntaxException {
		Inventory inventory = getBlockInventory(source, targetPos);
		List<ItemStack> list = Lists.<ItemStack>newArrayListWithCapacity(stacks.size());

		for (ItemStack itemStack : stacks) {
			if (insert(inventory, itemStack.copy())) {
				inventory.markDirty();
				list.add(itemStack);
			}
		}

		messageSender.accept(list);
		return list.size();
	}

	private static boolean insert(Inventory inventory, ItemStack stack) {
		boolean bl = false;

		for (int i = 0; i < inventory.size() && !stack.isEmpty(); i++) {
			ItemStack itemStack = inventory.getStack(i);
			if (inventory.isValid(i, stack)) {
				if (itemStack.isEmpty()) {
					inventory.setStack(i, stack);
					bl = true;
					break;
				}

				if (itemsMatch(itemStack, stack)) {
					int j = stack.getMaxCount() - itemStack.getCount();
					int k = Math.min(stack.getCount(), j);
					stack.decrement(k);
					itemStack.increment(k);
					bl = true;
				}
			}
		}

		return bl;
	}

	private static int executeBlock(
		ServerCommandSource source, BlockPos targetPos, int slot, int stackCount, List<ItemStack> stacks, LootCommand.FeedbackMessage messageSender
	) throws CommandSyntaxException {
		Inventory inventory = getBlockInventory(source, targetPos);
		int i = inventory.size();
		if (slot >= 0 && slot < i) {
			List<ItemStack> list = Lists.<ItemStack>newArrayListWithCapacity(stacks.size());

			for (int j = 0; j < stackCount; j++) {
				int k = slot + j;
				ItemStack itemStack = j < stacks.size() ? (ItemStack)stacks.get(j) : ItemStack.EMPTY;
				if (inventory.isValid(k, itemStack)) {
					inventory.setStack(k, itemStack);
					list.add(itemStack);
				}
			}

			messageSender.accept(list);
			return list.size();
		} else {
			throw ReplaceItemCommand.SLOT_INAPPLICABLE_EXCEPTION.create(slot);
		}
	}

	private static boolean itemsMatch(ItemStack first, ItemStack second) {
		return first.getItem() == second.getItem()
			&& first.getDamage() == second.getDamage()
			&& first.getCount() <= first.getMaxCount()
			&& Objects.equals(first.getTag(), second.getTag());
	}

	private static int executeGive(Collection<ServerPlayerEntity> players, List<ItemStack> stacks, LootCommand.FeedbackMessage messageSender) throws CommandSyntaxException {
		List<ItemStack> list = Lists.<ItemStack>newArrayListWithCapacity(stacks.size());

		for (ItemStack itemStack : stacks) {
			for (ServerPlayerEntity serverPlayerEntity : players) {
				if (serverPlayerEntity.inventory.insertStack(itemStack.copy())) {
					list.add(itemStack);
				}
			}
		}

		messageSender.accept(list);
		return list.size();
	}

	private static void replace(Entity entity, List<ItemStack> stacks, int slot, int stackCount, List<ItemStack> addedStacks) {
		for (int i = 0; i < stackCount; i++) {
			ItemStack itemStack = i < stacks.size() ? (ItemStack)stacks.get(i) : ItemStack.EMPTY;
			if (entity.equip(slot + i, itemStack.copy())) {
				addedStacks.add(itemStack);
			}
		}
	}

	private static int executeReplace(
		Collection<? extends Entity> targets, int slot, int stackCount, List<ItemStack> stacks, LootCommand.FeedbackMessage messageSender
	) throws CommandSyntaxException {
		List<ItemStack> list = Lists.<ItemStack>newArrayListWithCapacity(stacks.size());

		for (Entity entity : targets) {
			if (entity instanceof ServerPlayerEntity) {
				ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)entity;
				serverPlayerEntity.playerScreenHandler.sendContentUpdates();
				replace(entity, stacks, slot, stackCount, list);
				serverPlayerEntity.playerScreenHandler.sendContentUpdates();
			} else {
				replace(entity, stacks, slot, stackCount, list);
			}
		}

		messageSender.accept(list);
		return list.size();
	}

	private static int executeSpawn(ServerCommandSource source, Vec3d pos, List<ItemStack> stacks, LootCommand.FeedbackMessage messageSender) throws CommandSyntaxException {
		ServerWorld serverWorld = source.getWorld();
		stacks.forEach(itemStack -> {
			ItemEntity itemEntity = new ItemEntity(serverWorld, pos.x, pos.y, pos.z, itemStack.copy());
			itemEntity.setToDefaultPickupDelay();
			serverWorld.spawnEntity(itemEntity);
		});
		messageSender.accept(stacks);
		return stacks.size();
	}

	private static void sendDroppedFeedback(ServerCommandSource source, List<ItemStack> stacks) {
		if (stacks.size() == 1) {
			ItemStack itemStack = (ItemStack)stacks.get(0);
			source.sendFeedback(new TranslatableText("commands.drop.success.single", itemStack.getCount(), itemStack.toHoverableText()), false);
		} else {
			source.sendFeedback(new TranslatableText("commands.drop.success.multiple", stacks.size()), false);
		}
	}

	private static void sendDroppedFeedback(ServerCommandSource source, List<ItemStack> stacks, Identifier lootTable) {
		if (stacks.size() == 1) {
			ItemStack itemStack = (ItemStack)stacks.get(0);
			source.sendFeedback(new TranslatableText("commands.drop.success.single_with_table", itemStack.getCount(), itemStack.toHoverableText(), lootTable), false);
		} else {
			source.sendFeedback(new TranslatableText("commands.drop.success.multiple_with_table", stacks.size(), lootTable), false);
		}
	}

	private static ItemStack getHeldItem(ServerCommandSource source, EquipmentSlot slot) throws CommandSyntaxException {
		Entity entity = source.getEntityOrThrow();
		if (entity instanceof LivingEntity) {
			return ((LivingEntity)entity).getEquippedStack(slot);
		} else {
			throw NO_HELD_ITEMS_EXCEPTION.create(entity.getDisplayName());
		}
	}

	private static int executeMine(CommandContext<ServerCommandSource> context, BlockPos pos, ItemStack stack, LootCommand.Target constructor) throws CommandSyntaxException {
		ServerCommandSource serverCommandSource = context.getSource();
		ServerWorld serverWorld = serverCommandSource.getWorld();
		BlockState blockState = serverWorld.getBlockState(pos);
		BlockEntity blockEntity = serverWorld.getBlockEntity(pos);
		LootContext.Builder builder = new LootContext.Builder(serverWorld)
			.put(LootContextParameters.POSITION, pos)
			.put(LootContextParameters.BLOCK_STATE, blockState)
			.putNullable(LootContextParameters.BLOCK_ENTITY, blockEntity)
			.putNullable(LootContextParameters.THIS_ENTITY, serverCommandSource.getEntity())
			.put(LootContextParameters.TOOL, stack);
		List<ItemStack> list = blockState.getDroppedStacks(builder);
		return constructor.accept(context, list, listx -> sendDroppedFeedback(serverCommandSource, listx, blockState.getBlock().getDropTableID()));
	}

	private static int executeKill(CommandContext<ServerCommandSource> context, Entity entity, LootCommand.Target constructor) throws CommandSyntaxException {
		if (!(entity instanceof LivingEntity)) {
			throw NO_LOOT_TABLE_EXCEPTION.create(entity.getDisplayName());
		} else {
			Identifier identifier = ((LivingEntity)entity).getLootTable();
			ServerCommandSource serverCommandSource = context.getSource();
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
			LootTable lootTable = serverCommandSource.getMinecraftServer().getLootManager().getTable(identifier);
			List<ItemStack> list = lootTable.getDrops(builder.build(LootContextTypes.ENTITY));
			return constructor.accept(context, list, listx -> sendDroppedFeedback(serverCommandSource, listx, identifier));
		}
	}

	private static int executeLoot(CommandContext<ServerCommandSource> context, Identifier lootTable, LootCommand.Target constructor) throws CommandSyntaxException {
		ServerCommandSource serverCommandSource = context.getSource();
		LootContext.Builder builder = new LootContext.Builder(serverCommandSource.getWorld())
			.putNullable(LootContextParameters.THIS_ENTITY, serverCommandSource.getEntity())
			.put(LootContextParameters.POSITION, new BlockPos(serverCommandSource.getPosition()));
		return getFeedbackMessageSingle(context, lootTable, builder.build(LootContextTypes.CHEST), constructor);
	}

	private static int executeFish(
		CommandContext<ServerCommandSource> context, Identifier lootTable, BlockPos pos, ItemStack stack, LootCommand.Target constructor
	) throws CommandSyntaxException {
		ServerCommandSource serverCommandSource = context.getSource();
		LootContext lootContext = new LootContext.Builder(serverCommandSource.getWorld())
			.put(LootContextParameters.POSITION, pos)
			.put(LootContextParameters.TOOL, stack)
			.build(LootContextTypes.FISHING);
		return getFeedbackMessageSingle(context, lootTable, lootContext, constructor);
	}

	private static int getFeedbackMessageSingle(
		CommandContext<ServerCommandSource> context, Identifier lootTable, LootContext lootContext, LootCommand.Target constructor
	) throws CommandSyntaxException {
		ServerCommandSource serverCommandSource = context.getSource();
		LootTable lootTable2 = serverCommandSource.getMinecraftServer().getLootManager().getTable(lootTable);
		List<ItemStack> list = lootTable2.getDrops(lootContext);
		return constructor.accept(context, list, listx -> sendDroppedFeedback(serverCommandSource, listx));
	}

	@FunctionalInterface
	interface FeedbackMessage {
		void accept(List<ItemStack> items) throws CommandSyntaxException;
	}

	@FunctionalInterface
	interface SourceConstructor {
		ArgumentBuilder<ServerCommandSource, ?> construct(ArgumentBuilder<ServerCommandSource, ?> builder, LootCommand.Target target);
	}

	@FunctionalInterface
	interface Target {
		int accept(CommandContext<ServerCommandSource> context, List<ItemStack> items, LootCommand.FeedbackMessage messageSender) throws CommandSyntaxException;
	}
}
