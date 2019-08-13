package net.minecraft.client.network.packet;

import java.io.IOException;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.container.ContainerType;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.text.Text;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.registry.Registry;

public class OpenContainerPacket implements Packet<ClientPlayPacketListener> {
	private int syncId;
	private int containerId;
	private Text name;

	public OpenContainerPacket() {
	}

	public OpenContainerPacket(int i, ContainerType<?> containerType, Text text) {
		this.syncId = i;
		this.containerId = Registry.CONTAINER.getRawId(containerType);
		this.name = text;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.syncId = packetByteBuf.readVarInt();
		this.containerId = packetByteBuf.readVarInt();
		this.name = packetByteBuf.readText();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeVarInt(this.syncId);
		packetByteBuf.writeVarInt(this.containerId);
		packetByteBuf.writeText(this.name);
	}

	public void method_17591(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onOpenContainer(this);
	}

	@Environment(EnvType.CLIENT)
	public int getSyncId() {
		return this.syncId;
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public ContainerType<?> getContainerType() {
		return Registry.CONTAINER.get(this.containerId);
	}

	@Environment(EnvType.CLIENT)
	public Text getName() {
		return this.name;
	}
}
