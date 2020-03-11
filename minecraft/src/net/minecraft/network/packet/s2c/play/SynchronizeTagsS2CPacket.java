package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.tag.RegistryTagManager;

public class SynchronizeTagsS2CPacket implements Packet<ClientPlayPacketListener> {
	private RegistryTagManager tagManager;

	public SynchronizeTagsS2CPacket() {
	}

	public SynchronizeTagsS2CPacket(RegistryTagManager tagManager) {
		this.tagManager = tagManager;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.tagManager = RegistryTagManager.fromPacket(buf);
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		this.tagManager.toPacket(buf);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onSynchronizeTags(this);
	}

	@Environment(EnvType.CLIENT)
	public RegistryTagManager getTagManager() {
		return this.tagManager;
	}
}
