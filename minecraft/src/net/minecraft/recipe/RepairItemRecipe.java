package net.minecraft.recipe;

import com.mojang.datafixers.util.Pair;
import javax.annotation.Nullable;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.world.World;

public class RepairItemRecipe extends SpecialCraftingRecipe {
	public RepairItemRecipe(CraftingRecipeCategory craftingRecipeCategory) {
		super(craftingRecipeCategory);
	}

	@Nullable
	private Pair<ItemStack, ItemStack> findPair(CraftingRecipeInput input) {
		ItemStack itemStack = null;
		ItemStack itemStack2 = null;

		for (int i = 0; i < input.getSize(); i++) {
			ItemStack itemStack3 = input.getStackInSlot(i);
			if (!itemStack3.isEmpty()) {
				if (itemStack == null) {
					itemStack = itemStack3;
				} else {
					if (itemStack2 != null) {
						return null;
					}

					itemStack2 = itemStack3;
				}
			}
		}

		return itemStack != null && itemStack2 != null && canCombineStacks(itemStack, itemStack2) ? Pair.of(itemStack, itemStack2) : null;
	}

	private static boolean canCombineStacks(ItemStack first, ItemStack second) {
		return second.isOf(first.getItem())
			&& first.getCount() == 1
			&& second.getCount() == 1
			&& first.contains(DataComponentTypes.MAX_DAMAGE)
			&& second.contains(DataComponentTypes.MAX_DAMAGE)
			&& first.contains(DataComponentTypes.DAMAGE)
			&& second.contains(DataComponentTypes.DAMAGE);
	}

	public boolean matches(CraftingRecipeInput craftingRecipeInput, World world) {
		return this.findPair(craftingRecipeInput) != null;
	}

	public ItemStack craft(CraftingRecipeInput craftingRecipeInput, RegistryWrapper.WrapperLookup wrapperLookup) {
		Pair<ItemStack, ItemStack> pair = this.findPair(craftingRecipeInput);
		if (pair == null) {
			return ItemStack.EMPTY;
		} else {
			ItemStack itemStack = pair.getFirst();
			ItemStack itemStack2 = pair.getSecond();
			int i = Math.max(itemStack.getMaxDamage(), itemStack2.getMaxDamage());
			int j = itemStack.getMaxDamage() - itemStack.getDamage();
			int k = itemStack2.getMaxDamage() - itemStack2.getDamage();
			int l = j + k + i * 5 / 100;
			ItemStack itemStack3 = new ItemStack(itemStack.getItem());
			itemStack3.set(DataComponentTypes.MAX_DAMAGE, i);
			itemStack3.setDamage(Math.max(i - l, 0));
			ItemEnchantmentsComponent itemEnchantmentsComponent = EnchantmentHelper.getEnchantments(itemStack);
			ItemEnchantmentsComponent itemEnchantmentsComponent2 = EnchantmentHelper.getEnchantments(itemStack2);
			EnchantmentHelper.apply(
				itemStack3,
				builder -> wrapperLookup.getWrapperOrThrow(RegistryKeys.ENCHANTMENT)
						.streamEntries()
						.filter(enchantment -> enchantment.isIn(EnchantmentTags.CURSE))
						.forEach(enchantment -> {
							int ix = Math.max(itemEnchantmentsComponent.getLevel(enchantment), itemEnchantmentsComponent2.getLevel(enchantment));
							if (ix > 0) {
								builder.add(enchantment, ix);
							}
						})
			);
			return itemStack3;
		}
	}

	@Override
	public boolean fits(int width, int height) {
		return width * height >= 2;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeSerializer.REPAIR_ITEM;
	}
}
