package net.minecraft.world.block;

import com.mojang.logging.LogUtils;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.slf4j.Logger;

public class ChainRestrictedNeighborUpdater implements NeighborUpdater {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final ServerWorld world;
	private final ArrayDeque<ChainRestrictedNeighborUpdater.Entry> queue = new ArrayDeque();
	private final List<ChainRestrictedNeighborUpdater.Entry> pending = new ArrayList();
	private int depth = 0;

	public ChainRestrictedNeighborUpdater(ServerWorld world) {
		this.world = world;
	}

	@Override
	public void updateNeighbor(BlockPos pos, Block sourceBlock, BlockPos sourcePos) {
		this.enqueue(pos, new ChainRestrictedNeighborUpdater.SimpleEntry(pos, sourceBlock, sourcePos));
	}

	@Override
	public void updateNeighbor(BlockState state, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
		this.enqueue(pos, new ChainRestrictedNeighborUpdater.StatefulEntry(state, pos, sourceBlock, sourcePos, notify));
	}

	@Override
	public void updateNeighbors(BlockPos pos, Block sourceBlock, @Nullable Direction except) {
		this.enqueue(pos, new ChainRestrictedNeighborUpdater.SixWayEntry(pos, sourceBlock, except));
	}

	private void enqueue(BlockPos pos, ChainRestrictedNeighborUpdater.Entry entry) {
		int i = this.world.getServer().getMaxChainedNeighborUpdates();
		boolean bl = this.depth > 0;
		boolean bl2 = i >= 0 && this.depth >= i;
		this.depth++;
		if (!bl2) {
			if (bl) {
				this.pending.add(entry);
			} else {
				this.queue.push(entry);
			}
		} else if (this.depth - 1 == i) {
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
		boolean update(ServerWorld world);
	}

	static record SimpleEntry(BlockPos pos, Block sourceBlock, BlockPos sourcePos) implements ChainRestrictedNeighborUpdater.Entry {
		@Override
		public boolean update(ServerWorld world) {
			BlockState blockState = world.getBlockState(this.pos);
			NeighborUpdater.tryNeighborUpdate(world, blockState, this.pos, this.sourceBlock, this.sourcePos, false);
			return false;
		}
	}

	static final class SixWayEntry implements ChainRestrictedNeighborUpdater.Entry {
		private final BlockPos pos;
		private final Block sourceBlock;
		@Nullable
		private final Direction except;
		private int currentDirectionIndex = 0;

		SixWayEntry(BlockPos pos, Block sourceBlock, @Nullable Direction except) {
			this.pos = pos;
			this.sourceBlock = sourceBlock;
			this.except = except;
			if (NeighborUpdater.UPDATE_ORDER[this.currentDirectionIndex] == except) {
				this.currentDirectionIndex++;
			}
		}

		@Override
		public boolean update(ServerWorld world) {
			BlockPos blockPos = this.pos.offset(NeighborUpdater.UPDATE_ORDER[this.currentDirectionIndex++]);
			BlockState blockState = world.getBlockState(blockPos);
			blockState.neighborUpdate(world, blockPos, this.sourceBlock, this.pos, false);
			if (this.currentDirectionIndex < NeighborUpdater.UPDATE_ORDER.length && NeighborUpdater.UPDATE_ORDER[this.currentDirectionIndex] == this.except) {
				this.currentDirectionIndex++;
			}

			return this.currentDirectionIndex < NeighborUpdater.UPDATE_ORDER.length;
		}
	}

	static record StatefulEntry(BlockState state, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean movedByPiston)
		implements ChainRestrictedNeighborUpdater.Entry {
		@Override
		public boolean update(ServerWorld world) {
			NeighborUpdater.tryNeighborUpdate(world, this.state, this.pos, this.sourceBlock, this.sourcePos, this.movedByPiston);
			return false;
		}
	}
}
