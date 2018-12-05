package net.minecraft.sortme.structures;

import java.util.Random;
import net.minecraft.class_3418;
import net.minecraft.class_3485;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.StairsBlock;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sortme.StructurePiece;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.loot.LootTables;

public class DesertTempleGenerator extends class_3418 {
	private final boolean[] field_14397 = new boolean[4];

	public DesertTempleGenerator(Random random, int i, int j) {
		super(StructurePiece.field_16933, random, i, 64, j, 21, 15, 21);
	}

	public DesertTempleGenerator(class_3485 arg, CompoundTag compoundTag) {
		super(StructurePiece.field_16933, compoundTag);
		this.field_14397[0] = compoundTag.getBoolean("hasPlacedChest0");
		this.field_14397[1] = compoundTag.getBoolean("hasPlacedChest1");
		this.field_14397[2] = compoundTag.getBoolean("hasPlacedChest2");
		this.field_14397[3] = compoundTag.getBoolean("hasPlacedChest3");
	}

	@Override
	protected void toNbt(CompoundTag compoundTag) {
		super.toNbt(compoundTag);
		compoundTag.putBoolean("hasPlacedChest0", this.field_14397[0]);
		compoundTag.putBoolean("hasPlacedChest1", this.field_14397[1]);
		compoundTag.putBoolean("hasPlacedChest2", this.field_14397[2]);
		compoundTag.putBoolean("hasPlacedChest3", this.field_14397[3]);
	}

