package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.particle.TexturedParticle;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class DamageParticle extends Particle {
	private final float field_3788;

	protected DamageParticle(World world, double d, double e, double f, double g, double h, double i) {
		this(world, d, e, f, g, h, i, 1.0F);
	}

	protected DamageParticle(World world, double d, double e, double f, double g, double h, double i, float j) {
		super(world, d, e, f, 0.0, 0.0, 0.0);
		this.velocityX *= 0.1F;
		this.velocityY *= 0.1F;
		this.velocityZ *= 0.1F;
		this.velocityX += g * 0.4;
		this.velocityY += h * 0.4;
		this.velocityZ += i * 0.4;
		float k = (float)(Math.random() * 0.3F + 0.6F);
		this.colorRed = k;
		this.colorGreen = k;
		this.colorBlue = k;
		this.size *= 0.75F;
		this.size *= j;
		this.field_3788 = this.size;
		this.maxAge = (int)(6.0 / (Math.random() * 0.8 + 0.6));
		this.maxAge = (int)((float)this.maxAge * j);
		this.maxAge = Math.max(this.maxAge, 1);
		this.collidesWithWorld = false;
		this.setSpriteIndex(65);
		this.update();
	}

	@Override
	public void buildGeometry(VertexBuffer vertexBuffer, Entity entity, float f, float g, float h, float i, float j, float k) {
		float l = ((float)this.age + f) / (float)this.maxAge * 32.0F;
		l = MathHelper.clamp(l, 0.0F, 1.0F);
		this.size = this.field_3788 * l;
		super.buildGeometry(vertexBuffer, entity, f, g, h, i, j, k);
	}

	@Override
	public void update() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		if (this.age++ >= this.maxAge) {
			this.markDead();
		}

		this.addPos(this.velocityX, this.velocityY, this.velocityZ);
		this.colorGreen = (float)((double)this.colorGreen * 0.96);
		this.colorBlue = (float)((double)this.colorBlue * 0.9);
		this.velocityX *= 0.7F;
		this.velocityY *= 0.7F;
		this.velocityZ *= 0.7F;
		this.velocityY -= 0.02F;
		if (this.onGround) {
			this.velocityX *= 0.7F;
			this.velocityZ *= 0.7F;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class FactoryCrit implements FactoryParticle<TexturedParticle> {
		public Particle createParticle(TexturedParticle texturedParticle, World world, double d, double e, double f, double g, double h, double i) {
			return new DamageParticle(world, d, e, f, g, h, i);
		}
	}

	@Environment(EnvType.CLIENT)
	public static class FactoryCritMagic implements FactoryParticle<TexturedParticle> {
		public Particle createParticle(TexturedParticle texturedParticle, World world, double d, double e, double f, double g, double h, double i) {
			Particle particle = new DamageParticle(world, d, e, f, g, h, i);
			particle.setColor(particle.getColorRed() * 0.3F, particle.getColorGreen() * 0.8F, particle.getColorBlue());
			particle.incSpriteIndex();
			return particle;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class FactoryDefault implements FactoryParticle<TexturedParticle> {
		public Particle createParticle(TexturedParticle texturedParticle, World world, double d, double e, double f, double g, double h, double i) {
			Particle particle = new DamageParticle(world, d, e, f, g, h + 1.0, i, 1.0F);
			particle.setMaxAge(20);
			particle.setSpriteIndex(67);
			return particle;
		}
	}
}
