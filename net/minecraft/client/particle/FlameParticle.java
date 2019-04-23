/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

@Environment(value=EnvType.CLIENT)
public class FlameParticle
extends SpriteBillboardParticle {
    private FlameParticle(World world, double d, double e, double f, double g, double h, double i) {
        super(world, d, e, f, g, h, i);
        this.velocityX = this.velocityX * (double)0.01f + g;
        this.velocityY = this.velocityY * (double)0.01f + h;
        this.velocityZ = this.velocityZ * (double)0.01f + i;
        this.x += (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.05f);
        this.y += (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.05f);
        this.z += (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.05f);
        this.maxAge = (int)(8.0 / (Math.random() * 0.8 + 0.2)) + 4;
    }

    @Override
    public ParticleTextureSheet getType() {
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
        return this.scale * (1.0f - g * g * 0.5f);
    }

    @Override
    public int getColorMultiplier(float f) {
        float g = ((float)this.age + f) / (float)this.maxAge;
        g = MathHelper.clamp(g, 0.0f, 1.0f);
        int i = super.getColorMultiplier(f);
        int j = i & 0xFF;
        int k = i >> 16 & 0xFF;
        if ((j += (int)(g * 15.0f * 16.0f)) > 240) {
            j = 240;
        }
        return j | k << 16;
    }

    @Override
    public void tick() {
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;
        if (this.age++ >= this.maxAge) {
            this.markDead();
            return;
        }
        this.move(this.velocityX, this.velocityY, this.velocityZ);
        this.velocityX *= (double)0.96f;
        this.velocityY *= (double)0.96f;
        this.velocityZ *= (double)0.96f;
        if (this.onGround) {
            this.velocityX *= (double)0.7f;
            this.velocityZ *= (double)0.7f;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class Factory
    implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider field_17812;

        public Factory(SpriteProvider spriteProvider) {
            this.field_17812 = spriteProvider;
        }

        public Particle method_3036(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
            FlameParticle flameParticle = new FlameParticle(world, d, e, f, g, h, i);
            flameParticle.setSprite(this.field_17812);
            return flameParticle;
        }
    }
}

