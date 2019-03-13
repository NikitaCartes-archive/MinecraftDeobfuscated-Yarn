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
import net.minecraft.sortme.ItemScatterer;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class BrewingStandBlockEntity extends LockableContainerBlockEntity implements SidedInventory, Tickable {
	private static final int[] TOP_SLOTS = new int[]{3};
	private static final int[] BOTTOM_SLOTS = new int[]{0, 1, 2, 3};
	private static final int[] SIDE_SLOTS = new int[]{0, 1, 2, 4};
	private DefaultedList<ItemStack> field_11882 = DefaultedList.create(5, ItemStack.EMPTY);
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
		super(BlockEntityType.BREWING_STAND);
	}

	@Override
	protected TextComponent method_17823() {
		return new TranslatableTextComponent("container.brewing");
	}

	@Override
	public int getInvSize() {
		return this.field_11882.size();
	}

	@Override
	public boolean isInvEmpty() {
		for (ItemStack itemStack : this.field_11882) {
			if (!itemStack.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public void tick() {
		ItemStack itemStack = this.field_11882.get(4);
		if (this.fuel <= 0 && itemStack.getItem() == Items.field_8183) {
			this.fuel = 20;
			itemStack.subtractAmount(1);
			this.markDirty();
		}

		boolean bl = this.canCraft();
		boolean bl2 = this.brewTime > 0;
		ItemStack itemStack2 = this.field_11882.get(3);
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
				BlockState blockState = this.world.method_8320(this.method_11016());
				if (!(blockState.getBlock() instanceof BrewingStandBlock)) {
					return;
				}

				for (int i = 0; i < BrewingStandBlock.field_10700.length; i++) {
					blockState = blockState.method_11657(BrewingStandBlock.field_10700[i], Boolean.valueOf(bls[i]));
				}

				this.world.method_8652(this.field_11867, blockState, 2);
			}
		}
	}

	public boolean[] getSlotsEmpty() {
		boolean[] bls = new boolean[3];

		for (int i = 0; i < 3; i++) {
			if (!this.field_11882.get(i).isEmpty()) {
				bls[i] = true;
			}
		}

		return bls;
	}

	private boolean canCraft() {
		ItemStack itemStack = this.field_11882.get(3);
		if (itemStack.isEmpty()) {
			return false;
		} else if (!BrewingRecipeRegistry.isValidIngredient(itemStack)) {
			return false;
		} else {
			for (int i = 0; i < 3; i++) {
				ItemStack itemStack2 = this.field_11882.get(i);
				if (!itemStack2.isEmpty() && BrewingRecipeRegistry.hasRecipe(itemStack2, itemStack)) {
					return true;
				}
			}

			return false;
		}
	}

	private void craft() {
		ItemStack itemStack = this.field_11882.get(3);

		for (int i = 0; i < 3; i++) {
			this.field_11882.set(i, BrewingRecipeRegistry.craft(itemStack, this.field_11882.get(i)));
		}

		itemStack.subtractAmount(1);
		BlockPos blockPos = this.method_11016();
		if (itemStack.getItem().hasRecipeRemainder()) {
			ItemStack itemStack2 = new ItemStack(itemStack.getItem().getRecipeRemainder());
			if (itemStack.isEmpty()) {
				itemStack = itemStack2;
			} else if (!this.world.isClient) {
				ItemScatterer.method_5449(this.world, (double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), itemStack2);
			}
		}

		this.field_11882.set(3, itemStack);
		this.world.method_8535(1035, blockPos, 0);
	}

	@Override
	public void method_11014(CompoundTag compoundTag) {
		super.method_11014(compoundTag);
		this.field_11882 = DefaultedList.create(this.getInvSize(), ItemStack.EMPTY);
		Inventories.method_5429(compoundTag, this.field_11882);
		this.brewTime = compoundTag.getShort("BrewTime");
		this.fuel = compoundTag.getByte("Fuel");
	}

	@Override
	public CompoundTag method_11007(CompoundTag compoundTag) {
		super.method_11007(compoundTag);
		compoundTag.putShort("BrewTime", (short)this.brewTime);
		Inventories.method_5426(compoundTag, this.field_11882);
		compoundTag.putByte("Fuel", (byte)this.fuel);
		return compoundTag;
	}

	@Override
	public ItemStack method_5438(int i) {
		return i >= 0 && i < this.field_11882.size() ? this.field_11882.get(i) : ItemStack.EMPTY;
	}

	@Override
	public ItemStack method_5434(int i, int j) {
		return Inventories.method_5430(this.field_11882, i, j);
	}

	@Override
	public ItemStack method_5441(int i) {
		return Inventories.method_5428(this.field_11882, i);
	}

	@Override
	public void method_5447(int i, ItemStack itemStack) {
		if (i >= 0 && i < this.field_11882.size()) {
			this.field_11882.set(i, itemStack);
		}
	}

	@Override
	public boolean method_5443(PlayerEntity playerEntity) {
		return this.world.method_8321(this.field_11867) != this
			? false
			: !(
				playerEntity.squaredDistanceTo((double)this.field_11867.getX() + 0.5, (double)this.field_11867.getY() + 0.5, (double)this.field_11867.getZ() + 0.5) > 64.0
			);
	}

	@Override
	public boolean method_5437(int i, ItemStack itemStack) {
		if (i == 3) {
			return BrewingRecipeRegistry.isValidIngredient(itemStack);
		} else {
			Item item = itemStack.getItem();
			return i == 4
				? item == Items.field_8183
				: (item == Items.field_8574 || item == Items.field_8436 || item == Items.field_8150 || item == Items.field_8469) && this.method_5438(i).isEmpty();
		}
	}

	@Override
	public int[] method_5494(Direction direction) {
		if (direction == Direction.UP) {
			return TOP_SLOTS;
		} else {
			return direction == Direction.DOWN ? BOTTOM_SLOTS : SIDE_SLOTS;
		}
	}

	@Override
	public boolean method_5492(int i, ItemStack itemStack, @Nullable Direction direction) {
		return this.method_5437(i, itemStack);
	}

	@Override
	public boolean method_5493(int i, ItemStack itemStack, Direction direction) {
		return i == 3 ? itemStack.getItem() == Items.field_8469 : true;
	}

	@Override
	public void clear() {
		this.field_11882.clear();
	}

	@Override
	protected Container createContainer(int i, PlayerInventory playerInventory) {
		return new BrewingStandContainer(i, playerInventory, this, this.propertyDelegate);
	}
}
