package net.minecraft.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.book.CookingRecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;

public class PotatoRefinementRecipe implements Recipe<Inventory> {
	private final RecipeType<?> TYPE = RecipeType.POTATO_REFINEMENT;
	private final String field_50720;
	private final CookingRecipeCategory category;
	final Ingredient ingredient;
	final Ingredient bottleIngredient;
	final ItemStack result;
	final float experience;
	protected final int refinementTime;

	public PotatoRefinementRecipe(Ingredient ingredient, Ingredient bottleIngredient, ItemStack result, float experience, int refinementTime) {
		this("", CookingRecipeCategory.MISC, ingredient, bottleIngredient, result, experience, refinementTime);
	}

	private PotatoRefinementRecipe(
		String string, CookingRecipeCategory category, Ingredient ingredient, Ingredient bottleIngredient, ItemStack result, float experience, int refinementTime
	) {
		this.field_50720 = string;
		this.category = category;
		this.ingredient = ingredient;
		this.bottleIngredient = bottleIngredient;
		this.result = result;
		this.experience = experience;
		this.refinementTime = refinementTime;
	}

	@Override
	public boolean matches(Inventory inventory, World world) {
		return this.ingredient.test(inventory.getStack(0)) && this.bottleIngredient.test(inventory.getStack(2));
	}

	@Override
	public ItemStack craft(Inventory inventory, RegistryWrapper.WrapperLookup lookup) {
		return this.result.copy();
	}

	@Override
	public boolean fits(int width, int height) {
		return true;
	}

	@Override
	public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
		return this.result;
	}

	@Override
	public ItemStack createIcon() {
		return new ItemStack(Blocks.POTATO_REFINERY);
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeSerializer.POTATO_REFINEMENT;
	}

	@Override
	public RecipeType<?> getType() {
		return this.TYPE;
	}

	public int getRefinementTime() {
		return this.refinementTime;
	}

	public static class Serializer implements RecipeSerializer<PotatoRefinementRecipe> {
		private static final Codec<PotatoRefinementRecipe> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						Ingredient.ALLOW_EMPTY_CODEC.fieldOf("ingredient").forGetter(potatoRefinementRecipe -> potatoRefinementRecipe.ingredient),
						Ingredient.ALLOW_EMPTY_CODEC.fieldOf("bottle_ingredient").forGetter(potatoRefinementRecipe -> potatoRefinementRecipe.bottleIngredient),
						ItemStack.CODEC.fieldOf("result").forGetter(potatoRefinementRecipe -> potatoRefinementRecipe.result),
						Codec.FLOAT.fieldOf("experience").forGetter(potatoRefinementRecipe -> potatoRefinementRecipe.experience),
						Codec.INT.fieldOf("refinement_time").forGetter(potatoRefinementRecipe -> potatoRefinementRecipe.refinementTime)
					)
					.apply(instance, PotatoRefinementRecipe::new)
		);
		public static final PacketCodec<RegistryByteBuf, PotatoRefinementRecipe> PACKET_CODEC = PacketCodec.ofStatic(
			PotatoRefinementRecipe.Serializer::toPacket, PotatoRefinementRecipe.Serializer::fromPacket
		);

		@Override
		public Codec<PotatoRefinementRecipe> codec() {
			return CODEC;
		}

		@Override
		public PacketCodec<RegistryByteBuf, PotatoRefinementRecipe> packetCodec() {
			return PACKET_CODEC;
		}

		private static PotatoRefinementRecipe fromPacket(RegistryByteBuf buf) {
			Ingredient ingredient = Ingredient.PACKET_CODEC.decode(buf);
			Ingredient ingredient2 = Ingredient.PACKET_CODEC.decode(buf);
			ItemStack itemStack = ItemStack.PACKET_CODEC.decode(buf);
			float f = buf.readFloat();
			int i = buf.readInt();
			return new PotatoRefinementRecipe(ingredient, ingredient2, itemStack, f, i);
		}

		private static void toPacket(RegistryByteBuf registryByteBuf, PotatoRefinementRecipe buf) {
			Ingredient.PACKET_CODEC.encode(registryByteBuf, buf.ingredient);
			Ingredient.PACKET_CODEC.encode(registryByteBuf, buf.bottleIngredient);
			ItemStack.PACKET_CODEC.encode(registryByteBuf, buf.result);
			registryByteBuf.writeFloat(buf.experience);
			registryByteBuf.writeInt(buf.refinementTime);
		}
	}
}
