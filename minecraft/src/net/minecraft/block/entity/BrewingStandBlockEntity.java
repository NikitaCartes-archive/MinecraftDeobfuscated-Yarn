package net.minecraft.block.entity;

import java.util.Arrays;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.BrewingStandBlock;
import net.minecraft.container.BrewingStandContainer;
import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
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
import net.minecraft.util.InventoryUtil;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class BrewingStandBlockEntity extends LockableContainerBlockEntity implements SidedInventory, Tickable {
	private static final int[] TOP_SLOTS = new int[]{3};
	private static final int[] BOTTOM_SLOTS = new int[]{0, 1, 2, 3};
	private static final int[] SIDE_SLOTS = new int[]{0, 1, 2, 4};
	private DefaultedList<ItemStack> inventory = DefaultedList.create(5, ItemStack.EMPTY);
	private int brewTime;
	private boolean[] field_11883;
	private Item field_11881;
	private TextComponent customName;
	private int fuel;

	public BrewingStandBlockEntity() {
		super(BlockEntityType.BREWING_STAND);
	}

	@Override
	public TextComponent getName() {
		return (TextComponent)(this.customName != null ? this.customName : new TranslatableTextComponent("container.brewing"));
	}

	@Nullable
	@Override
	public TextComponent getCustomName() {
		return this.customName;
	}

	public void setCustomName(@Nullable TextComponent textComponent) {
		this.customName = textComponent;
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
			itemStack.subtractAmount(1);
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
			} else if (this.field_11881 != itemStack2.getItem()) {
				this.brewTime = 0;
				this.markDirty();
			}
		} else if (bl && this.fuel > 0) {
			this.fuel--;
			this.brewTime = 400;
			this.field_11881 = itemStack2.getItem();
			this.markDirty();
		}

		if (!this.world.isClient) {
			boolean[] bls = this.method_11028();
			if (!Arrays.equals(bls, this.field_11883)) {
				this.field_11883 = bls;
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

	public boolean[] method_11028() {
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

		itemStack.subtractAmount(1);
		BlockPos blockPos = this.getPos();
		if (itemStack.getItem().hasRecipeRemainder()) {
			ItemStack itemStack2 = new ItemStack(itemStack.getItem().getRecipeRemainder());
			if (itemStack.isEmpty()) {
				itemStack = itemStack2;
			} else {
				ItemScatterer.spawn(this.world, (double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), itemStack2);
			}
		}

		this.inventory.set(3, itemStack);
		this.world.fireWorldEvent(1035, blockPos, 0);
	}

	@Override
	public void fromTag(CompoundTag compoundTag) {
		super.fromTag(compoundTag);
		this.inventory = DefaultedList.create(this.getInvSize(), ItemStack.EMPTY);
		InventoryUtil.deserialize(compoundTag, this.inventory);
		this.brewTime = compoundTag.getShort("BrewTime");
		if (compoundTag.containsKey("CustomName", 8)) {
			this.customName = TextComponent.Serializer.fromJsonString(compoundTag.getString("CustomName"));
		}

		this.fuel = compoundTag.getByte("Fuel");
	}

	@Override
	public CompoundTag toTag(CompoundTag compoundTag) {
		super.toTag(compoundTag);
		compoundTag.putShort("BrewTime", (short)this.brewTime);
		InventoryUtil.serialize(compoundTag, this.inventory);
		if (this.customName != null) {
			compoundTag.putString("CustomName", TextComponent.Serializer.toJsonString(this.customName));
		}

		compoundTag.putByte("Fuel", (byte)this.fuel);
		return compoundTag;
	}

	@Override
	public ItemStack getInvStack(int i) {
		return i >= 0 && i < this.inventory.size() ? this.inventory.get(i) : ItemStack.EMPTY;
	}

	@Override
	public ItemStack takeInvStack(int i, int j) {
		return InventoryUtil.splitStack(this.inventory, i, j);
	}

	@Override
	public ItemStack removeInvStack(int i) {
		return InventoryUtil.removeStack(this.inventory, i);
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
		if (direction == Direction.UP) {
			return TOP_SLOTS;
		} else {
			return direction == Direction.DOWN ? BOTTOM_SLOTS : SIDE_SLOTS;
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
	public String getContainerId() {
		return "minecraft:brewing_stand";
	}

	@Override
	public Container createContainer(PlayerInventory playerInventory, PlayerEntity playerEntity) {
		return new BrewingStandContainer(playerInventory, this);
	}

	@Override
	public int getInvProperty(int i) {
		switch (i) {
			case 0:
				return this.brewTime;
			case 1:
				return this.fuel;
			default:
				return 0;
		}
	}

	@Override
	public void setInvProperty(int i, int j) {
		switch (i) {
			case 0:
				this.brewTime = j;
				break;
			case 1:
				this.fuel = j;
		}
	}

	@Override
	public int getInvPropertyCount() {
		return 2;
	}

	@Override
	public void clearInv() {
		this.inventory.clear();
	}
}
