package net.minecraft;

import java.util.Objects;
import java.util.Spliterators.AbstractSpliterator;
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
import net.minecraft.world.BlockView;
import net.minecraft.world.CollisionView;
import net.minecraft.world.border.WorldBorder;

public class class_5329 extends AbstractSpliterator<VoxelShape> {
	@Nullable
	private final Entity field_25168;
	private final Box field_25169;
	private final ShapeContext field_25170;
	private final CuboidBlockIterator field_25171;
	private final BlockPos.Mutable field_25172;
	private final VoxelShape field_25173;
	private final CollisionView field_25174;
	private boolean field_25175;

	public class_5329(CollisionView collisionView, @Nullable Entity entity, Box box) {
		super(Long.MAX_VALUE, 1280);
		this.field_25170 = entity == null ? ShapeContext.absent() : ShapeContext.of(entity);
		this.field_25172 = new BlockPos.Mutable();
		this.field_25173 = VoxelShapes.cuboid(box);
		this.field_25174 = collisionView;
		this.field_25175 = entity != null;
		this.field_25168 = entity;
		this.field_25169 = box;
		int i = MathHelper.floor(box.minX - 1.0E-7) - 1;
		int j = MathHelper.floor(box.maxX + 1.0E-7) + 1;
		int k = MathHelper.floor(box.minY - 1.0E-7) - 1;
		int l = MathHelper.floor(box.maxY + 1.0E-7) + 1;
		int m = MathHelper.floor(box.minZ - 1.0E-7) - 1;
		int n = MathHelper.floor(box.maxZ + 1.0E-7) + 1;
		this.field_25171 = new CuboidBlockIterator(i, k, m, j, l, n);
	}

	public boolean tryAdvance(Consumer<? super VoxelShape> consumer) {
		return this.field_25175 && this.method_29286(consumer) || this.method_29285(consumer);
	}

	boolean method_29285(Consumer<? super VoxelShape> consumer) {
		while (this.field_25171.step()) {
			int i = this.field_25171.getX();
			int j = this.field_25171.getY();
			int k = this.field_25171.getZ();
			int l = this.field_25171.getEdgeCoordinatesCount();
			if (l != 3) {
				BlockView blockView = this.method_29283(i, k);
				if (blockView != null) {
					this.field_25172.set(i, j, k);
					BlockState blockState = blockView.getBlockState(this.field_25172);
					if ((l != 1 || blockState.exceedsCube()) && (l != 2 || blockState.isOf(Blocks.MOVING_PISTON))) {
						VoxelShape voxelShape = blockState.getCollisionShape(this.field_25174, this.field_25172, this.field_25170);
						if (voxelShape == VoxelShapes.fullCube()) {
							if (this.field_25169.intersects((double)i, (double)j, (double)k, (double)i + 1.0, (double)j + 1.0, (double)k + 1.0)) {
								consumer.accept(voxelShape.offset((double)i, (double)j, (double)k));
								return true;
							}
						} else {
							VoxelShape voxelShape2 = voxelShape.offset((double)i, (double)j, (double)k);
							if (VoxelShapes.matchesAnywhere(voxelShape2, this.field_25173, BooleanBiFunction.AND)) {
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
	private BlockView method_29283(int i, int j) {
		int k = i >> 4;
		int l = j >> 4;
		return this.field_25174.getExistingChunk(k, l);
	}

	boolean method_29286(Consumer<? super VoxelShape> consumer) {
		Objects.requireNonNull(this.field_25168);
		this.field_25175 = false;
		WorldBorder worldBorder = this.field_25174.getWorldBorder();
		boolean bl = method_29284(worldBorder, this.field_25168.getBoundingBox().contract(1.0E-7));
		boolean bl2 = bl && !method_29284(worldBorder, this.field_25168.getBoundingBox().expand(1.0E-7));
		if (bl2) {
			consumer.accept(worldBorder.asVoxelShape());
			return true;
		} else {
			return false;
		}
	}

	public static boolean method_29284(WorldBorder worldBorder, Box box) {
		double d = (double)MathHelper.floor(worldBorder.getBoundWest());
		double e = (double)MathHelper.floor(worldBorder.getBoundNorth());
		double f = (double)MathHelper.ceil(worldBorder.getBoundEast());
		double g = (double)MathHelper.ceil(worldBorder.getBoundSouth());
		return box.minX > d && box.minX < f && box.minZ > e && box.minZ < g && box.maxX > d && box.maxX < f && box.maxZ > e && box.maxZ < g;
	}
}
