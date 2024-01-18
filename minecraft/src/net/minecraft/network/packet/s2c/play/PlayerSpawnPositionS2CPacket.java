package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.util.math.BlockPos;

public class PlayerSpawnPositionS2CPacket implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, PlayerSpawnPositionS2CPacket> CODEC = Packet.createCodec(
		PlayerSpawnPositionS2CPacket::write, PlayerSpawnPositionS2CPacket::new
	);
	private final BlockPos pos;
	private final float angle;

	public PlayerSpawnPositionS2CPacket(BlockPos pos, float angle) {
		this.pos = pos;
		this.angle = angle;
	}

	private PlayerSpawnPositionS2CPacket(PacketByteBuf buf) {
		this.pos = buf.readBlockPos();
		this.angle = buf.readFloat();
	}

	private void write(PacketByteBuf buf) {
		buf.writeBlockPos(this.pos);
		buf.writeFloat(this.angle);
	}

	@Override
	public PacketType<PlayerSpawnPositionS2CPacket> getPacketId() {
		return PlayPackets.SET_DEFAULT_SPAWN_POSITION;
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
