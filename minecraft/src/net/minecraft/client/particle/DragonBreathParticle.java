package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.particle.TexturedParticle;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class DragonBreathParticle extends Particle {
	private final float field_3791;
	private boolean field_3792;

	protected DragonBreathParticle(World world, double d, double e, double f, double g, double h, double i) {
		super(world, d, e, f, g, h, i);
		this.velocityX = g;
		this.velocityY = h;
		this.velocityZ = i;
		this.colorRed = MathHelper.nextFloat(this.random, 0.7176471F, 0.8745098F);
		this.colorGreen = MathHelper.nextFloat(this.random, 0.0F, 0.0F);
		this.colorBlue = MathHelper.nextFloat(this.random, 0.8235294F, 0.9764706F);
		this.size *= 0.75F;
		this.field_3791 = this.size;
		this.maxAge = (int)(20.0 / ((double)this.random.nextFloat() * 0.8 + 0.2));
		this.field_3792 = false;
		this.collidesWithWorld = false;
	}

	@Override
	public void update() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		if (this.age++ >= this.maxAge) {
			this.markDead();
		} else {
			this.setSpriteIndex(3 * this.age / this.maxAge + 5);
			if (this.onGround) {
				this.velocityY = 0.0;
				this.field_3792 = true;
			}

			if (this.field_3792) {
				this.velocityY += 0.002;
			}

			this.addPos(this.velocityX, this.velocityY, this.velocityZ);
			if (this.posY == this.prevPosY) {
				this.velocityX *= 1.1;
				this.velocityZ *= 1.1;
			}

			this.velocityX *= 0.96F;
			this.velocityZ *= 0.96F;
			if (this.field_3792) {
				this.velocityY *= 0.96F;
			}
		}
	}

	@Override
	public void buildGeometry(VertexBuffer vertexBuffer, Entity entity, float f, float g, float h, float i, float j, float k) {
		this.size = this.field_3791 * MathHelper.clamp(((float)this.age + f) / (float)this.maxAge * 32.0F, 0.0F, 1.0F);
		super.buildGeometry(vertexBuffer, entity, f, g, h, i, j, k);
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements FactoryParticle<TexturedParticle> {
		public Particle createParticle(TexturedParticle texturedParticle, World world, double d, double e, double f, double g, double h, double i) {
			return new DragonBreathParticle(world, d, e, f, g, h, i);
		}
	}
}
