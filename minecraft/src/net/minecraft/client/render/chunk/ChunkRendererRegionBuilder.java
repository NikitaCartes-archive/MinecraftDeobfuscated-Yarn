package net.minecraft.client.render.chunk;

import it.unimi.dsi.fastutil.longs.Long2ObjectFunction;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;

@Environment(EnvType.CLIENT)
public class ChunkRendererRegionBuilder {
	private final Long2ObjectMap<ChunkRendererRegionBuilder.ClientChunk> chunks = new Long2ObjectOpenHashMap<>();

	@Nullable
	public ChunkRendererRegion build(World world, ChunkSectionPos chunkSectionPos) {
		ChunkRendererRegionBuilder.ClientChunk clientChunk = this.method_60900(world, chunkSectionPos.getSectionX(), chunkSectionPos.getSectionZ());
		if (clientChunk.getChunk().method_60791(chunkSectionPos.getSectionY())) {
			return null;
		} else {
			int i = chunkSectionPos.getSectionX() - 1;
			int j = chunkSectionPos.getSectionZ() - 1;
			int k = chunkSectionPos.getSectionX() + 1;
			int l = chunkSectionPos.getSectionZ() + 1;
			RenderedChunk[] renderedChunks = new RenderedChunk[9];

			for (int m = j; m <= l; m++) {
				for (int n = i; n <= k; n++) {
					int o = ChunkRendererRegion.method_60899(i, j, n, m);
					ChunkRendererRegionBuilder.ClientChunk clientChunk2 = n == chunkSectionPos.getSectionX() && m == chunkSectionPos.getSectionZ()
						? clientChunk
						: this.method_60900(world, n, m);
					renderedChunks[o] = clientChunk2.getRenderedChunk();
				}
			}

			return new ChunkRendererRegion(world, i, j, renderedChunks);
		}
	}

	private ChunkRendererRegionBuilder.ClientChunk method_60900(World world, int i, int j) {
		return this.chunks
			.computeIfAbsent(
				ChunkPos.toLong(i, j),
				(Long2ObjectFunction<? extends ChunkRendererRegionBuilder.ClientChunk>)(l -> new ChunkRendererRegionBuilder.ClientChunk(
						world.getChunk(ChunkPos.getPackedX(l), ChunkPos.getPackedZ(l))
					))
			);
	}

	@Environment(EnvType.CLIENT)
	static final class ClientChunk {
		private final WorldChunk chunk;
		@Nullable
		private RenderedChunk renderedChunk;

		ClientChunk(WorldChunk chunk) {
			this.chunk = chunk;
		}

		public WorldChunk getChunk() {
			return this.chunk;
		}

		public RenderedChunk getRenderedChunk() {
			if (this.renderedChunk == null) {
				this.renderedChunk = new RenderedChunk(this.chunk);
			}

			return this.renderedChunk;
		}
	}
}
