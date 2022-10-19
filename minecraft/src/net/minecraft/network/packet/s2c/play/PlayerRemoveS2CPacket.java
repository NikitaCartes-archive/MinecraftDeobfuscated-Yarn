package net.minecraft.network.packet.s2c.play;

import java.util.List;
import java.util.UUID;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public record PlayerRemoveS2CPacket(List<UUID> profileIds) implements Packet<ClientPlayPacketListener> {
	public PlayerRemoveS2CPacket(PacketByteBuf buf) {
		this(buf.readList(PacketByteBuf::readUuid));
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeCollection(this.profileIds, PacketByteBuf::writeUuid);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onPlayerRemove(this);
	}
}
