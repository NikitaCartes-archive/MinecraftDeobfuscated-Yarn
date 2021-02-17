package net.minecraft.block.entity;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.Generic3x3ContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;

public class DispenserBlockEntity extends LootableContainerBlockEntity {
	private static final Random RANDOM = new Random();
	private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(9, ItemStack.EMPTY);

	protected DispenserBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
		super(blockEntityType, blockPos, blockState);
	}

	public DispenserBlockEntity(BlockPos pos, BlockState state) {
		this(BlockEntityType.DISPENSER, pos, state);
	}

	@Override
	public int size() {
		return 9;
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

	public int addToFirstFreeSlot(ItemStack stack) {
		for (int i = 0; i < this.inventory.size(); i++) {
			if (this.inventory.get(i).isEmpty()) {
				this.setStack(i, stack);
				return i;
			}
		}

		return -1;
	}

	@Override
	protected Text getContainerName() {
		return new TranslatableText("container.dispenser");
	}

	@Override
	public void readNbt(CompoundTag tag) {
		super.readNbt(tag);
		this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
		if (!this.deserializeLootTable(tag)) {
			Inventories.readNbt(tag, this.inventory);
		}
	}

	@Override
	public CompoundTag writeNbt(CompoundTag tag) {
		super.writeNbt(tag);
		if (!this.serializeLootTable(tag)) {
			Inventories.writeNbt(tag, this.inventory);
		}

		return tag;
	}

	@Override
	protected DefaultedList<ItemStack> getInvStackList() {
		return this.inventory;
	}

	@Override
	protected void setInvStackList(DefaultedList<ItemStack> list) {
		this.inventory = list;
	}

	@Override
	protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
		return new Generic3x3ContainerScreenHandler(syncId, playerInventory, this);
	}
}
