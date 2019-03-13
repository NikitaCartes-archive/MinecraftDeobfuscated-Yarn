package net.minecraft.container;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.FurnaceInputSlotFiller;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.RecipeInputProvider;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.cooking.CookingRecipe;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

public abstract class AbstractFurnaceContainer extends CraftingContainer<Inventory> {
	private final Inventory inventory;
	private final PropertyDelegate field_17286;
	protected final World field_7822;
	private final RecipeType<? extends CookingRecipe> field_17494;

	protected AbstractFurnaceContainer(ContainerType<?> containerType, RecipeType<? extends CookingRecipe> recipeType, int i, PlayerInventory playerInventory) {
		this(containerType, recipeType, i, playerInventory, new BasicInventory(3), new ArrayPropertyDelegate(4));
	}

	protected AbstractFurnaceContainer(
		ContainerType<?> containerType,
		RecipeType<? extends CookingRecipe> recipeType,
		int i,
		PlayerInventory playerInventory,
		Inventory inventory,
		PropertyDelegate propertyDelegate
	) {
		super(containerType, i);
		this.field_17494 = recipeType;
		checkContainerSize(inventory, 3);
		method_17361(propertyDelegate, 4);
		this.inventory = inventory;
		this.field_17286 = propertyDelegate;
		this.field_7822 = playerInventory.field_7546.field_6002;
		this.method_7621(new Slot(inventory, 0, 56, 17));
		this.method_7621(new FurnaceFuelSlot(this, inventory, 1, 56, 53));
		this.method_7621(new FurnaceOutputSlot(playerInventory.field_7546, inventory, 2, 116, 35));

		for (int j = 0; j < 3; j++) {
			for (int k = 0; k < 9; k++) {
				this.method_7621(new Slot(playerInventory, k + j * 9 + 9, 8 + k * 18, 84 + j * 18));
			}
		}

		for (int j = 0; j < 9; j++) {
			this.method_7621(new Slot(playerInventory, j, 8 + j * 18, 142));
		}

		this.method_17360(propertyDelegate);
	}

	@Override
	public void populateRecipeFinder(RecipeFinder recipeFinder) {
		if (this.inventory instanceof RecipeInputProvider) {
			((RecipeInputProvider)this.inventory).provideRecipeInputs(recipeFinder);
		}
	}

	@Override
	public void clearCraftingSlots() {
		this.inventory.clear();
	}

	@Override
	public void method_17697(boolean bl, Recipe<?> recipe, ServerPlayerEntity serverPlayerEntity) {
		new FurnaceInputSlotFiller<>(this).method_12826(serverPlayerEntity, (Recipe<Inventory>)recipe, bl);
	}

	@Override
	public boolean method_7652(Recipe<? super Inventory> recipe) {
		return recipe.method_8115(this.inventory, this.field_7822);
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

	@Environment(EnvType.CLIENT)
	@Override
	public int getCraftingSlotCount() {
		return 3;
	}

	@Override
	public boolean canUse(PlayerEntity playerEntity) {
		return this.inventory.method_5443(playerEntity);
	}

	@Override
	public ItemStack method_7601(PlayerEntity playerEntity, int i) {
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot = (Slot)this.slotList.get(i);
		if (slot != null && slot.hasStack()) {
			ItemStack itemStack2 = slot.method_7677();
			itemStack = itemStack2.copy();
			if (i == 2) {
				if (!this.method_7616(itemStack2, 3, 39, true)) {
					return ItemStack.EMPTY;
				}

				slot.method_7670(itemStack2, itemStack);
			} else if (i != 1 && i != 0) {
				if (this.method_7640(itemStack2)) {
					if (!this.method_7616(itemStack2, 0, 1, false)) {
						return ItemStack.EMPTY;
					}
				} else if (this.method_16945(itemStack2)) {
					if (!this.method_7616(itemStack2, 1, 2, false)) {
						return ItemStack.EMPTY;
					}
				} else if (i >= 3 && i < 30) {
					if (!this.method_7616(itemStack2, 30, 39, false)) {
						return ItemStack.EMPTY;
					}
				} else if (i >= 30 && i < 39 && !this.method_7616(itemStack2, 3, 30, false)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.method_7616(itemStack2, 3, 39, false)) {
				return ItemStack.EMPTY;
			}

			if (itemStack2.isEmpty()) {
				slot.method_7673(ItemStack.EMPTY);
			} else {
				slot.markDirty();
			}

			if (itemStack2.getAmount() == itemStack.getAmount()) {
				return ItemStack.EMPTY;
			}

			slot.method_7667(playerEntity, itemStack2);
		}

		return itemStack;
	}

	protected boolean method_7640(ItemStack itemStack) {
		return this.field_7822.getRecipeManager().method_8132(this.field_17494, new BasicInventory(itemStack), this.field_7822).isPresent();
	}

	protected boolean method_16945(ItemStack itemStack) {
		return AbstractFurnaceBlockEntity.canUseAsFuel(itemStack);
	}

	@Environment(EnvType.CLIENT)
	public int getCookProgress() {
		int i = this.field_17286.get(2);
		int j = this.field_17286.get(3);
		return j != 0 && i != 0 ? i * 24 / j : 0;
	}

	@Environment(EnvType.CLIENT)
	public int getFuelProgress() {
		int i = this.field_17286.get(1);
		if (i == 0) {
			i = 200;
		}

		return this.field_17286.get(0) * 13 / i;
	}

	@Environment(EnvType.CLIENT)
	public boolean isBurning() {
		return this.field_17286.get(0) > 0;
	}
}
