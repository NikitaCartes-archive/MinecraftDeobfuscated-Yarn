package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.math.BlockPos;

public class PlayerSpawnPositionS2CPacket implements Packet<ClientPlayPacketListener> {
	private final BlockPos pos;
	private final float angle;

	public PlayerSpawnPositionS2CPacket(BlockPos pos, float angle) {
		this.pos = pos;
		this.angle = angle;
	}

	public PlayerSpawnPositionS2CPacket(PacketByteBuf buf) {
		this.pos = buf.readBlockPos();
		this.angle = buf.readFloat();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeBlockPos(this.pos);
		buf.writeFloat(this.angle);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onPlayerSpawnPosition(this);
	}

	public BlockPos getPos() {
		return this.pos;
	}

	public float getAngle() {
		return this.angle;
	}
}
