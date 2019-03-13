package net.minecraft.client.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.tag.TagManager;
import net.minecraft.util.PacketByteBuf;

public class SynchronizeTagsS2CPacket implements Packet<ClientPlayPacketListener> {
	private TagManager field_12757;

	public SynchronizeTagsS2CPacket() {
	}

	public SynchronizeTagsS2CPacket(TagManager tagManager) {
		this.field_12757 = tagManager;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.field_12757 = TagManager.fromPacket(packetByteBuf);
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		this.field_12757.toPacket(packetByteBuf);
	}

	public void method_12001(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.method_11126(this);
	}

	@Environment(EnvType.CLIENT)
	public TagManager method_12000() {
		return this.field_12757;
	}
}
