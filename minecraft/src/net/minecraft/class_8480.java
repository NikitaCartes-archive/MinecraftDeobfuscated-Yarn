package net.minecraft;

import java.util.Optional;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.text.Text;

public record class_8480(int transactionId, Optional<Text> rejectReason) implements Packet<ClientPlayPacketListener> {
	public class_8480(PacketByteBuf packetByteBuf) {
		this(packetByteBuf.readVarInt(), packetByteBuf.readOptional(PacketByteBuf::readText));
	}

	public static class_8480 method_51141(int i) {
		return new class_8480(i, Optional.empty());
	}

	public static class_8480 method_51142(int i, Text text) {
		return new class_8480(i, Optional.of(text));
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarInt(this.transactionId);
		buf.writeOptional(this.rejectReason, PacketByteBuf::writeText);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.method_51012(this);
	}
}
