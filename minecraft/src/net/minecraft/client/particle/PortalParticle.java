package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class PortalParticle extends SpriteBillboardParticle {
	private final double startX;
	private final double startY;
	private final double startZ;

	private PortalParticle(World world, double d, double e, double f, double g, double h, double i) {
		super(world, d, e, f);
		this.velocityX = g;
		this.velocityY = h;
		this.velocityZ = i;
		this.x = d;
		this.y = e;
		this.z = f;
		this.startX = this.x;
		this.startY = this.y;
		this.startZ = this.z;
		this.scale = 0.1F * (this.random.nextFloat() * 0.2F + 0.5F);
		float j = this.random.nextFloat() * 0.6F + 0.4F;
		this.colorRed = j * 0.9F;
		this.colorGreen = j * 0.3F;
		this.colorBlue = j;
		this.maxAge = (int)(Math.random() * 10.0) + 40;
	}

	@Override
	public ParticleTextureSheet getTextureSheet() {
		return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
	}

	@Override
	public void move(double d, double e, double f) {
		this.setBoundingBox(this.getBoundingBox().offset(d, e, f));
		this.repositionFromBoundingBox();
	}

	@Override
	public float getSize(float f) {
		float g = ((float)this.age + f) / (float)this.maxAge;
		g = 1.0F - g;
		g *= g;
		g = 1.0F - g;
		return this.scale * g;
	}

	@Override
	public int getColorMultiplier(float f) {
		int i = super.getColorMultiplier(f);
		float g = (float)this.age / (float)this.maxAge;
		g *= g;
		g *= g;
		int j = i & 0xFF;
		int k = i >> 16 & 0xFF;
		k += (int)(g * 15.0F * 16.0F);
		if (k > 240) {
			k = 240;
		}

		return j | k << 16;
	}

	@Override
	public void update() {
		this.prevPosX = this.x;
		this.prevPosY = this.y;
		this.prevPosZ = this.z;
		if (this.age++ >= this.maxAge) {
			this.markDead();
		} else {
			float f = (float)this.age / (float)this.maxAge;
			float var3 = -f + f * f * 2.0F;
			float var4 = 1.0F - var3;
			this.x = this.startX + this.velocityX * (double)var4;
			this.y = this.startY + this.velocityY * (double)var4 + (double)(1.0F - f);
			this.z = this.startZ + this.velocityZ * (double)var4;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider field_17865;

		public Factory(SpriteProvider spriteProvider) {
			this.field_17865 = spriteProvider;
		}

		public Particle method_3094(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			PortalParticle portalParticle = new PortalParticle(world, d, e, f, g, h, i);
			portalParticle.setSprite(this.field_17865);
			return portalParticle;
		}
	}
}
