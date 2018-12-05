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
			ServerCommandManager.literal("spawnpoint")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.executes(
					commandContext -> method_13645(
							commandContext.getSource(), Collections.singleton(commandContext.getSource().getPlayer()), new BlockPos(commandContext.getSource().getPosition())
						)
				)
				.then(
					ServerCommandManager.argument("targets", EntityArgumentType.method_9308())
						.executes(
							commandContext -> method_13645(
									commandContext.getSource(), EntityArgumentType.method_9312(commandContext, "targets"), new BlockPos(commandContext.getSource().getPosition())
								)
						)
						.then(
							ServerCommandManager.argument("pos", BlockPosArgumentType.create())
								.executes(
									commandContext -> method_13645(
											commandContext.getSource(), EntityArgumentType.method_9312(commandContext, "targets"), BlockPosArgumentType.getPosArgument(commandContext, "pos")
										)
								)
						)
				)
		);
	}

	private static int method_13645(ServerCommandSource serverCommandSource, Collection<ServerPlayerEntity> collection, BlockPos blockPos) {
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
