package net.minecraft.entity.boss.dragon.phase;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class SittingScanningPhase extends AbstractSittingPhase {
	private int field_7050;

	public SittingScanningPhase(EnderDragonEntity enderDragonEntity) {
		super(enderDragonEntity);
	}

	@Override
	public void method_6855() {
		this.field_7050++;
		LivingEntity livingEntity = this.dragon.world.findMobAttackTarget(this.dragon, 20.0, 10.0);
		if (livingEntity != null) {
			if (this.field_7050 > 25) {
				this.dragon.getPhaseManager().setPhase(PhaseType.SITTING_ATTACKING);
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
		} else if (this.field_7050 >= 100) {
			livingEntity = this.dragon.world.findMobAttackTarget(this.dragon, 150.0, 150.0);
			this.dragon.getPhaseManager().setPhase(PhaseType.TAKEOFF);
			if (livingEntity != null) {
				this.dragon.getPhaseManager().setPhase(PhaseType.CHARGING_PLAYER);
				this.dragon.getPhaseManager().create(PhaseType.CHARGING_PLAYER).setTarget(new Vec3d(livingEntity.x, livingEntity.y, livingEntity.z));
			}
		}
	}

	@Override
	public void beginPhase() {
		this.field_7050 = 0;
	}

	@Override
	public PhaseType<SittingScanningPhase> getType() {
		return PhaseType.SITTING_SCANNING;
	}
}
