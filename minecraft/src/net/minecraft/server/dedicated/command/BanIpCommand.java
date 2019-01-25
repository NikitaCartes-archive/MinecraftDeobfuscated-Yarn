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
import net.minecraft.server.command.ServerCommandManager;
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
	private static final SimpleCommandExceptionType field_13468 = new SimpleCommandExceptionType(new TranslatableTextComponent("commands.banip.invalid"));
	private static final SimpleCommandExceptionType field_13467 = new SimpleCommandExceptionType(new TranslatableTextComponent("commands.banip.failed"));

	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			ServerCommandManager.literal("ban-ip")
				.requires(
					serverCommandSource -> serverCommandSource.getMinecraftServer().getPlayerManager().getIpBanList().isEnabled() && serverCommandSource.hasPermissionLevel(3)
				)
				.then(
					ServerCommandManager.argument("target", StringArgumentType.word())
						.executes(commandContext -> method_13009(commandContext.getSource(), StringArgumentType.getString(commandContext, "target"), null))
						.then(
							ServerCommandManager.argument("reason", MessageArgumentType.create())
								.executes(
									commandContext -> method_13009(
											commandContext.getSource(), StringArgumentType.getString(commandContext, "target"), MessageArgumentType.getMessageArgument(commandContext, "reason")
										)
								)
						)
				)
		);
	}

	private static int method_13009(ServerCommandSource serverCommandSource, String string, @Nullable TextComponent textComponent) throws CommandSyntaxException {
		Matcher matcher = field_13466.matcher(string);
		if (matcher.matches()) {
			return method_13007(serverCommandSource, string, textComponent);
		} else {
			ServerPlayerEntity serverPlayerEntity = serverCommandSource.getMinecraftServer().getPlayerManager().getPlayer(string);
			if (serverPlayerEntity != null) {
				return method_13007(serverCommandSource, serverPlayerEntity.getServerBrand(), textComponent);
			} else {
				throw field_13468.create();
			}
		}
	}

	private static int method_13007(ServerCommandSource serverCommandSource, String string, @Nullable TextComponent textComponent) throws CommandSyntaxException {
		BannedIpList bannedIpList = serverCommandSource.getMinecraftServer().getPlayerManager().getIpBanList();
		if (bannedIpList.method_14529(string)) {
			throw field_13467.create();
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
