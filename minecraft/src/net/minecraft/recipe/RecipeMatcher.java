package net.minecraft.recipe;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import java.util.BitSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;

/**
 * Matching class that matches a recipe to its required resources.
 * This specifically does not check patterns (See {@link ShapedRecipe} for that).
 */
public class RecipeMatcher<T> {
	public final Reference2IntOpenHashMap<T> available = new Reference2IntOpenHashMap<>();

	boolean hasAny(T input) {
		return this.available.getInt(input) > 0;
	}

	boolean hasAtLeast(T input, int minimum) {
		return this.available.getInt(input) >= minimum;
	}

	/**
	 * Consumes a resource from the pool of available items.
	 * 
	 * @param count the number of times that item must be consumed
	 */
	void consume(T input, int count) {
		int i = this.available.addTo(input, -count);
		if (i < count) {
			throw new IllegalStateException("Took " + count + " items, but only had " + i);
		}
	}

	/**
	 * Adds an input to be used for recipe matching.
	 * 
	 * @param count the item's count
	 */
	void addInput(T input, int count) {
		this.available.addTo(input, count);
	}

	public boolean match(List<RecipeMatcher.RawIngredient<T>> ingredients, int quantity, @Nullable RecipeMatcher.ItemCallback<T> itemCallback) {
		return new RecipeMatcher.Matcher(ingredients).match(quantity, itemCallback);
	}

	public int countCrafts(List<RecipeMatcher.RawIngredient<T>> ingredients, int max, @Nullable RecipeMatcher.ItemCallback<T> itemCallback) {
		return new RecipeMatcher.Matcher(ingredients).countCrafts(max, itemCallback);
	}

	public void clear() {
		this.available.clear();
	}

	public void add(T input, int count) {
		this.addInput(input, count);
	}

	@FunctionalInterface
	public interface ItemCallback<T> {
		void accept(T item);
	}

	class Matcher {
		private final List<RecipeMatcher.RawIngredient<T>> ingredients;
		private final int totalIngredients;
		private final List<T> requiredItems;
		private final int totalRequiredItems;
		private final BitSet bits;
		private final IntList ingredientItemLookup = new IntArrayList();

		public Matcher(final List<RecipeMatcher.RawIngredient<T>> ingredients) {
			this.ingredients = ingredients;
			this.totalIngredients = this.ingredients.size();
			this.requiredItems = this.createItemRequirementList();
			this.totalRequiredItems = this.requiredItems.size();
			this.bits = new BitSet(
				this.getVisitedIngredientIndexCount()
					+ this.getVisitedItemIndexCount()
					+ this.getRequirementIndexCount()
					+ this.getItemMatchIndexCount()
					+ this.getMissingIndexCount()
			);
			this.initItemMatch();
		}

		private void initItemMatch() {
			for (int i = 0; i < this.totalIngredients; i++) {
				List<T> list = ((RecipeMatcher.RawIngredient)this.ingredients.get(i)).allowedItems();

				for (int j = 0; j < this.totalRequiredItems; j++) {
					if (list.contains(this.requiredItems.get(j))) {
						this.setMatch(j, i);
					}
				}
			}
		}

		public boolean match(int quantity, @Nullable RecipeMatcher.ItemCallback<T> itemCallback) {
			if (quantity <= 0) {
				return true;
			} else {
				int i = 0;

				while (true) {
					IntList intList = this.tryFindIngredientItemLookup(quantity);
					if (intList == null) {
						boolean bl = i == this.totalIngredients;
						boolean bl2 = bl && itemCallback != null;
						this.clearVisited();
						this.clearRequirements();

						for (int k = 0; k < this.totalIngredients; k++) {
							for (int l = 0; l < this.totalRequiredItems; l++) {
								if (this.isMissing(l, k)) {
									this.markNotMissing(l, k);
									RecipeMatcher.this.addInput((T)this.requiredItems.get(l), quantity);
									if (bl2) {
										itemCallback.accept((T)this.requiredItems.get(l));
									}
									break;
								}
							}
						}

						assert this.bits.get(this.getMissingIndexOffset(), this.getMissingIndexOffset() + this.getMissingIndexCount()).isEmpty();

						return bl;
					}

					int j = intList.getInt(0);
					RecipeMatcher.this.consume((T)this.requiredItems.get(j), quantity);
					int k = intList.size() - 1;
					this.unfulfillRequirement(intList.getInt(k));
					i++;

					for (int lx = 0; lx < intList.size() - 1; lx++) {
						if (isItem(lx)) {
							int m = intList.getInt(lx);
							int n = intList.getInt(lx + 1);
							this.markMissing(m, n);
						} else {
							int m = intList.getInt(lx + 1);
							int n = intList.getInt(lx);
							this.markNotMissing(m, n);
						}
					}
				}
			}
		}

