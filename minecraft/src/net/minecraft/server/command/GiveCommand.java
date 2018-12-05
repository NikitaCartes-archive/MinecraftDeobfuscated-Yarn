package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import net.minecraft.class_2290;
import net.minecraft.command.arguments.EntityArgumentType;
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
			ServerCommandManager.literal("give")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.then(
					ServerCommandManager.argument("targets", EntityArgumentType.method_9308())
						.then(
							ServerCommandManager.argument("item", ItemStackArgumentType.create())
								.executes(
									commandContext -> method_13401(
											commandContext.getSource(), ItemStackArgumentType.method_9777(commandContext, "item"), EntityArgumentType.method_9312(commandContext, "targets"), 1
										)
								)
								.then(
									ServerCommandManager.argument("count", IntegerArgumentType.integer(1))
										.executes(
											commandContext -> method_13401(
													commandContext.getSource(),
													ItemStackArgumentType.method_9777(commandContext, "item"),
													EntityArgumentType.method_9312(commandContext, "targets"),
													IntegerArgumentType.getInteger(commandContext, "count")
												)
										)
								)
						)
				)
		);
	}

	private static int method_13401(ServerCommandSource serverCommandSource, class_2290 arg, Collection<ServerPlayerEntity> collection, int i) throws CommandSyntaxException {
		for (ServerPlayerEntity serverPlayerEntity : collection) {
			int j = i;

			while (j > 0) {
				int k = Math.min(arg.method_9785().getMaxAmount(), j);
				j -= k;
				ItemStack itemStack = arg.method_9781(k, false);
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
					serverPlayerEntity.containerPlayer.sendContentUpdates();
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
					"commands.give.success.single", i, arg.method_9781(i, false).toTextComponent(), ((ServerPlayerEntity)collection.iterator().next()).getDisplayName()
				),
				true
			);
		} else {
			serverCommandSource.sendFeedback(
				new TranslatableTextComponent("commands.give.success.single", i, arg.method_9781(i, false).toTextComponent(), collection.size()), true
			);
		}

		return collection.size();
	}
}
