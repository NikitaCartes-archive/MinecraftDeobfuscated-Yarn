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
							commandContext.getSource(), Collections.singleton(commandContext.getSource().method_9207()), new BlockPos(commandContext.getSource().method_9222())
						)
				)
				.then(
					ServerCommandManager.argument("targets", EntityArgumentType.multiplePlayer())
						.executes(
							commandContext -> method_13645(
									commandContext.getSource(), EntityArgumentType.method_9312(commandContext, "targets"), new BlockPos(commandContext.getSource().method_9222())
								)
						)
						.then(
							ServerCommandManager.argument("pos", BlockPosArgumentType.create())
								.executes(
									commandContext -> method_13645(
											commandContext.getSource(), EntityArgumentType.method_9312(commandContext, "targets"), BlockPosArgumentType.method_9697(commandContext, "pos")
										)
								)
						)
				)
		);
	}

	private static int method_13645(ServerCommandSource serverCommandSource, Collection<ServerPlayerEntity> collection, BlockPos blockPos) {
		for (ServerPlayerEntity serverPlayerEntity : collection) {
			serverPlayerEntity.method_7289(blockPos, true);
		}

		if (collection.size() == 1) {
			serverCommandSource.method_9226(
				new TranslatableTextComponent(
					"commands.spawnpoint.success.single", blockPos.getX(), blockPos.getY(), blockPos.getZ(), ((ServerPlayerEntity)collection.iterator().next()).method_5476()
				),
				true
			);
		} else {
			serverCommandSource.method_9226(
				new TranslatableTextComponent("commands.spawnpoint.success.multiple", blockPos.getX(), blockPos.getY(), blockPos.getZ(), collection.size()), true
			);
		}

		return collection.size();
	}
}
