package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Predicate;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.ItemPredicateArgumentType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;

public class ClearCommand {
	private static final DynamicCommandExceptionType FAILED_SINGLE_EXCEPTION = new DynamicCommandExceptionType(
		playerName -> new TranslatableText("clear.failed.single", playerName)
	);
	private static final DynamicCommandExceptionType FAILED_MULTIPLE_EXCEPTION = new DynamicCommandExceptionType(
		playerCount -> new TranslatableText("clear.failed.multiple", playerCount)
	);

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("clear")
				.requires(source -> source.hasPermissionLevel(2))
				.executes(context -> execute(context.getSource(), Collections.singleton(context.getSource().getPlayer()), stack -> true, -1))
				.then(
					CommandManager.argument("targets", EntityArgumentType.players())
						.executes(context -> execute(context.getSource(), EntityArgumentType.getPlayers(context, "targets"), stack -> true, -1))
						.then(
							CommandManager.argument("item", ItemPredicateArgumentType.itemPredicate())
								.executes(
									context -> execute(
											context.getSource(), EntityArgumentType.getPlayers(context, "targets"), ItemPredicateArgumentType.getItemPredicate(context, "item"), -1
										)
								)
								.then(
									CommandManager.argument("maxCount", IntegerArgumentType.integer(0))
										.executes(
											context -> execute(
													context.getSource(),
													EntityArgumentType.getPlayers(context, "targets"),
													ItemPredicateArgumentType.getItemPredicate(context, "item"),
													IntegerArgumentType.getInteger(context, "maxCount")
												)
										)
								)
						)
				)
		);
	}

	private static int execute(ServerCommandSource source, Collection<ServerPlayerEntity> targets, Predicate<ItemStack> item, int maxCount) throws CommandSyntaxException {
		int i = 0;

		for (ServerPlayerEntity serverPlayerEntity : targets) {
			i += serverPlayerEntity.getInventory().remove(item, maxCount, serverPlayerEntity.playerScreenHandler.getCraftingInput());
			serverPlayerEntity.currentScreenHandler.sendContentUpdates();
			serverPlayerEntity.playerScreenHandler.onContentChanged(serverPlayerEntity.getInventory());
		}

		if (i == 0) {
			if (targets.size() == 1) {
				throw FAILED_SINGLE_EXCEPTION.create(((ServerPlayerEntity)targets.iterator().next()).getName());
			} else {
				throw FAILED_MULTIPLE_EXCEPTION.create(targets.size());
			}
		} else {
			if (maxCount == 0) {
				if (targets.size() == 1) {
					source.sendFeedback(new TranslatableText("commands.clear.test.single", i, ((ServerPlayerEntity)targets.iterator().next()).getDisplayName()), true);
				} else {
					source.sendFeedback(new TranslatableText("commands.clear.test.multiple", i, targets.size()), true);
				}
			} else if (targets.size() == 1) {
				source.sendFeedback(new TranslatableText("commands.clear.success.single", i, ((ServerPlayerEntity)targets.iterator().next()).getDisplayName()), true);
			} else {
				source.sendFeedback(new TranslatableText("commands.clear.success.multiple", i, targets.size()), true);
			}

			return i;
		}
	}
}
