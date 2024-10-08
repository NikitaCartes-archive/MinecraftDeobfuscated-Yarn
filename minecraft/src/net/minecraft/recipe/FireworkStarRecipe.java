package net.minecraft.recipe;

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
import net.minecraft.world.World;

public class FireworkStarRecipe extends SpecialCraftingRecipe {
	private static final Map<Item, FireworkExplosionComponent.Type> TYPE_MODIFIER_MAP = Map.of(
		Items.FIRE_CHARGE,
		FireworkExplosionComponent.Type.LARGE_BALL,
		Items.FEATHER,
		FireworkExplosionComponent.Type.BURST,
		Items.GOLD_NUGGET,
		FireworkExplosionComponent.Type.STAR,
		Items.SKELETON_SKULL,
		FireworkExplosionComponent.Type.CREEPER,
		Items.WITHER_SKELETON_SKULL,
		FireworkExplosionComponent.Type.CREEPER,
		Items.CREEPER_HEAD,
		FireworkExplosionComponent.Type.CREEPER,
		Items.PLAYER_HEAD,
		FireworkExplosionComponent.Type.CREEPER,
		Items.DRAGON_HEAD,
		FireworkExplosionComponent.Type.CREEPER,
		Items.ZOMBIE_HEAD,
		FireworkExplosionComponent.Type.CREEPER,
		Items.PIGLIN_HEAD,
		FireworkExplosionComponent.Type.CREEPER
	);
	private static final Ingredient TRAIL_MODIFIER = Ingredient.ofItem(Items.DIAMOND);
	private static final Ingredient FLICKER_MODIFIER = Ingredient.ofItem(Items.GLOWSTONE_DUST);
	private static final Ingredient GUNPOWDER = Ingredient.ofItem(Items.GUNPOWDER);

	public FireworkStarRecipe(CraftingRecipeCategory craftingRecipeCategory) {
		super(craftingRecipeCategory);
	}

	public boolean matches(CraftingRecipeInput craftingRecipeInput, World world) {
		if (craftingRecipeInput.getStackCount() < 2) {
			return false;
		} else {
			boolean bl = false;
			boolean bl2 = false;
			boolean bl3 = false;
			boolean bl4 = false;
			boolean bl5 = false;

			for (int i = 0; i < craftingRecipeInput.size(); i++) {
				ItemStack itemStack = craftingRecipeInput.getStackInSlot(i);
				if (!itemStack.isEmpty()) {
					if (TYPE_MODIFIER_MAP.containsKey(itemStack.getItem())) {
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
	}

	public ItemStack craft(CraftingRecipeInput craftingRecipeInput, RegistryWrapper.WrapperLookup wrapperLookup) {
		FireworkExplosionComponent.Type type = FireworkExplosionComponent.Type.SMALL_BALL;
		boolean bl = false;
		boolean bl2 = false;
		IntList intList = new IntArrayList();

		for (int i = 0; i < craftingRecipeInput.size(); i++) {
			ItemStack itemStack = craftingRecipeInput.getStackInSlot(i);
			if (!itemStack.isEmpty()) {
				FireworkExplosionComponent.Type type2 = (FireworkExplosionComponent.Type)TYPE_MODIFIER_MAP.get(itemStack.getItem());
				if (type2 != null) {
					type = type2;
				} else if (FLICKER_MODIFIER.test(itemStack)) {
					bl = true;
				} else if (TRAIL_MODIFIER.test(itemStack)) {
					bl2 = true;
				} else if (itemStack.getItem() instanceof DyeItem dyeItem) {
					intList.add(dyeItem.getColor().getFireworkColor());
				}
			}
		}

		ItemStack itemStack2 = new ItemStack(Items.FIREWORK_STAR);
		itemStack2.set(DataComponentTypes.FIREWORK_EXPLOSION, new FireworkExplosionComponent(type, intList, IntList.of(), bl2, bl));
		return itemStack2;
	}

	@Override
	public RecipeSerializer<FireworkStarRecipe> getSerializer() {
		return RecipeSerializer.FIREWORK_STAR;
	}
}
