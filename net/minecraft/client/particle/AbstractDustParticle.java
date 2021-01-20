/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.AbstractDustParticleEffect;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class AbstractDustParticle<T extends AbstractDustParticleEffect>
extends SpriteBillboardParticle {
    private final SpriteProvider field_28247;

    protected AbstractDustParticle(ClientWorld world, double d, double e, double f, double g, double h, double i, T abstractDustParticleEffect, SpriteProvider spriteProvider) {
        super(world, d, e, f, g, h, i);
        this.field_28247 = spriteProvider;
        this.velocityX *= (double)0.1f;
        this.velocityY *= (double)0.1f;
        this.velocityZ *= (double)0.1f;
        float j = this.random.nextFloat() * 0.4f + 0.6f;
        this.colorRed = this.method_33076(((AbstractDustParticleEffect)abstractDustParticleEffect).getColor().getX(), j);
        this.colorGreen = this.method_33076(((AbstractDustParticleEffect)abstractDustParticleEffect).getColor().getY(), j);
        this.colorBlue = this.method_33076(((AbstractDustParticleEffect)abstractDustParticleEffect).getColor().getZ(), j);
        this.scale *= 0.75f * ((AbstractDustParticleEffect)abstractDustParticleEffect).getScale();
        int k = (int)(8.0 / (this.random.nextDouble() * 0.8 + 0.2));
        this.maxAge = (int)Math.max((float)k * ((AbstractDustParticleEffect)abstractDustParticleEffect).getScale(), 1.0f);
        this.setSpriteForAge(spriteProvider);
    }

    protected float method_33076(float f, float g) {
        return (this.random.nextFloat() * 0.2f + 0.8f) * f * g;
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public float getSize(float tickDelta) {
        return this.scale * MathHelper.clamp(((float)this.age + tickDelta) / (float)this.maxAge * 32.0f, 0.0f, 1.0f);
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
        this.setSpriteForAge(this.field_28247);
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
}

