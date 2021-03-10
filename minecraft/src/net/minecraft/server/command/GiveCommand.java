package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.ItemStackArgument;
import net.minecraft.command.argument.ItemStackArgumentType;
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
		int i = item.getItem().getMaxCount();
		int j = i * 100;
		if (count > j) {
			String string = item.getItem().getTranslationKey();
			source.sendError(new TranslatableText("commands.give.failed.toomanyitems", j, string));
			return 0;
		} else {
			for (ServerPlayerEntity serverPlayerEntity : targets) {
				int k = count;

				while (k > 0) {
					int l = Math.min(i, k);
					k -= l;
					ItemStack itemStack = item.createStack(l, false);
					boolean bl = serverPlayerEntity.getInventory().insertStack(itemStack);
					if (bl && itemStack.isEmpty()) {
						itemStack.setCount(1);
						ItemEntity itemEntity = serverPlayerEntity.dropItem(itemStack, false);
						if (itemEntity != null) {
							itemEntity.setDespawnImmediately();
						}

						serverPlayerEntity.world
							.playSound(
								null,
								serverPlayerEntity.getX(),
								serverPlayerEntity.getY(),
								serverPlayerEntity.getZ(),
								SoundEvents.ENTITY_ITEM_PICKUP,
								SoundCategory.PLAYERS,
								0.2F,
								((serverPlayerEntity.getRandom().nextFloat() - serverPlayerEntity.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F
							);
						serverPlayerEntity.currentScreenHandler.sendContentUpdates();
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
}
