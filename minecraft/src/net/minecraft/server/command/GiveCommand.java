package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.command.arguments.ItemStackArgument;
import net.minecraft.command.arguments.ItemStackArgumentType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.TranslatableText;

public class GiveCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("give")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.then(
					CommandManager.argument("targets", EntityArgumentType.players())
						.then(
							CommandManager.argument("item", ItemStackArgumentType.itemStack())
								.executes(
									commandContext -> execute(
											commandContext.getSource(),
											ItemStackArgumentType.getItemStackArgument(commandContext, "item"),
											EntityArgumentType.getPlayers(commandContext, "targets"),
											1
										)
								)
								.then(
									CommandManager.argument("count", IntegerArgumentType.integer(1))
										.executes(
											commandContext -> execute(
													commandContext.getSource(),
													ItemStackArgumentType.getItemStackArgument(commandContext, "item"),
													EntityArgumentType.getPlayers(commandContext, "targets"),
													IntegerArgumentType.getInteger(commandContext, "count")
												)
										)
								)
						)
				)
		);
	}

	private static int execute(ServerCommandSource source, ItemStackArgument item, Collection<ServerPlayerEntity> targets, int count) throws CommandSyntaxException {
		for (ServerPlayerEntity serverPlayerEntity : targets) {
			int i = count;

			while (i > 0) {
				int j = Math.min(item.getItem().getMaxCount(), i);
				i -= j;
				ItemStack itemStack = item.createStack(j, false);
				boolean bl = serverPlayerEntity.inventory.insertStack(itemStack);
				if (bl && itemStack.isEmpty()) {
					itemStack.setCount(1);
					ItemEntity itemEntity = serverPlayerEntity.dropItem(itemStack, false);
					if (itemEntity != null) {
						itemEntity.setDespawnImmediately();
					}

					serverPlayerEntity.world
						.playSound(
							null,
							serverPlayerEntity.x,
							serverPlayerEntity.y,
							serverPlayerEntity.z,
							SoundEvents.ENTITY_ITEM_PICKUP,
							SoundCategory.PLAYERS,
							0.2F,
							((serverPlayerEntity.getRandom().nextFloat() - serverPlayerEntity.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F
						);
					serverPlayerEntity.playerContainer.sendContentUpdates();
				} else {
					ItemEntity itemEntity = serverPlayerEntity.dropItem(itemStack, false);
					if (itemEntity != null) {
						itemEntity.resetPickupDelay();
						itemEntity.setOwner(serverPlayerEntity.getUuid());
					}
				}
			}
		}

		if (targets.size() == 1) {
			source.sendFeedback(
				new TranslatableText(
					"commands.give.success.single", count, item.createStack(count, false).toHoverableText(), ((ServerPlayerEntity)targets.iterator().next()).getDisplayName()
				),
				true
			);
		} else {
			source.sendFeedback(new TranslatableText("commands.give.success.single", count, item.createStack(count, false).toHoverableText(), targets.size()), true);
		}

		return targets.size();
	}
}
