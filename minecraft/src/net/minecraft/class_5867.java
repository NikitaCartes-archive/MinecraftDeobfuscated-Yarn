package net.minecraft;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;

public class class_5867 implements AutoCloseable {
	private final WorldAccess field_29037;
	private final Long2ObjectMap<ChunkSection> field_29038 = new Long2ObjectOpenHashMap<>();
	@Nullable
	private ChunkSection field_29039;
	private long field_29040;

	public class_5867(WorldAccess worldAccess) {
		this.field_29037 = worldAccess;
	}

	public ChunkSection method_33944(BlockPos blockPos) {
		long l = ChunkSectionPos.toLong(blockPos);
		if (this.field_29039 != null && this.field_29040 == l) {
			return this.field_29039;
		} else {
			this.field_29039 = this.field_29038.computeIfAbsent(l, lx -> {
				Chunk chunk = this.field_29037.getChunk(ChunkSectionPos.getSectionCoord(blockPos.getX()), ChunkSectionPos.getSectionCoord(blockPos.getZ()));
				ChunkSection chunkSection = chunk.getSection(chunk.getSectionIndex(blockPos.getY()));
				chunkSection.lock();
				return chunkSection;
			});
			this.field_29040 = l;
			return this.field_29039;
		}
	}

	public BlockState method_33946(BlockPos blockPos) {
		ChunkSection chunkSection = this.method_33944(blockPos);
		int i = ChunkSectionPos.getLocalCoord(blockPos.getX());
		int j = ChunkSectionPos.getLocalCoord(blockPos.getY());
		int k = ChunkSectionPos.getLocalCoord(blockPos.getZ());
		return chunkSection.getBlockState(i, j, k);
	}

	public void close() {
		for (ChunkSection chunkSection : this.field_29038.values()) {
			chunkSection.unlock();
		}
	}
}
