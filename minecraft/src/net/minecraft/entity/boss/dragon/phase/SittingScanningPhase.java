package net.minecraft.entity.boss.dragon.phase;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class SittingScanningPhase extends AbstractSittingPhase {
	private static final int DURATION = 100;
	private static final int MAX_HEIGHT_CLOSE_PLAYER_RANGE = 10;
	private static final int MAX_HORIZONTAL_CLOSE_PLAYER_RANGE = 20;
	private static final int MAX_PLAYER_RANGE = 150;
	private static final TargetPredicate PLAYER_WITHIN_RANGE_PREDICATE = TargetPredicate.createAttackable().setBaseMaxDistance(150.0);
	private final TargetPredicate CLOSE_PLAYER_PREDICATE;
	private int ticks;

	public SittingScanningPhase(EnderDragonEntity enderDragonEntity) {
		super(enderDragonEntity);
		this.CLOSE_PLAYER_PREDICATE = TargetPredicate.createAttackable()
			.setBaseMaxDistance(20.0)
			.setPredicate(player -> Math.abs(player.getY() - enderDragonEntity.getY()) <= 10.0);
	}

	@Override
	public void serverTick() {
		this.ticks++;
		LivingEntity livingEntity = this.dragon
			.getWorld()
			.getClosestPlayer(this.CLOSE_PLAYER_PREDICATE, this.dragon, this.dragon.getX(), this.dragon.getY(), this.dragon.getZ());
		if (livingEntity != null) {
			if (this.ticks > 25) {
				this.dragon.getPhaseManager().setPhase(PhaseType.SITTING_ATTACKING);
			} else {
				Vec3d vec3d = new Vec3d(livingEntity.getX() - this.dragon.getX(), 0.0, livingEntity.getZ() - this.dragon.getZ()).normalize();
				Vec3d vec3d2 = new Vec3d(
						(double)MathHelper.sin(this.dragon.getYaw() * (float) (Math.PI / 180.0)),
						0.0,
						(double)(-MathHelper.cos(this.dragon.getYaw() * (float) (Math.PI / 180.0)))
					)
					.normalize();
				float f = (float)vec3d2.dotProduct(vec3d);
				float g = (float)(Math.acos((double)f) * 180.0F / (float)Math.PI) + 0.5F;
				if (g < 0.0F || g > 10.0F) {
					double d = livingEntity.getX() - this.dragon.head.getX();
					double e = livingEntity.getZ() - this.dragon.head.getZ();
					double h = MathHelper.clamp(MathHelper.wrapDegrees(180.0 - MathHelper.atan2(d, e) * 180.0F / (float)Math.PI - (double)this.dragon.getYaw()), -100.0, 100.0);
					this.dragon.yawAcceleration *= 0.8F;
					float i = (float)Math.sqrt(d * d + e * e) + 1.0F;
					float j = i;
					if (i > 40.0F) {
						i = 40.0F;
					}

					this.dragon.yawAcceleration += (float)h * (0.7F / i / j);
					this.dragon.setYaw(this.dragon.getYaw() + this.dragon.yawAcceleration);
				}
			}
		} else if (this.ticks >= 100) {
			livingEntity = this.dragon
				.getWorld()
				.getClosestPlayer(PLAYER_WITHIN_RANGE_PREDICATE, this.dragon, this.dragon.getX(), this.dragon.getY(), this.dragon.getZ());
			this.dragon.getPhaseManager().setPhase(PhaseType.TAKEOFF);
			if (livingEntity != null) {
				this.dragon.getPhaseManager().setPhase(PhaseType.CHARGING_PLAYER);
				this.dragon.getPhaseManager().create(PhaseType.CHARGING_PLAYER).setPathTarget(new Vec3d(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ()));
			}
		}
	}

	@Override
	public void beginPhase() {
		this.ticks = 0;
	}

	@Override
	public PhaseType<SittingScanningPhase> getType() {
		return PhaseType.SITTING_SCANNING;
	}
}
