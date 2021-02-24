package net.minecraft.network.packet.s2c.play;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class UnloadChunkS2CPacket implements Packet<ClientPlayPacketListener> {
	private final int x;
	private final int z;

	public UnloadChunkS2CPacket(int x, int z) {
		this.x = x;
		this.z = z;
	}

	public UnloadChunkS2CPacket(PacketByteBuf packetByteBuf) {
		this.x = packetByteBuf.readInt();
		this.z = packetByteBuf.readInt();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeInt(this.x);
		buf.writeInt(this.z);
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
