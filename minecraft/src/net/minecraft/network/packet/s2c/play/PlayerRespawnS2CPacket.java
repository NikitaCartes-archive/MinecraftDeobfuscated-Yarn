package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;

public record PlayerRespawnS2CPacket(CommonPlayerSpawnInfo commonPlayerSpawnInfo, byte flag) implements Packet<ClientPlayPacketListener> {
	public static final byte KEEP_ATTRIBUTES = 1;
	public static final byte KEEP_TRACKED_DATA = 2;
	public static final byte KEEP_ALL = 3;

	public PlayerRespawnS2CPacket(PacketByteBuf buf) {
		this(new CommonPlayerSpawnInfo(buf), buf.readByte());
	}

	@Override
	public void write(PacketByteBuf buf) {
		this.commonPlayerSpawnInfo.write(buf);
		buf.writeByte(this.flag);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onPlayerRespawn(this);
	}

	public boolean hasFlag(byte flag) {
		return (this.flag & flag) != 0;
	}
}
