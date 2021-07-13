package net.minecraft.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.block.ChestAnimationProgress;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EnderChestBlockEntity extends BlockEntity implements ChestAnimationProgress {
	private final ChestLidAnimator lidAnimator = new ChestLidAnimator();
	private final ChestStateManager stateManager = new ChestStateManager() {
		@Override
		protected void onChestOpened(World world, BlockPos pos, BlockState state) {
			world.playSound(
				null,
				(double)pos.getX() + 0.5,
				(double)pos.getY() + 0.5,
				(double)pos.getZ() + 0.5,
				SoundEvents.BLOCK_ENDER_CHEST_OPEN,
				SoundCategory.BLOCKS,
				0.5F,
				world.random.nextFloat() * 0.1F + 0.9F
			);
		}

		@Override
		protected void onChestClosed(World world, BlockPos pos, BlockState state) {
			world.playSound(
				null,
				(double)pos.getX() + 0.5,
				(double)pos.getY() + 0.5,
				(double)pos.getZ() + 0.5,
				SoundEvents.BLOCK_ENDER_CHEST_CLOSE,
				SoundCategory.BLOCKS,
				0.5F,
				world.random.nextFloat() * 0.1F + 0.9F
			);
		}

		@Override
		protected void onInteracted(World world, BlockPos pos, BlockState state, int oldViewerCount, int newViewerCount) {
			world.addSyncedBlockEvent(EnderChestBlockEntity.this.pos, Blocks.ENDER_CHEST, 1, newViewerCount);
		}

		@Override
		protected boolean isPlayerViewing(PlayerEntity player) {
			return player.getEnderChestInventory().isActiveBlockEntity(EnderChestBlockEntity.this);
		}
	};

	public EnderChestBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityType.ENDER_CHEST, pos, state);
	}

	public static void clientTick(World world, BlockPos pos, BlockState state, EnderChestBlockEntity blockEntity) {
		blockEntity.lidAnimator.step();
	}

	@Override
	public boolean onSyncedBlockEvent(int type, int data) {
		if (type == 1) {
			this.lidAnimator.setOpen(data > 0);
			return true;
		} else {
			return super.onSyncedBlockEvent(type, data);
		}
	}

	public void onOpen(PlayerEntity player) {
		if (!this.removed && !player.isSpectator()) {
			this.stateManager.openChest(player, this.getWorld(), this.getPos(), this.getCachedState());
		}
	}

	public void onClose(PlayerEntity player) {
		if (!this.removed && !player.isSpectator()) {
			this.stateManager.closeChest(player, this.getWorld(), this.getPos(), this.getCachedState());
		}
	}

	public boolean canPlayerUse(PlayerEntity player) {
		return this.world.getBlockEntity(this.pos) != this
			? false
			: !(player.squaredDistanceTo((double)this.pos.getX() + 0.5, (double)this.pos.getY() + 0.5, (double)this.pos.getZ() + 0.5) > 64.0);
	}

	public void onScheduledTick() {
		if (!this.removed) {
			this.stateManager.updateViewerCount(this.getWorld(), this.getPos(), this.getCachedState());
		}
	}

	@Override
	public float getAnimationProgress(float tickDelta) {
		return this.lidAnimator.getProgress(tickDelta);
	}
}
