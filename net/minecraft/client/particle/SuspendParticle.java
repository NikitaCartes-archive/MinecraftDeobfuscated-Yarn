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
import net.minecraft.world.World;

@Environment(value=EnvType.CLIENT)
public class SuspendParticle
extends SpriteBillboardParticle {
    private SuspendParticle(World world, double d, double e, double f, double g, double h, double i) {
        super(world, d, e, f, g, h, i);
        float j;
        this.colorRed = j = this.random.nextFloat() * 0.1f + 0.2f;
        this.colorGreen = j;
        this.colorBlue = j;
        this.setBoundingBoxSpacing(0.02f, 0.02f);
        this.scale *= this.random.nextFloat() * 0.6f + 0.5f;
        this.velocityX *= (double)0.02f;
        this.velocityY *= (double)0.02f;
        this.velocityZ *= (double)0.02f;
        this.maxAge = (int)(20.0 / (Math.random() * 0.8 + 0.2));
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
    public void tick() {
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;
        if (this.maxAge-- <= 0) {
            this.markDead();
            return;
        }
        this.move(this.velocityX, this.velocityY, this.velocityZ);
        this.velocityX *= 0.99;
        this.velocityY *= 0.99;
        this.velocityZ *= 0.99;
    }

    @Environment(value=EnvType.CLIENT)
    public static class DolphinFactory
    implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider field_17881;

        public DolphinFactory(SpriteProvider spriteProvider) {
            this.field_17881 = spriteProvider;
        }

        public Particle method_3110(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
            SuspendParticle suspendParticle = new SuspendParticle(world, d, e, f, g, h, i);
            suspendParticle.setColor(0.3f, 0.5f, 1.0f);
            suspendParticle.setSprite(this.field_17881);
            suspendParticle.setColorAlpha(1.0f - world.random.nextFloat() * 0.7f);
            suspendParticle.setMaxAge(suspendParticle.getMaxAge() / 2);
            return suspendParticle;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class class_3991
    implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider field_17880;

        public class_3991(SpriteProvider spriteProvider) {
            this.field_17880 = spriteProvider;
        }

        public Particle method_18044(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
            SuspendParticle suspendParticle = new SuspendParticle(world, d, e, f, g, h, i);
            suspendParticle.setSprite(this.field_17880);
            suspendParticle.setColor(1.0f, 1.0f, 1.0f);
            suspendParticle.setMaxAge(3 + world.getRandom().nextInt(5));
            return suspendParticle;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class HappyVillagerFactory
    implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider field_17882;

        public HappyVillagerFactory(SpriteProvider spriteProvider) {
            this.field_17882 = spriteProvider;
        }

        public Particle method_3111(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
            SuspendParticle suspendParticle = new SuspendParticle(world, d, e, f, g, h, i);
            suspendParticle.setSprite(this.field_17882);
            suspendParticle.setColor(1.0f, 1.0f, 1.0f);
            return suspendParticle;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class MyceliumFactory
    implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider field_17883;

        public MyceliumFactory(SpriteProvider spriteProvider) {
            this.field_17883 = spriteProvider;
        }

        public Particle method_3112(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
            SuspendParticle suspendParticle = new SuspendParticle(world, d, e, f, g, h, i);
            suspendParticle.setSprite(this.field_17883);
            return suspendParticle;
        }
    }
}

