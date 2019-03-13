package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4002;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class CloudParticle extends SpriteBillboardParticle {
	private final class_4002 field_17862;

	private CloudParticle(World world, double d, double e, double f, double g, double h, double i, class_4002 arg) {
		super(world, d, e, f, 0.0, 0.0, 0.0);
		this.field_17862 = arg;
		float j = 2.5F;
		this.velocityX *= 0.1F;
		this.velocityY *= 0.1F;
		this.velocityZ *= 0.1F;
		this.velocityX += g;
		this.velocityY += h;
		this.velocityZ += i;
		float k = 1.0F - (float)(Math.random() * 0.3F);
		this.colorRed = k;
		this.colorGreen = k;
		this.colorBlue = k;
		this.scale *= 1.875F;
		int l = (int)(8.0 / (Math.random() * 0.8 + 0.3));
		this.maxAge = (int)Math.max((float)l * 2.5F, 1.0F);
		this.collidesWithWorld = false;
		this.method_18142(arg);
	}

	@Override
	public ParticleTextureSheet method_18122() {
		return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
	}

	@Override
	public float method_18132(float f) {
		return this.scale * MathHelper.clamp(((float)this.age + f) / (float)this.maxAge * 32.0F, 0.0F, 1.0F);
	}

	@Override
	public void update() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		if (this.age++ >= this.maxAge) {
			this.markDead();
		} else {
			this.method_18142(this.field_17862);
			this.move(this.velocityX, this.velocityY, this.velocityZ);
			this.velocityX *= 0.96F;
			this.velocityY *= 0.96F;
			this.velocityZ *= 0.96F;
			PlayerEntity playerEntity = this.world.method_18459(this.posX, this.posY, this.posZ, 2.0, false);
			if (playerEntity != null) {
				BoundingBox boundingBox = playerEntity.method_5829();
				if (this.posY > boundingBox.minY) {
					this.posY = this.posY + (boundingBox.minY - this.posY) * 0.2;
					this.velocityY = this.velocityY + (playerEntity.method_18798().y - this.velocityY) * 0.2;
					this.setPos(this.posX, this.posY, this.posZ);
				}
			}

			if (this.onGround) {
				this.velocityX *= 0.7F;
				this.velocityZ *= 0.7F;
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static class CloudFactory implements ParticleFactory<DefaultParticleType> {
		private final class_4002 field_17863;

		public CloudFactory(class_4002 arg) {
			this.field_17863 = arg;
		}

		public Particle method_3088(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			return new CloudParticle(world, d, e, f, g, h, i, this.field_17863);
		}
	}

	@Environment(EnvType.CLIENT)
	public static class SneezeFactory implements ParticleFactory<DefaultParticleType> {
		private final class_4002 field_17864;

		public SneezeFactory(class_4002 arg) {
			this.field_17864 = arg;
		}

		public Particle method_3089(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			Particle particle = new CloudParticle(world, d, e, f, g, h, i, this.field_17864);
			particle.setColor(200.0F, 50.0F, 120.0F);
			particle.setColorAlpha(0.4F);
			return particle;
		}
	}
}
