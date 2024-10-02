package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.recipe.display.RecipeDisplay;

public record CraftFailedResponseS2CPacket(int syncId, RecipeDisplay recipeDisplay) implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<RegistryByteBuf, CraftFailedResponseS2CPacket> CODEC = PacketCodec.tuple(
		PacketCodecs.SYNC_ID,
		CraftFailedResponseS2CPacket::syncId,
		RecipeDisplay.STREAM_CODEC,
		CraftFailedResponseS2CPacket::recipeDisplay,
		CraftFailedResponseS2CPacket::new
	);

	@Override
	public PacketType<CraftFailedResponseS2CPacket> getPacketId() {
		return PlayPackets.PLACE_GHOST_RECIPE;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onCraftFailedResponse(this);
	}
}
