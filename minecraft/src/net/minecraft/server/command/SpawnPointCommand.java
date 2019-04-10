package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.command.arguments.BlockPosArgumentType;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.math.BlockPos;

public class SpawnPointCommand {
	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
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
							CommandManager.argument("pos", BlockPosArgumentType.create())
								.executes(
									commandContext -> execute(
											commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), BlockPosArgumentType.getBlockPos(commandContext, "pos")
										)
								)
						)
				)
		);
	}

	private static int execute(ServerCommandSource serverCommandSource, Collection<ServerPlayerEntity> collection, BlockPos blockPos) {
		for (ServerPlayerEntity serverPlayerEntity : collection) {
			serverPlayerEntity.setPlayerSpawn(blockPos, true);
		}

		if (collection.size() == 1) {
			serverCommandSource.sendFeedback(
				new TranslatableTextComponent(
					"commands.spawnpoint.success.single",
					blockPos.getX(),
					blockPos.getY(),
					blockPos.getZ(),
					((ServerPlayerEntity)collection.iterator().next()).getDisplayName()
				),
				true
			);
		} else {
			serverCommandSource.sendFeedback(
				new TranslatableTextComponent("commands.spawnpoint.success.multiple", blockPos.getX(), blockPos.getY(), blockPos.getZ(), collection.size()), true
			);
		}

		return collection.size();
	}
}
