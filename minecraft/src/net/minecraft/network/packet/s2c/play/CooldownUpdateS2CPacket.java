package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.util.Identifier;

public record CooldownUpdateS2CPacket(Identifier cooldownGroup, int cooldown) implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<RegistryByteBuf, CooldownUpdateS2CPacket> CODEC = PacketCodec.tuple(
		Identifier.PACKET_CODEC, CooldownUpdateS2CPacket::cooldownGroup, PacketCodecs.VAR_INT, CooldownUpdateS2CPacket::cooldown, CooldownUpdateS2CPacket::new
	);

	@Override
	public PacketType<CooldownUpdateS2CPacket> getPacketId() {
		return PlayPackets.COOLDOWN;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onCooldownUpdate(this);
	}
}
