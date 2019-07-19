package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.registry.Registry;

public class SynchronizeRecipesS2CPacket implements Packet<ClientPlayPacketListener> {
	private List<Recipe<?>> recipes;

	public SynchronizeRecipesS2CPacket() {
	}

	public SynchronizeRecipesS2CPacket(Collection<Recipe<?>> recipes) {
		this.recipes = Lists.<Recipe<?>>newArrayList(recipes);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onSynchronizeRecipes(this);
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.recipes = Lists.<Recipe<?>>newArrayList();
		int i = buf.readVarInt();

		for (int j = 0; j < i; j++) {
			this.recipes.add(readRecipe(buf));
		}
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(this.recipes.size());

		for (Recipe<?> recipe : this.recipes) {
			writeRecipe(recipe, buf);
		}
	}

	@Environment(EnvType.CLIENT)
	public List<Recipe<?>> getRecipes() {
		return this.recipes;
	}

	public static Recipe<?> readRecipe(PacketByteBuf buf) {
		Identifier identifier = buf.readIdentifier();
		Identifier identifier2 = buf.readIdentifier();
		return ((RecipeSerializer)Registry.RECIPE_SERIALIZER
				.getOrEmpty(identifier)
				.orElseThrow(() -> new IllegalArgumentException("Unknown recipe serializer " + identifier)))
			.read(identifier2, buf);
	}

	public static <T extends Recipe<?>> void writeRecipe(T recipe, PacketByteBuf buf) {
		buf.writeIdentifier(Registry.RECIPE_SERIALIZER.getId(recipe.getSerializer()));
		buf.writeIdentifier(recipe.getId());
		((RecipeSerializer<T>)recipe.getSerializer()).write(buf, recipe);
	}
}
