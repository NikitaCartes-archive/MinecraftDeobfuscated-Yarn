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
public class EndRodParticle extends AnimatedParticle {
	private EndRodParticle(World world, double d, double e, double f, double g, double h, double i, class_4002 arg) {
		super(world, d, e, f, arg, -5.0E-4F);
		this.velocityX = g;
		this.velocityY = h;
		this.velocityZ = i;
		this.field_17867 *= 0.75F;
		this.maxAge = 60 + this.random.nextInt(12);
		this.setTargetColor(15916745);
		this.method_18142(arg);
	}

	@Override
	public void move(double d, double e, double f) {
		this.setBoundingBox(this.getBoundingBox().offset(d, e, f));
		this.repositionFromBoundingBox();
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<DefaultParticleType> {
		private final class_4002 field_17805;

		public Factory(class_4001 arg) {
			this.field_17805 = arg.register(Lists.<Identifier>reverse(class_4000.field_17854));
		}

		public Particle method_3024(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			return new EndRodParticle(world, d, e, f, g, h, i, this.field_17805);
		}
	}
}
