package net.minecraft;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;

public record class_8258(int transactionId, class_8373 optionId) implements Packet<ServerPlayPacketListener> {
	public class_8258(PacketByteBuf packetByteBuf) {
		this(packetByteBuf.readVarInt(), packetByteBuf.method_51130(class_8373.field_43988));
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarInt(this.transactionId);
		buf.method_51128(class_8373.field_43989, this.optionId);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.method_50043(this);
	}
}
