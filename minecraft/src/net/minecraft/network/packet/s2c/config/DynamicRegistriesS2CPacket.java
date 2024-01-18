package net.minecraft.network.packet.s2c.config;

import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientConfigurationPacketListener;
import net.minecraft.network.packet.ConfigPackets;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.SerializableRegistries;

public record DynamicRegistriesS2CPacket(DynamicRegistryManager.Immutable registryManager) implements Packet<ClientConfigurationPacketListener> {
	public static final PacketCodec<PacketByteBuf, DynamicRegistriesS2CPacket> CODEC = Packet.createCodec(
		DynamicRegistriesS2CPacket::write, DynamicRegistriesS2CPacket::new
	);
	private static final RegistryOps<NbtElement> VANILLA_REGISTRY_OPS = RegistryOps.of(NbtOps.INSTANCE, DynamicRegistryManager.of(Registries.REGISTRIES));

	private DynamicRegistriesS2CPacket(PacketByteBuf buf) {
		this(buf.decode(VANILLA_REGISTRY_OPS, SerializableRegistries.CODEC).toImmutable());
	}

	private void write(PacketByteBuf buf) {
		buf.encode(VANILLA_REGISTRY_OPS, SerializableRegistries.CODEC, this.registryManager);
	}

	@Override
	public PacketType<DynamicRegistriesS2CPacket> getPacketId() {
		return ConfigPackets.REGISTRY_DATA;
	}

	public void apply(ClientConfigurationPacketListener clientConfigurationPacketListener) {
		clientConfigurationPacketListener.onDynamicRegistries(this);
	}
}
