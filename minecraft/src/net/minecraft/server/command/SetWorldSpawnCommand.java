package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.argument.AngleArgumentType;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;

public class SetWorldSpawnCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("setworldspawn")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.executes(commandContext -> execute(commandContext.getSource(), new BlockPos(commandContext.getSource().getPosition()), 0.0F))
				.then(
					CommandManager.argument("pos", BlockPosArgumentType.blockPos())
						.executes(commandContext -> execute(commandContext.getSource(), BlockPosArgumentType.getBlockPos(commandContext, "pos"), 0.0F))
						.then(
							CommandManager.argument("angle", AngleArgumentType.angle())
								.executes(
									commandContext -> execute(
											commandContext.getSource(), BlockPosArgumentType.getBlockPos(commandContext, "pos"), AngleArgumentType.getAngle(commandContext, "angle")
										)
								)
						)
				)
		);
	}

	private static int execute(ServerCommandSource source, BlockPos pos, float angle) {
		source.getWorld().setSpawnPos(pos, angle);
		source.sendFeedback(new TranslatableText("commands.setworldspawn.success", pos.getX(), pos.getY(), pos.getZ(), angle), true);
		return 1;
	}
}
