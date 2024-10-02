package net.minecraft.screen;

import java.util.List;
import java.util.Optional;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.StonecuttingRecipe;
import net.minecraft.recipe.display.CuttingRecipeDisplay;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class StonecutterScreenHandler extends ScreenHandler {
	public static final int INPUT_ID = 0;
	public static final int OUTPUT_ID = 1;
	private static final int INVENTORY_START = 2;
	private static final int INVENTORY_END = 29;
	private static final int OUTPUT_START = 29;
	private static final int OUTPUT_END = 38;
	private final ScreenHandlerContext context;
	final Property selectedRecipe = Property.create();
	private final World world;
	private CuttingRecipeDisplay.Grouping<StonecuttingRecipe> availableRecipes = CuttingRecipeDisplay.Grouping.empty();
	private ItemStack inputStack = ItemStack.EMPTY;
	long lastTakeTime;
	final Slot inputSlot;
	final Slot outputSlot;
	Runnable contentsChangedListener = () -> {
	};
	public final Inventory input = new SimpleInventory(1) {
		@Override
		public void markDirty() {
			super.markDirty();
			StonecutterScreenHandler.this.onContentChanged(this);
			StonecutterScreenHandler.this.contentsChangedListener.run();
		}
	};
	final CraftingResultInventory output = new CraftingResultInventory();

	public StonecutterScreenHandler(int syncId, PlayerInventory playerInventory) {
		this(syncId, playerInventory, ScreenHandlerContext.EMPTY);
	}

	public StonecutterScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
		super(ScreenHandlerType.STONECUTTER, syncId);
		this.context = context;
		this.world = playerInventory.player.getWorld();
		this.inputSlot = this.addSlot(new Slot(this.input, 0, 20, 33));
		this.outputSlot = this.addSlot(new Slot(this.output, 1, 143, 33) {
			@Override
			public boolean canInsert(ItemStack stack) {
				return false;
			}

			@Override
			public void onTakeItem(PlayerEntity player, ItemStack stack) {
				stack.onCraftByPlayer(player.getWorld(), player, stack.getCount());
				StonecutterScreenHandler.this.output.unlockLastRecipe(player, this.getInputStacks());
				ItemStack itemStack = StonecutterScreenHandler.this.inputSlot.takeStack(1);
				if (!itemStack.isEmpty()) {
					StonecutterScreenHandler.this.populateResult(StonecutterScreenHandler.this.selectedRecipe.get());
				}

				context.run((world, pos) -> {
					long l = world.getTime();
					if (StonecutterScreenHandler.this.lastTakeTime != l) {
						world.playSound(null, pos, SoundEvents.UI_STONECUTTER_TAKE_RESULT, SoundCategory.BLOCKS, 1.0F, 1.0F);
						StonecutterScreenHandler.this.lastTakeTime = l;
					}
				});
				super.onTakeItem(player, stack);
			}

			private List<ItemStack> getInputStacks() {
				return List.of(StonecutterScreenHandler.this.inputSlot.getStack());
			}
		});
		this.addPlayerSlots(playerInventory, 8, 84);
		this.addProperty(this.selectedRecipe);
	}

	public int getSelectedRecipe() {
		return this.selectedRecipe.get();
	}

	public CuttingRecipeDisplay.Grouping<StonecuttingRecipe> getAvailableRecipes() {
		return this.availableRecipes;
	}

	public int getAvailableRecipeCount() {
		return this.availableRecipes.size();
	}

	public boolean canCraft() {
		return this.inputSlot.hasStack() && !this.availableRecipes.isEmpty();
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return canUse(this.context, player, Blocks.STONECUTTER);
	}

	@Override
	public boolean onButtonClick(PlayerEntity player, int id) {
		if (this.isInBounds(id)) {
			this.selectedRecipe.set(id);
			this.populateResult(id);
		}

		return true;
	}

	private boolean isInBounds(int id) {
		return id >= 0 && id < this.availableRecipes.size();
	}

	@Override
	public void onContentChanged(Inventory inventory) {
		ItemStack itemStack = this.inputSlot.getStack();
		if (!itemStack.isOf(this.inputStack.getItem())) {
			this.inputStack = itemStack.copy();
			this.updateInput(itemStack);
		}
	}

	private void updateInput(ItemStack stack) {
		this.selectedRecipe.set(-1);
		this.outputSlot.setStackNoCallbacks(ItemStack.EMPTY);
		if (!stack.isEmpty()) {
			this.availableRecipes = this.world.getRecipeManager().getStonecutterRecipes().filter(stack);
		} else {
			this.availableRecipes = CuttingRecipeDisplay.Grouping.empty();
		}
	}

	void populateResult(int selectedId) {
		Optional<RecipeEntry<StonecuttingRecipe>> optional;
		if (!this.availableRecipes.isEmpty() && this.isInBounds(selectedId)) {
			CuttingRecipeDisplay.GroupEntry<StonecuttingRecipe> groupEntry = (CuttingRecipeDisplay.GroupEntry<StonecuttingRecipe>)this.availableRecipes
				.entries()
				.get(selectedId);
			optional = groupEntry.recipe().recipe();
		} else {
			optional = Optional.empty();
		}

		optional.ifPresentOrElse(
			recipe -> {
				this.output.setLastRecipe(recipe);
				this.outputSlot
					.setStackNoCallbacks(((StonecuttingRecipe)recipe.value()).craft(new SingleStackRecipeInput(this.input.getStack(0)), this.world.getRegistryManager()));
			},
			() -> {
				this.outputSlot.setStackNoCallbacks(ItemStack.EMPTY);
				this.output.setLastRecipe(null);
			}
		);
		this.sendContentUpdates();
	}

	@Override
	public ScreenHandlerType<?> getType() {
		return ScreenHandlerType.STONECUTTER;
	}

	public void setContentsChangedListener(Runnable contentsChangedListener) {
		this.contentsChangedListener = contentsChangedListener;
	}

	@Override
	public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
		return slot.inventory != this.output && super.canInsertIntoSlot(stack, slot);
	}

	@Override
	public ItemStack quickMove(PlayerEntity player, int slot) {
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot2 = this.slots.get(slot);
		if (slot2 != null && slot2.hasStack()) {
			ItemStack itemStack2 = slot2.getStack();
			Item item = itemStack2.getItem();
			itemStack = itemStack2.copy();
			if (slot == 1) {
				item.onCraftByPlayer(itemStack2, player.getWorld(), player);
				if (!this.insertItem(itemStack2, 2, 38, true)) {
					return ItemStack.EMPTY;
				}

				slot2.onQuickTransfer(itemStack2, itemStack);
			} else if (slot == 0) {
				if (!this.insertItem(itemStack2, 2, 38, false)) {
					return ItemStack.EMPTY;
				}
			} else if (this.world.getRecipeManager().getStonecutterRecipes().contains(itemStack2)) {
				if (!this.insertItem(itemStack2, 0, 1, false)) {
					return ItemStack.EMPTY;
				}
			} else if (slot >= 2 && slot < 29) {
				if (!this.insertItem(itemStack2, 29, 38, false)) {
					return ItemStack.EMPTY;
				}
			} else if (slot >= 29 && slot < 38 && !this.insertItem(itemStack2, 2, 29, false)) {
				return ItemStack.EMPTY;
			}

			if (itemStack2.isEmpty()) {
				slot2.setStack(ItemStack.EMPTY);
			}

			slot2.markDirty();
			if (itemStack2.getCount() == itemStack.getCount()) {
				return ItemStack.EMPTY;
			}

			slot2.onTakeItem(player, itemStack2);
			if (slot == 1) {
				player.dropItem(itemStack2, false);
			}

			this.sendContentUpdates();
		}

		return itemStack;
	}

	@Override
	public void onClosed(PlayerEntity player) {
		super.onClosed(player);
		this.output.removeStack(1);
		this.context.run((world, pos) -> this.dropInventory(player, this.input));
	}
}
