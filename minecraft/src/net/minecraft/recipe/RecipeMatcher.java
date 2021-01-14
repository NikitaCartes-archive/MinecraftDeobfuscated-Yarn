package net.minecraft.recipe;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntAVLTreeSet;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.BitSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;

/**
 * Matching class that matches a recipe to its required resources.
 * This specifically does not check patterns (See {@link ShapedRecipe} for that).
 */
public class RecipeMatcher {
	public final Int2IntMap inputs = new Int2IntOpenHashMap();

	/**
	 * Adds a full item stack to the pool of available resources.
	 * 
	 * <p>This is equivalent to calling {@code addInput(stack, Item.DEFAULT_MAX_COUNT)}.</p>
	 */
	public void addUnenchantedInput(ItemStack stack) {
		if (!stack.isDamaged() && !stack.hasEnchantments() && !stack.hasCustomName()) {
			this.addInput(stack);
		}
	}

	/**
	 * Adds a full item stack to the pool of available resources.
	 * 
	 * <p>This is equivalent to calling {@code addInput(stack, Item.DEFAULT_MAX_COUNT)}.</p>
	 */
	public void addInput(ItemStack stack) {
		this.method_20478(stack, 64);
	}

	public void method_20478(ItemStack itemStack, int i) {
		if (!itemStack.isEmpty()) {
			int j = getItemId(itemStack);
			int k = Math.min(i, itemStack.getCount());
			this.addInput(j, k);
		}
	}

	public static int getItemId(ItemStack stack) {
		return Registry.ITEM.getRawId(stack.getItem());
	}

	/**
	 * Determines whether a raw item id is present in the pool of crafting resources.
	 */
	private boolean contains(int itemId) {
		return this.inputs.get(itemId) > 0;
	}

	/**
	 * Consumes a resource from the pool of available items.
	 * 
	 * @param itemId the raw id of the item being consumed
	 * @param count the number of times that item must be consumed
	 */
	private int consume(int itemId, int count) {
		int i = this.inputs.get(itemId);
		if (i >= count) {
			this.inputs.put(itemId, i - count);
			return itemId;
		} else {
			return 0;
		}
	}

	/**
	 * Adds an input to be used for recipe matching.
	 * 
	 * @param itemId the raw ID of the item to match
	 * @param count the item's count
	 */
	private void addInput(int itemId, int count) {
		this.inputs.put(itemId, this.inputs.get(itemId) + count);
	}

	/**
	 * Attempts to match the recipe against the collected inputs.
	 * Assumes only one output is required.
	 * 
	 * @param recipe the recipe to match against
	 * @param output optional output list of item ids that were matched whilst evaluating the recipe conditions
	 */
	public boolean match(Recipe<?> recipe, @Nullable IntList output) {
		return this.match(recipe, output, 1);
	}

	/**
	 * Attempts to match the recipe against the collected inputs. Will only succeed if there has been enough
	 * resources gathered to produce the requested number of outputs.
	 * 
	 * @param recipe the recipe to match against
	 * @param output optional output list of item ids that were matched whilst evaluating the recipe conditions
	 * @param multiplier the number of expected outputs
	 */
	public boolean match(Recipe<?> recipe, @Nullable IntList output, int multiplier) {
		return new RecipeMatcher.Filter(recipe).find(multiplier, output);
	}

	/**
	 * Determines the number of crafts that can be produced for a recipe using the
	 * collected resources available to this crafter.
	 * 
	 * @param recipe the recipe to match against
	 * @param output optional output list of item ids that were matched whilst evaluating the recipe conditions
	 */
	public int countCrafts(Recipe<?> recipe, @Nullable IntList output) {
		return this.countCrafts(recipe, Integer.MAX_VALUE, output);
	}

	/**
	 * Determines the number of crafts that can be produced for a recipe using the
	 * collected resources available to this crafter.
	 * 
	 * @param recipe the recipe to match against
	 * @param output optional output list of item ids that were matched whilst evaluating the recipe conditions
	 */
	public int countCrafts(Recipe<?> recipe, int limit, @Nullable IntList output) {
		return new RecipeMatcher.Filter(recipe).countCrafts(limit, output);
	}

	public static ItemStack getStackFromId(int itemId) {
		return itemId == 0 ? ItemStack.EMPTY : new ItemStack(Item.byRawId(itemId));
	}

	public void clear() {
		this.inputs.clear();
	}

	class Filter {
		private final Recipe<?> recipe;
		private final List<Ingredient> ingredients = Lists.<Ingredient>newArrayList();
		private final int ingredientCount;
		private final int[] field_7551;
		private final int field_7553;
		private final BitSet field_7558;
		private final IntList field_7557 = new IntArrayList();

		public Filter(Recipe<?> recipe2) {
			this.recipe = recipe2;
			this.ingredients.addAll(recipe2.getIngredients());
			this.ingredients.removeIf(Ingredient::isEmpty);
			this.ingredientCount = this.ingredients.size();
			this.field_7551 = this.method_7422();
			this.field_7553 = this.field_7551.length;
			this.field_7558 = new BitSet(this.ingredientCount + this.field_7553 + this.ingredientCount + this.ingredientCount * this.field_7553);

			for (int i = 0; i < this.ingredients.size(); i++) {
				IntList intList = ((Ingredient)this.ingredients.get(i)).getMatchingItemIds();

				for (int j = 0; j < this.field_7553; j++) {
					if (intList.contains(this.field_7551[j])) {
						this.field_7558.set(this.method_7420(true, j, i));
					}
				}
			}
		}

