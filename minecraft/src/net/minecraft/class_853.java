package net.minecraft;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ExtendedBlockView;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.WorldChunk;

@Environment(EnvType.CLIENT)
public class class_853 implements ExtendedBlockView {
	protected final int field_4488;
	protected final int field_4487;
	protected final BlockPos field_4481;
	protected final int field_4486;
	protected final int field_4484;
	protected final int field_4482;
	protected final WorldChunk[][] field_4483;
	protected final BlockState[] field_4489;
	protected final FluidState[] field_4485;
	protected final World field_4490;

	@Nullable
	public static class_853 method_3689(World world, BlockPos blockPos, BlockPos blockPos2, int i) {
		int j = blockPos.getX() - i >> 4;
		int k = blockPos.getZ() - i >> 4;
		int l = blockPos2.getX() + i >> 4;
		int m = blockPos2.getZ() + i >> 4;
		WorldChunk[][] worldChunks = new WorldChunk[l - j + 1][m - k + 1];

		for (int n = j; n <= l; n++) {
			for (int o = k; o <= m; o++) {
				worldChunks[n - j][o - k] = world.getChunk(n, o);
			}
		}

		boolean bl = true;

		for (int o = blockPos.getX() >> 4; o <= blockPos2.getX() >> 4; o++) {
			for (int p = blockPos.getZ() >> 4; p <= blockPos2.getZ() >> 4; p++) {
				WorldChunk worldChunk = worldChunks[o - j][p - k];
				if (!worldChunk.method_12228(blockPos.getY(), blockPos2.getY())) {
					bl = false;
				}
			}
		}

		if (bl) {
			return null;
		} else {
			int o = 1;
			BlockPos blockPos3 = blockPos.add(-1, -1, -1);
			BlockPos blockPos4 = blockPos2.add(1, 1, 1);
			return new class_853(world, j, k, worldChunks, blockPos3, blockPos4);
		}
	}

	public class_853(World world, int i, int j, WorldChunk[][] worldChunks, BlockPos blockPos, BlockPos blockPos2) {
		this.field_4490 = world;
		this.field_4488 = i;
		this.field_4487 = j;
		this.field_4483 = worldChunks;
		this.field_4481 = blockPos;
		this.field_4486 = blockPos2.getX() - blockPos.getX() + 1;
		this.field_4484 = blockPos2.getY() - blockPos.getY() + 1;
		this.field_4482 = blockPos2.getZ() - blockPos.getZ() + 1;
		this.field_4489 = new BlockState[this.field_4486 * this.field_4484 * this.field_4482];
		this.field_4485 = new FluidState[this.field_4486 * this.field_4484 * this.field_4482];

		for (BlockPos.Mutable mutable : BlockPos.iterateBoxPositionsMutable(blockPos, blockPos2)) {
			int k = (mutable.getX() >> 4) - i;
			int l = (mutable.getZ() >> 4) - j;
			WorldChunk worldChunk = worldChunks[k][l];
			int m = this.method_3691(mutable);
			this.field_4489[m] = worldChunk.getBlockState(mutable);
			this.field_4485[m] = worldChunk.getFluidState(mutable);
		}
	}

	protected final int method_3691(BlockPos blockPos) {
		return this.method_3690(blockPos.getX(), blockPos.getY(), blockPos.getZ());
	}

	protected int method_3690(int i, int j, int k) {
		int l = i - this.field_4481.getX();
		int m = j - this.field_4481.getY();
		int n = k - this.field_4481.getZ();
		return n * this.field_4486 * this.field_4484 + m * this.field_4486 + l;
	}

	@Override
	public BlockState getBlockState(BlockPos blockPos) {
		return this.field_4489[this.method_3691(blockPos)];
	}

	@Override
	public FluidState getFluidState(BlockPos blockPos) {
		return this.field_4485[this.method_3691(blockPos)];
	}

	@Override
	public int getLightLevel(LightType lightType, BlockPos blockPos) {
		return this.field_4490.getLightLevel(lightType, blockPos);
	}

	@Override
	public Biome getBiome(BlockPos blockPos) {
		int i = (blockPos.getX() >> 4) - this.field_4488;
		int j = (blockPos.getZ() >> 4) - this.field_4487;
		return this.field_4483[i][j].getBiome(blockPos);
	}

	@Nullable
	@Override
	public BlockEntity getBlockEntity(BlockPos blockPos) {
		return this.method_3688(blockPos, WorldChunk.AccessType.CREATE);
	}

	@Nullable
	public BlockEntity method_3688(BlockPos blockPos, WorldChunk.AccessType accessType) {
		int i = (blockPos.getX() >> 4) - this.field_4488;
		int j = (blockPos.getZ() >> 4) - this.field_4487;
		return this.field_4483[i][j].getBlockEntity(blockPos, accessType);
	}
}
