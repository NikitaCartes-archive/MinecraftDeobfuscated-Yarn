package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.Identifier;

public class RecipeBookDataC2SPacket implements Packet<ServerPlayPacketListener> {
	private final Identifier recipeId;

	public RecipeBookDataC2SPacket(Recipe<?> recipe) {
		this.recipeId = recipe.getId();
	}

	public RecipeBookDataC2SPacket(PacketByteBuf buf) {
		this.recipeId = buf.readIdentifier();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeIdentifier(this.recipeId);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onRecipeBookData(this);
	}

	public Identifier getRecipeId() {
		return this.recipeId;
	}
}
