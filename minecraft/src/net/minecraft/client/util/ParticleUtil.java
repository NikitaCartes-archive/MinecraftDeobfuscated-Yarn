package net.minecraft.client.util;

import java.util.function.Supplier;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class ParticleUtil {
	public static void spawnParticle(World world, BlockPos pos, ParticleEffect effect, IntProvider count) {
		for (Direction direction : Direction.values()) {
			spawnParticles(world, pos, effect, count, direction, () -> getRandomVelocity(world.random), 0.55);
		}
	}

	public static void spawnParticles(
		World world, BlockPos pos, ParticleEffect effect, IntProvider count, Direction direction, Supplier<Vec3d> velocity, double offsetMultiplier
	) {
		int i = count.get(world.random);

		for (int j = 0; j < i; j++) {
			spawnParticle(world, pos, direction, effect, (Vec3d)velocity.get(), offsetMultiplier);
		}
	}

	private static Vec3d getRandomVelocity(Random random) {
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

	public static void spawnParticle(World world, BlockPos pos, Direction direction, ParticleEffect effect, Vec3d velocity, double offsetMultiplier) {
		Vec3d vec3d = Vec3d.ofCenter(pos);
		int i = direction.getOffsetX();
		int j = direction.getOffsetY();
		int k = direction.getOffsetZ();
		double d = vec3d.x + (i == 0 ? MathHelper.nextDouble(world.random, -0.5, 0.5) : (double)i * offsetMultiplier);
		double e = vec3d.y + (j == 0 ? MathHelper.nextDouble(world.random, -0.5, 0.5) : (double)j * offsetMultiplier);
		double f = vec3d.z + (k == 0 ? MathHelper.nextDouble(world.random, -0.5, 0.5) : (double)k * offsetMultiplier);
		double g = i == 0 ? velocity.getX() : 0.0;
		double h = j == 0 ? velocity.getY() : 0.0;
		double l = k == 0 ? velocity.getZ() : 0.0;
		world.addParticle(effect, d, e, f, g, h, l);
	}

	public static void spawnParticle(World world, BlockPos pos, Random random, ParticleEffect effect) {
		double d = (double)pos.getX() + random.nextDouble();
		double e = (double)pos.getY() - 0.05;
		double f = (double)pos.getZ() + random.nextDouble();
		world.addParticle(effect, d, e, f, 0.0, 0.0, 0.0);
	}
}
