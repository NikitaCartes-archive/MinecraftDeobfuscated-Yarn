package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.EmptyBlockView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.RandomDimension;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;

public class class_5071 extends RandomDimension {
	public class_5071(World world, DimensionType dimensionType) {
		super(world, dimensionType, 1.5F);
	}

	@Override
	public ChunkGenerator<?> createChunkGenerator() {
		return new class_5071.class_5072(this.field_23566, method_26572(Biomes.THE_VOID), class_5099.field_23565);
	}

	@Override
	public float getSkyAngle(long timeOfDay, float tickDelta) {
		return 0.0F;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public Vec3d modifyFogColor(Vec3d vec3d, float tickDelta) {
		return vec3d;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean isFogThick(int x, int z) {
		return false;
	}

	public static class class_5072 extends ChunkGenerator<class_5099> {
		public class_5072(IWorld iWorld, BiomeSource biomeSource, class_5099 arg) {
			super(iWorld, biomeSource, arg);
		}

		@Override
		public void buildSurface(ChunkRegion region, Chunk chunk) {
		}

		@Override
		public void carve(BiomeAccess biomeAccess, Chunk chunk, GenerationStep.Carver carver) {
		}

		@Override
		public int getSpawnHeight() {
			return 0;
		}

		@Override
		public void populateNoise(IWorld world, Chunk chunk) {
		}

		@Override
		public void generateFeatures(ChunkRegion region) {
			if (region.getCenterChunkX() == 31415 && region.getCenterChunkZ() == 92653) {
				Chunk chunk = region.getChunk(region.getCenterChunkX(), region.getCenterChunkZ());
				BlockPos blockPos = new BlockPos(region.getCenterChunkX() * 16, 100, region.getCenterChunkZ() * 16);
				chunk.setBlockState(new BlockPos(region.getCenterChunkX() * 16, 99, region.getCenterChunkZ() * 16), Blocks.GRASS_BLOCK.getDefaultState(), false);
				chunk.setBlockState(blockPos, Blocks.ACACIA_SIGN.getDefaultState(), false);
				SignBlockEntity signBlockEntity = new SignBlockEntity();
				signBlockEntity.setTextOnRow(1, new LiteralText("Ha! I lied!"));
				signBlockEntity.setTextOnRow(2, new LiteralText("This isn't nothing!"));
				chunk.setBlockEntity(blockPos, signBlockEntity);
			}
		}

		@Override
		public int getHeight(int x, int z, Heightmap.Type heightmapType) {
			return 0;
		}

		@Override
		public BlockView getColumnSample(int x, int z) {
			return EmptyBlockView.INSTANCE;
		}

		@Override
		public ChunkGeneratorType<?, ?> method_26490() {
			return ChunkGeneratorType.field_23445;
		}
	}
}
