package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Collection;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.CommandFunctionArgumentType;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.server.function.CommandFunctionManager;
import net.minecraft.text.TranslatableText;

public class FunctionCommand {
	public static final SuggestionProvider<ServerCommandSource> SUGGESTION_PROVIDER = (commandContext, suggestionsBuilder) -> {
		CommandFunctionManager commandFunctionManager = commandContext.getSource().getMinecraftServer().getCommandFunctionManager();
		CommandSource.suggestIdentifiers(commandFunctionManager.getFunctionTags(), suggestionsBuilder, "#");
		return CommandSource.suggestIdentifiers(commandFunctionManager.getAllFunctions(), suggestionsBuilder);
	};

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("function")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.then(
					CommandManager.argument("name", CommandFunctionArgumentType.commandFunction())
						.suggests(SUGGESTION_PROVIDER)
						.executes(commandContext -> execute(commandContext.getSource(), CommandFunctionArgumentType.getFunctions(commandContext, "name")))
				)
		);
	}

	private static int execute(ServerCommandSource source, Collection<CommandFunction> functions) {
		int i = 0;

		for (CommandFunction commandFunction : functions) {
			i += source.getMinecraftServer().getCommandFunctionManager().execute(commandFunction, source.withSilent().withMaxLevel(2));
		}

		if (functions.size() == 1) {
			source.sendFeedback(new TranslatableText("commands.function.success.single", i, ((CommandFunction)functions.iterator().next()).getId()), true);
		} else {
			source.sendFeedback(new TranslatableText("commands.function.success.multiple", i, functions.size()), true);
		}

		return i;
	}
}