		private static boolean isItem(int index) {
			return (index & 1) == 0;
		}

		private List<T> createItemRequirementList() {
			Set<T> set = new ReferenceOpenHashSet<>();

			for (RecipeMatcher.RawIngredient<T> rawIngredient : this.ingredients) {
				set.addAll(rawIngredient.allowedItems());
			}

			set.removeIf(item -> !RecipeMatcher.this.hasAny((T)item));
			return List.copyOf(set);
		}

		@Nullable
		private IntList tryFindIngredientItemLookup(int min) {
			this.clearVisited();

			for (int i = 0; i < this.totalRequiredItems; i++) {
				if (RecipeMatcher.this.hasAtLeast((T)this.requiredItems.get(i), min)) {
					IntList intList = this.findIngredientItemLookup(i);
					if (intList != null) {
						return intList;
					}
				}
			}

			return null;
		}

		@Nullable
		private IntList findIngredientItemLookup(int itemIndex) {
			this.ingredientItemLookup.clear();
			this.markItemVisited(itemIndex);
			this.ingredientItemLookup.add(itemIndex);

			while (!this.ingredientItemLookup.isEmpty()) {
				int i = this.ingredientItemLookup.size();
				if (isItem(i - 1)) {
					int j = this.ingredientItemLookup.getInt(i - 1);

					for (int k = 0; k < this.totalIngredients; k++) {
						if (!this.hasVisitedIngredient(k) && this.matches(j, k) && !this.isMissing(j, k)) {
							this.markIngredientVisited(k);
							this.ingredientItemLookup.add(k);
							break;
						}
					}
				} else {
					int j = this.ingredientItemLookup.getInt(i - 1);
					if (!this.getRequirement(j)) {
						return this.ingredientItemLookup;
					}

					for (int kx = 0; kx < this.totalRequiredItems; kx++) {
						if (!this.isRequirementUnfulfilled(kx) && this.isMissing(kx, j)) {
							assert this.matches(kx, j);

							this.markItemVisited(kx);
							this.ingredientItemLookup.add(kx);
							break;
						}
					}
				}

				int j = this.ingredientItemLookup.size();
				if (j == i) {
					this.ingredientItemLookup.removeInt(j - 1);
				}
			}

			return null;
		}

		private int getVisitedIngredientIndexOffset() {
			return 0;
		}

		private int getVisitedIngredientIndexCount() {
			return this.totalIngredients;
		}

		private int getVisitedItemIndexOffset() {
			return this.getVisitedIngredientIndexOffset() + this.getVisitedIngredientIndexCount();
		}

		private int getVisitedItemIndexCount() {
			return this.totalRequiredItems;
		}

		private int getRequirementIndexOffset() {
			return this.getVisitedItemIndexOffset() + this.getVisitedItemIndexCount();
		}

		private int getRequirementIndexCount() {
			return this.totalIngredients;
		}

		private int getItemMatchIndexOffset() {
			return this.getRequirementIndexOffset() + this.getRequirementIndexCount();
		}

		private int getItemMatchIndexCount() {
			return this.totalIngredients * this.totalRequiredItems;
		}

		private int getMissingIndexOffset() {
			return this.getItemMatchIndexOffset() + this.getItemMatchIndexCount();
		}

		private int getMissingIndexCount() {
			return this.totalIngredients * this.totalRequiredItems;
		}

		private boolean getRequirement(int itemId) {
			return this.bits.get(this.getRequirementIndex(itemId));
		}

		private void unfulfillRequirement(int itemId) {
			this.bits.set(this.getRequirementIndex(itemId));
		}

