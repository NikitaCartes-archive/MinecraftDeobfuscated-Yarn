package net.minecraft.server.dedicated.command;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import java.util.Collection;
import net.minecraft.server.BanEntry;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class BanListCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("banlist")
				.requires(source -> source.hasPermissionLevel(3))
				.executes(context -> {
					PlayerManager playerManager = context.getSource().getServer().getPlayerManager();
					return execute(context.getSource(), Lists.newArrayList(Iterables.concat(playerManager.getUserBanList().values(), playerManager.getIpBanList().values())));
				})
				.then(
					CommandManager.literal("ips")
						.executes(context -> execute(context.getSource(), context.getSource().getServer().getPlayerManager().getIpBanList().values()))
				)
				.then(
					CommandManager.literal("players")
						.executes(context -> execute(context.getSource(), context.getSource().getServer().getPlayerManager().getUserBanList().values()))
				)
		);
	}

	private static int execute(ServerCommandSource source, Collection<? extends BanEntry<?>> targets) {
		if (targets.isEmpty()) {
			source.sendFeedback(() -> Text.translatable("commands.banlist.none"), false);
		} else {
			source.sendFeedback(() -> Text.translatable("commands.banlist.list", targets.size()), false);

			for (BanEntry<?> banEntry : targets) {
				source.sendFeedback(() -> Text.translatable("commands.banlist.entry", banEntry.toText(), banEntry.getSource(), banEntry.getReason()), false);
			}
		}

		return targets.size();
	}
}
