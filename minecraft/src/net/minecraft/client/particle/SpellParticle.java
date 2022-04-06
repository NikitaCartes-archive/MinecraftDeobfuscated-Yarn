package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.AbstractRandom;

@Environment(EnvType.CLIENT)
public class SpellParticle extends SpriteBillboardParticle {
	private static final AbstractRandom RANDOM = AbstractRandom.createAtomic();
	private final SpriteProvider spriteProvider;

	SpellParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteProvider spriteProvider) {
		super(world, x, y, z, 0.5 - RANDOM.nextDouble(), velocityY, 0.5 - RANDOM.nextDouble());
		this.velocityMultiplier = 0.96F;
		this.gravityStrength = -0.1F;
		this.field_28787 = true;
		this.spriteProvider = spriteProvider;
		this.velocityY *= 0.2F;
		if (velocityX == 0.0 && velocityZ == 0.0) {
			this.velocityX *= 0.1F;
			this.velocityZ *= 0.1F;
		}

		this.scale *= 0.75F;
		this.maxAge = (int)(8.0 / (Math.random() * 0.8 + 0.2));
		this.collidesWithWorld = false;
		this.setSpriteForAge(spriteProvider);
		if (this.isInvisible()) {
			this.setAlpha(0.0F);
		}
	}

	@Override
	public ParticleTextureSheet getType() {
		return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
	}

	@Override
	public void tick() {
		super.tick();
		this.setSpriteForAge(this.spriteProvider);
		if (this.isInvisible()) {
			this.setAlpha(0.0F);
		} else {
			this.setAlpha(MathHelper.lerp(0.05F, this.alpha, 1.0F));
		}
	}

	private boolean isInvisible() {
		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		ClientPlayerEntity clientPlayerEntity = minecraftClient.player;
		return clientPlayerEntity != null
			&& clientPlayerEntity.getEyePos().squaredDistanceTo(this.x, this.y, this.z) <= 9.0
			&& minecraftClient.options.getPerspective().isFirstPerson()
			&& clientPlayerEntity.isUsingSpyglass();
	}

	@Environment(EnvType.CLIENT)
	public static class DefaultFactory implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider spriteProvider;

		public DefaultFactory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
			return new SpellParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider);
		}
	}

	@Environment(EnvType.CLIENT)
	public static class EntityAmbientFactory implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider spriteProvider;

		public EntityAmbientFactory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
			Particle particle = new SpellParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider);
			particle.setAlpha(0.15F);
			particle.setColor((float)g, (float)h, (float)i);
			return particle;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class EntityFactory implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider spriteProvider;

		public EntityFactory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
			Particle particle = new SpellParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider);
			particle.setColor((float)g, (float)h, (float)i);
			return particle;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class InstantFactory implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider spriteProvider;

		public InstantFactory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
			return new SpellParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider);
		}
	}

	@Environment(EnvType.CLIENT)
	public static class WitchFactory implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider spriteProvider;

		public WitchFactory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
			SpellParticle spellParticle = new SpellParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider);
			float j = clientWorld.random.nextFloat() * 0.5F + 0.35F;
			spellParticle.setColor(1.0F * j, 0.0F * j, 1.0F * j);
			return spellParticle;
		}
	}
}
