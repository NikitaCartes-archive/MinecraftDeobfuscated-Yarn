package net.minecraft.network.packet.s2c.play;

import java.util.Collection;
import java.util.List;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.recipe.RecipeEntry;

public class SynchronizeRecipesS2CPacket implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<RegistryByteBuf, SynchronizeRecipesS2CPacket> CODEC = PacketCodec.tuple(
		RecipeEntry.PACKET_CODEC.collect(PacketCodecs.toList()), packet -> packet.recipes, SynchronizeRecipesS2CPacket::new
	);
	private final List<RecipeEntry<?>> recipes;

	public SynchronizeRecipesS2CPacket(Collection<RecipeEntry<?>> recipes) {
		this.recipes = List.copyOf(recipes);
	}

	@Override
	public PacketType<SynchronizeRecipesS2CPacket> getPacketId() {
		return PlayPackets.UPDATE_RECIPES;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onSynchronizeRecipes(this);
	}

	public List<RecipeEntry<?>> getRecipes() {
		return this.recipes;
	}
}
