package net.minecraft.network.packet.s2c.config;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientConfigurationPacketListener;
import net.minecraft.network.packet.ConfigPackets;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.util.Identifier;

public record FeaturesS2CPacket(Set<Identifier> features) implements Packet<ClientConfigurationPacketListener> {
	public static final PacketCodec<PacketByteBuf, FeaturesS2CPacket> CODEC = Packet.createCodec(FeaturesS2CPacket::write, FeaturesS2CPacket::new);

	private FeaturesS2CPacket(PacketByteBuf buf) {
		this(buf.readCollection(HashSet::new, PacketByteBuf::readIdentifier));
	}

	private void write(PacketByteBuf buf) {
		buf.writeCollection(this.features, PacketByteBuf::writeIdentifier);
	}

	@Override
	public PacketType<FeaturesS2CPacket> getPacketId() {
		return ConfigPackets.UPDATE_ENABLED_FEATURES;
	}

	public void apply(ClientConfigurationPacketListener clientConfigurationPacketListener) {
		clientConfigurationPacketListener.onFeatures(this);
	}
}
