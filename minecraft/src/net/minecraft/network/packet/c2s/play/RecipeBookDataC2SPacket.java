package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.util.Identifier;

public class RecipeBookDataC2SPacket implements Packet<ServerPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, RecipeBookDataC2SPacket> CODEC = Packet.createCodec(
		RecipeBookDataC2SPacket::write, RecipeBookDataC2SPacket::new
	);
	private final Identifier recipeId;

	public RecipeBookDataC2SPacket(RecipeEntry<?> recipe) {
		this.recipeId = recipe.id();
	}

	private RecipeBookDataC2SPacket(PacketByteBuf buf) {
		this.recipeId = buf.readIdentifier();
	}

	private void write(PacketByteBuf buf) {
		buf.writeIdentifier(this.recipeId);
	}

	@Override
	public PacketType<RecipeBookDataC2SPacket> getPacketId() {
		return PlayPackets.RECIPE_BOOK_SEEN_RECIPE;
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onRecipeBookData(this);
	}

	public Identifier getRecipeId() {
		return this.recipeId;
	}
}
