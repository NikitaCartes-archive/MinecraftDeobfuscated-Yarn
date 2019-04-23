package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Collection;
import net.minecraft.command.arguments.FunctionArgumentType;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.server.function.CommandFunctionManager;

public class FunctionCommand {
	public static final SuggestionProvider<ServerCommandSource> SUGGESTION_PROVIDER = (commandContext, suggestionsBuilder) -> {
		CommandFunctionManager commandFunctionManager = commandContext.getSource().getMinecraftServer().getCommandFunctionManager();
		CommandSource.suggestIdentifiers(commandFunctionManager.getTags().getKeys(), suggestionsBuilder, "#");
		return CommandSource.suggestIdentifiers(commandFunctionManager.getFunctions().keySet(), suggestionsBuilder);
	};

	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			CommandManager.literal("function")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.then(
					CommandManager.argument("name", FunctionArgumentType.create())
						.suggests(SUGGESTION_PROVIDER)
						.executes(commandContext -> execute(commandContext.getSource(), FunctionArgumentType.getFunctions(commandContext, "name")))
				)
		);
	}

	private static int execute(ServerCommandSource serverCommandSource, Collection<CommandFunction> collection) {
		int i = 0;

		for (CommandFunction commandFunction : collection) {
			i += serverCommandSource.getMinecraftServer().getCommandFunctionManager().execute(commandFunction, serverCommandSource.withSilent().withMaxLevel(2));
		}

		if (collection.size() == 1) {
			serverCommandSource.sendFeedback(
				new TranslatableComponent("commands.function.success.single", i, ((CommandFunction)collection.iterator().next()).getId()), true
			);
		} else {
			serverCommandSource.sendFeedback(new TranslatableComponent("commands.function.success.multiple", i, collection.size()), true);
		}

		return i;
	}
}
