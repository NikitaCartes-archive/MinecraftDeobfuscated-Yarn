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
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableTextComponent;
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
		LootManager lootManager = commandContext.getSource().getMinecraftServer().getLootManager();
		return CommandSource.suggestIdentifiers(lootManager.getSupplierNames(), suggestionsBuilder);
	};
	private static final DynamicCommandExceptionType NO_HELD_ITEMS_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableTextComponent("commands.drop.no_held_items", object)
	);
	private static final DynamicCommandExceptionType NO_LOOT_TABLE_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableTextComponent("commands.drop.no_loot_table", object)
	);

	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			method_13206(
				CommandManager.literal("loot").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2)),
				(argumentBuilder, argumentCallback) -> argumentBuilder.then(
							CommandManager.literal("fish")
								.then(
									CommandManager.argument("loot_table", IdentifierArgumentType.create())
										.suggests(SUGGESTION_PROVIDER)
										.then(
											CommandManager.argument("pos", BlockPosArgumentType.create())
												.executes(
													commandContext -> method_13199(
															commandContext,
															IdentifierArgumentType.getIdentifier(commandContext, "loot_table"),
															BlockPosArgumentType.getLoadedBlockPos(commandContext, "pos"),
															ItemStack.EMPTY,
															argumentCallback
														)
												)
												.then(
													CommandManager.argument("tool", ItemStackArgumentType.create())
														.executes(
															commandContext -> method_13199(
																	commandContext,
																	IdentifierArgumentType.getIdentifier(commandContext, "loot_table"),
																	BlockPosArgumentType.getLoadedBlockPos(commandContext, "pos"),
																	ItemStackArgumentType.getItemStackArgument(commandContext, "tool").method_9781(1, false),
																	argumentCallback
																)
														)
												)
												.then(
													CommandManager.literal("mainhand")
														.executes(
															commandContext -> method_13199(
																	commandContext,
																	IdentifierArgumentType.getIdentifier(commandContext, "loot_table"),
																	BlockPosArgumentType.getLoadedBlockPos(commandContext, "pos"),
																	getHeldItem(commandContext.getSource(), EquipmentSlot.HAND_MAIN),
																	argumentCallback
																)
														)
												)
												.then(
													CommandManager.literal("offhand")
														.executes(
															commandContext -> method_13199(
																	commandContext,
																	IdentifierArgumentType.getIdentifier(commandContext, "loot_table"),
																	BlockPosArgumentType.getLoadedBlockPos(commandContext, "pos"),
																	getHeldItem(commandContext.getSource(), EquipmentSlot.HAND_OFF),
																	argumentCallback
																)
														)
												)
										)
								)
						)
						.then(
							CommandManager.literal("loot")
								.then(
									CommandManager.argument("loot_table", IdentifierArgumentType.create())
										.suggests(SUGGESTION_PROVIDER)
										.executes(commandContext -> method_13197(commandContext, IdentifierArgumentType.getIdentifier(commandContext, "loot_table"), argumentCallback))
								)
						)
						.then(
							CommandManager.literal("kill")
								.then(
									CommandManager.argument("target", EntityArgumentType.entity())
										.executes(commandContext -> method_13189(commandContext, EntityArgumentType.getEntity(commandContext, "target"), argumentCallback))
								)
						)
						.then(
							CommandManager.literal("mine")
								.then(
									CommandManager.argument("pos", BlockPosArgumentType.create())
										.executes(
											commandContext -> method_13219(commandContext, BlockPosArgumentType.getLoadedBlockPos(commandContext, "pos"), ItemStack.EMPTY, argumentCallback)
										)
										.then(
											CommandManager.argument("tool", ItemStackArgumentType.create())
												.executes(
													commandContext -> method_13219(
															commandContext,
															BlockPosArgumentType.getLoadedBlockPos(commandContext, "pos"),
															ItemStackArgumentType.getItemStackArgument(commandContext, "tool").method_9781(1, false),
															argumentCallback
														)
												)
										)
										.then(
											CommandManager.literal("mainhand")
												.executes(
													commandContext -> method_13219(
															commandContext,
															BlockPosArgumentType.getLoadedBlockPos(commandContext, "pos"),
															getHeldItem(commandContext.getSource(), EquipmentSlot.HAND_MAIN),
															argumentCallback
														)
												)
										)
										.then(
											CommandManager.literal("offhand")
												.executes(
													commandContext -> method_13219(
															commandContext,
															BlockPosArgumentType.getLoadedBlockPos(commandContext, "pos"),
															getHeldItem(commandContext.getSource(), EquipmentSlot.HAND_OFF),
															argumentCallback
														)
												)
										)
								)
						)
			)
		);
	}

	private static <T extends ArgumentBuilder<ServerCommandSource, T>> T method_13206(
		T argumentBuilder, LootCommand.LootTableArgumentsBuilder lootTableArgumentsBuilder
	) {
		return argumentBuilder.then(
				CommandManager.literal("replace")
					.then(
						CommandManager.literal("entity")
							.then(
								CommandManager.argument("entities", EntityArgumentType.entities())
									.then(
										lootTableArgumentsBuilder.construct(
												CommandManager.argument("slot", ItemSlotArgumentType.create()),
												(commandContext, list, arg) -> executeReplace(
														EntityArgumentType.getEntities(commandContext, "entities"), ItemSlotArgumentType.getItemSlot(commandContext, "slot"), list.size(), list, arg
													)
											)
											.then(
												lootTableArgumentsBuilder.construct(
													CommandManager.argument("count", IntegerArgumentType.integer(0)),
													(commandContext, list, arg) -> executeReplace(
															EntityArgumentType.getEntities(commandContext, "entities"),
															ItemSlotArgumentType.getItemSlot(commandContext, "slot"),
															IntegerArgumentType.getInteger(commandContext, "count"),
															list,
															arg
														)
												)
											)
									)
							)
					)
					.then(
						CommandManager.literal("block")
							.then(
								CommandManager.argument("targetPos", BlockPosArgumentType.create())
									.then(
										lootTableArgumentsBuilder.construct(
												CommandManager.argument("slot", ItemSlotArgumentType.create()),
												(commandContext, list, arg) -> executeBlock(
														commandContext.getSource(),
														BlockPosArgumentType.getLoadedBlockPos(commandContext, "targetPos"),
														ItemSlotArgumentType.getItemSlot(commandContext, "slot"),
														list.size(),
														list,
														arg
													)
											)
											.then(
												lootTableArgumentsBuilder.construct(
													CommandManager.argument("count", IntegerArgumentType.integer(0)),
													(commandContext, list, arg) -> executeBlock(
															commandContext.getSource(),
															BlockPosArgumentType.getLoadedBlockPos(commandContext, "targetPos"),
															IntegerArgumentType.getInteger(commandContext, "slot"),
															IntegerArgumentType.getInteger(commandContext, "count"),
															list,
															arg
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
						lootTableArgumentsBuilder.construct(
							CommandManager.argument("targetPos", BlockPosArgumentType.create()),
							(commandContext, list, arg) -> executeInsert(commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos(commandContext, "targetPos"), list, arg)
						)
					)
			)
			.then(
				CommandManager.literal("give")
					.then(
						lootTableArgumentsBuilder.construct(
							CommandManager.argument("players", EntityArgumentType.players()),
							(commandContext, list, arg) -> executeGive(EntityArgumentType.getPlayers(commandContext, "players"), list, arg)
						)
					)
			)
			.then(
				CommandManager.literal("spawn")
					.then(
						lootTableArgumentsBuilder.construct(
							CommandManager.argument("targetPos", Vec3ArgumentType.create()),
							(commandContext, list, arg) -> executeSpawn(commandContext.getSource(), Vec3ArgumentType.getVec3(commandContext, "targetPos"), list, arg)
						)
					)
			);
	}

	private static Inventory getBlockInventory(ServerCommandSource serverCommandSource, BlockPos blockPos) throws CommandSyntaxException {
		BlockEntity blockEntity = serverCommandSource.getWorld().getBlockEntity(blockPos);
		if (!(blockEntity instanceof Inventory)) {
			throw ReplaceItemCommand.BLOCK_FAILED_EXCEPTION.create();
		} else {
			return (Inventory)blockEntity;
		}
	}

	private static int executeInsert(ServerCommandSource serverCommandSource, BlockPos blockPos, List<ItemStack> list, LootCommand.class_3040 arg) throws CommandSyntaxException {
		Inventory inventory = getBlockInventory(serverCommandSource, blockPos);
		List<ItemStack> list2 = Lists.<ItemStack>newArrayListWithCapacity(list.size());

		for (ItemStack itemStack : list) {
			if (insert(inventory, itemStack.copy())) {
				inventory.markDirty();
				list2.add(itemStack);
			}
		}

		arg.accept(list2);
		return list2.size();
	}

	private static boolean insert(Inventory inventory, ItemStack itemStack) {
		boolean bl = false;

		for (int i = 0; i < inventory.getInvSize() && !itemStack.isEmpty(); i++) {
			ItemStack itemStack2 = inventory.getInvStack(i);
			if (inventory.isValidInvStack(i, itemStack)) {
				if (itemStack2.isEmpty()) {
					inventory.setInvStack(i, itemStack);
					bl = true;
					break;
				}

				if (itemsMatch(itemStack2, itemStack)) {
					int j = itemStack.getMaxAmount() - itemStack2.getAmount();
					int k = Math.min(itemStack.getAmount(), j);
					itemStack.subtractAmount(k);
					itemStack2.addAmount(k);
					bl = true;
				}
			}
		}

		return bl;
	}

	private static int executeBlock(ServerCommandSource serverCommandSource, BlockPos blockPos, int i, int j, List<ItemStack> list, LootCommand.class_3040 arg) throws CommandSyntaxException {
		Inventory inventory = getBlockInventory(serverCommandSource, blockPos);
		int k = inventory.getInvSize();
		if (i >= 0 && i < k) {
			List<ItemStack> list2 = Lists.<ItemStack>newArrayListWithCapacity(list.size());

			for (int l = 0; l < j; l++) {
				int m = i + l;
				ItemStack itemStack = l < list.size() ? (ItemStack)list.get(l) : ItemStack.EMPTY;
				if (inventory.isValidInvStack(m, itemStack)) {
					inventory.setInvStack(m, itemStack);
					list2.add(itemStack);
				}
			}

			arg.accept(list2);
			return list2.size();
		} else {
			throw ReplaceItemCommand.SLOT_INAPPLICABLE_EXCEPTION.create(i);
		}
	}

	private static boolean itemsMatch(ItemStack itemStack, ItemStack itemStack2) {
		return itemStack.getItem() == itemStack2.getItem()
			&& itemStack.getDamage() == itemStack2.getDamage()
			&& itemStack.getAmount() <= itemStack.getMaxAmount()
			&& Objects.equals(itemStack.getTag(), itemStack2.getTag());
	}

	private static int executeGive(Collection<ServerPlayerEntity> collection, List<ItemStack> list, LootCommand.class_3040 arg) throws CommandSyntaxException {
		List<ItemStack> list2 = Lists.<ItemStack>newArrayListWithCapacity(list.size());

		for (ItemStack itemStack : list) {
			for (ServerPlayerEntity serverPlayerEntity : collection) {
				if (serverPlayerEntity.inventory.insertStack(itemStack.copy())) {
					list2.add(itemStack);
				}
			}
		}

		arg.accept(list2);
		return list2.size();
	}

	private static void replace(Entity entity, List<ItemStack> list, int i, int j, List<ItemStack> list2) {
		for (int k = 0; k < j; k++) {
			ItemStack itemStack = k < list.size() ? (ItemStack)list.get(k) : ItemStack.EMPTY;
			if (entity.equip(i + k, itemStack.copy())) {
				list2.add(itemStack);
			}
		}
	}

	private static int executeReplace(Collection<? extends Entity> collection, int i, int j, List<ItemStack> list, LootCommand.class_3040 arg) throws CommandSyntaxException {
		List<ItemStack> list2 = Lists.<ItemStack>newArrayListWithCapacity(list.size());

		for (Entity entity : collection) {
			if (entity instanceof ServerPlayerEntity) {
				ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)entity;
				serverPlayerEntity.playerContainer.sendContentUpdates();
				replace(entity, list, i, j, list2);
				serverPlayerEntity.playerContainer.sendContentUpdates();
			} else {
				replace(entity, list, i, j, list2);
			}
		}

		arg.accept(list2);
		return list2.size();
	}

	private static int executeSpawn(ServerCommandSource serverCommandSource, Vec3d vec3d, List<ItemStack> list, LootCommand.class_3040 arg) throws CommandSyntaxException {
		ServerWorld serverWorld = serverCommandSource.getWorld();
		list.forEach(itemStack -> {
			ItemEntity itemEntity = new ItemEntity(serverWorld, vec3d.x, vec3d.y, vec3d.z, itemStack.copy());
			itemEntity.setToDefaultPickupDelay();
			serverWorld.spawnEntity(itemEntity);
		});
		arg.accept(list);
		return list.size();
	}

	private static void sendDroppedFeedback(ServerCommandSource serverCommandSource, List<ItemStack> list) {
		if (list.size() == 1) {
			ItemStack itemStack = (ItemStack)list.get(0);
			serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.drop.success.single", itemStack.getAmount(), itemStack.toTextComponent()), false);
		} else {
			serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.drop.success.multiple", list.size()), false);
		}
	}

	private static void sendDroppedFeedback(ServerCommandSource serverCommandSource, List<ItemStack> list, Identifier identifier) {
		if (list.size() == 1) {
			ItemStack itemStack = (ItemStack)list.get(0);
			serverCommandSource.sendFeedback(
				new TranslatableTextComponent("commands.drop.success.single_with_table", itemStack.getAmount(), itemStack.toTextComponent(), identifier), false
			);
		} else {
			serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.drop.success.multiple_with_table", list.size(), identifier), false);
		}
	}

	private static ItemStack getHeldItem(ServerCommandSource serverCommandSource, EquipmentSlot equipmentSlot) throws CommandSyntaxException {
		Entity entity = serverCommandSource.getEntityOrThrow();
		if (entity instanceof LivingEntity) {
			return ((LivingEntity)entity).getEquippedStack(equipmentSlot);
		} else {
			throw NO_HELD_ITEMS_EXCEPTION.create(entity.getDisplayName());
		}
	}

	private static int method_13219(
		CommandContext<ServerCommandSource> commandContext, BlockPos blockPos, ItemStack itemStack, LootCommand.ArgumentCallback argumentCallback
	) throws CommandSyntaxException {
		ServerCommandSource serverCommandSource = commandContext.getSource();
		ServerWorld serverWorld = serverCommandSource.getWorld();
		BlockState blockState = serverWorld.getBlockState(blockPos);
		BlockEntity blockEntity = serverWorld.getBlockEntity(blockPos);
		LootContext.Builder builder = new LootContext.Builder(serverWorld)
			.put(LootContextParameters.field_1232, blockPos)
			.put(LootContextParameters.field_1224, blockState)
			.putNullable(LootContextParameters.field_1228, blockEntity)
			.putNullable(LootContextParameters.field_1226, serverCommandSource.getEntity())
			.put(LootContextParameters.field_1229, itemStack);
		List<ItemStack> list = blockState.getDroppedStacks(builder);
		return argumentCallback.accept(commandContext, list, listx -> sendDroppedFeedback(serverCommandSource, listx, blockState.getBlock().getDropTableId()));
	}

	private static int method_13189(CommandContext<ServerCommandSource> commandContext, Entity entity, LootCommand.ArgumentCallback argumentCallback) throws CommandSyntaxException {
		if (!(entity instanceof LivingEntity)) {
			throw NO_LOOT_TABLE_EXCEPTION.create(entity.getDisplayName());
		} else {
			Identifier identifier = ((LivingEntity)entity).getLootTable();
			ServerCommandSource serverCommandSource = commandContext.getSource();
			LootContext.Builder builder = new LootContext.Builder(serverCommandSource.getWorld());
			Entity entity2 = serverCommandSource.getEntity();
			if (entity2 instanceof PlayerEntity) {
				builder.put(LootContextParameters.field_1233, (PlayerEntity)entity2);
			}

			builder.put(LootContextParameters.field_1231, DamageSource.MAGIC);
			builder.putNullable(LootContextParameters.field_1227, entity2);
			builder.putNullable(LootContextParameters.field_1230, entity2);
			builder.put(LootContextParameters.field_1226, entity);
			builder.put(LootContextParameters.field_1232, new BlockPos(serverCommandSource.getPosition()));
			LootSupplier lootSupplier = serverCommandSource.getMinecraftServer().getLootManager().getSupplier(identifier);
			List<ItemStack> list = lootSupplier.getDrops(builder.build(LootContextTypes.ENTITY));
			return argumentCallback.accept(commandContext, list, listx -> sendDroppedFeedback(serverCommandSource, listx, identifier));
		}
	}

	private static int method_13197(CommandContext<ServerCommandSource> commandContext, Identifier identifier, LootCommand.ArgumentCallback argumentCallback) throws CommandSyntaxException {
		ServerCommandSource serverCommandSource = commandContext.getSource();
		LootContext.Builder builder = new LootContext.Builder(serverCommandSource.getWorld())
			.putNullable(LootContextParameters.field_1226, serverCommandSource.getEntity())
			.put(LootContextParameters.field_1232, new BlockPos(serverCommandSource.getPosition()));
		return method_13180(commandContext, identifier, builder.build(LootContextTypes.CHEST), argumentCallback);
	}

	private static int method_13199(
		CommandContext<ServerCommandSource> commandContext,
		Identifier identifier,
		BlockPos blockPos,
		ItemStack itemStack,
		LootCommand.ArgumentCallback argumentCallback
	) throws CommandSyntaxException {
		ServerCommandSource serverCommandSource = commandContext.getSource();
		LootContext lootContext = new LootContext.Builder(serverCommandSource.getWorld())
			.put(LootContextParameters.field_1232, blockPos)
			.put(LootContextParameters.field_1229, itemStack)
			.build(LootContextTypes.FISHING);
		return method_13180(commandContext, identifier, lootContext, argumentCallback);
	}

	private static int method_13180(
		CommandContext<ServerCommandSource> commandContext, Identifier identifier, LootContext lootContext, LootCommand.ArgumentCallback argumentCallback
	) throws CommandSyntaxException {
		ServerCommandSource serverCommandSource = commandContext.getSource();
		LootSupplier lootSupplier = serverCommandSource.getMinecraftServer().getLootManager().getSupplier(identifier);
		List<ItemStack> list = lootSupplier.getDrops(lootContext);
		return argumentCallback.accept(commandContext, list, listx -> sendDroppedFeedback(serverCommandSource, listx));
	}

	@FunctionalInterface
	interface ArgumentCallback {
		int accept(CommandContext<ServerCommandSource> commandContext, List<ItemStack> list, LootCommand.class_3040 arg) throws CommandSyntaxException;
	}

	@FunctionalInterface
	interface LootTableArgumentsBuilder {
		ArgumentBuilder<ServerCommandSource, ?> construct(ArgumentBuilder<ServerCommandSource, ?> argumentBuilder, LootCommand.ArgumentCallback argumentCallback);
	}

	@FunctionalInterface
	interface class_3040 {
		void accept(List<ItemStack> list) throws CommandSyntaxException;
	}
}
