package net.minecraft.client.render.chunk;

import it.unimi.dsi.fastutil.longs.Long2ObjectFunction;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;

@Environment(EnvType.CLIENT)
public class ChunkRendererRegionBuilder {
	private final Long2ObjectMap<ChunkRendererRegionBuilder.ClientChunk> chunks = new Long2ObjectOpenHashMap<>();

	@Nullable
	public ChunkRendererRegion build(World world, BlockPos startPos, BlockPos endPos, int offset) {
		int i = ChunkSectionPos.getSectionCoord(startPos.getX() - offset);
		int j = ChunkSectionPos.getSectionCoord(startPos.getZ() - offset);
		int k = ChunkSectionPos.getSectionCoord(endPos.getX() + offset);
		int l = ChunkSectionPos.getSectionCoord(endPos.getZ() + offset);
		ChunkRendererRegionBuilder.ClientChunk[][] clientChunks = new ChunkRendererRegionBuilder.ClientChunk[k - i + 1][l - j + 1];

		for (int m = i; m <= k; m++) {
			for (int n = j; n <= l; n++) {
				clientChunks[m - i][n - j] = this.chunks
					.computeIfAbsent(
						ChunkPos.toLong(m, n),
						(Long2ObjectFunction<? extends ChunkRendererRegionBuilder.ClientChunk>)(pos -> new ChunkRendererRegionBuilder.ClientChunk(
								world.getChunk(ChunkPos.getPackedX(pos), ChunkPos.getPackedZ(pos))
							))
					);
			}
		}

		if (isEmptyBetween(startPos, endPos, i, j, clientChunks)) {
			return null;
		} else {
			RenderedChunk[][] renderedChunks = new RenderedChunk[k - i + 1][l - j + 1];

			for (int n = i; n <= k; n++) {
				for (int o = j; o <= l; o++) {
					renderedChunks[n - i][o - j] = clientChunks[n - i][o - j].getRenderedChunk();
				}
			}

			return new ChunkRendererRegion(world, i, j, renderedChunks);
		}
	}

	private static boolean isEmptyBetween(BlockPos startPos, BlockPos endPos, int offsetX, int offsetZ, ChunkRendererRegionBuilder.ClientChunk[][] chunks) {
		int i = ChunkSectionPos.getSectionCoord(startPos.getX());
		int j = ChunkSectionPos.getSectionCoord(startPos.getZ());
		int k = ChunkSectionPos.getSectionCoord(endPos.getX());
		int l = ChunkSectionPos.getSectionCoord(endPos.getZ());

		for (int m = i; m <= k; m++) {
			for (int n = j; n <= l; n++) {
				WorldChunk worldChunk = chunks[m - offsetX][n - offsetZ].getChunk();
				if (!worldChunk.areSectionsEmptyBetween(startPos.getY(), endPos.getY())) {
					return false;
				}
			}
		}

		return true;
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
