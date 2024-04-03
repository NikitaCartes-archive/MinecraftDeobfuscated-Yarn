package net.minecraft.client.particle;

import it.unimi.dsi.fastutil.ints.IntList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.type.FireworkExplosionComponent;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Util;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class FireworksSparkParticle {
	@Environment(EnvType.CLIENT)
	static class Explosion extends AnimatedParticle {
		private boolean trail;
		private boolean flicker;
		private final ParticleManager particleManager;
		private float field_3801;
		private float field_3800;
		private float field_3799;
		private boolean field_3802;

		Explosion(
			ClientWorld world,
			double x,
			double y,
			double z,
			double velocityX,
			double velocityY,
			double velocityZ,
			ParticleManager particleManager,
			SpriteProvider spriteProvider
		) {
			super(world, x, y, z, spriteProvider, 0.1F);
			this.velocityX = velocityX;
			this.velocityY = velocityY;
			this.velocityZ = velocityZ;
			this.particleManager = particleManager;
			this.scale *= 0.75F;
			this.maxAge = 48 + this.random.nextInt(12);
			this.setSpriteForAge(spriteProvider);
		}

		public void setTrail(boolean trail) {
			this.trail = trail;
		}

		public void setFlicker(boolean flicker) {
			this.flicker = flicker;
		}

		@Override
		public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
			if (!this.flicker || this.age < this.maxAge / 3 || (this.age + this.maxAge) / 3 % 2 == 0) {
				super.buildGeometry(vertexConsumer, camera, tickDelta);
			}
		}

		@Override
		public void tick() {
			super.tick();
			if (this.trail && this.age < this.maxAge / 2 && (this.age + this.maxAge) % 2 == 0) {
				FireworksSparkParticle.Explosion explosion = new FireworksSparkParticle.Explosion(
					this.world, this.x, this.y, this.z, 0.0, 0.0, 0.0, this.particleManager, this.spriteProvider
				);
				explosion.setAlpha(0.99F);
				explosion.setColor(this.red, this.green, this.blue);
				explosion.age = explosion.maxAge / 2;
				if (this.field_3802) {
					explosion.field_3802 = true;
					explosion.field_3801 = this.field_3801;
					explosion.field_3800 = this.field_3800;
					explosion.field_3799 = this.field_3799;
				}

				explosion.flicker = this.flicker;
				this.particleManager.addParticle(explosion);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static class ExplosionFactory implements ParticleFactory<SimpleParticleType> {
		private final SpriteProvider spriteProvider;

		public ExplosionFactory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
			FireworksSparkParticle.Explosion explosion = new FireworksSparkParticle.Explosion(
				clientWorld, d, e, f, g, h, i, MinecraftClient.getInstance().particleManager, this.spriteProvider
			);
			explosion.setAlpha(0.99F);
			return explosion;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class FireworkParticle extends NoRenderParticle {
		private static final double[][] CREEPER_PATTERN = new double[][]{
			{0.0, 0.2}, {0.2, 0.2}, {0.2, 0.6}, {0.6, 0.6}, {0.6, 0.2}, {0.2, 0.2}, {0.2, 0.0}, {0.4, 0.0}, {0.4, -0.6}, {0.2, -0.6}, {0.2, -0.4}, {0.0, -0.4}
		};
		private static final double[][] STAR_PATTERN = new double[][]{
			{0.0, 1.0},
			{0.3455, 0.309},
			{0.9511, 0.309},
			{0.3795918367346939, -0.12653061224489795},
			{0.6122448979591837, -0.8040816326530612},
			{0.0, -0.35918367346938773}
		};
		private int age;
		private final ParticleManager particleManager;
		private final List<FireworkExplosionComponent> explosions;
		private boolean flicker;

		public FireworkParticle(
			ClientWorld world,
			double x,
			double y,
			double z,
			double velocityX,
			double velocityY,
			double velocityZ,
			ParticleManager particleManager,
			List<FireworkExplosionComponent> fireworkExplosions
		) {
			super(world, x, y, z);
			this.velocityX = velocityX;
			this.velocityY = velocityY;
			this.velocityZ = velocityZ;
			this.particleManager = particleManager;
			if (fireworkExplosions.isEmpty()) {
				throw new IllegalArgumentException("Cannot create firework starter with no explosions");
			} else {
				this.explosions = fireworkExplosions;
				this.maxAge = fireworkExplosions.size() * 2 - 1;

				for (FireworkExplosionComponent fireworkExplosionComponent : fireworkExplosions) {
					if (fireworkExplosionComponent.hasTwinkle()) {
						this.flicker = true;
						this.maxAge += 15;
						break;
					}
				}
			}
		}

		@Override
		public void tick() {
			if (this.age == 0) {
				boolean bl = this.isFar();
				boolean bl2 = false;
				if (this.explosions.size() >= 3) {
					bl2 = true;
				} else {
					for (FireworkExplosionComponent fireworkExplosionComponent : this.explosions) {
						if (fireworkExplosionComponent.shape() == FireworkExplosionComponent.Type.LARGE_BALL) {
							bl2 = true;
							break;
						}
					}
				}

				SoundEvent soundEvent;
				if (bl2) {
					soundEvent = bl ? SoundEvents.ENTITY_FIREWORK_ROCKET_LARGE_BLAST_FAR : SoundEvents.ENTITY_FIREWORK_ROCKET_LARGE_BLAST;
				} else {
					soundEvent = bl ? SoundEvents.ENTITY_FIREWORK_ROCKET_BLAST_FAR : SoundEvents.ENTITY_FIREWORK_ROCKET_BLAST;
				}

				this.world.playSound(this.x, this.y, this.z, soundEvent, SoundCategory.AMBIENT, 20.0F, 0.95F + this.random.nextFloat() * 0.1F, true);
			}

			if (this.age % 2 == 0 && this.age / 2 < this.explosions.size()) {
				int i = this.age / 2;
				FireworkExplosionComponent fireworkExplosionComponent2 = (FireworkExplosionComponent)this.explosions.get(i);
				boolean bl3 = fireworkExplosionComponent2.hasTrail();
				boolean bl4 = fireworkExplosionComponent2.hasTwinkle();
				IntList intList = fireworkExplosionComponent2.colors();
				IntList intList2 = fireworkExplosionComponent2.fadeColors();
				if (intList.isEmpty()) {
					intList = IntList.of(DyeColor.BLACK.getFireworkColor());
				}

				switch (fireworkExplosionComponent2.shape()) {
					case SMALL_BALL:
						this.explodeBall(0.25, 2, intList, intList2, bl3, bl4);
						break;
					case LARGE_BALL:
						this.explodeBall(0.5, 4, intList, intList2, bl3, bl4);
						break;
					case STAR:
						this.explodeStar(0.5, STAR_PATTERN, intList, intList2, bl3, bl4, false);
						break;
					case CREEPER:
						this.explodeStar(0.5, CREEPER_PATTERN, intList, intList2, bl3, bl4, true);
						break;
					case BURST:
						this.explodeBurst(intList, intList2, bl3, bl4);
				}

				int j = intList.getInt(0);
				Particle particle = this.particleManager.addParticle(ParticleTypes.FLASH, this.x, this.y, this.z, 0.0, 0.0, 0.0);
				particle.setColor((float)ColorHelper.Argb.getRed(j) / 255.0F, (float)ColorHelper.Argb.getGreen(j) / 255.0F, (float)ColorHelper.Argb.getBlue(j) / 255.0F);
			}

			this.age++;
			if (this.age > this.maxAge) {
				if (this.flicker) {
					boolean blx = this.isFar();
					SoundEvent soundEvent2 = blx ? SoundEvents.ENTITY_FIREWORK_ROCKET_TWINKLE_FAR : SoundEvents.ENTITY_FIREWORK_ROCKET_TWINKLE;
					this.world.playSound(this.x, this.y, this.z, soundEvent2, SoundCategory.AMBIENT, 20.0F, 0.9F + this.random.nextFloat() * 0.15F, true);
				}

				this.markDead();
			}
		}

		private boolean isFar() {
			MinecraftClient minecraftClient = MinecraftClient.getInstance();
			return minecraftClient.gameRenderer.getCamera().getPos().squaredDistanceTo(this.x, this.y, this.z) >= 256.0;
		}

		private void addExplosionParticle(
			double x, double y, double z, double velocityX, double velocityY, double velocityZ, IntList colors, IntList targetColors, boolean trail, boolean flicker
		) {
			FireworksSparkParticle.Explosion explosion = (FireworksSparkParticle.Explosion)this.particleManager
				.addParticle(ParticleTypes.FIREWORK, x, y, z, velocityX, velocityY, velocityZ);
			explosion.setTrail(trail);
			explosion.setFlicker(flicker);
			explosion.setAlpha(0.99F);
			explosion.setColor(Util.<Integer>getRandom(colors, this.random));
			if (!targetColors.isEmpty()) {
				explosion.setTargetColor(Util.<Integer>getRandom(targetColors, this.random));
			}
		}

		private void explodeBall(double size, int amount, IntList colors, IntList targetColors, boolean trail, boolean flicker) {
			double d = this.x;
			double e = this.y;
			double f = this.z;

			for (int i = -amount; i <= amount; i++) {
				for (int j = -amount; j <= amount; j++) {
					for (int k = -amount; k <= amount; k++) {
						double g = (double)j + (this.random.nextDouble() - this.random.nextDouble()) * 0.5;
						double h = (double)i + (this.random.nextDouble() - this.random.nextDouble()) * 0.5;
						double l = (double)k + (this.random.nextDouble() - this.random.nextDouble()) * 0.5;
						double m = Math.sqrt(g * g + h * h + l * l) / size + this.random.nextGaussian() * 0.05;
						this.addExplosionParticle(d, e, f, g / m, h / m, l / m, colors, targetColors, trail, flicker);
						if (i != -amount && i != amount && j != -amount && j != amount) {
							k += amount * 2 - 1;
						}
					}
				}
			}
		}

		private void explodeStar(double size, double[][] pattern, IntList colors, IntList targetColors, boolean trail, boolean flicker, boolean keepShape) {
			double d = pattern[0][0];
			double e = pattern[0][1];
			this.addExplosionParticle(this.x, this.y, this.z, d * size, e * size, 0.0, colors, targetColors, trail, flicker);
			float f = this.random.nextFloat() * (float) Math.PI;
			double g = keepShape ? 0.034 : 0.34;

			for (int i = 0; i < 3; i++) {
				double h = (double)f + (double)((float)i * (float) Math.PI) * g;
				double j = d;
				double k = e;

				for (int l = 1; l < pattern.length; l++) {
					double m = pattern[l][0];
					double n = pattern[l][1];

					for (double o = 0.25; o <= 1.0; o += 0.25) {
						double p = MathHelper.lerp(o, j, m) * size;
						double q = MathHelper.lerp(o, k, n) * size;
						double r = p * Math.sin(h);
						p *= Math.cos(h);

						for (double s = -1.0; s <= 1.0; s += 2.0) {
							this.addExplosionParticle(this.x, this.y, this.z, p * s, q, r * s, colors, targetColors, trail, flicker);
						}
					}

					j = m;
					k = n;
				}
			}
		}

		private void explodeBurst(IntList colors, IntList targetColors, boolean trail, boolean flicker) {
			double d = this.random.nextGaussian() * 0.05;
			double e = this.random.nextGaussian() * 0.05;

			for (int i = 0; i < 70; i++) {
				double f = this.velocityX * 0.5 + this.random.nextGaussian() * 0.15 + d;
				double g = this.velocityZ * 0.5 + this.random.nextGaussian() * 0.15 + e;
				double h = this.velocityY * 0.5 + this.random.nextDouble() * 0.5;
				this.addExplosionParticle(this.x, this.y, this.z, f, h, g, colors, targetColors, trail, flicker);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Flash extends SpriteBillboardParticle {
		Flash(ClientWorld clientWorld, double d, double e, double f) {
			super(clientWorld, d, e, f);
			this.maxAge = 4;
		}

		@Override
		public ParticleTextureSheet getType() {
			return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
		}

		@Override
		public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
			this.setAlpha(0.6F - ((float)this.age + tickDelta - 1.0F) * 0.25F * 0.5F);
			super.buildGeometry(vertexConsumer, camera, tickDelta);
		}

		@Override
		public float getSize(float tickDelta) {
			return 7.1F * MathHelper.sin(((float)this.age + tickDelta - 1.0F) * 0.25F * (float) Math.PI);
		}
	}

	@Environment(EnvType.CLIENT)
	public static class FlashFactory implements ParticleFactory<SimpleParticleType> {
		private final SpriteProvider spriteProvider;

		public FlashFactory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
			FireworksSparkParticle.Flash flash = new FireworksSparkParticle.Flash(clientWorld, d, e, f);
			flash.setSprite(this.spriteProvider);
			return flash;
		}
	}
}
