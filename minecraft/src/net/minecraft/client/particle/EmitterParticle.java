package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class EmitterParticle extends NoRenderParticle {
	private final Entity entity;
	private int emitterAge;
	private final int maxEmitterAge;
	private final ParticleEffect parameters;

	public EmitterParticle(ClientWorld world, Entity entity, ParticleEffect parameters) {
		this(world, entity, parameters, 3);
	}

	public EmitterParticle(ClientWorld world, Entity entity, ParticleEffect parameters, int maxEmitterAge) {
		this(world, entity, parameters, maxEmitterAge, entity.getVelocity());
	}

	private EmitterParticle(ClientWorld world, Entity entity, ParticleEffect parameters, int maxEmitterAge, Vec3d velocity) {
		super(world, entity.getX(), entity.getBodyY(0.5), entity.getZ(), velocity.x, velocity.y, velocity.z);
		this.entity = entity;
		this.maxEmitterAge = maxEmitterAge;
		this.parameters = parameters;
		this.tick();
	}

	@Override
	public void tick() {
		for (int i = 0; i < 16; i++) {
			double d = (double)(this.random.nextFloat() * 2.0F - 1.0F);
			double e = (double)(this.random.nextFloat() * 2.0F - 1.0F);
			double f = (double)(this.random.nextFloat() * 2.0F - 1.0F);
			if (!(d * d + e * e + f * f > 1.0)) {
				double g = this.entity.offsetX(d / 4.0);
				double h = this.entity.getBodyY(0.5 + e / 4.0);
				double j = this.entity.offsetZ(f / 4.0);
				this.world.addParticle(this.parameters, false, g, h, j, d, e + 0.2, f);
			}
		}

		this.emitterAge++;
		if (this.emitterAge >= this.maxEmitterAge) {
			this.markDead();
		}
	}
}
