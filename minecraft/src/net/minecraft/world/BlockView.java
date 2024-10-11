package net.minecraft.world;

import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;

/**
 * Represents a scoped, read-only view of block states, fluid states and block entities.
 */
public interface BlockView extends HeightLimitView {
	int field_54147 = 16;

	/**
	 * {@return the block entity at {@code pos}, or {@code null} if there is none}
	 */
	@Nullable
	BlockEntity getBlockEntity(BlockPos pos);

	default <T extends BlockEntity> Optional<T> getBlockEntity(BlockPos pos, BlockEntityType<T> type) {
		BlockEntity blockEntity = this.getBlockEntity(pos);
		return blockEntity != null && blockEntity.getType() == type ? Optional.of(blockEntity) : Optional.empty();
	}

	/**
	 * {@return the block state at {@code pos}}
	 * 
	 * @implNote This returns the block state for {@link net.minecraft.block.Blocks#VOID_AIR}
	 * if the Y coordinate is outside the height limit.
	 */
	BlockState getBlockState(BlockPos pos);

	/**
	 * {@return the fluid state at {@code pos}}
	 * 
	 * @implNote This returns the fluid state for {@link net.minecraft.fluid.Fluids#EMPTY}
	 * if the Y coordinate is outside the height limit.
	 */
	FluidState getFluidState(BlockPos pos);

	default int getLuminance(BlockPos pos) {
		return this.getBlockState(pos).getLuminance();
	}

	default Stream<BlockState> getStatesInBox(Box box) {
		return BlockPos.stream(box).map(this::getBlockState);
	}

	default BlockHitResult raycast(BlockStateRaycastContext context) {
		return raycast(
			context.getStart(),
			context.getEnd(),
			context,
			(innerContext, pos) -> {
				BlockState blockState = this.getBlockState(pos);
				Vec3d vec3d = innerContext.getStart().subtract(innerContext.getEnd());
				return innerContext.getStatePredicate().test(blockState)
					? new BlockHitResult(innerContext.getEnd(), Direction.getFacing(vec3d.x, vec3d.y, vec3d.z), BlockPos.ofFloored(innerContext.getEnd()), false)
					: null;
			},
			innerContext -> {
				Vec3d vec3d = innerContext.getStart().subtract(innerContext.getEnd());
				return BlockHitResult.createMissed(innerContext.getEnd(), Direction.getFacing(vec3d.x, vec3d.y, vec3d.z), BlockPos.ofFloored(innerContext.getEnd()));
			}
		);
	}

	default BlockHitResult raycast(RaycastContext context) {
		return raycast(context.getStart(), context.getEnd(), context, (innerContext, pos) -> {
			BlockState blockState = this.getBlockState(pos);
			FluidState fluidState = this.getFluidState(pos);
			Vec3d vec3d = innerContext.getStart();
			Vec3d vec3d2 = innerContext.getEnd();
			VoxelShape voxelShape = innerContext.getBlockShape(blockState, this, pos);
			BlockHitResult blockHitResult = this.raycastBlock(vec3d, vec3d2, pos, voxelShape, blockState);
			VoxelShape voxelShape2 = innerContext.getFluidShape(fluidState, this, pos);
			BlockHitResult blockHitResult2 = voxelShape2.raycast(vec3d, vec3d2, pos);
			double d = blockHitResult == null ? Double.MAX_VALUE : innerContext.getStart().squaredDistanceTo(blockHitResult.getPos());
			double e = blockHitResult2 == null ? Double.MAX_VALUE : innerContext.getStart().squaredDistanceTo(blockHitResult2.getPos());
			return d <= e ? blockHitResult : blockHitResult2;
		}, innerContext -> {
			Vec3d vec3d = innerContext.getStart().subtract(innerContext.getEnd());
			return BlockHitResult.createMissed(innerContext.getEnd(), Direction.getFacing(vec3d.x, vec3d.y, vec3d.z), BlockPos.ofFloored(innerContext.getEnd()));
		});
	}

