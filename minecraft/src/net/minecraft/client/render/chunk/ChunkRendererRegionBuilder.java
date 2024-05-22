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
	public ChunkRendererRegion build(World world, ChunkSectionPos sectionPos) {
		ChunkRendererRegionBuilder.ClientChunk clientChunk = this.computeClientChunk(world, sectionPos.getSectionX(), sectionPos.getSectionZ());
		if (clientChunk.getChunk().isSectionEmpty(sectionPos.getSectionY())) {
			return null;
		} else {
			int i = sectionPos.getSectionX() - 1;
			int j = sectionPos.getSectionZ() - 1;
			int k = sectionPos.getSectionX() + 1;
			int l = sectionPos.getSectionZ() + 1;
			RenderedChunk[] renderedChunks = new RenderedChunk[9];

			for (int m = j; m <= l; m++) {
				for (int n = i; n <= k; n++) {
					int o = ChunkRendererRegion.getIndex(i, j, n, m);
					ChunkRendererRegionBuilder.ClientChunk clientChunk2 = n == sectionPos.getSectionX() && m == sectionPos.getSectionZ()
						? clientChunk
						: this.computeClientChunk(world, n, m);
					renderedChunks[o] = clientChunk2.getRenderedChunk();
				}
			}

			return new ChunkRendererRegion(world, i, j, renderedChunks);
		}
	}

	private ChunkRendererRegionBuilder.ClientChunk computeClientChunk(World world, int chunkX, int chunkZ) {
		return this.chunks
			.computeIfAbsent(
				ChunkPos.toLong(chunkX, chunkZ),
				(Long2ObjectFunction<? extends ChunkRendererRegionBuilder.ClientChunk>)(chunkPos -> new ChunkRendererRegionBuilder.ClientChunk(
						world.getChunk(ChunkPos.getPackedX(chunkPos), ChunkPos.getPackedZ(chunkPos))
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
