package net.minecraft.client.item;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class CompassAnglePredicateProvider implements UnclampedModelPredicateProvider {
	public static final int field_38798 = 0;
	private final CompassAnglePredicateProvider.AngleInterpolator aimedInterpolator = new CompassAnglePredicateProvider.AngleInterpolator();
	private final CompassAnglePredicateProvider.AngleInterpolator aimlessInterpolator = new CompassAnglePredicateProvider.AngleInterpolator();
	public final CompassAnglePredicateProvider.CompassTarget compassTarget;

	public CompassAnglePredicateProvider(CompassAnglePredicateProvider.CompassTarget compassTarget) {
		this.compassTarget = compassTarget;
	}

	@Override
	public float unclampedCall(ItemStack itemStack, @Nullable ClientWorld clientWorld, @Nullable LivingEntity livingEntity, int i) {
		Entity entity = (Entity)(livingEntity != null ? livingEntity : itemStack.getHolder());
		if (entity == null) {
			return 0.0F;
		} else {
			clientWorld = this.getClientWorld(entity, clientWorld);
			return clientWorld == null ? 0.0F : this.getAngle(itemStack, clientWorld, i, entity);
		}
	}

	private float getAngle(ItemStack stack, ClientWorld world, int seed, Entity entity) {
		GlobalPos globalPos = this.compassTarget.getPos(world, stack, entity);
		long l = world.getTime();
		return !this.canPointTo(entity, globalPos) ? this.getAimlessAngle(seed, l) : this.getAngleTo(entity, l, globalPos.getPos());
	}

	private float getAimlessAngle(int seed, long time) {
		if (this.aimlessInterpolator.shouldUpdate(time)) {
			this.aimlessInterpolator.update(time, Math.random());
		}

		double d = this.aimlessInterpolator.value + (double)((float)this.scatter(seed) / 2.1474836E9F);
		return MathHelper.floorMod((float)d, 1.0F);
	}

	private float getAngleTo(Entity entity, long time, BlockPos pos) {
		double d = this.getAngleTo(entity, pos);
		double e = this.getBodyYaw(entity);
		if (entity instanceof PlayerEntity playerEntity && playerEntity.isMainPlayer()) {
			if (this.aimedInterpolator.shouldUpdate(time)) {
				this.aimedInterpolator.update(time, 0.5 - (e - 0.25));
			}

			double f = d + this.aimedInterpolator.value;
			return MathHelper.floorMod((float)f, 1.0F);
		}

		double f = 0.5 - (e - 0.25 - d);
		return MathHelper.floorMod((float)f, 1.0F);
	}

	@Nullable
	private ClientWorld getClientWorld(Entity entity, @Nullable ClientWorld world) {
		return world == null && entity.world instanceof ClientWorld ? (ClientWorld)entity.world : world;
	}

	private boolean canPointTo(Entity entity, @Nullable GlobalPos pos) {
		return pos != null && pos.getDimension() == entity.world.getRegistryKey() && !(pos.getPos().getSquaredDistance(entity.getPos()) < 1.0E-5F);
	}

	private double getAngleTo(Entity entity, BlockPos pos) {
		Vec3d vec3d = Vec3d.ofCenter(pos);
		return Math.atan2(vec3d.getZ() - entity.getZ(), vec3d.getX() - entity.getX()) / (float) (Math.PI * 2);
	}

	private double getBodyYaw(Entity entity) {
		return MathHelper.floorMod((double)(entity.getBodyYaw() / 360.0F), 1.0);
	}

	/**
	 * Scatters a seed by integer overflow in multiplication onto the whole
	 * int domain.
	 */
	private int scatter(int seed) {
		return seed * 1327217883;
	}

	@Environment(EnvType.CLIENT)
	static class AngleInterpolator {
		double value;
		private double speed;
		private long lastUpdateTime;

		boolean shouldUpdate(long time) {
			return this.lastUpdateTime != time;
		}

		void update(long time, double target) {
			this.lastUpdateTime = time;
			double d = target - this.value;
			d = MathHelper.floorMod(d + 0.5, 1.0) - 0.5;
			this.speed += d * 0.1;
			this.speed *= 0.8;
			this.value = MathHelper.floorMod(this.value + this.speed, 1.0);
		}
	}

	@Environment(EnvType.CLIENT)
	public interface CompassTarget {
		@Nullable
		GlobalPos getPos(ClientWorld world, ItemStack stack, Entity entity);
	}
}
