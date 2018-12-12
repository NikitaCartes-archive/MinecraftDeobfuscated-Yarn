package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class NoteParticle extends Particle {
	private final float field_3828;

	protected NoteParticle(World world, double d, double e, double f, double g, double h, double i) {
		this(world, d, e, f, g, h, i, 2.0F);
	}

	protected NoteParticle(World world, double d, double e, double f, double g, double h, double i, float j) {
		super(world, d, e, f, 0.0, 0.0, 0.0);
		this.velocityX *= 0.01F;
		this.velocityY *= 0.01F;
		this.velocityZ *= 0.01F;
		this.velocityY += 0.2;
		this.colorRed = MathHelper.sin(((float)g + 0.0F) * (float) (Math.PI * 2)) * 0.65F + 0.35F;
		this.colorGreen = MathHelper.sin(((float)g + 0.33333334F) * (float) (Math.PI * 2)) * 0.65F + 0.35F;
		this.colorBlue = MathHelper.sin(((float)g + 0.6666667F) * (float) (Math.PI * 2)) * 0.65F + 0.35F;
		this.size *= 0.75F;
		this.size *= j;
		this.field_3828 = this.size;
		this.maxAge = 6;
		this.setSpriteIndex(64);
	}

	@Override
	public void buildGeometry(BufferBuilder bufferBuilder, Entity entity, float f, float g, float h, float i, float j, float k) {
		float l = ((float)this.age + f) / (float)this.maxAge * 32.0F;
		l = MathHelper.clamp(l, 0.0F, 1.0F);
		this.size = this.field_3828 * l;
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

		this.move(this.velocityX, this.velocityY, this.velocityZ);
		if (this.posY == this.prevPosY) {
			this.velocityX *= 1.1;
			this.velocityZ *= 1.1;
		}

		this.velocityX *= 0.66F;
		this.velocityY *= 0.66F;
		this.velocityZ *= 0.66F;
		if (this.onGround) {
			this.velocityX *= 0.7F;
			this.velocityZ *= 0.7F;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<DefaultParticleType> {
		public Particle method_3041(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			return new NoteParticle(world, d, e, f, g, h, i);
		}
	}
}
