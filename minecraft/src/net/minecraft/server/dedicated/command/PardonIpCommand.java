package net.minecraft.server.dedicated.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.regex.Matcher;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.config.BannedIpList;
import net.minecraft.text.TranslatableTextComponent;

public class PardonIpCommand {
	private static final SimpleCommandExceptionType INVALID_IP_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableTextComponent("commands.pardonip.invalid")
	);
	private static final SimpleCommandExceptionType ALREADY_UNBANNED_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableTextComponent("commands.pardonip.failed")
	);

	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			CommandManager.literal("pardon-ip")
				.requires(
					serverCommandSource -> serverCommandSource.getMinecraftServer().getPlayerManager().getIpBanList().isEnabled() && serverCommandSource.hasPermissionLevel(3)
				)
				.then(
					CommandManager.argument("target", StringArgumentType.word())
						.suggests(
							(commandContext, suggestionsBuilder) -> CommandSource.suggestMatching(
									commandContext.getSource().getMinecraftServer().getPlayerManager().getIpBanList().getNames(), suggestionsBuilder
								)
						)
						.executes(commandContext -> pardonIp(commandContext.getSource(), StringArgumentType.getString(commandContext, "target")))
				)
		);
	}

	private static int pardonIp(ServerCommandSource serverCommandSource, String string) throws CommandSyntaxException {
		Matcher matcher = BanIpCommand.field_13466.matcher(string);
		if (!matcher.matches()) {
			throw INVALID_IP_EXCEPTION.create();
		} else {
			BannedIpList bannedIpList = serverCommandSource.getMinecraftServer().getPlayerManager().getIpBanList();
			if (!bannedIpList.method_14529(string)) {
				throw ALREADY_UNBANNED_EXCEPTION.create();
			} else {
				bannedIpList.remove(string);
				serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.pardonip.success", string), true);
				return 1;
			}
		}
	}
}
