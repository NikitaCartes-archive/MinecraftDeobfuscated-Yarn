package net.minecraft.structure;

import com.google.common.collect.ImmutableSet;
import java.util.List;
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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;
import net.minecraft.world.CollisionView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;

public abstract class StructurePiece {
	protected static final BlockState AIR = Blocks.CAVE_AIR.getDefaultState();
	protected BlockBox boundingBox;
	@Nullable
	private Direction facing;
	private BlockMirror mirror;
	private BlockRotation rotation;
	protected int field_15316;
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

	protected StructurePiece(StructurePieceType type, int i) {
		this.type = type;
		this.field_15316 = i;
	}

	public StructurePiece(StructurePieceType type, CompoundTag tag) {
		this(type, tag.getInt("GD"));
		if (tag.contains("BB")) {
			this.boundingBox = new BlockBox(tag.getIntArray("BB"));
		}

		int i = tag.getInt("O");
		this.setOrientation(i == -1 ? null : Direction.fromHorizontal(i));
	}

	public final CompoundTag getTag() {
		CompoundTag compoundTag = new CompoundTag();
		compoundTag.putString("id", Registry.STRUCTURE_PIECE.getId(this.getType()).toString());
		compoundTag.put("BB", this.boundingBox.toNbt());
		Direction direction = this.getFacing();
		compoundTag.putInt("O", direction == null ? -1 : direction.getHorizontal());
		compoundTag.putInt("GD", this.field_15316);
		this.toNbt(compoundTag);
		return compoundTag;
	}

	protected abstract void toNbt(CompoundTag tag);

	public void method_14918(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
	}

	public abstract boolean generate(IWorld world, Random random, BlockBox boundingBox, ChunkPos pos);

	public BlockBox getBoundingBox() {
		return this.boundingBox;
	}

	public int method_14923() {
		return this.field_15316;
	}

	public boolean method_16654(ChunkPos chunkPos, int i) {
		int j = chunkPos.x << 4;
		int k = chunkPos.z << 4;
		return this.boundingBox.intersectsXZ(j - i, k - i, j + 15 + i, k + 15 + i);
	}

	public static StructurePiece method_14932(List<StructurePiece> list, BlockBox blockBox) {
		for (StructurePiece structurePiece : list) {
			if (structurePiece.getBoundingBox() != null && structurePiece.getBoundingBox().intersects(blockBox)) {
				return structurePiece;
			}
		}

		return null;
	}

	protected boolean method_14937(BlockView blockView, BlockBox blockBox) {
		int i = Math.max(this.boundingBox.minX - 1, blockBox.minX);
		int j = Math.max(this.boundingBox.minY - 1, blockBox.minY);
		int k = Math.max(this.boundingBox.minZ - 1, blockBox.minZ);
		int l = Math.min(this.boundingBox.maxX + 1, blockBox.maxX);
		int m = Math.min(this.boundingBox.maxY + 1, blockBox.maxY);
		int n = Math.min(this.boundingBox.maxZ + 1, blockBox.maxZ);
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int o = i; o <= l; o++) {
			for (int p = k; p <= n; p++) {
				if (blockView.getBlockState(mutable.set(o, j, p)).getMaterial().isLiquid()) {
					return true;
				}

				if (blockView.getBlockState(mutable.set(o, m, p)).getMaterial().isLiquid()) {
					return true;
				}
			}
		}

		for (int o = i; o <= l; o++) {
			for (int p = j; p <= m; p++) {
				if (blockView.getBlockState(mutable.set(o, p, k)).getMaterial().isLiquid()) {
					return true;
				}

				if (blockView.getBlockState(mutable.set(o, p, n)).getMaterial().isLiquid()) {
					return true;
				}
			}
		}

		for (int o = k; o <= n; o++) {
			for (int p = j; p <= m; p++) {
				if (blockView.getBlockState(mutable.set(i, p, o)).getMaterial().isLiquid()) {
					return true;
				}

				if (blockView.getBlockState(mutable.set(l, p, o)).getMaterial().isLiquid()) {
					return true;
				}
			}
		}

