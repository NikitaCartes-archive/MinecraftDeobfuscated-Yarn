package net.minecraft.entity.boss.dragon.phase;

import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StrafePlayerPhase extends AbstractPhase {
	private static final Logger LOGGER = LogManager.getLogger();
	private int field_7060;
	private Path field_7059;
	private Vec3d field_7057;
	private LivingEntity field_7062;
	private boolean field_7058;

	public StrafePlayerPhase(EnderDragonEntity enderDragonEntity) {
		super(enderDragonEntity);
	}

	@Override
	public void method_6855() {
		if (this.field_7062 == null) {
			LOGGER.warn("Skipping player strafe phase because no player was found");
			this.dragon.method_6831().setPhase(PhaseType.HOLDING_PATTERN);
		} else {
			if (this.field_7059 != null && this.field_7059.isFinished()) {
				double d = this.field_7062.x;
				double e = this.field_7062.z;
				double f = d - this.dragon.x;
				double g = e - this.dragon.z;
				double h = (double)MathHelper.sqrt(f * f + g * g);
				double i = Math.min(0.4F + h / 80.0 - 1.0, 10.0);
				this.field_7057 = new Vec3d(d, this.field_7062.y + i, e);
			}

			double d = this.field_7057 == null ? 0.0 : this.field_7057.squaredDistanceTo(this.dragon.x, this.dragon.y, this.dragon.z);
			if (d < 100.0 || d > 22500.0) {
				this.method_6860();
			}

			double e = 64.0;
			if (this.field_7062.squaredDistanceTo(this.dragon) < 4096.0) {
				if (this.dragon.canSee(this.field_7062)) {
					this.field_7060++;
					Vec3d vec3d = new Vec3d(this.field_7062.x - this.dragon.x, 0.0, this.field_7062.z - this.dragon.z).normalize();
					Vec3d vec3d2 = new Vec3d(
							(double)MathHelper.sin(this.dragon.yaw * (float) (Math.PI / 180.0)), 0.0, (double)(-MathHelper.cos(this.dragon.yaw * (float) (Math.PI / 180.0)))
						)
						.normalize();
					float j = (float)vec3d2.dotProduct(vec3d);
					float k = (float)(Math.acos((double)j) * 180.0F / (float)Math.PI);
					k += 0.5F;
					if (this.field_7060 >= 5 && k >= 0.0F && k < 10.0F) {
						double h = 1.0;
						Vec3d vec3d3 = this.dragon.method_5828(1.0F);
						double l = this.dragon.partHead.x - vec3d3.x * 1.0;
						double m = this.dragon.partHead.y + (double)(this.dragon.partHead.getHeight() / 2.0F) + 0.5;
						double n = this.dragon.partHead.z - vec3d3.z * 1.0;
						double o = this.field_7062.x - l;
						double p = this.field_7062.y + (double)(this.field_7062.getHeight() / 2.0F) - (m + (double)(this.dragon.partHead.getHeight() / 2.0F));
						double q = this.field_7062.z - n;
						this.dragon.field_6002.method_8444(null, 1017, new BlockPos(this.dragon), 0);
						DragonFireballEntity dragonFireballEntity = new DragonFireballEntity(this.dragon.field_6002, this.dragon, o, p, q);
						dragonFireballEntity.setPositionAndAngles(l, m, n, 0.0F, 0.0F);
						this.dragon.field_6002.spawnEntity(dragonFireballEntity);
						this.field_7060 = 0;
						if (this.field_7059 != null) {
							while (!this.field_7059.isFinished()) {
								this.field_7059.next();
							}
						}

						this.dragon.method_6831().setPhase(PhaseType.HOLDING_PATTERN);
					}
				} else if (this.field_7060 > 0) {
					this.field_7060--;
				}
			} else if (this.field_7060 > 0) {
				this.field_7060--;
			}
		}
	}

	private void method_6860() {
		if (this.field_7059 == null || this.field_7059.isFinished()) {
			int i = this.dragon.method_6818();
			int j = i;
			if (this.dragon.getRand().nextInt(8) == 0) {
				this.field_7058 = !this.field_7058;
				j = i + 6;
			}

			if (this.field_7058) {
				j++;
			} else {
				j--;
			}

			if (this.dragon.method_6829() != null && this.dragon.method_6829().getAliveEndCrystals() > 0) {
				j %= 12;
				if (j < 0) {
					j += 12;
				}
			} else {
				j -= 12;
				j &= 7;
				j += 12;
			}

			this.field_7059 = this.dragon.method_6833(i, j, null);
			if (this.field_7059 != null) {
				this.field_7059.next();
			}
		}

		this.method_6861();
	}

	private void method_6861() {
		if (this.field_7059 != null && !this.field_7059.isFinished()) {
			Vec3d vec3d = this.field_7059.method_35();
			this.field_7059.next();
			double d = vec3d.x;
			double e = vec3d.z;

			double f;
			do {
				f = vec3d.y + (double)(this.dragon.getRand().nextFloat() * 20.0F);
			} while (f < vec3d.y);

			this.field_7057 = new Vec3d(d, f, e);
		}
	}

	@Override
	public void beginPhase() {
		this.field_7060 = 0;
		this.field_7057 = null;
		this.field_7059 = null;
		this.field_7062 = null;
	}

	public void method_6862(LivingEntity livingEntity) {
		this.field_7062 = livingEntity;
		int i = this.dragon.method_6818();
		int j = this.dragon.method_6822(this.field_7062.x, this.field_7062.y, this.field_7062.z);
		int k = MathHelper.floor(this.field_7062.x);
		int l = MathHelper.floor(this.field_7062.z);
		double d = (double)k - this.dragon.x;
		double e = (double)l - this.dragon.z;
		double f = (double)MathHelper.sqrt(d * d + e * e);
		double g = Math.min(0.4F + f / 80.0 - 1.0, 10.0);
		int m = MathHelper.floor(this.field_7062.y + g);
		PathNode pathNode = new PathNode(k, m, l);
		this.field_7059 = this.dragon.method_6833(i, j, pathNode);
		if (this.field_7059 != null) {
			this.field_7059.next();
			this.method_6861();
		}
	}

	@Nullable
	@Override
	public Vec3d method_6851() {
		return this.field_7057;
	}

	@Override
	public PhaseType<StrafePlayerPhase> method_6849() {
		return PhaseType.STRAFE_PLAYER;
	}
}
