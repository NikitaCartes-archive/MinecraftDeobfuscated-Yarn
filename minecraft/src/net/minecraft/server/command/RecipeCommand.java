package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.RegistryKeyArgumentType;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class RecipeCommand {
	private static final SimpleCommandExceptionType GIVE_FAILED_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.recipe.give.failed"));
	private static final SimpleCommandExceptionType TAKE_FAILED_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.recipe.take.failed"));

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("recipe")
				.requires(source -> source.hasPermissionLevel(2))
				.then(
					CommandManager.literal("give")
						.then(
							CommandManager.argument("targets", EntityArgumentType.players())
								.then(
									CommandManager.argument("recipe", RegistryKeyArgumentType.registryKey(RegistryKeys.RECIPE))
										.executes(
											context -> executeGive(
													context.getSource(),
													EntityArgumentType.getPlayers(context, "targets"),
													Collections.singleton(RegistryKeyArgumentType.getRecipeEntry(context, "recipe"))
												)
										)
								)
								.then(
									CommandManager.literal("*")
										.executes(
											context -> executeGive(
													context.getSource(), EntityArgumentType.getPlayers(context, "targets"), context.getSource().getServer().getRecipeManager().values()
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
									CommandManager.argument("recipe", RegistryKeyArgumentType.registryKey(RegistryKeys.RECIPE))
										.executes(
											context -> executeTake(
													context.getSource(),
													EntityArgumentType.getPlayers(context, "targets"),
													Collections.singleton(RegistryKeyArgumentType.getRecipeEntry(context, "recipe"))
												)
										)
								)
								.then(
									CommandManager.literal("*")
										.executes(
											context -> executeTake(
													context.getSource(), EntityArgumentType.getPlayers(context, "targets"), context.getSource().getServer().getRecipeManager().values()
												)
										)
								)
						)
				)
		);
	}

	private static int executeGive(ServerCommandSource source, Collection<ServerPlayerEntity> targets, Collection<RecipeEntry<?>> recipes) throws CommandSyntaxException {
		int i = 0;

		for (ServerPlayerEntity serverPlayerEntity : targets) {
			i += serverPlayerEntity.unlockRecipes(recipes);
		}

		if (i == 0) {
			throw GIVE_FAILED_EXCEPTION.create();
		} else {
			if (targets.size() == 1) {
				source.sendFeedback(
					() -> Text.translatable("commands.recipe.give.success.single", recipes.size(), ((ServerPlayerEntity)targets.iterator().next()).getDisplayName()), true
				);
			} else {
				source.sendFeedback(() -> Text.translatable("commands.recipe.give.success.multiple", recipes.size(), targets.size()), true);
			}

			return i;
		}
	}

	private static int executeTake(ServerCommandSource source, Collection<ServerPlayerEntity> targets, Collection<RecipeEntry<?>> recipes) throws CommandSyntaxException {
		int i = 0;

		for (ServerPlayerEntity serverPlayerEntity : targets) {
			i += serverPlayerEntity.lockRecipes(recipes);
		}

		if (i == 0) {
			throw TAKE_FAILED_EXCEPTION.create();
		} else {
			if (targets.size() == 1) {
				source.sendFeedback(
					() -> Text.translatable("commands.recipe.take.success.single", recipes.size(), ((ServerPlayerEntity)targets.iterator().next()).getDisplayName()), true
				);
			} else {
				source.sendFeedback(() -> Text.translatable("commands.recipe.take.success.multiple", recipes.size(), targets.size()), true);
			}

			return i;
		}
	}
}
