package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;

public class ReturnCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("return")
				.requires(source -> source.hasPermissionLevel(2))
				.then(
					CommandManager.argument("value", IntegerArgumentType.integer())
						.executes(context -> execute(context.getSource(), IntegerArgumentType.getInteger(context, "value")))
				)
				.then(CommandManager.literal("run").redirect(dispatcher.getRoot(), context -> context.getSource().withConsumer(ReturnCommand::executeRun)))
		);
	}

	private static int execute(ServerCommandSource source, int value) {
		source.getReturnValueConsumer().accept(value);
		return value;
	}

	private static int executeRun(CommandContext<ServerCommandSource> context, boolean success, int value) {
		int i = success ? value : 0;
		execute(context.getSource(), i);
		return i;
	}
}
