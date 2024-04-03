package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class BlockLeakParticle extends SpriteBillboardParticle {
	private final Fluid fluid;
	protected boolean obsidianTear;

	BlockLeakParticle(ClientWorld world, double x, double y, double z, Fluid fluid) {
		super(world, x, y, z);
		this.setBoundingBoxSpacing(0.01F, 0.01F);
		this.gravityStrength = 0.06F;
		this.fluid = fluid;
	}

	protected Fluid getFluid() {
		return this.fluid;
	}

	@Override
	public ParticleTextureSheet getType() {
		return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
	}

	@Override
	public int getBrightness(float tint) {
		return this.obsidianTear ? 240 : super.getBrightness(tint);
	}

	@Override
	public void tick() {
		this.prevPosX = this.x;
		this.prevPosY = this.y;
		this.prevPosZ = this.z;
		this.updateAge();
		if (!this.dead) {
			this.velocityY = this.velocityY - (double)this.gravityStrength;
			this.move(this.velocityX, this.velocityY, this.velocityZ);
			this.updateVelocity();
			if (!this.dead) {
				this.velocityX *= 0.98F;
				this.velocityY *= 0.98F;
				this.velocityZ *= 0.98F;
				if (this.fluid != Fluids.EMPTY) {
					BlockPos blockPos = BlockPos.ofFloored(this.x, this.y, this.z);
					FluidState fluidState = this.world.getFluidState(blockPos);
					if (fluidState.getFluid() == this.fluid && this.y < (double)((float)blockPos.getY() + fluidState.getHeight(this.world, blockPos))) {
						this.markDead();
					}
				}
			}
		}
	}

	protected void updateAge() {
		if (this.maxAge-- <= 0) {
			this.markDead();
		}
	}

	protected void updateVelocity() {
	}

	public static SpriteBillboardParticle createDrippingWater(
		SimpleParticleType type, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ
	) {
		BlockLeakParticle blockLeakParticle = new BlockLeakParticle.Dripping(world, x, y, z, Fluids.WATER, ParticleTypes.FALLING_WATER);
		blockLeakParticle.setColor(0.2F, 0.3F, 1.0F);
		return blockLeakParticle;
	}

	public static SpriteBillboardParticle createFallingWater(
		SimpleParticleType type, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ
	) {
		BlockLeakParticle blockLeakParticle = new BlockLeakParticle.ContinuousFalling(world, x, y, z, Fluids.WATER, ParticleTypes.SPLASH);
		blockLeakParticle.setColor(0.2F, 0.3F, 1.0F);
		return blockLeakParticle;
	}

	public static SpriteBillboardParticle createDrippingLava(
		SimpleParticleType type, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ
	) {
		return new BlockLeakParticle.DrippingLava(world, x, y, z, Fluids.LAVA, ParticleTypes.FALLING_LAVA);
	}

	public static SpriteBillboardParticle createFallingLava(
		SimpleParticleType type, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ
	) {
		BlockLeakParticle blockLeakParticle = new BlockLeakParticle.ContinuousFalling(world, x, y, z, Fluids.LAVA, ParticleTypes.LANDING_LAVA);
		blockLeakParticle.setColor(1.0F, 0.2857143F, 0.083333336F);
		return blockLeakParticle;
	}

	public static SpriteBillboardParticle createLandingLava(
		SimpleParticleType type, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ
	) {
		BlockLeakParticle blockLeakParticle = new BlockLeakParticle.Landing(world, x, y, z, Fluids.LAVA);
		blockLeakParticle.setColor(1.0F, 0.2857143F, 0.083333336F);
		return blockLeakParticle;
	}

	public static SpriteBillboardParticle createDrippingHoney(
		SimpleParticleType type, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ
	) {
		BlockLeakParticle.Dripping dripping = new BlockLeakParticle.Dripping(world, x, y, z, Fluids.EMPTY, ParticleTypes.FALLING_HONEY);
		dripping.gravityStrength *= 0.01F;
		dripping.maxAge = 100;
		dripping.setColor(0.622F, 0.508F, 0.082F);
		return dripping;
	}

	public static SpriteBillboardParticle createFallingHoney(
		SimpleParticleType type, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ
	) {
		BlockLeakParticle blockLeakParticle = new BlockLeakParticle.FallingHoney(world, x, y, z, Fluids.EMPTY, ParticleTypes.LANDING_HONEY);
		blockLeakParticle.gravityStrength = 0.01F;
		blockLeakParticle.setColor(0.582F, 0.448F, 0.082F);
		return blockLeakParticle;
	}

	public static SpriteBillboardParticle createLandingHoney(
		SimpleParticleType type, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ
	) {
		BlockLeakParticle blockLeakParticle = new BlockLeakParticle.Landing(world, x, y, z, Fluids.EMPTY);
		blockLeakParticle.maxAge = (int)(128.0 / (Math.random() * 0.8 + 0.2));
		blockLeakParticle.setColor(0.522F, 0.408F, 0.082F);
		return blockLeakParticle;
	}

	public static SpriteBillboardParticle createDrippingDripstoneWater(
		SimpleParticleType type, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ
	) {
		BlockLeakParticle blockLeakParticle = new BlockLeakParticle.Dripping(world, x, y, z, Fluids.WATER, ParticleTypes.FALLING_DRIPSTONE_WATER);
		blockLeakParticle.setColor(0.2F, 0.3F, 1.0F);
		return blockLeakParticle;
	}

	public static SpriteBillboardParticle createFallingDripstoneWater(
		SimpleParticleType type, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ
	) {
		BlockLeakParticle blockLeakParticle = new BlockLeakParticle.DripstoneLavaDrip(world, x, y, z, Fluids.WATER, ParticleTypes.SPLASH);
		blockLeakParticle.setColor(0.2F, 0.3F, 1.0F);
		return blockLeakParticle;
	}

	public static SpriteBillboardParticle createDrippingDripstoneLava(
		SimpleParticleType type, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ
	) {
		return new BlockLeakParticle.DrippingLava(world, x, y, z, Fluids.LAVA, ParticleTypes.FALLING_DRIPSTONE_LAVA);
	}

	public static SpriteBillboardParticle createFallingDripstoneLava(
		SimpleParticleType type, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ
	) {
		BlockLeakParticle blockLeakParticle = new BlockLeakParticle.DripstoneLavaDrip(world, x, y, z, Fluids.LAVA, ParticleTypes.LANDING_LAVA);
		blockLeakParticle.setColor(1.0F, 0.2857143F, 0.083333336F);
		return blockLeakParticle;
	}

	public static SpriteBillboardParticle createFallingNectar(
		SimpleParticleType type, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ
	) {
		BlockLeakParticle blockLeakParticle = new BlockLeakParticle.Falling(world, x, y, z, Fluids.EMPTY);
		blockLeakParticle.maxAge = (int)(16.0 / (Math.random() * 0.8 + 0.2));
		blockLeakParticle.gravityStrength = 0.007F;
		blockLeakParticle.setColor(0.92F, 0.782F, 0.72F);
		return blockLeakParticle;
	}

	public static SpriteBillboardParticle createFallingSporeBlossom(
		SimpleParticleType type, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ
	) {
		int i = (int)(64.0F / MathHelper.nextBetween(world.getRandom(), 0.1F, 0.9F));
		BlockLeakParticle blockLeakParticle = new BlockLeakParticle.Falling(world, x, y, z, Fluids.EMPTY, i);
		blockLeakParticle.gravityStrength = 0.005F;
		blockLeakParticle.setColor(0.32F, 0.5F, 0.22F);
		return blockLeakParticle;
	}

	public static SpriteBillboardParticle createDrippingObsidianTear(
		SimpleParticleType type, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ
	) {
		BlockLeakParticle.Dripping dripping = new BlockLeakParticle.Dripping(world, x, y, z, Fluids.EMPTY, ParticleTypes.FALLING_OBSIDIAN_TEAR);
		dripping.obsidianTear = true;
		dripping.gravityStrength *= 0.01F;
		dripping.maxAge = 100;
		dripping.setColor(0.51171875F, 0.03125F, 0.890625F);
		return dripping;
	}

	public static SpriteBillboardParticle createFallingObsidianTear(
		SimpleParticleType type, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ
	) {
		BlockLeakParticle blockLeakParticle = new BlockLeakParticle.ContinuousFalling(world, x, y, z, Fluids.EMPTY, ParticleTypes.LANDING_OBSIDIAN_TEAR);
		blockLeakParticle.obsidianTear = true;
		blockLeakParticle.gravityStrength = 0.01F;
		blockLeakParticle.setColor(0.51171875F, 0.03125F, 0.890625F);
		return blockLeakParticle;
	}

	public static SpriteBillboardParticle createLandingObsidianTear(
		SimpleParticleType type, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ
	) {
		BlockLeakParticle blockLeakParticle = new BlockLeakParticle.Landing(world, x, y, z, Fluids.EMPTY);
		blockLeakParticle.obsidianTear = true;
		blockLeakParticle.maxAge = (int)(28.0 / (Math.random() * 0.8 + 0.2));
		blockLeakParticle.setColor(0.51171875F, 0.03125F, 0.890625F);
		return blockLeakParticle;
	}

	@Environment(EnvType.CLIENT)
	static class ContinuousFalling extends BlockLeakParticle.Falling {
		protected final ParticleEffect nextParticle;

		ContinuousFalling(ClientWorld world, double x, double y, double z, Fluid fluid, ParticleEffect nextParticle) {
			super(world, x, y, z, fluid);
			this.nextParticle = nextParticle;
		}

		@Override
		protected void updateVelocity() {
			if (this.onGround) {
				this.markDead();
				this.world.addParticle(this.nextParticle, this.x, this.y, this.z, 0.0, 0.0, 0.0);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	static class Dripping extends BlockLeakParticle {
		private final ParticleEffect nextParticle;

		Dripping(ClientWorld world, double x, double y, double z, Fluid fluid, ParticleEffect nextParticle) {
			super(world, x, y, z, fluid);
			this.nextParticle = nextParticle;
			this.gravityStrength *= 0.02F;
			this.maxAge = 40;
		}

		@Override
		protected void updateAge() {
			if (this.maxAge-- <= 0) {
				this.markDead();
				this.world.addParticle(this.nextParticle, this.x, this.y, this.z, this.velocityX, this.velocityY, this.velocityZ);
			}
		}

		@Override
		protected void updateVelocity() {
			this.velocityX *= 0.02;
			this.velocityY *= 0.02;
			this.velocityZ *= 0.02;
		}
	}

	@Environment(EnvType.CLIENT)
	static class DrippingLava extends BlockLeakParticle.Dripping {
		DrippingLava(ClientWorld clientWorld, double d, double e, double f, Fluid fluid, ParticleEffect particleEffect) {
			super(clientWorld, d, e, f, fluid, particleEffect);
		}

		@Override
		protected void updateAge() {
			this.red = 1.0F;
			this.green = 16.0F / (float)(40 - this.maxAge + 16);
			this.blue = 4.0F / (float)(40 - this.maxAge + 8);
			super.updateAge();
		}
	}

	@Environment(EnvType.CLIENT)
	static class DripstoneLavaDrip extends BlockLeakParticle.ContinuousFalling {
		DripstoneLavaDrip(ClientWorld clientWorld, double d, double e, double f, Fluid fluid, ParticleEffect particleEffect) {
			super(clientWorld, d, e, f, fluid, particleEffect);
		}

		@Override
		protected void updateVelocity() {
			if (this.onGround) {
				this.markDead();
				this.world.addParticle(this.nextParticle, this.x, this.y, this.z, 0.0, 0.0, 0.0);
				SoundEvent soundEvent = this.getFluid() == Fluids.LAVA ? SoundEvents.BLOCK_POINTED_DRIPSTONE_DRIP_LAVA : SoundEvents.BLOCK_POINTED_DRIPSTONE_DRIP_WATER;
				float f = MathHelper.nextBetween(this.random, 0.3F, 1.0F);
				this.world.playSound(this.x, this.y, this.z, soundEvent, SoundCategory.BLOCKS, f, 1.0F, false);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	static class Falling extends BlockLeakParticle {
		Falling(ClientWorld clientWorld, double d, double e, double f, Fluid fluid) {
			this(clientWorld, d, e, f, fluid, (int)(64.0 / (Math.random() * 0.8 + 0.2)));
		}

		Falling(ClientWorld world, double x, double y, double z, Fluid fluid, int maxAge) {
			super(world, x, y, z, fluid);
			this.maxAge = maxAge;
		}

		@Override
		protected void updateVelocity() {
			if (this.onGround) {
				this.markDead();
			}
		}
	}

	@Environment(EnvType.CLIENT)
	static class FallingHoney extends BlockLeakParticle.ContinuousFalling {
		FallingHoney(ClientWorld clientWorld, double d, double e, double f, Fluid fluid, ParticleEffect particleEffect) {
			super(clientWorld, d, e, f, fluid, particleEffect);
		}

		@Override
		protected void updateVelocity() {
			if (this.onGround) {
				this.markDead();
				this.world.addParticle(this.nextParticle, this.x, this.y, this.z, 0.0, 0.0, 0.0);
				float f = MathHelper.nextBetween(this.random, 0.3F, 1.0F);
				this.world.playSound(this.x, this.y, this.z, SoundEvents.BLOCK_BEEHIVE_DRIP, SoundCategory.BLOCKS, f, 1.0F, false);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	static class Landing extends BlockLeakParticle {
		Landing(ClientWorld clientWorld, double d, double e, double f, Fluid fluid) {
			super(clientWorld, d, e, f, fluid);
			this.maxAge = (int)(16.0 / (Math.random() * 0.8 + 0.2));
		}
	}
}