		return false;
	}

	protected int applyXTransform(int i, int j) {
		Direction direction = this.getFacing();
		if (direction == null) {
			return i;
		} else {
			switch (direction) {
				case NORTH:
				case SOUTH:
					return this.boundingBox.minX + i;
				case WEST:
					return this.boundingBox.maxX - j;
				case EAST:
					return this.boundingBox.minX + j;
				default:
					return i;
			}
		}
	}

	protected int applyYTransform(int i) {
		return this.getFacing() == null ? i : i + this.boundingBox.minY;
	}

	protected int applyZTransform(int i, int j) {
		Direction direction = this.getFacing();
		if (direction == null) {
			return j;
		} else {
			switch (direction) {
				case NORTH:
					return this.boundingBox.maxZ - j;
				case SOUTH:
					return this.boundingBox.minZ + j;
				case WEST:
				case EAST:
					return this.boundingBox.minZ + i;
				default:
					return j;
			}
		}
	}

	protected void addBlock(IWorld world, BlockState block, int x, int y, int z, BlockBox blockBox) {
		BlockPos blockPos = new BlockPos(this.applyXTransform(x, z), this.applyYTransform(y), this.applyZTransform(x, z));
		if (blockBox.contains(blockPos)) {
			if (this.mirror != BlockMirror.NONE) {
				block = block.mirror(this.mirror);
			}

			if (this.rotation != BlockRotation.NONE) {
				block = block.rotate(this.rotation);
			}

			world.setBlockState(blockPos, block, 2);
			FluidState fluidState = world.getFluidState(blockPos);
			if (!fluidState.isEmpty()) {
				world.getFluidTickScheduler().schedule(blockPos, fluidState.getFluid(), 0);
			}

			if (BLOCKS_NEEDING_POST_PROCESSING.contains(block.getBlock())) {
				world.getChunk(blockPos).markBlockForPostProcessing(blockPos);
			}
		}
	}

	protected BlockState getBlockAt(BlockView blockView, int x, int y, int z, BlockBox blockBox) {
		int i = this.applyXTransform(x, z);
		int j = this.applyYTransform(y);
		int k = this.applyZTransform(x, z);
		BlockPos blockPos = new BlockPos(i, j, k);
		return !blockBox.contains(blockPos) ? Blocks.AIR.getDefaultState() : blockView.getBlockState(blockPos);
	}

	protected boolean isUnderSeaLevel(CollisionView collisionView, int x, int z, int y, BlockBox blockBox) {
		int i = this.applyXTransform(x, y);
		int j = this.applyYTransform(z + 1);
		int k = this.applyZTransform(x, y);
		BlockPos blockPos = new BlockPos(i, j, k);
		return !blockBox.contains(blockPos) ? false : j < collisionView.getTop(Heightmap.Type.OCEAN_FLOOR_WG, i, k);
	}

	protected void fill(IWorld world, BlockBox bounds, int minX, int minY, int minZ, int maxX, int maxY, int i) {
		for (int j = minY; j <= maxY; j++) {
			for (int k = minX; k <= maxX; k++) {
				for (int l = minZ; l <= i; l++) {
					this.addBlock(world, Blocks.AIR.getDefaultState(), k, j, l, bounds);
				}
			}
		}
	}

	protected void fillWithOutline(IWorld world, BlockBox blockBox, int i, int j, int k, int l, int m, int n, BlockState blockState, BlockState inside, boolean bl) {
		for (int o = j; o <= m; o++) {
			for (int p = i; p <= l; p++) {
				for (int q = k; q <= n; q++) {
					if (!bl || !this.getBlockAt(world, p, o, q, blockBox).isAir()) {
						if (o != j && o != m && p != i && p != l && q != k && q != n) {
							this.addBlock(world, inside, p, o, q, blockBox);
						} else {
							this.addBlock(world, blockState, p, o, q, blockBox);
						}
					}
				}
			}
		}
	}

	protected void fillWithOutline(
		IWorld iWorld,
		BlockBox blockBox,
		int minX,
		int minY,
		int minZ,
		int maxX,
		int maxY,
		int maxZ,
		boolean replaceBlocks,
		Random random,
		StructurePiece.BlockRandomizer blockRandomizer
	) {
		for (int i = minY; i <= maxY; i++) {
			for (int j = minX; j <= maxX; j++) {
				for (int k = minZ; k <= maxZ; k++) {
					if (!replaceBlocks || !this.getBlockAt(iWorld, j, i, k, blockBox).isAir()) {
						blockRandomizer.setBlock(random, j, i, k, i == minY || i == maxY || j == minX || j == maxX || k == minZ || k == maxZ);
						this.addBlock(iWorld, blockRandomizer.getBlock(), j, i, k, blockBox);
					}
				}
			}
		}
	}

	protected void fillWithOutlineUnderSealevel(
		IWorld iWorld,
		BlockBox blockBox,
		Random random,
		float f,
		int i,
		int j,
		int k,
		int l,
		int m,
		int n,
		BlockState blockState,
		BlockState blockState2,
		boolean bl,
		boolean bl2
	) {
		for (int o = j; o <= m; o++) {
			for (int p = i; p <= l; p++) {
				for (int q = k; q <= n; q++) {
					if (!(random.nextFloat() > f) && (!bl || !this.getBlockAt(iWorld, p, o, q, blockBox).isAir()) && (!bl2 || this.isUnderSeaLevel(iWorld, p, o, q, blockBox))
						)
					 {
						if (o != j && o != m && p != i && p != l && q != k && q != n) {
							this.addBlock(iWorld, blockState2, p, o, q, blockBox);
						} else {
							this.addBlock(iWorld, blockState, p, o, q, blockBox);
						}
					}
				}
			}
		}
	}

	protected void addBlockWithRandomThreshold(IWorld world, BlockBox bounds, Random random, float threshold, int x, int y, int z, BlockState blockState) {
		if (random.nextFloat() < threshold) {
			this.addBlock(world, blockState, x, y, z, bounds);
		}
	}

	protected void method_14919(IWorld world, BlockBox bounds, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, BlockState block, boolean bl) {
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
					if (!bl || !this.getBlockAt(world, m, k, o, bounds).isAir()) {
						float q = n * n + l * l + p * p;
						if (q <= 1.05F) {
							this.addBlock(world, block, m, k, o, bounds);
						}
					}
				}
			}
		}
	}

	protected void method_14936(IWorld world, BlockState blockState, int x, int y, int z, BlockBox blockBox) {
		int i = this.applyXTransform(x, z);
		int j = this.applyYTransform(y);
		int k = this.applyZTransform(x, z);
		if (blockBox.contains(new BlockPos(i, j, k))) {
			while ((world.isAir(new BlockPos(i, j, k)) || world.getBlockState(new BlockPos(i, j, k)).getMaterial().isLiquid()) && j > 1) {
				world.setBlockState(new BlockPos(i, j, k), blockState, 2);
				j--;
			}
		}
	}

	protected boolean addChest(IWorld world, BlockBox boundingBox, Random random, int x, int y, int z, Identifier lootTableId) {
		BlockPos blockPos = new BlockPos(this.applyXTransform(x, z), this.applyYTransform(y), this.applyZTransform(x, z));
		return this.addChest(world, boundingBox, random, blockPos, lootTableId, null);
	}

	public static BlockState method_14916(BlockView blockView, BlockPos blockPos, BlockState blockState) {
		Direction direction = null;

		for (Direction direction2 : Direction.Type.HORIZONTAL) {
			BlockPos blockPos2 = blockPos.offset(direction2);
			BlockState blockState2 = blockView.getBlockState(blockPos2);
			if (blockState2.getBlock() == Blocks.CHEST) {
				return blockState;
			}

			if (blockState2.isFullOpaque(blockView, blockPos2)) {
				if (direction != null) {
					direction = null;
					break;
				}

				direction = direction2;
			}
		}

		if (direction != null) {
			return blockState.with(HorizontalFacingBlock.FACING, direction.getOpposite());
		} else {
			Direction direction3 = blockState.get(HorizontalFacingBlock.FACING);
			BlockPos blockPos3 = blockPos.offset(direction3);
			if (blockView.getBlockState(blockPos3).isFullOpaque(blockView, blockPos3)) {
				direction3 = direction3.getOpposite();
				blockPos3 = blockPos.offset(direction3);
			}

			if (blockView.getBlockState(blockPos3).isFullOpaque(blockView, blockPos3)) {
				direction3 = direction3.rotateYClockwise();
				blockPos3 = blockPos.offset(direction3);
			}

			if (blockView.getBlockState(blockPos3).isFullOpaque(blockView, blockPos3)) {
				direction3 = direction3.getOpposite();
				blockPos3 = blockPos.offset(direction3);
			}

			return blockState.with(HorizontalFacingBlock.FACING, direction3);
		}
	}

	protected boolean addChest(IWorld world, BlockBox boundingBox, Random random, BlockPos pos, Identifier lootTableId, @Nullable BlockState block) {
		if (boundingBox.contains(pos) && world.getBlockState(pos).getBlock() != Blocks.CHEST) {
			if (block == null) {
				block = method_14916(world, pos, Blocks.CHEST.getDefaultState());
			}

			world.setBlockState(pos, block, 2);
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof ChestBlockEntity) {
				((ChestBlockEntity)blockEntity).setLootTable(lootTableId, random.nextLong());
			}

			return true;
		} else {
			return false;
		}
	}

	protected boolean addDispenser(IWorld world, BlockBox boundingBox, Random random, int x, int y, int z, Direction facing, Identifier lootTbaleId) {
		BlockPos blockPos = new BlockPos(this.applyXTransform(x, z), this.applyYTransform(y), this.applyZTransform(x, z));
		if (boundingBox.contains(blockPos) && world.getBlockState(blockPos).getBlock() != Blocks.DISPENSER) {
			this.addBlock(world, Blocks.DISPENSER.getDefaultState().with(DispenserBlock.FACING, facing), x, y, z, boundingBox);
			BlockEntity blockEntity = world.getBlockEntity(blockPos);
			if (blockEntity instanceof DispenserBlockEntity) {
				((DispenserBlockEntity)blockEntity).setLootTable(lootTbaleId, random.nextLong());
			}

			return true;
		} else {
			return false;
		}
	}

	public void translate(int x, int y, int z) {
		this.boundingBox.offset(x, y, z);
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

	public StructurePieceType getType() {
		return this.type;
	}

	public abstract static class BlockRandomizer {
		protected BlockState block = Blocks.AIR.getDefaultState();

		protected BlockRandomizer() {
		}

		public abstract void setBlock(Random random, int x, int y, int z, boolean placeBlock);

		public BlockState getBlock() {
			return this.block;
		}
	}
}
