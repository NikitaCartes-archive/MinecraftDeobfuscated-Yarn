package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class SynchronizeRecipesS2CPacket implements Packet<ClientPlayPacketListener> {
	private final List<RecipeEntry<?>> recipes;

	public SynchronizeRecipesS2CPacket(Collection<RecipeEntry<?>> recipes) {
		this.recipes = Lists.<RecipeEntry<?>>newArrayList(recipes);
	}

	public SynchronizeRecipesS2CPacket(PacketByteBuf buf) {
		this.recipes = buf.readList(SynchronizeRecipesS2CPacket::readRecipe);
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeCollection(this.recipes, SynchronizeRecipesS2CPacket::writeRecipe);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onSynchronizeRecipes(this);
	}

	public List<RecipeEntry<?>> getRecipes() {
		return this.recipes;
	}

	private static RecipeEntry<?> readRecipe(PacketByteBuf buf) {
		Identifier identifier = buf.readIdentifier();
		Identifier identifier2 = buf.readIdentifier();
		Recipe<?> recipe = ((RecipeSerializer)Registries.RECIPE_SERIALIZER
				.getOrEmpty(identifier)
				.orElseThrow(() -> new IllegalArgumentException("Unknown recipe serializer " + identifier)))
			.read(buf);
		return new RecipeEntry<>(identifier2, recipe);
	}

	public static <T extends Recipe<?>> void writeRecipe(PacketByteBuf buf, RecipeEntry<?> recipe) {
		buf.writeIdentifier(Registries.RECIPE_SERIALIZER.getId(recipe.value().getSerializer()));
		buf.writeIdentifier(recipe.id());
		((RecipeSerializer<Recipe<?>>)recipe.value().getSerializer()).write(buf, recipe.value());
	}
}
