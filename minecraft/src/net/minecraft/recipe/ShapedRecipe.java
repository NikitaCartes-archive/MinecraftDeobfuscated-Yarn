package net.minecraft.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class ShapedRecipe implements CraftingRecipe {
	final RawShapedRecipe raw;
	final ItemStack result;
	final String group;
	final CraftingRecipeCategory category;
	final boolean showNotification;

	public ShapedRecipe(String group, CraftingRecipeCategory category, RawShapedRecipe raw, ItemStack result, boolean showNotification) {
		this.group = group;
		this.category = category;
		this.raw = raw;
		this.result = result;
		this.showNotification = showNotification;
	}

	public ShapedRecipe(String group, CraftingRecipeCategory category, RawShapedRecipe raw, ItemStack result) {
		this(group, category, raw, result, true);
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeSerializer.SHAPED;
	}

	@Override
	public String getGroup() {
		return this.group;
	}

	@Override
	public CraftingRecipeCategory getCategory() {
		return this.category;
	}

	@Override
	public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
		return this.result;
	}

	@Override
	public DefaultedList<Ingredient> getIngredients() {
		return this.raw.getIngredients();
	}

	@Override
	public boolean showNotification() {
		return this.showNotification;
	}

	@Override
	public boolean fits(int width, int height) {
		return width >= this.raw.getWidth() && height >= this.raw.getHeight();
	}

	public boolean matches(CraftingRecipeInput craftingRecipeInput, World world) {
		return this.raw.matches(craftingRecipeInput);
	}

	public ItemStack craft(CraftingRecipeInput craftingRecipeInput, RegistryWrapper.WrapperLookup wrapperLookup) {
		return this.getResult(wrapperLookup).copy();
	}

	public int getWidth() {
		return this.raw.getWidth();
	}

	public int getHeight() {
		return this.raw.getHeight();
	}

	@Override
	public boolean isEmpty() {
		DefaultedList<Ingredient> defaultedList = this.getIngredients();
		return defaultedList.isEmpty()
			|| defaultedList.stream().filter(ingredient -> !ingredient.isEmpty()).anyMatch(ingredient -> ingredient.getMatchingStacks().length == 0);
	}

	public static class Serializer implements RecipeSerializer<ShapedRecipe> {
		public static final MapCodec<ShapedRecipe> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
						Codec.STRING.optionalFieldOf("group", "").forGetter(recipe -> recipe.group),
						CraftingRecipeCategory.CODEC.fieldOf("category").orElse(CraftingRecipeCategory.MISC).forGetter(recipe -> recipe.category),
						RawShapedRecipe.CODEC.forGetter(recipe -> recipe.raw),
						ItemStack.VALIDATED_CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
						Codec.BOOL.optionalFieldOf("show_notification", Boolean.valueOf(true)).forGetter(recipe -> recipe.showNotification)
					)
					.apply(instance, ShapedRecipe::new)
		);
		public static final PacketCodec<RegistryByteBuf, ShapedRecipe> PACKET_CODEC = PacketCodec.ofStatic(
			ShapedRecipe.Serializer::write, ShapedRecipe.Serializer::read
		);

		@Override
		public MapCodec<ShapedRecipe> codec() {
			return CODEC;
		}

		@Override
		public PacketCodec<RegistryByteBuf, ShapedRecipe> packetCodec() {
			return PACKET_CODEC;
		}

		private static ShapedRecipe read(RegistryByteBuf buf) {
			String string = buf.readString();
			CraftingRecipeCategory craftingRecipeCategory = buf.readEnumConstant(CraftingRecipeCategory.class);
			RawShapedRecipe rawShapedRecipe = RawShapedRecipe.PACKET_CODEC.decode(buf);
			ItemStack itemStack = ItemStack.PACKET_CODEC.decode(buf);
			boolean bl = buf.readBoolean();
			return new ShapedRecipe(string, craftingRecipeCategory, rawShapedRecipe, itemStack, bl);
		}

		private static void write(RegistryByteBuf buf, ShapedRecipe recipe) {
			buf.writeString(recipe.group);
			buf.writeEnumConstant(recipe.category);
			RawShapedRecipe.PACKET_CODEC.encode(buf, recipe.raw);
			ItemStack.PACKET_CODEC.encode(buf, recipe.result);
			buf.writeBoolean(recipe.showNotification);
		}
	}
}
