package net.minecraft.server.command;

import com.google.common.collect.Iterables;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.ParsedCommandNode;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.CommandNode;
import java.util.Map;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class HelpCommand {
	private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableComponent("commands.help.failed"));

	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			CommandManager.literal("help")
				.executes(commandContext -> {
					Map<CommandNode<ServerCommandSource>, String> map = commandDispatcher.getSmartUsage(commandDispatcher.getRoot(), commandContext.getSource());

					for (String string : map.values()) {
						commandContext.getSource().sendFeedback(new TextComponent("/" + string), false);
					}

					return map.size();
				})
				.then(
					CommandManager.argument("command", StringArgumentType.greedyString())
						.executes(
							commandContext -> {
								ParseResults<ServerCommandSource> parseResults = commandDispatcher.parse(
									StringArgumentType.getString(commandContext, "command"), commandContext.getSource()
								);
								if (parseResults.getContext().getNodes().isEmpty()) {
									throw FAILED_EXCEPTION.create();
								} else {
									Map<CommandNode<ServerCommandSource>, String> map = commandDispatcher.getSmartUsage(
										Iterables.<ParsedCommandNode<ServerCommandSource>>getLast(parseResults.getContext().getNodes()).getNode(), commandContext.getSource()
									);

									for (String string : map.values()) {
										commandContext.getSource().sendFeedback(new TextComponent("/" + parseResults.getReader().getString() + " " + string), false);
									}

									return map.size();
								}
							}
						)
				)
		);
	}
}
