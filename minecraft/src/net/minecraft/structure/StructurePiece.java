package net.minecraft.structure;

import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.class_4538;
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
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;

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

	protected StructurePiece(StructurePieceType structurePieceType, int i) {
		this.type = structurePieceType;
		this.field_15316 = i;
	}

	public StructurePiece(StructurePieceType structurePieceType, CompoundTag compoundTag) {
		this(structurePieceType, compoundTag.getInt("GD"));
		if (compoundTag.containsKey("BB")) {
			this.boundingBox = new BlockBox(compoundTag.getIntArray("BB"));
		}

		int i = compoundTag.getInt("O");
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

	protected abstract void toNbt(CompoundTag compoundTag);

	public void method_14918(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
	}

	public abstract boolean generate(IWorld iWorld, ChunkGenerator<?> chunkGenerator, Random random, BlockBox blockBox, ChunkPos chunkPos);

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

	protected void addBlock(IWorld iWorld, BlockState blockState, int i, int j, int k, BlockBox blockBox) {
		BlockPos blockPos = new BlockPos(this.applyXTransform(i, k), this.applyYTransform(j), this.applyZTransform(i, k));
		if (blockBox.contains(blockPos)) {
			if (this.mirror != BlockMirror.NONE) {
				blockState = blockState.mirror(this.mirror);
			}

			if (this.rotation != BlockRotation.NONE) {
				blockState = blockState.rotate(this.rotation);
			}

			iWorld.setBlockState(blockPos, blockState, 2);
			FluidState fluidState = iWorld.getFluidState(blockPos);
			if (!fluidState.isEmpty()) {
				iWorld.getFluidTickScheduler().schedule(blockPos, fluidState.getFluid(), 0);
			}

			if (BLOCKS_NEEDING_POST_PROCESSING.contains(blockState.getBlock())) {
				iWorld.getChunk(blockPos).markBlockForPostProcessing(blockPos);
			}
		}
	}

	protected BlockState getBlockAt(BlockView blockView, int i, int j, int k, BlockBox blockBox) {
		int l = this.applyXTransform(i, k);
		int m = this.applyYTransform(j);
		int n = this.applyZTransform(i, k);
		BlockPos blockPos = new BlockPos(l, m, n);
		return !blockBox.contains(blockPos) ? Blocks.AIR.getDefaultState() : blockView.getBlockState(blockPos);
	}

	protected boolean isUnderSeaLevel(class_4538 arg, int i, int j, int k, BlockBox blockBox) {
		int l = this.applyXTransform(i, k);
		int m = this.applyYTransform(j + 1);
		int n = this.applyZTransform(i, k);
		BlockPos blockPos = new BlockPos(l, m, n);
		return !blockBox.contains(blockPos) ? false : m < arg.getTopY(Heightmap.Type.OCEAN_FLOOR_WG, l, n);
	}

	protected void fill(IWorld iWorld, BlockBox blockBox, int i, int j, int k, int l, int m, int n) {
		for (int o = j; o <= m; o++) {
			for (int p = i; p <= l; p++) {
				for (int q = k; q <= n; q++) {
					this.addBlock(iWorld, Blocks.AIR.getDefaultState(), p, o, q, blockBox);
				}
			}
		}
	}

	protected void fillWithOutline(
		IWorld iWorld, BlockBox blockBox, int i, int j, int k, int l, int m, int n, BlockState blockState, BlockState blockState2, boolean bl
	) {
		for (int o = j; o <= m; o++) {
			for (int p = i; p <= l; p++) {
				for (int q = k; q <= n; q++) {
					if (!bl || !this.getBlockAt(iWorld, p, o, q, blockBox).isAir()) {
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

	protected void fillWithOutline(
		IWorld iWorld, BlockBox blockBox, int i, int j, int k, int l, int m, int n, boolean bl, Random random, StructurePiece.BlockRandomizer blockRandomizer
	) {
		for (int o = j; o <= m; o++) {
			for (int p = i; p <= l; p++) {
				for (int q = k; q <= n; q++) {
					if (!bl || !this.getBlockAt(iWorld, p, o, q, blockBox).isAir()) {
						blockRandomizer.setBlock(random, p, o, q, o == j || o == m || p == i || p == l || q == k || q == n);
						this.addBlock(iWorld, blockRandomizer.getBlock(), p, o, q, blockBox);
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

	protected void addBlockWithRandomThreshold(IWorld iWorld, BlockBox blockBox, Random random, float f, int i, int j, int k, BlockState blockState) {
		if (random.nextFloat() < f) {
			this.addBlock(iWorld, blockState, i, j, k, blockBox);
		}
	}

	protected void method_14919(IWorld iWorld, BlockBox blockBox, int i, int j, int k, int l, int m, int n, BlockState blockState, boolean bl) {
		float f = (float)(l - i + 1);
		float g = (float)(m - j + 1);
		float h = (float)(n - k + 1);
		float o = (float)i + f / 2.0F;
		float p = (float)k + h / 2.0F;

		for (int q = j; q <= m; q++) {
			float r = (float)(q - j) / g;

			for (int s = i; s <= l; s++) {
				float t = ((float)s - o) / (f * 0.5F);

				for (int u = k; u <= n; u++) {
					float v = ((float)u - p) / (h * 0.5F);
					if (!bl || !this.getBlockAt(iWorld, s, q, u, blockBox).isAir()) {
						float w = t * t + r * r + v * v;
						if (w <= 1.05F) {
							this.addBlock(iWorld, blockState, s, q, u, blockBox);
						}
					}
				}
			}
		}
	}

	protected void method_14936(IWorld iWorld, BlockState blockState, int i, int j, int k, BlockBox blockBox) {
		int l = this.applyXTransform(i, k);
		int m = this.applyYTransform(j);
		int n = this.applyZTransform(i, k);
		if (blockBox.contains(new BlockPos(l, m, n))) {
			while ((iWorld.isAir(new BlockPos(l, m, n)) || iWorld.getBlockState(new BlockPos(l, m, n)).getMaterial().isLiquid()) && m > 1) {
				iWorld.setBlockState(new BlockPos(l, m, n), blockState, 2);
				m--;
			}
		}
	}

	protected boolean addChest(IWorld iWorld, BlockBox blockBox, Random random, int i, int j, int k, Identifier identifier) {
		BlockPos blockPos = new BlockPos(this.applyXTransform(i, k), this.applyYTransform(j), this.applyZTransform(i, k));
		return this.addChest(iWorld, blockBox, random, blockPos, identifier, null);
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

	protected boolean addChest(IWorld iWorld, BlockBox blockBox, Random random, BlockPos blockPos, Identifier identifier, @Nullable BlockState blockState) {
		if (blockBox.contains(blockPos) && iWorld.getBlockState(blockPos).getBlock() != Blocks.CHEST) {
			if (blockState == null) {
				blockState = method_14916(iWorld, blockPos, Blocks.CHEST.getDefaultState());
			}

			iWorld.setBlockState(blockPos, blockState, 2);
			BlockEntity blockEntity = iWorld.getBlockEntity(blockPos);
			if (blockEntity instanceof ChestBlockEntity) {
				((ChestBlockEntity)blockEntity).setLootTable(identifier, random.nextLong());
			}

			return true;
		} else {
			return false;
		}
	}

	protected boolean addDispenser(IWorld iWorld, BlockBox blockBox, Random random, int i, int j, int k, Direction direction, Identifier identifier) {
		BlockPos blockPos = new BlockPos(this.applyXTransform(i, k), this.applyYTransform(j), this.applyZTransform(i, k));
		if (blockBox.contains(blockPos) && iWorld.getBlockState(blockPos).getBlock() != Blocks.DISPENSER) {
			this.addBlock(iWorld, Blocks.DISPENSER.getDefaultState().with(DispenserBlock.FACING, direction), i, j, k, blockBox);
			BlockEntity blockEntity = iWorld.getBlockEntity(blockPos);
			if (blockEntity instanceof DispenserBlockEntity) {
				((DispenserBlockEntity)blockEntity).setLootTable(identifier, random.nextLong());
			}

			return true;
		} else {
			return false;
		}
	}

	public void translate(int i, int j, int k) {
		this.boundingBox.offset(i, j, k);
	}

	@Nullable
	public Direction getFacing() {
		return this.facing;
	}

	public void setOrientation(@Nullable Direction direction) {
		this.facing = direction;
		if (direction == null) {
			this.rotation = BlockRotation.NONE;
			this.mirror = BlockMirror.NONE;
		} else {
			switch (direction) {
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

		public abstract void setBlock(Random random, int i, int j, int k, boolean bl);

		public BlockState getBlock() {
			return this.block;
		}
	}
}
