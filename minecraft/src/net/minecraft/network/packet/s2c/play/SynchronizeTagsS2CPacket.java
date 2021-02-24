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
	private final Map<RegistryKey<? extends Registry<?>>, TagGroup.class_5748> tagManager;

	public SynchronizeTagsS2CPacket(Map<RegistryKey<? extends Registry<?>>, TagGroup.class_5748> map) {
		this.tagManager = map;
	}

	public SynchronizeTagsS2CPacket(PacketByteBuf packetByteBuf) {
		this.tagManager = packetByteBuf.method_34067(packetByteBufx -> RegistryKey.ofRegistry(packetByteBufx.readIdentifier()), TagGroup.class_5748::method_33160);
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.method_34063(
			this.tagManager,
			(packetByteBuf, registryKey) -> packetByteBuf.writeIdentifier(registryKey.getValue()),
			(packetByteBuf, arg) -> arg.method_33159(packetByteBuf)
		);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onSynchronizeTags(this);
	}

	@Environment(EnvType.CLIENT)
	public Map<RegistryKey<? extends Registry<?>>, TagGroup.class_5748> getTagManager() {
		return this.tagManager;
	}
}
