package net.minecraft.client.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

public class PlayerSpawnPositionS2CPacket implements Packet<ClientPlayPacketListener> {
	private BlockPos pos;

	public PlayerSpawnPositionS2CPacket() {
	}

	public PlayerSpawnPositionS2CPacket(BlockPos blockPos) {
		this.pos = blockPos;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.pos = packetByteBuf.readBlockPos();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeBlockPos(this.pos);
	}

	public void method_11869(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.method_11142(this);
	}

	@Environment(EnvType.CLIENT)
	public BlockPos getPos() {
		return this.pos;
	}
}
