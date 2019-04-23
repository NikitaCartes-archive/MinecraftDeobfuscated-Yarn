/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.world.World;

@Environment(value=EnvType.CLIENT)
public class AnimatedParticle
extends SpriteBillboardParticle {
    protected final SpriteProvider spriteProvider;
    private final float upwardsAcceleration;
    private float resistance = 0.91f;
    private float targetColorRed;
    private float targetColorGreen;
    private float targetColorBlue;
    private boolean changesColor;

    protected AnimatedParticle(World world, double d, double e, double f, SpriteProvider spriteProvider, float g) {
        super(world, d, e, f);
        this.spriteProvider = spriteProvider;
        this.upwardsAcceleration = g;
    }

    public void setColor(int i) {
        float f = (float)((i & 0xFF0000) >> 16) / 255.0f;
        float g = (float)((i & 0xFF00) >> 8) / 255.0f;
        float h = (float)((i & 0xFF) >> 0) / 255.0f;
        float j = 1.0f;
        this.setColor(f * 1.0f, g * 1.0f, h * 1.0f);
    }

    public void setTargetColor(int i) {
        this.targetColorRed = (float)((i & 0xFF0000) >> 16) / 255.0f;
        this.targetColorGreen = (float)((i & 0xFF00) >> 8) / 255.0f;
        this.targetColorBlue = (float)((i & 0xFF) >> 0) / 255.0f;
        this.changesColor = true;
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
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
        this.setSpriteForAge(this.spriteProvider);
        if (this.age > this.maxAge / 2) {
            this.setColorAlpha(1.0f - ((float)this.age - (float)(this.maxAge / 2)) / (float)this.maxAge);
            if (this.changesColor) {
                this.colorRed += (this.targetColorRed - this.colorRed) * 0.2f;
                this.colorGreen += (this.targetColorGreen - this.colorGreen) * 0.2f;
                this.colorBlue += (this.targetColorBlue - this.colorBlue) * 0.2f;
            }
        }
        this.velocityY += (double)this.upwardsAcceleration;
        this.move(this.velocityX, this.velocityY, this.velocityZ);
        this.velocityX *= (double)this.resistance;
        this.velocityY *= (double)this.resistance;
        this.velocityZ *= (double)this.resistance;
        if (this.onGround) {
            this.velocityX *= (double)0.7f;
            this.velocityZ *= (double)0.7f;
        }
    }

    @Override
    public int getColorMultiplier(float f) {
        return 0xF000F0;
    }

    protected void setResistance(float f) {
        this.resistance = f;
    }
}

