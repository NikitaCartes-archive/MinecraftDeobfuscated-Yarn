package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import java.util.List;
import java.util.function.Function;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormatter;
import net.minecraft.text.TranslatableTextComponent;

public class ListCommand {
	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			ServerCommandManager.literal("list")
				.executes(commandContext -> method_13437(commandContext.getSource()))
				.then(ServerCommandManager.literal("uuids").executes(commandContext -> method_13436(commandContext.getSource())))
		);
	}

	private static int method_13437(ServerCommandSource serverCommandSource) {
		return method_13434(serverCommandSource, PlayerEntity::getDisplayName);
	}

	private static int method_13436(ServerCommandSource serverCommandSource) {
		return method_13434(serverCommandSource, PlayerEntity::method_7306);
	}

	private static int method_13434(ServerCommandSource serverCommandSource, Function<ServerPlayerEntity, TextComponent> function) {
		PlayerManager playerManager = serverCommandSource.getMinecraftServer().getPlayerManager();
		List<ServerPlayerEntity> list = playerManager.getPlayerList();
		TextComponent textComponent = TextFormatter.join(list, function);
		serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.list.players", list.size(), playerManager.getMaxPlayerCount(), textComponent), false);
		return list.size();
	}
}
