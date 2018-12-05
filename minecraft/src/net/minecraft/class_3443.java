package net.minecraft;

import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sortme.StructurePiece;
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

public abstract class class_3443 {
	protected static final BlockState blockAir = Blocks.field_10543.getDefaultState();
	protected MutableIntBoundingBox structureBounds;
	@Nullable
	private Direction field_15312;
	private Mirror mirror;
	private Rotation rotation;
	public int field_15316;
	private final StructurePiece field_16712;
	private static final Set<Block> field_15311 = ImmutableSet.<Block>builder()
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

	protected class_3443(StructurePiece structurePiece, int i) {
		this.field_16712 = structurePiece;
		this.field_15316 = i;
	}

	public class_3443(StructurePiece structurePiece, CompoundTag compoundTag) {
		this(structurePiece, compoundTag.getInt("GD"));
		if (compoundTag.containsKey("BB")) {
			this.structureBounds = new MutableIntBoundingBox(compoundTag.getIntArray("BB"));
		}

		int i = compoundTag.getInt("O");
		this.method_14926(i == -1 ? null : Direction.fromHorizontal(i));
	}

	public final CompoundTag getTag() {
		CompoundTag compoundTag = new CompoundTag();
		compoundTag.putString("id", Registry.STRUCTURE_PIECE.getId(this.method_16653()).toString());
		compoundTag.put("BB", this.structureBounds.toNbt());
		Direction direction = this.method_14934();
		compoundTag.putInt("O", direction == null ? -1 : direction.getHorizontal());
		compoundTag.putInt("GD", this.field_15316);
		this.toNbt(compoundTag);
		return compoundTag;
	}

	protected abstract void toNbt(CompoundTag compoundTag);

	public void method_14918(class_3443 arg, List<class_3443> list, Random random) {
	}

	public abstract boolean method_14931(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos);

	public MutableIntBoundingBox method_14935() {
		return this.structureBounds;
	}

	public int method_14923() {
		return this.field_15316;
	}

	public boolean method_16654(ChunkPos chunkPos, int i) {
		int j = chunkPos.x << 4;
		int k = chunkPos.z << 4;
		return this.structureBounds.intersectsXZ(j - i, k - i, j + 15 + i, k + 15 + i);
	}

	public static class_3443 method_14932(List<class_3443> list, MutableIntBoundingBox mutableIntBoundingBox) {
		for (class_3443 lv : list) {
			if (lv.method_14935() != null && lv.method_14935().intersects(mutableIntBoundingBox)) {
				return lv;
			}
		}

		return null;
	}

	protected boolean method_14937(BlockView blockView, MutableIntBoundingBox mutableIntBoundingBox) {
		int i = Math.max(this.structureBounds.minX - 1, mutableIntBoundingBox.minX);
		int j = Math.max(this.structureBounds.minY - 1, mutableIntBoundingBox.minY);
		int k = Math.max(this.structureBounds.minZ - 1, mutableIntBoundingBox.minZ);
		int l = Math.min(this.structureBounds.maxX + 1, mutableIntBoundingBox.maxX);
		int m = Math.min(this.structureBounds.maxY + 1, mutableIntBoundingBox.maxY);
		int n = Math.min(this.structureBounds.maxZ + 1, mutableIntBoundingBox.maxZ);
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int o = i; o <= l; o++) {
			for (int p = k; p <= n; p++) {
				if (blockView.getBlockState(mutable.set(o, j, p)).getMaterial().method_15797()) {
					return true;
				}

				if (blockView.getBlockState(mutable.set(o, m, p)).getMaterial().method_15797()) {
					return true;
				}
			}
		}

		for (int o = i; o <= l; o++) {
			for (int p = j; p <= m; p++) {
				if (blockView.getBlockState(mutable.set(o, p, k)).getMaterial().method_15797()) {
					return true;
				}

				if (blockView.getBlockState(mutable.set(o, p, n)).getMaterial().method_15797()) {
					return true;
				}
			}
		}

		for (int o = k; o <= n; o++) {
			for (int p = j; p <= m; p++) {
				if (blockView.getBlockState(mutable.set(i, p, o)).getMaterial().method_15797()) {
					return true;
				}

				if (blockView.getBlockState(mutable.set(l, p, o)).getMaterial().method_15797()) {
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
					return this.structureBounds.minX + i;
				case WEST:
					return this.structureBounds.maxX - j;
				case EAST:
					return this.structureBounds.minX + j;
				default:
					return i;
			}
		}
	}

