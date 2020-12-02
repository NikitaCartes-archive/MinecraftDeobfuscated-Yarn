package net.minecraft.world;

import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ProtoChunk;

public class ChunkTickScheduler<T> implements TickScheduler<T> {
	protected final Predicate<T> shouldExclude;
	private final ChunkPos pos;
	private final ShortList[] scheduledPositions;
	private HeightLimitView field_27230;

	public ChunkTickScheduler(Predicate<T> shouldExclude, ChunkPos pos, HeightLimitView heightLimitView) {
		this(shouldExclude, pos, new ListTag(), heightLimitView);
	}

	public ChunkTickScheduler(Predicate<T> predicate, ChunkPos chunkPos, ListTag tag, HeightLimitView heightLimitView) {
		this.shouldExclude = predicate;
		this.pos = chunkPos;
		this.field_27230 = heightLimitView;
		this.scheduledPositions = new ShortList[heightLimitView.method_32890()];

		for (int i = 0; i < tag.size(); i++) {
			ListTag listTag = tag.getList(i);

			for (int j = 0; j < listTag.size(); j++) {
				Chunk.getList(this.scheduledPositions, i).add(listTag.getShort(j));
			}
		}
	}

	public ListTag toNbt() {
		return ChunkSerializer.toNbt(this.scheduledPositions);
	}

	public void tick(TickScheduler<T> scheduler, Function<BlockPos, T> dataMapper) {
		for (int i = 0; i < this.scheduledPositions.length; i++) {
			if (this.scheduledPositions[i] != null) {
				for (Short short_ : this.scheduledPositions[i]) {
					BlockPos blockPos = ProtoChunk.joinBlockPos(short_, this.field_27230.getSection(i), this.pos);
					scheduler.schedule(blockPos, (T)dataMapper.apply(blockPos), 0);
				}

				this.scheduledPositions[i].clear();
			}
		}
	}

	@Override
	public boolean isScheduled(BlockPos pos, T object) {
		return false;
	}

	@Override
	public void schedule(BlockPos pos, T object, int delay, TickPriority priority) {
		Chunk.getList(this.scheduledPositions, this.field_27230.getSectionIndex(pos.getY())).add(ProtoChunk.getPackedSectionRelative(pos));
	}

	@Override
	public boolean isTicking(BlockPos pos, T object) {
		return false;
	}
}
