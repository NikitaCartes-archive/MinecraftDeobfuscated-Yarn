package net.minecraft.network.packet.s2c.common;

import java.util.Optional;
import java.util.UUID;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientCommonPacketListener;
import net.minecraft.network.packet.Packet;

public record ResourcePackRemoveS2CPacket(Optional<UUID> id) implements Packet<ClientCommonPacketListener> {
	public ResourcePackRemoveS2CPacket(PacketByteBuf buf) {
		this(buf.readOptional(PacketByteBuf::readUuid));
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeOptional(this.id, PacketByteBuf::writeUuid);
	}

	public void apply(ClientCommonPacketListener clientCommonPacketListener) {
		clientCommonPacketListener.onResourcePackRemove(this);
	}
}
