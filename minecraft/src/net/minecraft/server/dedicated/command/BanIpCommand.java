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
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.server.BannedIpEntry;
import net.minecraft.server.BannedIpList;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class BanIpCommand {
	public static final Pattern PATTERN = Pattern.compile(
		"^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$"
	);
	private static final SimpleCommandExceptionType INVALID_IP_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.banip.invalid"));
	private static final SimpleCommandExceptionType ALREADY_BANNED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.banip.failed"));

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("ban-ip")
				.requires(source -> source.hasPermissionLevel(3))
				.then(
					CommandManager.argument("target", StringArgumentType.word())
						.executes(context -> checkIp(context.getSource(), StringArgumentType.getString(context, "target"), null))
						.then(
							CommandManager.argument("reason", MessageArgumentType.message())
								.executes(context -> checkIp(context.getSource(), StringArgumentType.getString(context, "target"), MessageArgumentType.getMessage(context, "reason")))
						)
				)
		);
	}

	private static int checkIp(ServerCommandSource source, String target, @Nullable Text reason) throws CommandSyntaxException {
		Matcher matcher = PATTERN.matcher(target);
		if (matcher.matches()) {
			return banIp(source, target, reason);
		} else {
			ServerPlayerEntity serverPlayerEntity = source.getServer().getPlayerManager().getPlayer(target);
			if (serverPlayerEntity != null) {
				return banIp(source, serverPlayerEntity.getIp(), reason);
			} else {
				throw INVALID_IP_EXCEPTION.create();
			}
		}
	}

	private static int banIp(ServerCommandSource source, String targetIp, @Nullable Text reason) throws CommandSyntaxException {
		BannedIpList bannedIpList = source.getServer().getPlayerManager().getIpBanList();
		if (bannedIpList.isBanned(targetIp)) {
			throw ALREADY_BANNED_EXCEPTION.create();
		} else {
			List<ServerPlayerEntity> list = source.getServer().getPlayerManager().getPlayersByIp(targetIp);
			BannedIpEntry bannedIpEntry = new BannedIpEntry(targetIp, null, source.getName(), null, reason == null ? null : reason.getString());
			bannedIpList.add(bannedIpEntry);
			source.sendFeedback(new TranslatableText("commands.banip.success", targetIp, bannedIpEntry.getReason()), true);
			if (!list.isEmpty()) {
				source.sendFeedback(new TranslatableText("commands.banip.info", list.size(), EntitySelector.getNames(list)), true);
			}

			for (ServerPlayerEntity serverPlayerEntity : list) {
				serverPlayerEntity.networkHandler.disconnect(new TranslatableText("multiplayer.disconnect.ip_banned"));
			}

			return list.size();
		}
	}
}
