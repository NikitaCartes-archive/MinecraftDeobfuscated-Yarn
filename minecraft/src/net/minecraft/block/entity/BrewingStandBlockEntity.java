package net.minecraft.block.entity;

import java.util.Arrays;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BrewingStandBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.BrewingStandScreenHandler;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;

public class BrewingStandBlockEntity extends LockableContainerBlockEntity implements SidedInventory {
	private static final int INPUT_SLOT_INDEX = 3;
	private static final int FUEL_SLOT_INDEX = 4;
	private static final int[] TOP_SLOTS = new int[]{3};
	private static final int[] BOTTOM_SLOTS = new int[]{0, 1, 2, 3};
	private static final int[] SIDE_SLOTS = new int[]{0, 1, 2, 4};
	public static final int MAX_FUEL_USES = 20;
	public static final int BREW_TIME_PROPERTY_INDEX = 0;
	public static final int FUEL_PROPERTY_INDEX = 1;
	public static final int PROPERTY_COUNT = 2;
	private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(5, ItemStack.EMPTY);
	int brewTime;
	private boolean[] slotsEmptyLastTick;
	private Item itemBrewing;
	int fuel;
	protected final PropertyDelegate propertyDelegate = new PropertyDelegate() {
		@Override
		public int get(int index) {
			switch (index) {
				case 0:
					return BrewingStandBlockEntity.this.brewTime;
				case 1:
					return BrewingStandBlockEntity.this.fuel;
				default:
					return 0;
			}
		}

		@Override
		public void set(int index, int value) {
			switch (index) {
				case 0:
					BrewingStandBlockEntity.this.brewTime = value;
					break;
				case 1:
					BrewingStandBlockEntity.this.fuel = value;
			}
		}

		@Override
		public int size() {
			return 2;
		}
	};

