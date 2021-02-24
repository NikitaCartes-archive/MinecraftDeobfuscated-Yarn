package net.minecraft.network.packet.s2c.play;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.Identifier;

public class CraftFailedResponseS2CPacket implements Packet<ClientPlayPacketListener> {
	private final int syncId;
	private final Identifier recipeId;

	public CraftFailedResponseS2CPacket(int syncId, Recipe<?> recipe) {
		this.syncId = syncId;
		this.recipeId = recipe.getId();
	}

	public CraftFailedResponseS2CPacket(PacketByteBuf packetByteBuf) {
		this.syncId = packetByteBuf.readByte();
		this.recipeId = packetByteBuf.readIdentifier();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeByte(this.syncId);
		buf.writeIdentifier(this.recipeId);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onCraftFailedResponse(this);
	}

	@Environment(EnvType.CLIENT)
	public Identifier getRecipeId() {
		return this.recipeId;
	}

	@Environment(EnvType.CLIENT)
	public int getSyncId() {
		return this.syncId;
	}
}
