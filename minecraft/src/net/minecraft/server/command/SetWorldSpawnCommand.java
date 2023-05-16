package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.argument.AngleArgumentType;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class SetWorldSpawnCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("setworldspawn")
				.requires(source -> source.hasPermissionLevel(2))
				.executes(context -> execute(context.getSource(), BlockPos.ofFloored(context.getSource().getPosition()), 0.0F))
				.then(
					CommandManager.argument("pos", BlockPosArgumentType.blockPos())
						.executes(context -> execute(context.getSource(), BlockPosArgumentType.getValidBlockPos(context, "pos"), 0.0F))
						.then(
							CommandManager.argument("angle", AngleArgumentType.angle())
								.executes(context -> execute(context.getSource(), BlockPosArgumentType.getValidBlockPos(context, "pos"), AngleArgumentType.getAngle(context, "angle")))
						)
				)
		);
	}

	private static int execute(ServerCommandSource source, BlockPos pos, float angle) {
		source.getWorld().setSpawnPos(pos, angle);
		source.sendFeedback(() -> Text.translatable("commands.setworldspawn.success", pos.getX(), pos.getY(), pos.getZ(), angle), true);
		return 1;
	}
}
