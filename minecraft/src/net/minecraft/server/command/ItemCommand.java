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
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;

public class ItemCommand {
	static final Dynamic3CommandExceptionType NOT_A_CONTAINER_TARGET_EXCEPTION = new Dynamic3CommandExceptionType(
		(x, y, z) -> new TranslatableText("commands.item.target.not_a_container", x, y, z)
	);
	private static final Dynamic3CommandExceptionType NOT_A_CONTAINER_SOURCE_EXCEPTION = new Dynamic3CommandExceptionType(
		(x, y, z) -> new TranslatableText("commands.item.source.not_a_container", x, y, z)
	);
	static final DynamicCommandExceptionType NO_SUCH_SLOT_TARGET_EXCEPTION = new DynamicCommandExceptionType(
		slot -> new TranslatableText("commands.item.target.no_such_slot", slot)
	);
	private static final DynamicCommandExceptionType NO_SUCH_SLOT_SOURCE_EXCEPTION = new DynamicCommandExceptionType(
		slot -> new TranslatableText("commands.item.source.no_such_slot", slot)
	);
	private static final DynamicCommandExceptionType NO_CHANGES_EXCEPTION = new DynamicCommandExceptionType(
		slot -> new TranslatableText("commands.item.target.no_changes", slot)
	);
	private static final Dynamic2CommandExceptionType KNOWN_ITEM_EXCEPTION = new Dynamic2CommandExceptionType(
		(itemName, slot) -> new TranslatableText("commands.item.target.no_changed.known_item", itemName, slot)
	);
	private static final SuggestionProvider<ServerCommandSource> MODIFIER_SUGGESTION_PROVIDER = (context, builder) -> {
		LootFunctionManager lootFunctionManager = context.getSource().getMinecraftServer().getItemModifierManager();
		return CommandSource.suggestIdentifiers(lootFunctionManager.getFunctionIds(), builder);
	};

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("item")
				.requires(source -> source.hasPermissionLevel(2))
				.then(
					CommandManager.literal("replace")
						.then(
							CommandManager.literal("block")
								.then(
									CommandManager.argument("pos", BlockPosArgumentType.blockPos())
										.then(
											CommandManager.argument("slot", ItemSlotArgumentType.itemSlot())
												.then(
													CommandManager.literal("with")
														.then(
															CommandManager.argument("item", ItemStackArgumentType.itemStack())
																.executes(
																	context -> executeBlockReplace(
																			context.getSource(),
																			BlockPosArgumentType.getLoadedBlockPos(context, "pos"),
																			ItemSlotArgumentType.getItemSlot(context, "slot"),
																			ItemStackArgumentType.getItemStackArgument(context, "item").createStack(1, false)
																		)
																)
																.then(
																	CommandManager.argument("count", IntegerArgumentType.integer(1, 64))
																		.executes(
																			context -> executeBlockReplace(
																					context.getSource(),
																					BlockPosArgumentType.getLoadedBlockPos(context, "pos"),
																					ItemSlotArgumentType.getItemSlot(context, "slot"),
																					ItemStackArgumentType.getItemStackArgument(context, "item").createStack(IntegerArgumentType.getInteger(context, "count"), true)
																				)
																		)
																)
														)
												)
												.then(
													CommandManager.literal("from")
														.then(
															CommandManager.literal("block")
																.then(
																	CommandManager.argument("source", BlockPosArgumentType.blockPos())
																		.then(
																			CommandManager.argument("sourceSlot", ItemSlotArgumentType.itemSlot())
																				.executes(
																					context -> executeBlockCopyBlock(
																							context.getSource(),
																							BlockPosArgumentType.getLoadedBlockPos(context, "source"),
																							ItemSlotArgumentType.getItemSlot(context, "sourceSlot"),
																							BlockPosArgumentType.getLoadedBlockPos(context, "pos"),
																							ItemSlotArgumentType.getItemSlot(context, "slot")
																						)
																				)
																				.then(
																					CommandManager.argument("modifier", IdentifierArgumentType.identifier())
																						.suggests(MODIFIER_SUGGESTION_PROVIDER)
																						.executes(
																							context -> executeBlockCopyBlock(
																									context.getSource(),
																									BlockPosArgumentType.getLoadedBlockPos(context, "source"),
																									ItemSlotArgumentType.getItemSlot(context, "sourceSlot"),
																									BlockPosArgumentType.getLoadedBlockPos(context, "pos"),
																									ItemSlotArgumentType.getItemSlot(context, "slot"),
																									IdentifierArgumentType.getItemModifierArgument(context, "modifier")
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
																					context -> executeBlockCopyEntity(
																							context.getSource(),
																							EntityArgumentType.getEntity(context, "source"),
																							ItemSlotArgumentType.getItemSlot(context, "sourceSlot"),
																							BlockPosArgumentType.getLoadedBlockPos(context, "pos"),
																							ItemSlotArgumentType.getItemSlot(context, "slot")
																						)
																				)
																				.then(
																					CommandManager.argument("modifier", IdentifierArgumentType.identifier())
																						.suggests(MODIFIER_SUGGESTION_PROVIDER)
																						.executes(
																							context -> executeBlockCopyEntity(
																									context.getSource(),
																									EntityArgumentType.getEntity(context, "source"),
																									ItemSlotArgumentType.getItemSlot(context, "sourceSlot"),
																									BlockPosArgumentType.getLoadedBlockPos(context, "pos"),
																									ItemSlotArgumentType.getItemSlot(context, "slot"),
																									IdentifierArgumentType.getItemModifierArgument(context, "modifier")
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
													CommandManager.literal("with")
														.then(
															CommandManager.argument("item", ItemStackArgumentType.itemStack())
																.executes(
																	context -> executeEntityReplace(
																			context.getSource(),
																			EntityArgumentType.getEntities(context, "targets"),
																			ItemSlotArgumentType.getItemSlot(context, "slot"),
																			ItemStackArgumentType.getItemStackArgument(context, "item").createStack(1, false)
																		)
																)
																.then(
																	CommandManager.argument("count", IntegerArgumentType.integer(1, 64))
																		.executes(
																			context -> executeEntityReplace(
																					context.getSource(),
																					EntityArgumentType.getEntities(context, "targets"),
																					ItemSlotArgumentType.getItemSlot(context, "slot"),
																					ItemStackArgumentType.getItemStackArgument(context, "item").createStack(IntegerArgumentType.getInteger(context, "count"), true)
																				)
																		)
																)
														)
												)
												.then(
													CommandManager.literal("from")
														.then(
															CommandManager.literal("block")
																.then(
																	CommandManager.argument("source", BlockPosArgumentType.blockPos())
																		.then(
																			CommandManager.argument("sourceSlot", ItemSlotArgumentType.itemSlot())
																				.executes(
																					context -> executeEntityCopyBlock(
																							context.getSource(),
																							BlockPosArgumentType.getLoadedBlockPos(context, "source"),
																							ItemSlotArgumentType.getItemSlot(context, "sourceSlot"),
																							EntityArgumentType.getEntities(context, "targets"),
																							ItemSlotArgumentType.getItemSlot(context, "slot")
																						)
																				)
																				.then(
																					CommandManager.argument("modifier", IdentifierArgumentType.identifier())
																						.suggests(MODIFIER_SUGGESTION_PROVIDER)
																						.executes(
																							context -> executeEntityCopyBlock(
																									context.getSource(),
																									BlockPosArgumentType.getLoadedBlockPos(context, "source"),
																									ItemSlotArgumentType.getItemSlot(context, "sourceSlot"),
																									EntityArgumentType.getEntities(context, "targets"),
																									ItemSlotArgumentType.getItemSlot(context, "slot"),
																									IdentifierArgumentType.getItemModifierArgument(context, "modifier")
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
																					context -> executeEntityCopyEntity(
																							context.getSource(),
																							EntityArgumentType.getEntity(context, "source"),
																							ItemSlotArgumentType.getItemSlot(context, "sourceSlot"),
																							EntityArgumentType.getEntities(context, "targets"),
																							ItemSlotArgumentType.getItemSlot(context, "slot")
																						)
																				)
																				.then(
																					CommandManager.argument("modifier", IdentifierArgumentType.identifier())
																						.suggests(MODIFIER_SUGGESTION_PROVIDER)
																						.executes(
																							context -> executeEntityCopyEntity(
																									context.getSource(),
																									EntityArgumentType.getEntity(context, "source"),
																									ItemSlotArgumentType.getItemSlot(context, "sourceSlot"),
																									EntityArgumentType.getEntities(context, "targets"),
																									ItemSlotArgumentType.getItemSlot(context, "slot"),
																									IdentifierArgumentType.getItemModifierArgument(context, "modifier")
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
				)
				.then(
					CommandManager.literal("modify")
						.then(
							CommandManager.literal("block")
								.then(
									CommandManager.argument("pos", BlockPosArgumentType.blockPos())
										.then(
											CommandManager.argument("slot", ItemSlotArgumentType.itemSlot())
												.then(
													CommandManager.argument("modifier", IdentifierArgumentType.identifier())
														.suggests(MODIFIER_SUGGESTION_PROVIDER)
														.executes(
															context -> executeBlockModify(
																	context.getSource(),
																	BlockPosArgumentType.getLoadedBlockPos(context, "pos"),
																	ItemSlotArgumentType.getItemSlot(context, "slot"),
																	IdentifierArgumentType.getItemModifierArgument(context, "modifier")
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
													CommandManager.argument("modifier", IdentifierArgumentType.identifier())
														.suggests(MODIFIER_SUGGESTION_PROVIDER)
														.executes(
															context -> executeEntityModify(
																	context.getSource(),
																	EntityArgumentType.getEntities(context, "targets"),
																	ItemSlotArgumentType.getItemSlot(context, "slot"),
																	IdentifierArgumentType.getItemModifierArgument(context, "modifier")
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
		Inventory inventory = getInventoryAtPos(source, pos, NOT_A_CONTAINER_TARGET_EXCEPTION);
		if (slot >= 0 && slot < inventory.size()) {
			ItemStack itemStack = getStackWithModifier(source, modifier, inventory.getStack(slot));
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
			CommandItemSlot commandItemSlot = entity.getCommandItemSlot(slot);
			if (commandItemSlot != CommandItemSlot.EMPTY) {
				ItemStack itemStack = getStackWithModifier(source, modifier, commandItemSlot.get().copy());
				if (commandItemSlot.set(itemStack)) {
					map.put(entity, itemStack);
					if (entity instanceof ServerPlayerEntity) {
						((ServerPlayerEntity)entity).currentScreenHandler.sendContentUpdates();
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
		Inventory inventory = getInventoryAtPos(source, pos, NOT_A_CONTAINER_TARGET_EXCEPTION);
		if (slot >= 0 && slot < inventory.size()) {
			inventory.setStack(slot, stack);
			source.sendFeedback(new TranslatableText("commands.item.block.set.success", pos.getX(), pos.getY(), pos.getZ(), stack.toHoverableText()), true);
			return 1;
		} else {
			throw NO_SUCH_SLOT_TARGET_EXCEPTION.create(slot);
		}
	}

	private static Inventory getInventoryAtPos(ServerCommandSource source, BlockPos pos, Dynamic3CommandExceptionType exception) throws CommandSyntaxException {
		BlockEntity blockEntity = source.getWorld().getBlockEntity(pos);
		if (!(blockEntity instanceof Inventory)) {
			throw exception.create(pos.getX(), pos.getY(), pos.getZ());
		} else {
			return (Inventory)blockEntity;
		}
	}

	private static int executeEntityReplace(ServerCommandSource source, Collection<? extends Entity> targets, int slot, ItemStack stack) throws CommandSyntaxException {
		List<Entity> list = Lists.<Entity>newArrayListWithCapacity(targets.size());

		for (Entity entity : targets) {
			CommandItemSlot commandItemSlot = entity.getCommandItemSlot(slot);
			if (commandItemSlot != CommandItemSlot.EMPTY && commandItemSlot.set(stack.copy())) {
				list.add(entity);
				if (entity instanceof ServerPlayerEntity) {
					((ServerPlayerEntity)entity).currentScreenHandler.sendContentUpdates();
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
		return executeEntityReplace(source, targets, slot, getStackInSlotFromInventoryAt(source, sourcePos, sourceSlot));
	}

	private static int executeEntityCopyBlock(
		ServerCommandSource source, BlockPos sourcePos, int sourceSlot, Collection<? extends Entity> targets, int slot, LootFunction modifier
	) throws CommandSyntaxException {
		return executeEntityReplace(source, targets, slot, getStackWithModifier(source, modifier, getStackInSlotFromInventoryAt(source, sourcePos, sourceSlot)));
	}

	private static int executeBlockCopyBlock(ServerCommandSource source, BlockPos sourcePos, int sourceSlot, BlockPos pos, int slot) throws CommandSyntaxException {
		return executeBlockReplace(source, pos, slot, getStackInSlotFromInventoryAt(source, sourcePos, sourceSlot));
	}

	private static int executeBlockCopyBlock(ServerCommandSource source, BlockPos sourcePos, int sourceSlot, BlockPos pos, int slot, LootFunction modifier) throws CommandSyntaxException {
		return executeBlockReplace(source, pos, slot, getStackWithModifier(source, modifier, getStackInSlotFromInventoryAt(source, sourcePos, sourceSlot)));
	}

	private static int executeBlockCopyEntity(ServerCommandSource source, Entity sourceEntity, int sourceSlot, BlockPos pos, int slot) throws CommandSyntaxException {
		return executeBlockReplace(source, pos, slot, getStackInSlot(sourceEntity, sourceSlot));
	}

	private static int executeBlockCopyEntity(ServerCommandSource source, Entity sourceEntity, int sourceSlot, BlockPos pos, int slot, LootFunction modifier) throws CommandSyntaxException {
		return executeBlockReplace(source, pos, slot, getStackWithModifier(source, modifier, getStackInSlot(sourceEntity, sourceSlot)));
	}

	private static int executeEntityCopyEntity(ServerCommandSource source, Entity sourceEntity, int sourceSlot, Collection<? extends Entity> targets, int slot) throws CommandSyntaxException {
		return executeEntityReplace(source, targets, slot, getStackInSlot(sourceEntity, sourceSlot));
	}

	private static int executeEntityCopyEntity(
		ServerCommandSource source, Entity sourceEntity, int sourceSlot, Collection<? extends Entity> targets, int slot, LootFunction modifier
	) throws CommandSyntaxException {
		return executeEntityReplace(source, targets, slot, getStackWithModifier(source, modifier, getStackInSlot(sourceEntity, sourceSlot)));
	}

	private static ItemStack getStackWithModifier(ServerCommandSource source, LootFunction modifier, ItemStack stack) {
		ServerWorld serverWorld = source.getWorld();
		LootContext.Builder builder = new LootContext.Builder(serverWorld)
			.parameter(LootContextParameters.ORIGIN, source.getPosition())
			.optionalParameter(LootContextParameters.THIS_ENTITY, source.getEntity());
		return (ItemStack)modifier.apply(stack, builder.build(LootContextTypes.COMMAND));
	}

	private static ItemStack getStackInSlot(Entity entity, int slotId) throws CommandSyntaxException {
		CommandItemSlot commandItemSlot = entity.getCommandItemSlot(slotId);
		if (commandItemSlot == CommandItemSlot.EMPTY) {
			throw NO_SUCH_SLOT_SOURCE_EXCEPTION.create(slotId);
		} else {
			return commandItemSlot.get().copy();
		}
	}

	private static ItemStack getStackInSlotFromInventoryAt(ServerCommandSource source, BlockPos pos, int slotId) throws CommandSyntaxException {
		Inventory inventory = getInventoryAtPos(source, pos, NOT_A_CONTAINER_SOURCE_EXCEPTION);
		if (slotId >= 0 && slotId < inventory.size()) {
			return inventory.getStack(slotId).copy();
		} else {
			throw NO_SUCH_SLOT_SOURCE_EXCEPTION.create(slotId);
		}
	}
}
