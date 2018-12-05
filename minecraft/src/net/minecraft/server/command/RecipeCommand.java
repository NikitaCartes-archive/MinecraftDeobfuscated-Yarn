package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.command.arguments.ResourceLocationArgumentType;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.recipe.Recipe;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableTextComponent;

public class RecipeCommand {
	private static final SimpleCommandExceptionType GIVE_FAILED_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableTextComponent("commands.recipe.give.failed")
	);
	private static final SimpleCommandExceptionType TAKE_FAILED_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableTextComponent("commands.recipe.take.failed")
	);

	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			ServerCommandManager.literal("recipe")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.then(
					ServerCommandManager.literal("give")
						.then(
							ServerCommandManager.argument("targets", EntityArgumentType.method_9308())
								.then(
									ServerCommandManager.argument("recipe", ResourceLocationArgumentType.create())
										.suggests(SuggestionProviders.ALL_RECIPES)
										.executes(
											commandContext -> method_13520(
													commandContext.getSource(),
													EntityArgumentType.method_9312(commandContext, "targets"),
													Collections.singleton(ResourceLocationArgumentType.getRecipeArgument(commandContext, "recipe"))
												)
										)
								)
								.then(
									ServerCommandManager.literal("*")
										.executes(
											commandContext -> method_13520(
													commandContext.getSource(),
													EntityArgumentType.method_9312(commandContext, "targets"),
													commandContext.getSource().getMinecraftServer().getRecipeManager().values()
												)
										)
								)
						)
				)
				.then(
					ServerCommandManager.literal("take")
						.then(
							ServerCommandManager.argument("targets", EntityArgumentType.method_9308())
								.then(
									ServerCommandManager.argument("recipe", ResourceLocationArgumentType.create())
										.suggests(SuggestionProviders.ALL_RECIPES)
										.executes(
											commandContext -> method_13518(
													commandContext.getSource(),
													EntityArgumentType.method_9312(commandContext, "targets"),
													Collections.singleton(ResourceLocationArgumentType.getRecipeArgument(commandContext, "recipe"))
												)
										)
								)
								.then(
									ServerCommandManager.literal("*")
										.executes(
											commandContext -> method_13518(
													commandContext.getSource(),
													EntityArgumentType.method_9312(commandContext, "targets"),
													commandContext.getSource().getMinecraftServer().getRecipeManager().values()
												)
										)
								)
						)
				)
		);
	}

	private static int method_13520(ServerCommandSource serverCommandSource, Collection<ServerPlayerEntity> collection, Collection<Recipe> collection2) throws CommandSyntaxException {
		int i = 0;

		for (ServerPlayerEntity serverPlayerEntity : collection) {
			i += serverPlayerEntity.method_7254(collection2);
		}

		if (i == 0) {
			throw GIVE_FAILED_EXCEPTION.create();
		} else {
			if (collection.size() == 1) {
				serverCommandSource.sendFeedback(
					new TranslatableTextComponent(
						"commands.recipe.give.success.single", collection2.size(), ((ServerPlayerEntity)collection.iterator().next()).getDisplayName()
					),
					true
				);
			} else {
				serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.recipe.give.success.multiple", collection2.size(), collection.size()), true);
			}

			return i;
		}
	}

	private static int method_13518(ServerCommandSource serverCommandSource, Collection<ServerPlayerEntity> collection, Collection<Recipe> collection2) throws CommandSyntaxException {
		int i = 0;

		for (ServerPlayerEntity serverPlayerEntity : collection) {
			i += serverPlayerEntity.method_7333(collection2);
		}

		if (i == 0) {
			throw TAKE_FAILED_EXCEPTION.create();
		} else {
			if (collection.size() == 1) {
				serverCommandSource.sendFeedback(
					new TranslatableTextComponent(
						"commands.recipe.take.success.single", collection2.size(), ((ServerPlayerEntity)collection.iterator().next()).getDisplayName()
					),
					true
				);
			} else {
				serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.recipe.take.success.multiple", collection2.size(), collection.size()), true);
			}

			return i;
		}
	}
}