		private int getRequirementIndex(int itemId) {
			assert itemId >= 0 && itemId < this.totalIngredients;

			return this.getRequirementIndexOffset() + itemId;
		}

		private void clearRequirements() {
			this.clear(this.getRequirementIndexOffset(), this.getRequirementIndexCount());
		}

		private void setMatch(int itemIndex, int ingredientIndex) {
			this.bits.set(this.getMatchIndex(itemIndex, ingredientIndex));
		}

		private boolean matches(int itemIndex, int ingredientIndex) {
			return this.bits.get(this.getMatchIndex(itemIndex, ingredientIndex));
		}

		private int getMatchIndex(int itemIndex, int ingredientIndex) {
			assert itemIndex >= 0 && itemIndex < this.totalRequiredItems;

			assert ingredientIndex >= 0 && ingredientIndex < this.totalIngredients;

			return this.getItemMatchIndexOffset() + itemIndex * this.totalIngredients + ingredientIndex;
		}

		private boolean isMissing(int itemIndex, int ingredientIndex) {
			return this.bits.get(this.getMissingIndex(itemIndex, ingredientIndex));
		}

		private void markMissing(int itemIndex, int ingredientIndex) {
			int i = this.getMissingIndex(itemIndex, ingredientIndex);

			assert !this.bits.get(i);

			this.bits.set(i);
		}

		private void markNotMissing(int itemIndex, int ingredientIndex) {
			int i = this.getMissingIndex(itemIndex, ingredientIndex);

			assert this.bits.get(i);

			this.bits.clear(i);
		}

		private int getMissingIndex(int itemIndex, int ingredientIndex) {
			assert itemIndex >= 0 && itemIndex < this.totalRequiredItems;

			assert ingredientIndex >= 0 && ingredientIndex < this.totalIngredients;

			return this.getMissingIndexOffset() + itemIndex * this.totalIngredients + ingredientIndex;
		}

		private void markIngredientVisited(int index) {
			this.bits.set(this.getVisitedIngredientIndex(index));
		}

		private boolean hasVisitedIngredient(int index) {
			return this.bits.get(this.getVisitedIngredientIndex(index));
		}

		private int getVisitedIngredientIndex(int index) {
			assert index >= 0 && index < this.totalIngredients;

			return this.getVisitedIngredientIndexOffset() + index;
		}

		private void markItemVisited(int index) {
			this.bits.set(this.getVisitedItemIndex(index));
		}

		private boolean isRequirementUnfulfilled(int index) {
			return this.bits.get(this.getVisitedItemIndex(index));
		}

		private int getVisitedItemIndex(int index) {
			assert index >= 0 && index < this.totalRequiredItems;

			return this.getVisitedItemIndexOffset() + index;
		}

		private void clearVisited() {
			this.clear(this.getVisitedIngredientIndexOffset(), this.getVisitedIngredientIndexCount());
			this.clear(this.getVisitedItemIndexOffset(), this.getVisitedItemIndexCount());
		}

		private void clear(int start, int offset) {
			this.bits.clear(start, start + offset);
		}

		public int countCrafts(int max, @Nullable RecipeMatcher.ItemCallback<T> itemCallback) {
			int i = 0;
			int j = Math.min(max, this.getMaximumCrafts()) + 1;

			while (true) {
				int k = (i + j) / 2;
				if (this.match(k, null)) {
					if (j - i <= 1) {
						if (k > 0) {
							this.match(k, itemCallback);
						}

						return k;
					}

					i = k;
				} else {
					j = k;
				}
			}
		}

		private int getMaximumCrafts() {
			int i = Integer.MAX_VALUE;

			for (RecipeMatcher.RawIngredient<T> rawIngredient : this.ingredients) {
				int j = 0;

				for (T object : rawIngredient.allowedItems()) {
					j = Math.max(j, RecipeMatcher.this.available.getInt(object));
				}

				if (i > 0) {
					i = Math.min(i, j);
				}
			}

			return i;
		}
	}

	public static record RawIngredient<T>(List<T> allowedItems) {
		public RawIngredient(List<T> allowedItems) {
			if (allowedItems.isEmpty()) {
				throw new IllegalArgumentException("Ingredients can't be empty");
			} else {
				this.allowedItems = allowedItems;
			}
		}
	}
}
