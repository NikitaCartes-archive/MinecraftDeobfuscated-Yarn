package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.command.argument.UuidArgumentType;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.common.ResourcePackRemoveS2CPacket;
import net.minecraft.network.packet.s2c.common.ResourcePackSendS2CPacket;

public class ServerPackCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("serverpack")
				.requires(source -> source.hasPermissionLevel(2))
				.then(
					CommandManager.literal("push")
						.then(
							CommandManager.argument("url", StringArgumentType.string())
								.then(
									CommandManager.argument("uuid", UuidArgumentType.uuid())
										.then(
											CommandManager.argument("hash", StringArgumentType.word())
												.executes(
													context -> executePush(
															context.getSource(),
															StringArgumentType.getString(context, "url"),
															Optional.of(UuidArgumentType.getUuid(context, "uuid")),
															Optional.of(StringArgumentType.getString(context, "hash"))
														)
												)
										)
										.executes(
											context -> executePush(
													context.getSource(), StringArgumentType.getString(context, "url"), Optional.of(UuidArgumentType.getUuid(context, "uuid")), Optional.empty()
												)
										)
								)
								.executes(context -> executePush(context.getSource(), StringArgumentType.getString(context, "url"), Optional.empty(), Optional.empty()))
						)
				)
				.then(
					CommandManager.literal("pop")
						.then(
							CommandManager.argument("uuid", UuidArgumentType.uuid()).executes(context -> executePop(context.getSource(), UuidArgumentType.getUuid(context, "uuid")))
						)
				)
		);
	}

	private static void sendToAll(ServerCommandSource source, Packet<?> packet) {
		source.getServer().getNetworkIo().getConnections().forEach(connection -> connection.send(packet));
	}

	private static int executePush(ServerCommandSource source, String url, Optional<UUID> uuid, Optional<String> hash) {
		UUID uUID = (UUID)uuid.orElseGet(() -> UUID.nameUUIDFromBytes(url.getBytes(StandardCharsets.UTF_8)));
		String string = (String)hash.orElse("");
		ResourcePackSendS2CPacket resourcePackSendS2CPacket = new ResourcePackSendS2CPacket(uUID, url, string, false, null);
		sendToAll(source, resourcePackSendS2CPacket);
		return 0;
	}

	private static int executePop(ServerCommandSource source, UUID uuid) {
		ResourcePackRemoveS2CPacket resourcePackRemoveS2CPacket = new ResourcePackRemoveS2CPacket(Optional.of(uuid));
		sendToAll(source, resourcePackRemoveS2CPacket);
		return 0;
	}
}
