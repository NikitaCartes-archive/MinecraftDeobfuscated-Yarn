package net.minecraft.server.command;

import com.google.common.collect.ImmutableSet;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Comparator;
import java.util.stream.IntStream;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Heightmap;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.DimensionHashHelper;
import net.minecraft.world.dimension.DimensionType;

public class WarpCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("warp")
				.then(
					CommandManager.argument("target", StringArgumentType.greedyString())
						.executes(commandContext -> execute(commandContext.getSource(), StringArgumentType.getString(commandContext, "target")))
				)
		);
	}

	private static int execute(ServerCommandSource source, String taret) throws CommandSyntaxException {
		DimensionType dimensionType = Registry.DIMENSION_TYPE.get(DimensionHashHelper.getHash(taret));
		ServerWorld serverWorld = source.getMinecraftServer().getWorld(dimensionType);
		WorldChunk worldChunk = serverWorld.getChunk(0, 0);
		BlockPos blockPos = (BlockPos)IntStream.range(0, 15).boxed().flatMap(integer -> IntStream.range(0, 15).mapToObj(i -> {
				int j = worldChunk.sampleHeightmap(Heightmap.Type.MOTION_BLOCKING, integer, i);
				return new BlockPos(integer, j, i);
			})).filter(blockPosx -> blockPosx.getY() > 0).max(Comparator.comparing(Vec3i::getY)).orElse(new BlockPos(0, 256, 0));
		TeleportCommand.teleport(
			source,
			source.getEntityOrThrow(),
			serverWorld,
			(double)blockPos.getX(),
			(double)(blockPos.getY() + 1),
			(double)blockPos.getZ(),
			ImmutableSet.of(),
			0.0F,
			0.0F,
			null
		);
		return 0;
	}
}