	@Nullable
	default BlockHitResult raycastBlock(Vec3d start, Vec3d end, BlockPos pos, VoxelShape shape, BlockState state) {
		BlockHitResult blockHitResult = shape.raycast(start, end, pos);
		if (blockHitResult != null) {
			BlockHitResult blockHitResult2 = state.getRaycastShape(this, pos).raycast(start, end, pos);
			if (blockHitResult2 != null && blockHitResult2.getPos().subtract(start).lengthSquared() < blockHitResult.getPos().subtract(start).lengthSquared()) {
				return blockHitResult.withSide(blockHitResult2.getSide());
			}
		}

		return blockHitResult;
	}

	default double getDismountHeight(VoxelShape blockCollisionShape, Supplier<VoxelShape> belowBlockCollisionShapeGetter) {
		if (!blockCollisionShape.isEmpty()) {
			return blockCollisionShape.getMax(Direction.Axis.Y);
		} else {
			double d = ((VoxelShape)belowBlockCollisionShapeGetter.get()).getMax(Direction.Axis.Y);
			return d >= 1.0 ? d - 1.0 : Double.NEGATIVE_INFINITY;
		}
	}

	default double getDismountHeight(BlockPos pos) {
		return this.getDismountHeight(this.getBlockState(pos).getCollisionShape(this, pos), () -> {
			BlockPos blockPos2 = pos.down();
			return this.getBlockState(blockPos2).getCollisionShape(this, blockPos2);
		});
	}

	static <T, C> T raycast(Vec3d start, Vec3d end, C context, BiFunction<C, BlockPos, T> blockHitFactory, Function<C, T> missFactory) {
		if (start.equals(end)) {
			return (T)missFactory.apply(context);
		} else {
			double d = MathHelper.lerp(-1.0E-7, end.x, start.x);
			double e = MathHelper.lerp(-1.0E-7, end.y, start.y);
			double f = MathHelper.lerp(-1.0E-7, end.z, start.z);
			double g = MathHelper.lerp(-1.0E-7, start.x, end.x);
			double h = MathHelper.lerp(-1.0E-7, start.y, end.y);
			double i = MathHelper.lerp(-1.0E-7, start.z, end.z);
			int j = MathHelper.floor(g);
			int k = MathHelper.floor(h);
			int l = MathHelper.floor(i);
			BlockPos.Mutable mutable = new BlockPos.Mutable(j, k, l);
			T object = (T)blockHitFactory.apply(context, mutable);
			if (object != null) {
				return object;
			} else {
				double m = d - g;
				double n = e - h;
				double o = f - i;
				int p = MathHelper.sign(m);
				int q = MathHelper.sign(n);
				int r = MathHelper.sign(o);
				double s = p == 0 ? Double.MAX_VALUE : (double)p / m;
				double t = q == 0 ? Double.MAX_VALUE : (double)q / n;
				double u = r == 0 ? Double.MAX_VALUE : (double)r / o;
				double v = s * (p > 0 ? 1.0 - MathHelper.fractionalPart(g) : MathHelper.fractionalPart(g));
				double w = t * (q > 0 ? 1.0 - MathHelper.fractionalPart(h) : MathHelper.fractionalPart(h));
				double x = u * (r > 0 ? 1.0 - MathHelper.fractionalPart(i) : MathHelper.fractionalPart(i));

				while (v <= 1.0 || w <= 1.0 || x <= 1.0) {
					if (v < w) {
						if (v < x) {
							j += p;
							v += s;
						} else {
							l += r;
							x += u;
						}
					} else if (w < x) {
						k += q;
						w += t;
					} else {
						l += r;
						x += u;
					}

					T object2 = (T)blockHitFactory.apply(context, mutable.set(j, k, l));
					if (object2 != null) {
						return object2;
					}
				}

				return (T)missFactory.apply(context);
			}
		}
	}

