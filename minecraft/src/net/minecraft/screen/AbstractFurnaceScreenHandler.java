package net.minecraft.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.RecipeInputProvider;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.screen.slot.FurnaceFuelSlot;
import net.minecraft.screen.slot.FurnaceOutputSlot;
import net.minecraft.screen.slot.Slot;
import net.minecraft.world.World;

public abstract class AbstractFurnaceScreenHandler extends AbstractRecipeScreenHandler<Inventory> {
	private final Inventory inventory;
	private final PropertyDelegate propertyDelegate;
	protected final World world;
	private final RecipeType<? extends AbstractCookingRecipe> recipeType;
	private final RecipeBookCategory category;

	protected AbstractFurnaceScreenHandler(
		ScreenHandlerType<?> type,
		RecipeType<? extends AbstractCookingRecipe> recipeType,
		RecipeBookCategory recipeBookCategory,
		int i,
		PlayerInventory playerInventory
	) {
		this(type, recipeType, recipeBookCategory, i, playerInventory, new SimpleInventory(3), new ArrayPropertyDelegate(4));
	}

	protected AbstractFurnaceScreenHandler(
		ScreenHandlerType<?> type,
		RecipeType<? extends AbstractCookingRecipe> recipeType,
		RecipeBookCategory recipeBookCategory,
		int i,
		PlayerInventory playerInventory,
		Inventory inventory,
		PropertyDelegate propertyDelegate
	) {
		super(type, i);
		this.recipeType = recipeType;
		this.category = recipeBookCategory;
		checkSize(inventory, 3);
		checkDataCount(propertyDelegate, 4);
		this.inventory = inventory;
		this.propertyDelegate = propertyDelegate;
		this.world = playerInventory.player.world;
		this.addSlot(new Slot(inventory, 0, 56, 17));
		this.addSlot(new FurnaceFuelSlot(this, inventory, 1, 56, 53));
		this.addSlot(new FurnaceOutputSlot(playerInventory.player, inventory, 2, 116, 35));

		for (int j = 0; j < 3; j++) {
			for (int k = 0; k < 9; k++) {
				this.addSlot(new Slot(playerInventory, k + j * 9 + 9, 8 + k * 18, 84 + j * 18));
			}
		}

		for (int j = 0; j < 9; j++) {
			this.addSlot(new Slot(playerInventory, j, 8 + j * 18, 142));
		}

		this.addProperties(propertyDelegate);
	}

	@Override
	public void populateRecipeFinder(RecipeFinder finder) {
		if (this.inventory instanceof RecipeInputProvider) {
			((RecipeInputProvider)this.inventory).provideRecipeInputs(finder);
		}
	}

	@Override
	public void clearCraftingSlots() {
		this.getSlot(0).setStack(ItemStack.EMPTY);
		this.getSlot(2).setStack(ItemStack.EMPTY);
	}

	@Override
	public boolean matches(Recipe<? super Inventory> recipe) {
		return recipe.matches(this.inventory, this.world);
	}

	@Override
	public int getCraftingResultSlotIndex() {
		return 2;
	}

	@Override
	public int getCraftingWidth() {
		return 1;
	}

	@Override
	public int getCraftingHeight() {
		return 1;
	}

	@Override
	public int getCraftingSlotCount() {
		return 3;
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return this.inventory.canPlayerUse(player);
	}

	@Override
	public ItemStack transferSlot(PlayerEntity player, int index) {
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		if (slot != null && slot.hasStack()) {
			ItemStack itemStack2 = slot.getStack();
			itemStack = itemStack2.copy();
			if (index == 2) {
				if (!this.insertItem(itemStack2, 3, 39, true)) {
					return ItemStack.EMPTY;
				}

				slot.onStackChanged(itemStack2, itemStack);
			} else if (index != 1 && index != 0) {
				if (this.isSmeltable(itemStack2)) {
					if (!this.insertItem(itemStack2, 0, 1, false)) {
						return ItemStack.EMPTY;
					}
				} else if (this.isFuel(itemStack2)) {
					if (!this.insertItem(itemStack2, 1, 2, false)) {
						return ItemStack.EMPTY;
					}
				} else if (index >= 3 && index < 30) {
					if (!this.insertItem(itemStack2, 30, 39, false)) {
						return ItemStack.EMPTY;
					}
				} else if (index >= 30 && index < 39 && !this.insertItem(itemStack2, 3, 30, false)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.insertItem(itemStack2, 3, 39, false)) {
				return ItemStack.EMPTY;
			}

			if (itemStack2.isEmpty()) {
				slot.setStack(ItemStack.EMPTY);
			} else {
				slot.markDirty();
			}

			if (itemStack2.getCount() == itemStack.getCount()) {
				return ItemStack.EMPTY;
			}

			slot.onTakeItem(player, itemStack2);
		}

		return itemStack;
	}

	protected boolean isSmeltable(ItemStack itemStack) {
		return this.world.getRecipeManager().getFirstMatch(this.recipeType, new SimpleInventory(itemStack), this.world).isPresent();
	}

	public boolean isFuel(ItemStack itemStack) {
		return AbstractFurnaceBlockEntity.canUseAsFuel(itemStack);
	}

	@Environment(EnvType.CLIENT)
	public int getCookProgress() {
		int i = this.propertyDelegate.get(2);
		int j = this.propertyDelegate.get(3);
		return j != 0 && i != 0 ? i * 24 / j : 0;
	}

	@Environment(EnvType.CLIENT)
	public int getFuelProgress() {
		int i = this.propertyDelegate.get(1);
		if (i == 0) {
			i = 200;
		}

		return this.propertyDelegate.get(0) * 13 / i;
	}

	@Environment(EnvType.CLIENT)
	public boolean isBurning() {
		return this.propertyDelegate.get(0) > 0;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public RecipeBookCategory getCategory() {
		return this.category;
	}

	@Override
	public boolean method_32339(int i) {
		return i != 1;
	}
}
