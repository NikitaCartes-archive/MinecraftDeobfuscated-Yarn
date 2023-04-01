package net.minecraft;

import java.util.UUID;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;

public record class_8481(UUID id) implements Packet<ClientPlayPacketListener> {
	public class_8481(PacketByteBuf packetByteBuf) {
		this(packetByteBuf.readUuid());
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeUuid(this.id);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.method_51013(this);
	}
}
