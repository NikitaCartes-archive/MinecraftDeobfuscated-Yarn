package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.client.network.packet.PlayerSpawnPositionS2CPacket;
import net.minecraft.command.arguments.BlockPosArgumentType;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.math.BlockPos;

public class SetWorldSpawnCommand {
	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			ServerCommandManager.literal("setworldspawn")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.executes(commandContext -> method_13650(commandContext.getSource(), new BlockPos(commandContext.getSource().method_9222())))
				.then(
					ServerCommandManager.argument("pos", BlockPosArgumentType.create())
						.executes(commandContext -> method_13650(commandContext.getSource(), BlockPosArgumentType.method_9697(commandContext, "pos")))
				)
		);
	}

	private static int method_13650(ServerCommandSource serverCommandSource, BlockPos blockPos) {
		serverCommandSource.method_9225().method_8554(blockPos);
		serverCommandSource.getMinecraftServer().method_3760().sendToAll(new PlayerSpawnPositionS2CPacket(blockPos));
		serverCommandSource.method_9226(new TranslatableTextComponent("commands.setworldspawn.success", blockPos.getX(), blockPos.getY(), blockPos.getZ()), true);
		return 1;
	}
}
