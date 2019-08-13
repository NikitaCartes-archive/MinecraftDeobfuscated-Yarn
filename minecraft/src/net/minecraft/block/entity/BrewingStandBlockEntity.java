package net.minecraft.block.entity;

import java.util.Arrays;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.BrewingStandBlock;
import net.minecraft.container.BrewingStandContainer;
import net.minecraft.container.Container;
import net.minecraft.container.PropertyDelegate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class BrewingStandBlockEntity extends LockableContainerBlockEntity implements SidedInventory, Tickable {
	private static final int[] TOP_SLOTS = new int[]{3};
	private static final int[] BOTTOM_SLOTS = new int[]{0, 1, 2, 3};
	private static final int[] SIDE_SLOTS = new int[]{0, 1, 2, 4};
	private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(5, ItemStack.EMPTY);
	private int brewTime;
	private boolean[] slotsEmptyLastTick;
	private Item itemBrewing;
	private int fuel;
	protected final PropertyDelegate propertyDelegate = new PropertyDelegate() {
		@Override
		public int get(int i) {
			switch (i) {
				case 0:
					return BrewingStandBlockEntity.this.brewTime;
				case 1:
					return BrewingStandBlockEntity.this.fuel;
				default:
					return 0;
			}
		}

		@Override
		public void set(int i, int j) {
			switch (i) {
				case 0:
					BrewingStandBlockEntity.this.brewTime = j;
					break;
				case 1:
					BrewingStandBlockEntity.this.fuel = j;
			}
		}

		@Override
		public int size() {
			return 2;
		}
	};

	public BrewingStandBlockEntity() {
		super(BlockEntityType.field_11894);
	}

	@Override
	protected Text getContainerName() {
		return new TranslatableText("container.brewing");
	}

	@Override
	public int getInvSize() {
		return this.inventory.size();
	}

	@Override
	public boolean isInvEmpty() {
		for (ItemStack itemStack : this.inventory) {
			if (!itemStack.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public void tick() {
		ItemStack itemStack = this.inventory.get(4);
		if (this.fuel <= 0 && itemStack.getItem() == Items.field_8183) {
			this.fuel = 20;
			itemStack.decrement(1);
			this.markDirty();
		}

		boolean bl = this.canCraft();
		boolean bl2 = this.brewTime > 0;
		ItemStack itemStack2 = this.inventory.get(3);
		if (bl2) {
			this.brewTime--;
			boolean bl3 = this.brewTime == 0;
			if (bl3 && bl) {
				this.craft();
				this.markDirty();
			} else if (!bl) {
				this.brewTime = 0;
				this.markDirty();
			} else if (this.itemBrewing != itemStack2.getItem()) {
				this.brewTime = 0;
				this.markDirty();
			}
		} else if (bl && this.fuel > 0) {
			this.fuel--;
			this.brewTime = 400;
			this.itemBrewing = itemStack2.getItem();
			this.markDirty();
		}

		if (!this.world.isClient) {
			boolean[] bls = this.getSlotsEmpty();
			if (!Arrays.equals(bls, this.slotsEmptyLastTick)) {
				this.slotsEmptyLastTick = bls;
				BlockState blockState = this.world.getBlockState(this.getPos());
				if (!(blockState.getBlock() instanceof BrewingStandBlock)) {
					return;
				}

				for (int i = 0; i < BrewingStandBlock.BOTTLE_PROPERTIES.length; i++) {
					blockState = blockState.with(BrewingStandBlock.BOTTLE_PROPERTIES[i], Boolean.valueOf(bls[i]));
				}

				this.world.setBlockState(this.pos, blockState, 2);
			}
		}
	}

	public boolean[] getSlotsEmpty() {
		boolean[] bls = new boolean[3];

		for (int i = 0; i < 3; i++) {
			if (!this.inventory.get(i).isEmpty()) {
				bls[i] = true;
			}
		}

		return bls;
	}

	private boolean canCraft() {
		ItemStack itemStack = this.inventory.get(3);
		if (itemStack.isEmpty()) {
			return false;
		} else if (!BrewingRecipeRegistry.isValidIngredient(itemStack)) {
			return false;
		} else {
			for (int i = 0; i < 3; i++) {
				ItemStack itemStack2 = this.inventory.get(i);
				if (!itemStack2.isEmpty() && BrewingRecipeRegistry.hasRecipe(itemStack2, itemStack)) {
					return true;
				}
			}

			return false;
		}
	}

	private void craft() {
		ItemStack itemStack = this.inventory.get(3);

		for (int i = 0; i < 3; i++) {
			this.inventory.set(i, BrewingRecipeRegistry.craft(itemStack, this.inventory.get(i)));
		}

		itemStack.decrement(1);
		BlockPos blockPos = this.getPos();
		if (itemStack.getItem().hasRecipeRemainder()) {
			ItemStack itemStack2 = new ItemStack(itemStack.getItem().getRecipeRemainder());
			if (itemStack.isEmpty()) {
				itemStack = itemStack2;
			} else if (!this.world.isClient) {
				ItemScatterer.spawn(this.world, (double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), itemStack2);
			}
		}

		this.inventory.set(3, itemStack);
		this.world.playLevelEvent(1035, blockPos, 0);
	}

	@Override
	public void fromTag(CompoundTag compoundTag) {
		super.fromTag(compoundTag);
		this.inventory = DefaultedList.ofSize(this.getInvSize(), ItemStack.EMPTY);
		Inventories.fromTag(compoundTag, this.inventory);
		this.brewTime = compoundTag.getShort("BrewTime");
		this.fuel = compoundTag.getByte("Fuel");
	}

	@Override
	public CompoundTag toTag(CompoundTag compoundTag) {
		super.toTag(compoundTag);
		compoundTag.putShort("BrewTime", (short)this.brewTime);
		Inventories.toTag(compoundTag, this.inventory);
		compoundTag.putByte("Fuel", (byte)this.fuel);
		return compoundTag;
	}

	@Override
	public ItemStack getInvStack(int i) {
		return i >= 0 && i < this.inventory.size() ? this.inventory.get(i) : ItemStack.EMPTY;
	}

	@Override
	public ItemStack takeInvStack(int i, int j) {
		return Inventories.splitStack(this.inventory, i, j);
	}

	@Override
	public ItemStack removeInvStack(int i) {
		return Inventories.removeStack(this.inventory, i);
	}

	@Override
	public void setInvStack(int i, ItemStack itemStack) {
		if (i >= 0 && i < this.inventory.size()) {
			this.inventory.set(i, itemStack);
		}
	}

	@Override
	public boolean canPlayerUseInv(PlayerEntity playerEntity) {
		return this.world.getBlockEntity(this.pos) != this
			? false
			: !(playerEntity.squaredDistanceTo((double)this.pos.getX() + 0.5, (double)this.pos.getY() + 0.5, (double)this.pos.getZ() + 0.5) > 64.0);
	}

	@Override
	public boolean isValidInvStack(int i, ItemStack itemStack) {
		if (i == 3) {
			return BrewingRecipeRegistry.isValidIngredient(itemStack);
		} else {
			Item item = itemStack.getItem();
			return i == 4
				? item == Items.field_8183
				: (item == Items.field_8574 || item == Items.field_8436 || item == Items.field_8150 || item == Items.field_8469) && this.getInvStack(i).isEmpty();
		}
	}

	@Override
	public int[] getInvAvailableSlots(Direction direction) {
		if (direction == Direction.field_11036) {
			return TOP_SLOTS;
		} else {
			return direction == Direction.field_11033 ? BOTTOM_SLOTS : SIDE_SLOTS;
		}
	}

	@Override
	public boolean canInsertInvStack(int i, ItemStack itemStack, @Nullable Direction direction) {
		return this.isValidInvStack(i, itemStack);
	}

	@Override
	public boolean canExtractInvStack(int i, ItemStack itemStack, Direction direction) {
		return i == 3 ? itemStack.getItem() == Items.field_8469 : true;
	}

	@Override
	public void clear() {
		this.inventory.clear();
	}

	@Override
	protected Container createContainer(int i, PlayerInventory playerInventory) {
		return new BrewingStandContainer(i, playerInventory, this, this.propertyDelegate);
	}
}
