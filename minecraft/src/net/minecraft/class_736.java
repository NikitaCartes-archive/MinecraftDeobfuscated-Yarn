package net.minecraft;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.FactoryParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.particle.TexturedParticle;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class class_736 extends Particle {
	private float field_3897;

	protected class_736(World world, double d, double e, double f) {
		super(world, d, e, f, 0.0, 0.0, 0.0);
		this.setSpriteIndex(32);
		this.maxAge = (int)(Math.random() * 60.0) + 30;
		this.collidesWithWorld = false;
		this.velocityX = 0.0;
		this.velocityY = -0.05;
		this.velocityZ = 0.0;
		this.setBoundingBoxSpacing(0.02F, 0.02F);
		this.size = this.size * (this.random.nextFloat() * 0.6F + 0.2F);
		this.gravityStrength = 0.002F;
	}

	@Override
	public void update() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		float f = 0.6F;
		this.velocityX = this.velocityX + (double)(0.6F * MathHelper.cos(this.field_3897));
		this.velocityZ = this.velocityZ + (double)(0.6F * MathHelper.sin(this.field_3897));
		this.velocityX *= 0.07;
		this.velocityZ *= 0.07;
		this.addPos(this.velocityX, this.velocityY, this.velocityZ);
		if (!this.world.getFluidState(new BlockPos(this.posX, this.posY, this.posZ)).matches(FluidTags.field_15517)) {
			this.markDead();
		}

		if (this.age++ >= this.maxAge || this.onGround) {
			this.markDead();
		}

		this.field_3897 = (float)((double)this.field_3897 + 0.08);
	}

	@Environment(EnvType.CLIENT)
	public static class class_737 implements FactoryParticle<TexturedParticle> {
		@Nullable
		public Particle createParticle(TexturedParticle texturedParticle, World world, double d, double e, double f, double g, double h, double i) {
			return new class_736(world, d, e, f);
		}
	}
}
