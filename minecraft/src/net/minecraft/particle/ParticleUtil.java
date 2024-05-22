package net.minecraft.particle;

import java.util.function.Supplier;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

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

	public static void spawnParticlesAround(WorldAccess world, BlockPos pos, int count, ParticleEffect effect) {
		double d = 0.5;
		BlockState blockState = world.getBlockState(pos);
		double e = blockState.isAir() ? 1.0 : blockState.getOutlineShape(world, pos).getMax(Direction.Axis.Y);
		spawnParticlesAround(world, pos, count, 0.5, e, true, effect);
	}

	public static void spawnParticlesAround(
		WorldAccess world, BlockPos pos, int count, double horizontalOffset, double verticalOffset, boolean force, ParticleEffect effect
	) {
		Random random = world.getRandom();

		for (int i = 0; i < count; i++) {
			double d = random.nextGaussian() * 0.02;
			double e = random.nextGaussian() * 0.02;
			double f = random.nextGaussian() * 0.02;
			double g = 0.5 - horizontalOffset;
			double h = (double)pos.getX() + g + random.nextDouble() * horizontalOffset * 2.0;
			double j = (double)pos.getY() + random.nextDouble() * verticalOffset;
			double k = (double)pos.getZ() + g + random.nextDouble() * horizontalOffset * 2.0;
			if (force || !world.getBlockState(BlockPos.ofFloored(h, j, k).down()).isAir()) {
				world.addParticle(effect, h, j, k, d, e, f);
			}
		}
	}

	public static void spawnSmashAttackParticles(WorldAccess world, BlockPos pos, int count) {
		Vec3d vec3d = pos.toCenterPos().add(0.0, 0.5, 0.0);
		BlockStateParticleEffect blockStateParticleEffect = new BlockStateParticleEffect(ParticleTypes.DUST_PILLAR, world.getBlockState(pos));

		for (int i = 0; (float)i < (float)count / 3.0F; i++) {
			double d = vec3d.x + world.getRandom().nextGaussian() / 2.0;
			double e = vec3d.y;
			double f = vec3d.z + world.getRandom().nextGaussian() / 2.0;
			double g = world.getRandom().nextGaussian() * 0.2F;
			double h = world.getRandom().nextGaussian() * 0.2F;
			double j = world.getRandom().nextGaussian() * 0.2F;
			world.addParticle(blockStateParticleEffect, d, e, f, g, h, j);
		}

		for (int i = 0; (float)i < (float)count / 1.5F; i++) {
			double d = vec3d.x + 3.5 * Math.cos((double)i) + world.getRandom().nextGaussian() / 2.0;
			double e = vec3d.y;
			double f = vec3d.z + 3.5 * Math.sin((double)i) + world.getRandom().nextGaussian() / 2.0;
			double g = world.getRandom().nextGaussian() * 0.05F;
			double h = world.getRandom().nextGaussian() * 0.05F;
			double j = world.getRandom().nextGaussian() * 0.05F;
			world.addParticle(blockStateParticleEffect, d, e, f, g, h, j);
		}
	}
}
