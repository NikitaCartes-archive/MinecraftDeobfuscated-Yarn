package net.minecraft.world;

import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.TaskPriority;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ProtoChunk;

public class ChunkTickScheduler<T> implements TickScheduler<T> {
	protected final Predicate<T> shouldExclude;
	private final ChunkPos pos;
	private final ShortList[] scheduledPositions = new ShortList[16];

	public ChunkTickScheduler(Predicate<T> predicate, ChunkPos chunkPos) {
		this(predicate, chunkPos, new ListTag());
	}

	public ChunkTickScheduler(Predicate<T> predicate, ChunkPos chunkPos, ListTag listTag) {
		this.shouldExclude = predicate;
		this.pos = chunkPos;

		for (int i = 0; i < listTag.size(); i++) {
			ListTag listTag2 = listTag.getListTag(i);

			for (int j = 0; j < listTag2.size(); j++) {
				Chunk.getList(this.scheduledPositions, i).add(listTag2.getShort(j));
			}
		}
	}

	public ListTag toNbt() {
		return ChunkSerializer.toNbt(this.scheduledPositions);
	}

	public void tick(TickScheduler<T> tickScheduler, Function<BlockPos, T> function) {
		for (int i = 0; i < this.scheduledPositions.length; i++) {
			if (this.scheduledPositions[i] != null) {
				for (Short short_ : this.scheduledPositions[i]) {
					BlockPos blockPos = ProtoChunk.joinBlockPos(short_, i, this.pos);
					tickScheduler.schedule(blockPos, (T)function.apply(blockPos), 0);
				}

				this.scheduledPositions[i].clear();
			}
		}
	}

	@Override
	public boolean isScheduled(BlockPos blockPos, T object) {
		return false;
	}

	@Override
	public void schedule(BlockPos blockPos, T object, int i, TaskPriority taskPriority) {
		Chunk.getList(this.scheduledPositions, blockPos.getY() >> 4).add(ProtoChunk.getPackedSectionRelative(blockPos));
	}

	@Override
	public boolean isTicking(BlockPos blockPos, T object) {
		return false;
	}

	@Override
	public void scheduleAll(Stream<ScheduledTick<T>> stream) {
		stream.forEach(scheduledTick -> this.schedule(scheduledTick.pos, (T)scheduledTick.getObject(), 0, scheduledTick.priority));
	}
}
