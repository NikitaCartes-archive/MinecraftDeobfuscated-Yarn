package net.minecraft;

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
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.registry.Registry;

public class class_1662 {
	public final Int2IntMap field_7550 = new Int2IntOpenHashMap();

	public void method_7404(ItemStack itemStack) {
		if (!itemStack.isDamaged() && !itemStack.hasEnchantments() && !itemStack.hasDisplayName()) {
			this.method_7400(itemStack);
		}
	}

	public void method_7400(ItemStack itemStack) {
		if (!itemStack.isEmpty()) {
			int i = method_7408(itemStack);
			int j = itemStack.getAmount();
			this.method_7401(i, j);
		}
	}

	public static int method_7408(ItemStack itemStack) {
		return Registry.ITEM.getRawId(itemStack.getItem());
	}

	public boolean method_7410(int i) {
		return this.field_7550.get(i) > 0;
	}

	public int method_7411(int i, int j) {
		int k = this.field_7550.get(i);
		if (k >= j) {
			this.field_7550.put(i, k - j);
			return i;
		} else {
			return 0;
		}
	}

	private void method_7401(int i, int j) {
		this.field_7550.put(i, this.field_7550.get(i) + j);
	}

	public boolean method_7402(Recipe recipe, @Nullable IntList intList) {
		return this.method_7406(recipe, intList, 1);
	}

	public boolean method_7406(Recipe recipe, @Nullable IntList intList, int i) {
		return new class_1662.class_1663(recipe).method_7417(i, intList);
	}

	public int method_7407(Recipe recipe, @Nullable IntList intList) {
		return this.method_7403(recipe, Integer.MAX_VALUE, intList);
	}

	public int method_7403(Recipe recipe, int i, @Nullable IntList intList) {
		return new class_1662.class_1663(recipe).method_7427(i, intList);
	}

	public static ItemStack method_7405(int i) {
		return i == 0 ? ItemStack.EMPTY : new ItemStack(Item.byRawId(i));
	}

	public void method_7409() {
		this.field_7550.clear();
	}

	class class_1663 {
		private final Recipe field_7555;
		private final List<Ingredient> field_7552 = Lists.<Ingredient>newArrayList();
		private final int field_7556;
		private final int[] field_7551;
		private final int field_7553;
		private final BitSet field_7558;
		private final IntList field_7557 = new IntArrayList();

		public class_1663(Recipe recipe) {
			this.field_7555 = recipe;
			this.field_7552.addAll(recipe.getPreviewInputs());
			this.field_7552.removeIf(Ingredient::method_8103);
			this.field_7556 = this.field_7552.size();
			this.field_7551 = this.method_7422();
			this.field_7553 = this.field_7551.length;
			this.field_7558 = new BitSet(this.field_7556 + this.field_7553 + this.field_7556 + this.field_7556 * this.field_7553);

			for (int i = 0; i < this.field_7552.size(); i++) {
				IntList intList = ((Ingredient)this.field_7552.get(i)).method_8100();

				for (int j = 0; j < this.field_7553; j++) {
					if (intList.contains(this.field_7551[j])) {
						this.field_7558.set(this.method_7420(true, j, i));
					}
				}
			}
		}

		public boolean method_7417(int i, @Nullable IntList intList) {
			if (i <= 0) {
				return true;
			} else {
				int j;
				for (j = 0; this.method_7423(i); j++) {
					class_1662.this.method_7411(this.field_7551[this.field_7557.getInt(0)], i);
					int k = this.field_7557.size() - 1;
					this.method_7421(this.field_7557.getInt(k));

					for (int l = 0; l < k; l++) {
						this.method_7414((l & 1) == 0, this.field_7557.get(l), this.field_7557.get(l + 1));
					}

					this.field_7557.clear();
					this.field_7558.clear(0, this.field_7556 + this.field_7553);
				}

				boolean bl = j == this.field_7556;
				boolean bl2 = bl && intList != null;
				if (bl2) {
					intList.clear();
				}

				this.field_7558.clear(0, this.field_7556 + this.field_7553 + this.field_7556);
				int m = 0;
				List<Ingredient> list = this.field_7555.getPreviewInputs();

				for (int n = 0; n < list.size(); n++) {
					if (bl2 && ((Ingredient)list.get(n)).method_8103()) {
						intList.add(0);
					} else {
						for (int o = 0; o < this.field_7553; o++) {
							if (this.method_7425(false, m, o)) {
								this.method_7414(true, o, m);
								class_1662.this.method_7401(this.field_7551[o], i);
								if (bl2) {
									intList.add(this.field_7551[o]);
								}
							}
						}

						m++;
					}
				}

				return bl;
			}
		}

		private int[] method_7422() {
			IntCollection intCollection = new IntAVLTreeSet();

			for (Ingredient ingredient : this.field_7552) {
				intCollection.addAll(ingredient.method_8100());
			}

			IntIterator intIterator = intCollection.iterator();

			while (intIterator.hasNext()) {
				if (!class_1662.this.method_7410(intIterator.nextInt())) {
					intIterator.remove();
				}
			}

			return intCollection.toIntArray();
		}

		private boolean method_7423(int i) {
			int j = this.field_7553;

			for (int k = 0; k < j; k++) {
				if (class_1662.this.field_7550.get(this.field_7551[k]) >= i) {
					this.method_7413(false, k);

					while (!this.field_7557.isEmpty()) {
						int l = this.field_7557.size();
						boolean bl = (l & 1) == 1;
						int m = this.field_7557.getInt(l - 1);
						if (!bl && !this.method_7416(m)) {
							break;
						}

						int n = bl ? this.field_7556 : j;
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
			return this.field_7556 + this.field_7553 + i;
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
			int k = bl ? i * this.field_7556 + j : j * this.field_7556 + i;
			return this.field_7556 + this.field_7553 + this.field_7556 + 2 * k;
		}

		private void method_7413(boolean bl, int i) {
			this.field_7558.set(this.method_7424(bl, i));
			this.field_7557.add(i);
		}

		private boolean method_7426(boolean bl, int i) {
			return this.field_7558.get(this.method_7424(bl, i));
		}

		private int method_7424(boolean bl, int i) {
			return (bl ? 0 : this.field_7556) + i;
		}

		public int method_7427(int i, @Nullable IntList intList) {
			int j = 0;
			int k = Math.min(i, this.method_7415()) + 1;

			while (true) {
				int l = (j + k) / 2;
				if (this.method_7417(l, null)) {
					if (k - j <= 1) {
						if (l > 0) {
							this.method_7417(l, intList);
						}

						return l;
					}

					j = l;
				} else {
					k = l;
				}
			}
		}

		private int method_7415() {
			int i = Integer.MAX_VALUE;

			for (Ingredient ingredient : this.field_7552) {
				int j = 0;

				for (int k : ingredient.method_8100()) {
					j = Math.max(j, class_1662.this.field_7550.get(k));
				}

				if (i > 0) {
					i = Math.min(i, j);
				}
			}

			return i;
		}
	}
}
