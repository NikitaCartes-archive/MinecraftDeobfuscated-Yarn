package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class SynchronizeRecipesS2CPacket implements Packet<ClientPlayPacketListener> {
	private final List<Recipe<?>> recipes;

	public SynchronizeRecipesS2CPacket(Collection<Recipe<?>> recipes) {
		this.recipes = Lists.<Recipe<?>>newArrayList(recipes);
	}

	public SynchronizeRecipesS2CPacket(PacketByteBuf packetByteBuf) {
		this.recipes = packetByteBuf.method_34066(SynchronizeRecipesS2CPacket::readRecipe);
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.method_34062(this.recipes, SynchronizeRecipesS2CPacket::writeRecipe);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onSynchronizeRecipes(this);
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

	public static <T extends Recipe<?>> void writeRecipe(PacketByteBuf packetByteBuf, T recipe) {
		packetByteBuf.writeIdentifier(Registry.RECIPE_SERIALIZER.getId(recipe.getSerializer()));
		packetByteBuf.writeIdentifier(recipe.getId());
		((RecipeSerializer<T>)recipe.getSerializer()).write(packetByteBuf, recipe);
	}
}
