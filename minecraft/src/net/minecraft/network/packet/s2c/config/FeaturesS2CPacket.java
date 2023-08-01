package net.minecraft.network.packet.s2c.config;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientConfigurationPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.Identifier;

public record FeaturesS2CPacket(Set<Identifier> features) implements Packet<ClientConfigurationPacketListener> {
	public FeaturesS2CPacket(PacketByteBuf buf) {
		this(buf.readCollection(HashSet::new, PacketByteBuf::readIdentifier));
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeCollection(this.features, PacketByteBuf::writeIdentifier);
	}

	public void apply(ClientConfigurationPacketListener clientConfigurationPacketListener) {
		clientConfigurationPacketListener.onFeatures(this);
	}
}
