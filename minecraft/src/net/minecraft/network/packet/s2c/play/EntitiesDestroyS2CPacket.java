package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class EntitiesDestroyS2CPacket implements Packet<ClientPlayPacketListener> {
	private final int field_33690;

	public EntitiesDestroyS2CPacket(int i) {
		this.field_33690 = i;
	}

	public EntitiesDestroyS2CPacket(PacketByteBuf buf) {
		this.field_33690 = buf.readVarInt();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarInt(this.field_33690);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onEntitiesDestroy(this);
	}

	public int method_36548() {
		return this.field_33690;
	}
}
