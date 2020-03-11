package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class ConfirmGuiActionS2CPacket implements Packet<ClientPlayPacketListener> {
	private int id;
	private short actionId;
	private boolean accepted;

	public ConfirmGuiActionS2CPacket() {
	}

	public ConfirmGuiActionS2CPacket(int id, short actionId, boolean accepted) {
		this.id = id;
		this.actionId = actionId;
		this.accepted = accepted;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onGuiActionConfirm(this);
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.id = buf.readUnsignedByte();
		this.actionId = buf.readShort();
		this.accepted = buf.readBoolean();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeByte(this.id);
		buf.writeShort(this.actionId);
		buf.writeBoolean(this.accepted);
	}

	@Environment(EnvType.CLIENT)
	public int getId() {
		return this.id;
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