		public boolean find(int amount, @Nullable IntList outMatchingInputIds) {
			if (amount <= 0) {
				return true;
			} else {
				int i;
				for (i = 0; this.method_7423(amount); i++) {
					RecipeMatcher.this.consume(this.field_7551[this.field_7557.getInt(0)], amount);
					int j = this.field_7557.size() - 1;
					this.method_7421(this.field_7557.getInt(j));

					for (int k = 0; k < j; k++) {
						this.method_7414((k & 1) == 0, this.field_7557.get(k), this.field_7557.get(k + 1));
					}

					this.field_7557.clear();
					this.field_7558.clear(0, this.ingredientCount + this.field_7553);
				}

				boolean bl = i == this.ingredientCount;
				boolean bl2 = bl && outMatchingInputIds != null;
				if (bl2) {
					outMatchingInputIds.clear();
				}

				this.field_7558.clear(0, this.ingredientCount + this.field_7553 + this.ingredientCount);
				int l = 0;
				List<Ingredient> list = this.recipe.getIngredients();

				for (int m = 0; m < list.size(); m++) {
					if (bl2 && ((Ingredient)list.get(m)).isEmpty()) {
						outMatchingInputIds.add(0);
					} else {
						for (int n = 0; n < this.field_7553; n++) {
							if (this.method_7425(false, l, n)) {
								this.method_7414(true, n, l);
								RecipeMatcher.this.addInput(this.field_7551[n], amount);
								if (bl2) {
									outMatchingInputIds.add(this.field_7551[n]);
								}
							}
						}

						l++;
					}
				}

				return bl;
			}
		}

		private int[] method_7422() {
			IntCollection intCollection = new IntAVLTreeSet();

			for (Ingredient ingredient : this.ingredients) {
				intCollection.addAll(ingredient.getMatchingItemIds());
			}

			IntIterator intIterator = intCollection.iterator();

			while (intIterator.hasNext()) {
				if (!RecipeMatcher.this.contains(intIterator.nextInt())) {
					intIterator.remove();
				}
			}

			return intCollection.toIntArray();
		}

		private boolean method_7423(int i) {
			int j = this.field_7553;

			for (int k = 0; k < j; k++) {
				if (RecipeMatcher.this.inputs.get(this.field_7551[k]) >= i) {
					this.method_7413(false, k);

					while (!this.field_7557.isEmpty()) {
						int l = this.field_7557.size();
						boolean bl = (l & 1) == 1;
						int m = this.field_7557.getInt(l - 1);
						if (!bl && !this.method_7416(m)) {
							break;
						}

						int n = bl ? this.ingredientCount : j;
						int o = 0;

						while (true) {
							if (o < n) {
								if (this.method_7426(bl, o) || !this.method_7418(bl, m, o) || !this.method_7425(bl, m, o)) {
									o++;
									continue;
								}

								this.method_7413(bl, o);
							}

							o = this.field_7557.size();
							if (o == l) {
								this.field_7557.removeInt(o - 1);
							}
							break;
						}
					}

					if (!this.field_7557.isEmpty()) {
						return true;
					}
				}
			}

			return false;
		}

		private boolean method_7416(int i) {
			return this.field_7558.get(this.method_7419(i));
		}

		private void method_7421(int i) {
			this.field_7558.set(this.method_7419(i));
		}

		private int method_7419(int i) {
			return this.ingredientCount + this.field_7553 + i;
		}

		private boolean method_7418(boolean bl, int i, int j) {
			return this.field_7558.get(this.method_7420(bl, i, j));
		}

		private boolean method_7425(boolean bl, int i, int j) {
			return bl != this.field_7558.get(1 + this.method_7420(bl, i, j));
		}

		private void method_7414(boolean bl, int i, int j) {
			this.field_7558.flip(1 + this.method_7420(bl, i, j));
		}

		private int method_7420(boolean bl, int i, int j) {
			int k = bl ? i * this.ingredientCount + j : j * this.ingredientCount + i;
			return this.ingredientCount + this.field_7553 + this.ingredientCount + 2 * k;
		}

		private void method_7413(boolean bl, int i) {
			this.field_7558.set(this.method_7424(bl, i));
			this.field_7557.add(i);
		}

		private boolean method_7426(boolean bl, int i) {
			return this.field_7558.get(this.method_7424(bl, i));
		}

		private int method_7424(boolean bl, int i) {
			return (bl ? 0 : this.ingredientCount) + i;
		}

		public int countCrafts(int limit, @Nullable IntList outMatchingInputIds) {
			int i = 0;
			int j = Math.min(limit, this.method_7415()) + 1;

			while (true) {
				int k = (i + j) / 2;
				if (this.find(k, null)) {
					if (j - i <= 1) {
						if (k > 0) {
							this.find(k, outMatchingInputIds);
						}

						return k;
					}

					i = k;
				} else {
					j = k;
				}
			}
		}

		private int method_7415() {
			int i = Integer.MAX_VALUE;

			for (Ingredient ingredient : this.ingredients) {
				int j = 0;

				for (int k : ingredient.getMatchingItemIds()) {
					j = Math.max(j, RecipeMatcher.this.inputs.get(k));
				}

				if (i > 0) {
					i = Math.min(i, j);
				}
			}

			return i;
		}
	}
}
