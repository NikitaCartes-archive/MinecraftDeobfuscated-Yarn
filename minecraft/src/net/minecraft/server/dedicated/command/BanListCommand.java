package net.minecraft.server.dedicated.command;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import java.util.Collection;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.BanEntry;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class BanListCommand {
	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			CommandManager.literal("banlist")
				.requires(
					serverCommandSource -> (
								serverCommandSource.getMinecraftServer().getPlayerManager().getUserBanList().isEnabled()
									|| serverCommandSource.getMinecraftServer().getPlayerManager().getIpBanList().isEnabled()
							)
							&& serverCommandSource.hasPermissionLevel(3)
				)
				.executes(
					commandContext -> {
						PlayerManager playerManager = commandContext.getSource().getMinecraftServer().getPlayerManager();
						return execute(
							commandContext.getSource(), Lists.newArrayList(Iterables.concat(playerManager.getUserBanList().values(), playerManager.getIpBanList().values()))
						);
					}
				)
				.then(
					CommandManager.literal("ips")
						.executes(
							commandContext -> execute(commandContext.getSource(), commandContext.getSource().getMinecraftServer().getPlayerManager().getIpBanList().values())
						)
				)
				.then(
					CommandManager.literal("players")
						.executes(
							commandContext -> execute(commandContext.getSource(), commandContext.getSource().getMinecraftServer().getPlayerManager().getUserBanList().values())
						)
				)
		);
	}

	private static int execute(ServerCommandSource serverCommandSource, Collection<? extends BanEntry<?>> collection) {
		if (collection.isEmpty()) {
			serverCommandSource.sendFeedback(new TranslatableComponent("commands.banlist.none"), false);
		} else {
			serverCommandSource.sendFeedback(new TranslatableComponent("commands.banlist.list", collection.size()), false);

			for (BanEntry<?> banEntry : collection) {
				serverCommandSource.sendFeedback(
					new TranslatableComponent("commands.banlist.entry", banEntry.asTextComponent(), banEntry.getSource(), banEntry.getReason()), false
				);
			}
		}

		return collection.size();
	}
}
