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
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;

@Environment(value=EnvType.CLIENT)
public class BubbleColumnUpParticle
extends SpriteBillboardParticle {
    private BubbleColumnUpParticle(ClientWorld world, double x, double y, double z, double d, double e, double f) {
        super(world, x, y, z);
        this.gravityStrength = -0.125f;
        this.field_28786 = 0.85f;
        this.setBoundingBoxSpacing(0.02f, 0.02f);
        this.scale *= this.random.nextFloat() * 0.6f + 0.2f;
        this.velocityX = d * (double)0.2f + (Math.random() * 2.0 - 1.0) * (double)0.02f;
        this.velocityY = e * (double)0.2f + (Math.random() * 2.0 - 1.0) * (double)0.02f;
        this.velocityZ = f * (double)0.2f + (Math.random() * 2.0 - 1.0) * (double)0.02f;
        this.maxAge = (int)(40.0 / (Math.random() * 0.8 + 0.2));
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.dead && !this.world.getFluidState(new BlockPos(this.x, this.y, this.z)).isIn(FluidTags.WATER)) {
            this.markDead();
        }
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
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
            BubbleColumnUpParticle bubbleColumnUpParticle = new BubbleColumnUpParticle(clientWorld, d, e, f, g, h, i);
            bubbleColumnUpParticle.setSprite(this.spriteProvider);
            return bubbleColumnUpParticle;
        }
    }
}

