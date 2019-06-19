package net.minecraft.world;

import java.util.function.BiFunction;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;

public interface BlockView {
	@Nullable
	BlockEntity getBlockEntity(BlockPos blockPos);

	BlockState getBlockState(BlockPos blockPos);

	FluidState getFluidState(BlockPos blockPos);

	default int getLuminance(BlockPos blockPos) {
		return this.getBlockState(blockPos).getLuminance();
	}

	default int getMaxLightLevel() {
		return 15;
	}

	default int getHeight() {
		return 256;
	}

	default BlockHitResult rayTrace(RayTraceContext rayTraceContext) {
		return rayTrace(rayTraceContext, (rayTraceContextx, blockPos) -> {
			BlockState blockState = this.getBlockState(blockPos);
			FluidState fluidState = this.getFluidState(blockPos);
			Vec3d vec3d = rayTraceContextx.getStart();
			Vec3d vec3d2 = rayTraceContextx.getEnd();
			VoxelShape voxelShape = rayTraceContextx.getBlockShape(blockState, this, blockPos);
			BlockHitResult blockHitResult = this.rayTraceBlock(vec3d, vec3d2, blockPos, voxelShape, blockState);
			VoxelShape voxelShape2 = rayTraceContextx.getFluidShape(fluidState, this, blockPos);
			BlockHitResult blockHitResult2 = voxelShape2.rayTrace(vec3d, vec3d2, blockPos);
			double d = blockHitResult == null ? Double.MAX_VALUE : rayTraceContextx.getStart().squaredDistanceTo(blockHitResult.getPos());
			double e = blockHitResult2 == null ? Double.MAX_VALUE : rayTraceContextx.getStart().squaredDistanceTo(blockHitResult2.getPos());
			return d <= e ? blockHitResult : blockHitResult2;
		}, rayTraceContextx -> {
			Vec3d vec3d = rayTraceContextx.getStart().subtract(rayTraceContextx.getEnd());
			return BlockHitResult.createMissed(rayTraceContextx.getEnd(), Direction.getFacing(vec3d.x, vec3d.y, vec3d.z), new BlockPos(rayTraceContextx.getEnd()));
		});
	}

	@Nullable
	default BlockHitResult rayTraceBlock(Vec3d vec3d, Vec3d vec3d2, BlockPos blockPos, VoxelShape voxelShape, BlockState blockState) {
		BlockHitResult blockHitResult = voxelShape.rayTrace(vec3d, vec3d2, blockPos);
		if (blockHitResult != null) {
			BlockHitResult blockHitResult2 = blockState.getRayTraceShape(this, blockPos).rayTrace(vec3d, vec3d2, blockPos);
			if (blockHitResult2 != null && blockHitResult2.getPos().subtract(vec3d).lengthSquared() < blockHitResult.getPos().subtract(vec3d).lengthSquared()) {
				return blockHitResult.withSide(blockHitResult2.getSide());
			}
		}

		return blockHitResult;
	}

	static <T> T rayTrace(RayTraceContext rayTraceContext, BiFunction<RayTraceContext, BlockPos, T> biFunction, Function<RayTraceContext, T> function) {
		Vec3d vec3d = rayTraceContext.getStart();
		Vec3d vec3d2 = rayTraceContext.getEnd();
		if (vec3d.equals(vec3d2)) {
			return (T)function.apply(rayTraceContext);
		} else {
			double d = MathHelper.lerp(-1.0E-7, vec3d2.x, vec3d.x);
			double e = MathHelper.lerp(-1.0E-7, vec3d2.y, vec3d.y);
			double f = MathHelper.lerp(-1.0E-7, vec3d2.z, vec3d.z);
			double g = MathHelper.lerp(-1.0E-7, vec3d.x, vec3d2.x);
			double h = MathHelper.lerp(-1.0E-7, vec3d.y, vec3d2.y);
			double i = MathHelper.lerp(-1.0E-7, vec3d.z, vec3d2.z);
			int j = MathHelper.floor(g);
			int k = MathHelper.floor(h);
			int l = MathHelper.floor(i);
			BlockPos.Mutable mutable = new BlockPos.Mutable(j, k, l);
			T object = (T)biFunction.apply(rayTraceContext, mutable);
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

					T object2 = (T)biFunction.apply(rayTraceContext, mutable.set(j, k, l));
					if (object2 != null) {
						return object2;
					}
				}

				return (T)function.apply(rayTraceContext);
			}
		}
	}
}
