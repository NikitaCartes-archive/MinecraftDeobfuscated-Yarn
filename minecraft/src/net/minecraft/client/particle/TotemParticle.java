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
public class TotemParticle extends AnimatedParticle {
	private TotemParticle(World world, double d, double e, double f, double g, double h, double i, class_4002 arg) {
		super(world, d, e, f, arg, -0.05F);
		this.velocityX = g;
		this.velocityY = h;
		this.velocityZ = i;
		this.field_17867 *= 0.75F;
		this.maxAge = 60 + this.random.nextInt(12);
		this.method_18142(arg);
		if (this.random.nextInt(4) == 0) {
			this.setColor(0.6F + this.random.nextFloat() * 0.2F, 0.6F + this.random.nextFloat() * 0.3F, this.random.nextFloat() * 0.2F);
		} else {
			this.setColor(0.1F + this.random.nextFloat() * 0.2F, 0.4F + this.random.nextFloat() * 0.3F, this.random.nextFloat() * 0.2F);
		}

		this.method_3091(0.6F);
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<DefaultParticleType> {
		private final class_4002 field_17887;

		public Factory(class_4001 arg) {
			this.field_17887 = arg.register(Lists.<Identifier>reverse(class_4000.field_17854));
		}

		public Particle method_3113(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			return new TotemParticle(world, d, e, f, g, h, i, this.field_17887);
		}
	}
}
