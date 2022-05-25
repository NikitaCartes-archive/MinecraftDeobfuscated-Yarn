package net.minecraft.structure;

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
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class JungleTempleGenerator extends ShiftableStructurePiece {
	public static final int field_34727 = 12;
	public static final int DEPTH = 15;
	private boolean placedMainChest;
	private boolean placedHiddenChest;
	private boolean placedTrap1;
	private boolean placedTrap2;
	private static final JungleTempleGenerator.CobblestoneRandomizer COBBLESTONE_RANDOMIZER = new JungleTempleGenerator.CobblestoneRandomizer();

	public JungleTempleGenerator(Random random, int x, int z) {
		super(StructurePieceType.JUNGLE_TEMPLE, x, 64, z, 12, 10, 15, getRandomHorizontalDirection(random));
	}

	public JungleTempleGenerator(NbtCompound nbt) {
		super(StructurePieceType.JUNGLE_TEMPLE, nbt);
		this.placedMainChest = nbt.getBoolean("placedMainChest");
		this.placedHiddenChest = nbt.getBoolean("placedHiddenChest");
		this.placedTrap1 = nbt.getBoolean("placedTrap1");
		this.placedTrap2 = nbt.getBoolean("placedTrap2");
	}

	@Override
	protected void writeNbt(StructureContext context, NbtCompound nbt) {
		super.writeNbt(context, nbt);
		nbt.putBoolean("placedMainChest", this.placedMainChest);
		nbt.putBoolean("placedHiddenChest", this.placedHiddenChest);
		nbt.putBoolean("placedTrap1", this.placedTrap1);
		nbt.putBoolean("placedTrap2", this.placedTrap2);
	}

	@Override
	public void generate(
		StructureWorldAccess world,
		StructureAccessor structureAccessor,
		ChunkGenerator chunkGenerator,
		Random random,
		BlockBox chunkBox,
		ChunkPos chunkPos,
		BlockPos pivot
	) {
		if (this.adjustToAverageHeight(world, chunkBox, 0)) {
			this.fillWithOutline(world, chunkBox, 0, -4, 0, this.width - 1, 0, this.depth - 1, false, random, COBBLESTONE_RANDOMIZER);
			this.fillWithOutline(world, chunkBox, 2, 1, 2, 9, 2, 2, false, random, COBBLESTONE_RANDOMIZER);
			this.fillWithOutline(world, chunkBox, 2, 1, 12, 9, 2, 12, false, random, COBBLESTONE_RANDOMIZER);
			this.fillWithOutline(world, chunkBox, 2, 1, 3, 2, 2, 11, false, random, COBBLESTONE_RANDOMIZER);
			this.fillWithOutline(world, chunkBox, 9, 1, 3, 9, 2, 11, false, random, COBBLESTONE_RANDOMIZER);
			this.fillWithOutline(world, chunkBox, 1, 3, 1, 10, 6, 1, false, random, COBBLESTONE_RANDOMIZER);
			this.fillWithOutline(world, chunkBox, 1, 3, 13, 10, 6, 13, false, random, COBBLESTONE_RANDOMIZER);
			this.fillWithOutline(world, chunkBox, 1, 3, 2, 1, 6, 12, false, random, COBBLESTONE_RANDOMIZER);
			this.fillWithOutline(world, chunkBox, 10, 3, 2, 10, 6, 12, false, random, COBBLESTONE_RANDOMIZER);
			this.fillWithOutline(world, chunkBox, 2, 3, 2, 9, 3, 12, false, random, COBBLESTONE_RANDOMIZER);
			this.fillWithOutline(world, chunkBox, 2, 6, 2, 9, 6, 12, false, random, COBBLESTONE_RANDOMIZER);
			this.fillWithOutline(world, chunkBox, 3, 7, 3, 8, 7, 11, false, random, COBBLESTONE_RANDOMIZER);
			this.fillWithOutline(world, chunkBox, 4, 8, 4, 7, 8, 10, false, random, COBBLESTONE_RANDOMIZER);
			this.fill(world, chunkBox, 3, 1, 3, 8, 2, 11);
			this.fill(world, chunkBox, 4, 3, 6, 7, 3, 9);
			this.fill(world, chunkBox, 2, 4, 2, 9, 5, 12);
			this.fill(world, chunkBox, 4, 6, 5, 7, 6, 9);
			this.fill(world, chunkBox, 5, 7, 6, 6, 7, 8);
			this.fill(world, chunkBox, 5, 1, 2, 6, 2, 2);
			this.fill(world, chunkBox, 5, 2, 12, 6, 2, 12);
			this.fill(world, chunkBox, 5, 5, 1, 6, 5, 1);
			this.fill(world, chunkBox, 5, 5, 13, 6, 5, 13);
			this.addBlock(world, Blocks.AIR.getDefaultState(), 1, 5, 5, chunkBox);
			this.addBlock(world, Blocks.AIR.getDefaultState(), 10, 5, 5, chunkBox);
			this.addBlock(world, Blocks.AIR.getDefaultState(), 1, 5, 9, chunkBox);
			this.addBlock(world, Blocks.AIR.getDefaultState(), 10, 5, 9, chunkBox);

			for (int i = 0; i <= 14; i += 14) {
				this.fillWithOutline(world, chunkBox, 2, 4, i, 2, 5, i, false, random, COBBLESTONE_RANDOMIZER);
				this.fillWithOutline(world, chunkBox, 4, 4, i, 4, 5, i, false, random, COBBLESTONE_RANDOMIZER);
				this.fillWithOutline(world, chunkBox, 7, 4, i, 7, 5, i, false, random, COBBLESTONE_RANDOMIZER);
				this.fillWithOutline(world, chunkBox, 9, 4, i, 9, 5, i, false, random, COBBLESTONE_RANDOMIZER);
			}

			this.fillWithOutline(world, chunkBox, 5, 6, 0, 6, 6, 0, false, random, COBBLESTONE_RANDOMIZER);

			for (int i = 0; i <= 11; i += 11) {
				for (int j = 2; j <= 12; j += 2) {
					this.fillWithOutline(world, chunkBox, i, 4, j, i, 5, j, false, random, COBBLESTONE_RANDOMIZER);
				}

				this.fillWithOutline(world, chunkBox, i, 6, 5, i, 6, 5, false, random, COBBLESTONE_RANDOMIZER);
				this.fillWithOutline(world, chunkBox, i, 6, 9, i, 6, 9, false, random, COBBLESTONE_RANDOMIZER);
			}

			this.fillWithOutline(world, chunkBox, 2, 7, 2, 2, 9, 2, false, random, COBBLESTONE_RANDOMIZER);
			this.fillWithOutline(world, chunkBox, 9, 7, 2, 9, 9, 2, false, random, COBBLESTONE_RANDOMIZER);
			this.fillWithOutline(world, chunkBox, 2, 7, 12, 2, 9, 12, false, random, COBBLESTONE_RANDOMIZER);
			this.fillWithOutline(world, chunkBox, 9, 7, 12, 9, 9, 12, false, random, COBBLESTONE_RANDOMIZER);
			this.fillWithOutline(world, chunkBox, 4, 9, 4, 4, 9, 4, false, random, COBBLESTONE_RANDOMIZER);
			this.fillWithOutline(world, chunkBox, 7, 9, 4, 7, 9, 4, false, random, COBBLESTONE_RANDOMIZER);
			this.fillWithOutline(world, chunkBox, 4, 9, 10, 4, 9, 10, false, random, COBBLESTONE_RANDOMIZER);
			this.fillWithOutline(world, chunkBox, 7, 9, 10, 7, 9, 10, false, random, COBBLESTONE_RANDOMIZER);
			this.fillWithOutline(world, chunkBox, 5, 9, 7, 6, 9, 7, false, random, COBBLESTONE_RANDOMIZER);
			BlockState blockState = Blocks.COBBLESTONE_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.EAST);
			BlockState blockState2 = Blocks.COBBLESTONE_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.WEST);
			BlockState blockState3 = Blocks.COBBLESTONE_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.SOUTH);
			BlockState blockState4 = Blocks.COBBLESTONE_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.NORTH);
			this.addBlock(world, blockState4, 5, 9, 6, chunkBox);
			this.addBlock(world, blockState4, 6, 9, 6, chunkBox);
			this.addBlock(world, blockState3, 5, 9, 8, chunkBox);
			this.addBlock(world, blockState3, 6, 9, 8, chunkBox);
			this.addBlock(world, blockState4, 4, 0, 0, chunkBox);
			this.addBlock(world, blockState4, 5, 0, 0, chunkBox);
			this.addBlock(world, blockState4, 6, 0, 0, chunkBox);
			this.addBlock(world, blockState4, 7, 0, 0, chunkBox);
			this.addBlock(world, blockState4, 4, 1, 8, chunkBox);
			this.addBlock(world, blockState4, 4, 2, 9, chunkBox);
			this.addBlock(world, blockState4, 4, 3, 10, chunkBox);
			this.addBlock(world, blockState4, 7, 1, 8, chunkBox);
			this.addBlock(world, blockState4, 7, 2, 9, chunkBox);
			this.addBlock(world, blockState4, 7, 3, 10, chunkBox);
			this.fillWithOutline(world, chunkBox, 4, 1, 9, 4, 1, 9, false, random, COBBLESTONE_RANDOMIZER);
			this.fillWithOutline(world, chunkBox, 7, 1, 9, 7, 1, 9, false, random, COBBLESTONE_RANDOMIZER);
			this.fillWithOutline(world, chunkBox, 4, 1, 10, 7, 2, 10, false, random, COBBLESTONE_RANDOMIZER);
			this.fillWithOutline(world, chunkBox, 5, 4, 5, 6, 4, 5, false, random, COBBLESTONE_RANDOMIZER);
			this.addBlock(world, blockState, 4, 4, 5, chunkBox);
			this.addBlock(world, blockState2, 7, 4, 5, chunkBox);

			for (int k = 0; k < 4; k++) {
				this.addBlock(world, blockState3, 5, 0 - k, 6 + k, chunkBox);
				this.addBlock(world, blockState3, 6, 0 - k, 6 + k, chunkBox);
				this.fill(world, chunkBox, 5, 0 - k, 7 + k, 6, 0 - k, 9 + k);
			}

			this.fill(world, chunkBox, 1, -3, 12, 10, -1, 13);
			this.fill(world, chunkBox, 1, -3, 1, 3, -1, 13);
			this.fill(world, chunkBox, 1, -3, 1, 9, -1, 5);

			for (int k = 1; k <= 13; k += 2) {
				this.fillWithOutline(world, chunkBox, 1, -3, k, 1, -2, k, false, random, COBBLESTONE_RANDOMIZER);
			}

			for (int k = 2; k <= 12; k += 2) {
				this.fillWithOutline(world, chunkBox, 1, -1, k, 3, -1, k, false, random, COBBLESTONE_RANDOMIZER);
			}

			this.fillWithOutline(world, chunkBox, 2, -2, 1, 5, -2, 1, false, random, COBBLESTONE_RANDOMIZER);
			this.fillWithOutline(world, chunkBox, 7, -2, 1, 9, -2, 1, false, random, COBBLESTONE_RANDOMIZER);
			this.fillWithOutline(world, chunkBox, 6, -3, 1, 6, -3, 1, false, random, COBBLESTONE_RANDOMIZER);
			this.fillWithOutline(world, chunkBox, 6, -1, 1, 6, -1, 1, false, random, COBBLESTONE_RANDOMIZER);
			this.addBlock(
				world,
				Blocks.TRIPWIRE_HOOK.getDefaultState().with(TripwireHookBlock.FACING, Direction.EAST).with(TripwireHookBlock.ATTACHED, Boolean.valueOf(true)),
				1,
				-3,
				8,
				chunkBox
			);
			this.addBlock(
				world,
				Blocks.TRIPWIRE_HOOK.getDefaultState().with(TripwireHookBlock.FACING, Direction.WEST).with(TripwireHookBlock.ATTACHED, Boolean.valueOf(true)),
				4,
				-3,
				8,
				chunkBox
			);
			this.addBlock(
				world,
				Blocks.TRIPWIRE
					.getDefaultState()
					.with(TripwireBlock.EAST, Boolean.valueOf(true))
					.with(TripwireBlock.WEST, Boolean.valueOf(true))
					.with(TripwireBlock.ATTACHED, Boolean.valueOf(true)),
				2,
				-3,
				8,
				chunkBox
			);
			this.addBlock(
				world,
				Blocks.TRIPWIRE
					.getDefaultState()
					.with(TripwireBlock.EAST, Boolean.valueOf(true))
					.with(TripwireBlock.WEST, Boolean.valueOf(true))
					.with(TripwireBlock.ATTACHED, Boolean.valueOf(true)),
				3,
				-3,
				8,
				chunkBox
			);
			BlockState blockState5 = Blocks.REDSTONE_WIRE
				.getDefaultState()
				.with(RedstoneWireBlock.WIRE_CONNECTION_NORTH, WireConnection.SIDE)
				.with(RedstoneWireBlock.WIRE_CONNECTION_SOUTH, WireConnection.SIDE);
			this.addBlock(world, blockState5, 5, -3, 7, chunkBox);
			this.addBlock(world, blockState5, 5, -3, 6, chunkBox);
			this.addBlock(world, blockState5, 5, -3, 5, chunkBox);
			this.addBlock(world, blockState5, 5, -3, 4, chunkBox);
			this.addBlock(world, blockState5, 5, -3, 3, chunkBox);
			this.addBlock(world, blockState5, 5, -3, 2, chunkBox);
			this.addBlock(
				world,
				Blocks.REDSTONE_WIRE
					.getDefaultState()
					.with(RedstoneWireBlock.WIRE_CONNECTION_NORTH, WireConnection.SIDE)
					.with(RedstoneWireBlock.WIRE_CONNECTION_WEST, WireConnection.SIDE),
				5,
				-3,
				1,
				chunkBox
			);
			this.addBlock(
				world,
				Blocks.REDSTONE_WIRE
					.getDefaultState()
					.with(RedstoneWireBlock.WIRE_CONNECTION_EAST, WireConnection.SIDE)
					.with(RedstoneWireBlock.WIRE_CONNECTION_WEST, WireConnection.SIDE),
				4,
				-3,
				1,
				chunkBox
			);
			this.addBlock(world, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 3, -3, 1, chunkBox);
			if (!this.placedTrap1) {
				this.placedTrap1 = this.addDispenser(world, chunkBox, random, 3, -2, 1, Direction.NORTH, LootTables.JUNGLE_TEMPLE_DISPENSER_CHEST);
			}

			this.addBlock(world, Blocks.VINE.getDefaultState().with(VineBlock.SOUTH, Boolean.valueOf(true)), 3, -2, 2, chunkBox);
			this.addBlock(
				world,
				Blocks.TRIPWIRE_HOOK.getDefaultState().with(TripwireHookBlock.FACING, Direction.NORTH).with(TripwireHookBlock.ATTACHED, Boolean.valueOf(true)),
				7,
				-3,
				1,
				chunkBox
			);
			this.addBlock(
				world,
				Blocks.TRIPWIRE_HOOK.getDefaultState().with(TripwireHookBlock.FACING, Direction.SOUTH).with(TripwireHookBlock.ATTACHED, Boolean.valueOf(true)),
				7,
				-3,
				5,
				chunkBox
			);
			this.addBlock(
				world,
				Blocks.TRIPWIRE
					.getDefaultState()
					.with(TripwireBlock.NORTH, Boolean.valueOf(true))
					.with(TripwireBlock.SOUTH, Boolean.valueOf(true))
					.with(TripwireBlock.ATTACHED, Boolean.valueOf(true)),
				7,
				-3,
				2,
				chunkBox
			);
			this.addBlock(
				world,
				Blocks.TRIPWIRE
					.getDefaultState()
					.with(TripwireBlock.NORTH, Boolean.valueOf(true))
					.with(TripwireBlock.SOUTH, Boolean.valueOf(true))
					.with(TripwireBlock.ATTACHED, Boolean.valueOf(true)),
				7,
				-3,
				3,
				chunkBox
			);
			this.addBlock(
				world,
				Blocks.TRIPWIRE
					.getDefaultState()
					.with(TripwireBlock.NORTH, Boolean.valueOf(true))
					.with(TripwireBlock.SOUTH, Boolean.valueOf(true))
					.with(TripwireBlock.ATTACHED, Boolean.valueOf(true)),
				7,
				-3,
				4,
				chunkBox
			);
			this.addBlock(
				world,
				Blocks.REDSTONE_WIRE
					.getDefaultState()
					.with(RedstoneWireBlock.WIRE_CONNECTION_EAST, WireConnection.SIDE)
					.with(RedstoneWireBlock.WIRE_CONNECTION_WEST, WireConnection.SIDE),
				8,
				-3,
				6,
				chunkBox
			);
			this.addBlock(
				world,
				Blocks.REDSTONE_WIRE
					.getDefaultState()
					.with(RedstoneWireBlock.WIRE_CONNECTION_WEST, WireConnection.SIDE)
					.with(RedstoneWireBlock.WIRE_CONNECTION_SOUTH, WireConnection.SIDE),
				9,
				-3,
				6,
				chunkBox
			);
			this.addBlock(
				world,
				Blocks.REDSTONE_WIRE
					.getDefaultState()
					.with(RedstoneWireBlock.WIRE_CONNECTION_NORTH, WireConnection.SIDE)
					.with(RedstoneWireBlock.WIRE_CONNECTION_SOUTH, WireConnection.UP),
				9,
				-3,
				5,
				chunkBox
			);
			this.addBlock(world, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 9, -3, 4, chunkBox);
			this.addBlock(world, blockState5, 9, -2, 4, chunkBox);
			if (!this.placedTrap2) {
				this.placedTrap2 = this.addDispenser(world, chunkBox, random, 9, -2, 3, Direction.WEST, LootTables.JUNGLE_TEMPLE_DISPENSER_CHEST);
			}

			this.addBlock(world, Blocks.VINE.getDefaultState().with(VineBlock.EAST, Boolean.valueOf(true)), 8, -1, 3, chunkBox);
			this.addBlock(world, Blocks.VINE.getDefaultState().with(VineBlock.EAST, Boolean.valueOf(true)), 8, -2, 3, chunkBox);
			if (!this.placedMainChest) {
				this.placedMainChest = this.addChest(world, chunkBox, random, 8, -3, 3, LootTables.JUNGLE_TEMPLE_CHEST);
			}

			this.addBlock(world, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 9, -3, 2, chunkBox);
			this.addBlock(world, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 8, -3, 1, chunkBox);
			this.addBlock(world, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 4, -3, 5, chunkBox);
			this.addBlock(world, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 5, -2, 5, chunkBox);
			this.addBlock(world, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 5, -1, 5, chunkBox);
			this.addBlock(world, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 6, -3, 5, chunkBox);
			this.addBlock(world, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 7, -2, 5, chunkBox);
			this.addBlock(world, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 7, -1, 5, chunkBox);
			this.addBlock(world, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 8, -3, 5, chunkBox);
			this.fillWithOutline(world, chunkBox, 9, -1, 1, 9, -1, 5, false, random, COBBLESTONE_RANDOMIZER);
			this.fill(world, chunkBox, 8, -3, 8, 10, -1, 10);
			this.addBlock(world, Blocks.CHISELED_STONE_BRICKS.getDefaultState(), 8, -2, 11, chunkBox);
			this.addBlock(world, Blocks.CHISELED_STONE_BRICKS.getDefaultState(), 9, -2, 11, chunkBox);
			this.addBlock(world, Blocks.CHISELED_STONE_BRICKS.getDefaultState(), 10, -2, 11, chunkBox);
			BlockState blockState6 = Blocks.LEVER.getDefaultState().with(LeverBlock.FACING, Direction.NORTH).with(LeverBlock.FACE, WallMountLocation.WALL);
			this.addBlock(world, blockState6, 8, -2, 12, chunkBox);
			this.addBlock(world, blockState6, 9, -2, 12, chunkBox);
			this.addBlock(world, blockState6, 10, -2, 12, chunkBox);
			this.fillWithOutline(world, chunkBox, 8, -3, 8, 8, -3, 10, false, random, COBBLESTONE_RANDOMIZER);
			this.fillWithOutline(world, chunkBox, 10, -3, 8, 10, -3, 10, false, random, COBBLESTONE_RANDOMIZER);
			this.addBlock(world, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 10, -2, 9, chunkBox);
			this.addBlock(world, blockState5, 8, -2, 9, chunkBox);
			this.addBlock(world, blockState5, 8, -2, 10, chunkBox);
			this.addBlock(
				world,
				Blocks.REDSTONE_WIRE
					.getDefaultState()
					.with(RedstoneWireBlock.WIRE_CONNECTION_NORTH, WireConnection.SIDE)
					.with(RedstoneWireBlock.WIRE_CONNECTION_SOUTH, WireConnection.SIDE)
					.with(RedstoneWireBlock.WIRE_CONNECTION_EAST, WireConnection.SIDE)
					.with(RedstoneWireBlock.WIRE_CONNECTION_WEST, WireConnection.SIDE),
				10,
				-1,
				9,
				chunkBox
			);
			this.addBlock(world, Blocks.STICKY_PISTON.getDefaultState().with(PistonBlock.FACING, Direction.UP), 9, -2, 8, chunkBox);
			this.addBlock(world, Blocks.STICKY_PISTON.getDefaultState().with(PistonBlock.FACING, Direction.WEST), 10, -2, 8, chunkBox);
			this.addBlock(world, Blocks.STICKY_PISTON.getDefaultState().with(PistonBlock.FACING, Direction.WEST), 10, -1, 8, chunkBox);
			this.addBlock(world, Blocks.REPEATER.getDefaultState().with(RepeaterBlock.FACING, Direction.NORTH), 10, -2, 10, chunkBox);
			if (!this.placedHiddenChest) {
				this.placedHiddenChest = this.addChest(world, chunkBox, random, 9, -3, 10, LootTables.JUNGLE_TEMPLE_CHEST);
			}
		}
	}

	static class CobblestoneRandomizer extends StructurePiece.BlockRandomizer {
		@Override
		public void setBlock(Random random, int x, int y, int z, boolean placeBlock) {
			if (random.nextFloat() < 0.4F) {
				this.block = Blocks.COBBLESTONE.getDefaultState();
			} else {
				this.block = Blocks.MOSSY_COBBLESTONE.getDefaultState();
			}
		}
	}
}
