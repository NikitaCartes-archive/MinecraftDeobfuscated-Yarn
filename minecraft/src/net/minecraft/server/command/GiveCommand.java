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
import net.minecraft.text.TranslatableTextComponent;

public class GiveCommand {
	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			CommandManager.literal("give")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.then(
					CommandManager.argument("targets", EntityArgumentType.players())
						.then(
							CommandManager.argument("item", ItemStackArgumentType.create())
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

	private static int execute(ServerCommandSource serverCommandSource, ItemStackArgument itemStackArgument, Collection<ServerPlayerEntity> collection, int i) throws CommandSyntaxException {
		for (ServerPlayerEntity serverPlayerEntity : collection) {
			int j = i;

			while (j > 0) {
				int k = Math.min(itemStackArgument.getItem().getMaxAmount(), j);
				j -= k;
				ItemStack itemStack = itemStackArgument.method_9781(k, false);
				boolean bl = serverPlayerEntity.inventory.insertStack(itemStack);
				if (bl && itemStack.isEmpty()) {
					itemStack.setAmount(1);
					ItemEntity itemEntity = serverPlayerEntity.dropItem(itemStack, false);
					if (itemEntity != null) {
						itemEntity.method_6987();
					}

					serverPlayerEntity.world
						.playSound(
							null,
							serverPlayerEntity.x,
							serverPlayerEntity.y,
							serverPlayerEntity.z,
							SoundEvents.field_15197,
							SoundCategory.field_15248,
							0.2F,
							((serverPlayerEntity.getRand().nextFloat() - serverPlayerEntity.getRand().nextFloat()) * 0.7F + 1.0F) * 2.0F
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

		if (collection.size() == 1) {
			serverCommandSource.sendFeedback(
				new TranslatableTextComponent(
					"commands.give.success.single",
					i,
					itemStackArgument.method_9781(i, false).toTextComponent(),
					((ServerPlayerEntity)collection.iterator().next()).getDisplayName()
				),
				true
			);
		} else {
			serverCommandSource.sendFeedback(
				new TranslatableTextComponent("commands.give.success.single", i, itemStackArgument.method_9781(i, false).toTextComponent(), collection.size()), true
			);
		}

		return collection.size();
	}
}
