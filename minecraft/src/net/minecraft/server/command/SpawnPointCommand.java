package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.command.arguments.BlockPosArgumentType;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;

public class SpawnPointCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("spawnpoint")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.executes(
					commandContext -> execute(
							commandContext.getSource(), Collections.singleton(commandContext.getSource().getPlayer()), new BlockPos(commandContext.getSource().getPosition())
						)
				)
				.then(
					CommandManager.argument("targets", EntityArgumentType.players())
						.executes(
							commandContext -> execute(
									commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), new BlockPos(commandContext.getSource().getPosition())
								)
						)
						.then(
							CommandManager.argument("pos", BlockPosArgumentType.blockPos())
								.executes(
									commandContext -> execute(
											commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), BlockPosArgumentType.getBlockPos(commandContext, "pos")
										)
								)
						)
				)
		);
	}

	private static int execute(ServerCommandSource source, Collection<ServerPlayerEntity> targets, BlockPos pos) {
		for (ServerPlayerEntity serverPlayerEntity : targets) {
			serverPlayerEntity.setPlayerSpawn(pos, true, false);
		}

		if (targets.size() == 1) {
			source.sendFeedback(
				new TranslatableText(
					"commands.spawnpoint.success.single", pos.getX(), pos.getY(), pos.getZ(), ((ServerPlayerEntity)targets.iterator().next()).getDisplayName()
				),
				true
			);
		} else {
			source.sendFeedback(new TranslatableText("commands.spawnpoint.success.multiple", pos.getX(), pos.getY(), pos.getZ(), targets.size()), true);
		}

		return targets.size();
	}
}