	protected int applyYTransform(int i) {
		return this.method_14934() == null ? i : i + this.structureBounds.minY;
	}

	protected int applyZTransform(int i, int j) {
		Direction direction = this.method_14934();
		if (direction == null) {
			return j;
		} else {
			switch (direction) {
				case NORTH:
					return this.structureBounds.maxZ - j;
				case SOUTH:
					return this.structureBounds.minZ + j;
				case WEST:
				case EAST:
					return this.structureBounds.minZ + i;
				default:
					return j;
			}
		}
	}

	protected void addBlock(IWorld iWorld, BlockState blockState, int i, int j, int k, MutableIntBoundingBox mutableIntBoundingBox) {
		BlockPos blockPos = new BlockPos(this.applyXTransform(i, k), this.applyYTransform(j), this.applyZTransform(i, k));
		if (mutableIntBoundingBox.contains(blockPos)) {
			if (this.mirror != Mirror.NONE) {
				blockState = blockState.applyMirror(this.mirror);
			}

			if (this.rotation != Rotation.ROT_0) {
				blockState = blockState.applyRotation(this.rotation);
			}

			iWorld.setBlockState(blockPos, blockState, 2);
			FluidState fluidState = iWorld.getFluidState(blockPos);
			if (!fluidState.isEmpty()) {
				iWorld.getFluidTickScheduler().schedule(blockPos, fluidState.getFluid(), 0);
			}

			if (field_15311.contains(blockState.getBlock())) {
				iWorld.method_8399(blockPos).markBlockForPostProcessing(blockPos);
			}
		}
	}

	protected BlockState getBlockAt(BlockView blockView, int i, int j, int k, MutableIntBoundingBox mutableIntBoundingBox) {
		int l = this.applyXTransform(i, k);
		int m = this.applyYTransform(j);
		int n = this.applyZTransform(i, k);
		BlockPos blockPos = new BlockPos(l, m, n);
		return !mutableIntBoundingBox.contains(blockPos) ? Blocks.field_10124.getDefaultState() : blockView.getBlockState(blockPos);
	}

	protected boolean isUnderSeaLevel(ViewableWorld viewableWorld, int i, int j, int k, MutableIntBoundingBox mutableIntBoundingBox) {
		int l = this.applyXTransform(i, k);
		int m = this.applyYTransform(j + 1);
		int n = this.applyZTransform(i, k);
		BlockPos blockPos = new BlockPos(l, m, n);
		return !mutableIntBoundingBox.contains(blockPos) ? false : m < viewableWorld.getTop(Heightmap.Type.OCEAN_FLOOR_WG, l, n);
	}

