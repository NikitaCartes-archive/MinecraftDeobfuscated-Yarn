/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.command.arguments.IdentifierArgumentType;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.recipe.Recipe;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;

public class RecipeCommand {
    private static final SimpleCommandExceptionType GIVE_FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.recipe.give.failed", new Object[0]));
    private static final SimpleCommandExceptionType TAKE_FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.recipe.take.failed", new Object[0]));

    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("recipe").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).then(CommandManager.literal("give").then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)CommandManager.argument("targets", EntityArgumentType.players()).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("recipe", IdentifierArgumentType.create()).suggests(SuggestionProviders.ALL_RECIPES).executes(commandContext -> RecipeCommand.executeGive((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), Collections.singleton(IdentifierArgumentType.getRecipeArgument(commandContext, "recipe")))))).then(CommandManager.literal("*").executes(commandContext -> RecipeCommand.executeGive((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getRecipeManager().values())))))).then(CommandManager.literal("take").then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)CommandManager.argument("targets", EntityArgumentType.players()).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("recipe", IdentifierArgumentType.create()).suggests(SuggestionProviders.ALL_RECIPES).executes(commandContext -> RecipeCommand.executeTake((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), Collections.singleton(IdentifierArgumentType.getRecipeArgument(commandContext, "recipe")))))).then(CommandManager.literal("*").executes(commandContext -> RecipeCommand.executeTake((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getRecipeManager().values()))))));
    }

    private static int executeGive(ServerCommandSource serverCommandSource, Collection<ServerPlayerEntity> collection, Collection<Recipe<?>> collection2) throws CommandSyntaxException {
        int i = 0;
        for (ServerPlayerEntity serverPlayerEntity : collection) {
            i += serverPlayerEntity.unlockRecipes(collection2);
        }
        if (i == 0) {
            throw GIVE_FAILED_EXCEPTION.create();
        }
        if (collection.size() == 1) {
            serverCommandSource.sendFeedback(new TranslatableText("commands.recipe.give.success.single", collection2.size(), collection.iterator().next().getDisplayName()), true);
        } else {
            serverCommandSource.sendFeedback(new TranslatableText("commands.recipe.give.success.multiple", collection2.size(), collection.size()), true);
        }
        return i;
    }

    private static int executeTake(ServerCommandSource serverCommandSource, Collection<ServerPlayerEntity> collection, Collection<Recipe<?>> collection2) throws CommandSyntaxException {
        int i = 0;
        for (ServerPlayerEntity serverPlayerEntity : collection) {
            i += serverPlayerEntity.lockRecipes(collection2);
        }
        if (i == 0) {
            throw TAKE_FAILED_EXCEPTION.create();
        }
        if (collection.size() == 1) {
            serverCommandSource.sendFeedback(new TranslatableText("commands.recipe.take.success.single", collection2.size(), collection.iterator().next().getDisplayName()), true);
        } else {
            serverCommandSource.sendFeedback(new TranslatableText("commands.recipe.take.success.multiple", collection2.size(), collection.size()), true);
        }
        return i;
    }
}

