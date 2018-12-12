package net.minecraft.server.dedicated.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.world.SessionLockException;

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
		serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.save.saving"), false);
		MinecraftServer minecraftServer = serverCommandSource.getMinecraftServer();
		boolean bl2 = false;
		minecraftServer.getPlayerManager().saveAllPlayerData();

		for (ServerWorld serverWorld : minecraftServer.getWorlds()) {
			if (serverWorld != null && saveWorld(serverWorld, bl)) {
				bl2 = true;
			}
		}

		if (!bl2) {
			throw SAVE_FAILED.create();
		} else {
			serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.save.success"), true);
			return 1;
		}
	}

	private static boolean saveWorld(ServerWorld serverWorld, boolean bl) {
		boolean bl2 = serverWorld.savingDisabled;
		serverWorld.savingDisabled = false;

		boolean var4;
		try {
			serverWorld.save(null, bl);
			return true;
		} catch (SessionLockException var8) {
			var4 = false;
		} finally {
			serverWorld.savingDisabled = bl2;
		}

		return var4;
	}
}
