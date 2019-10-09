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

	public EmitterParticle(World world, Entity entity, ParticleEffect particleEffect) {
		this(world, entity, particleEffect, 3);
	}

	public EmitterParticle(World world, Entity entity, ParticleEffect particleEffect, int i) {
		this(world, entity, particleEffect, i, entity.getVelocity());
	}

	private EmitterParticle(World world, Entity entity, ParticleEffect particleEffect, int i, Vec3d vec3d) {
		super(world, entity.getX(), entity.method_23323(0.5), entity.getZ(), vec3d.x, vec3d.y, vec3d.z);
		this.entity = entity;
		this.maxEmitterAge = i;
		this.parameters = particleEffect;
		this.tick();
	}

	@Override
	public void tick() {
		for (int i = 0; i < 16; i++) {
			double d = (double)(this.random.nextFloat() * 2.0F - 1.0F);
			double e = (double)(this.random.nextFloat() * 2.0F - 1.0F);
			double f = (double)(this.random.nextFloat() * 2.0F - 1.0F);
			if (!(d * d + e * e + f * f > 1.0)) {
				double g = this.entity.method_23316(d / 4.0);
				double h = this.entity.method_23323(0.5 + e / 4.0);
				double j = this.entity.method_23324(f / 4.0);
				this.world.addParticle(this.parameters, false, g, h, j, d, e + 0.2, f);
			}
		}

		this.emitterAge++;
		if (this.emitterAge >= this.maxEmitterAge) {
			this.markDead();
		}
	}
}
