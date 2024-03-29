package net.minecraft.client.render;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
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
	public ChunkBuilder.BuiltChunk[] chunks;

	public BuiltChunkStorage(ChunkBuilder chunkBuilder, World world, int viewDistance, WorldRenderer worldRenderer) {
		this.worldRenderer = worldRenderer;
		this.world = world;
		this.setViewDistance(viewDistance);
		this.createChunks(chunkBuilder);
	}

	protected void createChunks(ChunkBuilder chunkBuilder) {
		if (!MinecraftClient.getInstance().isOnThread()) {
			throw new IllegalStateException("createSections called from wrong thread: " + Thread.currentThread().getName());
		} else {
			int i = this.sizeX * this.sizeY * this.sizeZ;
			this.chunks = new ChunkBuilder.BuiltChunk[i];

			for(int j = 0; j < this.sizeX; ++j) {
				for(int k = 0; k < this.sizeY; ++k) {
					for(int l = 0; l < this.sizeZ; ++l) {
						int m = this.getChunkIndex(j, k, l);
						this.chunks[m] = chunkBuilder.new BuiltChunk(m, j * 16, this.world.getBottomY() + k * 16, l * 16);
					}
				}
			}
		}
	}

	public void clear() {
		for(ChunkBuilder.BuiltChunk builtChunk : this.chunks) {
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

	public void updateCameraPosition(double x, double z) {
		int i = MathHelper.ceil(x);
		int j = MathHelper.ceil(z);

		for(int k = 0; k < this.sizeX; ++k) {
			int l = this.sizeX * 16;
			int m = i - 8 - l / 2;
			int n = m + Math.floorMod(k * 16 - m, l);

			for(int o = 0; o < this.sizeZ; ++o) {
				int p = this.sizeZ * 16;
				int q = j - 8 - p / 2;
				int r = q + Math.floorMod(o * 16 - q, p);

				for(int s = 0; s < this.sizeY; ++s) {
					int t = this.world.getBottomY() + s * 16;
					ChunkBuilder.BuiltChunk builtChunk = this.chunks[this.getChunkIndex(k, s, o)];
					BlockPos blockPos = builtChunk.getOrigin();
					if (n != blockPos.getX() || t != blockPos.getY() || r != blockPos.getZ()) {
						builtChunk.setOrigin(n, t, r);
					}
				}
			}
		}
	}

	public void scheduleRebuild(int x, int y, int z, boolean important) {
		int i = Math.floorMod(x, this.sizeX);
		int j = Math.floorMod(y - this.world.getBottomSectionCoord(), this.sizeY);
		int k = Math.floorMod(z, this.sizeZ);
		ChunkBuilder.BuiltChunk builtChunk = this.chunks[this.getChunkIndex(i, j, k)];
		builtChunk.scheduleRebuild(important);
	}

	@Nullable
	protected ChunkBuilder.BuiltChunk getRenderedChunk(BlockPos pos) {
		int i = MathHelper.floorDiv(pos.getY() - this.world.getBottomY(), 16);
		if (i >= 0 && i < this.sizeY) {
			int j = MathHelper.floorMod(MathHelper.floorDiv(pos.getX(), 16), this.sizeX);
			int k = MathHelper.floorMod(MathHelper.floorDiv(pos.getZ(), 16), this.sizeZ);
			return this.chunks[this.getChunkIndex(j, i, k)];
		} else {
			return null;
		}
	}
}
