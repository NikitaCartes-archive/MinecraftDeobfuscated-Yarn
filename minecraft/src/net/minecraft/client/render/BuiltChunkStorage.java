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
	private ChunkSectionPos field_53952;
	public ChunkBuilder.BuiltChunk[] chunks;

	public BuiltChunkStorage(ChunkBuilder chunkBuilder, World world, int viewDistance, WorldRenderer worldRenderer) {
		this.worldRenderer = worldRenderer;
		this.world = world;
		this.setViewDistance(viewDistance);
		this.createChunks(chunkBuilder);
		this.field_53952 = ChunkSectionPos.from(this.viewDistance + 1, 0, this.viewDistance + 1);
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

	public void updateCameraPosition(ChunkSectionPos chunkSectionPos) {
		for (int i = 0; i < this.sizeX; i++) {
			int j = chunkSectionPos.getSectionX() - this.viewDistance;
			int k = j + Math.floorMod(i - j, this.sizeX);

			for (int l = 0; l < this.sizeZ; l++) {
				int m = chunkSectionPos.getSectionZ() - this.viewDistance;
				int n = m + Math.floorMod(l - m, this.sizeZ);

				for (int o = 0; o < this.sizeY; o++) {
					int p = this.world.getBottomSectionCoord() + o;
					ChunkBuilder.BuiltChunk builtChunk = this.chunks[this.getChunkIndex(i, o, l)];
					long q = builtChunk.method_62975();
					if (q != ChunkSectionPos.asLong(k, p, n)) {
						builtChunk.method_62973(ChunkSectionPos.asLong(k, p, n));
					}
				}
			}
		}

		this.field_53952 = chunkSectionPos;
		this.worldRenderer.getChunkRenderingDataPreparer().scheduleTerrainUpdate();
	}

	public ChunkSectionPos method_62966() {
		return this.field_53952;
	}

	public void scheduleRebuild(int x, int y, int z, boolean important) {
		ChunkBuilder.BuiltChunk builtChunk = this.method_62964(x, y, z);
		if (builtChunk != null) {
			builtChunk.scheduleRebuild(important);
		}
	}

	@Nullable
	protected ChunkBuilder.BuiltChunk getRenderedChunk(BlockPos blockPos) {
		return this.method_62963(ChunkSectionPos.toLong(blockPos));
	}

	@Nullable
	protected ChunkBuilder.BuiltChunk method_62963(long l) {
		int i = ChunkSectionPos.unpackX(l);
		int j = ChunkSectionPos.unpackY(l);
		int k = ChunkSectionPos.unpackZ(l);
		return this.method_62964(i, j, k);
	}

	@Nullable
	private ChunkBuilder.BuiltChunk method_62964(int i, int j, int k) {
		if (!this.method_62965(i, j, k)) {
			return null;
		} else {
			int l = j - this.world.getBottomSectionCoord();
			int m = Math.floorMod(i, this.sizeX);
			int n = Math.floorMod(k, this.sizeZ);
			return this.chunks[this.getChunkIndex(m, l, n)];
		}
	}

	private boolean method_62965(int i, int j, int k) {
		if (j >= this.world.getBottomSectionCoord() && j <= this.world.getTopSectionCoord()) {
			return i < this.field_53952.getSectionX() - this.viewDistance || i > this.field_53952.getSectionX() + this.viewDistance
				? false
				: k >= this.field_53952.getSectionZ() - this.viewDistance && k <= this.field_53952.getSectionZ() + this.viewDistance;
		} else {
			return false;
		}
	}
}
