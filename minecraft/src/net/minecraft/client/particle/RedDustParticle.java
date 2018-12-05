package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.particle.DustParticle;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class RedDustParticle extends Particle {
	private final float field_3796;

	public RedDustParticle(World world, double d, double e, double f, double g, double h, double i, DustParticle dustParticle) {
		super(world, d, e, f, g, h, i);
		this.velocityX *= 0.1F;
		this.velocityY *= 0.1F;
		this.velocityZ *= 0.1F;
		float j = (float)Math.random() * 0.4F + 0.6F;
		this.colorRed = ((float)(Math.random() * 0.2F) + 0.8F) * dustParticle.getRed() * j;
		this.colorGreen = ((float)(Math.random() * 0.2F) + 0.8F) * dustParticle.getGreen() * j;
		this.colorBlue = ((float)(Math.random() * 0.2F) + 0.8F) * dustParticle.getBlue() * j;
		this.size *= 0.75F;
		this.size = this.size * dustParticle.getAlpha();
		this.field_3796 = this.size;
		this.maxAge = (int)(8.0 / (Math.random() * 0.8 + 0.2));
		this.maxAge = (int)((float)this.maxAge * dustParticle.getAlpha());
		this.maxAge = Math.max(this.maxAge, 1);
	}

	@Override
	public void buildGeometry(VertexBuffer vertexBuffer, Entity entity, float f, float g, float h, float i, float j, float k) {
		float l = ((float)this.age + f) / (float)this.maxAge * 32.0F;
		l = MathHelper.clamp(l, 0.0F, 1.0F);
		this.size = this.field_3796 * l;
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

		this.setSpriteIndex(7 - this.age * 8 / this.maxAge);
		this.addPos(this.velocityX, this.velocityY, this.velocityZ);
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

	@Environment(EnvType.CLIENT)
	public static class Factory implements FactoryParticle<DustParticle> {
		public Particle createParticle(DustParticle dustParticle, World world, double d, double e, double f, double g, double h, double i) {
			return new RedDustParticle(world, d, e, f, g, h, i, dustParticle);
		}
	}
}
