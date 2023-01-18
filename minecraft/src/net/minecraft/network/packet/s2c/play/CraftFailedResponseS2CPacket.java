package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.Identifier;

public class CraftFailedResponseS2CPacket implements Packet<ClientPlayPacketListener> {
	private final int syncId;
	private final Identifier recipeId;

	public CraftFailedResponseS2CPacket(int syncId, Recipe<?> recipe) {
		this.syncId = syncId;
		this.recipeId = recipe.getId();
	}

	public CraftFailedResponseS2CPacket(PacketByteBuf buf) {
		this.syncId = buf.readByte();
		this.recipeId = buf.readIdentifier();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeByte(this.syncId);
		buf.writeIdentifier(this.recipeId);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onCraftFailedResponse(this);
	}

	public Identifier getRecipeId() {
		return this.recipeId;
	}

	public int getSyncId() {
		return this.syncId;
	}
}
