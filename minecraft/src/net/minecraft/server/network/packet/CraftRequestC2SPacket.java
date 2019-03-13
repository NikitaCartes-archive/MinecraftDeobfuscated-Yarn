package net.minecraft.server.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

public class CraftRequestC2SPacket implements Packet<ServerPlayPacketListener> {
	private int syncId;
	private Identifier field_12931;
	private boolean craftAll;

	public CraftRequestC2SPacket() {
	}

	@Environment(EnvType.CLIENT)
	public CraftRequestC2SPacket(int i, Recipe<?> recipe, boolean bl) {
		this.syncId = i;
		this.field_12931 = recipe.method_8114();
		this.craftAll = bl;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.syncId = packetByteBuf.readByte();
		this.field_12931 = packetByteBuf.method_10810();
		this.craftAll = packetByteBuf.readBoolean();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeByte(this.syncId);
		packetByteBuf.method_10812(this.field_12931);
		packetByteBuf.writeBoolean(this.craftAll);
	}

	public void method_12317(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.method_12061(this);
	}

	public int getSyncId() {
		return this.syncId;
	}

	public Identifier method_12320() {
		return this.field_12931;
	}

	public boolean shouldCraftAll() {
		return this.craftAll;
	}
}
