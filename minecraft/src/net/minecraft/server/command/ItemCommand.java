package net.minecraft.server.command;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.class_5630;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.command.argument.ItemSlotArgumentType;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;

public class ItemCommand {
	static final Dynamic3CommandExceptionType NOT_A_CONTAINER_TARGET_EXCEPTION = new Dynamic3CommandExceptionType(
		(object, object2, object3) -> new TranslatableText("commands.item.target.not_a_container", object, object2, object3)
	);
	private static final Dynamic3CommandExceptionType NOT_A_CONTAINER_SOURCE_EXCEPTION = new Dynamic3CommandExceptionType(
		(object, object2, object3) -> new TranslatableText("commands.item.source.not_a_container", object, object2, object3)
	);
	static final DynamicCommandExceptionType NO_SUCH_SLOT_TARGET_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableText("commands.item.target.no_such_slot", object)
	);
	private static final DynamicCommandExceptionType NO_SUCH_SLOT_SOURCE_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableText("commands.item.source.no_such_slot", object)
	);
	private static final DynamicCommandExceptionType NO_CHANGES_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableText("commands.item.target.no_changes", object)
	);
	private static final Dynamic2CommandExceptionType KNOWN_ITEM_EXCEPTION = new Dynamic2CommandExceptionType(
		(object, object2) -> new TranslatableText("commands.item.target.no_changed.known_item", object, object2)
	);
	private static final SuggestionProvider<ServerCommandSource> MODIFIER_SUGGESTION_PROVIDER = (commandContext, suggestionsBuilder) -> {
		LootFunctionManager lootFunctionManager = commandContext.getSource().getMinecraftServer().getItemModifierManager();
		return CommandSource.suggestIdentifiers(lootFunctionManager.getFunctionIds(), suggestionsBuilder);
	};

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("item")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.then(
					CommandManager.literal("block")
						.then(
							CommandManager.argument("pos", BlockPosArgumentType.blockPos())
								.then(
									CommandManager.argument("slot", ItemSlotArgumentType.itemSlot())
										.then(
											CommandManager.literal("replace")
												.then(
													CommandManager.argument("item", ItemStackArgumentType.itemStack())
														.executes(
															commandContext -> executeBlockReplace(
																	commandContext.getSource(),
																	BlockPosArgumentType.getLoadedBlockPos(commandContext, "pos"),
																	ItemSlotArgumentType.getItemSlot(commandContext, "slot"),
																	ItemStackArgumentType.getItemStackArgument(commandContext, "item").createStack(1, false)
																)
														)
														.then(
															CommandManager.argument("count", IntegerArgumentType.integer(1, 64))
																.executes(
																	commandContext -> executeBlockReplace(
																			commandContext.getSource(),
																			BlockPosArgumentType.getLoadedBlockPos(commandContext, "pos"),
																			ItemSlotArgumentType.getItemSlot(commandContext, "slot"),
																			ItemStackArgumentType.getItemStackArgument(commandContext, "item")
																				.createStack(IntegerArgumentType.getInteger(commandContext, "count"), true)
																		)
																)
														)
												)
										)
										.then(
											CommandManager.literal("modify")
												.then(
													CommandManager.argument("modifier", IdentifierArgumentType.identifier())
														.suggests(MODIFIER_SUGGESTION_PROVIDER)
														.executes(
															commandContext -> executeBlockModify(
																	commandContext.getSource(),
																	BlockPosArgumentType.getLoadedBlockPos(commandContext, "pos"),
																	ItemSlotArgumentType.getItemSlot(commandContext, "slot"),
																	IdentifierArgumentType.getItemModifierArgument(commandContext, "modifier")
																)
														)
												)
										)
										.then(
											CommandManager.literal("copy")
												.then(
													CommandManager.literal("block")
														.then(
															CommandManager.argument("source", BlockPosArgumentType.blockPos())
																.then(
																	CommandManager.argument("sourceSlot", ItemSlotArgumentType.itemSlot())
																		.executes(
																			commandContext -> executeBlockCopyBlock(
																					commandContext.getSource(),
																					BlockPosArgumentType.getLoadedBlockPos(commandContext, "source"),
																					ItemSlotArgumentType.getItemSlot(commandContext, "sourceSlot"),
																					BlockPosArgumentType.getLoadedBlockPos(commandContext, "pos"),
																					ItemSlotArgumentType.getItemSlot(commandContext, "slot")
																				)
																		)
																		.then(
																			CommandManager.argument("modifier", IdentifierArgumentType.identifier())
																				.suggests(MODIFIER_SUGGESTION_PROVIDER)
																				.executes(
																					commandContext -> executeBlockCopyBlock(
																							commandContext.getSource(),
																							BlockPosArgumentType.getLoadedBlockPos(commandContext, "source"),
																							ItemSlotArgumentType.getItemSlot(commandContext, "sourceSlot"),
																							BlockPosArgumentType.getLoadedBlockPos(commandContext, "pos"),
																							ItemSlotArgumentType.getItemSlot(commandContext, "slot"),
																							IdentifierArgumentType.getItemModifierArgument(commandContext, "modifier")
																						)
																				)
																		)
																)
														)
												)
												.then(
													CommandManager.literal("entity")
														.then(
															CommandManager.argument("source", EntityArgumentType.entity())
																.then(
																	CommandManager.argument("sourceSlot", ItemSlotArgumentType.itemSlot())
																		.executes(
																			commandContext -> executeBlockCopyEntity(
																					commandContext.getSource(),
																					EntityArgumentType.getEntity(commandContext, "source"),
																					ItemSlotArgumentType.getItemSlot(commandContext, "sourceSlot"),
																					BlockPosArgumentType.getLoadedBlockPos(commandContext, "pos"),
																					ItemSlotArgumentType.getItemSlot(commandContext, "slot")
																				)
																		)
																		.then(
																			CommandManager.argument("modifier", IdentifierArgumentType.identifier())
																				.suggests(MODIFIER_SUGGESTION_PROVIDER)
																				.executes(
																					commandContext -> executeBlockCopyEntity(
																							commandContext.getSource(),
																							EntityArgumentType.getEntity(commandContext, "source"),
																							ItemSlotArgumentType.getItemSlot(commandContext, "sourceSlot"),
																							BlockPosArgumentType.getLoadedBlockPos(commandContext, "pos"),
																							ItemSlotArgumentType.getItemSlot(commandContext, "slot"),
																							IdentifierArgumentType.getItemModifierArgument(commandContext, "modifier")
																						)
																				)
																		)
																)
														)
												)
										)
								)
						)
				)
				.then(
					CommandManager.literal("entity")
						.then(
							CommandManager.argument("targets", EntityArgumentType.entities())
								.then(
									CommandManager.argument("slot", ItemSlotArgumentType.itemSlot())
										.then(
											CommandManager.literal("replace")
												.then(
													CommandManager.argument("item", ItemStackArgumentType.itemStack())
														.executes(
															commandContext -> executeEntityReplace(
																	commandContext.getSource(),
																	EntityArgumentType.getEntities(commandContext, "targets"),
																	ItemSlotArgumentType.getItemSlot(commandContext, "slot"),
																	ItemStackArgumentType.getItemStackArgument(commandContext, "item").createStack(1, false)
																)
														)
														.then(
															CommandManager.argument("count", IntegerArgumentType.integer(1, 64))
																.executes(
																	commandContext -> executeEntityReplace(
																			commandContext.getSource(),
																			EntityArgumentType.getEntities(commandContext, "targets"),
																			ItemSlotArgumentType.getItemSlot(commandContext, "slot"),
																			ItemStackArgumentType.getItemStackArgument(commandContext, "item")
																				.createStack(IntegerArgumentType.getInteger(commandContext, "count"), true)
																		)
																)
														)
												)
										)
										.then(
											CommandManager.literal("modify")
												.then(
													CommandManager.argument("modifier", IdentifierArgumentType.identifier())
														.suggests(MODIFIER_SUGGESTION_PROVIDER)
														.executes(
															commandContext -> executeEntityModify(
																	commandContext.getSource(),
																	EntityArgumentType.getEntities(commandContext, "targets"),
																	ItemSlotArgumentType.getItemSlot(commandContext, "slot"),
																	IdentifierArgumentType.getItemModifierArgument(commandContext, "modifier")
																)
														)
												)
										)
										.then(
											CommandManager.literal("copy")
												.then(
													CommandManager.literal("block")
														.then(
															CommandManager.argument("source", BlockPosArgumentType.blockPos())
																.then(
																	CommandManager.argument("sourceSlot", ItemSlotArgumentType.itemSlot())
																		.executes(
																			commandContext -> executeEntityCopyBlock(
																					commandContext.getSource(),
																					BlockPosArgumentType.getLoadedBlockPos(commandContext, "source"),
																					ItemSlotArgumentType.getItemSlot(commandContext, "sourceSlot"),
																					EntityArgumentType.getEntities(commandContext, "targets"),
																					ItemSlotArgumentType.getItemSlot(commandContext, "slot")
																				)
																		)
																		.then(
																			CommandManager.argument("modifier", IdentifierArgumentType.identifier())
																				.suggests(MODIFIER_SUGGESTION_PROVIDER)
																				.executes(
																					commandContext -> executeEntityCopyBlock(
																							commandContext.getSource(),
																							BlockPosArgumentType.getLoadedBlockPos(commandContext, "source"),
																							ItemSlotArgumentType.getItemSlot(commandContext, "sourceSlot"),
																							EntityArgumentType.getEntities(commandContext, "targets"),
																							ItemSlotArgumentType.getItemSlot(commandContext, "slot"),
																							IdentifierArgumentType.getItemModifierArgument(commandContext, "modifier")
																						)
																				)
																		)
																)
														)
												)
												.then(
													CommandManager.literal("entity")
														.then(
															CommandManager.argument("source", EntityArgumentType.entity())
																.then(
																	CommandManager.argument("sourceSlot", ItemSlotArgumentType.itemSlot())
																		.executes(
																			commandContext -> executeEntityCopyEntity(
																					commandContext.getSource(),
																					EntityArgumentType.getEntity(commandContext, "source"),
																					ItemSlotArgumentType.getItemSlot(commandContext, "sourceSlot"),
																					EntityArgumentType.getEntities(commandContext, "targets"),
																					ItemSlotArgumentType.getItemSlot(commandContext, "slot")
																				)
																		)
																		.then(
																			CommandManager.argument("modifier", IdentifierArgumentType.identifier())
																				.suggests(MODIFIER_SUGGESTION_PROVIDER)
																				.executes(
																					commandContext -> executeEntityCopyEntity(
																							commandContext.getSource(),
																							EntityArgumentType.getEntity(commandContext, "source"),
																							ItemSlotArgumentType.getItemSlot(commandContext, "sourceSlot"),
																							EntityArgumentType.getEntities(commandContext, "targets"),
																							ItemSlotArgumentType.getItemSlot(commandContext, "slot"),
																							IdentifierArgumentType.getItemModifierArgument(commandContext, "modifier")
																						)
																				)
																		)
																)
														)
												)
										)
								)
						)
				)
		);
	}

	private static int executeBlockModify(ServerCommandSource source, BlockPos pos, int slot, LootFunction modifier) throws CommandSyntaxException {
		Inventory inventory = method_32723(source, pos, NOT_A_CONTAINER_TARGET_EXCEPTION);
		if (slot >= 0 && slot < inventory.size()) {
			ItemStack itemStack = method_32715(source, modifier, inventory.getStack(slot));
			inventory.setStack(slot, itemStack);
			source.sendFeedback(new TranslatableText("commands.item.block.set.success", pos.getX(), pos.getY(), pos.getZ(), itemStack.toHoverableText()), true);
			return 1;
		} else {
			throw NO_SUCH_SLOT_TARGET_EXCEPTION.create(slot);
		}
	}

	private static int executeEntityModify(ServerCommandSource source, Collection<? extends Entity> targets, int slot, LootFunction modifier) throws CommandSyntaxException {
		Map<Entity, ItemStack> map = Maps.<Entity, ItemStack>newHashMapWithExpectedSize(targets.size());

		for (Entity entity : targets) {
			if (entity instanceof ServerPlayerEntity) {
				((ServerPlayerEntity)entity).playerScreenHandler.sendContentUpdates();
			}

			class_5630 lv = entity.method_32318(slot);
			if (lv != class_5630.field_27860) {
				ItemStack itemStack = method_32715(source, modifier, lv.method_32327().copy());
				if (lv.method_32332(itemStack)) {
					map.put(entity, itemStack);
					if (entity instanceof ServerPlayerEntity) {
						((ServerPlayerEntity)entity).playerScreenHandler.sendContentUpdates();
					}
				}
			}
		}

		if (map.isEmpty()) {
			throw NO_CHANGES_EXCEPTION.create(slot);
		} else {
			if (map.size() == 1) {
				Entry<Entity, ItemStack> entry = (Entry<Entity, ItemStack>)map.entrySet().iterator().next();
				source.sendFeedback(
					new TranslatableText("commands.item.entity.set.success.single", ((Entity)entry.getKey()).getDisplayName(), ((ItemStack)entry.getValue()).toHoverableText()),
					true
				);
			} else {
				source.sendFeedback(new TranslatableText("commands.item.entity.set.success.multiple", map.size()), true);
			}

			return map.size();
		}
	}

	private static int executeBlockReplace(ServerCommandSource source, BlockPos pos, int slot, ItemStack stack) throws CommandSyntaxException {
		Inventory inventory = method_32723(source, pos, NOT_A_CONTAINER_TARGET_EXCEPTION);
		if (slot >= 0 && slot < inventory.size()) {
			inventory.setStack(slot, stack);
			source.sendFeedback(new TranslatableText("commands.item.block.set.success", pos.getX(), pos.getY(), pos.getZ(), stack.toHoverableText()), true);
			return 1;
		} else {
			throw NO_SUCH_SLOT_TARGET_EXCEPTION.create(slot);
		}
	}

	private static Inventory method_32723(ServerCommandSource serverCommandSource, BlockPos blockPos, Dynamic3CommandExceptionType dynamic3CommandExceptionType) throws CommandSyntaxException {
		BlockEntity blockEntity = serverCommandSource.getWorld().getBlockEntity(blockPos);
		if (!(blockEntity instanceof Inventory)) {
			throw dynamic3CommandExceptionType.create(blockPos.getX(), blockPos.getY(), blockPos.getZ());
		} else {
			return (Inventory)blockEntity;
		}
	}

	private static int executeEntityReplace(ServerCommandSource source, Collection<? extends Entity> targets, int slot, ItemStack stack) throws CommandSyntaxException {
		List<Entity> list = Lists.<Entity>newArrayListWithCapacity(targets.size());

		for (Entity entity : targets) {
			if (entity instanceof ServerPlayerEntity) {
				((ServerPlayerEntity)entity).playerScreenHandler.sendContentUpdates();
			}

			class_5630 lv = entity.method_32318(slot);
			if (lv != class_5630.field_27860 && lv.method_32332(stack.copy())) {
				list.add(entity);
				if (entity instanceof ServerPlayerEntity) {
					((ServerPlayerEntity)entity).playerScreenHandler.sendContentUpdates();
				}
			}
		}

		if (list.isEmpty()) {
			throw KNOWN_ITEM_EXCEPTION.create(stack.toHoverableText(), slot);
		} else {
			if (list.size() == 1) {
				source.sendFeedback(
					new TranslatableText("commands.item.entity.set.success.single", ((Entity)list.iterator().next()).getDisplayName(), stack.toHoverableText()), true
				);
			} else {
				source.sendFeedback(new TranslatableText("commands.item.entity.set.success.multiple", list.size(), stack.toHoverableText()), true);
			}

			return list.size();
		}
	}

	private static int executeEntityCopyBlock(ServerCommandSource source, BlockPos sourcePos, int sourceSlot, Collection<? extends Entity> targets, int slot) throws CommandSyntaxException {
		return executeEntityReplace(source, targets, slot, method_32716(source, sourcePos, sourceSlot));
	}

	private static int executeEntityCopyBlock(
		ServerCommandSource source, BlockPos sourcePos, int sourceSlot, Collection<? extends Entity> targets, int slot, LootFunction modifier
	) throws CommandSyntaxException {
		return executeEntityReplace(source, targets, slot, method_32715(source, modifier, method_32716(source, sourcePos, sourceSlot)));
	}

	private static int executeBlockCopyBlock(ServerCommandSource source, BlockPos sourcePos, int sourceSlot, BlockPos pos, int slot) throws CommandSyntaxException {
		return executeBlockReplace(source, pos, slot, method_32716(source, sourcePos, sourceSlot));
	}

	private static int executeBlockCopyBlock(ServerCommandSource source, BlockPos sourcePos, int sourceSlot, BlockPos pos, int slot, LootFunction modifier) throws CommandSyntaxException {
		return executeBlockReplace(source, pos, slot, method_32715(source, modifier, method_32716(source, sourcePos, sourceSlot)));
	}

	private static int executeBlockCopyEntity(ServerCommandSource source, Entity sourceEntity, int sourceSlot, BlockPos pos, int slot) throws CommandSyntaxException {
		return executeBlockReplace(source, pos, slot, method_32706(sourceEntity, sourceSlot));
	}

	private static int executeBlockCopyEntity(ServerCommandSource source, Entity sourceEntity, int sourceSlot, BlockPos pos, int slot, LootFunction modifier) throws CommandSyntaxException {
		return executeBlockReplace(source, pos, slot, method_32715(source, modifier, method_32706(sourceEntity, sourceSlot)));
	}

	private static int executeEntityCopyEntity(ServerCommandSource source, Entity sourceEntity, int sourceSlot, Collection<? extends Entity> targets, int slot) throws CommandSyntaxException {
		return executeEntityReplace(source, targets, slot, method_32706(sourceEntity, sourceSlot));
	}

	private static int executeEntityCopyEntity(
		ServerCommandSource source, Entity sourceEntity, int sourceSlot, Collection<? extends Entity> targets, int slot, LootFunction modifier
	) throws CommandSyntaxException {
		return executeEntityReplace(source, targets, slot, method_32715(source, modifier, method_32706(sourceEntity, sourceSlot)));
	}

	private static ItemStack method_32715(ServerCommandSource serverCommandSource, LootFunction lootFunction, ItemStack itemStack) {
		ServerWorld serverWorld = serverCommandSource.getWorld();
		LootContext.Builder builder = new LootContext.Builder(serverWorld)
			.parameter(LootContextParameters.ORIGIN, serverCommandSource.getPosition())
			.optionalParameter(LootContextParameters.THIS_ENTITY, serverCommandSource.getEntity());
		return (ItemStack)lootFunction.apply(itemStack, builder.build(LootContextTypes.COMMAND));
	}

	private static ItemStack method_32706(Entity entity, int i) throws CommandSyntaxException {
		class_5630 lv = entity.method_32318(i);
		if (lv == class_5630.field_27860) {
			throw NO_SUCH_SLOT_SOURCE_EXCEPTION.create(i);
		} else {
			return lv.method_32327().copy();
		}
	}

	private static ItemStack method_32716(ServerCommandSource serverCommandSource, BlockPos blockPos, int i) throws CommandSyntaxException {
		Inventory inventory = method_32723(serverCommandSource, blockPos, NOT_A_CONTAINER_SOURCE_EXCEPTION);
		if (i >= 0 && i < inventory.size()) {
			return inventory.getStack(i).copy();
		} else {
			throw NO_SUCH_SLOT_SOURCE_EXCEPTION.create(i);
		}
	}
}
