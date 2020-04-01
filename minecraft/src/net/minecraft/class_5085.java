package net.minecraft;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.block.enums.DoubleBlockHalf;
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

public class class_5085 extends OverworldDimension {
	public class_5085(World world, DimensionType dimensionType) {
		super(world, dimensionType);
	}

	@Override
	public ChunkGenerator<? extends ChunkGeneratorConfig> createChunkGenerator() {
		return new class_5085.class_5086(this.world, RandomDimension.method_26573(this.world.getSeed()), ChunkGeneratorType.SURFACE.createConfig());
	}

	public static class class_5086 extends OverworldChunkGenerator {
		public class_5086(IWorld iWorld, BiomeSource biomeSource, OverworldChunkGeneratorConfig overworldChunkGeneratorConfig) {
			super(iWorld, biomeSource, overworldChunkGeneratorConfig);
		}

		@Override
		public void generateFeatures(ChunkRegion region) {
			super.generateFeatures(region);
			int i = region.getCenterChunkX();
			int j = region.getCenterChunkZ();
			BlockPos.Mutable mutable = new BlockPos.Mutable();
			BlockState blockState = Blocks.BRICKS.getDefaultState();
			BlockState blockState2 = Blocks.WALL_TORCH.getDefaultState();
			boolean bl = Math.floorMod(i, 4) == 2;
			boolean bl2 = Math.floorMod(j, 4) == 2;

			for (int k = 0; k < 16; k++) {
				for (int l = 0; l < 16; l++) {
					if (l != 0 || k != 0 || !bl || !bl2) {
						region.setBlockState(mutable.set(16 * i + l, 255, 16 * j + k), blockState, 4);
					}
				}
			}

			if (i % 4 == 0) {
				for (int k = 0; k < 16; k++) {
					for (int lx = 0; lx < 256; lx++) {
						region.setBlockState(mutable.set(16 * i, lx, 16 * j + k), blockState, 4);
					}
				}

				if (bl2) {
					BlockPos blockPos = region.getTopPosition(Heightmap.Type.WORLD_SURFACE_WG, new BlockPos(16 * i, 0, 16 * j));
					BlockState blockState3 = Blocks.OAK_DOOR.getDefaultState().with(DoorBlock.FACING, Direction.EAST);
					region.setBlockState(blockPos, blockState3.with(DoorBlock.HALF, DoubleBlockHalf.LOWER), 4);
					BlockPos blockPos2 = blockPos.up();
					region.setBlockState(blockPos2, blockState3.with(DoorBlock.HALF, DoubleBlockHalf.UPPER), 4);
					BlockPos blockPos3 = blockPos2.up();
					region.setBlockState(blockPos3.east(), blockState2.with(WallTorchBlock.FACING, Direction.EAST), 4);
					region.setBlockState(blockPos3.west(), blockState2.with(WallTorchBlock.FACING, Direction.WEST), 4);
				}
			}

			if (j % 4 == 0) {
				for (int k = 0; k < 16; k++) {
					for (int lx = 0; lx < 256; lx++) {
						region.setBlockState(mutable.set(16 * i + k, lx, 16 * j), blockState, 4);
					}
				}

				if (bl) {
					BlockPos blockPos = region.getTopPosition(Heightmap.Type.WORLD_SURFACE_WG, new BlockPos(16 * i, 0, 16 * j));
					BlockState blockState3 = Blocks.OAK_DOOR.getDefaultState().with(DoorBlock.FACING, Direction.SOUTH);
					region.setBlockState(blockPos, blockState3.with(DoorBlock.HALF, DoubleBlockHalf.LOWER), 4);
					BlockPos blockPos2 = blockPos.up();
					region.setBlockState(blockPos2, blockState3.with(DoorBlock.HALF, DoubleBlockHalf.UPPER), 4);
					BlockPos blockPos3 = blockPos2.up();
					region.setBlockState(blockPos3.north(), blockState2.with(WallTorchBlock.FACING, Direction.NORTH), 4);
					region.setBlockState(blockPos3.south(), blockState2.with(WallTorchBlock.FACING, Direction.SOUTH), 4);
				}
			}
		}

		@Override
		public ChunkGeneratorType<?, ?> method_26490() {
			return ChunkGeneratorType.field_23452;
		}
	}
}
