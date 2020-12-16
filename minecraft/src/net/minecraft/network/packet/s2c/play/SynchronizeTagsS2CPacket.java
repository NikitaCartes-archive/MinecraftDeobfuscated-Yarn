package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.Maps;
import java.io.IOException;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.tag.TagGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

public class SynchronizeTagsS2CPacket implements Packet<ClientPlayPacketListener> {
	private Map<RegistryKey<? extends Registry<?>>, TagGroup.class_5748> tagManager;

	public SynchronizeTagsS2CPacket() {
	}

	public SynchronizeTagsS2CPacket(Map<RegistryKey<? extends Registry<?>>, TagGroup.class_5748> map) {
		this.tagManager = map;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		int i = buf.readVarInt();
		this.tagManager = Maps.<RegistryKey<? extends Registry<?>>, TagGroup.class_5748>newHashMapWithExpectedSize(i);

		for (int j = 0; j < i; j++) {
			Identifier identifier = buf.readIdentifier();
			RegistryKey<? extends Registry<?>> registryKey = RegistryKey.ofRegistry(identifier);
			TagGroup.class_5748 lv = TagGroup.class_5748.method_33160(buf);
			this.tagManager.put(registryKey, lv);
		}
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(this.tagManager.size());
		this.tagManager.forEach((registryKey, arg) -> {
			buf.writeIdentifier(registryKey.getValue());
			arg.method_33159(buf);
		});
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onSynchronizeTags(this);
	}

	@Environment(EnvType.CLIENT)
	public Map<RegistryKey<? extends Registry<?>>, TagGroup.class_5748> getTagManager() {
		return this.tagManager;
	}
}
