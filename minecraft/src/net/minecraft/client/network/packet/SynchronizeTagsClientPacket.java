package net.minecraft.client.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.tag.TagManager;
import net.minecraft.util.PacketByteBuf;

public class SynchronizeTagsClientPacket implements Packet<ClientPlayPacketListener> {
	private TagManager tagManager;

	public SynchronizeTagsClientPacket() {
	}

	public SynchronizeTagsClientPacket(TagManager tagManager) {
		this.tagManager = tagManager;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.tagManager = TagManager.fromPacket(packetByteBuf);
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		this.tagManager.toPacket(packetByteBuf);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onSynchronizeTags(this);
	}

	@Environment(EnvType.CLIENT)
	public TagManager getTagManager() {
		return this.tagManager;
	}
}
