package net.minecraft.client.render;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class BuiltChunkStorage {
	protected final WorldRenderer worldRenderer;
	protected final World world;
	protected int sizeY;
	protected int sizeX;
	protected int sizeZ;
	private int viewDistance;
	private ChunkSectionPos sectionPos;
	public ChunkBuilder.BuiltChunk[] chunks;

	public BuiltChunkStorage(ChunkBuilder chunkBuilder, World world, int viewDistance, WorldRenderer worldRenderer) {
		this.worldRenderer = worldRenderer;
		this.world = world;
		this.setViewDistance(viewDistance);
		this.createChunks(chunkBuilder);
		this.sectionPos = ChunkSectionPos.from(this.viewDistance + 1, 0, this.viewDistance + 1);
	}

	protected void createChunks(ChunkBuilder chunkBuilder) {
		if (!MinecraftClient.getInstance().isOnThread()) {
			throw new IllegalStateException("createSections called from wrong thread: " + Thread.currentThread().getName());
		} else {
			int i = this.sizeX * this.sizeY * this.sizeZ;
			this.chunks = new ChunkBuilder.BuiltChunk[i];

			for (int j = 0; j < this.sizeX; j++) {
				for (int k = 0; k < this.sizeY; k++) {
					for (int l = 0; l < this.sizeZ; l++) {
						int m = this.getChunkIndex(j, k, l);
						this.chunks[m] = chunkBuilder.new BuiltChunk(m, ChunkSectionPos.asLong(j, k + this.world.getBottomSectionCoord(), l));
					}
				}
			}
		}
	}

	public void clear() {
		for (ChunkBuilder.BuiltChunk builtChunk : this.chunks) {
			builtChunk.delete();
		}
	}

	private int getChunkIndex(int x, int y, int z) {
		return (z * this.sizeY + y) * this.sizeX + x;
	}

	protected void setViewDistance(int viewDistance) {
		int i = viewDistance * 2 + 1;
		this.sizeX = i;
		this.sizeY = this.world.countVerticalSections();
		this.sizeZ = i;
		this.viewDistance = viewDistance;
	}

	public int getViewDistance() {
		return this.viewDistance;
	}

	public HeightLimitView getWorld() {
		return this.world;
	}

	public void updateCameraPosition(ChunkSectionPos sectionPos) {
		for (int i = 0; i < this.sizeX; i++) {
			int j = sectionPos.getSectionX() - this.viewDistance;
			int k = j + Math.floorMod(i - j, this.sizeX);

			for (int l = 0; l < this.sizeZ; l++) {
				int m = sectionPos.getSectionZ() - this.viewDistance;
				int n = m + Math.floorMod(l - m, this.sizeZ);

				for (int o = 0; o < this.sizeY; o++) {
					int p = this.world.getBottomSectionCoord() + o;
					ChunkBuilder.BuiltChunk builtChunk = this.chunks[this.getChunkIndex(i, o, l)];
					long q = builtChunk.getSectionPos();
					if (q != ChunkSectionPos.asLong(k, p, n)) {
						builtChunk.setSectionPos(ChunkSectionPos.asLong(k, p, n));
					}
				}
			}
		}

		this.sectionPos = sectionPos;
		this.worldRenderer.getChunkRenderingDataPreparer().scheduleTerrainUpdate();
	}

	public ChunkSectionPos getSectionPos() {
		return this.sectionPos;
	}

	public void scheduleRebuild(int x, int y, int z, boolean important) {
		ChunkBuilder.BuiltChunk builtChunk = this.getRenderedChunk(x, y, z);
		if (builtChunk != null) {
			builtChunk.scheduleRebuild(important);
		}
	}

	@Nullable
	protected ChunkBuilder.BuiltChunk getRenderedChunk(BlockPos blockPos) {
		return this.getRenderedChunk(ChunkSectionPos.toLong(blockPos));
	}

	@Nullable
	protected ChunkBuilder.BuiltChunk getRenderedChunk(long sectionPos) {
		int i = ChunkSectionPos.unpackX(sectionPos);
		int j = ChunkSectionPos.unpackY(sectionPos);
		int k = ChunkSectionPos.unpackZ(sectionPos);
		return this.getRenderedChunk(i, j, k);
	}

	@Nullable
	private ChunkBuilder.BuiltChunk getRenderedChunk(int sectionX, int sectionY, int sectionZ) {
		if (!this.isSectionWithinViewDistance(sectionX, sectionY, sectionZ)) {
			return null;
		} else {
			int i = sectionY - this.world.getBottomSectionCoord();
			int j = Math.floorMod(sectionX, this.sizeX);
			int k = Math.floorMod(sectionZ, this.sizeZ);
			return this.chunks[this.getChunkIndex(j, i, k)];
		}
	}

	private boolean isSectionWithinViewDistance(int sectionX, int sectionY, int sectionZ) {
		if (sectionY >= this.world.getBottomSectionCoord() && sectionY <= this.world.getTopSectionCoord()) {
			return sectionX < this.sectionPos.getSectionX() - this.viewDistance || sectionX > this.sectionPos.getSectionX() + this.viewDistance
				? false
				: sectionZ >= this.sectionPos.getSectionZ() - this.viewDistance && sectionZ <= this.sectionPos.getSectionZ() + this.viewDistance;
		} else {
			return false;
		}
	}
}
