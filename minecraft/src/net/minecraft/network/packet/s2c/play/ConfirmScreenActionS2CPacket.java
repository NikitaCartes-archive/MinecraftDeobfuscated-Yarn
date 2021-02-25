package net.minecraft.network.packet.s2c.play;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class ConfirmScreenActionS2CPacket implements Packet<ClientPlayPacketListener> {
	private final int syncId;
	private final short actionId;
	private final boolean accepted;

	public ConfirmScreenActionS2CPacket(int syncId, short actionId, boolean accepted) {
		this.syncId = syncId;
		this.actionId = actionId;
		this.accepted = accepted;
	}

	public ConfirmScreenActionS2CPacket(PacketByteBuf buf) {
		this.syncId = buf.readUnsignedByte();
		this.actionId = buf.readShort();
		this.accepted = buf.readBoolean();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeByte(this.syncId);
		buf.writeShort(this.actionId);
		buf.writeBoolean(this.accepted);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onConfirmScreenAction(this);
	}

	@Environment(EnvType.CLIENT)
	public int getSyncId() {
		return this.syncId;
	}

	@Environment(EnvType.CLIENT)
	public short getActionId() {
		return this.actionId;
	}

	@Environment(EnvType.CLIENT)
	public boolean wasAccepted() {
		return this.accepted;
	}
}
