package net.minecraft.server.dedicated.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class SaveAllCommand {
	private static final SimpleCommandExceptionType SAVE_FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableComponent("commands.save.failed"));

	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			CommandManager.literal("save-all")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(4))
				.executes(commandContext -> saveAll(commandContext.getSource(), false))
				.then(CommandManager.literal("flush").executes(commandContext -> saveAll(commandContext.getSource(), true)))
		);
	}

	private static int saveAll(ServerCommandSource serverCommandSource, boolean bl) throws CommandSyntaxException {
		serverCommandSource.sendFeedback(new TranslatableComponent("commands.save.saving"), false);
		MinecraftServer minecraftServer = serverCommandSource.getMinecraftServer();
		minecraftServer.getPlayerManager().saveAllPlayerData();
		boolean bl2 = minecraftServer.save(true, bl, true);
		if (!bl2) {
			throw SAVE_FAILED_EXCEPTION.create();
		} else {
			serverCommandSource.sendFeedback(new TranslatableComponent("commands.save.success"), true);
			return 1;
		}
	}
}
