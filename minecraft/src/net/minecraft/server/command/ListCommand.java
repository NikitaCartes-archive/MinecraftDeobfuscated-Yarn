package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import java.util.List;
import java.util.function.Function;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;

public class ListCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("list")
				.executes(context -> executeNames(context.getSource()))
				.then(CommandManager.literal("uuids").executes(context -> executeUuids(context.getSource())))
		);
	}

	private static int executeNames(ServerCommandSource source) {
		return execute(source, PlayerEntity::getDisplayName);
	}

	private static int executeUuids(ServerCommandSource source) {
		return execute(source, player -> Text.translatable("commands.list.nameAndId", player.getName(), player.getGameProfile().getId()));
	}

	private static int execute(ServerCommandSource source, Function<ServerPlayerEntity, Text> nameProvider) {
		PlayerManager playerManager = source.getServer().getPlayerManager();
		List<ServerPlayerEntity> list = playerManager.getPlayerList();
		Text text = Texts.join(list, nameProvider);
		source.sendFeedback(() -> Text.translatable("commands.list.players", list.size(), playerManager.getMaxPlayerCount(), text), false);
		return list.size();
	}
}
