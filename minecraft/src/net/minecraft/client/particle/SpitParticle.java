package net.minecraft.client.particle;

import com.google.common.collect.Lists;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4000;
import net.minecraft.class_4001;
import net.minecraft.class_4002;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class SpitParticle extends ExplosionSmokeParticle {
	private SpitParticle(World world, double d, double e, double f, double g, double h, double i, class_4002 arg) {
		super(world, d, e, f, g, h, i, arg);
		this.gravityStrength = 0.5F;
	}

	@Override
	public void update() {
		super.update();
		this.velocityY = this.velocityY - (0.004 + 0.04 * (double)this.gravityStrength);
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<DefaultParticleType> {
		private final class_4002 field_17876;

		public Factory(class_4001 arg) {
			this.field_17876 = arg.register(Lists.<Identifier>reverse(class_4000.field_17850));
		}

		public Particle method_3103(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			return new SpitParticle(world, d, e, f, g, h, i, this.field_17876);
		}
	}
}
