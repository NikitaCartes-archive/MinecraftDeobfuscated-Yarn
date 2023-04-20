package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;

public class ReturnCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("return")
				.requires(source -> source.hasPermissionLevel(2))
				.then(
					CommandManager.argument("value", IntegerArgumentType.integer())
						.executes(context -> execute(context.getSource(), IntegerArgumentType.getInteger(context, "value")))
				)
		);
	}

	private static int execute(ServerCommandSource source, int value) {
		source.getReturnValueConsumer().accept(value);
		return value;
	}
}
