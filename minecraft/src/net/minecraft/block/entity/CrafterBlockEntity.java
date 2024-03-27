package net.minecraft.block.entity;

import com.google.common.annotations.VisibleForTesting;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CrafterBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.RecipeMatcher;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.CrafterScreenHandler;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CrafterBlockEntity extends LootableContainerBlockEntity implements RecipeInputInventory {
	public static final int GRID_WIDTH = 3;
	public static final int GRID_HEIGHT = 3;
	public static final int GRID_SIZE = 9;
	public static final int SLOT_DISABLED = 1;
	public static final int SLOT_ENABLED = 0;
	public static final int TRIGGERED_PROPERTY = 9;
	public static final int PROPERTIES_COUNT = 10;
	private DefaultedList<ItemStack> inputStacks = DefaultedList.ofSize(9, ItemStack.EMPTY);
	private int craftingTicksRemaining = 0;
	protected final PropertyDelegate propertyDelegate = new PropertyDelegate() {
		private final int[] disabledSlots = new int[9];
		private int triggered = 0;

		@Override
		public int get(int index) {
			return index == 9 ? this.triggered : this.disabledSlots[index];
		}

		@Override
		public void set(int index, int value) {
			if (index == 9) {
				this.triggered = value;
			} else {
				this.disabledSlots[index] = value;
			}
		}

		@Override
		public int size() {
			return 10;
		}
	};

	public CrafterBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityType.CRAFTER, pos, state);
	}

	@Override
	protected Text getContainerName() {
		return Text.translatable("container.crafter");
	}

	@Override
	protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
		return new CrafterScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
	}

	public void setSlotEnabled(int slot, boolean enabled) {
		if (this.canToggleSlot(slot)) {
			this.propertyDelegate.set(slot, enabled ? 0 : 1);
			this.markDirty();
		}
	}

	public boolean isSlotDisabled(int slot) {
		return slot >= 0 && slot < 9 ? this.propertyDelegate.get(slot) == 1 : false;
	}

	@Override
	public boolean isValid(int slot, ItemStack stack) {
		if (this.propertyDelegate.get(slot) == 1) {
			return false;
		} else {
			ItemStack itemStack = this.inputStacks.get(slot);
			int i = itemStack.getCount();
			if (i >= itemStack.getMaxCount()) {
				return false;
			} else {
				return itemStack.isEmpty() ? true : !this.betterSlotExists(i, itemStack, slot);
			}
		}
	}

	private boolean betterSlotExists(int count, ItemStack stack, int slot) {
		for (int i = slot + 1; i < 9; i++) {
			if (!this.isSlotDisabled(i)) {
				ItemStack itemStack = this.getStack(i);
				if (itemStack.isEmpty() || itemStack.getCount() < count && ItemStack.areItemsAndComponentsEqual(itemStack, stack)) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
		super.readNbt(nbt, registryLookup);
		this.craftingTicksRemaining = nbt.getInt("crafting_ticks_remaining");
		this.inputStacks = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
		if (!this.readLootTable(nbt)) {
			Inventories.readNbt(nbt, this.inputStacks, registryLookup);
		}

		int[] is = nbt.getIntArray("disabled_slots");

		for (int i = 0; i < 9; i++) {
			this.propertyDelegate.set(i, 0);
		}

		for (int j : is) {
			if (this.canToggleSlot(j)) {
				this.propertyDelegate.set(j, 1);
			}
		}

		this.propertyDelegate.set(9, nbt.getInt("triggered"));
	}

	@Override
	protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
		super.writeNbt(nbt, registryLookup);
		nbt.putInt("crafting_ticks_remaining", this.craftingTicksRemaining);
		if (!this.writeLootTable(nbt)) {
			Inventories.writeNbt(nbt, this.inputStacks, registryLookup);
		}

		this.putDisabledSlots(nbt);
		this.putTriggered(nbt);
	}

	@Override
	public int size() {
		return 9;
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack itemStack : this.inputStacks) {
			if (!itemStack.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public ItemStack getStack(int slot) {
		return this.inputStacks.get(slot);
	}

	@Override
	public void setStack(int slot, ItemStack stack) {
		if (this.isSlotDisabled(slot)) {
			this.setSlotEnabled(slot, true);
		}

		super.setStack(slot, stack);
	}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		return Inventory.canPlayerUse(this, player);
	}

	@Override
	public DefaultedList<ItemStack> getHeldStacks() {
		return this.inputStacks;
	}

	@Override
	protected void setHeldStacks(DefaultedList<ItemStack> inventory) {
		this.inputStacks = inventory;
	}

	@Override
	public int getWidth() {
		return 3;
	}

	@Override
	public int getHeight() {
		return 3;
	}

	@Override
	public void provideRecipeInputs(RecipeMatcher finder) {
		for (ItemStack itemStack : this.inputStacks) {
			finder.addUnenchantedInput(itemStack);
		}
	}

	private void putDisabledSlots(NbtCompound nbt) {
		IntList intList = new IntArrayList();

		for (int i = 0; i < 9; i++) {
			if (this.isSlotDisabled(i)) {
				intList.add(i);
			}
		}

		nbt.putIntArray("disabled_slots", intList);
	}

	private void putTriggered(NbtCompound nbt) {
		nbt.putInt("triggered", this.propertyDelegate.get(9));
	}

	public void setTriggered(boolean triggered) {
		this.propertyDelegate.set(9, triggered ? 1 : 0);
	}

	@VisibleForTesting
	public boolean isTriggered() {
		return this.propertyDelegate.get(9) == 1;
	}

	public static void tickCrafting(World world, BlockPos pos, BlockState state, CrafterBlockEntity blockEntity) {
		int i = blockEntity.craftingTicksRemaining - 1;
		if (i >= 0) {
			blockEntity.craftingTicksRemaining = i;
			if (i == 0) {
				world.setBlockState(pos, state.with(CrafterBlock.CRAFTING, Boolean.valueOf(false)), Block.NOTIFY_ALL);
			}
		}
	}

	public void setCraftingTicksRemaining(int craftingTicksRemaining) {
		this.craftingTicksRemaining = craftingTicksRemaining;
	}

	public int getComparatorOutput() {
		int i = 0;

		for (int j = 0; j < this.size(); j++) {
			ItemStack itemStack = this.getStack(j);
			if (!itemStack.isEmpty() || this.isSlotDisabled(j)) {
				i++;
			}
		}

		return i;
	}

	private boolean canToggleSlot(int slot) {
		return slot > -1 && slot < 9 && this.inputStacks.get(slot).isEmpty();
	}
}
