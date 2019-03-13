package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class EmitterParticle extends NoRenderParticle {
	private final Entity entity;
	private int emitterAge;
	private final int maxEmitterAge;
	private final ParticleParameters field_3893;

	public EmitterParticle(World world, Entity entity, ParticleParameters particleParameters) {
		this(world, entity, particleParameters, 3);
	}

	public EmitterParticle(World world, Entity entity, ParticleParameters particleParameters, int i) {
		this(world, entity, particleParameters, i, entity.method_18798());
	}

	private EmitterParticle(World world, Entity entity, ParticleParameters particleParameters, int i, Vec3d vec3d) {
		super(world, entity.x, entity.method_5829().minY + (double)(entity.getHeight() / 2.0F), entity.z, vec3d.x, vec3d.y, vec3d.z);
		this.entity = entity;
		this.maxEmitterAge = i;
		this.field_3893 = particleParameters;
		this.update();
	}

	@Override
	public void update() {
		for (int i = 0; i < 16; i++) {
			double d = (double)(this.random.nextFloat() * 2.0F - 1.0F);
			double e = (double)(this.random.nextFloat() * 2.0F - 1.0F);
			double f = (double)(this.random.nextFloat() * 2.0F - 1.0F);
			if (!(d * d + e * e + f * f > 1.0)) {
				double g = this.entity.x + d * (double)this.entity.getWidth() / 4.0;
				double h = this.entity.method_5829().minY + (double)(this.entity.getHeight() / 2.0F) + e * (double)this.entity.getHeight() / 4.0;
				double j = this.entity.z + f * (double)this.entity.getWidth() / 4.0;
				this.world.method_8466(this.field_3893, false, g, h, j, d, e + 0.2, f);
			}
		}

		this.emitterAge++;
		if (this.emitterAge >= this.maxEmitterAge) {
			this.markDead();
		}
	}
}
