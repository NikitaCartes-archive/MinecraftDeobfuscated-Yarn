package net.minecraft;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class class_1414 {
	@Nullable
	public static Vec3d method_6375(MobEntityWithAi mobEntityWithAi, int i, int j) {
		return method_6376(mobEntityWithAi, i, j, null);
	}

	@Nullable
	public static Vec3d method_6378(MobEntityWithAi mobEntityWithAi, int i, int j) {
		return method_6371(mobEntityWithAi, i, j, null, false, 0.0);
	}

	@Nullable
	public static Vec3d method_6373(MobEntityWithAi mobEntityWithAi, int i, int j, Vec3d vec3d) {
		Vec3d vec3d2 = vec3d.subtract(mobEntityWithAi.x, mobEntityWithAi.y, mobEntityWithAi.z);
		return method_6376(mobEntityWithAi, i, j, vec3d2);
	}

	@Nullable
	public static Vec3d method_6377(MobEntityWithAi mobEntityWithAi, int i, int j, Vec3d vec3d, double d) {
		Vec3d vec3d2 = vec3d.subtract(mobEntityWithAi.x, mobEntityWithAi.y, mobEntityWithAi.z);
		return method_6371(mobEntityWithAi, i, j, vec3d2, true, d);
	}

	@Nullable
	public static Vec3d method_6379(MobEntityWithAi mobEntityWithAi, int i, int j, Vec3d vec3d) {
		Vec3d vec3d2 = new Vec3d(mobEntityWithAi.x, mobEntityWithAi.y, mobEntityWithAi.z).subtract(vec3d);
		return method_6376(mobEntityWithAi, i, j, vec3d2);
	}

	@Nullable
	private static Vec3d method_6376(MobEntityWithAi mobEntityWithAi, int i, int j, @Nullable Vec3d vec3d) {
		return method_6371(mobEntityWithAi, i, j, vec3d, true, (float) (Math.PI / 2));
	}

	@Nullable
	private static Vec3d method_6371(MobEntityWithAi mobEntityWithAi, int i, int j, @Nullable Vec3d vec3d, boolean bl, double d) {
		EntityNavigation entityNavigation = mobEntityWithAi.getNavigation();
		Random random = mobEntityWithAi.getRand();
		boolean bl2;
		if (mobEntityWithAi.hasLimitedAiRange()) {
			double e = mobEntityWithAi.getAiHome()
					.squaredDistanceTo((double)MathHelper.floor(mobEntityWithAi.x), (double)MathHelper.floor(mobEntityWithAi.y), (double)MathHelper.floor(mobEntityWithAi.z))
				+ 4.0;
			double f = (double)(mobEntityWithAi.getAiRadius() + (float)i);
			bl2 = e < f * f;
		} else {
			bl2 = false;
		}

		boolean bl3 = false;
		float g = -99999.0F;
		int k = 0;
		int l = 0;
		int m = 0;

		for (int n = 0; n < 10; n++) {
			BlockPos blockPos = method_6374(random, i, j, vec3d, d);
			if (blockPos != null) {
				int o = blockPos.getX();
				int p = blockPos.getY();
				int q = blockPos.getZ();
				if (mobEntityWithAi.hasLimitedAiRange() && i > 1) {
					BlockPos blockPos2 = mobEntityWithAi.getAiHome();
					if (mobEntityWithAi.x > (double)blockPos2.getX()) {
						o -= random.nextInt(i / 2);
					} else {
						o += random.nextInt(i / 2);
					}

					if (mobEntityWithAi.z > (double)blockPos2.getZ()) {
						q -= random.nextInt(i / 2);
					} else {
						q += random.nextInt(i / 2);
					}
				}

				BlockPos blockPos2x = new BlockPos((double)o + mobEntityWithAi.x, (double)p + mobEntityWithAi.y, (double)q + mobEntityWithAi.z);
				if ((!bl2 || mobEntityWithAi.isInAiRange(blockPos2x)) && entityNavigation.isValidPosition(blockPos2x)) {
					if (!bl) {
						blockPos2x = method_6372(blockPos2x, mobEntityWithAi);
						if (method_6380(blockPos2x, mobEntityWithAi)) {
							continue;
						}
					}

					float h = mobEntityWithAi.method_6149(blockPos2x);
					if (h > g) {
						g = h;
						k = o;
						l = p;
						m = q;
						bl3 = true;
					}
				}
			}
		}

		return bl3 ? new Vec3d((double)k + mobEntityWithAi.x, (double)l + mobEntityWithAi.y, (double)m + mobEntityWithAi.z) : null;
	}

	@Nullable
	private static BlockPos method_6374(Random random, int i, int j, @Nullable Vec3d vec3d, double d) {
		if (vec3d != null && !(d >= Math.PI)) {
			double e = MathHelper.atan2(vec3d.z, vec3d.x) - (float) (Math.PI / 2);
			double f = e + (double)(2.0F * random.nextFloat() - 1.0F) * d;
			double g = Math.sqrt(random.nextDouble()) * (double)MathHelper.SQUARE_ROOT_OF_TWO * (double)i;
			double h = -g * Math.sin(f);
			double n = g * Math.cos(f);
			if (!(Math.abs(h) > (double)i) && !(Math.abs(n) > (double)i)) {
				int o = random.nextInt(2 * j + 1) - j;
				return new BlockPos(h, (double)o, n);
			} else {
				return null;
			}
		} else {
			int k = random.nextInt(2 * i + 1) - i;
			int l = random.nextInt(2 * j + 1) - j;
			int m = random.nextInt(2 * i + 1) - i;
			return new BlockPos(k, l, m);
		}
	}

	private static BlockPos method_6372(BlockPos blockPos, MobEntityWithAi mobEntityWithAi) {
		if (!mobEntityWithAi.world.getBlockState(blockPos).getMaterial().method_15799()) {
			return blockPos;
		} else {
			BlockPos blockPos2 = blockPos.up();

			while (blockPos2.getY() < mobEntityWithAi.world.getHeight() && mobEntityWithAi.world.getBlockState(blockPos2).getMaterial().method_15799()) {
				blockPos2 = blockPos2.up();
			}

			return blockPos2;
		}
	}

	private static boolean method_6380(BlockPos blockPos, MobEntityWithAi mobEntityWithAi) {
		return mobEntityWithAi.world.getFluidState(blockPos).matches(FluidTags.field_15517);
	}
}
