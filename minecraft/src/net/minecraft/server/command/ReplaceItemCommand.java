package net.minecraft.server.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.List;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.command.arguments.BlockPosArgumentType;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.command.arguments.ItemSlotArgumentType;
import net.minecraft.command.arguments.ItemStackArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;

public class ReplaceItemCommand {
	public static final SimpleCommandExceptionType BLOCK_FAILED_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableText("commands.replaceitem.block.failed")
	);
	public static final DynamicCommandExceptionType SLOT_INAPPLICABLE_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableText("commands.replaceitem.slot.inapplicable", object)
	);
	public static final Dynamic2CommandExceptionType ENTITY_FAILED_EXCEPTION = new Dynamic2CommandExceptionType(
		(object, object2) -> new TranslatableText("commands.replaceitem.entity.failed", object, object2)
	);

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("replaceitem")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.then(
					CommandManager.literal("block")
						.then(
							CommandManager.argument("pos", BlockPosArgumentType.blockPos())
								.then(
									CommandManager.argument("slot", ItemSlotArgumentType.itemSlot())
										.then(
											CommandManager.argument("item", ItemStackArgumentType.itemStack())
												.executes(
													commandContext -> executeBlock(
															commandContext.getSource(),
															BlockPosArgumentType.getLoadedBlockPos(commandContext, "pos"),
															ItemSlotArgumentType.getItemSlot(commandContext, "slot"),
															ItemStackArgumentType.getItemStackArgument(commandContext, "item").createStack(1, false)
														)
												)
												.then(
													CommandManager.argument("count", IntegerArgumentType.integer(1, 64))
														.executes(
															commandContext -> executeBlock(
																	commandContext.getSource(),
																	BlockPosArgumentType.getLoadedBlockPos(commandContext, "pos"),
																	ItemSlotArgumentType.getItemSlot(commandContext, "slot"),
																	ItemStackArgumentType.getItemStackArgument(commandContext, "item").createStack(IntegerArgumentType.getInteger(commandContext, "count"), true)
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
											CommandManager.argument("item", ItemStackArgumentType.itemStack())
												.executes(
													commandContext -> executeEntity(
															commandContext.getSource(),
															EntityArgumentType.getEntities(commandContext, "targets"),
															ItemSlotArgumentType.getItemSlot(commandContext, "slot"),
															ItemStackArgumentType.getItemStackArgument(commandContext, "item").createStack(1, false)
														)
												)
												.then(
													CommandManager.argument("count", IntegerArgumentType.integer(1, 64))
														.executes(
															commandContext -> executeEntity(
																	commandContext.getSource(),
																	EntityArgumentType.getEntities(commandContext, "targets"),
																	ItemSlotArgumentType.getItemSlot(commandContext, "slot"),
																	ItemStackArgumentType.getItemStackArgument(commandContext, "item").createStack(IntegerArgumentType.getInteger(commandContext, "count"), true)
																)
														)
												)
										)
								)
						)
				)
		);
	}

	private static int executeBlock(ServerCommandSource source, BlockPos pos, int slot, ItemStack item) throws CommandSyntaxException {
		BlockEntity blockEntity = source.getWorld().getBlockEntity(pos);
		if (!(blockEntity instanceof Inventory)) {
			throw BLOCK_FAILED_EXCEPTION.create();
		} else {
			Inventory inventory = (Inventory)blockEntity;
			if (slot >= 0 && slot < inventory.getInvSize()) {
				inventory.setInvStack(slot, item);
				source.sendFeedback(new TranslatableText("commands.replaceitem.block.success", pos.getX(), pos.getY(), pos.getZ(), item.toHoverableText()), true);
				return 1;
			} else {
				throw SLOT_INAPPLICABLE_EXCEPTION.create(slot);
			}
		}
	}

	private static int executeEntity(ServerCommandSource source, Collection<? extends Entity> targets, int slot, ItemStack item) throws CommandSyntaxException {
		List<Entity> list = Lists.<Entity>newArrayListWithCapacity(targets.size());

		for (Entity entity : targets) {
			if (entity instanceof ServerPlayerEntity) {
				((ServerPlayerEntity)entity).playerScreenHandler.sendContentUpdates();
			}

			if (entity.equip(slot, item.copy())) {
				list.add(entity);
				if (entity instanceof ServerPlayerEntity) {
					((ServerPlayerEntity)entity).playerScreenHandler.sendContentUpdates();
				}
			}
		}

		if (list.isEmpty()) {
			throw ENTITY_FAILED_EXCEPTION.create(item.toHoverableText(), slot);
		} else {
			if (list.size() == 1) {
				source.sendFeedback(
					new TranslatableText("commands.replaceitem.entity.success.single", ((Entity)list.iterator().next()).getDisplayName(), item.toHoverableText()), true
				);
			} else {
				source.sendFeedback(new TranslatableText("commands.replaceitem.entity.success.multiple", list.size(), item.toHoverableText()), true);
			}

			return list.size();
		}
	}
}
