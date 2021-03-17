package net.minecraft.entity.boss.dragon.phase;

import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class SittingFlamingPhase extends AbstractSittingPhase {
	private int ticks;
	private int timesRun;
	private AreaEffectCloudEntity dragonBreathEntity;

	public SittingFlamingPhase(EnderDragonEntity enderDragonEntity) {
		super(enderDragonEntity);
	}

	@Override
	public void clientTick() {
		this.ticks++;
		if (this.ticks % 2 == 0 && this.ticks < 10) {
			Vec3d vec3d = this.dragon.getRotationVectorFromPhase(1.0F).normalize();
			vec3d.rotateY((float) (-Math.PI / 4));
			double d = this.dragon.partHead.getX();
			double e = this.dragon.partHead.getBodyY(0.5);
			double f = this.dragon.partHead.getZ();

			for (int i = 0; i < 8; i++) {
				double g = d + this.dragon.getRandom().nextGaussian() / 2.0;
				double h = e + this.dragon.getRandom().nextGaussian() / 2.0;
				double j = f + this.dragon.getRandom().nextGaussian() / 2.0;

				for (int k = 0; k < 6; k++) {
					this.dragon.world.addParticle(ParticleTypes.DRAGON_BREATH, g, h, j, -vec3d.x * 0.08F * (double)k, -vec3d.y * 0.6F, -vec3d.z * 0.08F * (double)k);
				}

				vec3d.rotateY((float) (Math.PI / 16));
			}
		}
	}

	@Override
	public void serverTick() {
		this.ticks++;
		if (this.ticks >= 200) {
			if (this.timesRun >= 4) {
				this.dragon.getPhaseManager().setPhase(PhaseType.TAKEOFF);
			} else {
				this.dragon.getPhaseManager().setPhase(PhaseType.SITTING_SCANNING);
			}
		} else if (this.ticks == 10) {
			Vec3d vec3d = new Vec3d(this.dragon.partHead.getX() - this.dragon.getX(), 0.0, this.dragon.partHead.getZ() - this.dragon.getZ()).normalize();
			float f = 5.0F;
			double d = this.dragon.partHead.getX() + vec3d.x * 5.0 / 2.0;
			double e = this.dragon.partHead.getZ() + vec3d.z * 5.0 / 2.0;
			double g = this.dragon.partHead.getBodyY(0.5);
			double h = g;
			BlockPos.Mutable mutable = new BlockPos.Mutable(d, g, e);

			while (this.dragon.world.isAir(mutable)) {
				if (--h < 0.0) {
					h = g;
					break;
				}

				mutable.set(d, h, e);
			}

			h = (double)(MathHelper.floor(h) + 1);
			this.dragonBreathEntity = new AreaEffectCloudEntity(this.dragon.world, d, h, e);
			this.dragonBreathEntity.setOwner(this.dragon);
			this.dragonBreathEntity.setRadius(5.0F);
			this.dragonBreathEntity.setDuration(200);
			this.dragonBreathEntity.setParticleType(ParticleTypes.DRAGON_BREATH);
			this.dragonBreathEntity.addEffect(new StatusEffectInstance(StatusEffects.INSTANT_DAMAGE));
			this.dragon.world.spawnEntity(this.dragonBreathEntity);
		}
	}

	@Override
	public void beginPhase() {
		this.ticks = 0;
		this.timesRun++;
	}

	@Override
	public void endPhase() {
		if (this.dragonBreathEntity != null) {
			this.dragonBreathEntity.discard();
			this.dragonBreathEntity = null;
		}
	}

	@Override
	public PhaseType<SittingFlamingPhase> getType() {
		return PhaseType.SITTING_FLAMING;
	}

	public void reset() {
		this.timesRun = 0;
	}
}
