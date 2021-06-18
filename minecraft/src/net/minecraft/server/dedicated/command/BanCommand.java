package net.minecraft.server.dedicated.command;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.server.BannedPlayerEntry;
import net.minecraft.server.BannedPlayerList;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.text.TranslatableText;

public class BanCommand {
	private static final SimpleCommandExceptionType ALREADY_BANNED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.ban.failed"));

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("ban")
				.requires(source -> source.hasPermissionLevel(3))
				.then(
					CommandManager.argument("targets", GameProfileArgumentType.gameProfile())
						.executes(context -> ban(context.getSource(), GameProfileArgumentType.getProfileArgument(context, "targets"), null))
						.then(
							CommandManager.argument("reason", MessageArgumentType.message())
								.executes(
									context -> ban(context.getSource(), GameProfileArgumentType.getProfileArgument(context, "targets"), MessageArgumentType.getMessage(context, "reason"))
								)
						)
				)
		);
	}

	private static int ban(ServerCommandSource source, Collection<GameProfile> targets, @Nullable Text reason) throws CommandSyntaxException {
		BannedPlayerList bannedPlayerList = source.getServer().getPlayerManager().getUserBanList();
		int i = 0;

		for (GameProfile gameProfile : targets) {
			if (!bannedPlayerList.contains(gameProfile)) {
				BannedPlayerEntry bannedPlayerEntry = new BannedPlayerEntry(gameProfile, null, source.getName(), null, reason == null ? null : reason.getString());
				bannedPlayerList.add(bannedPlayerEntry);
				i++;
				source.sendFeedback(new TranslatableText("commands.ban.success", Texts.toText(gameProfile), bannedPlayerEntry.getReason()), true);
				ServerPlayerEntity serverPlayerEntity = source.getServer().getPlayerManager().getPlayer(gameProfile.getId());
				if (serverPlayerEntity != null) {
					serverPlayerEntity.networkHandler.disconnect(new TranslatableText("multiplayer.disconnect.banned"));
				}
			}
		}

		if (i == 0) {
			throw ALREADY_BANNED_EXCEPTION.create();
		} else {
			return i;
		}
	}
}