	static Iterable<BlockPos> collectCollisionsBetween(Vec3d oldPos, Vec3d newPos, Box boundingBox) {
		Vec3d vec3d = newPos.subtract(oldPos);
		Iterable<BlockPos> iterable = BlockPos.iterate(boundingBox);
		if (vec3d.lengthSquared() < (double)MathHelper.square(0.99999F)) {
			return iterable;
		} else {
			Set<BlockPos> set = new ObjectLinkedOpenHashSet<>();
			Vec3d vec3d2 = vec3d.normalize().multiply(1.0E-7);
			Vec3d vec3d3 = boundingBox.getMinPos().add(vec3d2);
			Vec3d vec3d4 = boundingBox.getMinPos().subtract(vec3d).subtract(vec3d2);
			collectCollisionsBetween(set, vec3d4, vec3d3, boundingBox);

			for (BlockPos blockPos : iterable) {
				set.add(blockPos.toImmutable());
			}

			return set;
		}
	}

	private static void collectCollisionsBetween(Set<BlockPos> result, Vec3d oldPos, Vec3d newPos, Box boundingBox) {
		Vec3d vec3d = newPos.subtract(oldPos);
		int i = MathHelper.floor(oldPos.x);
		int j = MathHelper.floor(oldPos.y);
		int k = MathHelper.floor(oldPos.z);
		int l = MathHelper.sign(vec3d.x);
		int m = MathHelper.sign(vec3d.y);
		int n = MathHelper.sign(vec3d.z);
		double d = l == 0 ? Double.MAX_VALUE : (double)l / vec3d.x;
		double e = m == 0 ? Double.MAX_VALUE : (double)m / vec3d.y;
		double f = n == 0 ? Double.MAX_VALUE : (double)n / vec3d.z;
		double g = d * (l > 0 ? 1.0 - MathHelper.fractionalPart(oldPos.x) : MathHelper.fractionalPart(oldPos.x));
		double h = e * (m > 0 ? 1.0 - MathHelper.fractionalPart(oldPos.y) : MathHelper.fractionalPart(oldPos.y));
		double o = f * (n > 0 ? 1.0 - MathHelper.fractionalPart(oldPos.z) : MathHelper.fractionalPart(oldPos.z));
		int p = 0;

		while (g <= 1.0 || h <= 1.0 || o <= 1.0) {
			if (g < h) {
				if (g < o) {
					i += l;
					g += d;
				} else {
					k += n;
					o += f;
				}
			} else if (h < o) {
				j += m;
				h += e;
			} else {
				k += n;
				o += f;
			}

			if (p++ > 16) {
				break;
			}

			Optional<Vec3d> optional = Box.raycast((double)i, (double)j, (double)k, (double)(i + 1), (double)(j + 1), (double)(k + 1), oldPos, newPos);
			if (!optional.isEmpty()) {
				Vec3d vec3d2 = (Vec3d)optional.get();
				double q = MathHelper.clamp(vec3d2.x, (double)i + 1.0E-5F, (double)i + 1.0 - 1.0E-5F);
				double r = MathHelper.clamp(vec3d2.y, (double)j + 1.0E-5F, (double)j + 1.0 - 1.0E-5F);
				double s = MathHelper.clamp(vec3d2.z, (double)k + 1.0E-5F, (double)k + 1.0 - 1.0E-5F);
				int t = MathHelper.floor(q + boundingBox.getLengthX());
				int u = MathHelper.floor(r + boundingBox.getLengthY());
				int v = MathHelper.floor(s + boundingBox.getLengthZ());

				for (int w = i; w <= t; w++) {
					for (int x = j; x <= u; x++) {
						for (int y = k; y <= v; y++) {
							result.add(new BlockPos(w, x, y));
						}
					}
				}
			}
		}
	}
}
