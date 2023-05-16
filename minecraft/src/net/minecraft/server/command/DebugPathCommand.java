package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class DebugPathCommand {
	private static final SimpleCommandExceptionType SOURCE_NOT_MOB_EXCEPTION = new SimpleCommandExceptionType(Text.literal("Source is not a mob"));
	private static final SimpleCommandExceptionType PATH_NOT_FOUND_EXCEPTION = new SimpleCommandExceptionType(Text.literal("Path not found"));
	private static final SimpleCommandExceptionType TARGET_NOT_REACHED_EXCEPTION = new SimpleCommandExceptionType(Text.literal("Target not reached"));

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("debugpath")
				.requires(source -> source.hasPermissionLevel(2))
				.then(
					CommandManager.argument("to", BlockPosArgumentType.blockPos())
						.executes(context -> execute(context.getSource(), BlockPosArgumentType.getLoadedBlockPos(context, "to")))
				)
		);
	}

	private static int execute(ServerCommandSource source, BlockPos pos) throws CommandSyntaxException {
		if (!(source.getEntity() instanceof MobEntity mobEntity)) {
			throw SOURCE_NOT_MOB_EXCEPTION.create();
		} else {
			EntityNavigation entityNavigation = new MobNavigation(mobEntity, source.getWorld());
			Path path = entityNavigation.findPathTo(pos, 0);
			DebugInfoSender.sendPathfindingData(source.getWorld(), mobEntity, path, entityNavigation.getNodeReachProximity());
			if (path == null) {
				throw PATH_NOT_FOUND_EXCEPTION.create();
			} else if (!path.reachesTarget()) {
				throw TARGET_NOT_REACHED_EXCEPTION.create();
			} else {
				source.sendFeedback(() -> Text.literal("Made path"), true);
				return 1;
			}
		}
	}
}
