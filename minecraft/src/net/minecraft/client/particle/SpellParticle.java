package net.minecraft.client.particle;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4002;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class SpellParticle extends SpriteBillboardParticle {
	private static final Random RANDOM = new Random();
	private final class_4002 field_17870;

	private SpellParticle(World world, double d, double e, double f, double g, double h, double i, class_4002 arg) {
		super(world, d, e, f, 0.5 - RANDOM.nextDouble(), h, 0.5 - RANDOM.nextDouble());
		this.field_17870 = arg;
		this.velocityY *= 0.2F;
		if (g == 0.0 && i == 0.0) {
			this.velocityX *= 0.1F;
			this.velocityZ *= 0.1F;
		}

		this.scale *= 0.75F;
		this.maxAge = (int)(8.0 / (Math.random() * 0.8 + 0.2));
		this.collidesWithWorld = false;
		this.method_18142(arg);
	}

	@Override
	public ParticleTextureSheet getTextureSheet() {
		return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
	}

	@Override
	public void update() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		if (this.age++ >= this.maxAge) {
			this.markDead();
		} else {
			this.method_18142(this.field_17870);
			this.velocityY += 0.004;
			this.move(this.velocityX, this.velocityY, this.velocityZ);
			if (this.posY == this.prevPosY) {
				this.velocityX *= 1.1;
				this.velocityZ *= 1.1;
			}

			this.velocityX *= 0.96F;
			this.velocityY *= 0.96F;
			this.velocityZ *= 0.96F;
			if (this.onGround) {
				this.velocityX *= 0.7F;
				this.velocityZ *= 0.7F;
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static class DefaultFactory implements ParticleFactory<DefaultParticleType> {
		private final class_4002 field_17874;

		public DefaultFactory(class_4002 arg) {
			this.field_17874 = arg;
		}

		public Particle method_3099(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			return new SpellParticle(world, d, e, f, g, h, i, this.field_17874);
		}
	}

	@Environment(EnvType.CLIENT)
	public static class EntityAmbientFactory implements ParticleFactory<DefaultParticleType> {
		private final class_4002 field_17871;

		public EntityAmbientFactory(class_4002 arg) {
			this.field_17871 = arg;
		}

		public Particle method_3096(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			Particle particle = new SpellParticle(world, d, e, f, g, h, i, this.field_17871);
			particle.setColorAlpha(0.15F);
			particle.setColor((float)g, (float)h, (float)i);
			return particle;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class EntityFactory implements ParticleFactory<DefaultParticleType> {
		private final class_4002 field_17873;

		public EntityFactory(class_4002 arg) {
			this.field_17873 = arg;
		}

		public Particle method_3098(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			Particle particle = new SpellParticle(world, d, e, f, g, h, i, this.field_17873);
			particle.setColor((float)g, (float)h, (float)i);
			return particle;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class InstantFactory implements ParticleFactory<DefaultParticleType> {
		private final class_4002 field_17872;

		public InstantFactory(class_4002 arg) {
			this.field_17872 = arg;
		}

		public Particle method_3097(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			return new SpellParticle(world, d, e, f, g, h, i, this.field_17872);
		}
	}

	@Environment(EnvType.CLIENT)
	public static class WitchFactory implements ParticleFactory<DefaultParticleType> {
		private final class_4002 field_17875;

		public WitchFactory(class_4002 arg) {
			this.field_17875 = arg;
		}

		public Particle method_3100(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			SpellParticle spellParticle = new SpellParticle(world, d, e, f, g, h, i, this.field_17875);
			float j = world.random.nextFloat() * 0.5F + 0.35F;
			spellParticle.setColor(1.0F * j, 0.0F * j, 1.0F * j);
			return spellParticle;
		}
	}
}
