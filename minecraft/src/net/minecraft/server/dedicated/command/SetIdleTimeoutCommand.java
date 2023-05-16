package net.minecraft.server.dedicated.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class SetIdleTimeoutCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("setidletimeout")
				.requires(source -> source.hasPermissionLevel(3))
				.then(
					CommandManager.argument("minutes", IntegerArgumentType.integer(0))
						.executes(context -> execute(context.getSource(), IntegerArgumentType.getInteger(context, "minutes")))
				)
		);
	}

	private static int execute(ServerCommandSource source, int minutes) {
		source.getServer().setPlayerIdleTimeout(minutes);
		source.sendFeedback(() -> Text.translatable("commands.setidletimeout.success", minutes), true);
		return minutes;
	}
}
