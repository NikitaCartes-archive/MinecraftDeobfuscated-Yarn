package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public record PlayerRespawnS2CPacket(CommonPlayerSpawnInfo commonPlayerSpawnInfo, byte flag) implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, PlayerRespawnS2CPacket> CODEC = Packet.createCodec(PlayerRespawnS2CPacket::write, PlayerRespawnS2CPacket::new);
	public static final byte KEEP_ATTRIBUTES = 1;
	public static final byte KEEP_TRACKED_DATA = 2;
	public static final byte KEEP_ALL = 3;

	private PlayerRespawnS2CPacket(PacketByteBuf buf) {
		this(new CommonPlayerSpawnInfo(buf), buf.readByte());
	}

	private void write(PacketByteBuf buf) {
		this.commonPlayerSpawnInfo.write(buf);
		buf.writeByte(this.flag);
	}

	@Override
	public PacketType<PlayerRespawnS2CPacket> getPacketId() {
		return PlayPackets.RESPAWN;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onPlayerRespawn(this);
	}

	public boolean hasFlag(byte flag) {
		return (this.flag & flag) != 0;
	}
}
