package net.minecraft.screen;

import java.util.List;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.InputSlotFiller;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.RecipeInputProvider;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.screen.slot.FurnaceFuelSlot;
import net.minecraft.screen.slot.FurnaceOutputSlot;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public abstract class AbstractFurnaceScreenHandler extends AbstractRecipeScreenHandler {
	public static final int field_30738 = 0;
	public static final int field_30739 = 1;
	public static final int field_30740 = 2;
	public static final int field_30741 = 3;
	public static final int field_30742 = 4;
	private static final int field_30743 = 3;
	private static final int field_30744 = 30;
	private static final int field_30745 = 30;
	private static final int field_30746 = 39;
	final Inventory inventory;
	private final PropertyDelegate propertyDelegate;
	protected final World world;
	private final RecipeType<? extends AbstractCookingRecipe> recipeType;
	private final RecipeBookCategory category;

	protected AbstractFurnaceScreenHandler(
		ScreenHandlerType<?> type, RecipeType<? extends AbstractCookingRecipe> recipeType, RecipeBookCategory category, int syncId, PlayerInventory playerInventory
	) {
		this(type, recipeType, category, syncId, playerInventory, new SimpleInventory(3), new ArrayPropertyDelegate(4));
	}

	protected AbstractFurnaceScreenHandler(
		ScreenHandlerType<?> type,
		RecipeType<? extends AbstractCookingRecipe> recipeType,
		RecipeBookCategory category,
		int syncId,
		PlayerInventory playerInventory,
		Inventory inventory,
		PropertyDelegate propertyDelegate
	) {
		super(type, syncId);
		this.recipeType = recipeType;
		this.category = category;
		checkSize(inventory, 3);
		checkDataCount(propertyDelegate, 4);
		this.inventory = inventory;
		this.propertyDelegate = propertyDelegate;
		this.world = playerInventory.player.getWorld();
		this.addSlot(new Slot(inventory, 0, 56, 17));
		this.addSlot(new FurnaceFuelSlot(this, inventory, 1, 56, 53));
		this.addSlot(new FurnaceOutputSlot(playerInventory.player, inventory, 2, 116, 35));
		this.addPlayerSlots(playerInventory, 8, 84);
		this.addProperties(propertyDelegate);
	}

	@Override
	public void populateRecipeFinder(RecipeFinder finder) {
		if (this.inventory instanceof RecipeInputProvider) {
			((RecipeInputProvider)this.inventory).provideRecipeInputs(finder);
		}
	}

	public Slot getOutputSlot() {
		return this.slots.get(2);
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return this.inventory.canPlayerUse(player);
	}

	@Override
	public ItemStack quickMove(PlayerEntity player, int slot) {
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot2 = this.slots.get(slot);
		if (slot2 != null && slot2.hasStack()) {
			ItemStack itemStack2 = slot2.getStack();
			itemStack = itemStack2.copy();
			if (slot == 2) {
				if (!this.insertItem(itemStack2, 3, 39, true)) {
					return ItemStack.EMPTY;
				}

				slot2.onQuickTransfer(itemStack2, itemStack);
			} else if (slot != 1 && slot != 0) {
				if (this.isSmeltable(itemStack2)) {
					if (!this.insertItem(itemStack2, 0, 1, false)) {
						return ItemStack.EMPTY;
					}
				} else if (this.isFuel(itemStack2)) {
					if (!this.insertItem(itemStack2, 1, 2, false)) {
						return ItemStack.EMPTY;
					}
				} else if (slot >= 3 && slot < 30) {
					if (!this.insertItem(itemStack2, 30, 39, false)) {
						return ItemStack.EMPTY;
					}
				} else if (slot >= 30 && slot < 39 && !this.insertItem(itemStack2, 3, 30, false)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.insertItem(itemStack2, 3, 39, false)) {
				return ItemStack.EMPTY;
			}

			if (itemStack2.isEmpty()) {
				slot2.setStack(ItemStack.EMPTY);
			} else {
				slot2.markDirty();
			}

			if (itemStack2.getCount() == itemStack.getCount()) {
				return ItemStack.EMPTY;
			}

			slot2.onTakeItem(player, itemStack2);
		}

		return itemStack;
	}

	protected boolean isSmeltable(ItemStack itemStack) {
		return this.world.getRecipeManager().getFirstMatch(this.recipeType, new SingleStackRecipeInput(itemStack), this.world).isPresent();
	}

	public boolean isFuel(ItemStack item) {
		return this.world.getFuelRegistry().isFuel(item);
	}

	public float getCookProgress() {
		int i = this.propertyDelegate.get(2);
		int j = this.propertyDelegate.get(3);
		return j != 0 && i != 0 ? MathHelper.clamp((float)i / (float)j, 0.0F, 1.0F) : 0.0F;
	}

	public float getFuelProgress() {
		int i = this.propertyDelegate.get(1);
		if (i == 0) {
			i = 200;
		}

		return MathHelper.clamp((float)this.propertyDelegate.get(0) / (float)i, 0.0F, 1.0F);
	}

	public boolean isBurning() {
		return this.propertyDelegate.get(0) > 0;
	}

	@Override
	public RecipeBookCategory getCategory() {
		return this.category;
	}

	@Override
	public AbstractRecipeScreenHandler.PostFillAction fillInputSlots(boolean craftAll, boolean creative, RecipeEntry<?> recipe, PlayerInventory inventory) {
		final List<Slot> list = List.of(this.getSlot(0), this.getSlot(2));
		return InputSlotFiller.fill(new InputSlotFiller.Handler<AbstractCookingRecipe>() {
			@Override
			public void populateRecipeFinder(RecipeFinder finder) {
				AbstractFurnaceScreenHandler.this.populateRecipeFinder(finder);
			}

			@Override
			public void clear() {
				list.forEach(slot -> slot.setStackNoCallbacks(ItemStack.EMPTY));
			}

			@Override
			public boolean matches(RecipeEntry<AbstractCookingRecipe> entry) {
				return entry.value().matches(new SingleStackRecipeInput(AbstractFurnaceScreenHandler.this.inventory.getStack(0)), AbstractFurnaceScreenHandler.this.world);
			}
		}, 1, 1, List.of(this.getSlot(0)), list, inventory, (RecipeEntry<AbstractCookingRecipe>)recipe, craftAll, creative);
	}
}
