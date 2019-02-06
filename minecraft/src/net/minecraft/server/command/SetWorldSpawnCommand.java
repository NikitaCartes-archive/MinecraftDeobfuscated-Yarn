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
				.executes(commandContext -> method_13650(commandContext.getSource(), new BlockPos(commandContext.getSource().getPosition())))
				.then(
					ServerCommandManager.argument("pos", BlockPosArgumentType.create())
						.executes(commandContext -> method_13650(commandContext.getSource(), BlockPosArgumentType.getPosArgument(commandContext, "pos")))
				)
		);
	}

	private static int method_13650(ServerCommandSource serverCommandSource, BlockPos blockPos) {
		serverCommandSource.getWorld().setSpawnPos(blockPos);
		serverCommandSource.getMinecraftServer().getPlayerManager().sendToAll(new PlayerSpawnPositionS2CPacket(blockPos));
		serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.setworldspawn.success", blockPos.getX(), blockPos.getY(), blockPos.getZ()), true);
		return 1;
	}
}
