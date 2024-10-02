package net.minecraft.network.packet.s2c.play;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.recipe.RecipePropertySet;
import net.minecraft.recipe.StonecuttingRecipe;
import net.minecraft.recipe.display.CuttingRecipeDisplay;
import net.minecraft.registry.RegistryKey;

public record SynchronizeRecipesS2CPacket(
	Map<RegistryKey<RecipePropertySet>, RecipePropertySet> itemSets, CuttingRecipeDisplay.Grouping<StonecuttingRecipe> stonecutterRecipes
) implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<RegistryByteBuf, SynchronizeRecipesS2CPacket> CODEC = PacketCodec.tuple(
		PacketCodecs.map(HashMap::new, RegistryKey.createPacketCodec(RecipePropertySet.REGISTRY), RecipePropertySet.PACKET_CODEC),
		SynchronizeRecipesS2CPacket::itemSets,
		CuttingRecipeDisplay.Grouping.codec(),
		SynchronizeRecipesS2CPacket::stonecutterRecipes,
		SynchronizeRecipesS2CPacket::new
	);

	@Override
	public PacketType<SynchronizeRecipesS2CPacket> getPacketId() {
		return PlayPackets.UPDATE_RECIPES;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onSynchronizeRecipes(this);
	}
}
