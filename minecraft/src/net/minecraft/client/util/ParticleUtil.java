package net.minecraft.client.util;

import java.util.Random;
import java.util.function.Supplier;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.World;

public class ParticleUtil {
	public static void spawnParticle(World world, BlockPos blockPos, ParticleEffect particleEffect, IntProvider intProvider) {
		for (Direction direction : Direction.values()) {
			method_42774(world, blockPos, particleEffect, intProvider, direction, () -> method_42775(world.random), 0.55);
		}
	}

	public static void method_42774(
		World world, BlockPos blockPos, ParticleEffect particleEffect, IntProvider intProvider, Direction direction, Supplier<Vec3d> supplier, double d
	) {
		int i = intProvider.get(world.random);

		for (int j = 0; j < i; j++) {
			spawnParticle(world, blockPos, direction, particleEffect, (Vec3d)supplier.get(), d);
		}
	}

	private static Vec3d method_42775(Random random) {
		return new Vec3d(MathHelper.nextDouble(random, -0.5, 0.5), MathHelper.nextDouble(random, -0.5, 0.5), MathHelper.nextDouble(random, -0.5, 0.5));
	}

	public static void spawnParticle(Direction.Axis axis, World world, BlockPos pos, double variance, ParticleEffect effect, UniformIntProvider range) {
		Vec3d vec3d = Vec3d.ofCenter(pos);
		boolean bl = axis == Direction.Axis.X;
		boolean bl2 = axis == Direction.Axis.Y;
		boolean bl3 = axis == Direction.Axis.Z;
		int i = range.get(world.random);

		for (int j = 0; j < i; j++) {
			double d = vec3d.x + MathHelper.nextDouble(world.random, -1.0, 1.0) * (bl ? 0.5 : variance);
			double e = vec3d.y + MathHelper.nextDouble(world.random, -1.0, 1.0) * (bl2 ? 0.5 : variance);
			double f = vec3d.z + MathHelper.nextDouble(world.random, -1.0, 1.0) * (bl3 ? 0.5 : variance);
			double g = bl ? MathHelper.nextDouble(world.random, -1.0, 1.0) : 0.0;
			double h = bl2 ? MathHelper.nextDouble(world.random, -1.0, 1.0) : 0.0;
			double k = bl3 ? MathHelper.nextDouble(world.random, -1.0, 1.0) : 0.0;
			world.addParticle(effect, d, e, f, g, h, k);
		}
	}

	public static void spawnParticle(World world, BlockPos pos, Direction direction, ParticleEffect effect, Vec3d vec3d, double d) {
		Vec3d vec3d2 = Vec3d.ofCenter(pos);
		int i = direction.getOffsetX();
		int j = direction.getOffsetY();
		int k = direction.getOffsetZ();
		double e = vec3d2.x + (i == 0 ? MathHelper.nextDouble(world.random, -0.5, 0.5) : (double)i * d);
		double f = vec3d2.y + (j == 0 ? MathHelper.nextDouble(world.random, -0.5, 0.5) : (double)j * d);
		double g = vec3d2.z + (k == 0 ? MathHelper.nextDouble(world.random, -0.5, 0.5) : (double)k * d);
		double h = i == 0 ? vec3d.getX() : 0.0;
		double l = j == 0 ? vec3d.getY() : 0.0;
		double m = k == 0 ? vec3d.getZ() : 0.0;
		world.addParticle(effect, e, f, g, h, l, m);
	}
}
