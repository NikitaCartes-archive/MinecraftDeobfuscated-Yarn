package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import java.util.List;
import java.util.function.Function;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Components;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;

public class ListCommand {
	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			CommandManager.literal("list")
				.executes(commandContext -> executeNames(commandContext.getSource()))
				.then(CommandManager.literal("uuids").executes(commandContext -> executeUuids(commandContext.getSource())))
		);
	}

	private static int executeNames(ServerCommandSource serverCommandSource) {
		return execute(serverCommandSource, PlayerEntity::getDisplayName);
	}

	private static int executeUuids(ServerCommandSource serverCommandSource) {
		return execute(serverCommandSource, PlayerEntity::getNameAndUuid);
	}

	private static int execute(ServerCommandSource serverCommandSource, Function<ServerPlayerEntity, Component> function) {
		PlayerManager playerManager = serverCommandSource.getMinecraftServer().getPlayerManager();
		List<ServerPlayerEntity> list = playerManager.getPlayerList();
		Component component = Components.join(list, function);
		serverCommandSource.sendFeedback(new TranslatableComponent("commands.list.players", list.size(), playerManager.getMaxPlayerCount(), component), false);
		return list.size();
	}
}
