package net.minecraft.world.block;

import com.mojang.logging.LogUtils;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.slf4j.Logger;

public class ChainRestrictedNeighborUpdater implements NeighborUpdater {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final World world;
	private final int maxChainDepth;
	private final ArrayDeque<ChainRestrictedNeighborUpdater.Entry> queue = new ArrayDeque();
	private final List<ChainRestrictedNeighborUpdater.Entry> pending = new ArrayList();
	private int depth = 0;

	public ChainRestrictedNeighborUpdater(World world, int maxChainDepth) {
		this.world = world;
		this.maxChainDepth = maxChainDepth;
	}

	@Override
	public void replaceWithStateForNeighborUpdate(Direction direction, BlockState neighborState, BlockPos pos, BlockPos neighborPos, int flags, int maxUpdateDepth) {
		this.enqueue(
			pos, new ChainRestrictedNeighborUpdater.StateReplacementEntry(direction, neighborState, pos.toImmutable(), neighborPos.toImmutable(), flags, maxUpdateDepth)
		);
	}

	@Override
	public void updateNeighbor(BlockPos pos, Block sourceBlock, @Nullable WireOrientation orientation) {
		this.enqueue(pos, new ChainRestrictedNeighborUpdater.SimpleEntry(pos, sourceBlock, orientation));
	}

	@Override
	public void updateNeighbor(BlockState state, BlockPos pos, Block sourceBlock, @Nullable WireOrientation orientation, boolean notify) {
		this.enqueue(pos, new ChainRestrictedNeighborUpdater.StatefulEntry(state, pos.toImmutable(), sourceBlock, orientation, notify));
	}

	@Override
	public void updateNeighbors(BlockPos pos, Block sourceBlock, @Nullable Direction except, @Nullable WireOrientation orientation) {
		this.enqueue(pos, new ChainRestrictedNeighborUpdater.SixWayEntry(pos.toImmutable(), sourceBlock, orientation, except));
	}

	private void enqueue(BlockPos pos, ChainRestrictedNeighborUpdater.Entry entry) {
		boolean bl = this.depth > 0;
		boolean bl2 = this.maxChainDepth >= 0 && this.depth >= this.maxChainDepth;
		this.depth++;
		if (!bl2) {
			if (bl) {
				this.pending.add(entry);
			} else {
				this.queue.push(entry);
			}
		} else if (this.depth - 1 == this.maxChainDepth) {
			LOGGER.error("Too many chained neighbor updates. Skipping the rest. First skipped position: " + pos.toShortString());
		}

		if (!bl) {
			this.runQueuedUpdates();
		}
	}

	private void runQueuedUpdates() {
		try {
			while (!this.queue.isEmpty() || !this.pending.isEmpty()) {
				for (int i = this.pending.size() - 1; i >= 0; i--) {
					this.queue.push((ChainRestrictedNeighborUpdater.Entry)this.pending.get(i));
				}

				this.pending.clear();
				ChainRestrictedNeighborUpdater.Entry entry = (ChainRestrictedNeighborUpdater.Entry)this.queue.peek();

				while (this.pending.isEmpty()) {
					if (!entry.update(this.world)) {
						this.queue.pop();
						break;
					}
				}
			}
		} finally {
			this.queue.clear();
			this.pending.clear();
			this.depth = 0;
		}
	}

	interface Entry {
		boolean update(World world);
	}

	static record SimpleEntry(BlockPos pos, Block sourceBlock, @Nullable WireOrientation orientation) implements ChainRestrictedNeighborUpdater.Entry {
		@Override
		public boolean update(World world) {
			BlockState blockState = world.getBlockState(this.pos);
			NeighborUpdater.tryNeighborUpdate(world, blockState, this.pos, this.sourceBlock, this.orientation, false);
			return false;
		}
	}

	static final class SixWayEntry implements ChainRestrictedNeighborUpdater.Entry {
		private final BlockPos pos;
		private final Block sourceBlock;
		@Nullable
		private WireOrientation orientation;
		@Nullable
		private final Direction except;
		private int currentDirectionIndex = 0;

		SixWayEntry(BlockPos pos, Block sourceBlock, @Nullable WireOrientation orientation, @Nullable Direction except) {
			this.pos = pos;
			this.sourceBlock = sourceBlock;
			this.orientation = orientation;
			this.except = except;
			if (NeighborUpdater.UPDATE_ORDER[this.currentDirectionIndex] == except) {
				this.currentDirectionIndex++;
			}
		}

		@Override
		public boolean update(World world) {
			Direction direction = NeighborUpdater.UPDATE_ORDER[this.currentDirectionIndex++];
			BlockPos blockPos = this.pos.offset(direction);
			BlockState blockState = world.getBlockState(blockPos);
			WireOrientation wireOrientation = null;
			if (world.getEnabledFeatures().contains(FeatureFlags.REDSTONE_EXPERIMENTS)) {
				if (this.orientation == null) {
					this.orientation = OrientationHelper.getEmissionOrientation(world, this.except == null ? null : this.except.getOpposite(), null);
				}

				wireOrientation = this.orientation.withFront(direction);
			}

			NeighborUpdater.tryNeighborUpdate(world, blockState, blockPos, this.sourceBlock, wireOrientation, false);
			if (this.currentDirectionIndex < NeighborUpdater.UPDATE_ORDER.length && NeighborUpdater.UPDATE_ORDER[this.currentDirectionIndex] == this.except) {
				this.currentDirectionIndex++;
			}

			return this.currentDirectionIndex < NeighborUpdater.UPDATE_ORDER.length;
		}
	}

	static record StateReplacementEntry(Direction direction, BlockState neighborState, BlockPos pos, BlockPos neighborPos, int updateFlags, int updateLimit)
		implements ChainRestrictedNeighborUpdater.Entry {
		@Override
		public boolean update(World world) {
			NeighborUpdater.replaceWithStateForNeighborUpdate(world, this.direction, this.neighborState, this.pos, this.neighborPos, this.updateFlags, this.updateLimit);
			return false;
		}
	}

	static record StatefulEntry(BlockState state, BlockPos pos, Block sourceBlock, @Nullable WireOrientation orientation, boolean movedByPiston)
		implements ChainRestrictedNeighborUpdater.Entry {
		@Override
		public boolean update(World world) {
			NeighborUpdater.tryNeighborUpdate(world, this.state, this.pos, this.sourceBlock, this.orientation, this.movedByPiston);
			return false;
		}
	}
}
