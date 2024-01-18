package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.util.Identifier;

public class CraftFailedResponseS2CPacket implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, CraftFailedResponseS2CPacket> CODEC = Packet.createCodec(
		CraftFailedResponseS2CPacket::write, CraftFailedResponseS2CPacket::new
	);
	private final int syncId;
	private final Identifier recipeId;

	public CraftFailedResponseS2CPacket(int syncId, RecipeEntry<?> recipe) {
		this.syncId = syncId;
		this.recipeId = recipe.id();
	}

	private CraftFailedResponseS2CPacket(PacketByteBuf buf) {
		this.syncId = buf.readByte();
		this.recipeId = buf.readIdentifier();
	}

	private void write(PacketByteBuf buf) {
		buf.writeByte(this.syncId);
		buf.writeIdentifier(this.recipeId);
	}

	@Override
	public PacketType<CraftFailedResponseS2CPacket> getPacketId() {
		return PlayPackets.PLACE_GHOST_RECIPE;
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
