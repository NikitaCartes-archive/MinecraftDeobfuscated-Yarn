package net.minecraft.server.dedicated.command;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import net.minecraft.command.arguments.GameProfileArgumentType;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;

public class DeOpCommand {
	private static final SimpleCommandExceptionType ALREADY_DEOPPED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableComponent("commands.deop.failed"));

	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			CommandManager.literal("deop")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(3))
				.then(
					CommandManager.argument("targets", GameProfileArgumentType.create())
						.suggests(
							(commandContext, suggestionsBuilder) -> CommandSource.suggestMatching(
									commandContext.getSource().getMinecraftServer().getPlayerManager().getOpNames(), suggestionsBuilder
								)
						)
						.executes(commandContext -> deop(commandContext.getSource(), GameProfileArgumentType.getProfileArgument(commandContext, "targets")))
				)
		);
	}

	private static int deop(ServerCommandSource serverCommandSource, Collection<GameProfile> collection) throws CommandSyntaxException {
		PlayerManager playerManager = serverCommandSource.getMinecraftServer().getPlayerManager();
		int i = 0;

		for (GameProfile gameProfile : collection) {
			if (playerManager.isOperator(gameProfile)) {
				playerManager.removeFromOperators(gameProfile);
				i++;
				serverCommandSource.sendFeedback(new TranslatableComponent("commands.deop.success", ((GameProfile)collection.iterator().next()).getName()), true);
			}
		}

		if (i == 0) {
			throw ALREADY_DEOPPED_EXCEPTION.create();
		} else {
			serverCommandSource.getMinecraftServer().kickNonWhitelistedPlayers(serverCommandSource);
			return i;
		}
	}
}
