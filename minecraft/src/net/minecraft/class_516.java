package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.book.RecipeBook;

@Environment(EnvType.CLIENT)
public class class_516 {
	private final List<Recipe> field_3144 = Lists.<Recipe>newArrayList();
	private final Set<Recipe> field_3146 = Sets.<Recipe>newHashSet();
	private final Set<Recipe> field_3145 = Sets.<Recipe>newHashSet();
	private final Set<Recipe> field_3147 = Sets.<Recipe>newHashSet();
	private boolean field_3148 = true;

	public boolean method_2652() {
		return !this.field_3147.isEmpty();
	}

	public void method_2647(RecipeBook recipeBook) {
		for (Recipe recipe : this.field_3144) {
			if (recipeBook.method_14878(recipe)) {
				this.field_3147.add(recipe);
			}
		}
	}

	public void method_2649(class_1662 arg, int i, int j, RecipeBook recipeBook) {
		for (int k = 0; k < this.field_3144.size(); k++) {
			Recipe recipe = (Recipe)this.field_3144.get(k);
			boolean bl = recipe.fits(i, j) && recipeBook.method_14878(recipe);
			if (bl) {
				this.field_3145.add(recipe);
			} else {
				this.field_3145.remove(recipe);
			}

			if (bl && arg.method_7402(recipe, null)) {
				this.field_3146.add(recipe);
			} else {
				this.field_3146.remove(recipe);
			}
		}
	}

	public boolean method_2653(Recipe recipe) {
		return this.field_3146.contains(recipe);
	}

	public boolean method_2655() {
		return !this.field_3146.isEmpty();
	}

	public boolean method_2657() {
		return !this.field_3145.isEmpty();
	}

	public List<Recipe> method_2650() {
		return this.field_3144;
	}

	public List<Recipe> method_2651(boolean bl) {
		List<Recipe> list = Lists.<Recipe>newArrayList();
		Set<Recipe> set = bl ? this.field_3146 : this.field_3145;

		for (Recipe recipe : this.field_3144) {
			if (set.contains(recipe)) {
				list.add(recipe);
			}
		}

		return list;
	}

	public List<Recipe> method_2648(boolean bl) {
		List<Recipe> list = Lists.<Recipe>newArrayList();

		for (Recipe recipe : this.field_3144) {
			if (this.field_3145.contains(recipe) && this.field_3146.contains(recipe) == bl) {
				list.add(recipe);
			}
		}

		return list;
	}

	public void method_2654(Recipe recipe) {
		this.field_3144.add(recipe);
		if (this.field_3148) {
			ItemStack itemStack = ((Recipe)this.field_3144.get(0)).getOutput();
			ItemStack itemStack2 = recipe.getOutput();
			this.field_3148 = ItemStack.areEqualIgnoreTags(itemStack, itemStack2) && ItemStack.areTagsEqual(itemStack, itemStack2);
		}
	}

	public boolean method_2656() {
		return this.field_3148;
	}
}
