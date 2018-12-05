package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.render.VertexBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class class_733 extends Particle {
	private final Entity field_3894;
	private int field_3896;
	private final int field_3895;
	private final net.minecraft.particle.Particle field_3893;

	public class_733(World world, Entity entity, net.minecraft.particle.Particle particle) {
		this(world, entity, particle, 3);
	}

	public class_733(World world, Entity entity, net.minecraft.particle.Particle particle, int i) {
		super(world, entity.x, entity.getBoundingBox().minY + (double)(entity.height / 2.0F), entity.z, entity.velocityX, entity.velocityY, entity.velocityZ);
		this.field_3894 = entity;
		this.field_3895 = i;
		this.field_3893 = particle;
		this.update();
	}

	@Override
	public void buildGeometry(VertexBuffer vertexBuffer, Entity entity, float f, float g, float h, float i, float j, float k) {
	}

	@Override
	public void update() {
		for (int i = 0; i < 16; i++) {
			double d = (double)(this.random.nextFloat() * 2.0F - 1.0F);
			double e = (double)(this.random.nextFloat() * 2.0F - 1.0F);
			double f = (double)(this.random.nextFloat() * 2.0F - 1.0F);
			if (!(d * d + e * e + f * f > 1.0)) {
				double g = this.field_3894.x + d * (double)this.field_3894.width / 4.0;
				double h = this.field_3894.getBoundingBox().minY + (double)(this.field_3894.height / 2.0F) + e * (double)this.field_3894.height / 4.0;
				double j = this.field_3894.z + f * (double)this.field_3894.width / 4.0;
				this.world.method_8466(this.field_3893, false, g, h, j, d, e + 0.2, f);
			}
		}

		this.field_3896++;
		if (this.field_3896 >= this.field_3895) {
			this.markDead();
		}
	}

	@Override
	public int getParticleGroup() {
		return 3;
	}
}
