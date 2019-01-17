package net.minecraft.recipe.crafting;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.DyeItem;
import net.minecraft.item.FireworksItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;
import net.minecraft.world.World;

public class FireworkStarRecipe extends SpecialCraftingRecipe {
	private static final Ingredient field_9011 = Ingredient.ofItems(
		Items.field_8814,
		Items.field_8153,
		Items.field_8397,
		Items.field_8398,
		Items.field_8791,
		Items.field_8681,
		Items.field_8575,
		Items.field_8712,
		Items.field_8470
	);
	private static final Ingredient field_9010 = Ingredient.ofItems(Items.field_8477);
	private static final Ingredient field_9014 = Ingredient.ofItems(Items.field_8601);
	private static final Map<Item, FireworksItem.Type> field_9013 = SystemUtil.consume(Maps.<Item, FireworksItem.Type>newHashMap(), hashMap -> {
		hashMap.put(Items.field_8814, FireworksItem.Type.field_7977);
		hashMap.put(Items.field_8153, FireworksItem.Type.field_7970);
		hashMap.put(Items.field_8397, FireworksItem.Type.field_7973);
		hashMap.put(Items.field_8398, FireworksItem.Type.field_7974);
		hashMap.put(Items.field_8791, FireworksItem.Type.field_7974);
		hashMap.put(Items.field_8681, FireworksItem.Type.field_7974);
		hashMap.put(Items.field_8575, FireworksItem.Type.field_7974);
		hashMap.put(Items.field_8712, FireworksItem.Type.field_7974);
		hashMap.put(Items.field_8470, FireworksItem.Type.field_7974);
	});
	private static final Ingredient field_9012 = Ingredient.ofItems(Items.field_8054);

	public FireworkStarRecipe(Identifier identifier) {
		super(identifier);
	}

	public boolean method_17713(CraftingInventory craftingInventory, World world) {
		boolean bl = false;
		boolean bl2 = false;
		boolean bl3 = false;
		boolean bl4 = false;
		boolean bl5 = false;

		for (int i = 0; i < craftingInventory.getInvSize(); i++) {
			ItemStack itemStack = craftingInventory.getInvStack(i);
			if (!itemStack.isEmpty()) {
				if (field_9011.matches(itemStack)) {
					if (bl3) {
						return false;
					}

					bl3 = true;
				} else if (field_9014.matches(itemStack)) {
					if (bl5) {
						return false;
					}

					bl5 = true;
				} else if (field_9010.matches(itemStack)) {
					if (bl4) {
						return false;
					}

					bl4 = true;
				} else if (field_9012.matches(itemStack)) {
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

	public ItemStack method_17712(CraftingInventory craftingInventory) {
		ItemStack itemStack = new ItemStack(Items.field_8450);
		CompoundTag compoundTag = itemStack.getOrCreateSubCompoundTag("Explosion");
		FireworksItem.Type type = FireworksItem.Type.field_7976;
		List<Integer> list = Lists.<Integer>newArrayList();

		for (int i = 0; i < craftingInventory.getInvSize(); i++) {
			ItemStack itemStack2 = craftingInventory.getInvStack(i);
			if (!itemStack2.isEmpty()) {
				if (field_9011.matches(itemStack2)) {
					type = (FireworksItem.Type)field_9013.get(itemStack2.getItem());
				} else if (field_9014.matches(itemStack2)) {
					compoundTag.putBoolean("Flicker", true);
				} else if (field_9010.matches(itemStack2)) {
					compoundTag.putBoolean("Trail", true);
				} else if (itemStack2.getItem() instanceof DyeItem) {
					list.add(((DyeItem)itemStack2.getItem()).getColor().getFireworkColor());
				}
			}
		}

		compoundTag.putIntArray("Colors", list);
		compoundTag.putByte("Type", (byte)type.getId());
		return itemStack;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean fits(int i, int j) {
		return i * j >= 2;
	}

	@Override
	public ItemStack getOutput() {
		return new ItemStack(Items.field_8450);
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeSerializer.FIREWORK_STAR;
	}
}
