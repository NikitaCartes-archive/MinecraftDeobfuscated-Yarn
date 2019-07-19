package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.arguments.BlockPosArgumentType;
import net.minecraft.network.packet.s2c.play.PlayerSpawnPositionS2CPacket;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;

public class SetWorldSpawnCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("setworldspawn")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.executes(commandContext -> execute(commandContext.getSource(), new BlockPos(commandContext.getSource().getPosition())))
				.then(
					CommandManager.argument("pos", BlockPosArgumentType.blockPos())
						.executes(commandContext -> execute(commandContext.getSource(), BlockPosArgumentType.getBlockPos(commandContext, "pos")))
				)
		);
	}

	private static int execute(ServerCommandSource source, BlockPos pos) {
		source.getWorld().setSpawnPos(pos);
		source.getMinecraftServer().getPlayerManager().sendToAll(new PlayerSpawnPositionS2CPacket(pos));
		source.sendFeedback(new TranslatableText("commands.setworldspawn.success", pos.getX(), pos.getY(), pos.getZ()), true);
		return 1;
	}
}
