package net.minecraft.recipe;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Map;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FireworkExplosionComponent;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Util;
import net.minecraft.world.World;

public class FireworkStarRecipe extends SpecialCraftingRecipe {
	private static final Ingredient TYPE_MODIFIER = Ingredient.ofItems(
		Items.FIRE_CHARGE,
		Items.FEATHER,
		Items.GOLD_NUGGET,
		Items.SKELETON_SKULL,
		Items.WITHER_SKELETON_SKULL,
		Items.CREEPER_HEAD,
		Items.PLAYER_HEAD,
		Items.DRAGON_HEAD,
		Items.ZOMBIE_HEAD,
		Items.PIGLIN_HEAD
	);
	private static final Ingredient TRAIL_MODIFIER = Ingredient.ofItems(Items.DIAMOND);
	private static final Ingredient FLICKER_MODIFIER = Ingredient.ofItems(Items.GLOWSTONE_DUST);
	private static final Map<Item, FireworkExplosionComponent.Type> TYPE_MODIFIER_MAP = Util.make(
		Maps.<Item, FireworkExplosionComponent.Type>newHashMap(), typeModifiers -> {
			typeModifiers.put(Items.FIRE_CHARGE, FireworkExplosionComponent.Type.LARGE_BALL);
			typeModifiers.put(Items.FEATHER, FireworkExplosionComponent.Type.BURST);
			typeModifiers.put(Items.GOLD_NUGGET, FireworkExplosionComponent.Type.STAR);
			typeModifiers.put(Items.SKELETON_SKULL, FireworkExplosionComponent.Type.CREEPER);
			typeModifiers.put(Items.WITHER_SKELETON_SKULL, FireworkExplosionComponent.Type.CREEPER);
			typeModifiers.put(Items.CREEPER_HEAD, FireworkExplosionComponent.Type.CREEPER);
			typeModifiers.put(Items.PLAYER_HEAD, FireworkExplosionComponent.Type.CREEPER);
			typeModifiers.put(Items.DRAGON_HEAD, FireworkExplosionComponent.Type.CREEPER);
			typeModifiers.put(Items.ZOMBIE_HEAD, FireworkExplosionComponent.Type.CREEPER);
			typeModifiers.put(Items.PIGLIN_HEAD, FireworkExplosionComponent.Type.CREEPER);
		}
	);
	private static final Ingredient GUNPOWDER = Ingredient.ofItems(Items.GUNPOWDER);

	public FireworkStarRecipe(CraftingRecipeCategory craftingRecipeCategory) {
		super(craftingRecipeCategory);
	}

	public boolean matches(CraftingRecipeInput craftingRecipeInput, World world) {
		boolean bl = false;
		boolean bl2 = false;
		boolean bl3 = false;
		boolean bl4 = false;
		boolean bl5 = false;

		for (int i = 0; i < craftingRecipeInput.getSize(); i++) {
			ItemStack itemStack = craftingRecipeInput.getStackInSlot(i);
			if (!itemStack.isEmpty()) {
				if (TYPE_MODIFIER.test(itemStack)) {
					if (bl3) {
						return false;
					}

					bl3 = true;
				} else if (FLICKER_MODIFIER.test(itemStack)) {
					if (bl5) {
						return false;
					}

					bl5 = true;
				} else if (TRAIL_MODIFIER.test(itemStack)) {
					if (bl4) {
						return false;
					}

					bl4 = true;
				} else if (GUNPOWDER.test(itemStack)) {
					if (bl) {
						return false;
					}

					bl = true;
				} else {
					if (!(itemStack.getItem() instanceof DyeItem)) {
						return false;
					}

					bl2 = true;
				}
			}
		}

		return bl && bl2;
	}

	public ItemStack craft(CraftingRecipeInput craftingRecipeInput, RegistryWrapper.WrapperLookup wrapperLookup) {
		FireworkExplosionComponent.Type type = FireworkExplosionComponent.Type.SMALL_BALL;
		boolean bl = false;
		boolean bl2 = false;
		IntList intList = new IntArrayList();

		for (int i = 0; i < craftingRecipeInput.getSize(); i++) {
			ItemStack itemStack = craftingRecipeInput.getStackInSlot(i);
			if (!itemStack.isEmpty()) {
				if (TYPE_MODIFIER.test(itemStack)) {
					type = (FireworkExplosionComponent.Type)TYPE_MODIFIER_MAP.get(itemStack.getItem());
				} else if (FLICKER_MODIFIER.test(itemStack)) {
					bl = true;
				} else if (TRAIL_MODIFIER.test(itemStack)) {
					bl2 = true;
				} else if (itemStack.getItem() instanceof DyeItem) {
					intList.add(((DyeItem)itemStack.getItem()).getColor().getFireworkColor());
				}
			}
		}

		ItemStack itemStack2 = new ItemStack(Items.FIREWORK_STAR);
		itemStack2.set(DataComponentTypes.FIREWORK_EXPLOSION, new FireworkExplosionComponent(type, intList, IntList.of(), bl2, bl));
		return itemStack2;
	}

	@Override
	public boolean fits(int width, int height) {
		return width * height >= 2;
	}

	@Override
	public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
		return new ItemStack(Items.FIREWORK_STAR);
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeSerializer.FIREWORK_STAR;
	}
}
