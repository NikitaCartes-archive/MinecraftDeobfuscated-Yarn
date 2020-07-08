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
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("list")
				.executes(commandContext -> executeNames(commandContext.getSource()))
				.then(CommandManager.literal("uuids").executes(commandContext -> executeUuids(commandContext.getSource())))
		);
	}

	private static int executeNames(ServerCommandSource source) {
		return execute(source, PlayerEntity::getDisplayName);
	}

	private static int executeUuids(ServerCommandSource source) {
		return execute(
			source, serverPlayerEntity -> new TranslatableText("commands.list.nameAndId", serverPlayerEntity.getName(), serverPlayerEntity.getGameProfile().getId())
		);
	}

	private static int execute(ServerCommandSource source, Function<ServerPlayerEntity, Text> nameProvider) {
		PlayerManager playerManager = source.getMinecraftServer().getPlayerManager();
		List<ServerPlayerEntity> list = playerManager.getPlayerList();
		Text text = Texts.join(list, nameProvider);
		source.sendFeedback(new TranslatableText("commands.list.players", list.size(), playerManager.getMaxPlayerCount(), text), false);
		return list.size();
	}
}
