package net.minecraft.client.particle;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4002;
import net.minecraft.class_4184;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
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
	public static class Factory implements ParticleFactory<DefaultParticleType> {
		private final class_4002 field_17811;

		public Factory(class_4002 arg) {
			this.field_17811 = arg;
		}

		public Particle method_3025(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			FireworksSparkParticle.class_680 lv = new FireworksSparkParticle.class_680(
				world, d, e, f, g, h, i, MinecraftClient.getInstance().field_1713, this.field_17811
			);
			lv.setColorAlpha(0.99F);
			return lv;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_3997 implements ParticleFactory<DefaultParticleType> {
		private final class_4002 field_17810;

		public class_3997(class_4002 arg) {
			this.field_17810 = arg;
		}

		public Particle method_18121(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			FireworksSparkParticle.class_678 lv = new FireworksSparkParticle.class_678(world, d, e, f);
			lv.method_18140(this.field_17810);
			return lv;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_678 extends SpriteBillboardParticle {
		private class_678(World world, double d, double e, double f) {
			super(world, d, e, f);
			this.maxAge = 4;
		}

		@Override
		public ParticleTextureSheet method_18122() {
			return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
		}

		@Override
		public void buildGeometry(BufferBuilder bufferBuilder, class_4184 arg, float f, float g, float h, float i, float j, float k) {
			this.setColorAlpha(0.6F - ((float)this.age + f - 1.0F) * 0.25F * 0.5F);
			super.buildGeometry(bufferBuilder, arg, f, g, h, i, j, k);
		}

		@Override
		public float method_18132(float f) {
			return 7.1F * MathHelper.sin(((float)this.age + f - 1.0F) * 0.25F * (float) Math.PI);
		}
	}

	@Environment(EnvType.CLIENT)
	static class class_680 extends AnimatedParticle {
		private boolean field_3804;
		private boolean field_3803;
		private final ParticleManager field_3798;
		private float field_3801;
		private float field_3800;
		private float field_3799;
		private boolean field_3802;

		private class_680(World world, double d, double e, double f, double g, double h, double i, ParticleManager particleManager, class_4002 arg) {
			super(world, d, e, f, arg, -0.004F);
			this.velocityX = g;
			this.velocityY = h;
			this.velocityZ = i;
			this.field_3798 = particleManager;
			this.scale *= 0.75F;
			this.maxAge = 48 + this.random.nextInt(12);
			this.method_18142(arg);
		}

		public void method_3027(boolean bl) {
			this.field_3804 = bl;
		}

		public void method_3026(boolean bl) {
			this.field_3803 = bl;
		}

		@Override
		public void buildGeometry(BufferBuilder bufferBuilder, class_4184 arg, float f, float g, float h, float i, float j, float k) {
			if (!this.field_3803 || this.age < this.maxAge / 3 || (this.age + this.maxAge) / 3 % 2 == 0) {
				super.buildGeometry(bufferBuilder, arg, f, g, h, i, j, k);
			}
		}

		@Override
		public void update() {
			super.update();
			if (this.field_3804 && this.age < this.maxAge / 2 && (this.age + this.maxAge) % 2 == 0) {
				FireworksSparkParticle.class_680 lv = new FireworksSparkParticle.class_680(
					this.world, this.posX, this.posY, this.posZ, 0.0, 0.0, 0.0, this.field_3798, this.field_17866
				);
				lv.setColorAlpha(0.99F);
				lv.setColor(this.colorRed, this.colorGreen, this.colorBlue);
				lv.age = lv.maxAge / 2;
				if (this.field_3802) {
					lv.field_3802 = true;
					lv.field_3801 = this.field_3801;
					lv.field_3800 = this.field_3800;
					lv.field_3799 = this.field_3799;
				}

				lv.field_3803 = this.field_3803;
				this.field_3798.addParticle(lv);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static class create extends NoRenderParticle {
		private int age;
		private final ParticleManager field_3805;
		private ListTag field_3806;
		private boolean flicker;

		public create(World world, double d, double e, double f, double g, double h, double i, ParticleManager particleManager, @Nullable CompoundTag compoundTag) {
			super(world, d, e, f);
			this.velocityX = g;
			this.velocityY = h;
			this.velocityZ = i;
			this.field_3805 = particleManager;
			this.maxAge = 8;
			if (compoundTag != null) {
				this.field_3806 = compoundTag.method_10554("Explosions", 10);
				if (this.field_3806.isEmpty()) {
					this.field_3806 = null;
				} else {
					this.maxAge = this.field_3806.size() * 2 - 1;

					for (int j = 0; j < this.field_3806.size(); j++) {
						CompoundTag compoundTag2 = this.field_3806.getCompoundTag(j);
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
		public void update() {
			if (this.age == 0 && this.field_3806 != null) {
				boolean bl = this.method_3029();
				boolean bl2 = false;
				if (this.field_3806.size() >= 3) {
					bl2 = true;
				} else {
					for (int i = 0; i < this.field_3806.size(); i++) {
						CompoundTag compoundTag = this.field_3806.getCompoundTag(i);
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

				this.world.method_8486(this.posX, this.posY, this.posZ, soundEvent, SoundCategory.field_15256, 20.0F, 0.95F + this.random.nextFloat() * 0.1F, true);
			}

			if (this.age % 2 == 0 && this.field_3806 != null && this.age / 2 < this.field_3806.size()) {
				int j = this.age / 2;
				CompoundTag compoundTag2 = this.field_3806.getCompoundTag(j);
				FireworkItem.Type type = FireworkItem.Type.fromId(compoundTag2.getByte("Type"));
				boolean bl3 = compoundTag2.getBoolean("Trail");
				boolean bl4 = compoundTag2.getBoolean("Flicker");
				int[] is = compoundTag2.getIntArray("Colors");
				int[] js = compoundTag2.getIntArray("FadeColors");
				if (is.length == 0) {
					is = new int[]{DyeColor.BLACK.getFireworkColor()};
				}

				switch (type) {
					case field_7976:
					default:
						this.method_3031(0.25, 2, is, js, bl3, bl4);
						break;
					case field_7977:
						this.method_3031(0.5, 4, is, js, bl3, bl4);
						break;
					case field_7973:
						this.method_3028(
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
						this.method_3028(
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
						this.method_3032(is, js, bl3, bl4);
				}

				int k = is[0];
				float f = (float)((k & 0xFF0000) >> 16) / 255.0F;
				float g = (float)((k & 0xFF00) >> 8) / 255.0F;
				float h = (float)((k & 0xFF) >> 0) / 255.0F;
				Particle particle = this.field_3805.method_3056(ParticleTypes.field_17909, this.posX, this.posY, this.posZ, 0.0, 0.0, 0.0);
				particle.setColor(f, g, h);
			}

			this.age++;
			if (this.age > this.maxAge) {
				if (this.flicker) {
					boolean blx = this.method_3029();
					SoundEvent soundEvent2 = blx ? SoundEvents.field_14882 : SoundEvents.field_14800;
					this.world.method_8486(this.posX, this.posY, this.posZ, soundEvent2, SoundCategory.field_15256, 20.0F, 0.9F + this.random.nextFloat() * 0.15F, true);
				}

				this.markDead();
			}
		}

		private boolean method_3029() {
			MinecraftClient minecraftClient = MinecraftClient.getInstance();
			return minecraftClient.field_1773.method_19418().method_19326().squaredDistanceTo(this.posX, this.posY, this.posZ) >= 256.0;
		}

		private void method_3030(double d, double e, double f, double g, double h, double i, int[] is, int[] js, boolean bl, boolean bl2) {
			FireworksSparkParticle.class_680 lv = (FireworksSparkParticle.class_680)this.field_3805.method_3056(ParticleTypes.field_11248, d, e, f, g, h, i);
			lv.method_3027(bl);
			lv.method_3026(bl2);
			lv.setColorAlpha(0.99F);
			int j = this.random.nextInt(is.length);
			lv.setColor(is[j]);
			if (js.length > 0) {
				lv.setTargetColor(js[this.random.nextInt(js.length)]);
			}
		}

		private void method_3031(double d, int i, int[] is, int[] js, boolean bl, boolean bl2) {
			double e = this.posX;
			double f = this.posY;
			double g = this.posZ;

			for (int j = -i; j <= i; j++) {
				for (int k = -i; k <= i; k++) {
					for (int l = -i; l <= i; l++) {
						double h = (double)k + (this.random.nextDouble() - this.random.nextDouble()) * 0.5;
						double m = (double)j + (this.random.nextDouble() - this.random.nextDouble()) * 0.5;
						double n = (double)l + (this.random.nextDouble() - this.random.nextDouble()) * 0.5;
						double o = (double)MathHelper.sqrt(h * h + m * m + n * n) / d + this.random.nextGaussian() * 0.05;
						this.method_3030(e, f, g, h / o, m / o, n / o, is, js, bl, bl2);
						if (j != -i && j != i && k != -i && k != i) {
							l += i * 2 - 1;
						}
					}
				}
			}
		}

		private void method_3028(double d, double[][] ds, int[] is, int[] js, boolean bl, boolean bl2, boolean bl3) {
			double e = ds[0][0];
			double f = ds[0][1];
			this.method_3030(this.posX, this.posY, this.posZ, e * d, f * d, 0.0, is, js, bl, bl2);
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
							this.method_3030(this.posX, this.posY, this.posZ, q * t, r, s * t, is, js, bl, bl2);
						}
					}

					k = n;
					l = o;
				}
			}
		}

		private void method_3032(int[] is, int[] js, boolean bl, boolean bl2) {
			double d = this.random.nextGaussian() * 0.05;
			double e = this.random.nextGaussian() * 0.05;

			for (int i = 0; i < 70; i++) {
				double f = this.velocityX * 0.5 + this.random.nextGaussian() * 0.15 + d;
				double g = this.velocityZ * 0.5 + this.random.nextGaussian() * 0.15 + e;
				double h = this.velocityY * 0.5 + this.random.nextDouble() * 0.5;
				this.method_3030(this.posX, this.posY, this.posZ, f, h, g, is, js, bl, bl2);
			}
		}
	}
}
