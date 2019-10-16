package net.minecraft.structure;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class BuriedTreasureGenerator {
	public static class Piece extends StructurePiece {
		public Piece(BlockPos blockPos) {
			super(StructurePieceType.BTP, 0);
			this.boundingBox = new BlockBox(blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos.getX(), blockPos.getY(), blockPos.getZ());
		}

		public Piece(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.BTP, compoundTag);
		}

		@Override
		protected void toNbt(CompoundTag compoundTag) {
		}

		@Override
		public boolean generate(IWorld iWorld, ChunkGenerator<?> chunkGenerator, Random random, BlockBox blockBox, ChunkPos chunkPos) {
			int i = iWorld.getTopY(Heightmap.Type.OCEAN_FLOOR_WG, this.boundingBox.minX, this.boundingBox.minZ);
			BlockPos.Mutable mutable = new BlockPos.Mutable(this.boundingBox.minX, i, this.boundingBox.minZ);

			while (mutable.getY() > 0) {
				BlockState blockState = iWorld.getBlockState(mutable);
				BlockState blockState2 = iWorld.getBlockState(mutable.method_10074());
				if (blockState2 == Blocks.SANDSTONE.getDefaultState()
					|| blockState2 == Blocks.STONE.getDefaultState()
					|| blockState2 == Blocks.ANDESITE.getDefaultState()
					|| blockState2 == Blocks.GRANITE.getDefaultState()
					|| blockState2 == Blocks.DIORITE.getDefaultState()) {
					BlockState blockState3 = !blockState.isAir() && !this.isLiquid(blockState) ? blockState : Blocks.SAND.getDefaultState();

					for (Direction direction : Direction.values()) {
						BlockPos blockPos = mutable.offset(direction);
						BlockState blockState4 = iWorld.getBlockState(blockPos);
						if (blockState4.isAir() || this.isLiquid(blockState4)) {
							BlockPos blockPos2 = blockPos.method_10074();
							BlockState blockState5 = iWorld.getBlockState(blockPos2);
							if ((blockState5.isAir() || this.isLiquid(blockState5)) && direction != Direction.UP) {
								iWorld.setBlockState(blockPos, blockState2, 3);
							} else {
								iWorld.setBlockState(blockPos, blockState3, 3);
							}
						}
					}

					this.boundingBox = new BlockBox(mutable.getX(), mutable.getY(), mutable.getZ(), mutable.getX(), mutable.getY(), mutable.getZ());
					return this.addChest(iWorld, blockBox, random, mutable, LootTables.BURIED_TREASURE_CHEST, null);
				}

				mutable.setOffset(0, -1, 0);
			}

			return false;
		}

		private boolean isLiquid(BlockState blockState) {
			return blockState == Blocks.WATER.getDefaultState() || blockState == Blocks.LAVA.getDefaultState();
		}
	}
}
