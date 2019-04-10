package net.minecraft.server.dedicated.command;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.command.arguments.GameProfileArgumentType;
import net.minecraft.command.arguments.MessageArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.config.BannedPlayerEntry;
import net.minecraft.server.config.BannedPlayerList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormatter;
import net.minecraft.text.TranslatableTextComponent;

public class BanCommand {
	private static final SimpleCommandExceptionType ALREADY_BANNED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableTextComponent("commands.ban.failed"));

	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			CommandManager.literal("ban")
				.requires(
					serverCommandSource -> serverCommandSource.getMinecraftServer().getPlayerManager().getUserBanList().isEnabled()
							&& serverCommandSource.hasPermissionLevel(3)
				)
				.then(
					CommandManager.argument("targets", GameProfileArgumentType.create())
						.executes(commandContext -> ban(commandContext.getSource(), GameProfileArgumentType.getProfileArgument(commandContext, "targets"), null))
						.then(
							CommandManager.argument("reason", MessageArgumentType.create())
								.executes(
									commandContext -> ban(
											commandContext.getSource(),
											GameProfileArgumentType.getProfileArgument(commandContext, "targets"),
											MessageArgumentType.getMessage(commandContext, "reason")
										)
								)
						)
				)
		);
	}

	private static int ban(ServerCommandSource serverCommandSource, Collection<GameProfile> collection, @Nullable TextComponent textComponent) throws CommandSyntaxException {
		BannedPlayerList bannedPlayerList = serverCommandSource.getMinecraftServer().getPlayerManager().getUserBanList();
		int i = 0;

		for (GameProfile gameProfile : collection) {
			if (!bannedPlayerList.contains(gameProfile)) {
				BannedPlayerEntry bannedPlayerEntry = new BannedPlayerEntry(
					gameProfile, null, serverCommandSource.getName(), null, textComponent == null ? null : textComponent.getString()
				);
				bannedPlayerList.add(bannedPlayerEntry);
				i++;
				serverCommandSource.sendFeedback(
					new TranslatableTextComponent("commands.ban.success", TextFormatter.profile(gameProfile), bannedPlayerEntry.getReason()), true
				);
				ServerPlayerEntity serverPlayerEntity = serverCommandSource.getMinecraftServer().getPlayerManager().getPlayer(gameProfile.getId());
				if (serverPlayerEntity != null) {
					serverPlayerEntity.networkHandler.disconnect(new TranslatableTextComponent("multiplayer.disconnect.banned"));
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
