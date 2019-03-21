package net.minecraft.server.dedicated.command;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import net.minecraft.command.arguments.GameProfileArgumentType;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableTextComponent;

public class OpCommand {
	private static final SimpleCommandExceptionType field_13667 = new SimpleCommandExceptionType(new TranslatableTextComponent("commands.op.failed"));

	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			ServerCommandManager.literal("op")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(3))
				.then(
					ServerCommandManager.argument("targets", GameProfileArgumentType.create())
						.suggests(
							(commandContext, suggestionsBuilder) -> {
								PlayerManager playerManager = commandContext.getSource().getMinecraftServer().getPlayerManager();
								return CommandSource.suggestMatching(
									playerManager.getPlayerList()
										.stream()
										.filter(serverPlayerEntity -> !playerManager.isOperator(serverPlayerEntity.getGameProfile()))
										.map(serverPlayerEntity -> serverPlayerEntity.getGameProfile().getName()),
									suggestionsBuilder
								);
							}
						)
						.executes(commandContext -> method_13465(commandContext.getSource(), GameProfileArgumentType.getProfileArgument(commandContext, "targets")))
				)
		);
	}

	private static int method_13465(ServerCommandSource serverCommandSource, Collection<GameProfile> collection) throws CommandSyntaxException {
		PlayerManager playerManager = serverCommandSource.getMinecraftServer().getPlayerManager();
		int i = 0;

		for (GameProfile gameProfile : collection) {
			if (!playerManager.isOperator(gameProfile)) {
				playerManager.addToOperators(gameProfile);
				i++;
				serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.op.success", ((GameProfile)collection.iterator().next()).getName()), true);
			}
		}

		if (i == 0) {
			throw field_13667.create();
		} else {
			return i;
		}
	}
}
