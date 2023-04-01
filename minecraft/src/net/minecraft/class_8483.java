package net.minecraft;

import java.util.UUID;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;

public record class_8483(UUID id, class_8367 voteData) implements Packet<ClientPlayPacketListener> {
	public class_8483(PacketByteBuf packetByteBuf) {
		this(packetByteBuf.readUuid(), packetByteBuf.decode(NbtOps.INSTANCE, class_8367.field_43979));
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeUuid(this.id);
		buf.encode(NbtOps.INSTANCE, class_8367.field_43979, this.voteData);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.method_51015(this);
	}
}
