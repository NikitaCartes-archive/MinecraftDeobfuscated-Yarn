package net.minecraft.world.dimension;

import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;

public class TheNetherDimension extends Dimension {
	public TheNetherDimension(World world, DimensionType type) {
		super(world, type, 0.1F);
	}

	@Override
	public boolean hasVisibleSky() {
		return false;
	}

	@Nullable
	@Override
	public BlockPos getSpawningBlockInChunk(long l, ChunkPos chunkPos, boolean bl) {
		return null;
	}

	@Nullable
	@Override
	public BlockPos getTopSpawningBlockPosition(long l, int i, int j, boolean bl) {
		return null;
	}

	@Override
	public float getSkyAngle(long timeOfDay, float tickDelta) {
		return 0.5F;
	}

	@Override
	public boolean canPlayersSleep() {
		return false;
	}

	@Override
	public WorldBorder createWorldBorder() {
		return new WorldBorder() {
			@Override
			public double getCenterX() {
				return super.getCenterX() / 8.0;
			}

			@Override
			public double getCenterZ() {
				return super.getCenterZ() / 8.0;
			}
		};
	}

	@Override
	public DimensionType getType() {
		return DimensionType.THE_NETHER;
	}
}
