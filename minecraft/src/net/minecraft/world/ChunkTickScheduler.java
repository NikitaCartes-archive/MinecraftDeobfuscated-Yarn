package net.minecraft.world;

import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.TaskPriority;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.chunk.ProtoChunk;

public class ChunkTickScheduler<T> implements TickScheduler<T> {
	protected final Predicate<T> shouldExclude;
	protected final Function<T, Identifier> idToName;
	protected final Function<Identifier, T> nameToId;
	private final ChunkPos pos;
	private final ShortList[] scheduledPositions = new ShortList[16];

	public ChunkTickScheduler(Predicate<T> predicate, Function<T, Identifier> function, Function<Identifier, T> function2, ChunkPos chunkPos) {
		this(predicate, function, function2, chunkPos, new ListTag());
	}

	public ChunkTickScheduler(Predicate<T> predicate, Function<T, Identifier> function, Function<Identifier, T> function2, ChunkPos chunkPos, ListTag listTag) {
		this.shouldExclude = predicate;
		this.idToName = function;
		this.nameToId = function2;
		this.pos = chunkPos;

		for (int i = 0; i < listTag.size(); i++) {
			ListTag listTag2 = listTag.getListTag(i);

			for (int j = 0; j < listTag2.size(); j++) {
				Chunk.getList(this.scheduledPositions, i).add(listTag2.getShort(j));
			}
		}
	}

	public ListTag method_12367() {
		return ChunkSerializer.method_12393(this.scheduledPositions);
	}

	public void tick(TickScheduler<T> tickScheduler, Function<BlockPos, T> function) {
		for (int i = 0; i < this.scheduledPositions.length; i++) {
			if (this.scheduledPositions[i] != null) {
				for (Short short_ : this.scheduledPositions[i]) {
					BlockPos blockPos = ProtoChunk.method_12314(short_, i, this.pos);
					tickScheduler.method_8676(blockPos, (T)function.apply(blockPos), 0);
				}

				this.scheduledPositions[i].clear();
			}
		}
	}

	@Override
	public boolean method_8674(BlockPos blockPos, T object) {
		return false;
	}

	@Override
	public void method_8675(BlockPos blockPos, T object, int i, TaskPriority taskPriority) {
		Chunk.getList(this.scheduledPositions, blockPos.getY() >> 4).add(ProtoChunk.method_12300(blockPos));
	}

	@Override
	public boolean method_8677(BlockPos blockPos, T object) {
		return false;
	}
}
