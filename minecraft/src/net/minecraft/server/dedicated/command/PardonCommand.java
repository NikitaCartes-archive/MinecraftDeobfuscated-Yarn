package net.minecraft.server.dedicated.command;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import net.minecraft.command.arguments.GameProfileArgumentType;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.config.BannedPlayerList;
import net.minecraft.text.TextFormatter;
import net.minecraft.text.TranslatableTextComponent;

public class PardonCommand {
	private static final SimpleCommandExceptionType field_13669 = new SimpleCommandExceptionType(new TranslatableTextComponent("commands.pardon.failed"));

	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			ServerCommandManager.literal("pardon")
				.requires(
					serverCommandSource -> serverCommandSource.getMinecraftServer().getPlayerManager().getIpBanList().isEnabled() && serverCommandSource.hasPermissionLevel(3)
				)
				.then(
					ServerCommandManager.argument("targets", GameProfileArgumentType.create())
						.suggests(
							(commandContext, suggestionsBuilder) -> CommandSource.suggestMatching(
									commandContext.getSource().getMinecraftServer().getPlayerManager().getUserBanList().getNames(), suggestionsBuilder
								)
						)
						.executes(commandContext -> method_13473(commandContext.getSource(), GameProfileArgumentType.getProfileArgument(commandContext, "targets")))
				)
		);
	}

	private static int method_13473(ServerCommandSource serverCommandSource, Collection<GameProfile> collection) throws CommandSyntaxException {
		BannedPlayerList bannedPlayerList = serverCommandSource.getMinecraftServer().getPlayerManager().getUserBanList();
		int i = 0;

		for (GameProfile gameProfile : collection) {
			if (bannedPlayerList.contains(gameProfile)) {
				bannedPlayerList.remove(gameProfile);
				i++;
				serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.pardon.success", TextFormatter.profile(gameProfile)), true);
			}
		}

		if (i == 0) {
			throw field_13669.create();
		} else {
			return i;
		}
	}
}
