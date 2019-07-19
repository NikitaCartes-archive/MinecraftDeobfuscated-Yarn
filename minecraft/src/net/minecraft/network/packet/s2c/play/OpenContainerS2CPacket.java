package net.minecraft.network.packet.s2c.play;

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

public class OpenContainerS2CPacket implements Packet<ClientPlayPacketListener> {
	private int syncId;
	private int containerId;
	private Text name;

	public OpenContainerS2CPacket() {
	}

	public OpenContainerS2CPacket(int syncId, ContainerType<?> type, Text name) {
		this.syncId = syncId;
		this.containerId = Registry.CONTAINER.getRawId(type);
		this.name = name;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.syncId = buf.readVarInt();
		this.containerId = buf.readVarInt();
		this.name = buf.readText();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(this.syncId);
		buf.writeVarInt(this.containerId);
		buf.writeText(this.name);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
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
