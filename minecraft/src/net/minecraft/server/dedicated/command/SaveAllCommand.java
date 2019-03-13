package net.minecraft.server.dedicated.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableTextComponent;

public class SaveAllCommand {
	private static final SimpleCommandExceptionType SAVE_FAILED = new SimpleCommandExceptionType(new TranslatableTextComponent("commands.save.failed"));

	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			ServerCommandManager.literal("save-all")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(4))
				.executes(commandContext -> saveAll(commandContext.getSource(), false))
				.then(ServerCommandManager.literal("flush").executes(commandContext -> saveAll(commandContext.getSource(), true)))
		);
	}

	private static int saveAll(ServerCommandSource serverCommandSource, boolean bl) throws CommandSyntaxException {
		serverCommandSource.method_9226(new TranslatableTextComponent("commands.save.saving"), false);
		MinecraftServer minecraftServer = serverCommandSource.getMinecraftServer();
		minecraftServer.method_3760().saveAllPlayerData();
		boolean bl2 = minecraftServer.save(true, bl, true);
		if (!bl2) {
			throw SAVE_FAILED.create();
		} else {
			serverCommandSource.method_9226(new TranslatableTextComponent("commands.save.success"), true);
			return 1;
		}
	}
}
