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
public class FireSmokeParticle
extends SpriteBillboardParticle {
    private final SpriteProvider field_17868;

    protected FireSmokeParticle(World world, double d, double e, double f, double g, double h, double i, float j, SpriteProvider spriteProvider) {
        super(world, d, e, f, 0.0, 0.0, 0.0);
        float k;
        this.field_17868 = spriteProvider;
        this.velocityX *= (double)0.1f;
        this.velocityY *= (double)0.1f;
        this.velocityZ *= (double)0.1f;
        this.velocityX += g;
        this.velocityY += h;
        this.velocityZ += i;
        this.colorRed = k = (float)(Math.random() * (double)0.3f);
        this.colorGreen = k;
        this.colorBlue = k;
        this.scale *= 0.75f * j;
        this.maxAge = (int)(8.0 / (Math.random() * 0.8 + 0.2));
        this.maxAge = (int)((float)this.maxAge * j);
        this.maxAge = Math.max(this.maxAge, 1);
        this.setSpriteForAge(spriteProvider);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public float getSize(float f) {
        return this.scale * MathHelper.clamp(((float)this.age + f) / (float)this.maxAge * 32.0f, 0.0f, 1.0f);
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
        this.setSpriteForAge(this.field_17868);
        this.velocityY += 0.004;
        this.move(this.velocityX, this.velocityY, this.velocityZ);
        if (this.y == this.prevPosY) {
            this.velocityX *= 1.1;
            this.velocityZ *= 1.1;
        }
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
        private final SpriteProvider field_17869;

        public Factory(SpriteProvider spriteProvider) {
            this.field_17869 = spriteProvider;
        }

        public Particle method_3101(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
            return new FireSmokeParticle(world, d, e, f, g, h, i, 1.0f, this.field_17869);
        }
    }
}

