package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.math.BlockPos;

public class WorldEventS2CPacket implements Packet<ClientPlayPacketListener> {
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

	public WorldEventS2CPacket(PacketByteBuf buf) {
		this.eventId = buf.readInt();
		this.pos = buf.readBlockPos();
		this.data = buf.readInt();
		this.global = buf.readBoolean();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeInt(this.eventId);
		buf.writeBlockPos(this.pos);
		buf.writeInt(this.data);
		buf.writeBoolean(this.global);
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