	public BrewingStandBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityType.BREWING_STAND, pos, state);
	}

	@Override
	protected Text getContainerName() {
		return Text.translatable("container.brewing");
	}

	@Override
	public int size() {
		return this.inventory.size();
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack itemStack : this.inventory) {
			if (!itemStack.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	public static void tick(World world, BlockPos pos, BlockState state, BrewingStandBlockEntity blockEntity) {
		ItemStack itemStack = blockEntity.inventory.get(4);
		if (blockEntity.fuel <= 0 && itemStack.isOf(Items.BLAZE_POWDER)) {
			blockEntity.fuel = 20;
			itemStack.decrement(1);
			markDirty(world, pos, state);
		}

		boolean bl = canCraft(blockEntity.inventory);
		boolean bl2 = blockEntity.brewTime > 0;
		ItemStack itemStack2 = blockEntity.inventory.get(3);
		if (bl2) {
			blockEntity.brewTime--;
			boolean bl3 = blockEntity.brewTime == 0;
			if (bl3 && bl) {
				craft(world, pos, blockEntity.inventory);
				markDirty(world, pos, state);
			} else if (!bl || !itemStack2.isOf(blockEntity.itemBrewing)) {
				blockEntity.brewTime = 0;
				markDirty(world, pos, state);
			}
		} else if (bl && blockEntity.fuel > 0) {
			blockEntity.fuel--;
			blockEntity.brewTime = 400;
			blockEntity.itemBrewing = itemStack2.getItem();
			markDirty(world, pos, state);
		}

		boolean[] bls = blockEntity.getSlotsEmpty();
		if (!Arrays.equals(bls, blockEntity.slotsEmptyLastTick)) {
			blockEntity.slotsEmptyLastTick = bls;
			BlockState blockState = state;
			if (!(state.getBlock() instanceof BrewingStandBlock)) {
				return;
			}

			for (int i = 0; i < BrewingStandBlock.BOTTLE_PROPERTIES.length; i++) {
				blockState = blockState.with(BrewingStandBlock.BOTTLE_PROPERTIES[i], Boolean.valueOf(bls[i]));
			}

			world.setBlockState(pos, blockState, Block.NOTIFY_LISTENERS);
		}
	}

	private boolean[] getSlotsEmpty() {
		boolean[] bls = new boolean[3];

		for (int i = 0; i < 3; i++) {
			if (!this.inventory.get(i).isEmpty()) {
				bls[i] = true;
			}
		}

		return bls;
	}

	private static boolean canCraft(DefaultedList<ItemStack> slots) {
		ItemStack itemStack = slots.get(3);
		if (itemStack.isEmpty()) {
			return false;
		} else if (!BrewingRecipeRegistry.isValidIngredient(itemStack)) {
			return false;
		} else {
			for (int i = 0; i < 3; i++) {
				ItemStack itemStack2 = slots.get(i);
				if (!itemStack2.isEmpty() && BrewingRecipeRegistry.hasRecipe(itemStack2, itemStack)) {
					return true;
				}
			}

			return false;
		}
	}

	private static void craft(World world, BlockPos pos, DefaultedList<ItemStack> slots) {
		ItemStack itemStack = slots.get(3);

		for (int i = 0; i < 3; i++) {
			slots.set(i, BrewingRecipeRegistry.craft(itemStack, slots.get(i)));
		}

		itemStack.decrement(1);
		if (itemStack.getItem().hasRecipeRemainder()) {
			ItemStack itemStack2 = new ItemStack(itemStack.getItem().getRecipeRemainder());
			if (itemStack.isEmpty()) {
				itemStack = itemStack2;
			} else {
				ItemScatterer.spawn(world, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), itemStack2);
			}
		}

		slots.set(3, itemStack);
		world.syncWorldEvent(WorldEvents.BREWING_STAND_BREWS, pos, 0);
	}

	@Override
	public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
		super.readNbt(nbt, registryLookup);
		this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
		Inventories.readNbt(nbt, this.inventory);
		this.brewTime = nbt.getShort("BrewTime");
		this.fuel = nbt.getByte("Fuel");
	}

	@Override
	protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
		super.writeNbt(nbt, registryLookup);
		nbt.putShort("BrewTime", (short)this.brewTime);
		Inventories.writeNbt(nbt, this.inventory);
		nbt.putByte("Fuel", (byte)this.fuel);
	}

	@Override
	public ItemStack getStack(int slot) {
		return slot >= 0 && slot < this.inventory.size() ? this.inventory.get(slot) : ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeStack(int slot, int amount) {
		return Inventories.splitStack(this.inventory, slot, amount);
	}

	@Override
	public ItemStack removeStack(int slot) {
		return Inventories.removeStack(this.inventory, slot);
	}

	@Override
	public void setStack(int slot, ItemStack stack) {
		if (slot >= 0 && slot < this.inventory.size()) {
			this.inventory.set(slot, stack);
		}
	}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		return Inventory.canPlayerUse(this, player);
	}

	@Override
	public boolean isValid(int slot, ItemStack stack) {
		if (slot == 3) {
			return BrewingRecipeRegistry.isValidIngredient(stack);
		} else {
			return slot == 4
				? stack.isOf(Items.BLAZE_POWDER)
				: (stack.isOf(Items.POTION) || stack.isOf(Items.SPLASH_POTION) || stack.isOf(Items.LINGERING_POTION) || stack.isOf(Items.GLASS_BOTTLE))
					&& this.getStack(slot).isEmpty();
		}
	}

	@Override
	public int[] getAvailableSlots(Direction side) {
		if (side == Direction.UP) {
			return TOP_SLOTS;
		} else {
			return side == Direction.DOWN ? BOTTOM_SLOTS : SIDE_SLOTS;
		}
	}

	@Override
	public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
		return this.isValid(slot, stack);
	}

	@Override
	public boolean canExtract(int slot, ItemStack stack, Direction dir) {
		return slot == 3 ? stack.isOf(Items.GLASS_BOTTLE) : true;
	}

	@Override
	public void clear() {
		this.inventory.clear();
	}

	@Override
	protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
		return new BrewingStandScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
	}
}