	@Override
	public boolean method_14931(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
		this.fillWithOutline(
			iWorld, mutableIntBoundingBox, 0, -4, 0, this.width - 1, 0, this.depth - 1, Blocks.field_9979.getDefaultState(), Blocks.field_9979.getDefaultState(), false
		);

		for (int i = 1; i <= 9; i++) {
			this.fillWithOutline(
				iWorld,
				mutableIntBoundingBox,
				i,
				i,
				i,
				this.width - 1 - i,
				i,
				this.depth - 1 - i,
				Blocks.field_9979.getDefaultState(),
				Blocks.field_9979.getDefaultState(),
				false
			);
			this.fillWithOutline(
				iWorld,
				mutableIntBoundingBox,
				i + 1,
				i,
				i + 1,
				this.width - 2 - i,
				i,
				this.depth - 2 - i,
				Blocks.field_10124.getDefaultState(),
				Blocks.field_10124.getDefaultState(),
				false
			);
		}

		for (int i = 0; i < this.width; i++) {
			for (int j = 0; j < this.depth; j++) {
				int k = -5;
				this.method_14936(iWorld, Blocks.field_9979.getDefaultState(), i, -5, j, mutableIntBoundingBox);
			}
		}

		BlockState blockState = Blocks.field_10142.getDefaultState().with(StairsBlock.field_11571, Direction.NORTH);
		BlockState blockState2 = Blocks.field_10142.getDefaultState().with(StairsBlock.field_11571, Direction.SOUTH);
		BlockState blockState3 = Blocks.field_10142.getDefaultState().with(StairsBlock.field_11571, Direction.EAST);
		BlockState blockState4 = Blocks.field_10142.getDefaultState().with(StairsBlock.field_11571, Direction.WEST);
		this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 0, 0, 4, 9, 4, Blocks.field_9979.getDefaultState(), Blocks.field_10124.getDefaultState(), false);
		this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 10, 1, 3, 10, 3, Blocks.field_9979.getDefaultState(), Blocks.field_9979.getDefaultState(), false);
		this.addBlock(iWorld, blockState, 2, 10, 0, mutableIntBoundingBox);
		this.addBlock(iWorld, blockState2, 2, 10, 4, mutableIntBoundingBox);
		this.addBlock(iWorld, blockState3, 0, 10, 2, mutableIntBoundingBox);
		this.addBlock(iWorld, blockState4, 4, 10, 2, mutableIntBoundingBox);
		this.fillWithOutline(
			iWorld, mutableIntBoundingBox, this.width - 5, 0, 0, this.width - 1, 9, 4, Blocks.field_9979.getDefaultState(), Blocks.field_10124.getDefaultState(), false
		);
		this.fillWithOutline(
			iWorld, mutableIntBoundingBox, this.width - 4, 10, 1, this.width - 2, 10, 3, Blocks.field_9979.getDefaultState(), Blocks.field_9979.getDefaultState(), false
		);
		this.addBlock(iWorld, blockState, this.width - 3, 10, 0, mutableIntBoundingBox);
		this.addBlock(iWorld, blockState2, this.width - 3, 10, 4, mutableIntBoundingBox);
		this.addBlock(iWorld, blockState3, this.width - 5, 10, 2, mutableIntBoundingBox);
		this.addBlock(iWorld, blockState4, this.width - 1, 10, 2, mutableIntBoundingBox);
		this.fillWithOutline(iWorld, mutableIntBoundingBox, 8, 0, 0, 12, 4, 4, Blocks.field_9979.getDefaultState(), Blocks.field_10124.getDefaultState(), false);
		this.fillWithOutline(iWorld, mutableIntBoundingBox, 9, 1, 0, 11, 3, 4, Blocks.field_10124.getDefaultState(), Blocks.field_10124.getDefaultState(), false);
		this.addBlock(iWorld, Blocks.field_10361.getDefaultState(), 9, 1, 1, mutableIntBoundingBox);
		this.addBlock(iWorld, Blocks.field_10361.getDefaultState(), 9, 2, 1, mutableIntBoundingBox);
		this.addBlock(iWorld, Blocks.field_10361.getDefaultState(), 9, 3, 1, mutableIntBoundingBox);
		this.addBlock(iWorld, Blocks.field_10361.getDefaultState(), 10, 3, 1, mutableIntBoundingBox);
		this.addBlock(iWorld, Blocks.field_10361.getDefaultState(), 11, 3, 1, mutableIntBoundingBox);
		this.addBlock(iWorld, Blocks.field_10361.getDefaultState(), 11, 2, 1, mutableIntBoundingBox);
		this.addBlock(iWorld, Blocks.field_10361.getDefaultState(), 11, 1, 1, mutableIntBoundingBox);
		this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 1, 1, 8, 3, 3, Blocks.field_9979.getDefaultState(), Blocks.field_10124.getDefaultState(), false);
		this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 1, 2, 8, 2, 2, Blocks.field_10124.getDefaultState(), Blocks.field_10124.getDefaultState(), false);
		this.fillWithOutline(iWorld, mutableIntBoundingBox, 12, 1, 1, 16, 3, 3, Blocks.field_9979.getDefaultState(), Blocks.field_10124.getDefaultState(), false);
		this.fillWithOutline(iWorld, mutableIntBoundingBox, 12, 1, 2, 16, 2, 2, Blocks.field_10124.getDefaultState(), Blocks.field_10124.getDefaultState(), false);
		this.fillWithOutline(
			iWorld, mutableIntBoundingBox, 5, 4, 5, this.width - 6, 4, this.depth - 6, Blocks.field_9979.getDefaultState(), Blocks.field_9979.getDefaultState(), false
		);
		this.fillWithOutline(iWorld, mutableIntBoundingBox, 9, 4, 9, 11, 4, 11, Blocks.field_10124.getDefaultState(), Blocks.field_10124.getDefaultState(), false);
		this.fillWithOutline(iWorld, mutableIntBoundingBox, 8, 1, 8, 8, 3, 8, Blocks.field_10361.getDefaultState(), Blocks.field_10361.getDefaultState(), false);
		this.fillWithOutline(iWorld, mutableIntBoundingBox, 12, 1, 8, 12, 3, 8, Blocks.field_10361.getDefaultState(), Blocks.field_10361.getDefaultState(), false);
		this.fillWithOutline(iWorld, mutableIntBoundingBox, 8, 1, 12, 8, 3, 12, Blocks.field_10361.getDefaultState(), Blocks.field_10361.getDefaultState(), false);
		this.fillWithOutline(iWorld, mutableIntBoundingBox, 12, 1, 12, 12, 3, 12, Blocks.field_10361.getDefaultState(), Blocks.field_10361.getDefaultState(), false);
		this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 1, 5, 4, 4, 11, Blocks.field_9979.getDefaultState(), Blocks.field_9979.getDefaultState(), false);
		this.fillWithOutline(
			iWorld, mutableIntBoundingBox, this.width - 5, 1, 5, this.width - 2, 4, 11, Blocks.field_9979.getDefaultState(), Blocks.field_9979.getDefaultState(), false
		);
		this.fillWithOutline(iWorld, mutableIntBoundingBox, 6, 7, 9, 6, 7, 11, Blocks.field_9979.getDefaultState(), Blocks.field_9979.getDefaultState(), false);
		this.fillWithOutline(
			iWorld, mutableIntBoundingBox, this.width - 7, 7, 9, this.width - 7, 7, 11, Blocks.field_9979.getDefaultState(), Blocks.field_9979.getDefaultState(), false
		);
		this.fillWithOutline(iWorld, mutableIntBoundingBox, 5, 5, 9, 5, 7, 11, Blocks.field_10361.getDefaultState(), Blocks.field_10361.getDefaultState(), false);
		this.fillWithOutline(
			iWorld,
			mutableIntBoundingBox,
			this.width - 6,
			5,
			9,
			this.width - 6,
			7,
			11,
			Blocks.field_10361.getDefaultState(),
			Blocks.field_10361.getDefaultState(),
			false
		);
		this.addBlock(iWorld, Blocks.field_10124.getDefaultState(), 5, 5, 10, mutableIntBoundingBox);
		this.addBlock(iWorld, Blocks.field_10124.getDefaultState(), 5, 6, 10, mutableIntBoundingBox);
		this.addBlock(iWorld, Blocks.field_10124.getDefaultState(), 6, 6, 10, mutableIntBoundingBox);
		this.addBlock(iWorld, Blocks.field_10124.getDefaultState(), this.width - 6, 5, 10, mutableIntBoundingBox);
		this.addBlock(iWorld, Blocks.field_10124.getDefaultState(), this.width - 6, 6, 10, mutableIntBoundingBox);
		this.addBlock(iWorld, Blocks.field_10124.getDefaultState(), this.width - 7, 6, 10, mutableIntBoundingBox);
		this.fillWithOutline(iWorld, mutableIntBoundingBox, 2, 4, 4, 2, 6, 4, Blocks.field_10124.getDefaultState(), Blocks.field_10124.getDefaultState(), false);
		this.fillWithOutline(
			iWorld, mutableIntBoundingBox, this.width - 3, 4, 4, this.width - 3, 6, 4, Blocks.field_10124.getDefaultState(), Blocks.field_10124.getDefaultState(), false
		);
		this.addBlock(iWorld, blockState, 2, 4, 5, mutableIntBoundingBox);
		this.addBlock(iWorld, blockState, 2, 3, 4, mutableIntBoundingBox);
		this.addBlock(iWorld, blockState, this.width - 3, 4, 5, mutableIntBoundingBox);
		this.addBlock(iWorld, blockState, this.width - 3, 3, 4, mutableIntBoundingBox);
		this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 1, 3, 2, 2, 3, Blocks.field_9979.getDefaultState(), Blocks.field_9979.getDefaultState(), false);
		this.fillWithOutline(
			iWorld, mutableIntBoundingBox, this.width - 3, 1, 3, this.width - 2, 2, 3, Blocks.field_9979.getDefaultState(), Blocks.field_9979.getDefaultState(), false
		);
		this.addBlock(iWorld, Blocks.field_9979.getDefaultState(), 1, 1, 2, mutableIntBoundingBox);
		this.addBlock(iWorld, Blocks.field_9979.getDefaultState(), this.width - 2, 1, 2, mutableIntBoundingBox);
		this.addBlock(iWorld, Blocks.field_10007.getDefaultState(), 1, 2, 2, mutableIntBoundingBox);
		this.addBlock(iWorld, Blocks.field_10007.getDefaultState(), this.width - 2, 2, 2, mutableIntBoundingBox);
		this.addBlock(iWorld, blockState4, 2, 1, 2, mutableIntBoundingBox);
		this.addBlock(iWorld, blockState3, this.width - 3, 1, 2, mutableIntBoundingBox);
		this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 3, 5, 4, 3, 17, Blocks.field_9979.getDefaultState(), Blocks.field_9979.getDefaultState(), false);
		this.fillWithOutline(
			iWorld, mutableIntBoundingBox, this.width - 5, 3, 5, this.width - 5, 3, 17, Blocks.field_9979.getDefaultState(), Blocks.field_9979.getDefaultState(), false
		);
		this.fillWithOutline(iWorld, mutableIntBoundingBox, 3, 1, 5, 4, 2, 16, Blocks.field_10124.getDefaultState(), Blocks.field_10124.getDefaultState(), false);
		this.fillWithOutline(
			iWorld,
			mutableIntBoundingBox,
			this.width - 6,
			1,
			5,
			this.width - 5,
			2,
			16,
			Blocks.field_10124.getDefaultState(),
			Blocks.field_10124.getDefaultState(),
			false
		);

		for (int l = 5; l <= 17; l += 2) {
			this.addBlock(iWorld, Blocks.field_10361.getDefaultState(), 4, 1, l, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10292.getDefaultState(), 4, 2, l, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10361.getDefaultState(), this.width - 5, 1, l, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10292.getDefaultState(), this.width - 5, 2, l, mutableIntBoundingBox);
		}

		this.addBlock(iWorld, Blocks.field_10184.getDefaultState(), 10, 0, 7, mutableIntBoundingBox);
		this.addBlock(iWorld, Blocks.field_10184.getDefaultState(), 10, 0, 8, mutableIntBoundingBox);
		this.addBlock(iWorld, Blocks.field_10184.getDefaultState(), 9, 0, 9, mutableIntBoundingBox);
		this.addBlock(iWorld, Blocks.field_10184.getDefaultState(), 11, 0, 9, mutableIntBoundingBox);
		this.addBlock(iWorld, Blocks.field_10184.getDefaultState(), 8, 0, 10, mutableIntBoundingBox);
		this.addBlock(iWorld, Blocks.field_10184.getDefaultState(), 12, 0, 10, mutableIntBoundingBox);
		this.addBlock(iWorld, Blocks.field_10184.getDefaultState(), 7, 0, 10, mutableIntBoundingBox);
		this.addBlock(iWorld, Blocks.field_10184.getDefaultState(), 13, 0, 10, mutableIntBoundingBox);
		this.addBlock(iWorld, Blocks.field_10184.getDefaultState(), 9, 0, 11, mutableIntBoundingBox);
		this.addBlock(iWorld, Blocks.field_10184.getDefaultState(), 11, 0, 11, mutableIntBoundingBox);
		this.addBlock(iWorld, Blocks.field_10184.getDefaultState(), 10, 0, 12, mutableIntBoundingBox);
		this.addBlock(iWorld, Blocks.field_10184.getDefaultState(), 10, 0, 13, mutableIntBoundingBox);
		this.addBlock(iWorld, Blocks.field_10409.getDefaultState(), 10, 0, 10, mutableIntBoundingBox);

		for (int l = 0; l <= this.width - 1; l += this.width - 1) {
			this.addBlock(iWorld, Blocks.field_10361.getDefaultState(), l, 2, 1, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10184.getDefaultState(), l, 2, 2, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10361.getDefaultState(), l, 2, 3, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10361.getDefaultState(), l, 3, 1, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10184.getDefaultState(), l, 3, 2, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10361.getDefaultState(), l, 3, 3, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10184.getDefaultState(), l, 4, 1, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10292.getDefaultState(), l, 4, 2, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10184.getDefaultState(), l, 4, 3, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10361.getDefaultState(), l, 5, 1, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10184.getDefaultState(), l, 5, 2, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10361.getDefaultState(), l, 5, 3, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10184.getDefaultState(), l, 6, 1, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10292.getDefaultState(), l, 6, 2, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10184.getDefaultState(), l, 6, 3, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10184.getDefaultState(), l, 7, 1, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10184.getDefaultState(), l, 7, 2, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10184.getDefaultState(), l, 7, 3, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10361.getDefaultState(), l, 8, 1, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10361.getDefaultState(), l, 8, 2, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10361.getDefaultState(), l, 8, 3, mutableIntBoundingBox);
		}

		for (int l = 2; l <= this.width - 3; l += this.width - 3 - 2) {
			this.addBlock(iWorld, Blocks.field_10361.getDefaultState(), l - 1, 2, 0, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10184.getDefaultState(), l, 2, 0, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10361.getDefaultState(), l + 1, 2, 0, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10361.getDefaultState(), l - 1, 3, 0, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10184.getDefaultState(), l, 3, 0, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10361.getDefaultState(), l + 1, 3, 0, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10184.getDefaultState(), l - 1, 4, 0, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10292.getDefaultState(), l, 4, 0, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10184.getDefaultState(), l + 1, 4, 0, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10361.getDefaultState(), l - 1, 5, 0, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10184.getDefaultState(), l, 5, 0, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10361.getDefaultState(), l + 1, 5, 0, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10184.getDefaultState(), l - 1, 6, 0, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10292.getDefaultState(), l, 6, 0, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10184.getDefaultState(), l + 1, 6, 0, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10184.getDefaultState(), l - 1, 7, 0, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10184.getDefaultState(), l, 7, 0, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10184.getDefaultState(), l + 1, 7, 0, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10361.getDefaultState(), l - 1, 8, 0, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10361.getDefaultState(), l, 8, 0, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10361.getDefaultState(), l + 1, 8, 0, mutableIntBoundingBox);
		}

		this.fillWithOutline(iWorld, mutableIntBoundingBox, 8, 4, 0, 12, 6, 0, Blocks.field_10361.getDefaultState(), Blocks.field_10361.getDefaultState(), false);
		this.addBlock(iWorld, Blocks.field_10124.getDefaultState(), 8, 6, 0, mutableIntBoundingBox);
		this.addBlock(iWorld, Blocks.field_10124.getDefaultState(), 12, 6, 0, mutableIntBoundingBox);
		this.addBlock(iWorld, Blocks.field_10184.getDefaultState(), 9, 5, 0, mutableIntBoundingBox);
		this.addBlock(iWorld, Blocks.field_10292.getDefaultState(), 10, 5, 0, mutableIntBoundingBox);
		this.addBlock(iWorld, Blocks.field_10184.getDefaultState(), 11, 5, 0, mutableIntBoundingBox);
		this.fillWithOutline(iWorld, mutableIntBoundingBox, 8, -14, 8, 12, -11, 12, Blocks.field_10361.getDefaultState(), Blocks.field_10361.getDefaultState(), false);
		this.fillWithOutline(iWorld, mutableIntBoundingBox, 8, -10, 8, 12, -10, 12, Blocks.field_10292.getDefaultState(), Blocks.field_10292.getDefaultState(), false);
		this.fillWithOutline(iWorld, mutableIntBoundingBox, 8, -9, 8, 12, -9, 12, Blocks.field_10361.getDefaultState(), Blocks.field_10361.getDefaultState(), false);
		this.fillWithOutline(iWorld, mutableIntBoundingBox, 8, -8, 8, 12, -1, 12, Blocks.field_9979.getDefaultState(), Blocks.field_9979.getDefaultState(), false);
		this.fillWithOutline(iWorld, mutableIntBoundingBox, 9, -11, 9, 11, -1, 11, Blocks.field_10124.getDefaultState(), Blocks.field_10124.getDefaultState(), false);
		this.addBlock(iWorld, Blocks.field_10158.getDefaultState(), 10, -11, 10, mutableIntBoundingBox);
		this.fillWithOutline(iWorld, mutableIntBoundingBox, 9, -13, 9, 11, -13, 11, Blocks.field_10375.getDefaultState(), Blocks.field_10124.getDefaultState(), false);
		this.addBlock(iWorld, Blocks.field_10124.getDefaultState(), 8, -11, 10, mutableIntBoundingBox);
		this.addBlock(iWorld, Blocks.field_10124.getDefaultState(), 8, -10, 10, mutableIntBoundingBox);
		this.addBlock(iWorld, Blocks.field_10292.getDefaultState(), 7, -10, 10, mutableIntBoundingBox);
		this.addBlock(iWorld, Blocks.field_10361.getDefaultState(), 7, -11, 10, mutableIntBoundingBox);
		this.addBlock(iWorld, Blocks.field_10124.getDefaultState(), 12, -11, 10, mutableIntBoundingBox);
		this.addBlock(iWorld, Blocks.field_10124.getDefaultState(), 12, -10, 10, mutableIntBoundingBox);
		this.addBlock(iWorld, Blocks.field_10292.getDefaultState(), 13, -10, 10, mutableIntBoundingBox);
		this.addBlock(iWorld, Blocks.field_10361.getDefaultState(), 13, -11, 10, mutableIntBoundingBox);
		this.addBlock(iWorld, Blocks.field_10124.getDefaultState(), 10, -11, 8, mutableIntBoundingBox);
		this.addBlock(iWorld, Blocks.field_10124.getDefaultState(), 10, -10, 8, mutableIntBoundingBox);
		this.addBlock(iWorld, Blocks.field_10292.getDefaultState(), 10, -10, 7, mutableIntBoundingBox);
		this.addBlock(iWorld, Blocks.field_10361.getDefaultState(), 10, -11, 7, mutableIntBoundingBox);
		this.addBlock(iWorld, Blocks.field_10124.getDefaultState(), 10, -11, 12, mutableIntBoundingBox);
		this.addBlock(iWorld, Blocks.field_10124.getDefaultState(), 10, -10, 12, mutableIntBoundingBox);
		this.addBlock(iWorld, Blocks.field_10292.getDefaultState(), 10, -10, 13, mutableIntBoundingBox);
		this.addBlock(iWorld, Blocks.field_10361.getDefaultState(), 10, -11, 13, mutableIntBoundingBox);

		for (Direction direction : Direction.class_2353.HORIZONTAL) {
			if (!this.field_14397[direction.getHorizontal()]) {
				int m = direction.getOffsetX() * 2;
				int n = direction.getOffsetZ() * 2;
				this.field_14397[direction.getHorizontal()] = this.method_14915(iWorld, mutableIntBoundingBox, random, 10 + m, -11, 10 + n, LootTables.CHEST_DESERT_PYRAMID);
			}
		}

		return true;
	}
}
