package net.minecraft.client.particle;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class SpellParticle extends SpriteBillboardParticle {
	private static final Random RANDOM = new Random();
	private final SpriteProvider field_17870;

	private SpellParticle(World world, double d, double e, double f, double g, double h, double i, SpriteProvider spriteProvider) {
		super(world, d, e, f, 0.5 - RANDOM.nextDouble(), h, 0.5 - RANDOM.nextDouble());
		this.field_17870 = spriteProvider;
		this.velocityY *= 0.2F;
		if (g == 0.0 && i == 0.0) {
			this.velocityX *= 0.1F;
			this.velocityZ *= 0.1F;
		}

		this.scale *= 0.75F;
		this.maxAge = (int)(8.0 / (Math.random() * 0.8 + 0.2));
		this.collidesWithWorld = false;
		this.method_18142(spriteProvider);
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
		private final SpriteProvider field_17874;

		public DefaultFactory(SpriteProvider spriteProvider) {
			this.field_17874 = spriteProvider;
		}

		public Particle method_3099(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			return new SpellParticle(world, d, e, f, g, h, i, this.field_17874);
		}
	}

	@Environment(EnvType.CLIENT)
	public static class EntityAmbientFactory implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider field_17871;

		public EntityAmbientFactory(SpriteProvider spriteProvider) {
			this.field_17871 = spriteProvider;
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
		private final SpriteProvider field_17873;

		public EntityFactory(SpriteProvider spriteProvider) {
			this.field_17873 = spriteProvider;
		}

		public Particle method_3098(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			Particle particle = new SpellParticle(world, d, e, f, g, h, i, this.field_17873);
			particle.setColor((float)g, (float)h, (float)i);
			return particle;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class InstantFactory implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider field_17872;

		public InstantFactory(SpriteProvider spriteProvider) {
			this.field_17872 = spriteProvider;
		}

		public Particle method_3097(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			return new SpellParticle(world, d, e, f, g, h, i, this.field_17872);
		}
	}

	@Environment(EnvType.CLIENT)
	public static class WitchFactory implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider field_17875;

		public WitchFactory(SpriteProvider spriteProvider) {
			this.field_17875 = spriteProvider;
		}

		public Particle method_3100(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			SpellParticle spellParticle = new SpellParticle(world, d, e, f, g, h, i, this.field_17875);
			float j = world.random.nextFloat() * 0.5F + 0.35F;
			spellParticle.setColor(1.0F * j, 0.0F * j, 1.0F * j);
			return spellParticle;
		}
	}
}
