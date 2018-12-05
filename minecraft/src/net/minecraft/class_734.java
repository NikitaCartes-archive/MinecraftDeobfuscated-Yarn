package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.AnimatedParticle;
import net.minecraft.client.particle.FactoryParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.particle.TexturedParticle;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class class_734 extends AnimatedParticle {
	public class_734(World world, double d, double e, double f, double g, double h, double i) {
		super(world, d, e, f, 176, 8, -0.05F);
		this.velocityX = g;
		this.velocityY = h;
		this.velocityZ = i;
		this.size *= 0.75F;
		this.maxAge = 60 + this.random.nextInt(12);
		if (this.random.nextInt(4) == 0) {
			this.setColor(0.6F + this.random.nextFloat() * 0.2F, 0.6F + this.random.nextFloat() * 0.3F, this.random.nextFloat() * 0.2F);
		} else {
			this.setColor(0.1F + this.random.nextFloat() * 0.2F, 0.4F + this.random.nextFloat() * 0.3F, this.random.nextFloat() * 0.2F);
		}

		this.method_3091(0.6F);
	}

	@Environment(EnvType.CLIENT)
	public static class class_735 implements FactoryParticle<TexturedParticle> {
		public Particle createParticle(TexturedParticle texturedParticle, World world, double d, double e, double f, double g, double h, double i) {
			return new class_734(world, d, e, f, g, h, i);
		}
	}
}
