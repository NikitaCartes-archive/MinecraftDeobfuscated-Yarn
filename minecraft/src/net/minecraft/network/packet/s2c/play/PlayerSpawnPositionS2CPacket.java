package net.minecraft.network.packet.s2c.play;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.math.BlockPos;

public class PlayerSpawnPositionS2CPacket implements Packet<ClientPlayPacketListener> {
	private final BlockPos pos;
	private final float angle;

	public PlayerSpawnPositionS2CPacket(BlockPos pos, float angle) {
		this.pos = pos;
		this.angle = angle;
	}

	public PlayerSpawnPositionS2CPacket(PacketByteBuf packetByteBuf) {
		this.pos = packetByteBuf.readBlockPos();
		this.angle = packetByteBuf.readFloat();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeBlockPos(this.pos);
		buf.writeFloat(this.angle);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onPlayerSpawnPosition(this);
	}

	@Environment(EnvType.CLIENT)
	public BlockPos getPos() {
		return this.pos;
	}

	@Environment(EnvType.CLIENT)
	public float getAngle() {
		return this.angle;
	}
}
