package net.minecraft.network.packet.c2s.play;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;

public class ConfirmScreenActionC2SPacket implements Packet<ServerPlayPacketListener> {
	private final int syncId;
	private final short actionId;
	private final boolean accepted;

	@Environment(EnvType.CLIENT)
	public ConfirmScreenActionC2SPacket(int syncId, short actionId, boolean accepted) {
		this.syncId = syncId;
		this.actionId = actionId;
		this.accepted = accepted;
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onConfirmScreenAction(this);
	}

	public ConfirmScreenActionC2SPacket(PacketByteBuf buf) {
		this.syncId = buf.readByte();
		this.actionId = buf.readShort();
		this.accepted = buf.readByte() != 0;
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeByte(this.syncId);
		buf.writeShort(this.actionId);
		buf.writeByte(this.accepted ? 1 : 0);
	}

	public int getSyncId() {
		return this.syncId;
	}

	public short getActionId() {
		return this.actionId;
	}
}
