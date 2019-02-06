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
	private Identifier recipeId;

	public CraftResponseS2CPacket() {
	}

	public CraftResponseS2CPacket(int i, Recipe<?> recipe) {
		this.syncId = i;
		this.recipeId = recipe.getId();
	}

	@Environment(EnvType.CLIENT)
	public Identifier getRecipeId() {
		return this.recipeId;
	}

	@Environment(EnvType.CLIENT)
	public int getSyncId() {
		return this.syncId;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.syncId = packetByteBuf.readByte();
		this.recipeId = packetByteBuf.readIdentifier();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeByte(this.syncId);
		packetByteBuf.writeIdentifier(this.recipeId);
	}

	public void method_11686(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.method_11090(this);
	}
}
