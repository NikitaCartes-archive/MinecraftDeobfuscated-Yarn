package net.minecraft.client.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class UnloadChunkClientPacket implements Packet<ClientPlayPacketListener> {
	private int x;
	private int z;

	public UnloadChunkClientPacket() {
	}

	public UnloadChunkClientPacket(int i, int j) {
		this.x = i;
		this.z = j;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.x = packetByteBuf.readInt();
		this.z = packetByteBuf.readInt();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeInt(this.x);
		packetByteBuf.writeInt(this.z);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onUnloadChunk(this);
	}

	@Environment(EnvType.CLIENT)
	public int getX() {
		return this.x;
	}

	@Environment(EnvType.CLIENT)
	public int getZ() {
		return this.z;
	}
}
