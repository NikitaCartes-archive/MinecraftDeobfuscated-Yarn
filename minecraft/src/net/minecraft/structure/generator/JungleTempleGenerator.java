package net.minecraft.structure.generator;

import java.util.Random;
import net.minecraft.class_3418;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeverBlock;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.block.RepeaterBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.TripwireBlock;
import net.minecraft.block.TripwireHookBlock;
import net.minecraft.block.VineBlock;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.block.enums.WireConnection;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.loot.LootTables;

public class JungleTempleGenerator extends class_3418 {
	private boolean placedMainChest;
	private boolean placedHiddenChest;
	private boolean placedTrap1;
	private boolean placedTrap2;
	private static final JungleTempleGenerator.class_3349 field_14403 = new JungleTempleGenerator.class_3349();

	public JungleTempleGenerator(Random random, int i, int j) {
		super(StructurePieceType.JUNGLE_TEMPLE, random, i, 64, j, 12, 10, 15);
	}

	public JungleTempleGenerator(StructureManager structureManager, CompoundTag compoundTag) {
		super(StructurePieceType.JUNGLE_TEMPLE, compoundTag);
		this.placedMainChest = compoundTag.getBoolean("placedMainChest");
		this.placedHiddenChest = compoundTag.getBoolean("placedHiddenChest");
		this.placedTrap1 = compoundTag.getBoolean("placedTrap1");
		this.placedTrap2 = compoundTag.getBoolean("placedTrap2");
	}

	@Override
	protected void toNbt(CompoundTag compoundTag) {
		super.toNbt(compoundTag);
		compoundTag.putBoolean("placedMainChest", this.placedMainChest);
		compoundTag.putBoolean("placedHiddenChest", this.placedHiddenChest);
		compoundTag.putBoolean("placedTrap1", this.placedTrap1);
		compoundTag.putBoolean("placedTrap2", this.placedTrap2);
	}

