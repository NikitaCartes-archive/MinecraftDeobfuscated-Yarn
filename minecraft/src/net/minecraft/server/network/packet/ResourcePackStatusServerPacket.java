package net.minecraft.server.network.packet;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class ResourcePackStatusServerPacket implements Packet<ServerPlayPacketListener> {
	private ResourcePackStatusServerPacket.Status status;

	public ResourcePackStatusServerPacket() {
	}

	public ResourcePackStatusServerPacket(ResourcePackStatusServerPacket.Status status) {
		this.status = status;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.status = packetByteBuf.readEnumConstant(ResourcePackStatusServerPacket.Status.class);
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeEnumConstant(this.status);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onResourcePackStatus(this);
	}

	public static enum Status {
		field_13017,
		field_13018,
		field_13015,
		field_13016;
	}
}
