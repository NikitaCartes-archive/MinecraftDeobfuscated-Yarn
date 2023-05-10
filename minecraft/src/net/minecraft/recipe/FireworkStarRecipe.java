package net.minecraft.recipe;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.DyeItem;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
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
	private static final Map<Item, FireworkRocketItem.Type> TYPE_MODIFIER_MAP = Util.make(Maps.<Item, FireworkRocketItem.Type>newHashMap(), typeModifiers -> {
		typeModifiers.put(Items.FIRE_CHARGE, FireworkRocketItem.Type.LARGE_BALL);
		typeModifiers.put(Items.FEATHER, FireworkRocketItem.Type.BURST);
		typeModifiers.put(Items.GOLD_NUGGET, FireworkRocketItem.Type.STAR);
		typeModifiers.put(Items.SKELETON_SKULL, FireworkRocketItem.Type.CREEPER);
		typeModifiers.put(Items.WITHER_SKELETON_SKULL, FireworkRocketItem.Type.CREEPER);
		typeModifiers.put(Items.CREEPER_HEAD, FireworkRocketItem.Type.CREEPER);
		typeModifiers.put(Items.PLAYER_HEAD, FireworkRocketItem.Type.CREEPER);
		typeModifiers.put(Items.DRAGON_HEAD, FireworkRocketItem.Type.CREEPER);
		typeModifiers.put(Items.ZOMBIE_HEAD, FireworkRocketItem.Type.CREEPER);
		typeModifiers.put(Items.PIGLIN_HEAD, FireworkRocketItem.Type.CREEPER);
	});
	private static final Ingredient GUNPOWDER = Ingredient.ofItems(Items.GUNPOWDER);

	public FireworkStarRecipe(Identifier identifier, CraftingRecipeCategory craftingRecipeCategory) {
		super(identifier, craftingRecipeCategory);
	}

	public boolean matches(RecipeInputInventory recipeInputInventory, World world) {
		boolean bl = false;
		boolean bl2 = false;
		boolean bl3 = false;
		boolean bl4 = false;
		boolean bl5 = false;

		for (int i = 0; i < recipeInputInventory.size(); i++) {
			ItemStack itemStack = recipeInputInventory.getStack(i);
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

	public ItemStack craft(RecipeInputInventory recipeInputInventory, DynamicRegistryManager dynamicRegistryManager) {
		ItemStack itemStack = new ItemStack(Items.FIREWORK_STAR);
		NbtCompound nbtCompound = itemStack.getOrCreateSubNbt("Explosion");
		FireworkRocketItem.Type type = FireworkRocketItem.Type.SMALL_BALL;
		List<Integer> list = Lists.<Integer>newArrayList();

		for (int i = 0; i < recipeInputInventory.size(); i++) {
			ItemStack itemStack2 = recipeInputInventory.getStack(i);
			if (!itemStack2.isEmpty()) {
				if (TYPE_MODIFIER.test(itemStack2)) {
					type = (FireworkRocketItem.Type)TYPE_MODIFIER_MAP.get(itemStack2.getItem());
				} else if (FLICKER_MODIFIER.test(itemStack2)) {
					nbtCompound.putBoolean("Flicker", true);
				} else if (TRAIL_MODIFIER.test(itemStack2)) {
					nbtCompound.putBoolean("Trail", true);
				} else if (itemStack2.getItem() instanceof DyeItem) {
					list.add(((DyeItem)itemStack2.getItem()).getColor().getFireworkColor());
				}
			}
		}

		nbtCompound.putIntArray("Colors", list);
		nbtCompound.putByte("Type", (byte)type.getId());
		return itemStack;
	}

	@Override
	public boolean fits(int width, int height) {
		return width * height >= 2;
	}

	@Override
	public ItemStack getOutput(DynamicRegistryManager registryManager) {
		return new ItemStack(Items.FIREWORK_STAR);
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeSerializer.FIREWORK_STAR;
	}
}
