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
import net.minecraft.server.config.WhitelistEntry;
import net.minecraft.server.config.WhitelistList;
import net.minecraft.text.TextFormatter;
import net.minecraft.text.TranslatableTextComponent;

public class WhitelistCommand {
	private static final SimpleCommandExceptionType field_13767 = new SimpleCommandExceptionType(new TranslatableTextComponent("commands.whitelist.alreadyOn"));
	private static final SimpleCommandExceptionType field_13770 = new SimpleCommandExceptionType(new TranslatableTextComponent("commands.whitelist.alreadyOff"));
	private static final SimpleCommandExceptionType field_13768 = new SimpleCommandExceptionType(new TranslatableTextComponent("commands.whitelist.add.failed"));
	private static final SimpleCommandExceptionType field_13769 = new SimpleCommandExceptionType(new TranslatableTextComponent("commands.whitelist.remove.failed"));

	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			ServerCommandManager.literal("whitelist")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(3))
				.then(ServerCommandManager.literal("on").executes(commandContext -> method_13839(commandContext.getSource())))
				.then(ServerCommandManager.literal("off").executes(commandContext -> method_13837(commandContext.getSource())))
				.then(ServerCommandManager.literal("list").executes(commandContext -> method_13840(commandContext.getSource())))
				.then(
					ServerCommandManager.literal("add")
						.then(
							ServerCommandManager.argument("targets", GameProfileArgumentType.create())
								.suggests(
									(commandContext, suggestionsBuilder) -> {
										PlayerManager playerManager = commandContext.getSource().getMinecraftServer().method_3760();
										return CommandSource.suggestMatching(
											playerManager.getPlayerList()
												.stream()
												.filter(serverPlayerEntity -> !playerManager.method_14590().method_14653(serverPlayerEntity.getGameProfile()))
												.map(serverPlayerEntity -> serverPlayerEntity.getGameProfile().getName()),
											suggestionsBuilder
										);
									}
								)
								.executes(commandContext -> method_13838(commandContext.getSource(), GameProfileArgumentType.getProfilesArgument(commandContext, "targets")))
						)
				)
				.then(
					ServerCommandManager.literal("remove")
						.then(
							ServerCommandManager.argument("targets", GameProfileArgumentType.create())
								.suggests(
									(commandContext, suggestionsBuilder) -> CommandSource.suggestMatching(
											commandContext.getSource().getMinecraftServer().method_3760().getWhitelistedNames(), suggestionsBuilder
										)
								)
								.executes(commandContext -> method_13845(commandContext.getSource(), GameProfileArgumentType.getProfilesArgument(commandContext, "targets")))
						)
				)
				.then(ServerCommandManager.literal("reload").executes(commandContext -> method_13850(commandContext.getSource())))
		);
	}

	private static int method_13850(ServerCommandSource serverCommandSource) {
		serverCommandSource.getMinecraftServer().method_3760().reloadWhitelist();
		serverCommandSource.method_9226(new TranslatableTextComponent("commands.whitelist.reloaded"), true);
		serverCommandSource.getMinecraftServer().method_3728(serverCommandSource);
		return 1;
	}

	private static int method_13838(ServerCommandSource serverCommandSource, Collection<GameProfile> collection) throws CommandSyntaxException {
		WhitelistList whitelistList = serverCommandSource.getMinecraftServer().method_3760().method_14590();
		int i = 0;

		for (GameProfile gameProfile : collection) {
			if (!whitelistList.method_14653(gameProfile)) {
				WhitelistEntry whitelistEntry = new WhitelistEntry(gameProfile);
				whitelistList.add(whitelistEntry);
				serverCommandSource.method_9226(new TranslatableTextComponent("commands.whitelist.add.success", TextFormatter.profile(gameProfile)), true);
				i++;
			}
		}

		if (i == 0) {
			throw field_13768.create();
		} else {
			return i;
		}
	}

	private static int method_13845(ServerCommandSource serverCommandSource, Collection<GameProfile> collection) throws CommandSyntaxException {
		WhitelistList whitelistList = serverCommandSource.getMinecraftServer().method_3760().method_14590();
		int i = 0;

		for (GameProfile gameProfile : collection) {
			if (whitelistList.method_14653(gameProfile)) {
				WhitelistEntry whitelistEntry = new WhitelistEntry(gameProfile);
				whitelistList.removeEntry(whitelistEntry);
				serverCommandSource.method_9226(new TranslatableTextComponent("commands.whitelist.remove.success", TextFormatter.profile(gameProfile)), true);
				i++;
			}
		}

		if (i == 0) {
			throw field_13769.create();
		} else {
			serverCommandSource.getMinecraftServer().method_3728(serverCommandSource);
			return i;
		}
	}

	private static int method_13839(ServerCommandSource serverCommandSource) throws CommandSyntaxException {
		PlayerManager playerManager = serverCommandSource.getMinecraftServer().method_3760();
		if (playerManager.isWhitelistEnabled()) {
			throw field_13767.create();
		} else {
			playerManager.setWhitelistEnabled(true);
			serverCommandSource.method_9226(new TranslatableTextComponent("commands.whitelist.enabled"), true);
			serverCommandSource.getMinecraftServer().method_3728(serverCommandSource);
			return 1;
		}
	}

	private static int method_13837(ServerCommandSource serverCommandSource) throws CommandSyntaxException {
		PlayerManager playerManager = serverCommandSource.getMinecraftServer().method_3760();
		if (!playerManager.isWhitelistEnabled()) {
			throw field_13770.create();
		} else {
			playerManager.setWhitelistEnabled(false);
			serverCommandSource.method_9226(new TranslatableTextComponent("commands.whitelist.disabled"), true);
			return 1;
		}
	}

	private static int method_13840(ServerCommandSource serverCommandSource) {
		String[] strings = serverCommandSource.getMinecraftServer().method_3760().getWhitelistedNames();
		if (strings.length == 0) {
			serverCommandSource.method_9226(new TranslatableTextComponent("commands.whitelist.none"), false);
		} else {
			serverCommandSource.method_9226(new TranslatableTextComponent("commands.whitelist.list", strings.length, String.join(", ", strings)), false);
		}

		return strings.length;
	}
}
