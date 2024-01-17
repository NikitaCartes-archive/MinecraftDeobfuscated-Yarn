package net.minecraft.block.entity;

import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

/**
 * Handles the viewer count for container block entities, like chests,
 * ender chests, and barrels.
 */
public abstract class ViewerCountManager {
	private static final int SCHEDULE_TICK_DELAY = 5;
	private int viewerCount;
	private double maxBlockInteractionRange;

	/**
	 * Run when this container is opened (when the viewer count becomes nonzero).
	 */
	protected abstract void onContainerOpen(World world, BlockPos pos, BlockState state);

	/**
	 * Run when this container closes (when the viewer count reaches zero).
	 */
	protected abstract void onContainerClose(World world, BlockPos pos, BlockState state);

	/**
	 * Called when the viewer count updates, such as when a player interact with this container
	 * or when {@linkplain #updateViewerCount distance-based checks} are run.
	 */
	protected abstract void onViewerCountUpdate(World world, BlockPos pos, BlockState state, int oldViewerCount, int newViewerCount);

	/**
	 * Determines whether the given player is currently viewing this container.
	 */
	protected abstract boolean isPlayerViewing(PlayerEntity player);

	public void openContainer(PlayerEntity player, World world, BlockPos pos, BlockState state) {
		int i = this.viewerCount++;
		if (i == 0) {
			this.onContainerOpen(world, pos, state);
			world.emitGameEvent(player, GameEvent.CONTAINER_OPEN, pos);
			scheduleBlockTick(world, pos, state);
		}

		this.onViewerCountUpdate(world, pos, state, i, this.viewerCount);
		this.maxBlockInteractionRange = Math.max(player.getBlockInteractionRange(), this.maxBlockInteractionRange);
	}

	public void closeContainer(PlayerEntity player, World world, BlockPos pos, BlockState state) {
		int i = this.viewerCount--;
		if (this.viewerCount == 0) {
			this.onContainerClose(world, pos, state);
			world.emitGameEvent(player, GameEvent.CONTAINER_CLOSE, pos);
			this.maxBlockInteractionRange = 0.0;
		}

		this.onViewerCountUpdate(world, pos, state, i, this.viewerCount);
	}

	private List<PlayerEntity> getViewingPlayers(World world, BlockPos pos) {
		double d = this.maxBlockInteractionRange + 4.0;
		Box box = new Box(pos).expand(d);
		return world.getEntitiesByType(TypeFilter.instanceOf(PlayerEntity.class), box, this::isPlayerViewing);
	}

	public void updateViewerCount(World world, BlockPos pos, BlockState state) {
		List<PlayerEntity> list = this.getViewingPlayers(world, pos);
		this.maxBlockInteractionRange = 0.0;

		for (PlayerEntity playerEntity : list) {
			this.maxBlockInteractionRange = Math.max(playerEntity.getBlockInteractionRange(), this.maxBlockInteractionRange);
		}

		int i = list.size();
		int j = this.viewerCount;
		if (j != i) {
			boolean bl = i != 0;
			boolean bl2 = j != 0;
			if (bl && !bl2) {
				this.onContainerOpen(world, pos, state);
				world.emitGameEvent(null, GameEvent.CONTAINER_OPEN, pos);
			} else if (!bl) {
				this.onContainerClose(world, pos, state);
				world.emitGameEvent(null, GameEvent.CONTAINER_CLOSE, pos);
			}

			this.viewerCount = i;
		}

		this.onViewerCountUpdate(world, pos, state, j, i);
		if (i > 0) {
			scheduleBlockTick(world, pos, state);
		}
	}

	public int getViewerCount() {
		return this.viewerCount;
	}

	private static void scheduleBlockTick(World world, BlockPos pos, BlockState state) {
		world.scheduleBlockTick(pos, state.getBlock(), 5);
	}
}
