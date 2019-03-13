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

public class DeOpCommand {
	private static final SimpleCommandExceptionType field_13507 = new SimpleCommandExceptionType(new TranslatableTextComponent("commands.deop.failed"));

	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			ServerCommandManager.literal("deop")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(3))
				.then(
					ServerCommandManager.argument("targets", GameProfileArgumentType.create())
						.suggests(
							(commandContext, suggestionsBuilder) -> CommandSource.suggestMatching(
									commandContext.getSource().getMinecraftServer().method_3760().getOpNames(), suggestionsBuilder
								)
						)
						.executes(commandContext -> method_13144(commandContext.getSource(), GameProfileArgumentType.getProfilesArgument(commandContext, "targets")))
				)
		);
	}

	private static int method_13144(ServerCommandSource serverCommandSource, Collection<GameProfile> collection) throws CommandSyntaxException {
		PlayerManager playerManager = serverCommandSource.getMinecraftServer().method_3760();
		int i = 0;

		for (GameProfile gameProfile : collection) {
			if (playerManager.isOperator(gameProfile)) {
				playerManager.removeFromOperators(gameProfile);
				i++;
				serverCommandSource.method_9226(new TranslatableTextComponent("commands.deop.success", ((GameProfile)collection.iterator().next()).getName()), true);
			}
		}

		if (i == 0) {
			throw field_13507.create();
		} else {
			serverCommandSource.getMinecraftServer().method_3728(serverCommandSource);
			return i;
		}
	}
}
