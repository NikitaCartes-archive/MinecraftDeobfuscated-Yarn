package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.TexturedParticle;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class WaterSuspendParticle extends Particle {
	protected WaterSuspendParticle(World world, double d, double e, double f, double g, double h, double i) {
		super(world, d, e - 0.125, f, g, h, i);
		this.colorRed = 0.4F;
		this.colorGreen = 0.4F;
		this.colorBlue = 0.7F;
		this.setSpriteIndex(0);
		this.setBoundingBoxSpacing(0.01F, 0.01F);
		this.size = this.size * (this.random.nextFloat() * 0.6F + 0.2F);
		this.velocityX = g * 0.0;
		this.velocityY = h * 0.0;
		this.velocityZ = i * 0.0;
		this.maxAge = (int)(16.0 / (Math.random() * 0.8 + 0.2));
	}

	@Override
	public void update() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		this.addPos(this.velocityX, this.velocityY, this.velocityZ);
		if (!this.world.getFluidState(new BlockPos(this.posX, this.posY, this.posZ)).matches(FluidTags.field_15517)) {
			this.markDead();
		}

		if (this.maxAge-- <= 0) {
			this.markDead();
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_724 implements FactoryParticle<TexturedParticle> {
		public Particle createParticle(TexturedParticle texturedParticle, World world, double d, double e, double f, double g, double h, double i) {
			return new WaterSuspendParticle(world, d, e, f, g, h, i);
		}
	}
}
