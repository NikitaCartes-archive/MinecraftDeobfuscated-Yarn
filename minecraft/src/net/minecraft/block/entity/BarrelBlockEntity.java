package net.minecraft.block.entity;

import net.minecraft.block.BarrelBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;

public class BarrelBlockEntity extends LootableContainerBlockEntity {
	private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(27, ItemStack.EMPTY);
	private int viewerCount;

	private BarrelBlockEntity(BlockEntityType<?> type) {
		super(type);
	}

	public BarrelBlockEntity() {
		this(BlockEntityType.field_16411);
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		super.toTag(tag);
		if (!this.serializeLootTable(tag)) {
			Inventories.toTag(tag, this.inventory);
		}

		return tag;
	}

	@Override
	public void fromTag(BlockState state, CompoundTag tag) {
		super.fromTag(state, tag);
		this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
		if (!this.deserializeLootTable(tag)) {
			Inventories.fromTag(tag, this.inventory);
		}
	}

	@Override
	public int size() {
		return 27;
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
	protected Text getContainerName() {
		return new TranslatableText("container.barrel");
	}

	@Override
	protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
		return GenericContainerScreenHandler.createGeneric9x3(syncId, playerInventory, this);
	}

	@Override
	public void onOpen(PlayerEntity player) {
		if (!player.isSpectator()) {
			if (this.viewerCount < 0) {
				this.viewerCount = 0;
			}

			this.viewerCount++;
			BlockState blockState = this.getCachedState();
			boolean bl = (Boolean)blockState.get(BarrelBlock.OPEN);
			if (!bl) {
				this.playSound(blockState, SoundEvents.field_17604);
				this.setOpen(blockState, true);
			}

			this.scheduleUpdate();
		}
	}

	private void scheduleUpdate() {
		this.world.getBlockTickScheduler().schedule(this.getPos(), this.getCachedState().getBlock(), 5);
	}

	public void tick() {
		int i = this.pos.getX();
		int j = this.pos.getY();
		int k = this.pos.getZ();
		this.viewerCount = ChestBlockEntity.countViewers(this.world, this, i, j, k);
		if (this.viewerCount > 0) {
			this.scheduleUpdate();
		} else {
			BlockState blockState = this.getCachedState();
			if (!blockState.isOf(Blocks.field_16328)) {
				this.markRemoved();
				return;
			}

			boolean bl = (Boolean)blockState.get(BarrelBlock.OPEN);
			if (bl) {
				this.playSound(blockState, SoundEvents.field_17603);
				this.setOpen(blockState, false);
			}
		}
	}

	@Override
	public void onClose(PlayerEntity player) {
		if (!player.isSpectator()) {
			this.viewerCount--;
		}
	}

	private void setOpen(BlockState state, boolean open) {
		this.world.setBlockState(this.getPos(), state.with(BarrelBlock.OPEN, Boolean.valueOf(open)), 3);
	}

	private void playSound(BlockState blockState, SoundEvent soundEvent) {
		Vec3i vec3i = ((Direction)blockState.get(BarrelBlock.FACING)).getVector();
		double d = (double)this.pos.getX() + 0.5 + (double)vec3i.getX() / 2.0;
		double e = (double)this.pos.getY() + 0.5 + (double)vec3i.getY() / 2.0;
		double f = (double)this.pos.getZ() + 0.5 + (double)vec3i.getZ() / 2.0;
		this.world.playSound(null, d, e, f, soundEvent, SoundCategory.field_15245, 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
	}
}
