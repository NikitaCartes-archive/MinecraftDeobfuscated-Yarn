package net.minecraft.recipe;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.slot.Slot;

public class InputSlotFiller<R extends Recipe<?>> {
	private static final int field_51523 = -1;
	private final PlayerInventory inventory;
	private final InputSlotFiller.Handler<R> handler;
	private final boolean craftAll;
	private final int width;
	private final int height;
	private final List<Slot> inputSlots;
	private final List<Slot> slotsToReturn;

	public static <I extends RecipeInput, R extends Recipe<I>> AbstractRecipeScreenHandler.PostFillAction fill(
		InputSlotFiller.Handler<R> handler,
		int width,
		int height,
		List<Slot> inputSlots,
		List<Slot> slotsToReturn,
		PlayerInventory inventory,
		RecipeEntry<R> recipe,
		boolean craftAll,
		boolean creative
	) {
		InputSlotFiller<R> inputSlotFiller = new InputSlotFiller<>(handler, inventory, craftAll, width, height, inputSlots, slotsToReturn);
		if (!creative && !inputSlotFiller.canReturnInputs()) {
			return AbstractRecipeScreenHandler.PostFillAction.NOTHING;
		} else {
			RecipeFinder recipeFinder = new RecipeFinder();
			inventory.populateRecipeFinder(recipeFinder);
			handler.populateRecipeFinder(recipeFinder);
			return inputSlotFiller.tryFill(recipe, recipeFinder);
		}
	}

	private InputSlotFiller(
		InputSlotFiller.Handler<R> handler, PlayerInventory inventory, boolean craftAll, int width, int height, List<Slot> inputSlots, List<Slot> slotsToReturn
	) {
		this.handler = handler;
		this.inventory = inventory;
		this.craftAll = craftAll;
		this.width = width;
		this.height = height;
		this.inputSlots = inputSlots;
		this.slotsToReturn = slotsToReturn;
	}

	private AbstractRecipeScreenHandler.PostFillAction tryFill(RecipeEntry<R> recipe, RecipeFinder finder) {
		if (finder.isCraftable(recipe.value(), null)) {
			this.fill(recipe, finder);
			this.inventory.markDirty();
			return AbstractRecipeScreenHandler.PostFillAction.NOTHING;
		} else {
			this.returnInputs();
			this.inventory.markDirty();
			return AbstractRecipeScreenHandler.PostFillAction.PLACE_GHOST_RECIPE;
		}
	}

	private void returnInputs() {
		for (Slot slot : this.slotsToReturn) {
			ItemStack itemStack = slot.getStack().copy();
			this.inventory.offer(itemStack, false);
			slot.setStackNoCallbacks(itemStack);
		}

		this.handler.clear();
	}

	private void fill(RecipeEntry<R> recipe, RecipeFinder finder) {
		boolean bl = this.handler.matches(recipe);
		int i = finder.countCrafts(recipe.value(), null);
		if (bl) {
			for (Slot slot : this.inputSlots) {
				ItemStack itemStack = slot.getStack();
				if (!itemStack.isEmpty() && Math.min(i, itemStack.getMaxCount()) < itemStack.getCount() + 1) {
					return;
				}
			}
		}

		int j = this.calculateCraftAmount(i, bl);
		List<RegistryEntry<Item>> list = new ArrayList();
		if (finder.isCraftable(recipe.value(), j, list::add)) {
			OptionalInt optionalInt = list.stream().mapToInt(item -> ((Item)item.value()).getMaxCount()).min();
			if (optionalInt.isPresent()) {
				j = Math.min(j, optionalInt.getAsInt());
			}

			list.clear();
			if (finder.isCraftable(recipe.value(), j, list::add)) {
				this.returnInputs();
				int k = j;
				RecipeGridAligner.alignRecipeToGrid(this.width, this.height, recipe, recipe.value().getIngredientPlacement().getPlacementSlots(), (slotx, index, x, y) -> {
					if (!slotx.isEmpty()) {
						Slot slot2 = (Slot)this.inputSlots.get(index);
						int jx = ((IngredientPlacement.PlacementSlot)slotx.get()).placerOutputPosition();
						int kx = k;

						while (kx > 0) {
							RegistryEntry<Item> registryEntry = (RegistryEntry<Item>)list.get(jx);
							kx = this.fillInputSlot(slot2, registryEntry, kx);
							if (kx == -1) {
								return;
							}
						}
					}
				});
			}
		}
	}

	private int calculateCraftAmount(int forCraftAll, boolean match) {
		if (this.craftAll) {
			return forCraftAll;
		} else if (match) {
			int i = Integer.MAX_VALUE;

			for (Slot slot : this.inputSlots) {
				ItemStack itemStack = slot.getStack();
				if (!itemStack.isEmpty() && i > itemStack.getCount()) {
					i = itemStack.getCount();
				}
			}

			if (i != Integer.MAX_VALUE) {
				i++;
			}

			return i;
		} else {
			return 1;
		}
	}

	private int fillInputSlot(Slot slot, RegistryEntry<Item> item, int count) {
		int i = this.inventory.getMatchingSlot(item);
		if (i == -1) {
			return -1;
		} else {
			ItemStack itemStack = this.inventory.getStack(i);
			int j;
			if (count < itemStack.getCount()) {
				this.inventory.removeStack(i, count);
				j = count;
			} else {
				this.inventory.removeStack(i);
				j = itemStack.getCount();
			}

			if (slot.getStack().isEmpty()) {
				slot.setStackNoCallbacks(itemStack.copyWithCount(j));
			} else {
				slot.getStack().increment(j);
			}

			return count - j;
		}
	}

	private boolean canReturnInputs() {
		List<ItemStack> list = Lists.<ItemStack>newArrayList();
		int i = this.getFreeInventorySlots();

		for (Slot slot : this.inputSlots) {
			ItemStack itemStack = slot.getStack().copy();
			if (!itemStack.isEmpty()) {
				int j = this.inventory.getOccupiedSlotWithRoomForStack(itemStack);
				if (j == -1 && list.size() <= i) {
					for (ItemStack itemStack2 : list) {
						if (ItemStack.areItemsEqual(itemStack2, itemStack)
							&& itemStack2.getCount() != itemStack2.getMaxCount()
							&& itemStack2.getCount() + itemStack.getCount() <= itemStack2.getMaxCount()) {
							itemStack2.increment(itemStack.getCount());
							itemStack.setCount(0);
							break;
						}
					}

					if (!itemStack.isEmpty()) {
						if (list.size() >= i) {
							return false;
						}

						list.add(itemStack);
					}
				} else if (j == -1) {
					return false;
				}
			}
		}

		return true;
	}

	private int getFreeInventorySlots() {
		int i = 0;

		for (ItemStack itemStack : this.inventory.main) {
			if (itemStack.isEmpty()) {
				i++;
			}
		}

		return i;
	}

	public interface Handler<T extends Recipe<?>> {
		void populateRecipeFinder(RecipeFinder finder);

		void clear();

		boolean matches(RecipeEntry<T> entry);
	}
}
