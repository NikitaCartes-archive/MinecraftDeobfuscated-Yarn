/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Collection;
import net.minecraft.command.arguments.FunctionArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.server.function.CommandFunctionManager;
import net.minecraft.text.TranslatableText;

public class FunctionCommand {
    public static final SuggestionProvider<ServerCommandSource> SUGGESTION_PROVIDER = (commandContext, suggestionsBuilder) -> {
        CommandFunctionManager commandFunctionManager = ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getCommandFunctionManager();
        CommandSource.suggestIdentifiers(commandFunctionManager.getTags().getKeys(), suggestionsBuilder, "#");
        return CommandSource.suggestIdentifiers(commandFunctionManager.getFunctions().keySet(), suggestionsBuilder);
    };

    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("function").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).then(CommandManager.argument("name", FunctionArgumentType.function()).suggests(SUGGESTION_PROVIDER).executes(commandContext -> FunctionCommand.execute((ServerCommandSource)commandContext.getSource(), FunctionArgumentType.getFunctions(commandContext, "name")))));
    }

    private static int execute(ServerCommandSource serverCommandSource, Collection<CommandFunction> collection) {
        int i = 0;
        for (CommandFunction commandFunction : collection) {
            i += serverCommandSource.getMinecraftServer().getCommandFunctionManager().execute(commandFunction, serverCommandSource.withSilent().withMaxLevel(2));
        }
        if (collection.size() == 1) {
            serverCommandSource.sendFeedback(new TranslatableText("commands.function.success.single", i, collection.iterator().next().getId()), true);
        } else {
            serverCommandSource.sendFeedback(new TranslatableText("commands.function.success.multiple", i, collection.size()), true);
        }
        return i;
    }
}

