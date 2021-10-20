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
    private final SpriteProvider spriteProvider;

    protected AbstractDustParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, T abstractDustParticleEffect, SpriteProvider spriteProvider) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);
        this.velocityMultiplier = 0.96f;
        this.field_28787 = true;
        this.spriteProvider = spriteProvider;
        this.velocityX *= (double)0.1f;
        this.velocityY *= (double)0.1f;
        this.velocityZ *= (double)0.1f;
        float f = this.random.nextFloat() * 0.4f + 0.6f;
        this.colorRed = this.method_33076(((AbstractDustParticleEffect)abstractDustParticleEffect).getColor().getX(), f);
        this.colorGreen = this.method_33076(((AbstractDustParticleEffect)abstractDustParticleEffect).getColor().getY(), f);
        this.colorBlue = this.method_33076(((AbstractDustParticleEffect)abstractDustParticleEffect).getColor().getZ(), f);
        this.scale *= 0.75f * ((AbstractDustParticleEffect)abstractDustParticleEffect).getScale();
        int i = (int)(8.0 / (this.random.nextDouble() * 0.8 + 0.2));
        this.maxAge = (int)Math.max((float)i * ((AbstractDustParticleEffect)abstractDustParticleEffect).getScale(), 1.0f);
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
        super.tick();
        this.setSpriteForAge(this.spriteProvider);
    }
}

