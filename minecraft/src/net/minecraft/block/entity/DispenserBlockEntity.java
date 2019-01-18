package net.minecraft.block.entity;

import java.util.Random;
import net.minecraft.container.Container;
import net.minecraft.container.Generic3x3Container;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.InventoryUtil;

public class DispenserBlockEntity extends LootableContainerBlockEntity {
	private static final Random RANDOM = new Random();
	private DefaultedList<ItemStack> inventory = DefaultedList.create(9, ItemStack.EMPTY);

	protected DispenserBlockEntity(BlockEntityType<?> blockEntityType) {
		super(blockEntityType);
	}

	public DispenserBlockEntity() {
		this(BlockEntityType.DISPENSER);
	}

	@Override
	public int getInvSize() {
		return 9;
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

	public int chooseNonEmptySlot() {
		this.checkLootInteraction(null);
		int i = -1;
		int j = 1;

		for (int k = 0; k < this.inventory.size(); k++) {
			if (!this.inventory.get(k).isEmpty() && RANDOM.nextInt(j++) == 0) {
				i = k;
			}
		}

		return i;
	}

	public int addToFirstFreeSlot(ItemStack itemStack) {
		for (int i = 0; i < this.inventory.size(); i++) {
			if (this.inventory.get(i).isEmpty()) {
				this.setInvStack(i, itemStack);
				return i;
			}
		}

		return -1;
	}

	@Override
	protected TextComponent getContainerName() {
		return new TranslatableTextComponent("container.dispenser");
	}

	@Override
	public void fromTag(CompoundTag compoundTag) {
		super.fromTag(compoundTag);
		this.inventory = DefaultedList.create(this.getInvSize(), ItemStack.EMPTY);
		if (!this.deserializeLootTable(compoundTag)) {
			InventoryUtil.deserialize(compoundTag, this.inventory);
		}
	}

	@Override
	public CompoundTag toTag(CompoundTag compoundTag) {
		super.toTag(compoundTag);
		if (!this.serializeLootTable(compoundTag)) {
			InventoryUtil.serialize(compoundTag, this.inventory);
		}

		return compoundTag;
	}

	@Override
	protected DefaultedList<ItemStack> getInvStackList() {
		return this.inventory;
	}

	@Override
	protected void setInvStackList(DefaultedList<ItemStack> defaultedList) {
		this.inventory = defaultedList;
	}

	@Override
	protected Container createContainer(int i, PlayerInventory playerInventory) {
		return new Generic3x3Container(i, playerInventory, this);
	}
}
