package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import java.util.Collection;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class KickCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("kick")
				.requires(source -> source.hasPermissionLevel(3))
				.then(
					CommandManager.argument("targets", EntityArgumentType.players())
						.executes(context -> execute(context.getSource(), EntityArgumentType.getPlayers(context, "targets"), Text.translatable("multiplayer.disconnect.kicked")))
						.then(
							CommandManager.argument("reason", MessageArgumentType.message())
								.executes(context -> execute(context.getSource(), EntityArgumentType.getPlayers(context, "targets"), MessageArgumentType.getMessage(context, "reason")))
						)
				)
		);
	}

	private static int execute(ServerCommandSource source, Collection<ServerPlayerEntity> targets, Text reason) {
		for (ServerPlayerEntity serverPlayerEntity : targets) {
			serverPlayerEntity.networkHandler.disconnect(reason);
			source.sendFeedback(() -> Text.translatable("commands.kick.success", serverPlayerEntity.getDisplayName(), reason), true);
		}

		return targets.size();
	}
}
