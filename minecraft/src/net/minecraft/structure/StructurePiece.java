package net.minecraft.structure;

import com.google.common.collect.ImmutableSet;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.StructureWeightType;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class StructurePiece {
	private static final Logger field_29327 = LogManager.getLogger();
	protected static final BlockState AIR = Blocks.CAVE_AIR.getDefaultState();
	protected BlockBox boundingBox;
	@Nullable
	private Direction facing;
	private BlockMirror mirror;
	private BlockRotation rotation;
	protected int chainLength;
	private final StructurePieceType type;
	private static final Set<Block> BLOCKS_NEEDING_POST_PROCESSING = ImmutableSet.<Block>builder()
		.add(Blocks.NETHER_BRICK_FENCE)
		.add(Blocks.TORCH)
		.add(Blocks.WALL_TORCH)
		.add(Blocks.OAK_FENCE)
		.add(Blocks.SPRUCE_FENCE)
		.add(Blocks.DARK_OAK_FENCE)
		.add(Blocks.ACACIA_FENCE)
		.add(Blocks.BIRCH_FENCE)
		.add(Blocks.JUNGLE_FENCE)
		.add(Blocks.LADDER)
		.add(Blocks.IRON_BARS)
		.build();

	protected StructurePiece(StructurePieceType type, int length, BlockBox boundingBox) {
		this.type = type;
		this.chainLength = length;
		this.boundingBox = boundingBox;
	}

	public StructurePiece(StructurePieceType type, NbtCompound nbt) {
		this(
			type,
			nbt.getInt("GD"),
			(BlockBox)BlockBox.CODEC
				.parse(NbtOps.INSTANCE, nbt.get("BB"))
				.resultOrPartial(field_29327::error)
				.orElseThrow(() -> new IllegalArgumentException("Invalid boundingbox"))
		);
		int i = nbt.getInt("O");
		this.setOrientation(i == -1 ? null : Direction.fromHorizontal(i));
	}

	protected static BlockBox createBox(int x, int y, int z, Direction orientation, int width, int height, int depth) {
		return orientation.getAxis() == Direction.Axis.Z
			? new BlockBox(x, y, z, x + width - 1, y + height - 1, z + depth - 1)
			: new BlockBox(x, y, z, x + depth - 1, y + height - 1, z + width - 1);
	}

	protected static Direction getRandomHorizontalDirection(Random random) {
		return Direction.Type.HORIZONTAL.random(random);
	}

	public final NbtCompound toNbt(ServerWorld world) {
		NbtCompound nbtCompound = new NbtCompound();
		nbtCompound.putString("id", Registry.STRUCTURE_PIECE.getId(this.getType()).toString());
		BlockBox.CODEC.encodeStart(NbtOps.INSTANCE, this.boundingBox).resultOrPartial(field_29327::error).ifPresent(nbtElement -> nbtCompound.put("BB", nbtElement));
		Direction direction = this.getFacing();
		nbtCompound.putInt("O", direction == null ? -1 : direction.getHorizontal());
		nbtCompound.putInt("GD", this.chainLength);
		this.writeNbt(world, nbtCompound);
		return nbtCompound;
	}

	protected abstract void writeNbt(ServerWorld world, NbtCompound nbt);

	public StructureWeightType method_33882() {
		return StructureWeightType.BEARD;
	}

	public void fillOpenings(StructurePiece start, StructurePiecesHolder structurePiecesHolder, Random random) {
	}

	public abstract boolean generate(
		StructureWorldAccess world,
		StructureAccessor structureAccessor,
		ChunkGenerator chunkGenerator,
		Random random,
		BlockBox boundingBox,
		ChunkPos chunkPos,
		BlockPos pos
	);

	public BlockBox getBoundingBox() {
		return this.boundingBox;
	}

	public int getChainLength() {
		return this.chainLength;
	}

	public boolean intersectsChunk(ChunkPos pos, int offset) {
		int i = pos.getStartX();
		int j = pos.getStartZ();
		return this.boundingBox.intersectsXZ(i - offset, j - offset, i + 15 + offset, j + 15 + offset);
	}

	public BlockPos getCenter() {
		return new BlockPos(this.boundingBox.getCenter());
	}

	protected BlockPos.Mutable offsetPos(int x, int y, int z) {
		return new BlockPos.Mutable(this.applyXTransform(x, z), this.applyYTransform(y), this.applyZTransform(x, z));
	}

	protected int applyXTransform(int x, int z) {
		Direction direction = this.getFacing();
		if (direction == null) {
			return x;
		} else {
			switch (direction) {
				case NORTH:
				case SOUTH:
					return this.boundingBox.getMinX() + x;
				case WEST:
					return this.boundingBox.getMaxX() - z;
				case EAST:
					return this.boundingBox.getMinX() + z;
				default:
					return x;
			}
		}
	}

	protected int applyYTransform(int y) {
		return this.getFacing() == null ? y : y + this.boundingBox.getMinY();
	}

	protected int applyZTransform(int x, int z) {
		Direction direction = this.getFacing();
		if (direction == null) {
			return z;
		} else {
			switch (direction) {
				case NORTH:
					return this.boundingBox.getMaxZ() - z;
				case SOUTH:
					return this.boundingBox.getMinZ() + z;
				case WEST:
				case EAST:
					return this.boundingBox.getMinZ() + x;
				default:
					return z;
			}
		}
	}

	protected void addBlock(StructureWorldAccess world, BlockState block, int x, int y, int z, BlockBox box) {
		BlockPos blockPos = this.offsetPos(x, y, z);
		if (box.contains(blockPos)) {
			if (this.canAddBlock(world, x, y, z, box)) {
				if (this.mirror != BlockMirror.NONE) {
					block = block.mirror(this.mirror);
				}

				if (this.rotation != BlockRotation.NONE) {
					block = block.rotate(this.rotation);
				}

				world.setBlockState(blockPos, block, Block.NOTIFY_LISTENERS);
				FluidState fluidState = world.getFluidState(blockPos);
				if (!fluidState.isEmpty()) {
					world.getFluidTickScheduler().schedule(blockPos, fluidState.getFluid(), 0);
				}

				if (BLOCKS_NEEDING_POST_PROCESSING.contains(block.getBlock())) {
					world.getChunk(blockPos).markBlockForPostProcessing(blockPos);
				}
			}
		}
	}

	protected boolean canAddBlock(WorldView world, int x, int y, int z, BlockBox box) {
		return true;
	}

	protected BlockState getBlockAt(BlockView world, int x, int y, int z, BlockBox box) {
		BlockPos blockPos = this.offsetPos(x, y, z);
		return !box.contains(blockPos) ? Blocks.AIR.getDefaultState() : world.getBlockState(blockPos);
	}

	protected boolean isUnderSeaLevel(WorldView world, int x, int z, int y, BlockBox box) {
		BlockPos blockPos = this.offsetPos(x, z + 1, y);
		return !box.contains(blockPos) ? false : blockPos.getY() < world.getTopY(Heightmap.Type.OCEAN_FLOOR_WG, blockPos.getX(), blockPos.getZ());
	}

	protected void fill(StructureWorldAccess world, BlockBox bounds, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
		for (int i = minY; i <= maxY; i++) {
			for (int j = minX; j <= maxX; j++) {
				for (int k = minZ; k <= maxZ; k++) {
					this.addBlock(world, Blocks.AIR.getDefaultState(), j, i, k, bounds);
				}
			}
		}
	}

	protected void fillWithOutline(
		StructureWorldAccess world,
		BlockBox box,
		int minX,
		int minY,
		int minZ,
		int maxX,
		int maxY,
		int maxZ,
		BlockState outline,
		BlockState inside,
		boolean cantReplaceAir
	) {
		for (int i = minY; i <= maxY; i++) {
			for (int j = minX; j <= maxX; j++) {
				for (int k = minZ; k <= maxZ; k++) {
					if (!cantReplaceAir || !this.getBlockAt(world, j, i, k, box).isAir()) {
						if (i != minY && i != maxY && j != minX && j != maxX && k != minZ && k != maxZ) {
							this.addBlock(world, inside, j, i, k, box);
						} else {
							this.addBlock(world, outline, j, i, k, box);
						}
					}
				}
			}
		}
	}

	protected void fillWithOutline(StructureWorldAccess world, BlockBox box, BlockBox fillBox, BlockState outline, BlockState inside, boolean cantReplaceAir) {
		this.fillWithOutline(
			world,
			box,
			fillBox.getMinX(),
			fillBox.getMinY(),
			fillBox.getMinZ(),
			fillBox.getMaxX(),
			fillBox.getMaxY(),
			fillBox.getMaxZ(),
			outline,
			inside,
			cantReplaceAir
		);
	}

	protected void fillWithOutline(
		StructureWorldAccess world,
		BlockBox box,
		int minX,
		int minY,
		int minZ,
		int maxX,
		int maxY,
		int maxZ,
		boolean cantReplaceAir,
		Random random,
		StructurePiece.BlockRandomizer randomizer
	) {
		for (int i = minY; i <= maxY; i++) {
			for (int j = minX; j <= maxX; j++) {
				for (int k = minZ; k <= maxZ; k++) {
					if (!cantReplaceAir || !this.getBlockAt(world, j, i, k, box).isAir()) {
						randomizer.setBlock(random, j, i, k, i == minY || i == maxY || j == minX || j == maxX || k == minZ || k == maxZ);
						this.addBlock(world, randomizer.getBlock(), j, i, k, box);
					}
				}
			}
		}
	}

	protected void fillWithOutline(
		StructureWorldAccess world, BlockBox box, BlockBox fillBox, boolean cantReplaceAir, Random random, StructurePiece.BlockRandomizer randomizer
	) {
		this.fillWithOutline(
			world,
			box,
			fillBox.getMinX(),
			fillBox.getMinY(),
			fillBox.getMinZ(),
			fillBox.getMaxX(),
			fillBox.getMaxY(),
			fillBox.getMaxZ(),
			cantReplaceAir,
			random,
			randomizer
		);
	}

	protected void fillWithOutlineUnderSeaLevel(
		StructureWorldAccess world,
		BlockBox box,
		Random random,
		float blockChance,
		int minX,
		int minY,
		int minZ,
		int maxX,
		int maxY,
		int maxZ,
		BlockState outline,
		BlockState inside,
		boolean cantReplaceAir,
		boolean stayBelowSeaLevel
	) {
		for (int i = minY; i <= maxY; i++) {
			for (int j = minX; j <= maxX; j++) {
				for (int k = minZ; k <= maxZ; k++) {
					if (!(random.nextFloat() > blockChance)
						&& (!cantReplaceAir || !this.getBlockAt(world, j, i, k, box).isAir())
						&& (!stayBelowSeaLevel || this.isUnderSeaLevel(world, j, i, k, box))) {
						if (i != minY && i != maxY && j != minX && j != maxX && k != minZ && k != maxZ) {
							this.addBlock(world, inside, j, i, k, box);
						} else {
							this.addBlock(world, outline, j, i, k, box);
						}
					}
				}
			}
		}
	}

	protected void addBlockWithRandomThreshold(StructureWorldAccess world, BlockBox bounds, Random random, float threshold, int x, int y, int z, BlockState state) {
		if (random.nextFloat() < threshold) {
			this.addBlock(world, state, x, y, z, bounds);
		}
	}

	protected void fillHalfEllipsoid(
		StructureWorldAccess world, BlockBox bounds, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, BlockState block, boolean cantReplaceAir
	) {
		float f = (float)(maxX - minX + 1);
		float g = (float)(maxY - minY + 1);
		float h = (float)(maxZ - minZ + 1);
		float i = (float)minX + f / 2.0F;
		float j = (float)minZ + h / 2.0F;

		for (int k = minY; k <= maxY; k++) {
			float l = (float)(k - minY) / g;

			for (int m = minX; m <= maxX; m++) {
				float n = ((float)m - i) / (f * 0.5F);

				for (int o = minZ; o <= maxZ; o++) {
					float p = ((float)o - j) / (h * 0.5F);
					if (!cantReplaceAir || !this.getBlockAt(world, m, k, o, bounds).isAir()) {
						float q = n * n + l * l + p * p;
						if (q <= 1.05F) {
							this.addBlock(world, block, m, k, o, bounds);
						}
					}
				}
			}
		}
	}

	protected void fillDownwards(StructureWorldAccess world, BlockState state, int x, int y, int z, BlockBox box) {
		BlockPos.Mutable mutable = this.offsetPos(x, y, z);
		if (box.contains(mutable)) {
			while (this.canReplace(world.getBlockState(mutable)) && mutable.getY() > world.getBottomY() + 1) {
				world.setBlockState(mutable, state, Block.NOTIFY_LISTENERS);
				mutable.move(Direction.DOWN);
			}
		}
	}

	protected boolean canReplace(BlockState state) {
		return state.isAir() || state.getMaterial().isLiquid() || state.isOf(Blocks.GLOW_LICHEN) || state.isOf(Blocks.SEAGRASS) || state.isOf(Blocks.TALL_SEAGRASS);
	}

	protected boolean addChest(StructureWorldAccess world, BlockBox boundingBox, Random random, int x, int y, int z, Identifier lootTableId) {
		return this.addChest(world, boundingBox, random, this.offsetPos(x, y, z), lootTableId, null);
	}

	public static BlockState orientateChest(BlockView world, BlockPos pos, BlockState state) {
		Direction direction = null;

		for (Direction direction2 : Direction.Type.HORIZONTAL) {
			BlockPos blockPos = pos.offset(direction2);
			BlockState blockState = world.getBlockState(blockPos);
			if (blockState.isOf(Blocks.CHEST)) {
				return state;
			}

			if (blockState.isOpaqueFullCube(world, blockPos)) {
				if (direction != null) {
					direction = null;
					break;
				}

				direction = direction2;
			}
		}

		if (direction != null) {
			return state.with(HorizontalFacingBlock.FACING, direction.getOpposite());
		} else {
			Direction direction3 = state.get(HorizontalFacingBlock.FACING);
			BlockPos blockPos2 = pos.offset(direction3);
			if (world.getBlockState(blockPos2).isOpaqueFullCube(world, blockPos2)) {
				direction3 = direction3.getOpposite();
				blockPos2 = pos.offset(direction3);
			}

			if (world.getBlockState(blockPos2).isOpaqueFullCube(world, blockPos2)) {
				direction3 = direction3.rotateYClockwise();
				blockPos2 = pos.offset(direction3);
			}

			if (world.getBlockState(blockPos2).isOpaqueFullCube(world, blockPos2)) {
				direction3 = direction3.getOpposite();
				blockPos2 = pos.offset(direction3);
			}

			return state.with(HorizontalFacingBlock.FACING, direction3);
		}
	}

	protected boolean addChest(ServerWorldAccess world, BlockBox boundingBox, Random random, BlockPos pos, Identifier lootTableId, @Nullable BlockState block) {
		if (boundingBox.contains(pos) && !world.getBlockState(pos).isOf(Blocks.CHEST)) {
			if (block == null) {
				block = orientateChest(world, pos, Blocks.CHEST.getDefaultState());
			}

			world.setBlockState(pos, block, Block.NOTIFY_LISTENERS);
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof ChestBlockEntity) {
				((ChestBlockEntity)blockEntity).setLootTable(lootTableId, random.nextLong());
			}

			return true;
		} else {
			return false;
		}
	}

	protected boolean addDispenser(StructureWorldAccess world, BlockBox boundingBox, Random random, int x, int y, int z, Direction facing, Identifier lootTableId) {
		BlockPos blockPos = this.offsetPos(x, y, z);
		if (boundingBox.contains(blockPos) && !world.getBlockState(blockPos).isOf(Blocks.DISPENSER)) {
			this.addBlock(world, Blocks.DISPENSER.getDefaultState().with(DispenserBlock.FACING, facing), x, y, z, boundingBox);
			BlockEntity blockEntity = world.getBlockEntity(blockPos);
			if (blockEntity instanceof DispenserBlockEntity) {
				((DispenserBlockEntity)blockEntity).setLootTable(lootTableId, random.nextLong());
			}

			return true;
		} else {
			return false;
		}
	}

	public void translate(int x, int y, int z) {
		this.boundingBox.move(x, y, z);
	}

	@Nullable
	public Direction getFacing() {
		return this.facing;
	}

	public void setOrientation(@Nullable Direction orientation) {
		this.facing = orientation;
		if (orientation == null) {
			this.rotation = BlockRotation.NONE;
			this.mirror = BlockMirror.NONE;
		} else {
			switch (orientation) {
				case SOUTH:
					this.mirror = BlockMirror.LEFT_RIGHT;
					this.rotation = BlockRotation.NONE;
					break;
				case WEST:
					this.mirror = BlockMirror.LEFT_RIGHT;
					this.rotation = BlockRotation.CLOCKWISE_90;
					break;
				case EAST:
					this.mirror = BlockMirror.NONE;
					this.rotation = BlockRotation.CLOCKWISE_90;
					break;
				default:
					this.mirror = BlockMirror.NONE;
					this.rotation = BlockRotation.NONE;
			}
		}
	}

	public BlockRotation getRotation() {
		return this.rotation;
	}

	public BlockMirror getMirror() {
		return this.mirror;
	}

	public StructurePieceType getType() {
		return this.type;
	}

	protected abstract static class BlockRandomizer {
		protected BlockState block = Blocks.AIR.getDefaultState();

		public abstract void setBlock(Random random, int x, int y, int z, boolean placeBlock);

		public BlockState getBlock() {
			return this.block;
		}
	}
}
