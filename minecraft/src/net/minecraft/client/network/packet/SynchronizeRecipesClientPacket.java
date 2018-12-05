package net.minecraft.client.network.packet;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializers;
import net.minecraft.util.PacketByteBuf;

public class SynchronizeRecipesClientPacket implements Packet<ClientPlayPacketListener> {
	private List<Recipe> recipes;

	public SynchronizeRecipesClientPacket() {
	}

	public SynchronizeRecipesClientPacket(Collection<Recipe> collection) {
		this.recipes = Lists.<Recipe>newArrayList(collection);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onSynchronizeRecipes(this);
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.recipes = Lists.<Recipe>newArrayList();
		int i = packetByteBuf.readVarInt();

		for (int j = 0; j < i; j++) {
			this.recipes.add(RecipeSerializers.fromPacket(packetByteBuf));
		}
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeVarInt(this.recipes.size());

		for (Recipe recipe : this.recipes) {
			RecipeSerializers.toPacket(recipe, packetByteBuf);
		}
	}

	@Environment(EnvType.CLIENT)
	public List<Recipe> getRecipes() {
		return this.recipes;
	}
}
