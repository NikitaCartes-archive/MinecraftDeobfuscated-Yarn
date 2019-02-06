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
public class FireSmokeLargeParticle extends FireSmokeParticle {
	protected FireSmokeLargeParticle(World world, double d, double e, double f, double g, double h, double i, class_4002 arg) {
		super(world, d, e, f, g, h, i, 2.5F, arg);
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<DefaultParticleType> {
		private final class_4002 field_17817;

		public Factory(class_4001 arg) {
			this.field_17817 = arg.register(Lists.<Identifier>reverse(class_4000.field_17850));
		}

		public Particle method_3040(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			return new FireSmokeLargeParticle(world, d, e, f, g, h, i, this.field_17817);
		}
	}
}