	@Override
	public boolean generate(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
		if (!this.method_14839(iWorld, mutableIntBoundingBox, 0)) {
			return false;
		} else {
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, -4, 0, this.width - 1, 0, this.depth - 1, false, random, field_14403);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 2, 1, 2, 9, 2, 2, false, random, field_14403);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 2, 1, 12, 9, 2, 12, false, random, field_14403);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 2, 1, 3, 2, 2, 11, false, random, field_14403);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 9, 1, 3, 9, 2, 11, false, random, field_14403);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 3, 1, 10, 6, 1, false, random, field_14403);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 3, 13, 10, 6, 13, false, random, field_14403);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 3, 2, 1, 6, 12, false, random, field_14403);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 10, 3, 2, 10, 6, 12, false, random, field_14403);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 2, 3, 2, 9, 3, 12, false, random, field_14403);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 2, 6, 2, 9, 6, 12, false, random, field_14403);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 3, 7, 3, 8, 7, 11, false, random, field_14403);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 8, 4, 7, 8, 10, false, random, field_14403);
			this.fill(iWorld, mutableIntBoundingBox, 3, 1, 3, 8, 2, 11);
			this.fill(iWorld, mutableIntBoundingBox, 4, 3, 6, 7, 3, 9);
			this.fill(iWorld, mutableIntBoundingBox, 2, 4, 2, 9, 5, 12);
			this.fill(iWorld, mutableIntBoundingBox, 4, 6, 5, 7, 6, 9);
			this.fill(iWorld, mutableIntBoundingBox, 5, 7, 6, 6, 7, 8);
			this.fill(iWorld, mutableIntBoundingBox, 5, 1, 2, 6, 2, 2);
			this.fill(iWorld, mutableIntBoundingBox, 5, 2, 12, 6, 2, 12);
			this.fill(iWorld, mutableIntBoundingBox, 5, 5, 1, 6, 5, 1);
			this.fill(iWorld, mutableIntBoundingBox, 5, 5, 13, 6, 5, 13);
			this.addBlock(iWorld, Blocks.field_10124.getDefaultState(), 1, 5, 5, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10124.getDefaultState(), 10, 5, 5, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10124.getDefaultState(), 1, 5, 9, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10124.getDefaultState(), 10, 5, 9, mutableIntBoundingBox);

			for (int i = 0; i <= 14; i += 14) {
				this.fillWithOutline(iWorld, mutableIntBoundingBox, 2, 4, i, 2, 5, i, false, random, field_14403);
				this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 4, i, 4, 5, i, false, random, field_14403);
				this.fillWithOutline(iWorld, mutableIntBoundingBox, 7, 4, i, 7, 5, i, false, random, field_14403);
				this.fillWithOutline(iWorld, mutableIntBoundingBox, 9, 4, i, 9, 5, i, false, random, field_14403);
			}

			this.fillWithOutline(iWorld, mutableIntBoundingBox, 5, 6, 0, 6, 6, 0, false, random, field_14403);

			for (int i = 0; i <= 11; i += 11) {
				for (int j = 2; j <= 12; j += 2) {
					this.fillWithOutline(iWorld, mutableIntBoundingBox, i, 4, j, i, 5, j, false, random, field_14403);
				}

				this.fillWithOutline(iWorld, mutableIntBoundingBox, i, 6, 5, i, 6, 5, false, random, field_14403);
				this.fillWithOutline(iWorld, mutableIntBoundingBox, i, 6, 9, i, 6, 9, false, random, field_14403);
			}

			this.fillWithOutline(iWorld, mutableIntBoundingBox, 2, 7, 2, 2, 9, 2, false, random, field_14403);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 9, 7, 2, 9, 9, 2, false, random, field_14403);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 2, 7, 12, 2, 9, 12, false, random, field_14403);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 9, 7, 12, 9, 9, 12, false, random, field_14403);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 9, 4, 4, 9, 4, false, random, field_14403);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 7, 9, 4, 7, 9, 4, false, random, field_14403);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 9, 10, 4, 9, 10, false, random, field_14403);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 7, 9, 10, 7, 9, 10, false, random, field_14403);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 5, 9, 7, 6, 9, 7, false, random, field_14403);
			BlockState blockState = Blocks.field_10596.getDefaultState().with(StairsBlock.FACING, Direction.EAST);
			BlockState blockState2 = Blocks.field_10596.getDefaultState().with(StairsBlock.FACING, Direction.WEST);
			BlockState blockState3 = Blocks.field_10596.getDefaultState().with(StairsBlock.FACING, Direction.SOUTH);
			BlockState blockState4 = Blocks.field_10596.getDefaultState().with(StairsBlock.FACING, Direction.NORTH);
			this.addBlock(iWorld, blockState4, 5, 9, 6, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState4, 6, 9, 6, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState3, 5, 9, 8, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState3, 6, 9, 8, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState4, 4, 0, 0, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState4, 5, 0, 0, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState4, 6, 0, 0, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState4, 7, 0, 0, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState4, 4, 1, 8, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState4, 4, 2, 9, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState4, 4, 3, 10, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState4, 7, 1, 8, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState4, 7, 2, 9, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState4, 7, 3, 10, mutableIntBoundingBox);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 1, 9, 4, 1, 9, false, random, field_14403);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 7, 1, 9, 7, 1, 9, false, random, field_14403);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 1, 10, 7, 2, 10, false, random, field_14403);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 5, 4, 5, 6, 4, 5, false, random, field_14403);
			this.addBlock(iWorld, blockState, 4, 4, 5, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState2, 7, 4, 5, mutableIntBoundingBox);

			for (int k = 0; k < 4; k++) {
				this.addBlock(iWorld, blockState3, 5, 0 - k, 6 + k, mutableIntBoundingBox);
				this.addBlock(iWorld, blockState3, 6, 0 - k, 6 + k, mutableIntBoundingBox);
				this.fill(iWorld, mutableIntBoundingBox, 5, 0 - k, 7 + k, 6, 0 - k, 9 + k);
			}

			this.fill(iWorld, mutableIntBoundingBox, 1, -3, 12, 10, -1, 13);
			this.fill(iWorld, mutableIntBoundingBox, 1, -3, 1, 3, -1, 13);
			this.fill(iWorld, mutableIntBoundingBox, 1, -3, 1, 9, -1, 5);

			for (int k = 1; k <= 13; k += 2) {
				this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, -3, k, 1, -2, k, false, random, field_14403);
			}

			for (int k = 2; k <= 12; k += 2) {
				this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, -1, k, 3, -1, k, false, random, field_14403);
			}

			this.fillWithOutline(iWorld, mutableIntBoundingBox, 2, -2, 1, 5, -2, 1, false, random, field_14403);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 7, -2, 1, 9, -2, 1, false, random, field_14403);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 6, -3, 1, 6, -3, 1, false, random, field_14403);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 6, -1, 1, 6, -1, 1, false, random, field_14403);
			this.addBlock(
				iWorld,
				Blocks.field_10348.getDefaultState().with(TripwireHookBlock.FACING, Direction.EAST).with(TripwireHookBlock.field_11669, Boolean.valueOf(true)),
				1,
				-3,
				8,
				mutableIntBoundingBox
			);
			this.addBlock(
				iWorld,
				Blocks.field_10348.getDefaultState().with(TripwireHookBlock.FACING, Direction.WEST).with(TripwireHookBlock.field_11669, Boolean.valueOf(true)),
				4,
				-3,
				8,
				mutableIntBoundingBox
			);
			this.addBlock(
				iWorld,
				Blocks.field_10589
					.getDefaultState()
					.with(TripwireBlock.EAST, Boolean.valueOf(true))
					.with(TripwireBlock.WEST, Boolean.valueOf(true))
					.with(TripwireBlock.ATTACHED, Boolean.valueOf(true)),
				2,
				-3,
				8,
				mutableIntBoundingBox
			);
			this.addBlock(
				iWorld,
				Blocks.field_10589
					.getDefaultState()
					.with(TripwireBlock.EAST, Boolean.valueOf(true))
					.with(TripwireBlock.WEST, Boolean.valueOf(true))
					.with(TripwireBlock.ATTACHED, Boolean.valueOf(true)),
				3,
				-3,
				8,
				mutableIntBoundingBox
			);
			BlockState blockState5 = Blocks.field_10091
				.getDefaultState()
				.with(RedstoneWireBlock.WIRE_CONNECTION_NORTH, WireConnection.field_12689)
				.with(RedstoneWireBlock.WIRE_CONNECTION_SOUTH, WireConnection.field_12689);
			this.addBlock(
				iWorld, Blocks.field_10091.getDefaultState().with(RedstoneWireBlock.WIRE_CONNECTION_SOUTH, WireConnection.field_12689), 5, -3, 7, mutableIntBoundingBox
			);
			this.addBlock(iWorld, blockState5, 5, -3, 6, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState5, 5, -3, 5, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState5, 5, -3, 4, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState5, 5, -3, 3, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState5, 5, -3, 2, mutableIntBoundingBox);
			this.addBlock(
				iWorld,
				Blocks.field_10091
					.getDefaultState()
					.with(RedstoneWireBlock.WIRE_CONNECTION_NORTH, WireConnection.field_12689)
					.with(RedstoneWireBlock.WIRE_CONNECTION_WEST, WireConnection.field_12689),
				5,
				-3,
				1,
				mutableIntBoundingBox
			);
			this.addBlock(
				iWorld, Blocks.field_10091.getDefaultState().with(RedstoneWireBlock.WIRE_CONNECTION_EAST, WireConnection.field_12689), 4, -3, 1, mutableIntBoundingBox
			);
			this.addBlock(iWorld, Blocks.field_9989.getDefaultState(), 3, -3, 1, mutableIntBoundingBox);
			if (!this.placedTrap1) {
				this.placedTrap1 = this.addDispenser(iWorld, mutableIntBoundingBox, random, 3, -2, 1, Direction.NORTH, LootTables.DISPENSER_JUNGLE_TEMPLE);
			}

			this.addBlock(iWorld, Blocks.field_10597.getDefaultState().with(VineBlock.SOUTH, Boolean.valueOf(true)), 3, -2, 2, mutableIntBoundingBox);
			this.addBlock(
				iWorld,
				Blocks.field_10348.getDefaultState().with(TripwireHookBlock.FACING, Direction.NORTH).with(TripwireHookBlock.field_11669, Boolean.valueOf(true)),
				7,
				-3,
				1,
				mutableIntBoundingBox
			);
			this.addBlock(
				iWorld,
				Blocks.field_10348.getDefaultState().with(TripwireHookBlock.FACING, Direction.SOUTH).with(TripwireHookBlock.field_11669, Boolean.valueOf(true)),
				7,
				-3,
				5,
				mutableIntBoundingBox
			);
			this.addBlock(
				iWorld,
				Blocks.field_10589
					.getDefaultState()
					.with(TripwireBlock.NORTH, Boolean.valueOf(true))
					.with(TripwireBlock.SOUTH, Boolean.valueOf(true))
					.with(TripwireBlock.ATTACHED, Boolean.valueOf(true)),
				7,
				-3,
				2,
				mutableIntBoundingBox
			);
			this.addBlock(
				iWorld,
				Blocks.field_10589
					.getDefaultState()
					.with(TripwireBlock.NORTH, Boolean.valueOf(true))
					.with(TripwireBlock.SOUTH, Boolean.valueOf(true))
					.with(TripwireBlock.ATTACHED, Boolean.valueOf(true)),
				7,
				-3,
				3,
				mutableIntBoundingBox
			);
			this.addBlock(
				iWorld,
				Blocks.field_10589
					.getDefaultState()
					.with(TripwireBlock.NORTH, Boolean.valueOf(true))
					.with(TripwireBlock.SOUTH, Boolean.valueOf(true))
					.with(TripwireBlock.ATTACHED, Boolean.valueOf(true)),
				7,
				-3,
				4,
				mutableIntBoundingBox
			);
			this.addBlock(
				iWorld, Blocks.field_10091.getDefaultState().with(RedstoneWireBlock.WIRE_CONNECTION_EAST, WireConnection.field_12689), 8, -3, 6, mutableIntBoundingBox
			);
			this.addBlock(
				iWorld,
				Blocks.field_10091
					.getDefaultState()
					.with(RedstoneWireBlock.WIRE_CONNECTION_WEST, WireConnection.field_12689)
					.with(RedstoneWireBlock.WIRE_CONNECTION_SOUTH, WireConnection.field_12689),
				9,
				-3,
				6,
				mutableIntBoundingBox
			);
			this.addBlock(
				iWorld,
				Blocks.field_10091
					.getDefaultState()
					.with(RedstoneWireBlock.WIRE_CONNECTION_NORTH, WireConnection.field_12689)
					.with(RedstoneWireBlock.WIRE_CONNECTION_SOUTH, WireConnection.field_12686),
				9,
				-3,
				5,
				mutableIntBoundingBox
			);
			this.addBlock(iWorld, Blocks.field_9989.getDefaultState(), 9, -3, 4, mutableIntBoundingBox);
			this.addBlock(
				iWorld, Blocks.field_10091.getDefaultState().with(RedstoneWireBlock.WIRE_CONNECTION_NORTH, WireConnection.field_12689), 9, -2, 4, mutableIntBoundingBox
			);
			if (!this.placedTrap2) {
				this.placedTrap2 = this.addDispenser(iWorld, mutableIntBoundingBox, random, 9, -2, 3, Direction.WEST, LootTables.DISPENSER_JUNGLE_TEMPLE);
			}

			this.addBlock(iWorld, Blocks.field_10597.getDefaultState().with(VineBlock.EAST, Boolean.valueOf(true)), 8, -1, 3, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10597.getDefaultState().with(VineBlock.EAST, Boolean.valueOf(true)), 8, -2, 3, mutableIntBoundingBox);
			if (!this.placedMainChest) {
				this.placedMainChest = this.method_14915(iWorld, mutableIntBoundingBox, random, 8, -3, 3, LootTables.CHEST_JUNGLE_TEMPLE);
			}

			this.addBlock(iWorld, Blocks.field_9989.getDefaultState(), 9, -3, 2, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_9989.getDefaultState(), 8, -3, 1, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_9989.getDefaultState(), 4, -3, 5, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_9989.getDefaultState(), 5, -2, 5, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_9989.getDefaultState(), 5, -1, 5, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_9989.getDefaultState(), 6, -3, 5, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_9989.getDefaultState(), 7, -2, 5, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_9989.getDefaultState(), 7, -1, 5, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_9989.getDefaultState(), 8, -3, 5, mutableIntBoundingBox);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 9, -1, 1, 9, -1, 5, false, random, field_14403);
			this.fill(iWorld, mutableIntBoundingBox, 8, -3, 8, 10, -1, 10);
			this.addBlock(iWorld, Blocks.field_10552.getDefaultState(), 8, -2, 11, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10552.getDefaultState(), 9, -2, 11, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10552.getDefaultState(), 10, -2, 11, mutableIntBoundingBox);
			BlockState blockState6 = Blocks.field_10363
				.getDefaultState()
				.with(LeverBlock.field_11177, Direction.NORTH)
				.with(LeverBlock.FACE, WallMountLocation.field_12471);
			this.addBlock(iWorld, blockState6, 8, -2, 12, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState6, 9, -2, 12, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState6, 10, -2, 12, mutableIntBoundingBox);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 8, -3, 8, 8, -3, 10, false, random, field_14403);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 10, -3, 8, 10, -3, 10, false, random, field_14403);
			this.addBlock(iWorld, Blocks.field_9989.getDefaultState(), 10, -2, 9, mutableIntBoundingBox);
			this.addBlock(
				iWorld, Blocks.field_10091.getDefaultState().with(RedstoneWireBlock.WIRE_CONNECTION_NORTH, WireConnection.field_12689), 8, -2, 9, mutableIntBoundingBox
			);
			this.addBlock(
				iWorld, Blocks.field_10091.getDefaultState().with(RedstoneWireBlock.WIRE_CONNECTION_SOUTH, WireConnection.field_12689), 8, -2, 10, mutableIntBoundingBox
			);
			this.addBlock(iWorld, Blocks.field_10091.getDefaultState(), 10, -1, 9, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10615.getDefaultState().with(PistonBlock.FACING, Direction.UP), 9, -2, 8, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10615.getDefaultState().with(PistonBlock.FACING, Direction.WEST), 10, -2, 8, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10615.getDefaultState().with(PistonBlock.FACING, Direction.WEST), 10, -1, 8, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10450.getDefaultState().with(RepeaterBlock.field_11177, Direction.NORTH), 10, -2, 10, mutableIntBoundingBox);
			if (!this.placedHiddenChest) {
				this.placedHiddenChest = this.method_14915(iWorld, mutableIntBoundingBox, random, 9, -3, 10, LootTables.CHEST_JUNGLE_TEMPLE);
			}

			return true;
		}
	}

	static class class_3349 extends StructurePiece.class_3444 {
		private class_3349() {
		}

		@Override
		public void method_14948(Random random, int i, int j, int k, boolean bl) {
			if (random.nextFloat() < 0.4F) {
				this.block = Blocks.field_10445.getDefaultState();
			} else {
				this.block = Blocks.field_9989.getDefaultState();
			}
		}
	}
}
