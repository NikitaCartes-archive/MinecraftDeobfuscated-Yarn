package net.minecraft.world.dimension;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.source.BiomeSourceType;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.gen.chunk.CavesChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;

public class TheNetherDimension extends Dimension {
	public TheNetherDimension(World world, DimensionType dimensionType) {
		super(world, dimensionType);
		this.field_13057 = true;
		this.isNether = true;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public Vec3d method_12445(float f, float g) {
		return new Vec3d(0.2F, 0.03F, 0.03F);
	}

	@Override
	protected void method_12447() {
		float f = 0.1F;

		for (int i = 0; i <= 15; i++) {
			float g = 1.0F - (float)i / 15.0F;
			this.field_13053[i] = (1.0F - g) / (g * 3.0F + 1.0F) * 0.9F + 0.1F;
		}
	}

	@Override
	public ChunkGenerator<?> createChunkGenerator() {
		CavesChunkGeneratorConfig cavesChunkGeneratorConfig = ChunkGeneratorType.field_12765.method_12117();
		cavesChunkGeneratorConfig.setDefaultBlock(Blocks.field_10515.getDefaultState());
		cavesChunkGeneratorConfig.setDefaultFluid(Blocks.field_10164.getDefaultState());
		return ChunkGeneratorType.field_12765
			.create(this.world, BiomeSourceType.FIXED.applyConfig(BiomeSourceType.FIXED.getConfig().method_8782(Biomes.field_9461)), cavesChunkGeneratorConfig);
	}

	@Override
	public boolean hasVisibleSky() {
		return false;
	}

	@Nullable
	@Override
	public BlockPos method_12452(ChunkPos chunkPos, boolean bl) {
		return null;
	}

	@Nullable
	@Override
	public BlockPos method_12444(int i, int j, boolean bl) {
		return null;
	}

	@Override
	public float getSkyAngle(long l, float f) {
		return 0.5F;
	}

	@Override
	public boolean method_12448() {
		return false;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_12453(int i, int j) {
		return true;
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
		return DimensionType.field_13076;
	}
}
