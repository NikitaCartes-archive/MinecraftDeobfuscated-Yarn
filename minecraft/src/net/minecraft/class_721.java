package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.ExplosionSmokeParticle;
import net.minecraft.client.particle.FactoryParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.particle.TexturedParticle;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class class_721 extends ExplosionSmokeParticle {
	protected class_721(World world, double d, double e, double f, double g, double h, double i) {
		super(world, d, e, f, g, h, i);
		this.gravityStrength = 0.5F;
	}

	@Override
	public void update() {
		super.update();
		this.velocityY = this.velocityY - (0.004 + 0.04 * (double)this.gravityStrength);
	}

	@Environment(EnvType.CLIENT)
	public static class class_722 implements FactoryParticle<TexturedParticle> {
		public Particle createParticle(TexturedParticle texturedParticle, World world, double d, double e, double f, double g, double h, double i) {
			return new class_721(world, d, e, f, g, h, i);
		}
	}
}
