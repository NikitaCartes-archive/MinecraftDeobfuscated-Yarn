package net.minecraft.screen;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.entity.PotatoRefineryBlockEntity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.PotatoRefinementRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeInputProvider;
import net.minecraft.recipe.RecipeMatcher;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class PotatoRefineryScreenHandler extends AbstractRecipeScreenHandler<Inventory> {
	public static final int field_50567 = 0;
	public static final int field_50568 = 1;
	public static final int field_50569 = 2;
	public static final int field_50570 = 3;
	public static final int field_50571 = 4;
	public static final int field_50572 = 4;
	private static final int field_50574 = 4;
	private static final int field_50575 = 31;
	private static final int field_50576 = 31;
	private static final int field_50577 = 40;
	private final Inventory inventory;
	private final PropertyDelegate propertyDelegate;
	protected final World world;
	private final RecipeType<PotatoRefinementRecipe> recipeType;
	private final RecipeBookCategory recipeBookCategory;

	public PotatoRefineryScreenHandler(int syncId, PlayerInventory playerInventory) {
		this(syncId, playerInventory, new SimpleInventory(4), new ArrayPropertyDelegate(4));
	}

	public PotatoRefineryScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
		this(ScreenHandlerType.POTATO_REFINERY, RecipeType.POTATO_REFINEMENT, RecipeBookCategory.FURNACE, syncId, playerInventory, inventory, propertyDelegate);
	}

	private PotatoRefineryScreenHandler(
		ScreenHandlerType<?> type,
		RecipeType<PotatoRefinementRecipe> recipeType,
		RecipeBookCategory recipeBookCategory,
		int syncId,
		PlayerInventory playerInventory,
		Inventory inventory,
		PropertyDelegate propertyDelegate
	) {
		super(type, syncId);
		this.recipeType = recipeType;
		this.recipeBookCategory = recipeBookCategory;
		checkSize(inventory, 4);
		checkDataCount(propertyDelegate, 4);
		this.inventory = inventory;
		this.propertyDelegate = propertyDelegate;
		this.world = playerInventory.player.getWorld();
		this.addSlot(new Slot(inventory, 0, 52, 33));
		this.addSlot(new Slot(inventory, 2, 107, 36));
		this.addSlot(new PotatoRefineryScreenHandler.RefineryFuelSlot(inventory, 1, 52, 71));
		this.addSlot(new PotatoRefineryScreenHandler.RefineryOutputSLot(playerInventory.player, inventory, 3, 107, 62));

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 104 + i * 18));
			}
		}

		for (int i = 0; i < 9; i++) {
			this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 162));
		}

		this.addProperties(propertyDelegate);
	}

	@Override
	public void populateRecipeFinder(RecipeMatcher finder) {
		if (this.inventory instanceof RecipeInputProvider) {
			((RecipeInputProvider)this.inventory).provideRecipeInputs(finder);
		}
	}

	@Override
	public void clearCraftingSlots() {
		this.getSlot(0).setStackNoCallbacks(ItemStack.EMPTY);
		this.getSlot(2).setStackNoCallbacks(ItemStack.EMPTY);
		this.getSlot(3).setStackNoCallbacks(ItemStack.EMPTY);
	}

	@Override
	public boolean matches(RecipeEntry<? extends Recipe<Inventory>> recipe) {
		return recipe.value().matches(this.inventory, this.world);
	}

	@Override
	public int getCraftingResultSlotIndex() {
		return 3;
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
	public ItemStack quickMove(PlayerEntity player, int slot) {
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot2 = this.slots.get(slot);
		if (slot2 != null && slot2.hasStack()) {
			ItemStack itemStack2 = slot2.getStack();
			itemStack = itemStack2.copy();
			if (slot == 3) {
				if (!this.insertItem(itemStack2, 4, 40, true)) {
					return ItemStack.EMPTY;
				}

				slot2.onQuickTransfer(itemStack2, itemStack);
			} else if (slot != 1 && slot != 0) {
				if (this.isRefinable(itemStack2)) {
					if (!this.insertItem(itemStack2, 0, 1, false)) {
						return ItemStack.EMPTY;
					}
				} else if (this.isFuel(itemStack2)) {
					if (!this.insertItem(itemStack2, 1, 2, false)) {
						return ItemStack.EMPTY;
					}
				} else if (slot >= 4 && slot < 31) {
					if (!this.insertItem(itemStack2, 31, 40, false)) {
						return ItemStack.EMPTY;
					}
				} else if (slot >= 31 && slot < 40 && !this.insertItem(itemStack2, 4, 31, false)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.insertItem(itemStack2, 4, 40, false)) {
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

	protected boolean isRefinable(ItemStack stack) {
		return this.world.getRecipeManager().getFirstMatch(this.recipeType, new SimpleInventory(stack), this.world).isPresent();
	}

	protected boolean isFuel(ItemStack stack) {
		return PotatoRefineryBlockEntity.isFuel(stack);
	}

	public float getRefiningProgress() {
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
		return this.recipeBookCategory;
	}

	@Override
	public boolean canInsertIntoSlot(int index) {
		return index != 1;
	}

	class RefineryFuelSlot extends Slot {
		public RefineryFuelSlot(Inventory inventory, int index, int x, int y) {
			super(inventory, index, x, y);
		}

		@Override
		public boolean canInsert(ItemStack stack) {
			return PotatoRefineryScreenHandler.this.isFuel(stack) || isBucket(stack);
		}

		@Override
		public int getMaxItemCount(ItemStack stack) {
			return isBucket(stack) ? 1 : super.getMaxItemCount(stack);
		}

		public static boolean isBucket(ItemStack stack) {
			return stack.isOf(Items.BUCKET);
		}
	}

	static class RefineryOutputSLot extends Slot {
		private final PlayerEntity player;
		private int amount;

		public RefineryOutputSLot(PlayerEntity player, Inventory inventory, int index, int x, int y) {
			super(inventory, index, x, y);
			this.player = player;
		}

		@Override
		public boolean canInsert(ItemStack stack) {
			return false;
		}

		@Override
		public ItemStack takeStack(int amount) {
			if (this.hasStack()) {
				this.amount = this.amount + Math.min(amount, this.getStack().getCount());
			}

			return super.takeStack(amount);
		}

		@Override
		public void onTakeItem(PlayerEntity player, ItemStack stack) {
			this.onCrafted(stack);
			super.onTakeItem(player, stack);
		}

		@Override
		protected void onCrafted(ItemStack stack, int amount) {
			this.amount += amount;
			this.onCrafted(stack);
		}

		protected void onCrafted(ItemStack stack) {
			stack.onCraftByPlayer(this.player.getWorld(), this.player, this.amount);
			this.amount = 0;
			if (this.player instanceof ServerPlayerEntity serverPlayerEntity && this.inventory instanceof PotatoRefineryBlockEntity potatoRefineryBlockEntity) {
				Criteria.POTATO_REFINED.trigger(serverPlayerEntity, stack);
				float f = potatoRefineryBlockEntity.clearStoredExperience();
				if (f > 0.0F) {
					dropExperience(serverPlayerEntity.getServerWorld(), serverPlayerEntity.getPos(), 1, f);
				}
			}
		}

		private static void dropExperience(ServerWorld world, Vec3d pos, int i, float amount) {
			int j = MathHelper.floor((float)i * amount);
			float f = MathHelper.fractionalPart((float)i * amount);
			if (f != 0.0F && Math.random() < (double)f) {
				j++;
			}

			ExperienceOrbEntity.spawn(world, pos, j);
		}
	}
}
