package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;

public class ConfirmScreenActionC2SPacket implements Packet<ServerPlayPacketListener> {
	private int syncId;
	private short actionId;
	private boolean accepted;

	public ConfirmScreenActionC2SPacket() {
	}

	@Environment(EnvType.CLIENT)
	public ConfirmScreenActionC2SPacket(int syncId, short actionId, boolean accepted) {
		this.syncId = syncId;
		this.actionId = actionId;
		this.accepted = accepted;
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onConfirmScreenAction(this);
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.syncId = buf.readByte();
		this.actionId = buf.readShort();
		this.accepted = buf.readByte() != 0;
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
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
