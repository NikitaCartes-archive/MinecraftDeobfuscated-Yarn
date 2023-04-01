package net.minecraft;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;

public record class_8482(class_8373 id, class_8375 voters) implements Packet<ClientPlayPacketListener> {
	public class_8482(PacketByteBuf packetByteBuf) {
		this(packetByteBuf.method_51130(class_8373.field_43988), packetByteBuf.method_51130(class_8375.field_43993));
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.method_51128(class_8373.field_43989, this.id);
		buf.method_51128(class_8375.field_43994, this.voters);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.method_51014(this);
	}
}
