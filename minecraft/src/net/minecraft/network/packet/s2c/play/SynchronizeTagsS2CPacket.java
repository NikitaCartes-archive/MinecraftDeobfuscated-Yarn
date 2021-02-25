package net.minecraft.network.packet.s2c.play;

import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.tag.TagGroup;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

public class SynchronizeTagsS2CPacket implements Packet<ClientPlayPacketListener> {
	private final Map<RegistryKey<? extends Registry<?>>, TagGroup.Serialized> groups;

	public SynchronizeTagsS2CPacket(Map<RegistryKey<? extends Registry<?>>, TagGroup.Serialized> groups) {
		this.groups = groups;
	}

	public SynchronizeTagsS2CPacket(PacketByteBuf buf) {
		this.groups = buf.readMap(bufx -> RegistryKey.ofRegistry(bufx.readIdentifier()), TagGroup.Serialized::fromBuf);
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeMap(this.groups, (bufx, registryKey) -> bufx.writeIdentifier(registryKey.getValue()), (bufx, serializedGroup) -> serializedGroup.writeBuf(bufx));
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onSynchronizeTags(this);
	}

	@Environment(EnvType.CLIENT)
	public Map<RegistryKey<? extends Registry<?>>, TagGroup.Serialized> getGroups() {
		return this.groups;
	}
}
