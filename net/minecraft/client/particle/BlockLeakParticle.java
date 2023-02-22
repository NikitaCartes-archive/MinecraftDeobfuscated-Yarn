/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class BlockLeakParticle
extends SpriteBillboardParticle {
    private final Fluid fluid;
    protected boolean obsidianTear;

    BlockLeakParticle(ClientWorld world, double x, double y, double z, Fluid fluid) {
        super(world, x, y, z);
        this.setBoundingBoxSpacing(0.01f, 0.01f);
        this.gravityStrength = 0.06f;
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
        if (this.obsidianTear) {
            return 240;
        }
        return super.getBrightness(tint);
    }

    @Override
    public void tick() {
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;
        this.updateAge();
        if (this.dead) {
            return;
        }
        this.velocityY -= (double)this.gravityStrength;
        this.move(this.velocityX, this.velocityY, this.velocityZ);
        this.updateVelocity();
        if (this.dead) {
            return;
        }
        this.velocityX *= (double)0.98f;
        this.velocityY *= (double)0.98f;
        this.velocityZ *= (double)0.98f;
        if (this.fluid == Fluids.EMPTY) {
            return;
        }
        BlockPos blockPos = BlockPos.ofFloored(this.x, this.y, this.z);
        FluidState fluidState = this.world.getFluidState(blockPos);
        if (fluidState.getFluid() == this.fluid && this.y < (double)((float)blockPos.getY() + fluidState.getHeight(this.world, blockPos))) {
            this.markDead();
        }
    }

    protected void updateAge() {
        if (this.maxAge-- <= 0) {
            this.markDead();
        }
    }

    protected void updateVelocity() {
    }

    public static SpriteBillboardParticle createDrippingWater(DefaultParticleType type, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        Dripping blockLeakParticle = new Dripping(world, x, y, z, Fluids.WATER, ParticleTypes.FALLING_WATER);
        blockLeakParticle.setColor(0.2f, 0.3f, 1.0f);
        return blockLeakParticle;
    }

    public static SpriteBillboardParticle createFallingWater(DefaultParticleType type, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        ContinuousFalling blockLeakParticle = new ContinuousFalling(world, x, y, z, (Fluid)Fluids.WATER, ParticleTypes.SPLASH);
        blockLeakParticle.setColor(0.2f, 0.3f, 1.0f);
        return blockLeakParticle;
    }

    public static SpriteBillboardParticle createDrippingLava(DefaultParticleType type, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        return new DrippingLava(world, x, y, z, Fluids.LAVA, ParticleTypes.FALLING_LAVA);
    }

    public static SpriteBillboardParticle createFallingLava(DefaultParticleType type, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        ContinuousFalling blockLeakParticle = new ContinuousFalling(world, x, y, z, (Fluid)Fluids.LAVA, ParticleTypes.LANDING_LAVA);
        blockLeakParticle.setColor(1.0f, 0.2857143f, 0.083333336f);
        return blockLeakParticle;
    }

    public static SpriteBillboardParticle createLandingLava(DefaultParticleType type, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        Landing blockLeakParticle = new Landing(world, x, y, z, Fluids.LAVA);
        blockLeakParticle.setColor(1.0f, 0.2857143f, 0.083333336f);
        return blockLeakParticle;
    }

    private static BlockLeakParticle setCherryLeavesColor(BlockLeakParticle particle) {
        particle.setColor(0.937f, 0.655f, 0.804f);
        return particle;
    }

    public static SpriteBillboardParticle createDrippingCherryLeaves(DefaultParticleType type, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        return BlockLeakParticle.setCherryLeavesColor(new Dripping(world, x, y, z, Fluids.EMPTY, ParticleTypes.FALLING_CHERRY_LEAVES));
    }

    public static SpriteBillboardParticle createFallingCherryLeaves(DefaultParticleType type, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        ContinuousFalling continuousFalling = new ContinuousFalling(world, x, y, z, Fluids.EMPTY, ParticleTypes.LANDING_CHERRY_LEAVES);
        continuousFalling.gravityStrength = 0.005f;
        return BlockLeakParticle.setCherryLeavesColor(continuousFalling);
    }

    public static SpriteBillboardParticle createLandingCherryLeaves(DefaultParticleType type, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        return BlockLeakParticle.setCherryLeavesColor(new Landing(world, x, y, z, Fluids.EMPTY));
    }

    public static SpriteBillboardParticle createDrippingHoney(DefaultParticleType type, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        Dripping dripping = new Dripping(world, x, y, z, Fluids.EMPTY, ParticleTypes.FALLING_HONEY);
        dripping.gravityStrength *= 0.01f;
        dripping.maxAge = 100;
        dripping.setColor(0.622f, 0.508f, 0.082f);
        return dripping;
    }

    public static SpriteBillboardParticle createFallingHoney(DefaultParticleType type, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        FallingHoney blockLeakParticle = new FallingHoney(world, x, y, z, Fluids.EMPTY, ParticleTypes.LANDING_HONEY);
        blockLeakParticle.gravityStrength = 0.01f;
        blockLeakParticle.setColor(0.582f, 0.448f, 0.082f);
        return blockLeakParticle;
    }

    public static SpriteBillboardParticle createLandingHoney(DefaultParticleType type, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        Landing blockLeakParticle = new Landing(world, x, y, z, Fluids.EMPTY);
        blockLeakParticle.maxAge = (int)(128.0 / (Math.random() * 0.8 + 0.2));
        blockLeakParticle.setColor(0.522f, 0.408f, 0.082f);
        return blockLeakParticle;
    }

    public static SpriteBillboardParticle createDrippingDripstoneWater(DefaultParticleType type, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        Dripping blockLeakParticle = new Dripping(world, x, y, z, Fluids.WATER, ParticleTypes.FALLING_DRIPSTONE_WATER);
        blockLeakParticle.setColor(0.2f, 0.3f, 1.0f);
        return blockLeakParticle;
    }

    public static SpriteBillboardParticle createFallingDripstoneWater(DefaultParticleType type, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        DripstoneLavaDrip blockLeakParticle = new DripstoneLavaDrip(world, x, y, z, (Fluid)Fluids.WATER, ParticleTypes.SPLASH);
        blockLeakParticle.setColor(0.2f, 0.3f, 1.0f);
        return blockLeakParticle;
    }

    public static SpriteBillboardParticle createDrippingDripstoneLava(DefaultParticleType type, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        return new DrippingLava(world, x, y, z, Fluids.LAVA, ParticleTypes.FALLING_DRIPSTONE_LAVA);
    }

    public static SpriteBillboardParticle createFallingDripstoneLava(DefaultParticleType type, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        DripstoneLavaDrip blockLeakParticle = new DripstoneLavaDrip(world, x, y, z, (Fluid)Fluids.LAVA, ParticleTypes.LANDING_LAVA);
        blockLeakParticle.setColor(1.0f, 0.2857143f, 0.083333336f);
        return blockLeakParticle;
    }

    public static SpriteBillboardParticle createFallingNectar(DefaultParticleType type, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        Falling blockLeakParticle = new Falling(world, x, y, z, Fluids.EMPTY);
        blockLeakParticle.maxAge = (int)(16.0 / (Math.random() * 0.8 + 0.2));
        blockLeakParticle.gravityStrength = 0.007f;
        blockLeakParticle.setColor(0.92f, 0.782f, 0.72f);
        return blockLeakParticle;
    }

    public static SpriteBillboardParticle createFallingSporeBlossom(DefaultParticleType type, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        int i = (int)(64.0f / MathHelper.nextBetween(world.getRandom(), 0.1f, 0.9f));
        Falling blockLeakParticle = new Falling(world, x, y, z, Fluids.EMPTY, i);
        blockLeakParticle.gravityStrength = 0.005f;
        blockLeakParticle.setColor(0.32f, 0.5f, 0.22f);
        return blockLeakParticle;
    }

    public static SpriteBillboardParticle createDrippingObsidianTear(DefaultParticleType type, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        Dripping dripping = new Dripping(world, x, y, z, Fluids.EMPTY, ParticleTypes.FALLING_OBSIDIAN_TEAR);
        dripping.obsidianTear = true;
        dripping.gravityStrength *= 0.01f;
        dripping.maxAge = 100;
        dripping.setColor(0.51171875f, 0.03125f, 0.890625f);
        return dripping;
    }

    public static SpriteBillboardParticle createFallingObsidianTear(DefaultParticleType type, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        ContinuousFalling blockLeakParticle = new ContinuousFalling(world, x, y, z, Fluids.EMPTY, ParticleTypes.LANDING_OBSIDIAN_TEAR);
        blockLeakParticle.obsidianTear = true;
        blockLeakParticle.gravityStrength = 0.01f;
        blockLeakParticle.setColor(0.51171875f, 0.03125f, 0.890625f);
        return blockLeakParticle;
    }

    public static SpriteBillboardParticle createLandingObsidianTear(DefaultParticleType type, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        Landing blockLeakParticle = new Landing(world, x, y, z, Fluids.EMPTY);
        blockLeakParticle.obsidianTear = true;
        blockLeakParticle.maxAge = (int)(28.0 / (Math.random() * 0.8 + 0.2));
        blockLeakParticle.setColor(0.51171875f, 0.03125f, 0.890625f);
        return blockLeakParticle;
    }

    @Environment(value=EnvType.CLIENT)
    static class Dripping
    extends BlockLeakParticle {
        private final ParticleEffect nextParticle;

        Dripping(ClientWorld world, double x, double y, double z, Fluid fluid, ParticleEffect nextParticle) {
            super(world, x, y, z, fluid);
            this.nextParticle = nextParticle;
            this.gravityStrength *= 0.02f;
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

    @Environment(value=EnvType.CLIENT)
    static class ContinuousFalling
    extends Falling {
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

    @Environment(value=EnvType.CLIENT)
    static class DrippingLava
    extends Dripping {
        DrippingLava(ClientWorld clientWorld, double d, double e, double f, Fluid fluid, ParticleEffect particleEffect) {
            super(clientWorld, d, e, f, fluid, particleEffect);
        }

        @Override
        protected void updateAge() {
            this.red = 1.0f;
            this.green = 16.0f / (float)(40 - this.maxAge + 16);
            this.blue = 4.0f / (float)(40 - this.maxAge + 8);
            super.updateAge();
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class Landing
    extends BlockLeakParticle {
        Landing(ClientWorld clientWorld, double d, double e, double f, Fluid fluid) {
            super(clientWorld, d, e, f, fluid);
            this.maxAge = (int)(16.0 / (Math.random() * 0.8 + 0.2));
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class FallingHoney
    extends ContinuousFalling {
        FallingHoney(ClientWorld clientWorld, double d, double e, double f, Fluid fluid, ParticleEffect particleEffect) {
            super(clientWorld, d, e, f, fluid, particleEffect);
        }

        @Override
        protected void updateVelocity() {
            if (this.onGround) {
                this.markDead();
                this.world.addParticle(this.nextParticle, this.x, this.y, this.z, 0.0, 0.0, 0.0);
                float f = MathHelper.nextBetween(this.random, 0.3f, 1.0f);
                this.world.playSound(this.x, this.y, this.z, SoundEvents.BLOCK_BEEHIVE_DRIP, SoundCategory.BLOCKS, f, 1.0f, false);
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class DripstoneLavaDrip
    extends ContinuousFalling {
        DripstoneLavaDrip(ClientWorld clientWorld, double d, double e, double f, Fluid fluid, ParticleEffect particleEffect) {
            super(clientWorld, d, e, f, fluid, particleEffect);
        }

        @Override
        protected void updateVelocity() {
            if (this.onGround) {
                this.markDead();
                this.world.addParticle(this.nextParticle, this.x, this.y, this.z, 0.0, 0.0, 0.0);
                SoundEvent soundEvent = this.getFluid() == Fluids.LAVA ? SoundEvents.BLOCK_POINTED_DRIPSTONE_DRIP_LAVA : SoundEvents.BLOCK_POINTED_DRIPSTONE_DRIP_WATER;
                float f = MathHelper.nextBetween(this.random, 0.3f, 1.0f);
                this.world.playSound(this.x, this.y, this.z, soundEvent, SoundCategory.BLOCKS, f, 1.0f, false);
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class Falling
    extends BlockLeakParticle {
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
}

