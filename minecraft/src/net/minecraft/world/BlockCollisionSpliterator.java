package net.minecraft.world;

import java.util.Objects;
import java.util.Spliterators.AbstractSpliterator;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.util.CuboidBlockIterator;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.border.WorldBorder;

public class BlockCollisionSpliterator extends AbstractSpliterator<VoxelShape> {
	@Nullable
	private final Entity entity;
	private final Box box;
	private final ShapeContext context;
	private final CuboidBlockIterator blockIterator;
	private final BlockPos.Mutable pos;
	private final VoxelShape boxShape;
	private final CollisionView world;
	private boolean checkEntity;
	private final BiPredicate<BlockState, BlockPos> blockPredicate;

	public BlockCollisionSpliterator(CollisionView world, @Nullable Entity entity, Box box) {
		this(world, entity, box, (blockState, blockPos) -> true);
	}

	public BlockCollisionSpliterator(CollisionView collisionView, @Nullable Entity entity, Box box, BiPredicate<BlockState, BlockPos> blockPredicate) {
		super(Long.MAX_VALUE, 1280);
		this.context = entity == null ? ShapeContext.absent() : ShapeContext.of(entity);
		this.pos = new BlockPos.Mutable();
		this.boxShape = VoxelShapes.cuboid(box);
		this.world = collisionView;
		this.checkEntity = entity != null;
		this.entity = entity;
		this.box = box;
		this.blockPredicate = blockPredicate;
		int i = MathHelper.floor(box.minX - 1.0E-7) - 1;
		int j = MathHelper.floor(box.maxX + 1.0E-7) + 1;
		int k = MathHelper.floor(box.minY - 1.0E-7) - 1;
		int l = MathHelper.floor(box.maxY + 1.0E-7) + 1;
		int m = MathHelper.floor(box.minZ - 1.0E-7) - 1;
		int n = MathHelper.floor(box.maxZ + 1.0E-7) + 1;
		this.blockIterator = new CuboidBlockIterator(i, k, m, j, l, n);
	}

	public boolean tryAdvance(Consumer<? super VoxelShape> consumer) {
		return this.checkEntity && this.offerEntityShape(consumer) || this.offerBlockShape(consumer);
	}

	boolean offerBlockShape(Consumer<? super VoxelShape> consumer) {
		while (this.blockIterator.step()) {
			int i = this.blockIterator.getX();
			int j = this.blockIterator.getY();
			int k = this.blockIterator.getZ();
			int l = this.blockIterator.getEdgeCoordinatesCount();
			if (l != 3) {
				BlockView blockView = this.getChunk(i, k);
				if (blockView != null) {
					this.pos.set(i, j, k);
					BlockState blockState = blockView.getBlockState(this.pos);
					if (this.blockPredicate.test(blockState, this.pos) && (l != 1 || blockState.exceedsCube()) && (l != 2 || blockState.isOf(Blocks.MOVING_PISTON))) {
						VoxelShape voxelShape = blockState.getCollisionShape(this.world, this.pos, this.context);
						if (voxelShape == VoxelShapes.fullCube()) {
							if (this.box.intersects((double)i, (double)j, (double)k, (double)i + 1.0, (double)j + 1.0, (double)k + 1.0)) {
								consumer.accept(voxelShape.offset((double)i, (double)j, (double)k));
								return true;
							}
						} else {
							VoxelShape voxelShape2 = voxelShape.offset((double)i, (double)j, (double)k);
							if (VoxelShapes.matchesAnywhere(voxelShape2, this.boxShape, BooleanBiFunction.AND)) {
								consumer.accept(voxelShape2);
								return true;
							}
						}
					}
				}
			}
		}

		return false;
	}

	@Nullable
	private BlockView getChunk(int x, int z) {
		int i = x >> 4;
		int j = z >> 4;
		return this.world.getExistingChunk(i, j);
	}

	boolean offerEntityShape(Consumer<? super VoxelShape> consumer) {
		Objects.requireNonNull(this.entity);
		this.checkEntity = false;
		WorldBorder worldBorder = this.world.getWorldBorder();
		Box box = this.entity.getBoundingBox();
		if (!isInWorldBorder(worldBorder, box)) {
			VoxelShape voxelShape = worldBorder.asVoxelShape();
			if (!method_30131(voxelShape, box) && method_30130(voxelShape, box)) {
				consumer.accept(voxelShape);
				return true;
			}
		}

		return false;
	}

	private static boolean method_30130(VoxelShape voxelShape, Box box) {
		return VoxelShapes.matchesAnywhere(voxelShape, VoxelShapes.cuboid(box.expand(1.0E-7)), BooleanBiFunction.AND);
	}

	private static boolean method_30131(VoxelShape voxelShape, Box box) {
		return VoxelShapes.matchesAnywhere(voxelShape, VoxelShapes.cuboid(box.contract(1.0E-7)), BooleanBiFunction.AND);
	}

	public static boolean isInWorldBorder(WorldBorder border, Box box) {
		double d = (double)MathHelper.floor(border.getBoundWest());
		double e = (double)MathHelper.floor(border.getBoundNorth());
		double f = (double)MathHelper.ceil(border.getBoundEast());
		double g = (double)MathHelper.ceil(border.getBoundSouth());
		return box.minX > d && box.minX < f && box.minZ > e && box.minZ < g && box.maxX > d && box.maxX < f && box.maxZ > e && box.maxZ < g;
	}
}
