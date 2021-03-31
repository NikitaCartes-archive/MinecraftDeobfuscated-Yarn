package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.SpawnHelper;

public class DebugMobSpawningCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder = CommandManager.literal("debugmobspawning")
			.requires(source -> source.hasPermissionLevel(2));

		for (SpawnGroup spawnGroup : SpawnGroup.values()) {
			literalArgumentBuilder.then(
				CommandManager.literal(spawnGroup.getName())
					.then(
						CommandManager.argument("at", BlockPosArgumentType.blockPos())
							.executes(context -> execute(context.getSource(), spawnGroup, BlockPosArgumentType.getLoadedBlockPos(context, "at")))
					)
			);
		}

		dispatcher.register(literalArgumentBuilder);
	}

	private static int execute(ServerCommandSource source, SpawnGroup group, BlockPos pos) {
		SpawnHelper.spawnEntitiesInChunk(group, source.getWorld(), pos);
		return 1;
	}
}
