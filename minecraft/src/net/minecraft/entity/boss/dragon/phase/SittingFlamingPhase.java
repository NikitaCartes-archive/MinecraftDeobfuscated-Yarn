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
	private int field_7052;
	private AreaEffectCloudEntity field_7051;

	public SittingFlamingPhase(EnderDragonEntity enderDragonEntity) {
		super(enderDragonEntity);
	}

	@Override
	public void clientTick() {
		this.ticks++;
		if (this.ticks % 2 == 0 && this.ticks < 10) {
			Vec3d vec3d = this.dragon.method_6834(1.0F).normalize();
			vec3d.rotateY((float) (-Math.PI / 4));
			double d = this.dragon.partHead.x;
			double e = this.dragon.partHead.y + (double)(this.dragon.partHead.getHeight() / 2.0F);
			double f = this.dragon.partHead.z;

			for (int i = 0; i < 8; i++) {
				double g = d + this.dragon.getRand().nextGaussian() / 2.0;
				double h = e + this.dragon.getRand().nextGaussian() / 2.0;
				double j = f + this.dragon.getRand().nextGaussian() / 2.0;

				for (int k = 0; k < 6; k++) {
					this.dragon.world.addParticle(ParticleTypes.field_11216, g, h, j, -vec3d.x * 0.08F * (double)k, -vec3d.y * 0.6F, -vec3d.z * 0.08F * (double)k);
				}

				vec3d.rotateY((float) (Math.PI / 16));
			}
		}
	}

	@Override
	public void serverTick() {
		this.ticks++;
		if (this.ticks >= 200) {
			if (this.field_7052 >= 4) {
				this.dragon.getPhaseManager().setPhase(PhaseType.field_7077);
			} else {
				this.dragon.getPhaseManager().setPhase(PhaseType.field_7081);
			}
		} else if (this.ticks == 10) {
			Vec3d vec3d = new Vec3d(this.dragon.partHead.x - this.dragon.x, 0.0, this.dragon.partHead.z - this.dragon.z).normalize();
			float f = 5.0F;
			double d = this.dragon.partHead.x + vec3d.x * 5.0 / 2.0;
			double e = this.dragon.partHead.z + vec3d.z * 5.0 / 2.0;
			double g = this.dragon.partHead.y + (double)(this.dragon.partHead.getHeight() / 2.0F);
			BlockPos.Mutable mutable = new BlockPos.Mutable(d, g, e);

			while (this.dragon.world.isAir(mutable)) {
				mutable.set(d, --g, e);
			}

			g = (double)(MathHelper.floor(g) + 1);
			this.field_7051 = new AreaEffectCloudEntity(this.dragon.world, d, g, e);
			this.field_7051.setOwner(this.dragon);
			this.field_7051.setRadius(5.0F);
			this.field_7051.setDuration(200);
			this.field_7051.setParticleType(ParticleTypes.field_11216);
			this.field_7051.addEffect(new StatusEffectInstance(StatusEffects.field_5921));
			this.dragon.world.spawnEntity(this.field_7051);
		}
	}

	@Override
	public void beginPhase() {
		this.ticks = 0;
		this.field_7052++;
	}

	@Override
	public void endPhase() {
		if (this.field_7051 != null) {
			this.field_7051.remove();
			this.field_7051 = null;
		}
	}

	@Override
	public PhaseType<SittingFlamingPhase> getType() {
		return PhaseType.field_7072;
	}

	public void method_6857() {
		this.field_7052 = 0;
	}
}
