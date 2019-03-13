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
import net.minecraft.util.Identifier;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.gen.Heightmap;

public abstract class StructurePiece {
	protected static final BlockState AIR = Blocks.field_10543.method_9564();
	protected MutableIntBoundingBox boundingBox;
	@Nullable
	private Direction field_15312;
	private Mirror mirror;
	private Rotation rotation;
	public int field_15316;
	private final StructurePieceType type;
	private static final Set<Block> BLOCKS_NEEDING_POST_PROCESSING = ImmutableSet.<Block>builder()
		.add(Blocks.field_10364)
		.add(Blocks.field_10336)
		.add(Blocks.field_10099)
		.add(Blocks.field_10620)
		.add(Blocks.field_10020)
		.add(Blocks.field_10132)
		.add(Blocks.field_10144)
		.add(Blocks.field_10299)
		.add(Blocks.field_10319)
		.add(Blocks.field_9983)
		.add(Blocks.field_10576)
		.build();

	protected StructurePiece(StructurePieceType structurePieceType, int i) {
		this.type = structurePieceType;
		this.field_15316 = i;
	}

	public StructurePiece(StructurePieceType structurePieceType, CompoundTag compoundTag) {
		this(structurePieceType, compoundTag.getInt("GD"));
		if (compoundTag.containsKey("BB")) {
			this.boundingBox = new MutableIntBoundingBox(compoundTag.getIntArray("BB"));
		}

		int i = compoundTag.getInt("O");
		this.method_14926(i == -1 ? null : Direction.fromHorizontal(i));
	}

	public final CompoundTag method_14946() {
		CompoundTag compoundTag = new CompoundTag();
		compoundTag.putString("id", Registry.STRUCTURE_PIECE.method_10221(this.getType()).toString());
		compoundTag.method_10566("BB", this.boundingBox.method_14658());
		Direction direction = this.method_14934();
		compoundTag.putInt("O", direction == null ? -1 : direction.getHorizontal());
		compoundTag.putInt("GD", this.field_15316);
		this.method_14943(compoundTag);
		return compoundTag;
	}

	protected abstract void method_14943(CompoundTag compoundTag);

	public void method_14918(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
	}

	public abstract boolean generate(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos);

	public MutableIntBoundingBox getBoundingBox() {
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

	public static StructurePiece method_14932(List<StructurePiece> list, MutableIntBoundingBox mutableIntBoundingBox) {
		for (StructurePiece structurePiece : list) {
			if (structurePiece.getBoundingBox() != null && structurePiece.getBoundingBox().intersects(mutableIntBoundingBox)) {
				return structurePiece;
			}
		}

		return null;
	}

	protected boolean method_14937(BlockView blockView, MutableIntBoundingBox mutableIntBoundingBox) {
		int i = Math.max(this.boundingBox.minX - 1, mutableIntBoundingBox.minX);
		int j = Math.max(this.boundingBox.minY - 1, mutableIntBoundingBox.minY);
		int k = Math.max(this.boundingBox.minZ - 1, mutableIntBoundingBox.minZ);
		int l = Math.min(this.boundingBox.maxX + 1, mutableIntBoundingBox.maxX);
		int m = Math.min(this.boundingBox.maxY + 1, mutableIntBoundingBox.maxY);
		int n = Math.min(this.boundingBox.maxZ + 1, mutableIntBoundingBox.maxZ);
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int o = i; o <= l; o++) {
			for (int p = k; p <= n; p++) {
				if (blockView.method_8320(mutable.set(o, j, p)).method_11620().isLiquid()) {
					return true;
				}

				if (blockView.method_8320(mutable.set(o, m, p)).method_11620().isLiquid()) {
					return true;
				}
			}
		}

		for (int o = i; o <= l; o++) {
			for (int p = j; p <= m; p++) {
				if (blockView.method_8320(mutable.set(o, p, k)).method_11620().isLiquid()) {
					return true;
				}

				if (blockView.method_8320(mutable.set(o, p, n)).method_11620().isLiquid()) {
					return true;
				}
			}
		}

		for (int o = k; o <= n; o++) {
			for (int p = j; p <= m; p++) {
				if (blockView.method_8320(mutable.set(i, p, o)).method_11620().isLiquid()) {
					return true;
				}

				if (blockView.method_8320(mutable.set(l, p, o)).method_11620().isLiquid()) {
					return true;
				}
			}
		}

		return false;
	}

