package net.minecraft.block.entity;

import net.minecraft.class_5575;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

/**
 * Handles the viewer count for chest-like block entities.
 */
public abstract class ChestStateManager {
	private int viewerCount;

	/**
	 * Run when this chest is opened (when the viewer count becomes nonzero).
	 */
	protected abstract void onChestOpened(World world, BlockPos pos, BlockState state);

	/**
	 * Run when this chest closes (when the viewer count reaches zero).
	 */
	protected abstract void onChestClosed(World world, BlockPos pos, BlockState state);

	/**
	 * Run when a player interacts with this chest.
	 */
	protected abstract void onInteracted(World world, BlockPos pos, BlockState state, int oldViewerCount, int newViewerCount);

	/**
	 * Determines whether the given player is currently viewing this chest.
	 */
	protected abstract boolean isPlayerViewing(PlayerEntity player);

	public void openChest(World world, BlockPos pos, BlockState state) {
		int i = this.viewerCount++;
		if (i == 0) {
			this.onChestOpened(world, pos, state);
			scheduleBlockTick(world, pos, state);
		}

		this.onInteracted(world, pos, state, i, this.viewerCount);
	}

	public void closeChest(World world, BlockPos pos, BlockState state) {
		int i = this.viewerCount--;
		if (this.viewerCount == 0) {
			this.onChestClosed(world, pos, state);
		}

		this.onInteracted(world, pos, state, i, this.viewerCount);
	}

	private int getInRangeViewerCount(World world, BlockPos pos) {
		int i = pos.getX();
		int j = pos.getY();
		int k = pos.getZ();
		float f = 5.0F;
		Box box = new Box(
			(double)((float)i - 5.0F),
			(double)((float)j - 5.0F),
			(double)((float)k - 5.0F),
			(double)((float)(i + 1) + 5.0F),
			(double)((float)(j + 1) + 5.0F),
			(double)((float)(k + 1) + 5.0F)
		);
		return world.getEntitiesByType(class_5575.method_31795(PlayerEntity.class), box, this::isPlayerViewing).size();
	}

	public void updateViewerCount(World world, BlockPos pos, BlockState state) {
		int i = this.getInRangeViewerCount(world, pos);
		int j = this.viewerCount;
		if (j != i) {
			boolean bl = i != 0;
			boolean bl2 = j != 0;
			if (bl && !bl2) {
				this.onChestOpened(world, pos, state);
			} else if (!bl) {
				this.onChestClosed(world, pos, state);
			}

			this.viewerCount = i;
		}

		this.onInteracted(world, pos, state, j, i);
		if (i > 0) {
			scheduleBlockTick(world, pos, state);
		}
	}

	public int getViewerCount() {
		return this.viewerCount;
	}

	private static void scheduleBlockTick(World world, BlockPos pos, BlockState state) {
		world.getBlockTickScheduler().schedule(pos, state.getBlock(), 5);
	}
}
