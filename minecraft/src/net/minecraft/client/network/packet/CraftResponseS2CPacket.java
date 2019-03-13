package net.minecraft.client.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

public class CraftResponseS2CPacket implements Packet<ClientPlayPacketListener> {
	private int syncId;
	private Identifier field_12332;

	public CraftResponseS2CPacket() {
	}

	public CraftResponseS2CPacket(int i, Recipe<?> recipe) {
		this.syncId = i;
		this.field_12332 = recipe.method_8114();
	}

	@Environment(EnvType.CLIENT)
	public Identifier method_11684() {
		return this.field_12332;
	}

	@Environment(EnvType.CLIENT)
	public int getSyncId() {
		return this.syncId;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.syncId = packetByteBuf.readByte();
		this.field_12332 = packetByteBuf.method_10810();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeByte(this.syncId);
		packetByteBuf.method_10812(this.field_12332);
	}

	public void method_11686(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.method_11090(this);
	}
}
