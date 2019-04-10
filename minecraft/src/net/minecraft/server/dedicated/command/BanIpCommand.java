package net.minecraft.server.dedicated.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.arguments.MessageArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.config.BannedIpEntry;
import net.minecraft.server.config.BannedIpList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;

public class BanIpCommand {
	public static final Pattern field_13466 = Pattern.compile(
		"^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$"
	);
	private static final SimpleCommandExceptionType INVALID_IP_EXCEPTION = new SimpleCommandExceptionType(new TranslatableTextComponent("commands.banip.invalid"));
	private static final SimpleCommandExceptionType ALREADY_BANNED_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableTextComponent("commands.banip.failed")
	);

	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			CommandManager.literal("ban-ip")
				.requires(
					serverCommandSource -> serverCommandSource.getMinecraftServer().getPlayerManager().getIpBanList().isEnabled() && serverCommandSource.hasPermissionLevel(3)
				)
				.then(
					CommandManager.argument("target", StringArgumentType.word())
						.executes(commandContext -> checkIp(commandContext.getSource(), StringArgumentType.getString(commandContext, "target"), null))
						.then(
							CommandManager.argument("reason", MessageArgumentType.create())
								.executes(
									commandContext -> checkIp(
											commandContext.getSource(), StringArgumentType.getString(commandContext, "target"), MessageArgumentType.getMessage(commandContext, "reason")
										)
								)
						)
				)
		);
	}

	private static int checkIp(ServerCommandSource serverCommandSource, String string, @Nullable TextComponent textComponent) throws CommandSyntaxException {
		Matcher matcher = field_13466.matcher(string);
		if (matcher.matches()) {
			return banIp(serverCommandSource, string, textComponent);
		} else {
			ServerPlayerEntity serverPlayerEntity = serverCommandSource.getMinecraftServer().getPlayerManager().getPlayer(string);
			if (serverPlayerEntity != null) {
				return banIp(serverCommandSource, serverPlayerEntity.getServerBrand(), textComponent);
			} else {
				throw INVALID_IP_EXCEPTION.create();
			}
		}
	}

	private static int banIp(ServerCommandSource serverCommandSource, String string, @Nullable TextComponent textComponent) throws CommandSyntaxException {
		BannedIpList bannedIpList = serverCommandSource.getMinecraftServer().getPlayerManager().getIpBanList();
		if (bannedIpList.method_14529(string)) {
			throw ALREADY_BANNED_EXCEPTION.create();
		} else {
			List<ServerPlayerEntity> list = serverCommandSource.getMinecraftServer().getPlayerManager().getPlayersByIp(string);
			BannedIpEntry bannedIpEntry = new BannedIpEntry(string, null, serverCommandSource.getName(), null, textComponent == null ? null : textComponent.getString());
			bannedIpList.add(bannedIpEntry);
			serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.banip.success", string, bannedIpEntry.getReason()), true);
			if (!list.isEmpty()) {
				serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.banip.info", list.size(), EntitySelector.getNames(list)), true);
			}

			for (ServerPlayerEntity serverPlayerEntity : list) {
				serverPlayerEntity.networkHandler.disconnect(new TranslatableTextComponent("multiplayer.disconnect.ip_banned"));
			}

			return list.size();
		}
	}
}
