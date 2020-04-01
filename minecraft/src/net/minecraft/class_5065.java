package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.OverworldDimension;
import net.minecraft.world.dimension.RandomDimension;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
import net.minecraft.world.gen.chunk.OverworldChunkGenerator;
import net.minecraft.world.gen.chunk.OverworldChunkGeneratorConfig;

public class class_5065 extends OverworldDimension {
	private static final Vector3f field_23544 = new Vector3f(1.0F, 0.0F, 0.0F);
	private static final Vector3f field_23545 = new Vector3f(0.0F, 0.0F, 1.0F);

	public class_5065(World world, DimensionType dimensionType) {
		super(world, dimensionType);
	}

	@Override
	public ChunkGenerator<? extends ChunkGeneratorConfig> createChunkGenerator() {
		return new class_5065.class_5066(this.world, RandomDimension.method_26573(this.world.getSeed()), ChunkGeneratorType.SURFACE.createConfig());
	}

	@Environment(EnvType.CLIENT)
	private static Vector3f method_26551(BlockPos blockPos) {
		if (blockPos.getX() > 0) {
			return field_23544;
		} else {
			return blockPos.getX() < 0 ? field_23545 : field_23480;
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public Vector3f method_26495(BlockState blockState, BlockPos blockPos) {
		return method_26551(blockPos);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public <T extends LivingEntity> Vector3f method_26494(T livingEntity) {
		return method_26551(livingEntity.getBlockPos());
	}

	public static class class_5066 extends OverworldChunkGenerator {
		public class_5066(IWorld iWorld, BiomeSource biomeSource, OverworldChunkGeneratorConfig overworldChunkGeneratorConfig) {
			super(iWorld, biomeSource, overworldChunkGeneratorConfig);
		}

		@Override
		public void generateFeatures(ChunkRegion region) {
			super.generateFeatures(region);
			int i = region.getCenterChunkX();
			int j = region.getCenterChunkZ();
			if (i == 0) {
				BlockPos.Mutable mutable = new BlockPos.Mutable();

				for (int k = 0; k < 16; k++) {
					for (int l = 0; l < 256; l++) {
						region.setBlockState(mutable.set(0, l, 16 * j + k), Blocks.BEDROCK.getDefaultState(), 4);
					}
				}

				if (j == 0) {
					BlockPos blockPos = region.getTopPosition(Heightmap.Type.WORLD_SURFACE_WG, mutable.set(0, 0, 0));
					BlockState blockState = Blocks.IRON_DOOR.getDefaultState().with(DoorBlock.FACING, Direction.EAST);
					region.setBlockState(blockPos, blockState.with(DoorBlock.HALF, DoubleBlockHalf.LOWER), 4);
					region.setBlockState(blockPos.up(), blockState.with(DoorBlock.HALF, DoubleBlockHalf.UPPER), 4);
				}
			}
		}

		@Override
		public ChunkGeneratorType<?, ?> method_26490() {
			return ChunkGeneratorType.field_23477;
		}
	}
}
