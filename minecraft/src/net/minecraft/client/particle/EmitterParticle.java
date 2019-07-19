package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class EmitterParticle extends NoRenderParticle {
	private final Entity entity;
	private int emitterAge;
	private final int maxEmitterAge;
	private final ParticleEffect parameters;

	public EmitterParticle(World world, Entity entity, ParticleEffect parameters) {
		this(world, entity, parameters, 3);
	}

	public EmitterParticle(World world, Entity entity, ParticleEffect particleEffect, int i) {
		this(world, entity, particleEffect, i, entity.getVelocity());
	}

	private EmitterParticle(World world, Entity entity, ParticleEffect parameters, int maxEmitterAge, Vec3d vec3d) {
		super(world, entity.x, entity.getBoundingBox().y1 + (double)(entity.getHeight() / 2.0F), entity.z, vec3d.x, vec3d.y, vec3d.z);
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
				double g = this.entity.x + d * (double)this.entity.getWidth() / 4.0;
				double h = this.entity.getBoundingBox().y1 + (double)(this.entity.getHeight() / 2.0F) + e * (double)this.entity.getHeight() / 4.0;
				double j = this.entity.z + f * (double)this.entity.getWidth() / 4.0;
				this.world.addParticle(this.parameters, false, g, h, j, d, e + 0.2, f);
			}
		}

		this.emitterAge++;
		if (this.emitterAge >= this.maxEmitterAge) {
			this.markDead();
		}
	}
}
