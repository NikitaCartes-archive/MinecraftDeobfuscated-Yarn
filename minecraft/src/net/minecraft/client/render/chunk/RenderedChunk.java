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
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.PalettedContainer;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.chunk.DebugChunkGenerator;

@Environment(EnvType.CLIENT)
class RenderedChunk {
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
