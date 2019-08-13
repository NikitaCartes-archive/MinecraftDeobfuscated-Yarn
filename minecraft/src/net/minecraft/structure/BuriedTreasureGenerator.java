package net.minecraft.structure;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.loot.LootTables;

public class BuriedTreasureGenerator {
	public static class Piece extends StructurePiece {
		public Piece(BlockPos blockPos) {
			super(StructurePieceType.BURIED_TREASURE, 0);
			this.boundingBox = new MutableIntBoundingBox(blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos.getX(), blockPos.getY(), blockPos.getZ());
		}

		public Piece(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.BURIED_TREASURE, compoundTag);
		}

		@Override
		protected void toNbt(CompoundTag compoundTag) {
		}

		@Override
		public boolean generate(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
			int i = iWorld.getTop(Heightmap.Type.field_13195, this.boundingBox.minX, this.boundingBox.minZ);
			BlockPos.Mutable mutable = new BlockPos.Mutable(this.boundingBox.minX, i, this.boundingBox.minZ);

			while (mutable.getY() > 0) {
				BlockState blockState = iWorld.getBlockState(mutable);
				BlockState blockState2 = iWorld.getBlockState(mutable.down());
				if (blockState2 == Blocks.field_9979.getDefaultState()
					|| blockState2 == Blocks.field_10340.getDefaultState()
					|| blockState2 == Blocks.field_10115.getDefaultState()
					|| blockState2 == Blocks.field_10474.getDefaultState()
					|| blockState2 == Blocks.field_10508.getDefaultState()) {
					BlockState blockState3 = !blockState.isAir() && !this.isLiquid(blockState) ? blockState : Blocks.field_10102.getDefaultState();

					for (Direction direction : Direction.values()) {
						BlockPos blockPos = mutable.offset(direction);
						BlockState blockState4 = iWorld.getBlockState(blockPos);
						if (blockState4.isAir() || this.isLiquid(blockState4)) {
							BlockPos blockPos2 = blockPos.down();
							BlockState blockState5 = iWorld.getBlockState(blockPos2);
							if ((blockState5.isAir() || this.isLiquid(blockState5)) && direction != Direction.field_11036) {
								iWorld.setBlockState(blockPos, blockState2, 3);
							} else {
								iWorld.setBlockState(blockPos, blockState3, 3);
							}
						}
					}

					this.boundingBox = new MutableIntBoundingBox(mutable.getX(), mutable.getY(), mutable.getZ(), mutable.getX(), mutable.getY(), mutable.getZ());
					return this.addChest(iWorld, mutableIntBoundingBox, random, mutable, LootTables.field_251, null);
				}

				mutable.setOffset(0, -1, 0);
			}

			return false;
		}

		private boolean isLiquid(BlockState blockState) {
			return blockState == Blocks.field_10382.getDefaultState() || blockState == Blocks.field_10164.getDefaultState();
		}
	}
}
