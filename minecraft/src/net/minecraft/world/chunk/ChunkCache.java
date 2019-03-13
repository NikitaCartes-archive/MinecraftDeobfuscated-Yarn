package net.minecraft.world.chunk;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ExtendedBlockView;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

public class ChunkCache implements ExtendedBlockView {
	protected final int minX;
	protected final int minZ;
	protected final Chunk[][] field_9305;
	protected boolean empty;
	protected final World world;

	public ChunkCache(World world, BlockPos blockPos, BlockPos blockPos2) {
		this.world = world;
		this.minX = blockPos.getX() >> 4;
		this.minZ = blockPos.getZ() >> 4;
		int i = blockPos2.getX() >> 4;
		int j = blockPos2.getZ() >> 4;
		this.field_9305 = new Chunk[i - this.minX + 1][j - this.minZ + 1];
		this.empty = true;

		for (int k = this.minX; k <= i; k++) {
			for (int l = this.minZ; l <= j; l++) {
				this.field_9305[k - this.minX][l - this.minZ] = world.method_8402(k, l, ChunkStatus.FULL, false);
			}
		}

		for (int k = blockPos.getX() >> 4; k <= blockPos2.getX() >> 4; k++) {
			for (int l = blockPos.getZ() >> 4; l <= blockPos2.getZ() >> 4; l++) {
				Chunk chunk = this.field_9305[k - this.minX][l - this.minZ];
				if (chunk != null && !chunk.method_12228(blockPos.getY(), blockPos2.getY())) {
					this.empty = false;
					return;
				}
			}
		}
	}

	@Nullable
	private Chunk method_18474(BlockPos blockPos) {
		int i = (blockPos.getX() >> 4) - this.minX;
		int j = (blockPos.getZ() >> 4) - this.minZ;
		return i >= 0 && i < this.field_9305.length && j >= 0 && j < this.field_9305[i].length ? this.field_9305[i][j] : null;
	}

	@Nullable
	@Override
	public BlockEntity method_8321(BlockPos blockPos) {
		Chunk chunk = this.method_18474(blockPos);
		return chunk == null ? null : chunk.method_8321(blockPos);
	}

	@Override
	public BlockState method_8320(BlockPos blockPos) {
		if (World.method_8518(blockPos)) {
			return Blocks.field_10124.method_9564();
		} else {
			Chunk chunk = this.method_18474(blockPos);
			return chunk != null ? chunk.method_8320(blockPos) : Blocks.field_9987.method_9564();
		}
	}

	@Override
	public FluidState method_8316(BlockPos blockPos) {
		if (World.method_8518(blockPos)) {
			return Fluids.EMPTY.method_15785();
		} else {
			Chunk chunk = this.method_18474(blockPos);
			return chunk != null ? chunk.method_8316(blockPos) : Fluids.EMPTY.method_15785();
		}
	}

	@Override
	public Biome method_8310(BlockPos blockPos) {
		Chunk chunk = this.method_18474(blockPos);
		return chunk == null ? Biomes.field_9451 : chunk.method_16552(blockPos);
	}

	@Override
	public int method_8314(LightType lightType, BlockPos blockPos) {
		return this.world.method_8314(lightType, blockPos);
	}
}
