package net.minecraft.block.entity;

import net.minecraft.block.BarrelBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

public class BarrelBlockEntity extends LootableContainerBlockEntity {
	private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(27, ItemStack.EMPTY);
	private ChestStateManager stateManager = new ChestStateManager() {
		@Override
		protected void onChestOpened(World world, BlockPos pos, BlockState state) {
			BarrelBlockEntity.this.playSound(state, SoundEvents.BLOCK_BARREL_OPEN);
			BarrelBlockEntity.this.setOpen(state, true);
		}

		@Override
		protected void onChestClosed(World world, BlockPos pos, BlockState state) {
			BarrelBlockEntity.this.playSound(state, SoundEvents.BLOCK_BARREL_CLOSE);
			BarrelBlockEntity.this.setOpen(state, false);
		}

		@Override
		protected void onInteracted(World world, BlockPos pos, BlockState state, int oldViewerCount, int newViewerCount) {
		}

		@Override
		protected boolean isPlayerViewing(PlayerEntity player) {
			if (player.currentScreenHandler instanceof GenericContainerScreenHandler) {
				Inventory inventory = ((GenericContainerScreenHandler)player.currentScreenHandler).getInventory();
				return inventory == BarrelBlockEntity.this;
			} else {
				return false;
			}
		}
	};

	public BarrelBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityType.BARREL, pos, state);
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
	public void fromTag(CompoundTag tag) {
		super.fromTag(tag);
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
			this.stateManager.openChest(player, this.getWorld(), this.getPos(), this.getCachedState());
		}
	}

	@Override
	public void onClose(PlayerEntity player) {
		if (!player.isSpectator()) {
			this.stateManager.closeChest(player, this.getWorld(), this.getPos(), this.getCachedState());
		}
	}

	public void tick() {
		this.stateManager.updateViewerCount(this.getWorld(), this.getPos(), this.getCachedState());
	}

	private void setOpen(BlockState state, boolean open) {
		this.world.setBlockState(this.getPos(), state.with(BarrelBlock.OPEN, Boolean.valueOf(open)), 3);
	}

	private void playSound(BlockState state, SoundEvent soundEvent) {
		Vec3i vec3i = ((Direction)state.get(BarrelBlock.FACING)).getVector();
		double d = (double)this.pos.getX() + 0.5 + (double)vec3i.getX() / 2.0;
		double e = (double)this.pos.getY() + 0.5 + (double)vec3i.getY() / 2.0;
		double f = (double)this.pos.getZ() + 0.5 + (double)vec3i.getZ() / 2.0;
		this.world.playSound(null, d, e, f, soundEvent, SoundCategory.BLOCKS, 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
	}
}
