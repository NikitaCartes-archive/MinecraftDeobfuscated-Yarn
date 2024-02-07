package net.minecraft.network.packet.s2c.config;

import io.netty.buffer.ByteBuf;
import java.util.List;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ClientConfigurationPacketListener;
import net.minecraft.network.packet.ConfigPackets;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SerializableRegistries;
import net.minecraft.util.Identifier;

public record DynamicRegistriesS2CPacket(RegistryKey<? extends Registry<?>> registry, List<SerializableRegistries.SerializedRegistryEntry> entries)
	implements Packet<ClientConfigurationPacketListener> {
	private static final PacketCodec<ByteBuf, RegistryKey<? extends Registry<?>>> REGISTRY_KEY_CODEC = Identifier.PACKET_CODEC
		.xmap(RegistryKey::ofRegistry, RegistryKey::getValue);
	public static final PacketCodec<PacketByteBuf, DynamicRegistriesS2CPacket> CODEC = PacketCodec.tuple(
		REGISTRY_KEY_CODEC,
		DynamicRegistriesS2CPacket::registry,
		SerializableRegistries.SerializedRegistryEntry.PACKET_CODEC.collect(PacketCodecs.toList()),
		DynamicRegistriesS2CPacket::entries,
		DynamicRegistriesS2CPacket::new
	);

	@Override
	public PacketType<DynamicRegistriesS2CPacket> getPacketId() {
		return ConfigPackets.REGISTRY_DATA;
	}

	public void apply(ClientConfigurationPacketListener clientConfigurationPacketListener) {
		clientConfigurationPacketListener.onDynamicRegistries(this);
	}
}
