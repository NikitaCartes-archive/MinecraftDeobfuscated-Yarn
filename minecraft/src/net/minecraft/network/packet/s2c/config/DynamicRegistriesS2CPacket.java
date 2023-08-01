package net.minecraft.network.packet.s2c.config;

import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientConfigurationPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.SerializableRegistries;

public record DynamicRegistriesS2CPacket(DynamicRegistryManager.Immutable registryManager) implements Packet<ClientConfigurationPacketListener> {
	private static final RegistryOps<NbtElement> VANILLA_REGISTRY_OPS = RegistryOps.of(NbtOps.INSTANCE, DynamicRegistryManager.of(Registries.REGISTRIES));

	public DynamicRegistriesS2CPacket(PacketByteBuf buf) {
		this(buf.decode(VANILLA_REGISTRY_OPS, SerializableRegistries.CODEC).toImmutable());
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.encode(VANILLA_REGISTRY_OPS, SerializableRegistries.CODEC, this.registryManager);
	}

	public void apply(ClientConfigurationPacketListener clientConfigurationPacketListener) {
		clientConfigurationPacketListener.onDynamicRegistries(this);
	}
}
