package net.minecraft.server.command;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.UuidArgumentType;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerConfigurationNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class DebugConfigCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("debugconfig")
				.requires(source -> source.hasPermissionLevel(3))
				.then(
					CommandManager.literal("config")
						.then(
							CommandManager.argument("target", EntityArgumentType.player())
								.executes(context -> executeConfig(context.getSource(), EntityArgumentType.getPlayer(context, "target")))
						)
				)
				.then(
					CommandManager.literal("unconfig")
						.then(
							CommandManager.argument("target", UuidArgumentType.uuid())
								.suggests(
									(context, suggestionsBuilder) -> CommandSource.suggestMatching(collectConfiguringPlayers(context.getSource().getServer()), suggestionsBuilder)
								)
								.executes(context -> executeUnconfig(context.getSource(), UuidArgumentType.getUuid(context, "target")))
						)
				)
		);
	}

	private static Iterable<String> collectConfiguringPlayers(MinecraftServer server) {
		Set<String> set = new HashSet();

		for (ClientConnection clientConnection : server.getNetworkIo().getConnections()) {
			if (clientConnection.getPacketListener() instanceof ServerConfigurationNetworkHandler serverConfigurationNetworkHandler) {
				set.add(serverConfigurationNetworkHandler.getDebugProfile().getId().toString());
			}
		}

		return set;
	}

	private static int executeConfig(ServerCommandSource source, ServerPlayerEntity player) {
		GameProfile gameProfile = player.getGameProfile();
		player.networkHandler.reconfigure();
		source.sendFeedback(() -> Text.literal("Switched player " + gameProfile.getName() + "(" + gameProfile.getId() + ") to config mode"), false);
		return 1;
	}

	private static int executeUnconfig(ServerCommandSource source, UUID uuid) {
		for (ClientConnection clientConnection : source.getServer().getNetworkIo().getConnections()) {
			PacketListener var5 = clientConnection.getPacketListener();
			if (var5 instanceof ServerConfigurationNetworkHandler) {
				ServerConfigurationNetworkHandler serverConfigurationNetworkHandler = (ServerConfigurationNetworkHandler)var5;
				if (serverConfigurationNetworkHandler.getDebugProfile().getId().equals(uuid)) {
					serverConfigurationNetworkHandler.endConfiguration();
				}
			}
		}

		source.sendError(Text.literal("Can't find player to unconfig"));
		return 0;
	}
}
