package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.util.math.BlockPos;

public class WorldEventS2CPacket implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, WorldEventS2CPacket> CODEC = Packet.createCodec(WorldEventS2CPacket::write, WorldEventS2CPacket::new);
	private final int eventId;
	private final BlockPos pos;
	private final int data;
	private final boolean global;

	public WorldEventS2CPacket(int eventId, BlockPos pos, int data, boolean global) {
		this.eventId = eventId;
		this.pos = pos.toImmutable();
		this.data = data;
		this.global = global;
	}

	private WorldEventS2CPacket(PacketByteBuf buf) {
		this.eventId = buf.readInt();
		this.pos = buf.readBlockPos();
		this.data = buf.readInt();
		this.global = buf.readBoolean();
	}

	private void write(PacketByteBuf buf) {
		buf.writeInt(this.eventId);
		buf.writeBlockPos(this.pos);
		buf.writeInt(this.data);
		buf.writeBoolean(this.global);
	}

	@Override
	public PacketType<WorldEventS2CPacket> getPacketId() {
		return PlayPackets.LEVEL_EVENT;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onWorldEvent(this);
	}

	public boolean isGlobal() {
		return this.global;
	}

	public int getEventId() {
		return this.eventId;
	}

	public int getData() {
		return this.data;
	}

	public BlockPos getPos() {
		return this.pos;
	}
}
