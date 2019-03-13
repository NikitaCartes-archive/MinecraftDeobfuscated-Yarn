package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4002;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class CurrentDownParticle extends SpriteBillboardParticle {
	private float field_3897;

	private CurrentDownParticle(World world, double d, double e, double f) {
		super(world, d, e, f);
		this.maxAge = (int)(Math.random() * 60.0) + 30;
		this.collidesWithWorld = false;
		this.velocityX = 0.0;
		this.velocityY = -0.05;
		this.velocityZ = 0.0;
		this.setBoundingBoxSpacing(0.02F, 0.02F);
		this.scale = this.scale * (this.random.nextFloat() * 0.6F + 0.2F);
		this.gravityStrength = 0.002F;
	}

	@Override
	public ParticleTextureSheet method_18122() {
		return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
	}

	@Override
	public void update() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		if (this.age++ >= this.maxAge) {
			this.markDead();
		} else {
			float f = 0.6F;
			this.velocityX = this.velocityX + (double)(0.6F * MathHelper.cos(this.field_3897));
			this.velocityZ = this.velocityZ + (double)(0.6F * MathHelper.sin(this.field_3897));
			this.velocityX *= 0.07;
			this.velocityZ *= 0.07;
			this.move(this.velocityX, this.velocityY, this.velocityZ);
			if (!this.world.method_8316(new BlockPos(this.posX, this.posY, this.posZ)).method_15767(FluidTags.field_15517) || this.onGround) {
				this.markDead();
			}

			this.field_3897 = (float)((double)this.field_3897 + 0.08);
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<DefaultParticleType> {
		private final class_4002 field_17890;

		public Factory(class_4002 arg) {
			this.field_17890 = arg;
		}

		public Particle method_3114(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			CurrentDownParticle currentDownParticle = new CurrentDownParticle(world, d, e, f);
			currentDownParticle.method_18140(this.field_17890);
			return currentDownParticle;
		}
	}
}
