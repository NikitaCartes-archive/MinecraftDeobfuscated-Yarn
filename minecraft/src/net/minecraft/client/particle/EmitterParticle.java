package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class EmitterParticle extends Particle {
	private final Entity entity;
	private int emitterAge;
	private final int maxEmitterAge;
	private final ParticleParameters field_3893;

	public EmitterParticle(World world, Entity entity, ParticleParameters particleParameters) {
		this(world, entity, particleParameters, 3);
	}

	public EmitterParticle(World world, Entity entity, ParticleParameters particleParameters, int i) {
		super(world, entity.x, entity.getBoundingBox().minY + (double)(entity.height / 2.0F), entity.z, entity.velocityX, entity.velocityY, entity.velocityZ);
		this.entity = entity;
		this.maxEmitterAge = i;
		this.field_3893 = particleParameters;
		this.update();
	}

	@Override
	public void buildGeometry(BufferBuilder bufferBuilder, Entity entity, float f, float g, float h, float i, float j, float k) {
	}

	@Override
	public void update() {
		for (int i = 0; i < 16; i++) {
			double d = (double)(this.random.nextFloat() * 2.0F - 1.0F);
			double e = (double)(this.random.nextFloat() * 2.0F - 1.0F);
			double f = (double)(this.random.nextFloat() * 2.0F - 1.0F);
			if (!(d * d + e * e + f * f > 1.0)) {
				double g = this.entity.x + d * (double)this.entity.width / 4.0;
				double h = this.entity.getBoundingBox().minY + (double)(this.entity.height / 2.0F) + e * (double)this.entity.height / 4.0;
				double j = this.entity.z + f * (double)this.entity.width / 4.0;
				this.world.method_8466(this.field_3893, false, g, h, j, d, e + 0.2, f);
			}
		}

		this.emitterAge++;
		if (this.emitterAge >= this.maxEmitterAge) {
			this.markDead();
		}
	}

	@Override
	public int getParticleGroup() {
		return 3;
	}
}
