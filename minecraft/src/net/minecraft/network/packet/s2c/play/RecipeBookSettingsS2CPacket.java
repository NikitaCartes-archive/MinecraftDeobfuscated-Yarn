package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.recipe.book.RecipeBookOptions;

public record RecipeBookSettingsS2CPacket(RecipeBookOptions bookSettings) implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, RecipeBookSettingsS2CPacket> CODEC = PacketCodec.tuple(
		RecipeBookOptions.PACKET_CODEC, RecipeBookSettingsS2CPacket::bookSettings, RecipeBookSettingsS2CPacket::new
	);

	@Override
	public PacketType<RecipeBookSettingsS2CPacket> getPacketId() {
		return PlayPackets.RECIPE_BOOK_SETTINGS;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onRecipeBookSettings(this);
	}
}
