package net.minecraft.client.render.chunk;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.level.ColorResolver;

@Environment(EnvType.CLIENT)
public class ChunkRendererRegion implements BlockRenderView {
	protected final int chunkXOffset;
	protected final int chunkZOffset;
	protected final BlockPos offset;
	protected final int xSize;
	protected final int ySize;
	protected final int zSize;
	protected final WorldChunk[][] chunks;
	protected final BlockState[] blockStates;
	protected final FluidState[] fluidStates;
	protected final World world;

	@Nullable
	public static ChunkRendererRegion create(World world, BlockPos startPos, BlockPos endPos, int chunkRadius) {
		int i = ChunkSectionPos.getSectionCoord(startPos.getX() - chunkRadius);
		int j = ChunkSectionPos.getSectionCoord(startPos.getZ() - chunkRadius);
		int k = ChunkSectionPos.getSectionCoord(endPos.getX() + chunkRadius);
		int l = ChunkSectionPos.getSectionCoord(endPos.getZ() + chunkRadius);
		WorldChunk[][] worldChunks = new WorldChunk[k - i + 1][l - j + 1];

		for (int m = i; m <= k; m++) {
			for (int n = j; n <= l; n++) {
				worldChunks[m - i][n - j] = world.getChunk(m, n);
			}
		}

		if (method_30000(startPos, endPos, i, j, worldChunks)) {
			return null;
		} else {
			int m = 1;
			BlockPos blockPos = startPos.add(-1, -1, -1);
			BlockPos blockPos2 = endPos.add(1, 1, 1);
			return new ChunkRendererRegion(world, i, j, worldChunks, blockPos, blockPos2);
		}
	}

	public static boolean method_30000(BlockPos blockPos, BlockPos blockPos2, int i, int j, WorldChunk[][] worldChunks) {
		for (int k = ChunkSectionPos.getSectionCoord(blockPos.getX()); k <= ChunkSectionPos.getSectionCoord(blockPos2.getX()); k++) {
			for (int l = ChunkSectionPos.getSectionCoord(blockPos.getZ()); l <= ChunkSectionPos.getSectionCoord(blockPos2.getZ()); l++) {
				WorldChunk worldChunk = worldChunks[k - i][l - j];
				if (!worldChunk.areSectionsEmptyBetween(blockPos.getY(), blockPos2.getY())) {
					return false;
				}
			}
		}

		return true;
	}

	public ChunkRendererRegion(World world, int chunkX, int chunkZ, WorldChunk[][] chunks, BlockPos startPos, BlockPos endPos) {
		this.world = world;
		this.chunkXOffset = chunkX;
		this.chunkZOffset = chunkZ;
		this.chunks = chunks;
		this.offset = startPos;
		this.xSize = endPos.getX() - startPos.getX() + 1;
		this.ySize = endPos.getY() - startPos.getY() + 1;
		this.zSize = endPos.getZ() - startPos.getZ() + 1;
		this.blockStates = new BlockState[this.xSize * this.ySize * this.zSize];
		this.fluidStates = new FluidState[this.xSize * this.ySize * this.zSize];

		for (BlockPos blockPos : BlockPos.iterate(startPos, endPos)) {
			int i = ChunkSectionPos.getSectionCoord(blockPos.getX()) - chunkX;
			int j = ChunkSectionPos.getSectionCoord(blockPos.getZ()) - chunkZ;
			WorldChunk worldChunk = chunks[i][j];
			int k = this.getIndex(blockPos);
			this.blockStates[k] = worldChunk.getBlockState(blockPos);
			this.fluidStates[k] = worldChunk.getFluidState(blockPos);
		}
	}

	protected final int getIndex(BlockPos pos) {
		return this.getIndex(pos.getX(), pos.getY(), pos.getZ());
	}

	protected int getIndex(int x, int y, int z) {
		int i = x - this.offset.getX();
		int j = y - this.offset.getY();
		int k = z - this.offset.getZ();
		return k * this.xSize * this.ySize + j * this.xSize + i;
	}

	@Override
	public BlockState getBlockState(BlockPos pos) {
		return this.blockStates[this.getIndex(pos)];
	}

	@Override
	public FluidState getFluidState(BlockPos pos) {
		return this.fluidStates[this.getIndex(pos)];
	}

	@Override
	public float getBrightness(Direction direction, boolean shaded) {
		return this.world.getBrightness(direction, shaded);
	}

	@Override
	public LightingProvider getLightingProvider() {
		return this.world.getLightingProvider();
	}

	@Nullable
	@Override
	public BlockEntity getBlockEntity(BlockPos pos) {
		return this.getBlockEntity(pos, WorldChunk.CreationType.IMMEDIATE);
	}

	@Nullable
	public BlockEntity getBlockEntity(BlockPos pos, WorldChunk.CreationType creationType) {
		int i = ChunkSectionPos.getSectionCoord(pos.getX()) - this.chunkXOffset;
		int j = ChunkSectionPos.getSectionCoord(pos.getZ()) - this.chunkZOffset;
		return this.chunks[i][j].getBlockEntity(pos, creationType);
	}

	@Override
	public int getColor(BlockPos pos, ColorResolver colorResolver) {
		return this.world.getColor(pos, colorResolver);
	}

	@Override
	public int getBottomY() {
		return this.world.getBottomY();
	}

	@Override
	public int getHeight() {
		return this.world.getHeight();
	}
}
