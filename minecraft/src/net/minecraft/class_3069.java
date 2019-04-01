package net.minecraft;

import com.google.common.collect.Iterables;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.ParsedCommandNode;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.CommandNode;
import java.util.Map;

public class class_3069 {
	private static final SimpleCommandExceptionType field_13665 = new SimpleCommandExceptionType(new class_2588("commands.help.failed"));

	public static void method_13405(CommandDispatcher<class_2168> commandDispatcher) {
		commandDispatcher.register(
			class_2170.method_9247("help")
				.executes(commandContext -> {
					Map<CommandNode<class_2168>, String> map = commandDispatcher.getSmartUsage(commandDispatcher.getRoot(), commandContext.getSource());

					for (String string : map.values()) {
						commandContext.getSource().method_9226(new class_2585("/" + string), false);
					}

					return map.size();
				})
				.then(
					class_2170.method_9244("command", StringArgumentType.greedyString())
						.executes(
							commandContext -> {
								ParseResults<class_2168> parseResults = commandDispatcher.parse(StringArgumentType.getString(commandContext, "command"), commandContext.getSource());
								if (parseResults.getContext().getNodes().isEmpty()) {
									throw field_13665.create();
								} else {
									Map<CommandNode<class_2168>, String> map = commandDispatcher.getSmartUsage(
										Iterables.<ParsedCommandNode<class_2168>>getLast(parseResults.getContext().getNodes()).getNode(), commandContext.getSource()
									);

									for (String string : map.values()) {
										commandContext.getSource().method_9226(new class_2585("/" + parseResults.getReader().getString() + " " + string), false);
									}

									return map.size();
								}
							}
						)
				)
		);
	}
}
