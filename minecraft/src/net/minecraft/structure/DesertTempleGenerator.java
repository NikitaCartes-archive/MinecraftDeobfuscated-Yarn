package net.minecraft.structure;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.StairsBlock;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class DesertTempleGenerator extends StructurePieceWithDimensions {
	private final boolean[] hasPlacedChest = new boolean[4];

	public DesertTempleGenerator(Random random, int x, int z) {
		super(StructurePieceType.DESERT_TEMPLE, random, x, 64, z, 21, 15, 21);
	}

	public DesertTempleGenerator(StructureManager manager, CompoundTag tag) {
		super(StructurePieceType.DESERT_TEMPLE, tag);
		this.hasPlacedChest[0] = tag.getBoolean("hasPlacedChest0");
		this.hasPlacedChest[1] = tag.getBoolean("hasPlacedChest1");
		this.hasPlacedChest[2] = tag.getBoolean("hasPlacedChest2");
		this.hasPlacedChest[3] = tag.getBoolean("hasPlacedChest3");
	}

	@Override
	protected void toNbt(CompoundTag tag) {
		super.toNbt(tag);
		tag.putBoolean("hasPlacedChest0", this.hasPlacedChest[0]);
		tag.putBoolean("hasPlacedChest1", this.hasPlacedChest[1]);
		tag.putBoolean("hasPlacedChest2", this.hasPlacedChest[2]);
		tag.putBoolean("hasPlacedChest3", this.hasPlacedChest[3]);
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
		this.fillWithOutline(
			structureWorldAccess,
			boundingBox,
			0,
			-4,
			0,
			this.width - 1,
			0,
			this.depth - 1,
			Blocks.field_9979.getDefaultState(),
			Blocks.field_9979.getDefaultState(),
			false
		);

		for (int i = 1; i <= 9; i++) {
			this.fillWithOutline(
				structureWorldAccess,
				boundingBox,
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
				structureWorldAccess,
				boundingBox,
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
				this.method_14936(structureWorldAccess, Blocks.field_9979.getDefaultState(), i, -5, j, boundingBox);
			}
		}

		BlockState blockState = Blocks.field_10142.getDefaultState().with(StairsBlock.FACING, Direction.field_11043);
		BlockState blockState2 = Blocks.field_10142.getDefaultState().with(StairsBlock.FACING, Direction.field_11035);
		BlockState blockState3 = Blocks.field_10142.getDefaultState().with(StairsBlock.FACING, Direction.field_11034);
		BlockState blockState4 = Blocks.field_10142.getDefaultState().with(StairsBlock.FACING, Direction.field_11039);
		this.fillWithOutline(structureWorldAccess, boundingBox, 0, 0, 0, 4, 9, 4, Blocks.field_9979.getDefaultState(), Blocks.field_10124.getDefaultState(), false);
		this.fillWithOutline(structureWorldAccess, boundingBox, 1, 10, 1, 3, 10, 3, Blocks.field_9979.getDefaultState(), Blocks.field_9979.getDefaultState(), false);
		this.addBlock(structureWorldAccess, blockState, 2, 10, 0, boundingBox);
		this.addBlock(structureWorldAccess, blockState2, 2, 10, 4, boundingBox);
		this.addBlock(structureWorldAccess, blockState3, 0, 10, 2, boundingBox);
		this.addBlock(structureWorldAccess, blockState4, 4, 10, 2, boundingBox);
		this.fillWithOutline(
			structureWorldAccess,
			boundingBox,
			this.width - 5,
			0,
			0,
			this.width - 1,
			9,
			4,
			Blocks.field_9979.getDefaultState(),
			Blocks.field_10124.getDefaultState(),
			false
		);
		this.fillWithOutline(
			structureWorldAccess,
			boundingBox,
			this.width - 4,
			10,
			1,
			this.width - 2,
			10,
			3,
			Blocks.field_9979.getDefaultState(),
			Blocks.field_9979.getDefaultState(),
			false
		);
		this.addBlock(structureWorldAccess, blockState, this.width - 3, 10, 0, boundingBox);
		this.addBlock(structureWorldAccess, blockState2, this.width - 3, 10, 4, boundingBox);
		this.addBlock(structureWorldAccess, blockState3, this.width - 5, 10, 2, boundingBox);
		this.addBlock(structureWorldAccess, blockState4, this.width - 1, 10, 2, boundingBox);
		this.fillWithOutline(structureWorldAccess, boundingBox, 8, 0, 0, 12, 4, 4, Blocks.field_9979.getDefaultState(), Blocks.field_10124.getDefaultState(), false);
		this.fillWithOutline(structureWorldAccess, boundingBox, 9, 1, 0, 11, 3, 4, Blocks.field_10124.getDefaultState(), Blocks.field_10124.getDefaultState(), false);
		this.addBlock(structureWorldAccess, Blocks.field_10361.getDefaultState(), 9, 1, 1, boundingBox);
		this.addBlock(structureWorldAccess, Blocks.field_10361.getDefaultState(), 9, 2, 1, boundingBox);
		this.addBlock(structureWorldAccess, Blocks.field_10361.getDefaultState(), 9, 3, 1, boundingBox);
		this.addBlock(structureWorldAccess, Blocks.field_10361.getDefaultState(), 10, 3, 1, boundingBox);
		this.addBlock(structureWorldAccess, Blocks.field_10361.getDefaultState(), 11, 3, 1, boundingBox);
		this.addBlock(structureWorldAccess, Blocks.field_10361.getDefaultState(), 11, 2, 1, boundingBox);
		this.addBlock(structureWorldAccess, Blocks.field_10361.getDefaultState(), 11, 1, 1, boundingBox);
		this.fillWithOutline(structureWorldAccess, boundingBox, 4, 1, 1, 8, 3, 3, Blocks.field_9979.getDefaultState(), Blocks.field_10124.getDefaultState(), false);
		this.fillWithOutline(structureWorldAccess, boundingBox, 4, 1, 2, 8, 2, 2, Blocks.field_10124.getDefaultState(), Blocks.field_10124.getDefaultState(), false);
		this.fillWithOutline(structureWorldAccess, boundingBox, 12, 1, 1, 16, 3, 3, Blocks.field_9979.getDefaultState(), Blocks.field_10124.getDefaultState(), false);
		this.fillWithOutline(structureWorldAccess, boundingBox, 12, 1, 2, 16, 2, 2, Blocks.field_10124.getDefaultState(), Blocks.field_10124.getDefaultState(), false);
		this.fillWithOutline(
			structureWorldAccess,
			boundingBox,
			5,
			4,
			5,
			this.width - 6,
			4,
			this.depth - 6,
			Blocks.field_9979.getDefaultState(),
			Blocks.field_9979.getDefaultState(),
			false
		);
		this.fillWithOutline(structureWorldAccess, boundingBox, 9, 4, 9, 11, 4, 11, Blocks.field_10124.getDefaultState(), Blocks.field_10124.getDefaultState(), false);
		this.fillWithOutline(structureWorldAccess, boundingBox, 8, 1, 8, 8, 3, 8, Blocks.field_10361.getDefaultState(), Blocks.field_10361.getDefaultState(), false);
		this.fillWithOutline(structureWorldAccess, boundingBox, 12, 1, 8, 12, 3, 8, Blocks.field_10361.getDefaultState(), Blocks.field_10361.getDefaultState(), false);
		this.fillWithOutline(structureWorldAccess, boundingBox, 8, 1, 12, 8, 3, 12, Blocks.field_10361.getDefaultState(), Blocks.field_10361.getDefaultState(), false);
		this.fillWithOutline(
			structureWorldAccess, boundingBox, 12, 1, 12, 12, 3, 12, Blocks.field_10361.getDefaultState(), Blocks.field_10361.getDefaultState(), false
		);
		this.fillWithOutline(structureWorldAccess, boundingBox, 1, 1, 5, 4, 4, 11, Blocks.field_9979.getDefaultState(), Blocks.field_9979.getDefaultState(), false);
		this.fillWithOutline(
			structureWorldAccess,
			boundingBox,
			this.width - 5,
			1,
			5,
			this.width - 2,
			4,
			11,
			Blocks.field_9979.getDefaultState(),
			Blocks.field_9979.getDefaultState(),
			false
		);
		this.fillWithOutline(structureWorldAccess, boundingBox, 6, 7, 9, 6, 7, 11, Blocks.field_9979.getDefaultState(), Blocks.field_9979.getDefaultState(), false);
		this.fillWithOutline(
			structureWorldAccess,
			boundingBox,
			this.width - 7,
			7,
			9,
			this.width - 7,
			7,
			11,
			Blocks.field_9979.getDefaultState(),
			Blocks.field_9979.getDefaultState(),
			false
		);
		this.fillWithOutline(structureWorldAccess, boundingBox, 5, 5, 9, 5, 7, 11, Blocks.field_10361.getDefaultState(), Blocks.field_10361.getDefaultState(), false);
		this.fillWithOutline(
			structureWorldAccess,
			boundingBox,
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
		this.addBlock(structureWorldAccess, Blocks.field_10124.getDefaultState(), 5, 5, 10, boundingBox);
		this.addBlock(structureWorldAccess, Blocks.field_10124.getDefaultState(), 5, 6, 10, boundingBox);
		this.addBlock(structureWorldAccess, Blocks.field_10124.getDefaultState(), 6, 6, 10, boundingBox);
		this.addBlock(structureWorldAccess, Blocks.field_10124.getDefaultState(), this.width - 6, 5, 10, boundingBox);
		this.addBlock(structureWorldAccess, Blocks.field_10124.getDefaultState(), this.width - 6, 6, 10, boundingBox);
		this.addBlock(structureWorldAccess, Blocks.field_10124.getDefaultState(), this.width - 7, 6, 10, boundingBox);
		this.fillWithOutline(structureWorldAccess, boundingBox, 2, 4, 4, 2, 6, 4, Blocks.field_10124.getDefaultState(), Blocks.field_10124.getDefaultState(), false);
		this.fillWithOutline(
			structureWorldAccess,
			boundingBox,
			this.width - 3,
			4,
			4,
			this.width - 3,
			6,
			4,
			Blocks.field_10124.getDefaultState(),
			Blocks.field_10124.getDefaultState(),
			false
		);
		this.addBlock(structureWorldAccess, blockState, 2, 4, 5, boundingBox);
		this.addBlock(structureWorldAccess, blockState, 2, 3, 4, boundingBox);
		this.addBlock(structureWorldAccess, blockState, this.width - 3, 4, 5, boundingBox);
		this.addBlock(structureWorldAccess, blockState, this.width - 3, 3, 4, boundingBox);
		this.fillWithOutline(structureWorldAccess, boundingBox, 1, 1, 3, 2, 2, 3, Blocks.field_9979.getDefaultState(), Blocks.field_9979.getDefaultState(), false);
		this.fillWithOutline(
			structureWorldAccess,
			boundingBox,
			this.width - 3,
			1,
			3,
			this.width - 2,
			2,
			3,
			Blocks.field_9979.getDefaultState(),
			Blocks.field_9979.getDefaultState(),
			false
		);
		this.addBlock(structureWorldAccess, Blocks.field_9979.getDefaultState(), 1, 1, 2, boundingBox);
		this.addBlock(structureWorldAccess, Blocks.field_9979.getDefaultState(), this.width - 2, 1, 2, boundingBox);
		this.addBlock(structureWorldAccess, Blocks.field_10007.getDefaultState(), 1, 2, 2, boundingBox);
		this.addBlock(structureWorldAccess, Blocks.field_10007.getDefaultState(), this.width - 2, 2, 2, boundingBox);
		this.addBlock(structureWorldAccess, blockState4, 2, 1, 2, boundingBox);
		this.addBlock(structureWorldAccess, blockState3, this.width - 3, 1, 2, boundingBox);
		this.fillWithOutline(structureWorldAccess, boundingBox, 4, 3, 5, 4, 3, 17, Blocks.field_9979.getDefaultState(), Blocks.field_9979.getDefaultState(), false);
		this.fillWithOutline(
			structureWorldAccess,
			boundingBox,
			this.width - 5,
			3,
			5,
			this.width - 5,
			3,
			17,
			Blocks.field_9979.getDefaultState(),
			Blocks.field_9979.getDefaultState(),
			false
		);
		this.fillWithOutline(structureWorldAccess, boundingBox, 3, 1, 5, 4, 2, 16, Blocks.field_10124.getDefaultState(), Blocks.field_10124.getDefaultState(), false);
		this.fillWithOutline(
			structureWorldAccess,
			boundingBox,
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
			this.addBlock(structureWorldAccess, Blocks.field_10361.getDefaultState(), 4, 1, l, boundingBox);
			this.addBlock(structureWorldAccess, Blocks.field_10292.getDefaultState(), 4, 2, l, boundingBox);
			this.addBlock(structureWorldAccess, Blocks.field_10361.getDefaultState(), this.width - 5, 1, l, boundingBox);
			this.addBlock(structureWorldAccess, Blocks.field_10292.getDefaultState(), this.width - 5, 2, l, boundingBox);
		}

		this.addBlock(structureWorldAccess, Blocks.field_10184.getDefaultState(), 10, 0, 7, boundingBox);
		this.addBlock(structureWorldAccess, Blocks.field_10184.getDefaultState(), 10, 0, 8, boundingBox);
		this.addBlock(structureWorldAccess, Blocks.field_10184.getDefaultState(), 9, 0, 9, boundingBox);
		this.addBlock(structureWorldAccess, Blocks.field_10184.getDefaultState(), 11, 0, 9, boundingBox);
		this.addBlock(structureWorldAccess, Blocks.field_10184.getDefaultState(), 8, 0, 10, boundingBox);
		this.addBlock(structureWorldAccess, Blocks.field_10184.getDefaultState(), 12, 0, 10, boundingBox);
		this.addBlock(structureWorldAccess, Blocks.field_10184.getDefaultState(), 7, 0, 10, boundingBox);
		this.addBlock(structureWorldAccess, Blocks.field_10184.getDefaultState(), 13, 0, 10, boundingBox);
		this.addBlock(structureWorldAccess, Blocks.field_10184.getDefaultState(), 9, 0, 11, boundingBox);
		this.addBlock(structureWorldAccess, Blocks.field_10184.getDefaultState(), 11, 0, 11, boundingBox);
		this.addBlock(structureWorldAccess, Blocks.field_10184.getDefaultState(), 10, 0, 12, boundingBox);
		this.addBlock(structureWorldAccess, Blocks.field_10184.getDefaultState(), 10, 0, 13, boundingBox);
		this.addBlock(structureWorldAccess, Blocks.field_10409.getDefaultState(), 10, 0, 10, boundingBox);

		for (int l = 0; l <= this.width - 1; l += this.width - 1) {
			this.addBlock(structureWorldAccess, Blocks.field_10361.getDefaultState(), l, 2, 1, boundingBox);
			this.addBlock(structureWorldAccess, Blocks.field_10184.getDefaultState(), l, 2, 2, boundingBox);
			this.addBlock(structureWorldAccess, Blocks.field_10361.getDefaultState(), l, 2, 3, boundingBox);
			this.addBlock(structureWorldAccess, Blocks.field_10361.getDefaultState(), l, 3, 1, boundingBox);
			this.addBlock(structureWorldAccess, Blocks.field_10184.getDefaultState(), l, 3, 2, boundingBox);
			this.addBlock(structureWorldAccess, Blocks.field_10361.getDefaultState(), l, 3, 3, boundingBox);
			this.addBlock(structureWorldAccess, Blocks.field_10184.getDefaultState(), l, 4, 1, boundingBox);
			this.addBlock(structureWorldAccess, Blocks.field_10292.getDefaultState(), l, 4, 2, boundingBox);
			this.addBlock(structureWorldAccess, Blocks.field_10184.getDefaultState(), l, 4, 3, boundingBox);
			this.addBlock(structureWorldAccess, Blocks.field_10361.getDefaultState(), l, 5, 1, boundingBox);
			this.addBlock(structureWorldAccess, Blocks.field_10184.getDefaultState(), l, 5, 2, boundingBox);
			this.addBlock(structureWorldAccess, Blocks.field_10361.getDefaultState(), l, 5, 3, boundingBox);
			this.addBlock(structureWorldAccess, Blocks.field_10184.getDefaultState(), l, 6, 1, boundingBox);
			this.addBlock(structureWorldAccess, Blocks.field_10292.getDefaultState(), l, 6, 2, boundingBox);
			this.addBlock(structureWorldAccess, Blocks.field_10184.getDefaultState(), l, 6, 3, boundingBox);
			this.addBlock(structureWorldAccess, Blocks.field_10184.getDefaultState(), l, 7, 1, boundingBox);
			this.addBlock(structureWorldAccess, Blocks.field_10184.getDefaultState(), l, 7, 2, boundingBox);
			this.addBlock(structureWorldAccess, Blocks.field_10184.getDefaultState(), l, 7, 3, boundingBox);
			this.addBlock(structureWorldAccess, Blocks.field_10361.getDefaultState(), l, 8, 1, boundingBox);
			this.addBlock(structureWorldAccess, Blocks.field_10361.getDefaultState(), l, 8, 2, boundingBox);
			this.addBlock(structureWorldAccess, Blocks.field_10361.getDefaultState(), l, 8, 3, boundingBox);
		}

		for (int l = 2; l <= this.width - 3; l += this.width - 3 - 2) {
			this.addBlock(structureWorldAccess, Blocks.field_10361.getDefaultState(), l - 1, 2, 0, boundingBox);
			this.addBlock(structureWorldAccess, Blocks.field_10184.getDefaultState(), l, 2, 0, boundingBox);
			this.addBlock(structureWorldAccess, Blocks.field_10361.getDefaultState(), l + 1, 2, 0, boundingBox);
			this.addBlock(structureWorldAccess, Blocks.field_10361.getDefaultState(), l - 1, 3, 0, boundingBox);
			this.addBlock(structureWorldAccess, Blocks.field_10184.getDefaultState(), l, 3, 0, boundingBox);
			this.addBlock(structureWorldAccess, Blocks.field_10361.getDefaultState(), l + 1, 3, 0, boundingBox);
			this.addBlock(structureWorldAccess, Blocks.field_10184.getDefaultState(), l - 1, 4, 0, boundingBox);
			this.addBlock(structureWorldAccess, Blocks.field_10292.getDefaultState(), l, 4, 0, boundingBox);
			this.addBlock(structureWorldAccess, Blocks.field_10184.getDefaultState(), l + 1, 4, 0, boundingBox);
			this.addBlock(structureWorldAccess, Blocks.field_10361.getDefaultState(), l - 1, 5, 0, boundingBox);
			this.addBlock(structureWorldAccess, Blocks.field_10184.getDefaultState(), l, 5, 0, boundingBox);
			this.addBlock(structureWorldAccess, Blocks.field_10361.getDefaultState(), l + 1, 5, 0, boundingBox);
			this.addBlock(structureWorldAccess, Blocks.field_10184.getDefaultState(), l - 1, 6, 0, boundingBox);
			this.addBlock(structureWorldAccess, Blocks.field_10292.getDefaultState(), l, 6, 0, boundingBox);
			this.addBlock(structureWorldAccess, Blocks.field_10184.getDefaultState(), l + 1, 6, 0, boundingBox);
			this.addBlock(structureWorldAccess, Blocks.field_10184.getDefaultState(), l - 1, 7, 0, boundingBox);
			this.addBlock(structureWorldAccess, Blocks.field_10184.getDefaultState(), l, 7, 0, boundingBox);
			this.addBlock(structureWorldAccess, Blocks.field_10184.getDefaultState(), l + 1, 7, 0, boundingBox);
			this.addBlock(structureWorldAccess, Blocks.field_10361.getDefaultState(), l - 1, 8, 0, boundingBox);
			this.addBlock(structureWorldAccess, Blocks.field_10361.getDefaultState(), l, 8, 0, boundingBox);
			this.addBlock(structureWorldAccess, Blocks.field_10361.getDefaultState(), l + 1, 8, 0, boundingBox);
		}

		this.fillWithOutline(structureWorldAccess, boundingBox, 8, 4, 0, 12, 6, 0, Blocks.field_10361.getDefaultState(), Blocks.field_10361.getDefaultState(), false);
		this.addBlock(structureWorldAccess, Blocks.field_10124.getDefaultState(), 8, 6, 0, boundingBox);
		this.addBlock(structureWorldAccess, Blocks.field_10124.getDefaultState(), 12, 6, 0, boundingBox);
		this.addBlock(structureWorldAccess, Blocks.field_10184.getDefaultState(), 9, 5, 0, boundingBox);
		this.addBlock(structureWorldAccess, Blocks.field_10292.getDefaultState(), 10, 5, 0, boundingBox);
		this.addBlock(structureWorldAccess, Blocks.field_10184.getDefaultState(), 11, 5, 0, boundingBox);
		this.fillWithOutline(
			structureWorldAccess, boundingBox, 8, -14, 8, 12, -11, 12, Blocks.field_10361.getDefaultState(), Blocks.field_10361.getDefaultState(), false
		);
		this.fillWithOutline(
			structureWorldAccess, boundingBox, 8, -10, 8, 12, -10, 12, Blocks.field_10292.getDefaultState(), Blocks.field_10292.getDefaultState(), false
		);
		this.fillWithOutline(
			structureWorldAccess, boundingBox, 8, -9, 8, 12, -9, 12, Blocks.field_10361.getDefaultState(), Blocks.field_10361.getDefaultState(), false
		);
		this.fillWithOutline(structureWorldAccess, boundingBox, 8, -8, 8, 12, -1, 12, Blocks.field_9979.getDefaultState(), Blocks.field_9979.getDefaultState(), false);
		this.fillWithOutline(
			structureWorldAccess, boundingBox, 9, -11, 9, 11, -1, 11, Blocks.field_10124.getDefaultState(), Blocks.field_10124.getDefaultState(), false
		);
		this.addBlock(structureWorldAccess, Blocks.field_10158.getDefaultState(), 10, -11, 10, boundingBox);
		this.fillWithOutline(
			structureWorldAccess, boundingBox, 9, -13, 9, 11, -13, 11, Blocks.field_10375.getDefaultState(), Blocks.field_10124.getDefaultState(), false
		);
		this.addBlock(structureWorldAccess, Blocks.field_10124.getDefaultState(), 8, -11, 10, boundingBox);
		this.addBlock(structureWorldAccess, Blocks.field_10124.getDefaultState(), 8, -10, 10, boundingBox);
		this.addBlock(structureWorldAccess, Blocks.field_10292.getDefaultState(), 7, -10, 10, boundingBox);
		this.addBlock(structureWorldAccess, Blocks.field_10361.getDefaultState(), 7, -11, 10, boundingBox);
		this.addBlock(structureWorldAccess, Blocks.field_10124.getDefaultState(), 12, -11, 10, boundingBox);
		this.addBlock(structureWorldAccess, Blocks.field_10124.getDefaultState(), 12, -10, 10, boundingBox);
		this.addBlock(structureWorldAccess, Blocks.field_10292.getDefaultState(), 13, -10, 10, boundingBox);
		this.addBlock(structureWorldAccess, Blocks.field_10361.getDefaultState(), 13, -11, 10, boundingBox);
		this.addBlock(structureWorldAccess, Blocks.field_10124.getDefaultState(), 10, -11, 8, boundingBox);
		this.addBlock(structureWorldAccess, Blocks.field_10124.getDefaultState(), 10, -10, 8, boundingBox);
		this.addBlock(structureWorldAccess, Blocks.field_10292.getDefaultState(), 10, -10, 7, boundingBox);
		this.addBlock(structureWorldAccess, Blocks.field_10361.getDefaultState(), 10, -11, 7, boundingBox);
		this.addBlock(structureWorldAccess, Blocks.field_10124.getDefaultState(), 10, -11, 12, boundingBox);
		this.addBlock(structureWorldAccess, Blocks.field_10124.getDefaultState(), 10, -10, 12, boundingBox);
		this.addBlock(structureWorldAccess, Blocks.field_10292.getDefaultState(), 10, -10, 13, boundingBox);
		this.addBlock(structureWorldAccess, Blocks.field_10361.getDefaultState(), 10, -11, 13, boundingBox);

		for (Direction direction : Direction.Type.field_11062) {
			if (!this.hasPlacedChest[direction.getHorizontal()]) {
				int m = direction.getOffsetX() * 2;
				int n = direction.getOffsetZ() * 2;
				this.hasPlacedChest[direction.getHorizontal()] = this.addChest(structureWorldAccess, boundingBox, random, 10 + m, -11, 10 + n, LootTables.field_885);
			}
		}

		return true;
	}
}
