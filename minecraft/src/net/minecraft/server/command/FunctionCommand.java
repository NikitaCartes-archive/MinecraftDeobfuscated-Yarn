package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Collection;
import net.minecraft.command.arguments.FunctionArgumentType;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.server.function.CommandFunctionManager;
import net.minecraft.text.TranslatableTextComponent;

public class FunctionCommand {
	public static final SuggestionProvider<ServerCommandSource> SUGGESTION_PROVIDER = (commandContext, suggestionsBuilder) -> {
		CommandFunctionManager commandFunctionManager = commandContext.getSource().getMinecraftServer().getCommandFunctionManager();
		CommandSource.suggestIdentifiers(commandFunctionManager.getTags().getKeys(), suggestionsBuilder, "#");
		return CommandSource.suggestIdentifiers(commandFunctionManager.getFunctions().keySet(), suggestionsBuilder);
	};

	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			ServerCommandManager.literal("function")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.then(
					ServerCommandManager.argument("name", FunctionArgumentType.create())
						.suggests(SUGGESTION_PROVIDER)
						.executes(commandContext -> method_13381(commandContext.getSource(), FunctionArgumentType.method_9769(commandContext, "name")))
				)
		);
	}

	private static int method_13381(ServerCommandSource serverCommandSource, Collection<CommandFunction> collection) {
		int i = 0;

		for (CommandFunction commandFunction : collection) {
			i += serverCommandSource.getMinecraftServer().getCommandFunctionManager().execute(commandFunction, serverCommandSource.withSilent().withMaxLevel(2));
		}

		if (collection.size() == 1) {
			serverCommandSource.sendFeedback(
				new TranslatableTextComponent("commands.function.success.single", i, ((CommandFunction)collection.iterator().next()).getId()), true
			);
		} else {
			serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.function.success.multiple", i, collection.size()), true);
		}

		return i;
	}
}
