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
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.math.BlockPos;

public class ReplaceItemCommand {
	public static final SimpleCommandExceptionType BLOCK_FAILED_EXCEPTON = new SimpleCommandExceptionType(
		new TranslatableTextComponent("commands.replaceitem.block.failed")
	);
	public static final DynamicCommandExceptionType SLOT_INAPPLICABLE_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableTextComponent("commands.replaceitem.slot.inapplicable", object)
	);
	public static final Dynamic2CommandExceptionType ENTITY_FAILED_EXCEPTION = new Dynamic2CommandExceptionType(
		(object, object2) -> new TranslatableTextComponent("commands.replaceitem.entity.failed", object, object2)
	);

	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			ServerCommandManager.literal("replaceitem")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.then(
					ServerCommandManager.literal("block")
						.then(
							ServerCommandManager.argument("pos", BlockPosArgumentType.create())
								.then(
									ServerCommandManager.argument("slot", ItemSlotArgumentType.create())
										.then(
											ServerCommandManager.argument("item", ItemStackArgumentType.create())
												.executes(
													commandContext -> method_13539(
															commandContext.getSource(),
															BlockPosArgumentType.getValidPosArgument(commandContext, "pos"),
															ItemSlotArgumentType.getSlotArgument(commandContext, "slot"),
															ItemStackArgumentType.method_9777(commandContext, "item").method_9781(1, false)
														)
												)
												.then(
													ServerCommandManager.argument("count", IntegerArgumentType.integer(1, 64))
														.executes(
															commandContext -> method_13539(
																	commandContext.getSource(),
																	BlockPosArgumentType.getValidPosArgument(commandContext, "pos"),
																	ItemSlotArgumentType.getSlotArgument(commandContext, "slot"),
																	ItemStackArgumentType.method_9777(commandContext, "item").method_9781(IntegerArgumentType.getInteger(commandContext, "count"), true)
																)
														)
												)
										)
								)
						)
				)
				.then(
					ServerCommandManager.literal("entity")
						.then(
							ServerCommandManager.argument("targets", EntityArgumentType.multipleEntities())
								.then(
									ServerCommandManager.argument("slot", ItemSlotArgumentType.create())
										.then(
											ServerCommandManager.argument("item", ItemStackArgumentType.create())
												.executes(
													commandContext -> method_13537(
															commandContext.getSource(),
															EntityArgumentType.method_9317(commandContext, "targets"),
															ItemSlotArgumentType.getSlotArgument(commandContext, "slot"),
															ItemStackArgumentType.method_9777(commandContext, "item").method_9781(1, false)
														)
												)
												.then(
													ServerCommandManager.argument("count", IntegerArgumentType.integer(1, 64))
														.executes(
															commandContext -> method_13537(
																	commandContext.getSource(),
																	EntityArgumentType.method_9317(commandContext, "targets"),
																	ItemSlotArgumentType.getSlotArgument(commandContext, "slot"),
																	ItemStackArgumentType.method_9777(commandContext, "item").method_9781(IntegerArgumentType.getInteger(commandContext, "count"), true)
																)
														)
												)
										)
								)
						)
				)
		);
	}

	private static int method_13539(ServerCommandSource serverCommandSource, BlockPos blockPos, int i, ItemStack itemStack) throws CommandSyntaxException {
		BlockEntity blockEntity = serverCommandSource.getWorld().getBlockEntity(blockPos);
		if (!(blockEntity instanceof Inventory)) {
			throw BLOCK_FAILED_EXCEPTON.create();
		} else {
			Inventory inventory = (Inventory)blockEntity;
			if (i >= 0 && i < inventory.getInvSize()) {
				inventory.setInvStack(i, itemStack);
				serverCommandSource.sendFeedback(
					new TranslatableTextComponent("commands.replaceitem.block.success", blockPos.getX(), blockPos.getY(), blockPos.getZ(), itemStack.toTextComponent()), true
				);
				return 1;
			} else {
				throw SLOT_INAPPLICABLE_EXCEPTION.create(i);
			}
		}
	}

	private static int method_13537(ServerCommandSource serverCommandSource, Collection<? extends Entity> collection, int i, ItemStack itemStack) throws CommandSyntaxException {
		List<Entity> list = Lists.<Entity>newArrayListWithCapacity(collection.size());

		for (Entity entity : collection) {
			if (entity instanceof ServerPlayerEntity) {
				((ServerPlayerEntity)entity).playerContainer.sendContentUpdates();
			}

			if (entity.method_5758(i, itemStack.copy())) {
				list.add(entity);
				if (entity instanceof ServerPlayerEntity) {
					((ServerPlayerEntity)entity).playerContainer.sendContentUpdates();
				}
			}
		}

		if (list.isEmpty()) {
			throw ENTITY_FAILED_EXCEPTION.create(itemStack.toTextComponent(), i);
		} else {
			if (list.size() == 1) {
				serverCommandSource.sendFeedback(
					new TranslatableTextComponent("commands.replaceitem.entity.success.single", ((Entity)list.iterator().next()).getDisplayName(), itemStack.toTextComponent()),
					true
				);
			} else {
				serverCommandSource.sendFeedback(
					new TranslatableTextComponent("commands.replaceitem.entity.success.multiple", list.size(), itemStack.toTextComponent()), true
				);
			}

			return list.size();
		}
	}
}
