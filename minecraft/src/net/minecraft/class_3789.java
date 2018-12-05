package net.minecraft;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sortme.StructurePiece;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.loot.LootTables;

public class class_3789 {
	public static class class_3339 extends class_3443 {
		public class_3339(BlockPos blockPos) {
			super(StructurePiece.field_16960, 0);
			this.structureBounds = new MutableIntBoundingBox(blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos.getX(), blockPos.getY(), blockPos.getZ());
		}

		public class_3339(class_3485 arg, CompoundTag compoundTag) {
			super(StructurePiece.field_16960, compoundTag);
		}

		@Override
		protected void toNbt(CompoundTag compoundTag) {
		}

		@Override
		public boolean method_14931(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
			int i = iWorld.getTop(Heightmap.Type.OCEAN_FLOOR_WG, this.structureBounds.minX, this.structureBounds.minZ);
			BlockPos.Mutable mutable = new BlockPos.Mutable(this.structureBounds.minX, i, this.structureBounds.minZ);

			while (mutable.getY() > 0) {
				BlockState blockState = iWorld.getBlockState(mutable);
				BlockState blockState2 = iWorld.getBlockState(mutable.down());
				if (blockState2 == Blocks.field_9979.getDefaultState()
					|| blockState2 == Blocks.field_10340.getDefaultState()
					|| blockState2 == Blocks.field_10115.getDefaultState()
					|| blockState2 == Blocks.field_10474.getDefaultState()
					|| blockState2 == Blocks.field_10508.getDefaultState()) {
					BlockState blockState3 = !blockState.isAir() && !this.method_14655(blockState) ? blockState : Blocks.field_10102.getDefaultState();

					for (Direction direction : Direction.values()) {
						BlockPos blockPos = mutable.method_10093(direction);
						BlockState blockState4 = iWorld.getBlockState(blockPos);
						if (blockState4.isAir() || this.method_14655(blockState4)) {
							BlockPos blockPos2 = blockPos.down();
							BlockState blockState5 = iWorld.getBlockState(blockPos2);
							if ((blockState5.isAir() || this.method_14655(blockState5)) && direction != Direction.UP) {
								iWorld.setBlockState(blockPos, blockState2, 3);
							} else {
								iWorld.setBlockState(blockPos, blockState3, 3);
							}
						}
					}

					return this.addChest(
						iWorld, mutableIntBoundingBox, random, new BlockPos(this.structureBounds.minX, mutable.getY(), this.structureBounds.minZ), LootTables.field_251, null
					);
				}

				mutable.method_10100(0, -1, 0);
			}

			return false;
		}

		private boolean method_14655(BlockState blockState) {
			return blockState == Blocks.field_10382.getDefaultState() || blockState == Blocks.field_10164.getDefaultState();
		}
	}
}
