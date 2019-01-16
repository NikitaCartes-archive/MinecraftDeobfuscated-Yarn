package net.minecraft.server.dedicated.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.regex.Matcher;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.config.BannedIpsList;
import net.minecraft.text.TranslatableTextComponent;

public class PardonIpCommand {
	private static final SimpleCommandExceptionType field_13671 = new SimpleCommandExceptionType(new TranslatableTextComponent("commands.pardonip.invalid"));
	private static final SimpleCommandExceptionType field_13672 = new SimpleCommandExceptionType(new TranslatableTextComponent("commands.pardonip.failed"));

	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			ServerCommandManager.literal("pardon-ip")
				.requires(
					serverCommandSource -> serverCommandSource.getMinecraftServer().getPlayerManager().getIpBanList().isEnabled() && serverCommandSource.hasPermissionLevel(3)
				)
				.then(
					ServerCommandManager.argument("target", StringArgumentType.word())
						.suggests(
							(commandContext, suggestionsBuilder) -> CommandSource.suggestMatching(
									commandContext.getSource().getMinecraftServer().getPlayerManager().getIpBanList().getNames(), suggestionsBuilder
								)
						)
						.executes(commandContext -> method_13482(commandContext.getSource(), StringArgumentType.getString(commandContext, "target")))
				)
		);
	}

	private static int method_13482(ServerCommandSource serverCommandSource, String string) throws CommandSyntaxException {
		Matcher matcher = BanIpCommand.field_13466.matcher(string);
		if (!matcher.matches()) {
			throw field_13671.create();
		} else {
			BannedIpsList bannedIpsList = serverCommandSource.getMinecraftServer().getPlayerManager().getIpBanList();
			if (!bannedIpsList.method_14529(string)) {
				throw field_13672.create();
			} else {
				bannedIpsList.remove(string);
				serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.pardonip.success", string), true);
				return 1;
			}
		}
	}
}