	protected void fill(IWorld iWorld, MutableIntBoundingBox mutableIntBoundingBox, int i, int j, int k, int l, int m, int n) {
		for (int o = j; o <= m; o++) {
			for (int p = i; p <= l; p++) {
				for (int q = k; q <= n; q++) {
					this.addBlock(iWorld, Blocks.field_10124.getDefaultState(), p, o, q, mutableIntBoundingBox);
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
		IWorld iWorld, MutableIntBoundingBox mutableIntBoundingBox, int i, int j, int k, int l, int m, int n, boolean bl, Random random, class_3443.class_3444 arg
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

	protected void method_14920(IWorld iWorld, int i, int j, int k, MutableIntBoundingBox mutableIntBoundingBox) {
		BlockPos blockPos = new BlockPos(this.applyXTransform(i, k), this.applyYTransform(j), this.applyZTransform(i, k));
		if (mutableIntBoundingBox.contains(blockPos)) {
			while (!iWorld.isAir(blockPos) && blockPos.getY() < 255) {
				iWorld.setBlockState(blockPos, Blocks.field_10124.getDefaultState(), 2);
				blockPos = blockPos.up();
			}
		}
	}

	protected void method_14936(IWorld iWorld, BlockState blockState, int i, int j, int k, MutableIntBoundingBox mutableIntBoundingBox) {
		int l = this.applyXTransform(i, k);
		int m = this.applyYTransform(j);
		int n = this.applyZTransform(i, k);
		if (mutableIntBoundingBox.contains(new BlockPos(l, m, n))) {
			while ((iWorld.isAir(new BlockPos(l, m, n)) || iWorld.getBlockState(new BlockPos(l, m, n)).getMaterial().method_15797()) && m > 1) {
				iWorld.setBlockState(new BlockPos(l, m, n), blockState, 2);
				m--;
			}
		}
	}

	protected boolean method_14915(IWorld iWorld, MutableIntBoundingBox mutableIntBoundingBox, Random random, int i, int j, int k, Identifier identifier) {
		BlockPos blockPos = new BlockPos(this.applyXTransform(i, k), this.applyYTransform(j), this.applyZTransform(i, k));
		return this.addChest(iWorld, mutableIntBoundingBox, random, blockPos, identifier, null);
	}

	public static BlockState method_14916(BlockView blockView, BlockPos blockPos, BlockState blockState) {
		Direction direction = null;

		for (Direction direction2 : Direction.class_2353.HORIZONTAL) {
			BlockPos blockPos2 = blockPos.method_10093(direction2);
			BlockState blockState2 = blockView.getBlockState(blockPos2);
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
			return blockState.with(HorizontalFacingBlock.field_11177, direction.getOpposite());
		} else {
			Direction direction3 = blockState.get(HorizontalFacingBlock.field_11177);
			BlockPos blockPos3 = blockPos.method_10093(direction3);
			if (blockView.getBlockState(blockPos3).method_11598(blockView, blockPos3)) {
				direction3 = direction3.getOpposite();
				blockPos3 = blockPos.method_10093(direction3);
			}

			if (blockView.getBlockState(blockPos3).method_11598(blockView, blockPos3)) {
				direction3 = direction3.rotateYClockwise();
				blockPos3 = blockPos.method_10093(direction3);
			}

			if (blockView.getBlockState(blockPos3).method_11598(blockView, blockPos3)) {
				direction3 = direction3.getOpposite();
				blockPos3 = blockPos.method_10093(direction3);
			}

			return blockState.with(HorizontalFacingBlock.field_11177, direction3);
		}
	}

	protected boolean addChest(
		IWorld iWorld, MutableIntBoundingBox mutableIntBoundingBox, Random random, BlockPos blockPos, Identifier identifier, @Nullable BlockState blockState
	) {
		if (mutableIntBoundingBox.contains(blockPos) && iWorld.getBlockState(blockPos).getBlock() != Blocks.field_10034) {
			if (blockState == null) {
				blockState = method_14916(iWorld, blockPos, Blocks.field_10034.getDefaultState());
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

	protected boolean method_14930(
		IWorld iWorld, MutableIntBoundingBox mutableIntBoundingBox, Random random, int i, int j, int k, Direction direction, Identifier identifier
	) {
		BlockPos blockPos = new BlockPos(this.applyXTransform(i, k), this.applyYTransform(j), this.applyZTransform(i, k));
		if (mutableIntBoundingBox.contains(blockPos) && iWorld.getBlockState(blockPos).getBlock() != Blocks.field_10200) {
			this.addBlock(iWorld, Blocks.field_10200.getDefaultState().with(DispenserBlock.field_10918, direction), i, j, k, mutableIntBoundingBox);
			BlockEntity blockEntity = iWorld.getBlockEntity(blockPos);
			if (blockEntity instanceof DispenserBlockEntity) {
				((DispenserBlockEntity)blockEntity).setLootTable(identifier, random.nextLong());
			}

			return true;
		} else {
			return false;
		}
	}

	protected void method_14925(
		IWorld iWorld, MutableIntBoundingBox mutableIntBoundingBox, Random random, int i, int j, int k, Direction direction, DoorBlock doorBlock
	) {
		this.addBlock(iWorld, doorBlock.getDefaultState().with(DoorBlock.field_10938, direction), i, j, k, mutableIntBoundingBox);
		this.addBlock(
			iWorld,
			doorBlock.getDefaultState().with(DoorBlock.field_10938, direction).with(DoorBlock.field_10946, BlockHalf.field_12609),
			i,
			j + 1,
			k,
			mutableIntBoundingBox
		);
	}

	public void translate(int i, int j, int k) {
		this.structureBounds.translate(i, j, k);
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

	public Rotation method_16888() {
		return this.rotation;
	}

	public StructurePiece method_16653() {
		return this.field_16712;
	}

	public abstract static class class_3444 {
		protected BlockState block = Blocks.field_10124.getDefaultState();

		protected class_3444() {
		}

		public abstract void method_14948(Random random, int i, int j, int k, boolean bl);

		public BlockState getBlock() {
			return this.block;
		}
	}
}
