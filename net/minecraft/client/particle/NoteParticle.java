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
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class NoteParticle
extends SpriteBillboardParticle {
    NoteParticle(ClientWorld clientWorld, double d, double e, double f, double g) {
        super(clientWorld, d, e, f, 0.0, 0.0, 0.0);
        this.velocityMultiplier = 0.66f;
        this.field_28787 = true;
        this.velocityX *= (double)0.01f;
        this.velocityY *= (double)0.01f;
        this.velocityZ *= (double)0.01f;
        this.velocityY += 0.2;
        this.colorRed = Math.max(0.0f, MathHelper.sin(((float)g + 0.0f) * ((float)Math.PI * 2)) * 0.65f + 0.35f);
        this.colorGreen = Math.max(0.0f, MathHelper.sin(((float)g + 0.33333334f) * ((float)Math.PI * 2)) * 0.65f + 0.35f);
        this.colorBlue = Math.max(0.0f, MathHelper.sin(((float)g + 0.6666667f) * ((float)Math.PI * 2)) * 0.65f + 0.35f);
        this.scale *= 1.5f;
        this.maxAge = 6;
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public float getSize(float tickDelta) {
        return this.scale * MathHelper.clamp(((float)this.age + tickDelta) / (float)this.maxAge * 32.0f, 0.0f, 1.0f);
    }

    @Environment(value=EnvType.CLIENT)
    public static class Factory
    implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            NoteParticle noteParticle = new NoteParticle(clientWorld, d, e, f, g);
            noteParticle.setSprite(this.spriteProvider);
            return noteParticle;
        }
    }
}

