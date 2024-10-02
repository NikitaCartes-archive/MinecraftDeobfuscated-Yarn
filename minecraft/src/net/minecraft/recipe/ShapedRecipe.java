package net.minecraft.recipe;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.display.RecipeDisplay;
import net.minecraft.recipe.display.ShapedCraftingRecipeDisplay;
import net.minecraft.recipe.display.SlotDisplay;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;

public class ShapedRecipe implements CraftingRecipe {
	final RawShapedRecipe raw;
	final ItemStack result;
	final String group;
	final CraftingRecipeCategory category;
	final boolean showNotification;
	@Nullable
	private IngredientPlacement ingredientPlacement;

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
	public RecipeSerializer<? extends ShapedRecipe> getSerializer() {
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

	@VisibleForTesting
	public List<Optional<Ingredient>> getIngredients() {
		return this.raw.getIngredients();
	}

	@Override
	public IngredientPlacement getIngredientPlacement() {
		if (this.ingredientPlacement == null) {
			this.ingredientPlacement = IngredientPlacement.forMultipleSlots(this.raw.getIngredients());
		}

		return this.ingredientPlacement;
	}

	@Override
	public boolean showNotification() {
		return this.showNotification;
	}

	public boolean matches(CraftingRecipeInput craftingRecipeInput, World world) {
		return this.raw.matches(craftingRecipeInput);
	}

	public ItemStack craft(CraftingRecipeInput craftingRecipeInput, RegistryWrapper.WrapperLookup wrapperLookup) {
		return this.result.copy();
	}

	public int getWidth() {
		return this.raw.getWidth();
	}

	public int getHeight() {
		return this.raw.getHeight();
	}

	@Override
	public List<RecipeDisplay> getDisplays() {
		return List.of(
			new ShapedCraftingRecipeDisplay(
				this.raw.getWidth(),
				this.raw.getHeight(),
				this.raw
					.getIngredients()
					.stream()
					.map(ingredient -> (SlotDisplay)ingredient.map(Ingredient::toDisplay).orElse(SlotDisplay.EmptySlotDisplay.INSTANCE))
					.toList(),
				new SlotDisplay.StackSlotDisplay(this.result),
				new SlotDisplay.ItemSlotDisplay(Items.CRAFTING_TABLE)
			)
		);
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
