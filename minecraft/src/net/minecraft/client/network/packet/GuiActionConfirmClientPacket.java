package net.minecraft.client.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class GuiActionConfirmClientPacket implements Packet<ClientPlayPacketListener> {
	private int id;
	private short actionId;
	private boolean accepted;

	public GuiActionConfirmClientPacket() {
	}

	public GuiActionConfirmClientPacket(int i, short s, boolean bl) {
		this.id = i;
		this.actionId = s;
		this.accepted = bl;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onGuiActionConfirm(this);
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.id = packetByteBuf.readUnsignedByte();
		this.actionId = packetByteBuf.readShort();
		this.accepted = packetByteBuf.readBoolean();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeByte(this.id);
		packetByteBuf.writeShort(this.actionId);
		packetByteBuf.writeBoolean(this.accepted);
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
