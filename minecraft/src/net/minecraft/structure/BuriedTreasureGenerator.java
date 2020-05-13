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
import net.minecraft.world.ServerWorldAccess;
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
			ServerWorldAccess serverWorldAccess,
			StructureAccessor structureAccessor,
			ChunkGenerator chunkGenerator,
			Random random,
			BlockBox boundingBox,
			ChunkPos chunkPos,
			BlockPos blockPos
		) {
			int i = serverWorldAccess.getTopY(Heightmap.Type.OCEAN_FLOOR_WG, this.boundingBox.minX, this.boundingBox.minZ);
			BlockPos.Mutable mutable = new BlockPos.Mutable(this.boundingBox.minX, i, this.boundingBox.minZ);

			while (mutable.getY() > 0) {
				BlockState blockState = serverWorldAccess.getBlockState(mutable);
				BlockState blockState2 = serverWorldAccess.getBlockState(mutable.down());
				if (blockState2 == Blocks.SANDSTONE.getDefaultState()
					|| blockState2 == Blocks.STONE.getDefaultState()
					|| blockState2 == Blocks.ANDESITE.getDefaultState()
					|| blockState2 == Blocks.GRANITE.getDefaultState()
					|| blockState2 == Blocks.DIORITE.getDefaultState()) {
					BlockState blockState3 = !blockState.isAir() && !this.isLiquid(blockState) ? blockState : Blocks.SAND.getDefaultState();

					for (Direction direction : Direction.values()) {
						BlockPos blockPos2 = mutable.offset(direction);
						BlockState blockState4 = serverWorldAccess.getBlockState(blockPos2);
						if (blockState4.isAir() || this.isLiquid(blockState4)) {
							BlockPos blockPos3 = blockPos2.down();
							BlockState blockState5 = serverWorldAccess.getBlockState(blockPos3);
							if ((blockState5.isAir() || this.isLiquid(blockState5)) && direction != Direction.UP) {
								serverWorldAccess.setBlockState(blockPos2, blockState2, 3);
							} else {
								serverWorldAccess.setBlockState(blockPos2, blockState3, 3);
							}
						}
					}

					this.boundingBox = new BlockBox(mutable.getX(), mutable.getY(), mutable.getZ(), mutable.getX(), mutable.getY(), mutable.getZ());
					return this.addChest(serverWorldAccess, boundingBox, random, mutable, LootTables.BURIED_TREASURE_CHEST, null);
				}

				mutable.move(0, -1, 0);
			}

			return false;
		}

		private boolean isLiquid(BlockState state) {
			return state == Blocks.WATER.getDefaultState() || state == Blocks.LAVA.getDefaultState();
		}
	}
}
