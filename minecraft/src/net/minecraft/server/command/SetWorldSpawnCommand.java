package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.client.network.packet.PlayerSpawnPositionS2CPacket;
import net.minecraft.command.arguments.BlockPosArgumentType;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;

public class SetWorldSpawnCommand {
	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			CommandManager.literal("setworldspawn")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.executes(commandContext -> execute(commandContext.getSource(), new BlockPos(commandContext.getSource().getPosition())))
				.then(
					CommandManager.argument("pos", BlockPosArgumentType.create())
						.executes(commandContext -> execute(commandContext.getSource(), BlockPosArgumentType.getBlockPos(commandContext, "pos")))
				)
		);
	}

	private static int execute(ServerCommandSource serverCommandSource, BlockPos blockPos) {
		serverCommandSource.getWorld().setSpawnPos(blockPos);
		serverCommandSource.getMinecraftServer().getPlayerManager().sendToAll(new PlayerSpawnPositionS2CPacket(blockPos));
		serverCommandSource.method_9226(new TranslatableText("commands.setworldspawn.success", blockPos.getX(), blockPos.getY(), blockPos.getZ()), true);
		return 1;
	}
}
