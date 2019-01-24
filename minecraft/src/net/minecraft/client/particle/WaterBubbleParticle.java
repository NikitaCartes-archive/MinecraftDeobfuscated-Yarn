package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class WaterBubbleParticle extends Particle {
	protected WaterBubbleParticle(World world, double d, double e, double f, double g, double h, double i) {
		super(world, d, e, f, g, h, i);
		this.colorRed = 1.0F;
		this.colorGreen = 1.0F;
		this.colorBlue = 1.0F;
		this.setSpriteIndex(32);
		this.setBoundingBoxSpacing(0.02F, 0.02F);
		this.size = this.size * (this.random.nextFloat() * 0.6F + 0.2F);
		this.velocityX = g * 0.2F + (Math.random() * 2.0 - 1.0) * 0.02F;
		this.velocityY = h * 0.2F + (Math.random() * 2.0 - 1.0) * 0.02F;
		this.velocityZ = i * 0.2F + (Math.random() * 2.0 - 1.0) * 0.02F;
		this.maxAge = (int)(8.0 / (Math.random() * 0.8 + 0.2));
	}

	@Override
	public void update() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		this.velocityY += 0.002;
		this.move(this.velocityX, this.velocityY, this.velocityZ);
		this.velocityX *= 0.85F;
		this.velocityY *= 0.85F;
		this.velocityZ *= 0.85F;
		if (!this.world.getFluidState(new BlockPos(this.posX, this.posY, this.posZ)).matches(FluidTags.field_15517)) {
			this.markDead();
		}

		if (this.maxAge-- <= 0) {
			this.markDead();
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<DefaultParticleType> {
		public Particle method_3011(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			return new WaterBubbleParticle(world, d, e, f, g, h, i);
		}
	}
}
