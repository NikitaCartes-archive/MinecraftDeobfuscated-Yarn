package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import java.util.List;
import java.util.function.Function;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.text.TranslatableText;

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

	private static int execute(ServerCommandSource serverCommandSource, Function<ServerPlayerEntity, Text> function) {
		PlayerManager playerManager = serverCommandSource.getMinecraftServer().getPlayerManager();
		List<ServerPlayerEntity> list = playerManager.getPlayerList();
		Text text = Texts.join(list, function);
		serverCommandSource.sendFeedback(new TranslatableText("commands.list.players", list.size(), playerManager.getMaxPlayerCount(), text), false);
		return list.size();
	}
}
