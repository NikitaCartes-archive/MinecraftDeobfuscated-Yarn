package net.minecraft.structure;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.StairsBlock;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
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
		super(StructurePieceType.DESERT_TEMPLE, x, 64, z, 21, 15, 21, method_35457(random));
	}

	public DesertTempleGenerator(ServerWorld world, NbtCompound nbt) {
		super(StructurePieceType.DESERT_TEMPLE, nbt);
		this.hasPlacedChest[0] = nbt.getBoolean("hasPlacedChest0");
		this.hasPlacedChest[1] = nbt.getBoolean("hasPlacedChest1");
		this.hasPlacedChest[2] = nbt.getBoolean("hasPlacedChest2");
		this.hasPlacedChest[3] = nbt.getBoolean("hasPlacedChest3");
	}

	@Override
	protected void writeNbt(ServerWorld world, NbtCompound nbt) {
		super.writeNbt(world, nbt);
		nbt.putBoolean("hasPlacedChest0", this.hasPlacedChest[0]);
		nbt.putBoolean("hasPlacedChest1", this.hasPlacedChest[1]);
		nbt.putBoolean("hasPlacedChest2", this.hasPlacedChest[2]);
		nbt.putBoolean("hasPlacedChest3", this.hasPlacedChest[3]);
	}

	@Override
	public boolean generate(
		StructureWorldAccess world,
		StructureAccessor structureAccessor,
		ChunkGenerator chunkGenerator,
		Random random,
		BlockBox boundingBox,
		ChunkPos chunkPos,
		BlockPos pos
	) {
		this.fillWithOutline(
			world, boundingBox, 0, -4, 0, this.width - 1, 0, this.depth - 1, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false
		);

		for (int i = 1; i <= 9; i++) {
			this.fillWithOutline(
				world, boundingBox, i, i, i, this.width - 1 - i, i, this.depth - 1 - i, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false
			);
			this.fillWithOutline(
				world, boundingBox, i + 1, i, i + 1, this.width - 2 - i, i, this.depth - 2 - i, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false
			);
		}

		for (int i = 0; i < this.width; i++) {
			for (int j = 0; j < this.depth; j++) {
				int k = -5;
				this.fillDownwards(world, Blocks.SANDSTONE.getDefaultState(), i, -5, j, boundingBox);
			}
		}

		BlockState blockState = Blocks.SANDSTONE_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.NORTH);
		BlockState blockState2 = Blocks.SANDSTONE_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.SOUTH);
		BlockState blockState3 = Blocks.SANDSTONE_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.EAST);
		BlockState blockState4 = Blocks.SANDSTONE_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.WEST);
		this.fillWithOutline(world, boundingBox, 0, 0, 0, 4, 9, 4, Blocks.SANDSTONE.getDefaultState(), Blocks.AIR.getDefaultState(), false);
		this.fillWithOutline(world, boundingBox, 1, 10, 1, 3, 10, 3, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
		this.addBlock(world, blockState, 2, 10, 0, boundingBox);
		this.addBlock(world, blockState2, 2, 10, 4, boundingBox);
		this.addBlock(world, blockState3, 0, 10, 2, boundingBox);
		this.addBlock(world, blockState4, 4, 10, 2, boundingBox);
		this.fillWithOutline(world, boundingBox, this.width - 5, 0, 0, this.width - 1, 9, 4, Blocks.SANDSTONE.getDefaultState(), Blocks.AIR.getDefaultState(), false);
		this.fillWithOutline(
			world, boundingBox, this.width - 4, 10, 1, this.width - 2, 10, 3, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false
		);
		this.addBlock(world, blockState, this.width - 3, 10, 0, boundingBox);
		this.addBlock(world, blockState2, this.width - 3, 10, 4, boundingBox);
		this.addBlock(world, blockState3, this.width - 5, 10, 2, boundingBox);
		this.addBlock(world, blockState4, this.width - 1, 10, 2, boundingBox);
		this.fillWithOutline(world, boundingBox, 8, 0, 0, 12, 4, 4, Blocks.SANDSTONE.getDefaultState(), Blocks.AIR.getDefaultState(), false);
		this.fillWithOutline(world, boundingBox, 9, 1, 0, 11, 3, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
		this.addBlock(world, Blocks.CUT_SANDSTONE.getDefaultState(), 9, 1, 1, boundingBox);
		this.addBlock(world, Blocks.CUT_SANDSTONE.getDefaultState(), 9, 2, 1, boundingBox);
		this.addBlock(world, Blocks.CUT_SANDSTONE.getDefaultState(), 9, 3, 1, boundingBox);
		this.addBlock(world, Blocks.CUT_SANDSTONE.getDefaultState(), 10, 3, 1, boundingBox);
		this.addBlock(world, Blocks.CUT_SANDSTONE.getDefaultState(), 11, 3, 1, boundingBox);
		this.addBlock(world, Blocks.CUT_SANDSTONE.getDefaultState(), 11, 2, 1, boundingBox);
		this.addBlock(world, Blocks.CUT_SANDSTONE.getDefaultState(), 11, 1, 1, boundingBox);
		this.fillWithOutline(world, boundingBox, 4, 1, 1, 8, 3, 3, Blocks.SANDSTONE.getDefaultState(), Blocks.AIR.getDefaultState(), false);
		this.fillWithOutline(world, boundingBox, 4, 1, 2, 8, 2, 2, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
		this.fillWithOutline(world, boundingBox, 12, 1, 1, 16, 3, 3, Blocks.SANDSTONE.getDefaultState(), Blocks.AIR.getDefaultState(), false);
		this.fillWithOutline(world, boundingBox, 12, 1, 2, 16, 2, 2, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
		this.fillWithOutline(
			world, boundingBox, 5, 4, 5, this.width - 6, 4, this.depth - 6, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false
		);
		this.fillWithOutline(world, boundingBox, 9, 4, 9, 11, 4, 11, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
		this.fillWithOutline(world, boundingBox, 8, 1, 8, 8, 3, 8, Blocks.CUT_SANDSTONE.getDefaultState(), Blocks.CUT_SANDSTONE.getDefaultState(), false);
		this.fillWithOutline(world, boundingBox, 12, 1, 8, 12, 3, 8, Blocks.CUT_SANDSTONE.getDefaultState(), Blocks.CUT_SANDSTONE.getDefaultState(), false);
		this.fillWithOutline(world, boundingBox, 8, 1, 12, 8, 3, 12, Blocks.CUT_SANDSTONE.getDefaultState(), Blocks.CUT_SANDSTONE.getDefaultState(), false);
		this.fillWithOutline(world, boundingBox, 12, 1, 12, 12, 3, 12, Blocks.CUT_SANDSTONE.getDefaultState(), Blocks.CUT_SANDSTONE.getDefaultState(), false);
		this.fillWithOutline(world, boundingBox, 1, 1, 5, 4, 4, 11, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
		this.fillWithOutline(
			world, boundingBox, this.width - 5, 1, 5, this.width - 2, 4, 11, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false
		);
		this.fillWithOutline(world, boundingBox, 6, 7, 9, 6, 7, 11, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
		this.fillWithOutline(
			world, boundingBox, this.width - 7, 7, 9, this.width - 7, 7, 11, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false
		);
		this.fillWithOutline(world, boundingBox, 5, 5, 9, 5, 7, 11, Blocks.CUT_SANDSTONE.getDefaultState(), Blocks.CUT_SANDSTONE.getDefaultState(), false);
		this.fillWithOutline(
			world, boundingBox, this.width - 6, 5, 9, this.width - 6, 7, 11, Blocks.CUT_SANDSTONE.getDefaultState(), Blocks.CUT_SANDSTONE.getDefaultState(), false
		);
		this.addBlock(world, Blocks.AIR.getDefaultState(), 5, 5, 10, boundingBox);
		this.addBlock(world, Blocks.AIR.getDefaultState(), 5, 6, 10, boundingBox);
		this.addBlock(world, Blocks.AIR.getDefaultState(), 6, 6, 10, boundingBox);
		this.addBlock(world, Blocks.AIR.getDefaultState(), this.width - 6, 5, 10, boundingBox);
		this.addBlock(world, Blocks.AIR.getDefaultState(), this.width - 6, 6, 10, boundingBox);
		this.addBlock(world, Blocks.AIR.getDefaultState(), this.width - 7, 6, 10, boundingBox);
		this.fillWithOutline(world, boundingBox, 2, 4, 4, 2, 6, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
		this.fillWithOutline(world, boundingBox, this.width - 3, 4, 4, this.width - 3, 6, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
		this.addBlock(world, blockState, 2, 4, 5, boundingBox);
		this.addBlock(world, blockState, 2, 3, 4, boundingBox);
		this.addBlock(world, blockState, this.width - 3, 4, 5, boundingBox);
		this.addBlock(world, blockState, this.width - 3, 3, 4, boundingBox);
		this.fillWithOutline(world, boundingBox, 1, 1, 3, 2, 2, 3, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
		this.fillWithOutline(
			world, boundingBox, this.width - 3, 1, 3, this.width - 2, 2, 3, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false
		);
		this.addBlock(world, Blocks.SANDSTONE.getDefaultState(), 1, 1, 2, boundingBox);
		this.addBlock(world, Blocks.SANDSTONE.getDefaultState(), this.width - 2, 1, 2, boundingBox);
		this.addBlock(world, Blocks.SANDSTONE_SLAB.getDefaultState(), 1, 2, 2, boundingBox);
		this.addBlock(world, Blocks.SANDSTONE_SLAB.getDefaultState(), this.width - 2, 2, 2, boundingBox);
		this.addBlock(world, blockState4, 2, 1, 2, boundingBox);
		this.addBlock(world, blockState3, this.width - 3, 1, 2, boundingBox);
		this.fillWithOutline(world, boundingBox, 4, 3, 5, 4, 3, 17, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
		this.fillWithOutline(
			world, boundingBox, this.width - 5, 3, 5, this.width - 5, 3, 17, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false
		);
		this.fillWithOutline(world, boundingBox, 3, 1, 5, 4, 2, 16, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
		this.fillWithOutline(world, boundingBox, this.width - 6, 1, 5, this.width - 5, 2, 16, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);

		for (int l = 5; l <= 17; l += 2) {
			this.addBlock(world, Blocks.CUT_SANDSTONE.getDefaultState(), 4, 1, l, boundingBox);
			this.addBlock(world, Blocks.CHISELED_SANDSTONE.getDefaultState(), 4, 2, l, boundingBox);
			this.addBlock(world, Blocks.CUT_SANDSTONE.getDefaultState(), this.width - 5, 1, l, boundingBox);
			this.addBlock(world, Blocks.CHISELED_SANDSTONE.getDefaultState(), this.width - 5, 2, l, boundingBox);
		}

		this.addBlock(world, Blocks.ORANGE_TERRACOTTA.getDefaultState(), 10, 0, 7, boundingBox);
		this.addBlock(world, Blocks.ORANGE_TERRACOTTA.getDefaultState(), 10, 0, 8, boundingBox);
		this.addBlock(world, Blocks.ORANGE_TERRACOTTA.getDefaultState(), 9, 0, 9, boundingBox);
		this.addBlock(world, Blocks.ORANGE_TERRACOTTA.getDefaultState(), 11, 0, 9, boundingBox);
		this.addBlock(world, Blocks.ORANGE_TERRACOTTA.getDefaultState(), 8, 0, 10, boundingBox);
		this.addBlock(world, Blocks.ORANGE_TERRACOTTA.getDefaultState(), 12, 0, 10, boundingBox);
		this.addBlock(world, Blocks.ORANGE_TERRACOTTA.getDefaultState(), 7, 0, 10, boundingBox);
		this.addBlock(world, Blocks.ORANGE_TERRACOTTA.getDefaultState(), 13, 0, 10, boundingBox);
		this.addBlock(world, Blocks.ORANGE_TERRACOTTA.getDefaultState(), 9, 0, 11, boundingBox);
		this.addBlock(world, Blocks.ORANGE_TERRACOTTA.getDefaultState(), 11, 0, 11, boundingBox);
		this.addBlock(world, Blocks.ORANGE_TERRACOTTA.getDefaultState(), 10, 0, 12, boundingBox);
		this.addBlock(world, Blocks.ORANGE_TERRACOTTA.getDefaultState(), 10, 0, 13, boundingBox);
		this.addBlock(world, Blocks.BLUE_TERRACOTTA.getDefaultState(), 10, 0, 10, boundingBox);

		for (int l = 0; l <= this.width - 1; l += this.width - 1) {
			this.addBlock(world, Blocks.CUT_SANDSTONE.getDefaultState(), l, 2, 1, boundingBox);
			this.addBlock(world, Blocks.ORANGE_TERRACOTTA.getDefaultState(), l, 2, 2, boundingBox);
			this.addBlock(world, Blocks.CUT_SANDSTONE.getDefaultState(), l, 2, 3, boundingBox);
			this.addBlock(world, Blocks.CUT_SANDSTONE.getDefaultState(), l, 3, 1, boundingBox);
			this.addBlock(world, Blocks.ORANGE_TERRACOTTA.getDefaultState(), l, 3, 2, boundingBox);
			this.addBlock(world, Blocks.CUT_SANDSTONE.getDefaultState(), l, 3, 3, boundingBox);
			this.addBlock(world, Blocks.ORANGE_TERRACOTTA.getDefaultState(), l, 4, 1, boundingBox);
			this.addBlock(world, Blocks.CHISELED_SANDSTONE.getDefaultState(), l, 4, 2, boundingBox);
			this.addBlock(world, Blocks.ORANGE_TERRACOTTA.getDefaultState(), l, 4, 3, boundingBox);
			this.addBlock(world, Blocks.CUT_SANDSTONE.getDefaultState(), l, 5, 1, boundingBox);
			this.addBlock(world, Blocks.ORANGE_TERRACOTTA.getDefaultState(), l, 5, 2, boundingBox);
			this.addBlock(world, Blocks.CUT_SANDSTONE.getDefaultState(), l, 5, 3, boundingBox);
			this.addBlock(world, Blocks.ORANGE_TERRACOTTA.getDefaultState(), l, 6, 1, boundingBox);
			this.addBlock(world, Blocks.CHISELED_SANDSTONE.getDefaultState(), l, 6, 2, boundingBox);
			this.addBlock(world, Blocks.ORANGE_TERRACOTTA.getDefaultState(), l, 6, 3, boundingBox);
			this.addBlock(world, Blocks.ORANGE_TERRACOTTA.getDefaultState(), l, 7, 1, boundingBox);
			this.addBlock(world, Blocks.ORANGE_TERRACOTTA.getDefaultState(), l, 7, 2, boundingBox);
			this.addBlock(world, Blocks.ORANGE_TERRACOTTA.getDefaultState(), l, 7, 3, boundingBox);
			this.addBlock(world, Blocks.CUT_SANDSTONE.getDefaultState(), l, 8, 1, boundingBox);
			this.addBlock(world, Blocks.CUT_SANDSTONE.getDefaultState(), l, 8, 2, boundingBox);
			this.addBlock(world, Blocks.CUT_SANDSTONE.getDefaultState(), l, 8, 3, boundingBox);
		}

		for (int l = 2; l <= this.width - 3; l += this.width - 3 - 2) {
			this.addBlock(world, Blocks.CUT_SANDSTONE.getDefaultState(), l - 1, 2, 0, boundingBox);
			this.addBlock(world, Blocks.ORANGE_TERRACOTTA.getDefaultState(), l, 2, 0, boundingBox);
			this.addBlock(world, Blocks.CUT_SANDSTONE.getDefaultState(), l + 1, 2, 0, boundingBox);
			this.addBlock(world, Blocks.CUT_SANDSTONE.getDefaultState(), l - 1, 3, 0, boundingBox);
			this.addBlock(world, Blocks.ORANGE_TERRACOTTA.getDefaultState(), l, 3, 0, boundingBox);
			this.addBlock(world, Blocks.CUT_SANDSTONE.getDefaultState(), l + 1, 3, 0, boundingBox);
			this.addBlock(world, Blocks.ORANGE_TERRACOTTA.getDefaultState(), l - 1, 4, 0, boundingBox);
			this.addBlock(world, Blocks.CHISELED_SANDSTONE.getDefaultState(), l, 4, 0, boundingBox);
			this.addBlock(world, Blocks.ORANGE_TERRACOTTA.getDefaultState(), l + 1, 4, 0, boundingBox);
			this.addBlock(world, Blocks.CUT_SANDSTONE.getDefaultState(), l - 1, 5, 0, boundingBox);
			this.addBlock(world, Blocks.ORANGE_TERRACOTTA.getDefaultState(), l, 5, 0, boundingBox);
			this.addBlock(world, Blocks.CUT_SANDSTONE.getDefaultState(), l + 1, 5, 0, boundingBox);
			this.addBlock(world, Blocks.ORANGE_TERRACOTTA.getDefaultState(), l - 1, 6, 0, boundingBox);
			this.addBlock(world, Blocks.CHISELED_SANDSTONE.getDefaultState(), l, 6, 0, boundingBox);
			this.addBlock(world, Blocks.ORANGE_TERRACOTTA.getDefaultState(), l + 1, 6, 0, boundingBox);
			this.addBlock(world, Blocks.ORANGE_TERRACOTTA.getDefaultState(), l - 1, 7, 0, boundingBox);
			this.addBlock(world, Blocks.ORANGE_TERRACOTTA.getDefaultState(), l, 7, 0, boundingBox);
			this.addBlock(world, Blocks.ORANGE_TERRACOTTA.getDefaultState(), l + 1, 7, 0, boundingBox);
			this.addBlock(world, Blocks.CUT_SANDSTONE.getDefaultState(), l - 1, 8, 0, boundingBox);
			this.addBlock(world, Blocks.CUT_SANDSTONE.getDefaultState(), l, 8, 0, boundingBox);
			this.addBlock(world, Blocks.CUT_SANDSTONE.getDefaultState(), l + 1, 8, 0, boundingBox);
		}

		this.fillWithOutline(world, boundingBox, 8, 4, 0, 12, 6, 0, Blocks.CUT_SANDSTONE.getDefaultState(), Blocks.CUT_SANDSTONE.getDefaultState(), false);
		this.addBlock(world, Blocks.AIR.getDefaultState(), 8, 6, 0, boundingBox);
		this.addBlock(world, Blocks.AIR.getDefaultState(), 12, 6, 0, boundingBox);
		this.addBlock(world, Blocks.ORANGE_TERRACOTTA.getDefaultState(), 9, 5, 0, boundingBox);
		this.addBlock(world, Blocks.CHISELED_SANDSTONE.getDefaultState(), 10, 5, 0, boundingBox);
		this.addBlock(world, Blocks.ORANGE_TERRACOTTA.getDefaultState(), 11, 5, 0, boundingBox);
		this.fillWithOutline(world, boundingBox, 8, -14, 8, 12, -11, 12, Blocks.CUT_SANDSTONE.getDefaultState(), Blocks.CUT_SANDSTONE.getDefaultState(), false);
		this.fillWithOutline(
			world, boundingBox, 8, -10, 8, 12, -10, 12, Blocks.CHISELED_SANDSTONE.getDefaultState(), Blocks.CHISELED_SANDSTONE.getDefaultState(), false
		);
		this.fillWithOutline(world, boundingBox, 8, -9, 8, 12, -9, 12, Blocks.CUT_SANDSTONE.getDefaultState(), Blocks.CUT_SANDSTONE.getDefaultState(), false);
		this.fillWithOutline(world, boundingBox, 8, -8, 8, 12, -1, 12, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
		this.fillWithOutline(world, boundingBox, 9, -11, 9, 11, -1, 11, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
		this.addBlock(world, Blocks.STONE_PRESSURE_PLATE.getDefaultState(), 10, -11, 10, boundingBox);
		this.fillWithOutline(world, boundingBox, 9, -13, 9, 11, -13, 11, Blocks.TNT.getDefaultState(), Blocks.AIR.getDefaultState(), false);
		this.addBlock(world, Blocks.AIR.getDefaultState(), 8, -11, 10, boundingBox);
		this.addBlock(world, Blocks.AIR.getDefaultState(), 8, -10, 10, boundingBox);
		this.addBlock(world, Blocks.CHISELED_SANDSTONE.getDefaultState(), 7, -10, 10, boundingBox);
		this.addBlock(world, Blocks.CUT_SANDSTONE.getDefaultState(), 7, -11, 10, boundingBox);
		this.addBlock(world, Blocks.AIR.getDefaultState(), 12, -11, 10, boundingBox);
		this.addBlock(world, Blocks.AIR.getDefaultState(), 12, -10, 10, boundingBox);
		this.addBlock(world, Blocks.CHISELED_SANDSTONE.getDefaultState(), 13, -10, 10, boundingBox);
		this.addBlock(world, Blocks.CUT_SANDSTONE.getDefaultState(), 13, -11, 10, boundingBox);
		this.addBlock(world, Blocks.AIR.getDefaultState(), 10, -11, 8, boundingBox);
		this.addBlock(world, Blocks.AIR.getDefaultState(), 10, -10, 8, boundingBox);
		this.addBlock(world, Blocks.CHISELED_SANDSTONE.getDefaultState(), 10, -10, 7, boundingBox);
		this.addBlock(world, Blocks.CUT_SANDSTONE.getDefaultState(), 10, -11, 7, boundingBox);
		this.addBlock(world, Blocks.AIR.getDefaultState(), 10, -11, 12, boundingBox);
		this.addBlock(world, Blocks.AIR.getDefaultState(), 10, -10, 12, boundingBox);
		this.addBlock(world, Blocks.CHISELED_SANDSTONE.getDefaultState(), 10, -10, 13, boundingBox);
		this.addBlock(world, Blocks.CUT_SANDSTONE.getDefaultState(), 10, -11, 13, boundingBox);

		for (Direction direction : Direction.Type.HORIZONTAL) {
			if (!this.hasPlacedChest[direction.getHorizontal()]) {
				int m = direction.getOffsetX() * 2;
				int n = direction.getOffsetZ() * 2;
				this.hasPlacedChest[direction.getHorizontal()] = this.addChest(world, boundingBox, random, 10 + m, -11, 10 + n, LootTables.DESERT_PYRAMID_CHEST);
			}
		}

		return true;
	}
}
