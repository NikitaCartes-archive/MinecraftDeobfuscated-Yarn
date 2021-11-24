package net.minecraft.client.render.chunk;

import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.PalettedContainer;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.gen.chunk.DebugChunkGenerator;
import net.minecraft.world.level.ColorResolver;

@Environment(EnvType.CLIENT)
public class ChunkRendererRegion implements BlockRenderView {
	private final int chunkXOffset;
	private final int chunkZOffset;
	protected final ChunkRendererRegion.RenderedChunk[][] chunks;
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

		if (isEmptyBetween(startPos, endPos, i, j, worldChunks)) {
			return null;
		} else {
			ChunkRendererRegion.RenderedChunk[][] renderedChunks = new ChunkRendererRegion.RenderedChunk[k - i + 1][l - j + 1];

			for (int n = i; n <= k; n++) {
				for (int o = j; o <= l; o++) {
					WorldChunk worldChunk = worldChunks[n - i][o - j];
					renderedChunks[n - i][o - j] = new ChunkRendererRegion.RenderedChunk(worldChunk);
				}
			}

			return new ChunkRendererRegion(world, i, j, renderedChunks);
		}
	}

	private static boolean isEmptyBetween(BlockPos from, BlockPos to, int i, int j, WorldChunk[][] chunks) {
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

	private ChunkRendererRegion(World world, int chunkX, int chunkZ, ChunkRendererRegion.RenderedChunk[][] chunks) {
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
		int i = ChunkSectionPos.getSectionCoord(pos.getX()) - this.chunkXOffset;
		int j = ChunkSectionPos.getSectionCoord(pos.getZ()) - this.chunkZOffset;
		return this.chunks[i][j].getBlockEntity(pos);
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

	@Environment(EnvType.CLIENT)
	static final class RenderedChunk {
		private final Map<BlockPos, BlockEntity> blockEntities;
		@Nullable
		private final List<PalettedContainer<BlockState>> blockStateContainers;
		private final boolean debugWorld;
		private final WorldChunk chunk;

		RenderedChunk(WorldChunk chunk) {
			this.chunk = chunk;
			this.debugWorld = chunk.getWorld().isDebugWorld();
			this.blockEntities = ImmutableMap.copyOf(chunk.getBlockEntities());
			if (chunk instanceof EmptyChunk) {
				this.blockStateContainers = null;
			} else {
				ChunkSection[] chunkSections = chunk.getSectionArray();
				this.blockStateContainers = new ArrayList(chunkSections.length);

				for (ChunkSection chunkSection : chunkSections) {
					this.blockStateContainers.add(chunkSection.isEmpty() ? null : chunkSection.getBlockStateContainer().copy());
				}
			}
		}

		@Nullable
		public BlockEntity getBlockEntity(BlockPos pos) {
			return (BlockEntity)this.blockEntities.get(pos);
		}

		public BlockState getBlockState(BlockPos pos) {
			int i = pos.getX();
			int j = pos.getY();
			int k = pos.getZ();
			if (this.debugWorld) {
				BlockState blockState = null;
				if (j == 60) {
					blockState = Blocks.BARRIER.getDefaultState();
				}

				if (j == 70) {
					blockState = DebugChunkGenerator.getBlockState(i, k);
				}

				return blockState == null ? Blocks.AIR.getDefaultState() : blockState;
			} else if (this.blockStateContainers == null) {
				return Blocks.AIR.getDefaultState();
			} else {
				try {
					int l = this.chunk.getSectionIndex(j);
					if (l >= 0 && l < this.blockStateContainers.size()) {
						PalettedContainer<BlockState> palettedContainer = (PalettedContainer<BlockState>)this.blockStateContainers.get(l);
						if (palettedContainer != null) {
							return palettedContainer.get(i & 15, j & 15, k & 15);
						}
					}

					return Blocks.AIR.getDefaultState();
				} catch (Throwable var8) {
					CrashReport crashReport = CrashReport.create(var8, "Getting block state");
					CrashReportSection crashReportSection = crashReport.addElement("Block being got");
					crashReportSection.add("Location", (CrashCallable<String>)(() -> CrashReportSection.createPositionString(this.chunk, i, j, k)));
					throw new CrashException(crashReport);
				}
			}
		}
	}
}
