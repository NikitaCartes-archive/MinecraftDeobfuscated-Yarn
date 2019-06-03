package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.command.arguments.IdentifierArgumentType;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.recipe.Recipe;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;

public class RecipeCommand {
	private static final SimpleCommandExceptionType GIVE_FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.recipe.give.failed"));
	private static final SimpleCommandExceptionType TAKE_FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.recipe.take.failed"));

	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			CommandManager.literal("recipe")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.then(
					CommandManager.literal("give")
						.then(
							CommandManager.argument("targets", EntityArgumentType.players())
								.then(
									CommandManager.argument("recipe", IdentifierArgumentType.create())
										.suggests(SuggestionProviders.ALL_RECIPES)
										.executes(
											commandContext -> executeGive(
													commandContext.getSource(),
													EntityArgumentType.getPlayers(commandContext, "targets"),
													Collections.singleton(IdentifierArgumentType.getRecipeArgument(commandContext, "recipe"))
												)
										)
								)
								.then(
									CommandManager.literal("*")
										.executes(
											commandContext -> executeGive(
													commandContext.getSource(),
													EntityArgumentType.getPlayers(commandContext, "targets"),
													commandContext.getSource().getMinecraftServer().getRecipeManager().values()
												)
										)
								)
						)
				)
				.then(
					CommandManager.literal("take")
						.then(
							CommandManager.argument("targets", EntityArgumentType.players())
								.then(
									CommandManager.argument("recipe", IdentifierArgumentType.create())
										.suggests(SuggestionProviders.ALL_RECIPES)
										.executes(
											commandContext -> executeTake(
													commandContext.getSource(),
													EntityArgumentType.getPlayers(commandContext, "targets"),
													Collections.singleton(IdentifierArgumentType.getRecipeArgument(commandContext, "recipe"))
												)
										)
								)
								.then(
									CommandManager.literal("*")
										.executes(
											commandContext -> executeTake(
													commandContext.getSource(),
													EntityArgumentType.getPlayers(commandContext, "targets"),
													commandContext.getSource().getMinecraftServer().getRecipeManager().values()
												)
										)
								)
						)
				)
		);
	}

	private static int executeGive(ServerCommandSource serverCommandSource, Collection<ServerPlayerEntity> collection, Collection<Recipe<?>> collection2) throws CommandSyntaxException {
		int i = 0;

		for (ServerPlayerEntity serverPlayerEntity : collection) {
			i += serverPlayerEntity.unlockRecipes(collection2);
		}

		if (i == 0) {
			throw GIVE_FAILED_EXCEPTION.create();
		} else {
			if (collection.size() == 1) {
				serverCommandSource.method_9226(
					new TranslatableText("commands.recipe.give.success.single", collection2.size(), ((ServerPlayerEntity)collection.iterator().next()).method_5476()), true
				);
			} else {
				serverCommandSource.method_9226(new TranslatableText("commands.recipe.give.success.multiple", collection2.size(), collection.size()), true);
			}

			return i;
		}
	}

	private static int executeTake(ServerCommandSource serverCommandSource, Collection<ServerPlayerEntity> collection, Collection<Recipe<?>> collection2) throws CommandSyntaxException {
		int i = 0;

		for (ServerPlayerEntity serverPlayerEntity : collection) {
			i += serverPlayerEntity.lockRecipes(collection2);
		}

		if (i == 0) {
			throw TAKE_FAILED_EXCEPTION.create();
		} else {
			if (collection.size() == 1) {
				serverCommandSource.method_9226(
					new TranslatableText("commands.recipe.take.success.single", collection2.size(), ((ServerPlayerEntity)collection.iterator().next()).method_5476()), true
				);
			} else {
				serverCommandSource.method_9226(new TranslatableText("commands.recipe.take.success.multiple", collection2.size(), collection.size()), true);
			}

			return i;
		}
	}
}
