package net.minecraft.server.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class GuiActionConfirmServerPacket implements Packet<ServerPlayPacketListener> {
	private int windowId;
	private short actionId;
	private boolean accepted;

	public GuiActionConfirmServerPacket() {
	}

	@Environment(EnvType.CLIENT)
	public GuiActionConfirmServerPacket(int i, short s, boolean bl) {
		this.windowId = i;
		this.actionId = s;
		this.accepted = bl;
	}

	public void method_12177(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onConfirmTransaction(this);
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.windowId = packetByteBuf.readByte();
		this.actionId = packetByteBuf.readShort();
		this.accepted = packetByteBuf.readByte() != 0;
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeByte(this.windowId);
		packetByteBuf.writeShort(this.actionId);
		packetByteBuf.writeByte(this.accepted ? 1 : 0);
	}

	public int getWindowId() {
		return this.windowId;
	}

	public short getSyncId() {
		return this.actionId;
	}
}
