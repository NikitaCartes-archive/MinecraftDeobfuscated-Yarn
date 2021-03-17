package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.IntRange;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class class_5945 {
	public static void method_34682(World world, BlockPos blockPos, ParticleEffect particleEffect, IntRange intRange) {
		for (Direction direction : Direction.values()) {
			int i = intRange.choose(world.random);

			for (int j = 0; j < i; j++) {
				method_34681(world, blockPos, direction, particleEffect);
			}
		}
	}

	public static void method_34683(Direction.Axis axis, World world, BlockPos blockPos, double d, ParticleEffect particleEffect, IntRange intRange) {
		Vec3d vec3d = Vec3d.ofCenter(blockPos);
		boolean bl = axis == Direction.Axis.X;
		boolean bl2 = axis == Direction.Axis.Y;
		boolean bl3 = axis == Direction.Axis.Z;
		int i = intRange.choose(world.random);

		for (int j = 0; j < i; j++) {
			double e = vec3d.x + MathHelper.nextDouble(world.random, -1.0, 1.0) * (bl ? 0.5 : d);
			double f = vec3d.y + MathHelper.nextDouble(world.random, -1.0, 1.0) * (bl2 ? 0.5 : d);
			double g = vec3d.z + MathHelper.nextDouble(world.random, -1.0, 1.0) * (bl3 ? 0.5 : d);
			double h = bl ? MathHelper.nextDouble(world.random, -1.0, 1.0) : 0.0;
			double k = bl2 ? MathHelper.nextDouble(world.random, -1.0, 1.0) : 0.0;
			double l = bl3 ? MathHelper.nextDouble(world.random, -1.0, 1.0) : 0.0;
			world.addParticle(particleEffect, e, f, g, h, k, l);
		}
	}

	public static void method_34681(World world, BlockPos blockPos, Direction direction, ParticleEffect particleEffect) {
		Vec3d vec3d = Vec3d.ofCenter(blockPos);
		int i = direction.getOffsetX();
		int j = direction.getOffsetY();
		int k = direction.getOffsetZ();
		double d = vec3d.x + (i == 0 ? MathHelper.nextDouble(world.random, -0.5, 0.5) : (double)i * 0.55);
		double e = vec3d.y + (j == 0 ? MathHelper.nextDouble(world.random, -0.5, 0.5) : (double)j * 0.55);
		double f = vec3d.z + (k == 0 ? MathHelper.nextDouble(world.random, -0.5, 0.5) : (double)k * 0.55);
		double g = i == 0 ? MathHelper.nextDouble(world.random, -1.0, 1.0) : 0.0;
		double h = j == 0 ? MathHelper.nextDouble(world.random, -1.0, 1.0) : 0.0;
		double l = k == 0 ? MathHelper.nextDouble(world.random, -1.0, 1.0) : 0.0;
		world.addParticle(particleEffect, d, e, f, g, h, l);
	}
}
