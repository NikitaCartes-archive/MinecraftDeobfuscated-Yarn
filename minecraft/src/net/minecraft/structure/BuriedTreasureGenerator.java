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
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class BuriedTreasureGenerator {
	public static class Piece extends StructurePiece {
		public Piece(BlockPos pos) {
			super(StructurePieceType.BURIED_TREASURE, 0);
			this.boundingBox = new BlockBox(pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY(), pos.getZ());
		}

		public Piece(StructureManager manager, CompoundTag tag) {
			super(StructurePieceType.BURIED_TREASURE, tag);
		}

		@Override
		protected void toNbt(CompoundTag tag) {
		}

		@Override
		public boolean generate(
			StructureWorldAccess structureWorldAccess,
			StructureAccessor structureAccessor,
			ChunkGenerator chunkGenerator,
			Random random,
			BlockBox boundingBox,
			ChunkPos chunkPos,
			BlockPos blockPos
		) {
			int i = structureWorldAccess.getTopY(Heightmap.Type.field_13195, this.boundingBox.minX, this.boundingBox.minZ);
			BlockPos.Mutable mutable = new BlockPos.Mutable(this.boundingBox.minX, i, this.boundingBox.minZ);

			while (mutable.getY() > 0) {
				BlockState blockState = structureWorldAccess.getBlockState(mutable);
				BlockState blockState2 = structureWorldAccess.getBlockState(mutable.method_10074());
				if (blockState2 == Blocks.field_9979.getDefaultState()
					|| blockState2 == Blocks.field_10340.getDefaultState()
					|| blockState2 == Blocks.field_10115.getDefaultState()
					|| blockState2 == Blocks.field_10474.getDefaultState()
					|| blockState2 == Blocks.field_10508.getDefaultState()) {
					BlockState blockState3 = !blockState.isAir() && !this.isLiquid(blockState) ? blockState : Blocks.field_10102.getDefaultState();

					for (Direction direction : Direction.values()) {
						BlockPos blockPos2 = mutable.offset(direction);
						BlockState blockState4 = structureWorldAccess.getBlockState(blockPos2);
						if (blockState4.isAir() || this.isLiquid(blockState4)) {
							BlockPos blockPos3 = blockPos2.method_10074();
							BlockState blockState5 = structureWorldAccess.getBlockState(blockPos3);
							if ((blockState5.isAir() || this.isLiquid(blockState5)) && direction != Direction.field_11036) {
								structureWorldAccess.setBlockState(blockPos2, blockState2, 3);
							} else {
								structureWorldAccess.setBlockState(blockPos2, blockState3, 3);
							}
						}
					}

					this.boundingBox = new BlockBox(mutable.getX(), mutable.getY(), mutable.getZ(), mutable.getX(), mutable.getY(), mutable.getZ());
					return this.addChest(structureWorldAccess, boundingBox, random, mutable, LootTables.field_251, null);
				}

				mutable.move(0, -1, 0);
			}

			return false;
		}

		private boolean isLiquid(BlockState state) {
			return state == Blocks.field_10382.getDefaultState() || state == Blocks.field_10164.getDefaultState();
		}
	}
}