	protected int applyXTransform(int i, int j) {
		Direction direction = this.method_14934();
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
		return this.method_14934() == null ? i : i + this.boundingBox.minY;
	}

	protected int applyZTransform(int i, int j) {
		Direction direction = this.method_14934();
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

	protected void addBlock(IWorld iWorld, BlockState blockState, int i, int j, int k, MutableIntBoundingBox mutableIntBoundingBox) {
		BlockPos blockPos = new BlockPos(this.applyXTransform(i, k), this.applyYTransform(j), this.applyZTransform(i, k));
		if (mutableIntBoundingBox.method_14662(blockPos)) {
			if (this.mirror != Mirror.NONE) {
				blockState = blockState.mirror(this.mirror);
			}

			if (this.rotation != Rotation.ROT_0) {
				blockState = blockState.rotate(this.rotation);
			}

			iWorld.method_8652(blockPos, blockState, 2);
			FluidState fluidState = iWorld.method_8316(blockPos);
			if (!fluidState.isEmpty()) {
				iWorld.method_8405().method_8676(blockPos, fluidState.getFluid(), 0);
			}

			if (BLOCKS_NEEDING_POST_PROCESSING.contains(blockState.getBlock())) {
				iWorld.method_16955(blockPos).method_12039(blockPos);
			}
		}
	}

	protected BlockState getBlockAt(BlockView blockView, int i, int j, int k, MutableIntBoundingBox mutableIntBoundingBox) {
		int l = this.applyXTransform(i, k);
		int m = this.applyYTransform(j);
		int n = this.applyZTransform(i, k);
		BlockPos blockPos = new BlockPos(l, m, n);
		return !mutableIntBoundingBox.method_14662(blockPos) ? Blocks.field_10124.method_9564() : blockView.method_8320(blockPos);
	}

	protected boolean isUnderSeaLevel(ViewableWorld viewableWorld, int i, int j, int k, MutableIntBoundingBox mutableIntBoundingBox) {
		int l = this.applyXTransform(i, k);
		int m = this.applyYTransform(j + 1);
		int n = this.applyZTransform(i, k);
		BlockPos blockPos = new BlockPos(l, m, n);
		return !mutableIntBoundingBox.method_14662(blockPos) ? false : m < viewableWorld.method_8589(Heightmap.Type.OCEAN_FLOOR_WG, l, n);
	}

	protected void fill(IWorld iWorld, MutableIntBoundingBox mutableIntBoundingBox, int i, int j, int k, int l, int m, int n) {
		for (int o = j; o <= m; o++) {
			for (int p = i; p <= l; p++) {
				for (int q = k; q <= n; q++) {
					this.addBlock(iWorld, Blocks.field_10124.method_9564(), p, o, q, mutableIntBoundingBox);
				}
			}
		}
	}

	protected void fillWithOutline(
		IWorld iWorld,
		MutableIntBoundingBox mutableIntBoundingBox,
		int i,
		int j,
		int k,
		int l,
		int m,
		int n,
		BlockState blockState,
		BlockState blockState2,
		boolean bl
	) {
		for (int o = j; o <= m; o++) {
			for (int p = i; p <= l; p++) {
				for (int q = k; q <= n; q++) {
					if (!bl || !this.getBlockAt(iWorld, p, o, q, mutableIntBoundingBox).isAir()) {
						if (o != j && o != m && p != i && p != l && q != k && q != n) {
							this.addBlock(iWorld, blockState2, p, o, q, mutableIntBoundingBox);
						} else {
							this.addBlock(iWorld, blockState, p, o, q, mutableIntBoundingBox);
						}
					}
				}
			}
		}
	}

	protected void fillWithOutline(
		IWorld iWorld,
		MutableIntBoundingBox mutableIntBoundingBox,
		int i,
		int j,
		int k,
		int l,
		int m,
		int n,
		boolean bl,
		Random random,
		StructurePiece.class_3444 arg
	) {
		for (int o = j; o <= m; o++) {
			for (int p = i; p <= l; p++) {
				for (int q = k; q <= n; q++) {
					if (!bl || !this.getBlockAt(iWorld, p, o, q, mutableIntBoundingBox).isAir()) {
						arg.method_14948(random, p, o, q, o == j || o == m || p == i || p == l || q == k || q == n);
						this.addBlock(iWorld, arg.getBlock(), p, o, q, mutableIntBoundingBox);
					}
				}
			}
		}
	}

	protected void fillWithOutlineUnderSealevel(
		IWorld iWorld,
		MutableIntBoundingBox mutableIntBoundingBox,
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
					if (!(random.nextFloat() > f)
						&& (!bl || !this.getBlockAt(iWorld, p, o, q, mutableIntBoundingBox).isAir())
						&& (!bl2 || this.isUnderSeaLevel(iWorld, p, o, q, mutableIntBoundingBox))) {
						if (o != j && o != m && p != i && p != l && q != k && q != n) {
							this.addBlock(iWorld, blockState2, p, o, q, mutableIntBoundingBox);
						} else {
							this.addBlock(iWorld, blockState, p, o, q, mutableIntBoundingBox);
						}
					}
				}
			}
		}
	}

	protected void addBlockWithRandomThreshold(
		IWorld iWorld, MutableIntBoundingBox mutableIntBoundingBox, Random random, float f, int i, int j, int k, BlockState blockState
	) {
		if (random.nextFloat() < f) {
			this.addBlock(iWorld, blockState, i, j, k, mutableIntBoundingBox);
		}
	}

	protected void method_14919(
		IWorld iWorld, MutableIntBoundingBox mutableIntBoundingBox, int i, int j, int k, int l, int m, int n, BlockState blockState, boolean bl
	) {
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
					if (!bl || !this.getBlockAt(iWorld, s, q, u, mutableIntBoundingBox).isAir()) {
						float w = t * t + r * r + v * v;
						if (w <= 1.05F) {
							this.addBlock(iWorld, blockState, s, q, u, mutableIntBoundingBox);
						}
					}
				}
			}
		}
	}

	protected void method_14936(IWorld iWorld, BlockState blockState, int i, int j, int k, MutableIntBoundingBox mutableIntBoundingBox) {
		int l = this.applyXTransform(i, k);
		int m = this.applyYTransform(j);
		int n = this.applyZTransform(i, k);
		if (mutableIntBoundingBox.method_14662(new BlockPos(l, m, n))) {
			while ((iWorld.method_8623(new BlockPos(l, m, n)) || iWorld.method_8320(new BlockPos(l, m, n)).method_11620().isLiquid()) && m > 1) {
				iWorld.method_8652(new BlockPos(l, m, n), blockState, 2);
				m--;
			}
		}
	}

	protected boolean method_14915(IWorld iWorld, MutableIntBoundingBox mutableIntBoundingBox, Random random, int i, int j, int k, Identifier identifier) {
		BlockPos blockPos = new BlockPos(this.applyXTransform(i, k), this.applyYTransform(j), this.applyZTransform(i, k));
		return this.method_14921(iWorld, mutableIntBoundingBox, random, blockPos, identifier, null);
	}

	public static BlockState method_14916(BlockView blockView, BlockPos blockPos, BlockState blockState) {
		Direction direction = null;

		for (Direction direction2 : Direction.Type.HORIZONTAL) {
			BlockPos blockPos2 = blockPos.method_10093(direction2);
			BlockState blockState2 = blockView.method_8320(blockPos2);
			if (blockState2.getBlock() == Blocks.field_10034) {
				return blockState;
			}

			if (blockState2.method_11598(blockView, blockPos2)) {
				if (direction != null) {
					direction = null;
					break;
				}

				direction = direction2;
			}
		}

		if (direction != null) {
			return blockState.method_11657(HorizontalFacingBlock.field_11177, direction.getOpposite());
		} else {
			Direction direction3 = blockState.method_11654(HorizontalFacingBlock.field_11177);
			BlockPos blockPos3 = blockPos.method_10093(direction3);
			if (blockView.method_8320(blockPos3).method_11598(blockView, blockPos3)) {
				direction3 = direction3.getOpposite();
				blockPos3 = blockPos.method_10093(direction3);
			}

			if (blockView.method_8320(blockPos3).method_11598(blockView, blockPos3)) {
				direction3 = direction3.rotateYClockwise();
				blockPos3 = blockPos.method_10093(direction3);
			}

			if (blockView.method_8320(blockPos3).method_11598(blockView, blockPos3)) {
				direction3 = direction3.getOpposite();
				blockPos3 = blockPos.method_10093(direction3);
			}

			return blockState.method_11657(HorizontalFacingBlock.field_11177, direction3);
		}
	}

	protected boolean method_14921(
		IWorld iWorld, MutableIntBoundingBox mutableIntBoundingBox, Random random, BlockPos blockPos, Identifier identifier, @Nullable BlockState blockState
	) {
		if (mutableIntBoundingBox.method_14662(blockPos) && iWorld.method_8320(blockPos).getBlock() != Blocks.field_10034) {
			if (blockState == null) {
				blockState = method_14916(iWorld, blockPos, Blocks.field_10034.method_9564());
			}

			iWorld.method_8652(blockPos, blockState, 2);
			BlockEntity blockEntity = iWorld.method_8321(blockPos);
			if (blockEntity instanceof ChestBlockEntity) {
				((ChestBlockEntity)blockEntity).method_11285(identifier, random.nextLong());
			}

			return true;
		} else {
			return false;
		}
	}

	protected boolean method_14930(
		IWorld iWorld, MutableIntBoundingBox mutableIntBoundingBox, Random random, int i, int j, int k, Direction direction, Identifier identifier
	) {
		BlockPos blockPos = new BlockPos(this.applyXTransform(i, k), this.applyYTransform(j), this.applyZTransform(i, k));
		if (mutableIntBoundingBox.method_14662(blockPos) && iWorld.method_8320(blockPos).getBlock() != Blocks.field_10200) {
			this.addBlock(iWorld, Blocks.field_10200.method_9564().method_11657(DispenserBlock.field_10918, direction), i, j, k, mutableIntBoundingBox);
			BlockEntity blockEntity = iWorld.method_8321(blockPos);
			if (blockEntity instanceof DispenserBlockEntity) {
				((DispenserBlockEntity)blockEntity).method_11285(identifier, random.nextLong());
			}

			return true;
		} else {
			return false;
		}
	}

	public void translate(int i, int j, int k) {
		this.boundingBox.translate(i, j, k);
	}

	@Nullable
	public Direction method_14934() {
		return this.field_15312;
	}

	public void method_14926(@Nullable Direction direction) {
		this.field_15312 = direction;
		if (direction == null) {
			this.rotation = Rotation.ROT_0;
			this.mirror = Mirror.NONE;
		} else {
			switch (direction) {
				case SOUTH:
					this.mirror = Mirror.LEFT_RIGHT;
					this.rotation = Rotation.ROT_0;
					break;
				case WEST:
					this.mirror = Mirror.LEFT_RIGHT;
					this.rotation = Rotation.ROT_90;
					break;
				case EAST:
					this.mirror = Mirror.NONE;
					this.rotation = Rotation.ROT_90;
					break;
				default:
					this.mirror = Mirror.NONE;
					this.rotation = Rotation.ROT_0;
			}
		}
	}

	public Rotation getRotation() {
		return this.rotation;
	}

	public StructurePieceType getType() {
		return this.type;
	}

	public abstract static class class_3444 {
		protected BlockState block = Blocks.field_10124.method_9564();

		protected class_3444() {
		}

		public abstract void method_14948(Random random, int i, int j, int k, boolean bl);

		public BlockState getBlock() {
			return this.block;
		}
	}
}
