package net.minecraft.server.network;

import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.common.ResourcePackSendS2CPacket;
import net.minecraft.server.MinecraftServer;

public class SendResourcePackTask implements ServerPlayerConfigurationTask {
	public static final ServerPlayerConfigurationTask.Key KEY = new ServerPlayerConfigurationTask.Key("server_resource_pack");
	private final MinecraftServer.ServerResourcePackProperties packProperties;

	public SendResourcePackTask(MinecraftServer.ServerResourcePackProperties packProperties) {
		this.packProperties = packProperties;
	}

	@Override
	public void sendPacket(Consumer<Packet<?>> sender) {
		sender.accept(
			new ResourcePackSendS2CPacket(
				this.packProperties.id(),
				this.packProperties.url(),
				this.packProperties.hash(),
				this.packProperties.isRequired(),
				Optional.ofNullable(this.packProperties.prompt())
			)
		);
	}

	@Override
	public ServerPlayerConfigurationTask.Key getKey() {
		return KEY;
	}
}
