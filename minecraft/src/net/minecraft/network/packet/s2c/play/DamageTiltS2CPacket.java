package net.minecraft.network.packet.s2c.play;

import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;

public record DamageTiltS2CPacket(int id, float yaw) implements Packet<ClientPlayPacketListener> {
	public DamageTiltS2CPacket(LivingEntity entity) {
		this(entity.getId(), entity.getDamageTiltYaw());
	}

	public DamageTiltS2CPacket(PacketByteBuf buf) {
		this(buf.readVarInt(), buf.readFloat());
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarInt(this.id);
		buf.writeFloat(this.yaw);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onDamageTilt(this);
	}
}
