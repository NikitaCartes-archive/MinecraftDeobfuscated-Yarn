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

@Environment(value=EnvType.CLIENT)
public class AnimatedParticle
extends SpriteBillboardParticle {
    protected final SpriteProvider spriteProvider;
    private float targetRed;
    private float targetGreen;
    private float targetBlue;
    private boolean changesColor;

    protected AnimatedParticle(ClientWorld world, double x, double y, double z, SpriteProvider spriteProvider, float upwardsAcceleration) {
        super(world, x, y, z);
        this.velocityMultiplier = 0.91f;
        this.gravityStrength = upwardsAcceleration;
        this.spriteProvider = spriteProvider;
    }

    public void setColor(int rgbHex) {
        float f = (float)((rgbHex & 0xFF0000) >> 16) / 255.0f;
        float g = (float)((rgbHex & 0xFF00) >> 8) / 255.0f;
        float h = (float)((rgbHex & 0xFF) >> 0) / 255.0f;
        float i = 1.0f;
        this.setColor(f * 1.0f, g * 1.0f, h * 1.0f);
    }

    public void setTargetColor(int rgbHex) {
        this.targetRed = (float)((rgbHex & 0xFF0000) >> 16) / 255.0f;
        this.targetGreen = (float)((rgbHex & 0xFF00) >> 8) / 255.0f;
        this.targetBlue = (float)((rgbHex & 0xFF) >> 0) / 255.0f;
        this.changesColor = true;
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteForAge(this.spriteProvider);
        if (this.age > this.maxAge / 2) {
            this.setAlpha(1.0f - ((float)this.age - (float)(this.maxAge / 2)) / (float)this.maxAge);
            if (this.changesColor) {
                this.red += (this.targetRed - this.red) * 0.2f;
                this.green += (this.targetGreen - this.green) * 0.2f;
                this.blue += (this.targetBlue - this.blue) * 0.2f;
            }
        }
    }

    @Override
    public int getBrightness(float tint) {
        return 0xF000F0;
    }
}

