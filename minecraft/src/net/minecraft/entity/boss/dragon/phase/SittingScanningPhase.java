package net.minecraft.entity.boss.dragon.phase;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class SittingScanningPhase extends AbstractSittingPhase {
	private static final TargetPredicate PLAYER_WITHIN_RANGE_PREDICATE = new TargetPredicate().setBaseMaxDistance(150.0);
	private final TargetPredicate CLOSE_PLAYER_PREDICATE;
	private int ticks;

	public SittingScanningPhase(EnderDragonEntity enderDragonEntity) {
		super(enderDragonEntity);
		this.CLOSE_PLAYER_PREDICATE = new TargetPredicate()
			.setBaseMaxDistance(20.0)
			.setPredicate(livingEntity -> Math.abs(livingEntity.y - enderDragonEntity.y) <= 10.0);
	}

	@Override
	public void serverTick() {
		this.ticks++;
		LivingEntity livingEntity = this.dragon.world.getClosestPlayer(this.CLOSE_PLAYER_PREDICATE, this.dragon, this.dragon.x, this.dragon.y, this.dragon.z);
		if (livingEntity != null) {
			if (this.ticks > 25) {
				this.dragon.getPhaseManager().setPhase(PhaseType.field_7073);
			} else {
				Vec3d vec3d = new Vec3d(livingEntity.x - this.dragon.x, 0.0, livingEntity.z - this.dragon.z).normalize();
				Vec3d vec3d2 = new Vec3d(
						(double)MathHelper.sin(this.dragon.yaw * (float) (Math.PI / 180.0)), 0.0, (double)(-MathHelper.cos(this.dragon.yaw * (float) (Math.PI / 180.0)))
					)
					.normalize();
				float f = (float)vec3d2.dotProduct(vec3d);
				float g = (float)(Math.acos((double)f) * 180.0F / (float)Math.PI) + 0.5F;
				if (g < 0.0F || g > 10.0F) {
					double d = livingEntity.x - this.dragon.partHead.x;
					double e = livingEntity.z - this.dragon.partHead.z;
					double h = MathHelper.clamp(MathHelper.wrapDegrees(180.0 - MathHelper.atan2(d, e) * 180.0F / (float)Math.PI - (double)this.dragon.yaw), -100.0, 100.0);
					this.dragon.field_6267 *= 0.8F;
					float i = MathHelper.sqrt(d * d + e * e) + 1.0F;
					float j = i;
					if (i > 40.0F) {
						i = 40.0F;
					}

					this.dragon.field_6267 = (float)((double)this.dragon.field_6267 + h * (double)(0.7F / i / j));
					this.dragon.yaw = this.dragon.yaw + this.dragon.field_6267;
				}
			}
		} else if (this.ticks >= 100) {
			livingEntity = this.dragon.world.getClosestPlayer(PLAYER_WITHIN_RANGE_PREDICATE, this.dragon, this.dragon.x, this.dragon.y, this.dragon.z);
			this.dragon.getPhaseManager().setPhase(PhaseType.field_7077);
			if (livingEntity != null) {
				this.dragon.getPhaseManager().setPhase(PhaseType.field_7078);
				this.dragon.getPhaseManager().create(PhaseType.field_7078).setTarget(new Vec3d(livingEntity.x, livingEntity.y, livingEntity.z));
			}
		}
	}

	@Override
	public void beginPhase() {
		this.ticks = 0;
	}

	@Override
	public PhaseType<SittingScanningPhase> getType() {
		return PhaseType.field_7081;
	}
}
