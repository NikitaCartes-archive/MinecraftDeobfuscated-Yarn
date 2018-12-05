package net.minecraft.server.dedicated.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.MinecraftException;

public class SaveAllCommand {
	private static final SimpleCommandExceptionType field_13701 = new SimpleCommandExceptionType(new TranslatableTextComponent("commands.save.failed"));

	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			ServerCommandManager.literal("save-all")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(4))
				.executes(commandContext -> method_13550(commandContext.getSource(), false))
				.then(ServerCommandManager.literal("flush").executes(commandContext -> method_13550(commandContext.getSource(), true)))
		);
	}

	private static int method_13550(ServerCommandSource serverCommandSource, boolean bl) throws CommandSyntaxException {
		serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.save.saving"), false);
		MinecraftServer minecraftServer = serverCommandSource.getMinecraftServer();
		boolean bl2 = false;
		minecraftServer.getConfigurationManager().saveAllPlayerData();

		for (ServerWorld serverWorld : minecraftServer.getWorlds()) {
			if (serverWorld != null && method_13552(serverWorld, bl)) {
				bl2 = true;
			}
		}

		if (!bl2) {
			throw field_13701.create();
		} else {
			serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.save.success"), true);
			return 1;
		}
	}

	private static boolean method_13552(ServerWorld serverWorld, boolean bl) {
		boolean bl2 = serverWorld.field_13957;
		serverWorld.field_13957 = false;

		boolean var4;
		try {
			serverWorld.save(null, bl);
			return true;
		} catch (MinecraftException var8) {
			var4 = false;
		} finally {
			serverWorld.field_13957 = bl2;
		}

		return var4;
	}
}
