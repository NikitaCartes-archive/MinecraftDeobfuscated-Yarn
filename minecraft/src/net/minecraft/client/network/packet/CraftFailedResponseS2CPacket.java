package net.minecraft.client.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

public class CraftFailedResponseS2CPacket implements Packet<ClientPlayPacketListener> {
	private int syncId;
	private Identifier recipeId;

	public CraftFailedResponseS2CPacket() {
	}

	public CraftFailedResponseS2CPacket(int syncId, Recipe<?> recipe) {
		this.syncId = syncId;
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
	public void read(PacketByteBuf buf) throws IOException {
		this.syncId = buf.readByte();
		this.recipeId = buf.readIdentifier();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeByte(this.syncId);
		buf.writeIdentifier(this.recipeId);
	}

	public void method_11686(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onCraftFailedResponse(this);
	}
}
