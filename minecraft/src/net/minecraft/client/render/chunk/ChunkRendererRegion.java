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
	protected final WorldChunk[][] chunks;
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

		return isEmptyBetween(startPos, endPos, i, j, worldChunks) ? null : new ChunkRendererRegion(world, i, j, worldChunks);
	}

	public static boolean isEmptyBetween(BlockPos from, BlockPos to, int i, int j, WorldChunk[][] chunks) {
		for (int k = ChunkSectionPos.getSectionCoord(from.getX()); k <= ChunkSectionPos.getSectionCoord(to.getX()); k++) {
			for (int l = ChunkSectionPos.getSectionCoord(from.getZ()); l <= ChunkSectionPos.getSectionCoord(to.getZ()); l++) {
				WorldChunk worldChunk = chunks[k - i][l - j];
				if (!worldChunk.areSectionsEmptyBetween(from.getY(), to.getY())) {
					return false;
				}
			}
		}

		return true;
	}

	public ChunkRendererRegion(World world, int chunkX, int chunkZ, WorldChunk[][] chunks) {
		this.world = world;
		this.chunkXOffset = chunkX;
		this.chunkZOffset = chunkZ;
		this.chunks = chunks;
	}

	@Override
	public BlockState getBlockState(BlockPos pos) {
		int i = ChunkSectionPos.getSectionCoord(pos.getX()) - this.chunkXOffset;
		int j = ChunkSectionPos.getSectionCoord(pos.getZ()) - this.chunkZOffset;
		return this.chunks[i][j].getBlockState(pos);
	}

	@Override
	public FluidState getFluidState(BlockPos pos) {
		int i = ChunkSectionPos.getSectionCoord(pos.getX()) - this.chunkXOffset;
		int j = ChunkSectionPos.getSectionCoord(pos.getZ()) - this.chunkZOffset;
		return this.chunks[i][j].getBlockState(pos).getFluidState();
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
