package net.minecraft.network.packet.s2c.play;

import io.netty.buffer.ByteBuf;
import java.util.List;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.recipe.NetworkRecipeId;

public record RecipeBookRemoveS2CPacket(List<NetworkRecipeId> recipes) implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<ByteBuf, RecipeBookRemoveS2CPacket> CODEC = PacketCodec.tuple(
		NetworkRecipeId.PACKET_CODEC.collect(PacketCodecs.toList()), RecipeBookRemoveS2CPacket::recipes, RecipeBookRemoveS2CPacket::new
	);

	@Override
	public PacketType<RecipeBookRemoveS2CPacket> getPacketId() {
		return PlayPackets.RECIPE_BOOK_REMOVE;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onRecipeBookRemove(this);
	}
}
