package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class CloudParticle extends Particle {
	private final float field_3875;

	protected CloudParticle(World world, double d, double e, double f, double g, double h, double i) {
		super(world, d, e, f, 0.0, 0.0, 0.0);
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
		this.size *= 0.75F;
		this.size *= 2.5F;
		this.field_3875 = this.size;
		this.maxAge = (int)(8.0 / (Math.random() * 0.8 + 0.3));
		this.maxAge = (int)((float)this.maxAge * 2.5F);
		this.maxAge = Math.max(this.maxAge, 1);
		this.collidesWithWorld = false;
	}

	@Override
	public void buildGeometry(BufferBuilder bufferBuilder, Entity entity, float f, float g, float h, float i, float j, float k) {
		float l = ((float)this.age + f) / (float)this.maxAge * 32.0F;
		l = MathHelper.clamp(l, 0.0F, 1.0F);
		this.size = this.field_3875 * l;
		super.buildGeometry(bufferBuilder, entity, f, g, h, i, j, k);
	}

	@Override
	public void update() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		if (this.age++ >= this.maxAge) {
			this.markDead();
		}

		this.setSpriteIndex(7 - this.age * 8 / this.maxAge);
		this.move(this.velocityX, this.velocityY, this.velocityZ);
		this.velocityX *= 0.96F;
		this.velocityY *= 0.96F;
		this.velocityZ *= 0.96F;
		PlayerEntity playerEntity = this.world.getClosestPlayer(this.posX, this.posY, this.posZ, 2.0, false);
		if (playerEntity != null) {
			BoundingBox boundingBox = playerEntity.getBoundingBox();
			if (this.posY > boundingBox.minY) {
				this.posY = this.posY + (boundingBox.minY - this.posY) * 0.2;
				this.velocityY = this.velocityY + (playerEntity.velocityY - this.velocityY) * 0.2;
				this.setPos(this.posX, this.posY, this.posZ);
			}
		}

		if (this.onGround) {
			this.velocityX *= 0.7F;
			this.velocityZ *= 0.7F;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class CloudFactory implements ParticleFactory<DefaultParticleType> {
		public Particle createParticle(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			return new CloudParticle(world, d, e, f, g, h, i);
		}
	}

	@Environment(EnvType.CLIENT)
	public static class SneezeFactory implements ParticleFactory<DefaultParticleType> {
		public Particle createParticle(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			Particle particle = new CloudParticle(world, d, e, f, g, h, i);
			particle.setColor(200.0F, 50.0F, 120.0F);
			particle.setColorAlpha(0.4F);
			return particle;
		}
	}
}
