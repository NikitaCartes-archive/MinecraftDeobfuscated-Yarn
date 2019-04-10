package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Predicate;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.command.arguments.ItemPredicateArgumentType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableTextComponent;

public class ClearCommand {
	private static final DynamicCommandExceptionType FAILED_SINGLE_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableTextComponent("clear.failed.single", object)
	);
	private static final DynamicCommandExceptionType FAILED_MULTIPLE_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableTextComponent("clear.failed.multiple", object)
	);

	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			CommandManager.literal("clear")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.executes(commandContext -> execute(commandContext.getSource(), Collections.singleton(commandContext.getSource().getPlayer()), itemStack -> true, -1))
				.then(
					CommandManager.argument("targets", EntityArgumentType.players())
						.executes(commandContext -> execute(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), itemStack -> true, -1))
						.then(
							CommandManager.argument("item", ItemPredicateArgumentType.create())
								.executes(
									commandContext -> execute(
											commandContext.getSource(),
											EntityArgumentType.getPlayers(commandContext, "targets"),
											ItemPredicateArgumentType.getItemPredicate(commandContext, "item"),
											-1
										)
								)
								.then(
									CommandManager.argument("maxCount", IntegerArgumentType.integer(0))
										.executes(
											commandContext -> execute(
													commandContext.getSource(),
													EntityArgumentType.getPlayers(commandContext, "targets"),
													ItemPredicateArgumentType.getItemPredicate(commandContext, "item"),
													IntegerArgumentType.getInteger(commandContext, "maxCount")
												)
										)
								)
						)
				)
		);
	}

	private static int execute(ServerCommandSource serverCommandSource, Collection<ServerPlayerEntity> collection, Predicate<ItemStack> predicate, int i) throws CommandSyntaxException {
		int j = 0;

		for (ServerPlayerEntity serverPlayerEntity : collection) {
			j += serverPlayerEntity.inventory.method_7369(predicate, i);
			serverPlayerEntity.container.sendContentUpdates();
			serverPlayerEntity.method_14241();
		}

		if (j == 0) {
			if (collection.size() == 1) {
				throw FAILED_SINGLE_EXCEPTION.create(((ServerPlayerEntity)collection.iterator().next()).getName().getFormattedText());
			} else {
				throw FAILED_MULTIPLE_EXCEPTION.create(collection.size());
			}
		} else {
			if (i == 0) {
				if (collection.size() == 1) {
					serverCommandSource.sendFeedback(
						new TranslatableTextComponent("commands.clear.test.single", j, ((ServerPlayerEntity)collection.iterator().next()).getDisplayName()), true
					);
				} else {
					serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.clear.test.multiple", j, collection.size()), true);
				}
			} else if (collection.size() == 1) {
				serverCommandSource.sendFeedback(
					new TranslatableTextComponent("commands.clear.success.single", j, ((ServerPlayerEntity)collection.iterator().next()).getDisplayName()), true
				);
			} else {
				serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.clear.success.multiple", j, collection.size()), true);
			}

			return j;
		}
	}
}
