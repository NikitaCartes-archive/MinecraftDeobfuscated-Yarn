package net.minecraft.server.dedicated.command;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import java.util.Collection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.ServerCommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.config.BanEntry;
import net.minecraft.text.TranslatableTextComponent;

public class BanListCommand {
	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			ServerCommandManager.literal("banlist")
				.requires(
					serverCommandSource -> (
								serverCommandSource.getMinecraftServer().method_3760().method_14563().isEnabled()
									|| serverCommandSource.getMinecraftServer().method_3760().getIpBanList().isEnabled()
							)
							&& serverCommandSource.hasPermissionLevel(3)
				)
				.executes(
					commandContext -> {
						PlayerManager playerManager = commandContext.getSource().getMinecraftServer().method_3760();
						return method_13015(
							commandContext.getSource(), Lists.newArrayList(Iterables.concat(playerManager.method_14563().values(), playerManager.getIpBanList().values()))
						);
					}
				)
				.then(
					ServerCommandManager.literal("ips")
						.executes(
							commandContext -> method_13015(commandContext.getSource(), commandContext.getSource().getMinecraftServer().method_3760().getIpBanList().values())
						)
				)
				.then(
					ServerCommandManager.literal("players")
						.executes(
							commandContext -> method_13015(commandContext.getSource(), commandContext.getSource().getMinecraftServer().method_3760().method_14563().values())
						)
				)
		);
	}

	private static int method_13015(ServerCommandSource serverCommandSource, Collection<? extends BanEntry<?>> collection) {
		if (collection.isEmpty()) {
			serverCommandSource.method_9226(new TranslatableTextComponent("commands.banlist.none"), false);
		} else {
			serverCommandSource.method_9226(new TranslatableTextComponent("commands.banlist.list", collection.size()), false);

			for (BanEntry<?> banEntry : collection) {
				serverCommandSource.method_9226(
					new TranslatableTextComponent("commands.banlist.entry", banEntry.asTextComponent(), banEntry.getSource(), banEntry.getReason()), false
				);
			}
		}

		return collection.size();
	}
}
