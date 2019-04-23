package net.minecraft.client.particle;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.item.FireworkItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class FireworksSparkParticle {
	@Environment(EnvType.CLIENT)
	public static class ExplosionFactory implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider spriteProvider;

		public ExplosionFactory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle method_3025(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			FireworksSparkParticle.ExplosionParticle explosionParticle = new FireworksSparkParticle.ExplosionParticle(
				world, d, e, f, g, h, i, MinecraftClient.getInstance().particleManager, this.spriteProvider
			);
			explosionParticle.setColorAlpha(0.99F);
			return explosionParticle;
		}
	}

	@Environment(EnvType.CLIENT)
	static class ExplosionParticle extends AnimatedParticle {
		private boolean trail;
		private boolean flicker;
		private final ParticleManager particleManager;
		private float field_3801;
		private float field_3800;
		private float field_3799;
		private boolean field_3802;

		private ExplosionParticle(
			World world, double d, double e, double f, double g, double h, double i, ParticleManager particleManager, SpriteProvider spriteProvider
		) {
			super(world, d, e, f, spriteProvider, -0.004F);
			this.velocityX = g;
			this.velocityY = h;
			this.velocityZ = i;
			this.particleManager = particleManager;
			this.scale *= 0.75F;
			this.maxAge = 48 + this.random.nextInt(12);
			this.setSpriteForAge(spriteProvider);
		}

		public void setTrail(boolean bl) {
			this.trail = bl;
		}

		public void setFlicker(boolean bl) {
			this.flicker = bl;
		}

		@Override
		public void buildGeometry(BufferBuilder bufferBuilder, Camera camera, float f, float g, float h, float i, float j, float k) {
			if (!this.flicker || this.age < this.maxAge / 3 || (this.age + this.maxAge) / 3 % 2 == 0) {
				super.buildGeometry(bufferBuilder, camera, f, g, h, i, j, k);
			}
		}

		@Override
		public void tick() {
			super.tick();
			if (this.trail && this.age < this.maxAge / 2 && (this.age + this.maxAge) % 2 == 0) {
				FireworksSparkParticle.ExplosionParticle explosionParticle = new FireworksSparkParticle.ExplosionParticle(
					this.world, this.x, this.y, this.z, 0.0, 0.0, 0.0, this.particleManager, this.spriteProvider
				);
				explosionParticle.setColorAlpha(0.99F);
				explosionParticle.setColor(this.colorRed, this.colorGreen, this.colorBlue);
				explosionParticle.age = explosionParticle.maxAge / 2;
				if (this.field_3802) {
					explosionParticle.field_3802 = true;
					explosionParticle.field_3801 = this.field_3801;
					explosionParticle.field_3800 = this.field_3800;
					explosionParticle.field_3799 = this.field_3799;
				}

				explosionParticle.flicker = this.flicker;
				this.particleManager.addParticle(explosionParticle);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static class FireworkParticle extends NoRenderParticle {
		private int age;
		private final ParticleManager particleManager;
		private ListTag explosions;
		private boolean flicker;

		public FireworkParticle(
			World world, double d, double e, double f, double g, double h, double i, ParticleManager particleManager, @Nullable CompoundTag compoundTag
		) {
			super(world, d, e, f);
			this.velocityX = g;
			this.velocityY = h;
			this.velocityZ = i;
			this.particleManager = particleManager;
			this.maxAge = 8;
			if (compoundTag != null) {
				this.explosions = compoundTag.getList("Explosions", 10);
				if (this.explosions.isEmpty()) {
					this.explosions = null;
				} else {
					this.maxAge = this.explosions.size() * 2 - 1;

					for (int j = 0; j < this.explosions.size(); j++) {
						CompoundTag compoundTag2 = this.explosions.getCompoundTag(j);
						if (compoundTag2.getBoolean("Flicker")) {
							this.flicker = true;
							this.maxAge += 15;
							break;
						}
					}
				}
			}
		}

		@Override
		public void tick() {
			if (this.age == 0 && this.explosions != null) {
				boolean bl = this.isFar();
				boolean bl2 = false;
				if (this.explosions.size() >= 3) {
					bl2 = true;
				} else {
					for (int i = 0; i < this.explosions.size(); i++) {
						CompoundTag compoundTag = this.explosions.getCompoundTag(i);
						if (FireworkItem.Type.fromId(compoundTag.getByte("Type")) == FireworkItem.Type.field_7977) {
							bl2 = true;
							break;
						}
					}
				}

				SoundEvent soundEvent;
				if (bl2) {
					soundEvent = bl ? SoundEvents.field_14612 : SoundEvents.field_15188;
				} else {
					soundEvent = bl ? SoundEvents.field_15090 : SoundEvents.field_14917;
				}

				this.world.playSound(this.x, this.y, this.z, soundEvent, SoundCategory.field_15256, 20.0F, 0.95F + this.random.nextFloat() * 0.1F, true);
			}

			if (this.age % 2 == 0 && this.explosions != null && this.age / 2 < this.explosions.size()) {
				int j = this.age / 2;
				CompoundTag compoundTag2 = this.explosions.getCompoundTag(j);
				FireworkItem.Type type = FireworkItem.Type.fromId(compoundTag2.getByte("Type"));
				boolean bl3 = compoundTag2.getBoolean("Trail");
				boolean bl4 = compoundTag2.getBoolean("Flicker");
				int[] is = compoundTag2.getIntArray("Colors");
				int[] js = compoundTag2.getIntArray("FadeColors");
				if (is.length == 0) {
					is = new int[]{DyeColor.field_7963.getFireworkColor()};
				}

				switch (type) {
					case field_7976:
					default:
						this.explodeBall(0.25, 2, is, js, bl3, bl4);
						break;
					case field_7977:
						this.explodeBall(0.5, 4, is, js, bl3, bl4);
						break;
					case field_7973:
						this.explodeStar(
							0.5,
							new double[][]{
								{0.0, 1.0},
								{0.3455, 0.309},
								{0.9511, 0.309},
								{0.3795918367346939, -0.12653061224489795},
								{0.6122448979591837, -0.8040816326530612},
								{0.0, -0.35918367346938773}
							},
							is,
							js,
							bl3,
							bl4,
							false
						);
						break;
					case field_7974:
						this.explodeStar(
							0.5,
							new double[][]{
								{0.0, 0.2}, {0.2, 0.2}, {0.2, 0.6}, {0.6, 0.6}, {0.6, 0.2}, {0.2, 0.2}, {0.2, 0.0}, {0.4, 0.0}, {0.4, -0.6}, {0.2, -0.6}, {0.2, -0.4}, {0.0, -0.4}
							},
							is,
							js,
							bl3,
							bl4,
							true
						);
						break;
					case field_7970:
						this.explodeBurst(is, js, bl3, bl4);
				}

				int k = is[0];
				float f = (float)((k & 0xFF0000) >> 16) / 255.0F;
				float g = (float)((k & 0xFF00) >> 8) / 255.0F;
				float h = (float)((k & 0xFF) >> 0) / 255.0F;
				Particle particle = this.particleManager.addParticle(ParticleTypes.field_17909, this.x, this.y, this.z, 0.0, 0.0, 0.0);
				particle.setColor(f, g, h);
			}

			this.age++;
			if (this.age > this.maxAge) {
				if (this.flicker) {
					boolean blx = this.isFar();
					SoundEvent soundEvent2 = blx ? SoundEvents.field_14882 : SoundEvents.field_14800;
					this.world.playSound(this.x, this.y, this.z, soundEvent2, SoundCategory.field_15256, 20.0F, 0.9F + this.random.nextFloat() * 0.15F, true);
				}

				this.markDead();
			}
		}

		private boolean isFar() {
			MinecraftClient minecraftClient = MinecraftClient.getInstance();
			return minecraftClient.gameRenderer.getCamera().getPos().squaredDistanceTo(this.x, this.y, this.z) >= 256.0;
		}

		private void addExplosionParticle(double d, double e, double f, double g, double h, double i, int[] is, int[] js, boolean bl, boolean bl2) {
			FireworksSparkParticle.ExplosionParticle explosionParticle = (FireworksSparkParticle.ExplosionParticle)this.particleManager
				.addParticle(ParticleTypes.field_11248, d, e, f, g, h, i);
			explosionParticle.setTrail(bl);
			explosionParticle.setFlicker(bl2);
			explosionParticle.setColorAlpha(0.99F);
			int j = this.random.nextInt(is.length);
			explosionParticle.setColor(is[j]);
			if (js.length > 0) {
				explosionParticle.setTargetColor(js[this.random.nextInt(js.length)]);
			}
		}

		private void explodeBall(double d, int i, int[] is, int[] js, boolean bl, boolean bl2) {
			double e = this.x;
			double f = this.y;
			double g = this.z;

			for (int j = -i; j <= i; j++) {
				for (int k = -i; k <= i; k++) {
					for (int l = -i; l <= i; l++) {
						double h = (double)k + (this.random.nextDouble() - this.random.nextDouble()) * 0.5;
						double m = (double)j + (this.random.nextDouble() - this.random.nextDouble()) * 0.5;
						double n = (double)l + (this.random.nextDouble() - this.random.nextDouble()) * 0.5;
						double o = (double)MathHelper.sqrt(h * h + m * m + n * n) / d + this.random.nextGaussian() * 0.05;
						this.addExplosionParticle(e, f, g, h / o, m / o, n / o, is, js, bl, bl2);
						if (j != -i && j != i && k != -i && k != i) {
							l += i * 2 - 1;
						}
					}
				}
			}
		}

		private void explodeStar(double d, double[][] ds, int[] is, int[] js, boolean bl, boolean bl2, boolean bl3) {
			double e = ds[0][0];
			double f = ds[0][1];
			this.addExplosionParticle(this.x, this.y, this.z, e * d, f * d, 0.0, is, js, bl, bl2);
			float g = this.random.nextFloat() * (float) Math.PI;
			double h = bl3 ? 0.034 : 0.34;

			for (int i = 0; i < 3; i++) {
				double j = (double)g + (double)((float)i * (float) Math.PI) * h;
				double k = e;
				double l = f;

				for (int m = 1; m < ds.length; m++) {
					double n = ds[m][0];
					double o = ds[m][1];

					for (double p = 0.25; p <= 1.0; p += 0.25) {
						double q = MathHelper.lerp(p, k, n) * d;
						double r = MathHelper.lerp(p, l, o) * d;
						double s = q * Math.sin(j);
						q *= Math.cos(j);

						for (double t = -1.0; t <= 1.0; t += 2.0) {
							this.addExplosionParticle(this.x, this.y, this.z, q * t, r, s * t, is, js, bl, bl2);
						}
					}

					k = n;
					l = o;
				}
			}
		}

		private void explodeBurst(int[] is, int[] js, boolean bl, boolean bl2) {
			double d = this.random.nextGaussian() * 0.05;
			double e = this.random.nextGaussian() * 0.05;

			for (int i = 0; i < 70; i++) {
				double f = this.velocityX * 0.5 + this.random.nextGaussian() * 0.15 + d;
				double g = this.velocityZ * 0.5 + this.random.nextGaussian() * 0.15 + e;
				double h = this.velocityY * 0.5 + this.random.nextDouble() * 0.5;
				this.addExplosionParticle(this.x, this.y, this.z, f, h, g, is, js, bl, bl2);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static class FlashFactory implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider spriteProvider;

		public FlashFactory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle method_18121(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			FireworksSparkParticle.FlashParticle flashParticle = new FireworksSparkParticle.FlashParticle(world, d, e, f);
			flashParticle.setSprite(this.spriteProvider);
			return flashParticle;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class FlashParticle extends SpriteBillboardParticle {
		private FlashParticle(World world, double d, double e, double f) {
			super(world, d, e, f);
			this.maxAge = 4;
		}

		@Override
		public ParticleTextureSheet getType() {
			return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
		}

		@Override
		public void buildGeometry(BufferBuilder bufferBuilder, Camera camera, float f, float g, float h, float i, float j, float k) {
			this.setColorAlpha(0.6F - ((float)this.age + f - 1.0F) * 0.25F * 0.5F);
			super.buildGeometry(bufferBuilder, camera, f, g, h, i, j, k);
		}

		@Override
		public float getSize(float f) {
			return 7.1F * MathHelper.sin(((float)this.age + f - 1.0F) * 0.25F * (float) Math.PI);
		}
	}
}
