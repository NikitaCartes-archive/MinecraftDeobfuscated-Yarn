package net.minecraft.block.entity;

import net.minecraft.block.BarrelBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.container.Container;
import net.minecraft.container.GenericContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;

public class BarrelBlockEntity extends LootableContainerBlockEntity {
	private DefaultedList<ItemStack> inventory = DefaultedList.create(27, ItemStack.EMPTY);
	private int viewerCount;

	private BarrelBlockEntity(BlockEntityType<?> blockEntityType) {
		super(blockEntityType);
	}

	public BarrelBlockEntity() {
		this(BlockEntityType.field_16411);
	}

	@Override
	public CompoundTag toTag(CompoundTag compoundTag) {
		super.toTag(compoundTag);
		if (!this.serializeLootTable(compoundTag)) {
			Inventories.toTag(compoundTag, this.inventory);
		}

		return compoundTag;
	}

	@Override
	public void fromTag(CompoundTag compoundTag) {
		super.fromTag(compoundTag);
		this.inventory = DefaultedList.create(this.getInvSize(), ItemStack.EMPTY);
		if (!this.deserializeLootTable(compoundTag)) {
			Inventories.fromTag(compoundTag, this.inventory);
		}
	}

	@Override
	public int getInvSize() {
		return 27;
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
	public ItemStack getInvStack(int i) {
		return this.inventory.get(i);
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
		this.inventory.set(i, itemStack);
		if (itemStack.getCount() > this.getInvMaxStackAmount()) {
			itemStack.setCount(this.getInvMaxStackAmount());
		}
	}

	@Override
	public void clear() {
		this.inventory.clear();
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
	protected Text getContainerName() {
		return new TranslatableText("container.barrel");
	}

	@Override
	protected Container createContainer(int i, PlayerInventory playerInventory) {
		return GenericContainer.createGeneric9x3(i, playerInventory, this);
	}

	@Override
	public void onInvOpen(PlayerEntity playerEntity) {
		if (!playerEntity.isSpectator()) {
			if (this.viewerCount < 0) {
				this.viewerCount = 0;
			}

			this.viewerCount++;
			BlockState blockState = this.method_11010();
			boolean bl = (Boolean)blockState.method_11654(BarrelBlock.field_18006);
			if (!bl) {
				this.method_17764(blockState, SoundEvents.field_17604);
				this.method_18318(blockState, true);
			}

			this.scheduleUpdate();
		}
	}

	private void scheduleUpdate() {
		this.world.method_8397().schedule(this.getPos(), this.method_11010().getBlock(), 5);
	}

	public void tick() {
		int i = this.pos.getX();
		int j = this.pos.getY();
		int k = this.pos.getZ();
		this.viewerCount = ChestBlockEntity.countViewers(this.world, this, i, j, k);
		if (this.viewerCount > 0) {
			this.scheduleUpdate();
		} else {
			BlockState blockState = this.method_11010();
			if (blockState.getBlock() != Blocks.field_16328) {
				this.invalidate();
				return;
			}

			boolean bl = (Boolean)blockState.method_11654(BarrelBlock.field_18006);
			if (bl) {
				this.method_17764(blockState, SoundEvents.field_17603);
				this.method_18318(blockState, false);
			}
		}
	}

	@Override
	public void onInvClose(PlayerEntity playerEntity) {
		if (!playerEntity.isSpectator()) {
			this.viewerCount--;
		}
	}

	private void method_18318(BlockState blockState, boolean bl) {
		this.world.method_8652(this.getPos(), blockState.method_11657(BarrelBlock.field_18006, Boolean.valueOf(bl)), 3);
	}

	private void method_17764(BlockState blockState, SoundEvent soundEvent) {
		Vec3i vec3i = ((Direction)blockState.method_11654(BarrelBlock.field_16320)).getVector();
		double d = (double)this.pos.getX() + 0.5 + (double)vec3i.getX() / 2.0;
		double e = (double)this.pos.getY() + 0.5 + (double)vec3i.getY() / 2.0;
		double f = (double)this.pos.getZ() + 0.5 + (double)vec3i.getZ() / 2.0;
		this.world.playSound(null, d, e, f, soundEvent, SoundCategory.field_15245, 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
	}
}
