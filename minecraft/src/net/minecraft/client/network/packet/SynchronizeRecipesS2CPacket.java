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
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.registry.Registry;

public class SynchronizeRecipesS2CPacket implements Packet<ClientPlayPacketListener> {
	private List<Recipe<?>> recipes;

	public SynchronizeRecipesS2CPacket() {
	}

	public SynchronizeRecipesS2CPacket(Collection<Recipe<?>> collection) {
		this.recipes = Lists.<Recipe<?>>newArrayList(collection);
	}

	public void method_11997(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.method_11106(this);
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.recipes = Lists.<Recipe<?>>newArrayList();
		int i = packetByteBuf.readVarInt();

		for (int j = 0; j < i; j++) {
			this.recipes.add(method_17817(packetByteBuf));
		}
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeVarInt(this.recipes.size());

		for (Recipe<?> recipe : this.recipes) {
			method_17816(recipe, packetByteBuf);
		}
	}

	@Environment(EnvType.CLIENT)
	public List<Recipe<?>> getRecipes() {
		return this.recipes;
	}

	public static Recipe<?> method_17817(PacketByteBuf packetByteBuf) {
		Identifier identifier = packetByteBuf.method_10810();
		Identifier identifier2 = packetByteBuf.method_10810();
		return ((RecipeSerializer)Registry.RECIPE_SERIALIZER
				.method_17966(identifier)
				.orElseThrow(() -> new IllegalArgumentException("Unknown recipe serializer " + identifier)))
			.method_8122(identifier2, packetByteBuf);
	}

	public static <T extends Recipe<?>> void method_17816(T recipe, PacketByteBuf packetByteBuf) {
		packetByteBuf.method_10812(Registry.RECIPE_SERIALIZER.method_10221(recipe.method_8119()));
		packetByteBuf.method_10812(recipe.method_8114());
		((RecipeSerializer<T>)recipe.method_8119()).method_8124(packetByteBuf, recipe);
	}
}
