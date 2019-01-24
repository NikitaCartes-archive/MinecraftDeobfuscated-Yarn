package net.minecraft.server.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

public class CraftRequestServerPacket implements Packet<ServerPlayPacketListener> {
	private int syncId;
	private Identifier recipe;
	private boolean craftAll;

	public CraftRequestServerPacket() {
	}

	@Environment(EnvType.CLIENT)
	public CraftRequestServerPacket(int i, Recipe<?> recipe, boolean bl) {
		this.syncId = i;
		this.recipe = recipe.getId();
		this.craftAll = bl;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.syncId = packetByteBuf.readByte();
		this.recipe = packetByteBuf.readIdentifier();
		this.craftAll = packetByteBuf.readBoolean();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeByte(this.syncId);
		packetByteBuf.writeIdentifier(this.recipe);
		packetByteBuf.writeBoolean(this.craftAll);
	}

	public void method_12317(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onCraftRequest(this);
	}

	public int getSyncId() {
		return this.syncId;
	}

	public Identifier getRecipe() {
		return this.recipe;
	}

	public boolean shouldCraftAll() {
		return this.craftAll;
	}
}
